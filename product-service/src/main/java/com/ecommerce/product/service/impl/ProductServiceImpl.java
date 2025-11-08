package com.ecommerce.product.service.impl;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.*;
import com.ecommerce.product.entity.ProductVariantAttributeValue;
import com.ecommerce.product.repository.*;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public void createProduct(ReqCreateProductDTO request) {

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

        if (FnCommon.isNotNullOrEmptyList(request.getImageUrls())) {
            List<ProductImage> images = request.getImageUrls().stream()
                    .map(url -> ProductImage.builder()
                            .product(product)
                            .imageUrl(url)
                            .build())
                    .collect(Collectors.toList());
            product.setProductImages(images);
        }


        if (FnCommon.isNotNullOrEmptyList(request.getProductAttributes())) {
            List<ProductAttribute> attributes = new ArrayList<>();
            for (ReqProductAttributeDTO attrReq : request.getProductAttributes()) {
                ProductAttribute attribute = ProductAttribute.builder()
                        .product(product)
                        .attributeName(attrReq.getAttributeName())
                        .build();

                List<ProductAttributeValue> attributeValues = attrReq.getAttributeValues().stream()
                        .map(val -> ProductAttributeValue.builder()
                                .productAttribute(attribute)
                                .value(val)
                                .build())
                        .collect(Collectors.toList());
                attribute.setProductAttributeValues(attributeValues);
            }
            product.setProductAttributes(attributes);
        }

        if (FnCommon.isNotNullOrEmptyList(request.getProductVariants())) {

            for (ReqProductVariantDTO variantReq : request.getProductVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .price(variantReq.getPrice())
                        .stockQuantity(variantReq.getStockQuantity())
                        .build();


                if (FnCommon.isNotNullOrEmptyMap(variantReq.getAttributeValues())) {
                    List<ProductVariantAttributeValue> variantAttrValues = new ArrayList<>();

                    for (Map.Entry<String, String> entry : variantReq.getAttributeValues().entrySet()) {
                        String attributeName = entry.getKey();
                        String attributeValue = entry.getValue();

                        ProductAttributeValue matchingValue = product.getProductAttributes().stream()
                                .filter(attr -> attr.getAttributeName().equals(attributeName))
                                .flatMap(attr -> attr.getProductAttributeValues().stream())
                                .filter(val -> val.getValue().equals(attributeValue))
                                .findFirst()
                                .orElse(null);

                        if (matchingValue != null) {
                            ProductVariantAttributeValue variantAttrValue = ProductVariantAttributeValue.builder()
                                    .productVariant(variant)
                                    .productAttributeValue(matchingValue)
                                    .build();
                            variantAttrValues.add(variantAttrValue);
                        }
                    }
                    variant.setProductVariantAttributeValues(variantAttrValues);
                }
                product.getProductVariants().add(variant);
            }
        }
        productRepository.save(product);
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
                .productStatus(product.getProductStatus())
                .category(buildCategoryResponse(product.getCategory()))
                .productImages(buildProductImagesResponse(product.getProductImages()))
                .productAttributes(buildProductAttributesResponse(product.getProductAttributes()))
                .productVariants(buildProductVariantsResponse(product.getProductVariants()))
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
                        .imageUrl(productImage.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ResProductAttributeDTO> buildProductAttributesResponse(List<ProductAttribute> productAttributes) {
        if (!FnCommon.isNotNullOrEmptyList(productAttributes)) {
            return new ArrayList<>();
        }
        return productAttributes.stream()
                .map(productAttribute -> ResProductAttributeDTO.builder()
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
                            .attributeValues(attributeValues)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateProduct(Long productId, ReqUpdateProductDTO request) {

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
        product.setProductStatus(request.getProductStatus());

        productRepository.save(product);

    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<ResProductDTO> searchProducts(Long shopId, Long categoryId, ProductStatus status,
                                                      String keyword, int pageNo, int pageSize,
                                                      String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Product> productsPage = productRepository.searchProducts(shopId, categoryId, status, keyword, pageable);

        return buildPageResponse(productsPage);
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
                .build();
    }
}

