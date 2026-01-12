package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.*;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.ProductViewHomePageDTO;
import com.ecommerce.read.dto.ProductViewStatisticDTO;
import com.ecommerce.read.dto.SearchKeywordDTO;
import com.ecommerce.read.entity.FlashSaleProductView;
import com.ecommerce.read.entity.ProductSearch;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.entity.SearchView;
import com.ecommerce.read.entity.UserCategoryScore;
import com.ecommerce.read.repository.FlashSaleProductViewRepository;
import com.ecommerce.read.repository.ProductSearchRepository;
import com.ecommerce.read.repository.ProductViewRepository;
import com.ecommerce.read.repository.SearchViewRepository;
import com.ecommerce.read.repository.impl.*;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.ProductViewService;
import com.ecommerce.read.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductViewServiceImpl implements ProductViewService {
    private final ShopViewRepositoryImpl shopViewRepositoryImpl;
    private final ProductViewRepository productViewRepository;
    private final ProductViewRepositoryImpl productViewRepositoryImpl;
    private final SearchViewRepository searchViewRepository;
    private final SearchViewRepositoryImpl searchViewRepositoryImpl;
    private final FileService fileService;
    private final UserHelper userHelper;
    private final UserCategoryRepositoryImpl userCategoryRepositoryImpl;
    private final ProductSearchRepository productSearchRepository;
    private final ProductSearchRepositoryImpl productSearchRepositoryImpl;
    private final FlashSaleProductViewRepository flashSaleProductViewRepository;
    private final SearchKeywordService searchKeywordService;

    @Value("${ai-service.host}")
    private String aiServiceHost;

    @Value("${ai-service.port}")
    private String aiServicePort;

    @Override
    public void createProductEvent(CreateProductEvent event) {
        ProductView productViewExisting = productViewRepository.findById(String.valueOf(event.getProductId()))
            .orElse(null);
        AtomicReference<BigDecimal> basePrice = new AtomicReference<>(BigDecimal.ZERO);

        if (FnCommon.isNotNull(productViewExisting)) {

            productViewExisting.setName(event.getProductName());
            productViewExisting.setDescription(event.getDescription());
            productViewExisting.setProductStatus(event.getProductStatus());
            productViewExisting.setOwnerId(String.valueOf(event.getOwnerId()));
            productViewExisting.setUpdatedAt(event.getUpdatedAt());
            productViewExisting.setCategoryId(event.getCategoryId() == null ? null : String.valueOf(event.getCategoryId()));
            productViewExisting.setCategoryName(event.getCategoryName());
            productViewExisting.setShopId(event.getShopId() == null ? null : String.valueOf(event.getShopId()));
            productViewExisting.setShopStatus(event.getShopStatus());
            productViewExisting.setProductDetails(event.getProductDetails());
            productViewExisting.setProductImages(event.getProductImages() == null ? null : event.getProductImages().stream()
                .map(img -> ProductView.ProductImage.builder()
                    ._id(String.valueOf(img.getProductImageId()))
                    .imageUrl(img.getImageUrl())
                    .build())
                .toList());

            productViewExisting.setProductAttributes(event.getProductAttributes() == null ? null : event.getProductAttributes().stream()
                .map(attr -> ProductView.ProductAttribute.builder()
                    ._id(String.valueOf(attr.getProductAttributeId()))
                    .productAttributeName(attr.getProductAttributeName())
                    .productAttributeValues(attr.getProductAttributeValues() == null ? null : attr.getProductAttributeValues().stream()
                        .map(val -> ProductView.ProductAttributeValue.builder()
                            ._id(String.valueOf(val.getProductAttributeValueId()))
                            .productAttributeValue(val.getValue())
                            .build())
                        .toList())
                    .build())
                .toList());

            productViewExisting.setProductVariants(event.getProductVariants() == null ? null : event.getProductVariants().stream()
                .map(variant -> {
                    if (variant.getIsDefault()) {
                        basePrice.set(variant.getPrice());
                    }
                    return ProductView.ProductVariant.builder()
                        ._id(String.valueOf(variant.getProductVariantId()))
                        .price(variant.getPrice())
                        .salePrice(variant.getSalePrice())
                        .stockQuantity(variant.getStockQuantity())
                        .productVariantStatus(variant.getProductVariantStatus())
                        .isDefault(variant.getIsDefault())
                        .productVariantAttributeValues(variant.getProductVariantAttributeValues() == null ? null : variant.getProductVariantAttributeValues().stream()
                            .map(val -> ProductView.ProductVariantAttributeValue.builder()
                                ._id(String.valueOf(val.getProductVariantAttributeValueId()))
                                .productAttributeId(String.valueOf(val.getProductAttributeId()))
                                .productAttributeValueId(String.valueOf(val.getProductAttributeValueId()))
                                .build())
                            .toList())
                        .build();
                })
                .toList());

            productViewExisting.setBasePrice(basePrice.get());
            productViewRepository.save(productViewExisting);
            createProductSearch(productViewExisting, basePrice.get());
        } else {
            ProductView productView = ProductView.builder()
                ._id(String.valueOf(event.getProductId()))
                .shopId(event.getShopId() == null ? null : String.valueOf(event.getShopId()))
                .name(event.getProductName())
                .description(event.getDescription())
                .productStatus(event.getProductStatus())
                .ownerId(String.valueOf(event.getOwnerId()))
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .categoryId(event.getCategoryId() == null ? null : String.valueOf(event.getCategoryId()))
                .categoryName(event.getCategoryName())
                .shopStatus(event.getShopStatus())
                .productDetails(event.getProductDetails())
                .productImages(event.getProductImages() == null ? null : event.getProductImages().stream()
                    .map(img -> ProductView.ProductImage.builder()
                        ._id(String.valueOf(img.getProductImageId()))
                        .imageUrl(img.getImageUrl())
                        .build())
                    .toList())
                .productAttributes(event.getProductAttributes() == null ? null : event.getProductAttributes().stream()
                    .map(attr -> ProductView.ProductAttribute.builder()
                        ._id(String.valueOf(attr.getProductAttributeId()))
                        .productAttributeName(attr.getProductAttributeName())
                        .productAttributeValues(attr.getProductAttributeValues() == null ? null : attr.getProductAttributeValues().stream()
                            .map(val -> ProductView.ProductAttributeValue.builder()
                                ._id(String.valueOf(val.getProductAttributeValueId()))
                                .productAttributeValue(val.getValue())
                                .build())
                            .toList())
                        .build())
                    .toList())
                .productVariants(event.getProductVariants() == null ? null : event.getProductVariants().stream()
                    .map(variant -> {
                        if (variant.getIsDefault()) {
                            basePrice.set(variant.getPrice());
                        }
                        return ProductView.ProductVariant.builder()
                            ._id(String.valueOf(variant.getProductVariantId()))
                            .price(variant.getPrice())
                            .salePrice(variant.getSalePrice())
                            .stockQuantity(variant.getStockQuantity())
                            .productVariantStatus(variant.getProductVariantStatus())
                            .isDefault(variant.getIsDefault())
                            .productVariantAttributeValues(variant.getProductVariantAttributeValues() == null ? null : variant.getProductVariantAttributeValues().stream()
                                .map(val -> ProductView.ProductVariantAttributeValue.builder()
                                    ._id(String.valueOf(val.getProductVariantAttributeValueId()))
                                    .productAttributeId(String.valueOf(val.getProductAttributeId()))
                                    .productAttributeValueId(String.valueOf(val.getProductAttributeValueId()))
                                    .build())
                                .toList())
                            .build();
                    })
                    .toList())
                .build();
            productView.setBasePrice(basePrice.get());
            productViewRepository.save(productView);
            createProductSearch(productView, basePrice.get());
            if (Boolean.TRUE.equals(event.getCreated())) {
                shopViewRepositoryImpl.incrementProductCount(event.getShopId());
            }
            searchKeywordService.createSearchKeyword(SearchKeywordDTO.builder()
                .keyword(event.getProductName())
                .build());
        }

        updateFlashSaleProductsAsync(event);
    }

    @Async
    public void createProductSearch(ProductView productView, BigDecimal basePrice) {
        ProductSearch productViewExisting = productSearchRepository.findById(productView.get_id()).orElse(null);

        if (FnCommon.isNotNull(productViewExisting)) {
            productViewExisting.setName(productView.getName());
            productViewExisting.setCategoryId(productView.getCategoryId());
            productViewExisting.setCategoryName(productView.getCategoryName());
            productViewExisting.setShopId(productView.getShopId());
            productViewExisting.setBasePrice(basePrice);
            productViewExisting.setProductStatus(productView.getProductStatus());
            productViewExisting.setShopStatus(productView.getShopStatus());
            productSearchRepository.save(productViewExisting);
        } else {
            ProductSearch productSearch = ProductSearch.builder()
                .id(String.valueOf(productView.get_id()))
                .name(productView.getName())
                .categoryId(productView.getCategoryId())
                .categoryName(productView.getCategoryName())
                .shopId(productView.getShopId())
                .basePrice(basePrice)
                .productStatus(productView.getProductStatus())
                .shopStatus(productView.getShopStatus())
                .totalSold(0)
                .rating(0.0)
                .createdAt(productView.getCreatedAt())
                .build();
            productSearchRepository.save(productSearch);
        }
    }

    /**
     * Update flash sale products asynchronously when product is updated
     * Only updates flash sale products with startTime > current date (future flash sales)
     */
    @Async
    public void updateFlashSaleProductsAsync(CreateProductEvent event) {

        Instant now = Instant.now();

        String productId = String.valueOf(event.getProductId());

        List<FlashSaleProductView> flashSaleProducts = flashSaleProductViewRepository
            .findByProductIdAndStartTimeAfter(productId, now);

        if (flashSaleProducts.isEmpty()) {
            return;
        }

        List<ProductView.ProductImage> productImages = null;
        if (event.getProductImages() != null && !event.getProductImages().isEmpty()) {
            productImages = event.getProductImages().stream()
                .map(img -> ProductView.ProductImage.builder()
                    ._id(String.valueOf(img.getProductImageId()))
                    .imageUrl(img.getImageUrl())
                    .build())
                .toList();
        }


        Map<String, BigDecimal> variantPriceMap = new HashMap<>();
        if (event.getProductVariants() != null) {
            for (CreateProductEvent.CreateProductVariantEvent variant : event.getProductVariants()) {
                variantPriceMap.put(String.valueOf(variant.getProductVariantId()), variant.getPrice());
            }
        }

        List<ProductView.ProductImage> finalProductImages = productImages;
        flashSaleProducts.forEach(flashSaleProduct -> {

            flashSaleProduct.setProductName(event.getProductName());

            if (finalProductImages != null) {
                flashSaleProduct.setProductImages(finalProductImages);
            }

            String variantId = flashSaleProduct.getProductVariantId();
            if (variantId != null && variantPriceMap.containsKey(variantId)) {
                BigDecimal variantPrice = variantPriceMap.get(variantId);
                if (variantPrice != null) {
                    flashSaleProduct.setOriginalPrice(variantPrice);
                }
            }
        });
        flashSaleProductViewRepository.saveAll(flashSaleProducts);
    }

    @Override
    public void updateProductStatus(UpdateProductStatusEvent event) {
        ProductView productView = productViewRepository.findById(String.valueOf(event.getProductId()))
            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        productView.setProductStatus(event.getStatus());
        productViewRepository.save(productView);
        shopViewRepositoryImpl.updateProductStatusInShopView(productView.getShopId(), event.getStatus());
        updateProductSearchStatus(event);
    }

    @Async
    public void updateProductSearchStatus(UpdateProductStatusEvent event) {
        ProductSearch productSearch = productSearchRepository.findById(String.valueOf(event.getProductId()))
            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        productSearch.setProductStatus(event.getStatus());
        productSearchRepository.save(productSearch);
    }

    @Override
    public void updateProductVariantStatus(UpdateProductVariantStatusEvent event) {
        productViewRepositoryImpl.updateProductVariantStatus(event);
    }

    @Override
    public PageResponse<ProductView> searchProducts(String searchId, Boolean isOwner, Long shopId, Long categoryId, ProductStatus status, String keyword, Integer star, BigDecimal startPrice, BigDecimal endPrice, int pageNo, int pageSize, String sortBy, String sortDir) {

        Map<String, Float> imageScoreMap = Map.of();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        if (FnCommon.isNotNullOrEmpty(searchId)) {
            pageable = PageRequest.of(pageNo, pageSize);
            SearchView searchView = searchViewRepositoryImpl.getById(searchId);
            if (!FnCommon.isNotNull(searchView)) {
                throw new NotFoundException(MessageError.PRODUCT_NOT_FOUND);
            }
            imageScoreMap = searchView.getSearchImages().stream()
                .collect(Collectors.toMap(
                    SearchView.SearchImageResult::getProductId,
                    SearchView.SearchImageResult::getSimilarityScore,
                    (a, b) -> a
                ));

        }
        Long ownerId;
        ShopStatus shopStatus = null;

        try {
            Role currentUserRole = userHelper.getRole();

            if (currentUserRole == Role.ADMIN) {
                Page<ProductView> productsPage = productViewRepositoryImpl.getProductView(null, null, shopId, categoryId, status, shopStatus, keyword, star, startPrice, endPrice, pageable);

                return PageResponse.<ProductView>builder()
                    .data(productsPage.getContent().stream().peek(productView ->
                        productView.getProductImages().forEach(productImage ->
                            productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                        )).toList())
                    .pageNo(productsPage.getNumber())
                    .pageSize(productsPage.getSize())
                    .totalElements(productsPage.getTotalElements())
                    .totalPages(productsPage.getTotalPages())
                    .hasNextPage(productsPage.hasNext())
                    .hasPreviousPage(productsPage.hasPrevious())
                    .build();
            }
        } catch (Exception ignored) {
        }

        if (Boolean.TRUE.equals(isOwner)) {

            ownerId = userHelper.getCurrentUserId();


            Page<ProductView> productsPage = productViewRepositoryImpl.getProductView(null, ownerId, shopId, categoryId, status, shopStatus, keyword, star, startPrice, endPrice, pageable);

            return PageResponse.<ProductView>builder()
                .data(productsPage.getContent().stream().peek(productView ->
                    productView.getProductImages().forEach(productImage ->
                        productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                    )).toList())
                .pageNo(productsPage.getNumber())
                .pageSize(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .hasNextPage(productsPage.hasNext())
                .hasPreviousPage(productsPage.hasPrevious())
                .build();
        } else if (FnCommon.isNotNull(shopId)) {
            status = ProductStatus.ACTIVE;
            shopStatus = ShopStatus.ACTIVE;
            Page<ProductView> productsPage = productViewRepositoryImpl.getProductView(null, null, shopId, categoryId, status, shopStatus, keyword, star, startPrice, endPrice, pageable);

            return PageResponse.<ProductView>builder()
                .data(productsPage.getContent().stream().peek(productView ->
                    productView.getProductImages().forEach(productImage ->
                        productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                    )).toList())
                .pageNo(productsPage.getNumber())
                .pageSize(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .hasNextPage(productsPage.hasNext())
                .hasPreviousPage(productsPage.hasPrevious())
                .build();
        } else {

            status = ProductStatus.ACTIVE;
            shopStatus = ShopStatus.ACTIVE;
            Page<ProductSearch> productSearchPage = productSearchRepositoryImpl.getProductSearch(imageScoreMap, categoryId, status, shopStatus, keyword, star, startPrice, endPrice, pageable);
            if (!FnCommon.isNotNullOrEmptyList(productSearchPage.getContent())) {
                return PageResponse.<ProductView>builder()
                    .data(Collections.emptyList())
                    .pageNo(productSearchPage.getNumber())
                    .pageSize(productSearchPage.getSize())
                    .totalElements(productSearchPage.getTotalElements())
                    .totalPages(productSearchPage.getTotalPages())
                    .hasNextPage(productSearchPage.hasNext())
                    .hasPreviousPage(productSearchPage.hasPrevious())
                    .build();
            }
            List<ProductView> productViews = productViewRepositoryImpl.getProductViewByIdsPreserveOrder(
                productSearchPage.getContent().stream().map(ProductSearch::getId).toList()
            );

            return PageResponse.<ProductView>builder()
                .data(productViews.stream().peek(productView ->
                    productView.getProductImages().forEach(productImage ->
                        productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                    )).toList())
                .pageNo(productSearchPage.getNumber())
                .pageSize(productSearchPage.getSize())
                .totalElements(productSearchPage.getTotalElements())
                .totalPages(productSearchPage.getTotalPages())
                .hasNextPage(productSearchPage.hasNext())
                .hasPreviousPage(productSearchPage.hasPrevious())
                .build();
        }

    }

    @Override
    public ProductView getProductById(Long productId, boolean isOwner) {
        if (isOwner) {
            Long ownerId = userHelper.getCurrentUserId();
            ProductView productView = productViewRepository.findBy_idAndOwnerId(String.valueOf(productId), String.valueOf(ownerId))
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
            productView.getProductImages().forEach(productImage ->
                productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
            );
            return productView;
        }
        try {
            Role currentUserRole = userHelper.getRole();

            if (currentUserRole == Role.ADMIN) {
                ProductView productView = productViewRepository.findById(String.valueOf(productId))
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
                productView.getProductImages().forEach(productImage ->
                    productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                );
                return productView;
            }
        } catch (Exception ignored) {
        }
        ProductView productView = productViewRepository.findBy_idAndProductStatusAndShopStatus(String.valueOf(productId), ProductStatus.ACTIVE, ShopStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        productView.getProductImages().forEach(productImage ->
            productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
        );

        List<FlashSaleProductView> ongoingFlashSales = flashSaleProductViewRepository
            .findOngoingFlashSalesByProductId(String.valueOf(productId), Instant.now());

        if (!ongoingFlashSales.isEmpty()) {
            productView.setFlashSaleProductViews(ongoingFlashSales);
        }

        return productView;
    }

    @Override
    public void updateStockAfterCreateOrder(CreateListOrderEvent createListOrderEvent) {

        createListOrderEvent.getCreateOrderEventList().forEach(createOrderEvent -> {
            if (OrderStatus.CANCELLED.equals(createOrderEvent.getOrderStatus())) {
                return;
            }
            createOrderEvent.getCreateOrderItemEventList().forEach(createOrderItemEvent -> {
                ProductView productView = productViewRepository.findById(String.valueOf(createOrderItemEvent.getProductId()))
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

                String variantId = String.valueOf(createOrderItemEvent.getProductVariantId());

                ProductView.ProductVariant matchedVariant = productView.getProductVariants().stream()
                    .filter(v -> variantId.equals(v.get_id()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND));

                int currentStock = matchedVariant.getStockQuantity() == null ? 0 : matchedVariant.getStockQuantity();
                int quantity = createOrderItemEvent.getQuantity() == null ? 0 : createOrderItemEvent.getQuantity();
                int updatedStock = currentStock - quantity;

                if (updatedStock <= 0) {
                    matchedVariant.setStockQuantity(0);
                    matchedVariant.setProductVariantStatus(ProductVariantStatus.OUT_OF_STOCK);
                } else {
                    matchedVariant.setStockQuantity(updatedStock);
                }

                productViewRepository.save(productView);
            });
        });

    }

    @Override
    public void updateShopStatusInProductViews(UpdateShopStatusEvent updateShopStatusEvent) {
        List<ProductView> productViews = productViewRepository.findByShopId(String.valueOf(updateShopStatusEvent.getShopId()));
        productViews.forEach(productView -> productView.setShopStatus(updateShopStatusEvent.getShopStatus()));
        productViewRepository.saveAll(productViews);
        updateProductSearchShopStatus(updateShopStatusEvent);
    }

    @Async
    public void updateProductSearchShopStatus(UpdateShopStatusEvent updateShopStatusEvent) {
        List<ProductSearch> productSearches = productSearchRepository.findByShopId(String.valueOf(updateShopStatusEvent.getShopId()));
        productSearches.forEach(productSearch -> productSearch.setShopStatus(updateShopStatusEvent.getShopStatus()));
        productSearchRepository.saveAll(productSearches);
    }


    @Override
    public void updateRating(Long productId, RatingNumber rating, Boolean isUpdate, RatingNumber oldRating, Boolean isDelete) {
        productViewRepositoryImpl.updateRating(productId, rating, isUpdate, oldRating, isDelete);
        ProductView productView = productViewRepository.findById(String.valueOf(productId))
            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        shopViewRepositoryImpl.updateRating(productView.getShopId(), rating, isUpdate, oldRating, isDelete);
        ProductSearch productSearch = productSearchRepository.findById(String.valueOf(productId))
            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        double currentRating = productSearch.getRating() == null ? 0.0 : productSearch.getRating();
        int numberOfRatings = productView.getNumberOfRatings() == null ? 0 : productView.getNumberOfRatings();
        double newRating;
        if (Boolean.TRUE.equals(isDelete)) {
            if (numberOfRatings <= 1) {
                newRating = 0.0;
            } else {
                newRating = (currentRating * numberOfRatings - rating.getValue()) / (numberOfRatings - 1);
            }
        } else if (Boolean.TRUE.equals(isUpdate)) {
            newRating = (currentRating * numberOfRatings - oldRating.getValue() + rating.getValue()) / numberOfRatings;
        } else {
            newRating = (currentRating * numberOfRatings + rating.getValue()) / (numberOfRatings + 1);
        }
        productSearch.setRating(newRating);
        productSearchRepository.save(productSearch);
    }

    @Override
    public List<ProductViewStatisticDTO> getProductStatistics(String shopId, Boolean isOwner, Instant nowDate, String type) {
        Long currentUserId = null;
        if (Boolean.TRUE.equals(isOwner)) {
            currentUserId = userHelper.getCurrentUserId();
        }
        return productViewRepositoryImpl.getProductStatistics(shopId, isOwner, currentUserId, nowDate, type);
    }

    @Override
    public void updateProductSoldAndRevenue(String productId, String productVariantId, Integer quantity, BigDecimal revenue) {
        ProductView productView = productViewRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

        // Update Product totalSold and totalRevenue
        if (productView.getTotalSold() == null) {
            productView.setTotalSold(0);
        }
        if (productView.getTotalRevenue() == null) {
            productView.setTotalRevenue(BigDecimal.ZERO);
        }
        productView.setTotalSold(productView.getTotalSold() + quantity);
        productView.setTotalRevenue(productView.getTotalRevenue().add(revenue));

        productView.getProductVariants().stream()
            .filter(variant -> variant.get_id().equals(productVariantId))
            .findFirst()
            .ifPresent(variant -> {
                if (variant.getSold() == null) {
                    variant.setSold(0);
                }
                if (variant.getRevenue() == null) {
                    variant.setRevenue(BigDecimal.ZERO);
                }
                variant.setSold(variant.getSold() + quantity);
                variant.setRevenue(variant.getRevenue().add(revenue));
            });

        productViewRepository.save(productView);

        ProductSearch productSearch = productSearchRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        if (productSearch.getTotalSold() == null) {
            productSearch.setTotalSold(0);
        }
        productSearch.setTotalSold(productSearch.getTotalSold() + quantity);
        productSearchRepository.save(productSearch);
    }

    @Override
    public void restoreProductStock(String productId, String productVariantId, Integer quantity) {
        ProductView productView = productViewRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

        // Find and update the product variant stock
        productView.getProductVariants().stream()
            .filter(variant -> variant.get_id().equals(productVariantId))
            .findFirst()
            .ifPresent(variant -> {
                int currentStock = variant.getStockQuantity() == null ? 0 : variant.getStockQuantity();
                int restoredStock = currentStock + quantity;

                variant.setStockQuantity(restoredStock);

                // Update variant status if stock is restored
                if (restoredStock > 0 && variant.getProductVariantStatus() == ProductVariantStatus.OUT_OF_STOCK) {
                    variant.setProductVariantStatus(ProductVariantStatus.ACTIVE);
                }
            });

        productViewRepository.save(productView);
    }

    @Override
    public String searchProductByImages(MultipartFile file) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        body.add("top_k", "5");
        List<SearchView.SearchImageResult> searchImagesDTOList = RestClient.builder()
            .baseUrl(String.format("http://%s:%s/search-images", aiServiceHost, aiServicePort))
            .build()
            .post()
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(body)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });
        if (!FnCommon.isNotNullOrEmptyList(searchImagesDTOList)) return null;
        SearchView view = SearchView.builder()
            .createdAt(Instant.now())
            .searchImages(searchImagesDTOList)
            .build();
        searchViewRepository.save(view);
        return view.get_id();
    }

    @Override
    public ProductViewHomePageDTO getHomepageProducts(int pageNo, int pageSize, List<String> showProductIds, Long totalElements) {
        Long userId = null;
        try {
            userId = userHelper.getCurrentUserId();
        } catch (Exception ignored) {
        }
        if (!FnCommon.isNotNull(userId)) {
            return getHomepageProductsForGuest(pageNo, pageSize);
        }
        UserCategoryScore userCategoryScore = userCategoryRepositoryImpl.findById(String.valueOf(userId));
        if (!FnCommon.isNotNull(userCategoryScore)) {
            return getHomepageProductsForGuest(pageNo, pageSize);
        }

        List<String> sortedCategoryIds = userCategoryScore.getCategoryScores().entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .map(Map.Entry::getKey)
            .toList();

        String top1 = sortedCategoryIds.get(0);
        String top2 = sortedCategoryIds.size() > 1 ? sortedCategoryIds.get(1) : null;


        int top1Count = (int) Math.round(pageSize * 0.5);
        int top2Count = (int) Math.round(pageSize * 0.3);

        if (!FnCommon.isNotNullOrEmptyList(showProductIds)) {
            showProductIds = new ArrayList<>();
        }
        List<ProductView> listTop1 = productViewRepositoryImpl.getHomepageProducts(showProductIds, top1, Sort.by("totalSold").descending(), top1Count);
        List<ProductView> candidates = new ArrayList<>(listTop1);
        showProductIds.addAll(
            listTop1.stream()
                .map(ProductView::get_id)
                .toList()
        );
        if (FnCommon.isNotNullOrEmpty(top2) && top2Count > 0) {
            List<ProductView> listTop2 = productViewRepositoryImpl.getHomepageProducts(showProductIds, top2, Sort.by("totalSold").descending(), top2Count);
            candidates.addAll(listTop2);
            showProductIds.addAll(
                listTop2.stream()
                    .map(ProductView::get_id)
                    .toList()
            );
        }

        int remaining = pageSize - candidates.size();
        if (remaining > 0) {
            List<ProductView> additionalProducts = productViewRepositoryImpl.getHomepageProducts(
                showProductIds,
                null,
                Sort.by("totalSold").descending(),
                remaining
            );
            candidates.addAll(additionalProducts);
            showProductIds.addAll(
                additionalProducts.stream()
                    .map(ProductView::get_id)
                    .toList()
            );
        }


        if (!FnCommon.isNotNull(totalElements)) {
            totalElements = productViewRepositoryImpl.countProductsForHomepage();
        }
        PageImpl<ProductView> page = new PageImpl<>(
            candidates.stream().peek(productView ->
                productView.getProductImages().forEach(productImage ->
                    productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                )).toList(),
            PageRequest.of(pageNo, pageSize),
            totalElements
        );


        return ProductViewHomePageDTO.builder()
            .pageResponse(PageResponse.<ProductView>builder()
                .data(page.getContent())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build())
            .showProductIds(showProductIds)
            .build();
    }

    private ProductViewHomePageDTO getHomepageProductsForGuest(int pageNo, int pageSize) {
        Sort sort = Sort.by("totalSold").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ProductView> productsPage = productViewRepositoryImpl.getProductView(null, null, null, null, ProductStatus.ACTIVE, ShopStatus.ACTIVE, null, null, null, null, pageable);

        return ProductViewHomePageDTO.builder()
            .pageResponse(PageResponse.<ProductView>builder()
                .data(productsPage.getContent().stream().peek(productView ->
                    productView.getProductImages().forEach(productImage ->
                        productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                    )).toList())
                .pageNo(productsPage.getNumber())
                .pageSize(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .build())
            .build();
    }

}

