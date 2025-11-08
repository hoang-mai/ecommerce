package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.ReqCreateProductImageDTO;
import com.ecommerce.product.dto.ReqUpdateProductImageDTO;
import com.ecommerce.product.dto.ResProductImageDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.repository.ProductImageRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.IProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServiceImpl implements IProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    /**
     * Tạo mới một product image
     *
     * @param request Request DTO chứa thông tin product image
     * @return ResProductImageDTO
     */
    @Override
    @Transactional
    public ResProductImageDTO createProductImage(ReqCreateProductImageDTO request) {
        log.info("Creating product image for product ID: {}", request.getProductId());

        // Kiểm tra product có tồn tại không
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.getProductId()));

        // Tạo product image mới
        ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageUrl(request.getImageUrl())
                .build();

        ProductImage savedImage = productImageRepository.save(productImage);
        log.info("Product image created successfully with ID: {}", savedImage.getProductImageId());

        return mapToResProductImageDTO(savedImage);
    }

    /**
     * Lấy thông tin product image theo ID
     *
     * @param imageId ID của product image
     * @return ResProductImageDTO
     */
    @Override
    @Transactional(readOnly = true)
    public ResProductImageDTO getProductImageById(Long imageId) {
        log.info("Fetching product image with ID: {}", imageId);

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Product image not found with ID: " + imageId));

        return mapToResProductImageDTO(productImage);
    }

    /**
     * Lấy danh sách product images theo product ID
     *
     * @param productId ID của product
     * @return List of ResProductImageDTO
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResProductImageDTO> getProductImagesByProductId(Long productId) {
        log.info("Fetching product images for product ID: {}", productId);

        // Kiểm tra product có tồn tại không
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(productId);

        return productImages.stream()
                .map(this::mapToResProductImageDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin product image
     *
     * @param imageId ID của product image cần cập nhật
     * @param request Request DTO chứa thông tin cập nhật
     * @return ResProductImageDTO
     */
    @Override
    @Transactional
    public ResProductImageDTO updateProductImage(Long imageId, ReqUpdateProductImageDTO request) {
        log.info("Updating product image with ID: {}", imageId);

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Product image not found with ID: " + imageId));

        // Cập nhật thông tin
        productImage.setImageUrl(request.getImageUrl());

        ProductImage updatedImage = productImageRepository.save(productImage);
        log.info("Product image updated successfully with ID: {}", updatedImage.getProductImageId());

        return mapToResProductImageDTO(updatedImage);
    }

    /**
     * Xóa product image
     *
     * @param imageId ID của product image cần xóa
     */
    @Override
    @Transactional
    public void deleteProductImage(Long imageId) {
        log.info("Deleting product image with ID: {}", imageId);

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Product image not found with ID: " + imageId));

        productImageRepository.delete(productImage);
        log.info("Product image deleted successfully with ID: {}", imageId);
    }

    /**
     * Map ProductImage entity to ResProductImageDTO
     */
    private ResProductImageDTO mapToResProductImageDTO(ProductImage productImage) {
        return ResProductImageDTO.builder()
                .productImageId(productImage.getProductImageId())
                .imageUrl(productImage.getImageUrl())
                .build();
    }
}

