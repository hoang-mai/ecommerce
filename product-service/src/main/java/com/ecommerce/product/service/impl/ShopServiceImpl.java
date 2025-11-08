package com.ecommerce.product.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.ReqCreateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopStatusDTO;
import com.ecommerce.product.dto.ResShopDTO;
import com.ecommerce.product.entity.Shop;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ShopRepository;
import com.ecommerce.product.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final UserHelper userHelper;

    @Override
    public void createShop(ReqCreateShopDTO reqCreateShopDTO) {

        long shopCount = shopRepository.countByOwnerIdAndStatus(userHelper.getCurrentUserId());
        if (shopCount >= 50) {
            throw new IllegalStateException(MessageError.EXCEED_MAX_SHOP_LIMIT);
        }
        Shop shop = Shop.builder()
                .shopName(reqCreateShopDTO.getShopName())
                .description(reqCreateShopDTO.getDescription())
                .ownerId(userHelper.getCurrentUserId())
                .bannerUrl(reqCreateShopDTO.getBannerUrl())
                .logoUrl(reqCreateShopDTO.getLogoUrl())
                .province(reqCreateShopDTO.getProvince())
                .ward(reqCreateShopDTO.getWard())
                .detail(reqCreateShopDTO.getDetail())
                .phoneNumber(reqCreateShopDTO.getPhoneNumber())
                .shopStatus(ShopStatus.ACTIVE)
                .build();
        shopRepository.save(shop);
    }

    @Override
    public void updateShop(Long shopId, ReqUpdateShopDTO reqUpdateShopDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));

        if (FnCommon.isNotNull(reqUpdateShopDTO.getCategoryIds())) {
            long count = categoryRepository.countByCategoryIdIn(reqUpdateShopDTO.getCategoryIds());
            if (count != reqUpdateShopDTO.getCategoryIds().size()) {
                throw new NotFoundException(MessageError.CATEGORY_NOT_FOUND);
            }
        }

        shop.setCategoryIds(reqUpdateShopDTO.getCategoryIds());
        shop.setShopName(reqUpdateShopDTO.getShopName());
        shop.setDescription(reqUpdateShopDTO.getDescription());
        shop.setLogoUrl(reqUpdateShopDTO.getLogoUrl());
        shop.setBannerUrl(reqUpdateShopDTO.getBannerUrl());
        shop.setProvince(reqUpdateShopDTO.getProvince());
        shop.setWard(reqUpdateShopDTO.getWard());
        shop.setDetail(reqUpdateShopDTO.getDetail());
        shop.setPhoneNumber(reqUpdateShopDTO.getPhoneNumber());


        shopRepository.save(shop);
    }

    @Override
    public void updateShopStatus(Long shopId, ReqUpdateShopStatusDTO reqUpdateShopStatusDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));

        if (reqUpdateShopStatusDTO.getStatus() == ShopStatus.ACTIVE &&
            shop.getShopStatus() == ShopStatus.INACTIVE) {
            long shopCount = shopRepository.countByOwnerIdAndStatus(shop.getOwnerId());
            if (shopCount >= 50) {
                throw new IllegalStateException(MessageError.EXCEED_MAX_SHOP_LIMIT);
            }
        }

        shop.setShopStatus(reqUpdateShopStatusDTO.getStatus());
        shopRepository.save(shop);
    }

    @Override
    public PageResponse<ResShopDTO> getShops(ShopStatus status,
                                             String keyword, int pageNo, int pageSize,
                                             String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Shop> shopsPage = shopRepository.searchShops( status, keyword, pageable);

        return buildPageResponse(shopsPage);
    }

    @Override
    public PageResponse<ResShopDTO> getShopsByCurrentOwner(ShopStatus status,
                                                            String keyword, int pageNo, int pageSize,
                                                            String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Long currentOwnerId = userHelper.getCurrentUserId();
        Page<Shop> shopsPage = shopRepository.searchShopsByOwner(currentOwnerId, status, keyword, pageable);

        return buildPageResponse(shopsPage);
    }

    private PageResponse<ResShopDTO> buildPageResponse(Page<Shop> shopsPage) {
        List<ResShopDTO> shopResponses = shopsPage.getContent().stream()
                .map(this::convertToShopDTO)
                .collect(Collectors.toList());

        return PageResponse.<ResShopDTO>builder()
                .pageNo(shopsPage.getNumber())
                .pageSize(shopsPage.getSize())
                .totalElements(shopsPage.getTotalElements())
                .totalPages(shopsPage.getTotalPages())
                .hasNextPage(shopsPage.hasNext())
                .hasPreviousPage(shopsPage.hasPrevious())
                .data(shopResponses)
                .build();
    }

    private ResShopDTO convertToShopDTO(Shop shop) {
        return ResShopDTO.builder()
                .shopId(shop.getShopId())
                .ownerId(shop.getOwnerId())
                .shopName(shop.getShopName())
                .description(shop.getDescription())
                .logoUrl(shop.getLogoUrl())
                .bannerUrl(shop.getBannerUrl())
                .rating(shop.getRating())
                .shopStatus(shop.getShopStatus())
                .province(shop.getProvince())
                .ward(shop.getWard())
                .detail(shop.getDetail())
                .phoneNumber(shop.getPhoneNumber())
                .categoryIds(shop.getCategoryIds())
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }
}
