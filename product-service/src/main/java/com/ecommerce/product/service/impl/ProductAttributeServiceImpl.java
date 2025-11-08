package com.ecommerce.product.service.impl;

import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.product.dto.ReqCreateProductAttributeDTO;
import com.ecommerce.product.dto.ReqUpdateProductAttributeDTO;
import com.ecommerce.product.dto.ResProductAttributeDTO;
import com.ecommerce.product.dto.ResProductAttributeValueDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductAttribute;
import com.ecommerce.product.entity.ProductAttributeValue;
import com.ecommerce.product.repository.ProductAttributeRepository;
import com.ecommerce.product.repository.ProductAttributeValueRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final ProductRepository productRepository;

    @Override
    public void createProductAttribute(ReqCreateProductAttributeDTO request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

        ProductAttribute productAttribute = ProductAttribute.builder()
                .product(product)
                .attributeName(request.getAttributeName())
                .build();

        List<ProductAttributeValue> attributeValues = request.getAttributeValues().stream()
                .map(value -> ProductAttributeValue.builder()
                        .productAttribute(productAttribute)
                        .value(value)
                        .build())
                .toList();
        productAttribute.setProductAttributeValues(attributeValues);
        productAttributeRepository.save(productAttribute);
    }

    /**
     * Lấy thông tin product attribute theo ID
     *
     * @param attributeId ID của product attribute
     * @return ResProductAttributeDTO
     */
    @Override
    @Transactional(readOnly = true)
    public ResProductAttributeDTO getProductAttributeById(Long attributeId) {
        log.info("Fetching product attribute with ID: {}", attributeId);

        ProductAttribute productAttribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new RuntimeException("Product attribute not found with ID: " + attributeId));

        return mapToResProductAttributeDTO(productAttribute);
    }

    /**
     * Lấy danh sách product attributes theo product ID
     *
     * @param productId ID của product
     * @return List of ResProductAttributeDTO
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResProductAttributeDTO> getProductAttributesByProductId(Long productId) {
        log.info("Fetching product attributes for product ID: {}", productId);

        // Kiểm tra product có tồn tại không
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        List<ProductAttribute> productAttributes = productAttributeRepository.findByProduct_ProductId(productId);

        return productAttributes.stream()
                .map(this::mapToResProductAttributeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin product attribute
     *
     * @param attributeId ID của product attribute cần cập nhật
     * @param request Request DTO chứa thông tin cập nhật
     * @return ResProductAttributeDTO
     */
    @Override
    @Transactional
    public ResProductAttributeDTO updateProductAttribute(Long attributeId, ReqUpdateProductAttributeDTO request) {
        log.info("Updating product attribute with ID: {}", attributeId);

        ProductAttribute productAttribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new RuntimeException("Product attribute not found with ID: " + attributeId));

        // Cập nhật tên attribute
        productAttribute.setAttributeName(request.getAttributeName());

        // Xóa các attribute values cũ
        if (productAttribute.getProductAttributeValues() != null) {
            productAttributeValueRepository.deleteAll(productAttribute.getProductAttributeValues());
        }

        // Tạo các attribute values mới
        List<ProductAttributeValue> newAttributeValues = request.getAttributeValues().stream()
                .map(value -> ProductAttributeValue.builder()
                        .productAttribute(productAttribute)
                        .value(value)
                        .build())
                .collect(Collectors.toList());

        List<ProductAttributeValue> savedValues = productAttributeValueRepository.saveAll(newAttributeValues);
        productAttribute.setProductAttributeValues(savedValues);

        ProductAttribute updatedAttribute = productAttributeRepository.save(productAttribute);
        log.info("Product attribute updated successfully with ID: {}", updatedAttribute.getAttributeId());

        return mapToResProductAttributeDTO(updatedAttribute);
    }

    /**
     * Xóa product attribute
     *
     * @param attributeId ID của product attribute cần xóa
     */
    @Override
    @Transactional
    public void deleteProductAttribute(Long attributeId) {
        log.info("Deleting product attribute with ID: {}", attributeId);

        ProductAttribute productAttribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new RuntimeException("Product attribute not found with ID: " + attributeId));

        productAttributeRepository.delete(productAttribute);
        log.info("Product attribute deleted successfully with ID: {}", attributeId);
    }

    /**
     * Map ProductAttribute entity to ResProductAttributeDTO
     */
    private ResProductAttributeDTO mapToResProductAttributeDTO(ProductAttribute productAttribute) {
        List<ResProductAttributeValueDTO> attributeValueDTOs = productAttribute.getProductAttributeValues() != null
                ? productAttribute.getProductAttributeValues().stream()
                .map(value -> ResProductAttributeValueDTO.builder()
                        .attributeValueId(value.getAttributeValueId())
                        .attributeValue(value.getValue())
                        .build())
                .collect(Collectors.toList())
                : List.of();

        return ResProductAttributeDTO.builder()
                .productAttributeId(productAttribute.getAttributeId())
                .attributeName(productAttribute.getAttributeName())
                .attributeValues(attributeValueDTOs)
                .build();
    }
}
