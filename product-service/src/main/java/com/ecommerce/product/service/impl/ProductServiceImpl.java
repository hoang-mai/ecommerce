package com.ecommerce.product.service.impl;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.*;
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
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final UserHelper userHelper;

    @Override
    @Transactional
    public void createProduct(ReqCreateProductDTO request, List<MultipartFile> files) {
        Long ownerId = userHelper.getCurrentUserId();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));

        Shop shop = shopRepository.findByShopIdAndOwnerId(request.getShopId(), ownerId)
                .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
        Product product = Product.builder()
                .shop(shop)
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .productStatus(ProductStatus.ACTIVE)
                .discount(request.getDiscount() != null ? request.getDiscount() : 0.0)
                .discountStartDate(request.getDiscountStartDate())
                .discountEndDate(request.getDiscountEndDate())
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
            fileService.uploadFiles(files, "shop/" + shop.getShopId() + "/product-images/" + product.getProductId())
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
        productEventProducer.send(
                CreateProductEvent.builder()
                        .shopId(product.getShop().getShopId())
                        .categoryId(product.getCategory().getCategoryId())
                        .categoryName(product.getCategory().getCategoryName())
                        .shopStatus(product.getShop().getShopStatus())
                        .productName(product.getName())
                        .productId(product.getProductId())
                        .ownerId(ownerId)
                        .productStatus(product.getProductStatus())
                        .description(product.getDescription())
                        .discount(product.getDiscount())
                        .discountStartDate(product.getDiscountStartDate())
                        .discountEndDate(product.getDiscountEndDate())
                        .totalSold(product.getTotalSold())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .productImages(product.getProductImages().stream()
                                .map(productImage -> CreateProductEvent.CreateProductImageEvent.builder()
                                        .productImageId(productImage.getProductImageId())
                                        .imageUrl(productImage.getImageUrl())
                                        .build()
                                ).toList())
                        .productAttributes(product.getProductAttributes().stream().map(
                                productAttribute -> CreateProductEvent.CreateProductAttributeEvent.builder()
                                        .productAttributeId(productAttribute.getAttributeId())
                                        .productAttributeName(productAttribute.getAttributeName())
                                        .productAttributeValues(productAttribute.getProductAttributeValues().stream().map(attributeValue -> CreateProductEvent.CreateProductAttributeValueEvent.builder()
                                                .productAttributeValueId(attributeValue.getAttributeValueId())
                                                .value(attributeValue.getValue())
                                                .build()).toList())
                                        .build()).toList())
                        .productVariants(product.getProductVariants().stream().map(
                                productVariant -> CreateProductEvent.CreateProductVariantEvent.builder()
                                        .productVariantId(productVariant.getProductVariantId())
                                        .price(productVariant.getPrice())
                                        .productVariantStatus(productVariant.getProductVariantStatus())
                                        .stockQuantity(productVariant.getStockQuantity())
                                        .soldQuantity(productVariant.getSold())
                                        .isDefault(productVariant.getIsDefault())
                                        .productVariantAttributeValues(
                                                productVariant.getProductVariantAttributeValues().stream().map(
                                                        variantAttrValue -> CreateProductEvent.CreateProductVariantValueEvent.builder()
                                                                .productVariantAttributeValueId(variantAttrValue.getProductVariantAttributeValueId())
                                                                .productAttributeId(variantAttrValue.getProductAttributeValue().getProductAttribute().getAttributeId())
                                                                .productAttributeValueId(variantAttrValue.getProductAttributeValue().getAttributeValueId())
                                                                .build()).toList())
                                        .build()).toList())
                        .build());
    }


    @Override
    @Transactional
    public void updateProduct(Long productId, ReqUpdateProductDTO request, List<MultipartFile> files) {
        Long ownerId = userHelper.getCurrentUserId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

        if (!product.getShop().getOwnerId().equals(ownerId)) {
            throw new NotFoundException(MessageError.PRODUCT_NOT_FOUND);
        }
        if (FnCommon.isNotNull(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        } else {
            throw new NotFoundException(MessageError.CATEGORY_NOT_FOUND);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setDiscount(request.getDiscount());
        product.setDiscountStartDate(request.getDiscountStartDate());
        product.setDiscountEndDate(request.getDiscountEndDate());
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
            fileService.uploadFiles(files, "shop/" + product.getShop().getShopId() + "/product-images/" + product.getProductId())
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

                    if (existingAttr != null && FnCommon.isNotNullOrEmptyList(attrReq.getProductAttributeValues())) {

                        // Xử lý attribute values: nếu có ID thì bỏ qua (không cập nhật), không có ID thì thêm mới
                        attrReq.getProductAttributeValues().forEach(valReq -> {
                            if (valReq.getProductAttributeValueId() == null && valReq.getProductAttributeValue() != null) {
                                ProductAttributeValue newValue = ProductAttributeValue.builder()
                                        .value(valReq.getProductAttributeValue())
                                        .build();
                                existingAttr.addAttributeValue(newValue);
                            }
                        });
                    }
                } else {
                    // KHÔNG CÓ ID -> THÊM MỚI attribute
                    ProductAttribute newAttribute = ProductAttribute.builder()
                            .attributeName(attrReq.getProductAttributeName())
                            .build();

                    if (FnCommon.isNotNullOrEmptyList(attrReq.getProductAttributeValues())) {
                        attrReq.getProductAttributeValues().forEach(valReq -> {
                            ProductAttributeValue newValue = ProductAttributeValue.builder()
                                    .value(valReq.getProductAttributeValue())
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
                                variant.setProductVariantStatus(variantReq.getStockQuantity() > 0 ? ProductVariantStatus.ACTIVE : ProductVariantStatus.OUT_OF_STOCK);

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
        productEventProducer.send(
                CreateProductEvent.builder()
                        .shopId(product.getShop().getShopId())
                        .categoryId(product.getCategory().getCategoryId())
                        .categoryName(product.getCategory().getCategoryName())
                        .shopStatus(product.getShop().getShopStatus())
                        .productName(product.getName())
                        .productId(product.getProductId())
                        .ownerId(ownerId)
                        .productStatus(product.getProductStatus())
                        .description(product.getDescription())
                        .discount(product.getDiscount())
                        .discountStartDate(product.getDiscountStartDate())
                        .discountEndDate(product.getDiscountEndDate())
                        .totalSold(product.getTotalSold())
                        .createdAt(product.getCreatedAt())
                        .productImages(product.getProductImages().stream()
                                .map(productImage -> CreateProductEvent.CreateProductImageEvent.builder()
                                        .productImageId(productImage.getProductImageId())
                                        .imageUrl(productImage.getImageUrl())
                                        .build()
                                ).toList())
                        .productAttributes(product.getProductAttributes().stream().map(
                                productAttribute -> CreateProductEvent.CreateProductAttributeEvent.builder()
                                        .productAttributeId(productAttribute.getAttributeId())
                                        .productAttributeName(productAttribute.getAttributeName())
                                        .productAttributeValues(productAttribute.getProductAttributeValues().stream().map(attributeValue -> CreateProductEvent.CreateProductAttributeValueEvent.builder()
                                                .productAttributeValueId(attributeValue.getAttributeValueId())
                                                .value(attributeValue.getValue())
                                                .build()).toList())
                                        .build()).toList())
                        .productVariants(product.getProductVariants().stream().map(
                                productVariant -> CreateProductEvent.CreateProductVariantEvent.builder()
                                        .productVariantId(productVariant.getProductVariantId())
                                        .price(productVariant.getPrice())
                                        .productVariantStatus(productVariant.getProductVariantStatus())
                                        .stockQuantity(productVariant.getStockQuantity())
                                        .soldQuantity(productVariant.getSold())
                                        .isDefault(productVariant.getIsDefault())
                                        .productVariantAttributeValues(
                                                productVariant.getProductVariantAttributeValues().stream().map(
                                                        variantAttrValue -> CreateProductEvent.CreateProductVariantValueEvent.builder()
                                                                .productVariantAttributeValueId(variantAttrValue.getProductVariantAttributeValueId())
                                                                .productAttributeId(variantAttrValue.getProductAttributeValue().getProductAttribute().getAttributeId())
                                                                .productAttributeValueId(variantAttrValue.getProductAttributeValue().getAttributeValueId())
                                                                .build()).toList())
                                        .build()).toList())
                        .build());
    }

    @Override
    public void updateProductVariantStatus(Long productVariantId, ReqUpdateProductVariantStatusDTO request) {
        Long ownerId = userHelper.getCurrentUserId();
        ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND));

        if (!productVariant.getProduct().getShop().getOwnerId().equals(ownerId)) {
            throw new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND);
        }
        productVariant.setProductVariantStatus(request.getProductVariantStatus());
        productVariantRepository.save(productVariant);

        productEventProducer.send(
                UpdateProductVariantStatusEvent.builder()
                        .productId(productVariant.getProduct().getProductId())
                        .productVariantId(productVariant.getProductVariantId())
                        .status(productVariant.getProductVariantStatus())
                        .build()
        );

    }

    @Override
    public void updateProductStatusByProductId(Long productId, ReqUpdateProductStatusDTO status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        if (!product.getShop().getOwnerId().equals(userHelper.getCurrentUserId())) {
            throw new NotFoundException(MessageError.PRODUCT_NOT_FOUND);
        }
        product.setProductStatus(status.getProductStatus());
        productRepository.save(product);
        productEventProducer.send(
                UpdateProductStatusEvent.builder()
                        .productId(product.getProductId())
                        .status(product.getProductStatus())
                        .build()
        );
    }

    @Transactional
    @Override
    public void handleCreateOrderEvent(CreateOrderEvent createOrderEvent) {
        AtomicBoolean isOutOfStock = new AtomicBoolean(false);

        // Kiểm tra tồn kho trước khi cập nhật
        createOrderEvent.getCreateOrderItemEventList().forEach(orderItem -> {
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
            orderItem.setProductName(product.getName());
            orderItem.setCreateProductImageList(
                    product.getProductImages().stream()
                            .map(productImage -> CreateProductImage.builder()
                                    .imageUrl(productImage.getImageUrl())
                                    .build())
                            .collect(Collectors.toList())
            );
            orderItem.getCreateProductOrderItemEvents().forEach(productOrderItemEvent -> {

                ProductVariant productVariant = productVariantRepository.findById(productOrderItemEvent.getProductVariantId())
                        .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND));
                productOrderItemEvent.setCreateProductAttributeList(
                        productVariant.getProductVariantAttributeValues().stream()
                                .map(variantAttrValue -> CreateProductAttribute.builder()
                                        .attributeName(variantAttrValue.getProductAttributeValue().getProductAttribute().getAttributeName())
                                        .attributeValue(variantAttrValue.getProductAttributeValue().getValue())
                                        .build())
                                .collect(Collectors.toList())
                );

                if (productVariant.getProductVariantStatus() == ProductVariantStatus.INACTIVE) {
                    orderEventProducer.send(
                            OrderStatusEvent.builder()
                                    .userId(createOrderEvent.getUserId())
                                    .orderId(createOrderEvent.getOrderId())
                                    .orderStatus(OrderStatus.CANCELLED)
                                    .reason(messageService.getMessage(MessageError.PRODUCT_VARIANT_INACTIVE))
                                    .build()
                    );
                    createOrderEvent.setOrderStatus(OrderStatus.CANCELLED);
                    createOrderEvent.setReason(messageService.getMessage(MessageError.PRODUCT_VARIANT_INACTIVE));
                    orderEventProducer.send(createOrderEvent);
                    return;
                }

                int updatedStock = productVariant.getStockQuantity() - productOrderItemEvent.getQuantity();


                if (updatedStock < 0) {
                    isOutOfStock.set(true);

                }
            });
        });

        if (isOutOfStock.get()) {
            orderEventProducer.send(
                    OrderStatusEvent.builder()
                            .userId(createOrderEvent.getUserId())
                            .orderId(createOrderEvent.getOrderId())
                            .orderStatus(OrderStatus.CANCELLED)
                            .reason(messageService.getMessage(MessageError.INSUFFICIENT_PRODUCT_VARIANT_STOCK))
                            .build()
            );
            createOrderEvent.setOrderStatus(OrderStatus.CANCELLED);
            createOrderEvent.setReason(messageService.getMessage(MessageError.INSUFFICIENT_PRODUCT_VARIANT_STOCK));
            orderEventProducer.send(createOrderEvent);
        } else {

            createOrderEvent.getCreateOrderItemEventList().forEach(orderItem -> {
                Product product = productRepository.findById(orderItem.getProductId())
                        .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
                orderItem.getCreateProductOrderItemEvents().forEach(productOrderItemEvent -> {

                    ProductVariant productVariant = productVariantRepository.findByIdForUpDate(productOrderItemEvent.getProductVariantId())
                            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND));

                    if (productVariant.getProductVariantStatus() == ProductVariantStatus.INACTIVE) {
                        orderEventProducer.send(
                                OrderStatusEvent.builder()
                                        .userId(createOrderEvent.getUserId())
                                        .orderId(createOrderEvent.getOrderId())
                                        .orderStatus(OrderStatus.CANCELLED)
                                        .reason(messageService.getMessage(MessageError.PRODUCT_VARIANT_INACTIVE))
                                        .build()
                        );
                        createOrderEvent.setOrderStatus(OrderStatus.CANCELLED);
                        createOrderEvent.setReason(messageService.getMessage(MessageError.PRODUCT_VARIANT_INACTIVE));
                        orderEventProducer.send(createOrderEvent);
                        return;
                    }

                    int updatedStock = productVariant.getStockQuantity() - productOrderItemEvent.getQuantity();

                    if (updatedStock == 0) {
                        productVariant.setProductVariantStatus(ProductVariantStatus.OUT_OF_STOCK);
                    }

                    productVariant.setStockQuantity(updatedStock);
                    productVariant.addSold(productOrderItemEvent.getQuantity());
                    product.addSold(productOrderItemEvent.getQuantity());

                    productVariantRepository.save(productVariant);
                });
                productRepository.save(product);
            });


            createOrderEvent.setOrderStatus(OrderStatus.PAID);
            orderEventProducer.send(createOrderEvent);
            orderEventProducer.send(
                    OrderStatusEvent.builder()
                            .userId(createOrderEvent.getUserId())
                            .orderId(createOrderEvent.getOrderId())
                            .orderStatus(OrderStatus.PAID)
                            .build()
            );
        }
    }

}

