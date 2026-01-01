package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.OwnerViewStatisticDTO;
import com.ecommerce.read.dto.ShopViewStatisticDTO;
import com.ecommerce.read.entity.ShopView;
import com.ecommerce.read.repository.ShopViewRepository;
import com.ecommerce.read.repository.impl.ShopViewRepositoryImpl;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.ProductViewService;
import com.ecommerce.read.service.ShopViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopViewServiceImpl implements ShopViewService {

    private final ShopViewRepository shopViewRepository;
    private final ProductViewService productViewService;
    private final ShopViewRepositoryImpl shopViewRepositoryImpl;
    private final FileService fileService;
    private final UserHelper userHelper;

    @Override
    public void createShopView(CreateShopEvent createShopEvent) {
        ShopView existing = shopViewRepository.findById(String.valueOf(createShopEvent.getShopId())).orElse(null);
        if (FnCommon.isNotNull(existing)) {
            existing.setOwnerId(String.valueOf(createShopEvent.getOwnerId()));
            existing.setShopName(createShopEvent.getShopName());
            existing.setShopStatus(createShopEvent.getShopStatus());
            existing.setDescription(createShopEvent.getDescription());
            existing.setLogoUrl(createShopEvent.getLogoUrl());
            existing.setBannerUrl(createShopEvent.getBannerUrl());
            existing.setProvince(createShopEvent.getProvince());
            existing.setWard(createShopEvent.getWard());
            existing.setDetail(createShopEvent.getDetail());
            existing.setPhoneNumber(createShopEvent.getPhoneNumber());
            existing.setUpdatedAt(createShopEvent.getUpdatedAt());

            shopViewRepository.save(existing);
            return;
        }

        // Create new ShopView if not exists
        shopViewRepository.save(
                ShopView.builder()
                        ._id(String.valueOf(createShopEvent.getShopId()))
                        .ownerId(String.valueOf(createShopEvent.getOwnerId()))
                        .shopName(createShopEvent.getShopName())
                        .shopStatus(createShopEvent.getShopStatus())
                        .description(createShopEvent.getDescription())
                        .logoUrl(createShopEvent.getLogoUrl())
                        .bannerUrl(createShopEvent.getBannerUrl())
                        .province(createShopEvent.getProvince())
                        .ward(createShopEvent.getWard())
                        .detail(createShopEvent.getDetail())
                        .phoneNumber(createShopEvent.getPhoneNumber())
                        .createdAt(createShopEvent.getCreatedAt())
                        .updatedAt(createShopEvent.getUpdatedAt())
                        .build()
        );
    }

    @Override
    public void updateShopStatusView(UpdateShopStatusEvent updateShopStatusEvent) {
        ShopView shopView = shopViewRepository.findById(String.valueOf(updateShopStatusEvent.getShopId()))
                .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
        shopView.setShopStatus(updateShopStatusEvent.getShopStatus());
        shopViewRepository.save(shopView);
        productViewService.updateShopStatusInProductViews(updateShopStatusEvent);
    }

    @Override
    public PageResponse<ShopView> getShopsByCurrentOwner(ShopStatus status, String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Long ownerId = userHelper.getCurrentUserId();
        Page<ShopView> shopViews = shopViewRepositoryImpl.getShopsByCurrentOwner(String.valueOf(ownerId), status, keyword, pageable);
        return PageResponse.<ShopView>builder()
                .data(shopViews.getContent().stream().peek(shop -> {
                    shop.setLogoUrl(fileService.getPresignedUrl(shop.getLogoUrl()));
                    shop.setBannerUrl(fileService.getPresignedUrl(shop.getBannerUrl()));
                }).toList()
                )
                .pageNo(shopViews.getNumber())
                .pageSize(shopViews.getSize())
                .totalElements(shopViews.getTotalElements())
                .totalPages(shopViews.getTotalPages())
                .hasNextPage(shopViews.hasNext())
                .hasPreviousPage(shopViews.hasPrevious())
                .build();
    }

    @Override
    public ShopView getShopById(Long shopId, boolean isOwner) {

        try {
            if (isOwner) {
                Long ownerId = userHelper.getCurrentUserId();
                ShopView shopView= shopViewRepository.findBy_idAndOwnerId(String.valueOf(shopId), String.valueOf(ownerId))
                        .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
                shopView.setLogoUrl(fileService.getPresignedUrl(shopView.getLogoUrl()));
                shopView.setBannerUrl(fileService.getPresignedUrl(shopView.getBannerUrl()));
                return shopView;
            }
            Role currentUserRole = userHelper.getRole();
            if (currentUserRole != Role.ADMIN) {
                ShopView shopView= shopViewRepository.findBy_idAndShopStatus(String.valueOf(shopId), ShopStatus.ACTIVE)
                        .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
                shopView.setLogoUrl(fileService.getPresignedUrl(shopView.getLogoUrl()));
                shopView.setBannerUrl(fileService.getPresignedUrl(shopView.getBannerUrl()));
                return shopView;
            } else {
                ShopView shopView= shopViewRepository.findById(String.valueOf(shopId))
                        .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
                shopView.setLogoUrl(fileService.getPresignedUrl(shopView.getLogoUrl()));
                shopView.setBannerUrl(fileService.getPresignedUrl(shopView.getBannerUrl()));
                return shopView;
            }
        } catch (Exception e) {
            ShopView shopView= shopViewRepository.findBy_idAndShopStatus(String.valueOf(shopId), ShopStatus.ACTIVE)
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
            shopView.setLogoUrl(fileService.getPresignedUrl(shopView.getLogoUrl()));
            shopView.setBannerUrl(fileService.getPresignedUrl(shopView.getBannerUrl()));
            return shopView;
        }

    }

    @Override
    public PageResponse<ShopView> getShops(ShopStatus status,
                                           String keyword, int pageNo, int pageSize,
                                           String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        try {
            Role currentUserRole = userHelper.getRole();

            if (currentUserRole != Role.ADMIN) {
                status = ShopStatus.ACTIVE;
            }
        } catch (Exception e) {
            status = ShopStatus.ACTIVE;
        }


        Page<ShopView> shopsPage = shopViewRepositoryImpl.searchShops(status, keyword, pageable);

        return PageResponse.<ShopView>builder()
                .data(shopsPage.getContent().stream().peek(shop -> {
                                    shop.setLogoUrl(fileService.getPresignedUrl(shop.getLogoUrl()));
                                    shop.setBannerUrl(fileService.getPresignedUrl(shop.getBannerUrl()));
                                }).toList()
                )
                .pageNo(shopsPage.getNumber())
                .pageSize(shopsPage.getSize())
                .totalElements(shopsPage.getTotalElements())
                .totalPages(shopsPage.getTotalPages())
                .hasNextPage(shopsPage.hasNext())
                .hasPreviousPage(shopsPage.hasPrevious())
                .build();

    }

    @Override
    public OwnerViewStatisticDTO getOverviewStatistics() {
        Long ownerId = userHelper.getCurrentUserId();
        return shopViewRepositoryImpl.getOverviewStatistics(String.valueOf(ownerId));
    }

    @Override
    public List<ShopViewStatisticDTO> getTopShopsByRevenue(LocalDateTime nowDate, String type) {
        Long ownerId = userHelper.getCurrentUserId();
        return shopViewRepositoryImpl.getTopShopsByRevenue(String.valueOf(ownerId),nowDate, type);
    }
}
