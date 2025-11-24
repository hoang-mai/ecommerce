package com.ecommerce.read.service.impl;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.ProductViewDTO;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.repository.ProductViewRepository;
import com.ecommerce.read.repository.impl.ProductViewRepositoryImpl;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductViewServiceImpl implements ProductViewService {
    private final ProductViewRepository productViewRepository;
    private final ProductViewRepositoryImpl productViewRepositoryImpl;
    private final FileService fileService;

    @Override
    public void createProductEvent(CreateProductEvent event) {
        ProductView productView = ProductView.builder()
                .productId(String.valueOf(event.getProductId()))
                .shopId(String.valueOf(event.getShopId()))
                .name(event.getProductName())
                .description(event.getDescription())
                .productStatus(event.getProductStatus())
                .totalSold(event.getTotalSold())
                .discount(event.getDiscount())
                .discountStartDate(event.getDiscountStartDate())
                .discountEndDate(event.getDiscountEndDate())
                .categoryId(event.getCategoryId() == null ? null : String.valueOf(event.getCategoryId()))
                .productImages(event.getProductImages() == null ? null : event.getProductImages().stream()
                        .map(img -> ProductView.ProductImage.builder()
                                .productImageId(String.valueOf(img.getProductImageId()))
                                .url(img.getImageUrl())
                                .build())
                        .toList())
                .productAttributes(event.getProductAttributes() == null ? null : event.getProductAttributes().stream()
                        .map(attr -> ProductView.ProductAttribute.builder()
                                .productAttributeId(String.valueOf(attr.getProductAttributeId()))
                                .productAttributeName(attr.getProductAttributeName())
                                .productAttributeValues(attr.getProductAttributeValues() == null ? null : attr.getProductAttributeValues().stream()
                                        .map(val -> ProductView.ProductAttributeValue.builder()
                                                .productAttributeValueId(String.valueOf(val.getProductAttributeValueId()))
                                                .productAttributeValue(val.getValue())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .productVariants(event.getProductVariants() == null ? null : event.getProductVariants().stream()
                        .map(variant -> ProductView.ProductVariant.builder()
                                .productVariantId(String.valueOf(variant.getProductVariantId()))
                                .price(variant.getPrice())
                                .stockQuantity(variant.getStockQuantity())
                                .sold(variant.getSoldQuantity())
                                .productVariantStatus(variant.getProductVariantStatus())
                                .isDefault(variant.getIsDefault())
                                .productVariantAttributeValues(variant.getProductVariantAttributeValues() == null ? null : variant.getProductVariantAttributeValues().stream()
                                        .map(val -> ProductView.ProductVariantAttributeValue.builder()
                                                .productVariantAttributeValueId(String.valueOf(val.getProductVariantAttributeValueId()))
                                                .productAttributeId(String.valueOf(val.getProductAttributeId()))
                                                .productAttributeValueId(String.valueOf(val.getProductAttributeValueId()))
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
        productViewRepository.save(productView);
    }

    @Override
    public void updateProductStatus(UpdateProductStatusEvent event) {
        ProductView productView = productViewRepository.findById(String.valueOf(event.getProductId()))
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        productView.setProductStatus(event.getStatus());
        productViewRepository.save(productView);
    }

    @Override
    public void updateProductVariantStatus(UpdateProductVariantStatusEvent event) {
        productViewRepositoryImpl.updateProductVariantStatus(event);
    }

    @Override
    public PageResponse<ProductViewDTO> searchProducts(Long shopId, Long categoryId, ProductStatus status, String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ProductView> productsPage = productViewRepositoryImpl.getProductView(shopId, categoryId, status, keyword, pageable);

        return PageResponse.<ProductViewDTO>builder()
                .data(productsPage.getContent().stream().map(this::toDTO).collect(Collectors.toList()))
                .pageNo(productsPage.getNumber())
                .pageSize(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .build();
    }

    @Override
    public ProductViewDTO getProductById(Long productId) {
        ProductView productView = productViewRepository.findById(String.valueOf(productId))
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        return toDTO(productView);
    }

    @Override
    public void updateStockAfterCreateOrder(CreateOrderEvent createOrderViewEvent) {

        createOrderViewEvent.getCreateOrderItemEventList().forEach(orderItem -> {

            ProductView productView = productViewRepository.findById(String.valueOf(orderItem.getProductId()))
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

            orderItem.getCreateProductOrderItemEvents().forEach(productOrderItemEvent -> {

                String variantId = String.valueOf(productOrderItemEvent.getProductVariantId());

                ProductView.ProductVariant matchedVariant = productView.getProductVariants().stream()
                        .filter(v -> variantId.equals(v.getProductVariantId()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND));

                int currentStock = matchedVariant.getStockQuantity() == null ? 0 : matchedVariant.getStockQuantity();
                int quantity = productOrderItemEvent.getQuantity() == null ? 0 : productOrderItemEvent.getQuantity();
                int updatedStock = currentStock - quantity;

                if (updatedStock <= 0) {
                    matchedVariant.setStockQuantity(0);
                    matchedVariant.setProductVariantStatus(ProductVariantStatus.OUT_OF_STOCK);
                } else {
                    matchedVariant.setStockQuantity(updatedStock);
                }

                matchedVariant.addSold(quantity);
                productView.addSold(quantity);
            });

            productViewRepository.save(productView);
        });
    }

    private ProductViewDTO toDTO(ProductView productView) {

        return ProductViewDTO.builder()
                ._id(productView.getProductId())
                .shopId(productView.getShopId())
                .rating(productView.getRating())
                .name(productView.getName())
                .description(productView.getDescription())
                .productStatus(productView.getProductStatus())
                .totalSold(productView.getTotalSold())
                .discount(productView.getDiscount())
                .discountStartDate(productView.getDiscountStartDate())
                .discountEndDate(productView.getDiscountEndDate())
                .categoryId(productView.getCategoryId())
                .productImages(productView.getProductImages().stream().map(img -> ProductViewDTO.ProductImageDTO.builder()
                        ._id(img.getProductImageId())
                        .url(fileService.getPresignedUrl(img.getUrl()))
                        .build()).collect(Collectors.toList()))
                .productAttributes(productView.getProductAttributes().stream().map(attr -> ProductViewDTO.ProductAttributeDTO.builder()
                        ._id(attr.getProductAttributeId())
                        .productAttributeName(attr.getProductAttributeName())
                        .productAttributeValues(attr.getProductAttributeValues().stream().map(val -> ProductViewDTO.ProductAttributeValueDTO.builder()
                                ._id(val.getProductAttributeValueId())
                                .productAttributeValue(val.getProductAttributeValue())
                                .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList()))
                .productVariants(productView.getProductVariants().stream().map(variant -> ProductViewDTO.ProductVariantDTO.builder()
                        ._id(variant.getProductVariantId())
                        .productVariantStatus(variant.getProductVariantStatus())
                        .price(variant.getPrice())
                        .stockQuantity(variant.getStockQuantity())
                        .sold(variant.getSold())
                        .isDefault(variant.getIsDefault())
                        .productVariantAttributeValues(variant.getProductVariantAttributeValues().stream().map(val -> ProductViewDTO.ProductVariantAttributeValueDTO.builder()
                                ._id(val.getProductVariantAttributeValueId())
                                .productAttributeId(val.getProductAttributeId())
                                .productAttributeValueId(val.getProductAttributeValueId())
                                .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
