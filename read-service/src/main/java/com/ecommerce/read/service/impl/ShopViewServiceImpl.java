package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.ShopView;
import com.ecommerce.read.repository.ShopViewRepository;
import com.ecommerce.read.repository.impl.ShopViewRepositoryImpl;
import com.ecommerce.read.service.ProductViewService;
import com.ecommerce.read.service.ShopViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopViewServiceImpl implements ShopViewService {

    private final ShopViewRepository shopViewRepository;
    private final ProductViewService productViewService;
    private final ShopViewRepositoryImpl shopViewRepositoryImpl;
    private final UserHelper userHelper;
    @Override
    public void createShopView(CreateShopEvent createShopEvent) {
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
        Page<ShopView> shopViews=  shopViewRepositoryImpl.getShopsByCurrentOwner(String.valueOf(ownerId),status, keyword, pageable);
        return PageResponse.<ShopView>builder()
                .data(shopViews.getContent())
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
        if(isOwner){
            Long ownerId = userHelper.getCurrentUserId();
            return shopViewRepository.findBy_idAndOwnerId(String.valueOf(shopId), String.valueOf(ownerId))
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
        }
        Role currentUserRole = userHelper.getRole();
        if(currentUserRole != Role.ADMIN ){
            return shopViewRepository.findBy_idAndShopStatus(String.valueOf(shopId), ShopStatus.ACTIVE)
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
        } else {
            return shopViewRepository.findById(String.valueOf(shopId))
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
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

        Role currentUserRole = userHelper.getRole();

        if(currentUserRole != Role.ADMIN ){
            status = ShopStatus.ACTIVE;
        }

        Page<ShopView> shopsPage = shopViewRepositoryImpl.searchShops( status, keyword, pageable);

        return PageResponse.<ShopView>builder()
                .data(shopsPage.getContent())
                .pageNo(shopsPage.getNumber())
                .pageSize(shopsPage.getSize())
                .totalElements(shopsPage.getTotalElements())
                .totalPages(shopsPage.getTotalPages())
                .hasNextPage(shopsPage.hasNext())
                .hasPreviousPage(shopsPage.hasPrevious())
                .build();
    }
}
