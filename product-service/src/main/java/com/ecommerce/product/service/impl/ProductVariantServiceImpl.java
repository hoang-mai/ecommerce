package com.ecommerce.product.service.impl;

import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.product.dto.ReqUpdateProductVariantDTO;
import com.ecommerce.product.dto.ResProductVariantDTO;
import com.ecommerce.product.entity.ProductVariant;
import com.ecommerce.product.repository.ProductVariantRepository;
import com.ecommerce.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional(readOnly = true)
    public ResProductVariantDTO getVariantById(Long variantId) {
        log.info("Fetching product variant with id: {}", variantId);
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Product variant not found with id: " + variantId));
        return toProductVariantResponse(variant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResProductVariantDTO> getVariantsByProductId(Long productId) {
        log.info("Fetching product variants for product id: {}", productId);
        List<ProductVariant> variants = productVariantRepository.findByProductProductId(productId);
        return variants.stream()
                .map(this::toProductVariantResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResProductVariantDTO updateVariant(Long variantId, ReqUpdateProductVariantDTO request) {
        log.info("Updating product variant with id: {}", variantId);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Product variant not found with id: " + variantId));

        variant.setPrice(request.getPrice());
        variant.setStockQuantity(request.getStockQuantity());

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        log.info("Product variant updated successfully with id: {}", variantId);
        return toProductVariantResponse(updatedVariant);
    }

    @Override
    @Transactional
    public void deleteVariant(Long variantId) {
        log.info("Deleting product variant with id: {}", variantId);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Product variant not found with id: " + variantId));

        productVariantRepository.delete(variant);
        log.info("Product variant deleted successfully with id: {}", variantId);
    }

    @Override
    @Transactional
    public ResProductVariantDTO updateStock(Long variantId, Integer quantity) {
        log.info("Updating stock for variant id: {} with quantity: {}", variantId, quantity);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Product variant not found with id: " + variantId));

        variant.setStockQuantity(variant.getStockQuantity() + quantity);

        if (variant.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Insufficient stock for variant id: " + variantId);
        }

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        log.info("Stock updated successfully for variant id: {}", variantId);
        return toProductVariantResponse(updatedVariant);
    }

    /**
     * Convert ProductVariant entity to ResProductVariantDTO
     */
    private ResProductVariantDTO toProductVariantResponse(ProductVariant variant) {
        Map<String, String> attributeValues = variant.getProductVariantAttributeValues()
                .stream()
                .collect(Collectors.toMap(
                        variantAttrValue -> variantAttrValue.getProductAttributeValue()
                                .getProductAttribute().getAttributeName(),
                        variantAttrValue -> variantAttrValue.getProductAttributeValue().getValue()
                ));

        return ResProductVariantDTO.builder()
                .productVariantId(variant.getProductVariantId())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .attributeValues(attributeValues)
                .build();
    }
}
