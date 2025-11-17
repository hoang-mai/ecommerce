package com.ecommerce.product.service.impl;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.kafka.event.product.*;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.*;
import com.ecommerce.product.entity.ProductVariantAttributeValue;
import com.ecommerce.product.messaging.producer.OrderEventProducer;
import com.ecommerce.product.messaging.producer.ProductEventProducer;
import com.ecommerce.product.repository.*;
import com.ecommerce.product.service.FileService;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final FileService fileService;
    private final ProductEventProducer productEventProducer;
    private final OrderEventProducer orderEventProducer;
    private final ProductVariantRepository productVariantRepository;
    private final MessageService messageService;

    @Override
    @Transactional
    public void createProduct(ReqCreateProductDTO request, List<MultipartFile> files) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));

        if (!shopRepository.existsById(request.getShopId())) {
            throw new NotFoundException(MessageError.SHOP_NOT_FOUND);
        }
        Product product = Product.builder()
                .shopId(request.getShopId())
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .productStatus(ProductStatus.ACTIVE)
                .build();

        if (FnCommon.isNotNullOrEmptyList(request.getProductAttributes())) {
            request.getProductAttributes().forEach(reqProductAttributeDTO -> {
                ProductAttribute productAttribute = ProductAttribute.builder()
                        .attributeName(reqProductAttributeDTO.getAttributeName())
                        .build();

                if (FnCommon.isNotNullOrEmptyList(reqProductAttributeDTO.getAttributeValues())) {
                    reqProductAttributeDTO.getAttributeValues().forEach(reqProductAttributeValueDTO -> {
                        ProductAttributeValue attributeValue = ProductAttributeValue.builder()
                                .value(reqProductAttributeValueDTO)
                                .build();
                        productAttribute.addAttributeValue(attributeValue);
                    });
                }
                product.addProductAttribute(productAttribute);
            });
        }

        if (FnCommon.isNotNullOrEmptyList(request.getProductVariants())) {
            request.getProductVariants().forEach(reqProductVariantDTO -> {
                ProductVariant productVariant = ProductVariant.builder()
                        .price(reqProductVariantDTO.getPrice())
                        .stockQuantity(reqProductVariantDTO.getStockQuantity())
                        .productVariantStatus(reqProductVariantDTO.getStockQuantity() > 0 ? ProductVariantStatus.ACTIVE : ProductVariantStatus.OUT_OF_STOCK)
                        .isDefault(reqProductVariantDTO.getIsDefault() != null ? reqProductVariantDTO.getIsDefault() : false)
                        .build();
                if (FnCommon.isNotNullOrEmptyMap(reqProductVariantDTO.getAttributeValues())) {
                    reqProductVariantDTO.getAttributeValues().forEach((attributeName, attributeValue) -> product.getProductAttributes().stream()
                            .filter(attr -> attr.getAttributeName().equals(attributeName))
                            .flatMap(attr -> attr.getProductAttributeValues().stream())
                            .filter(val -> val.getValue().equals(attributeValue))
                            .findFirst()
                            .ifPresent(productAttributeValue -> {
                                ProductVariantAttributeValue variantAttrValue = ProductVariantAttributeValue.builder()
                                        .productAttributeValue(productAttributeValue)
                                        .build();
                                productVariant.addProductVariantAttributeValue(variantAttrValue);
                            }));
                }
                product.addProductVariant(productVariant);
            });
        }
        productRepository.save(product);

        if (FnCommon.isNotNullOrEmptyList(files)) {
            fileService.uploadFiles(files, "shop/" + product.getShopId() + "/product-images/" + product.getProductId())
                    .forEach(filePath -> {
                        ProductImage productImage = ProductImage.builder()
                                .imageUrl(filePath)
                                .build();
                        product.addProductImage(productImage);
                    });
            productRepository.save(product);
        }
        productEventProducer.send(
                CreateProductCacheEvent.builder()
                        .productId(product.getProductId())
                        .productVariantId(product.getProductVariants().stream().map(ProductVariant::getProductVariantId).toList())
                        .build()
        );
    }

    @Override
    public ResProductDTO getProductById(Long productId) {
        Product product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

        return ResProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .shopId(product.getShopId())
                .category(buildCategoryResponse(product.getCategory()))
                .productImages(buildProductImagesResponse(product.getProductImages()))
                .productAttributes(buildProductAttributesResponse(product.getProductAttributes()))
                .productVariants(buildProductVariantsResponse(product.getProductVariants()))
                .build();
    }

    @Override
    @Transactional
    public void updateProduct(Long productId, ReqUpdateProductDTO request, List<MultipartFile> files) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

        if (FnCommon.isNotNull(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        } else {
            throw new NotFoundException(MessageError.CATEGORY_NOT_FOUND);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());

        // Xóa ảnh đã được chỉ định
        if (FnCommon.isNotNullOrEmptyList(request.getDeletedImageIds()) && FnCommon.isNotNullOrEmptyList(product.getProductImages())) {
            request.getDeletedImageIds().forEach(imageId -> product.getProductImages().stream()
                    .filter(img -> img.getProductImageId().equals(imageId))
                    .findFirst().ifPresent(productImage -> {
                        fileService.deleteFile(productImage.getImageUrl());
                        product.deleteProductImage(productImage);
                    }));
        }

        // Xử lý upload ảnh mới nếu có
        if (FnCommon.isNotNullOrEmptyList(files)) {
            fileService.uploadFiles(files, "shop/" + product.getShopId() + "/product-images/" + product.getProductId())
                    .forEach(filePath -> {
                        ProductImage productImage = ProductImage.builder()
                                .imageUrl(filePath)
                                .build();
                        product.addProductImage(productImage);
                    });
        }


        // Xử lý product attributes: nếu có ID thì update, không có ID thì thêm mới
        if (FnCommon.isNotNullOrEmptyList(request.getProductAttributes())) {

            request.getProductAttributes().forEach(attrReq -> {
                if (FnCommon.isNotNull(attrReq.getProductAttributeId())) {
                    // CÓ ID -> CẬP NHẬT attribute hiện có
                    ProductAttribute existingAttr = product.getProductAttributes().stream()
                            .filter(attr -> attr.getAttributeId().equals(attrReq.getProductAttributeId()))
                            .findFirst()
                            .orElse(null);

                    if (existingAttr != null && FnCommon.isNotNullOrEmptyList(attrReq.getAttributeValues())) {

                        // Xử lý attribute values: nếu có ID thì bỏ qua (không cập nhật), không có ID thì thêm mới
                        attrReq.getAttributeValues().forEach(valReq -> {
                            if (valReq.getAttributeValueId() == null && valReq.getAttributeValue() != null) {
                                ProductAttributeValue newValue = ProductAttributeValue.builder()
                                        .value(valReq.getAttributeValue())
                                        .build();
                                existingAttr.addAttributeValue(newValue);
                            }
                        });
                    }
                } else {
                    // KHÔNG CÓ ID -> THÊM MỚI attribute
                    ProductAttribute newAttribute = ProductAttribute.builder()
                            .attributeName(attrReq.getAttributeName())
                            .build();

                    if (FnCommon.isNotNullOrEmptyList(attrReq.getAttributeValues())) {
                        attrReq.getAttributeValues().forEach(valReq -> {
                            ProductAttributeValue newValue = ProductAttributeValue.builder()
                                    .value(valReq.getAttributeValue())
                                    .build();
                            newAttribute.addAttributeValue(newValue);
                        });
                    }
                    product.addProductAttribute(newAttribute);
                }
            });
        }

        // Xử lý product variants: nếu có ID thì update, không có ID thì thêm mới
        if (FnCommon.isNotNullOrEmptyList(request.getProductVariants())) {

            request.getProductVariants().forEach(variantReq -> {
                if (FnCommon.isNotNull(variantReq.getProductVariantId())) {
                    // CÓ ID -> CẬP NHẬT variant hiện có
                    product.getProductVariants().stream()
                            .filter(variant -> variant.getProductVariantId().equals(variantReq.getProductVariantId()))
                            .findFirst()
                            .ifPresent(variant -> {
                                variant.setPrice(variantReq.getPrice());
                                if (variant.getProductVariantStatus() != ProductVariantStatus.INACTIVE) {
                                    variant.setProductVariantStatus(variant.getStockQuantity() > 0 ? ProductVariantStatus.ACTIVE : ProductVariantStatus.OUT_OF_STOCK);
                                }
                                variant.setStockQuantity(variantReq.getStockQuantity());
                                if (variantReq.getIsDefault() != null) {
                                    variant.setIsDefault(variantReq.getIsDefault());
                                }
                            });
                } else {
                    // KHÔNG CÓ ID -> THÊM MỚI variant

                    ProductVariant newVariant = ProductVariant.builder()
                            .product(product)
                            .price(variantReq.getPrice())
                            .stockQuantity(variantReq.getStockQuantity())
                            .productVariantStatus(variantReq.getStockQuantity() > 0 ? ProductVariantStatus.ACTIVE : ProductVariantStatus.OUT_OF_STOCK)
                            .isDefault(variantReq.getIsDefault() != null ? variantReq.getIsDefault() : false)
                            .build();

                    if (FnCommon.isNotNullOrEmptyMap(variantReq.getAttributeValues())) {
                        variantReq.getAttributeValues().forEach((attributeName, attributeValue) -> product.getProductAttributes().stream()
                                .filter(attr -> attr.getAttributeName().equals(attributeName))
                                .flatMap(attr -> attr.getProductAttributeValues().stream())
                                .filter(val -> val.getValue().equals(attributeValue))
                                .findFirst()
                                .ifPresent(productAttributeValue -> {
                                    ProductVariantAttributeValue variantAttrValue = ProductVariantAttributeValue.builder()
                                            .productAttributeValue(productAttributeValue)
                                            .build();
                                    newVariant.addProductVariantAttributeValue(variantAttrValue);
                                }));
                    }
                    product.addProductVariant(newVariant);
                }
            });
        }
        productRepository.save(product);

        productEventProducer.send(
                CreateProductCacheEvent.builder()
                        .productId(product.getProductId())
                        .productVariantId(product.getProductVariants().stream().map(ProductVariant::getProductVariantId).toList())
                        .build()
        );

    }

    @Override
    public void updateProductVariantStatus(Long productVariantId, ReqUpdateProductVariantStatusDTO request) {
        ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND));
        productVariant.setProductVariantStatus(request.getProductVariantStatus());
        productVariantRepository.save(productVariant);

    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<ResProductDTO> searchProducts(Long shopId, Long categoryId, ProductVariantStatus status,
                                                      String keyword, int pageNo, int pageSize,
                                                      String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Product> productsPage = productRepository.searchProducts(shopId, categoryId, status, keyword, pageable);

        return buildPageResponse(productsPage);
    }

    @Override
    public void updateProductStatusByProductId(Long productId, ReqUpdateProductStatusDTO status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        product.setProductStatus(status.getProductStatus());
        productRepository.save(product);

    }

    @Transactional
    @Override
    public void handleCreateOrderEvent(CreateOrderEvent createOrderEvent) {
        createOrderEvent.getCreateOrderItemEventList().forEach(orderItem -> orderItem.getCreateProductOrderItemEvents().forEach(productOrderItemEvent -> {
            ProductVariant productVariant = productVariantRepository.findByIdForUpDate(productOrderItemEvent.getProductVariantId())
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND));

            if (productVariant.getProductVariantStatus() == ProductVariantStatus.INACTIVE) {
                orderEventProducer.send(
                        OrderStatusEvent.builder()
                                .orderId(createOrderEvent.getOrderId())
                                .orderStatus(OrderStatus.CANCELLED)
                                .reason(messageService.getMessage(MessageError.PRODUCT_VARIANT_INACTIVE))
                                .build()
                );
                return;
            }

            int updatedStock = productVariant.getStockQuantity() - productOrderItemEvent.getQuantity();
            productVariant.setStockQuantity(updatedStock);

            if (updatedStock < 0) {
                orderEventProducer.send(
                        OrderStatusEvent.builder()
                                .orderId(createOrderEvent.getOrderId())
                                .orderStatus(OrderStatus.CANCELLED)
                                .reason(messageService.getMessage(MessageError.INSUFFICIENT_PRODUCT_VARIANT_STOCK))
                                .build()
                );
                return;
            } else if (updatedStock == 0) {
                productVariant.setProductVariantStatus(ProductVariantStatus.OUT_OF_STOCK);
            }

            productVariantRepository.save(productVariant);
        }));
    }

    private PageResponse<ResProductDTO> buildPageResponse(Page<Product> productsPage) {
        List<ResProductDTO> productResponses = productsPage.getContent().stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        return PageResponse.<ResProductDTO>builder()
                .pageNo(productsPage.getNumber())
                .pageSize(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .hasNextPage(productsPage.hasNext())
                .hasPreviousPage(productsPage.hasPrevious())
                .data(productResponses)
                .build();
    }

    private ResProductDTO convertToProductDTO(Product product) {
        return ResProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .shopId(product.getShopId())
                .productStatus(product.getProductStatus())
                .category(buildCategoryResponse(product.getCategory()))
                .productImages(buildProductImagesResponse(product.getProductImages()))
                .productAttributes(buildProductAttributesResponse(product.getProductAttributes()))
                .productVariants(buildProductVariantsResponse(product.getProductVariants()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }


    private ResCategoryDTO buildCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        return ResCategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .categoryStatus(category.getCategoryStatus())
                .build();
    }

    private List<ResProductImageDTO> buildProductImagesResponse(List<ProductImage> productImages) {
        if (!FnCommon.isNotNullOrEmptyList(productImages)) {
            return new ArrayList<>();
        }
        return productImages.stream()
                .map(productImage -> ResProductImageDTO.builder()
                        .productImageId(productImage.getProductImageId())
                        .imageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<ResProductAttributeDTO> buildProductAttributesResponse(List<ProductAttribute> productAttributes) {
        if (!FnCommon.isNotNullOrEmptyList(productAttributes)) {
            return new ArrayList<>();
        }
        return productAttributes.stream()
                .map(productAttribute -> ResProductAttributeDTO.builder()
                        .productAttributeId(productAttribute.getAttributeId())
                        .attributeName(productAttribute.getAttributeName())
                        .attributeValues(buildProductAttributeValuesResponse(productAttribute.getProductAttributeValues()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<ResProductAttributeValueDTO> buildProductAttributeValuesResponse(List<ProductAttributeValue> attributeValues) {
        if (!FnCommon.isNotNullOrEmptyList(attributeValues)) {
            return new ArrayList<>();
        }
        return attributeValues.stream()
                .map(productAttributeValue -> ResProductAttributeValueDTO.builder()
                        .attributeValueId(productAttributeValue.getAttributeValueId())
                        .attributeValue(productAttributeValue.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ResProductVariantDTO> buildProductVariantsResponse(List<ProductVariant> productVariants) {
        if (!FnCommon.isNotNullOrEmptyList(productVariants)) {
            return new ArrayList<>();
        }
        return productVariants.stream()
                .map(productVariant -> {
                    Map<String, String> attributeValues = productVariant.getProductVariantAttributeValues()
                            .stream()
                            .collect(Collectors.toMap(
                                    variantAttrValue -> variantAttrValue.getProductAttributeValue()
                                            .getProductAttribute().getAttributeName(),
                                    variantAttrValue -> variantAttrValue.getProductAttributeValue().getValue()
                            ));

                    return ResProductVariantDTO.builder()
                            .productVariantId(productVariant.getProductVariantId())
                            .price(productVariant.getPrice())
                            .stockQuantity(productVariant.getStockQuantity())
                            .productVariantStatus(productVariant.getProductVariantStatus())
                            .isDefault(productVariant.getIsDefault())
                            .attributeValues(attributeValues)
                            .build();
                })
                .collect(Collectors.toList());
    }

}

