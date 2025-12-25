package com.ecommerce.product.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.shop.CreateShopCacheEvent;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.ReqCreateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopStatusDTO;
import com.ecommerce.product.entity.Shop;
import com.ecommerce.product.messaging.producer.ShopEventProducer;
import com.ecommerce.product.repository.ShopRepository;
import com.ecommerce.product.service.FileService;
import com.ecommerce.product.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopEventProducer shopEventProducer;
    private final ShopRepository shopRepository;
    private final UserHelper userHelper;
    private final FileService fileService;

    @Transactional
    @Override
    public void createShop(ReqCreateShopDTO reqCreateShopDTO, MultipartFile logoFile, MultipartFile bannerFile) {

        long shopCount = shopRepository.countByOwnerIdAndStatus(userHelper.getCurrentUserId());
        if (shopCount >= 50) {
            throw new IllegalStateException(MessageError.EXCEED_MAX_SHOP_LIMIT);
        }

        Shop shop = Shop.builder()
                .shopName(reqCreateShopDTO.getShopName())
                .description(reqCreateShopDTO.getDescription())
                .ownerId(userHelper.getCurrentUserId())
                .province(reqCreateShopDTO.getProvince())
                .ward(reqCreateShopDTO.getWard())
                .detail(reqCreateShopDTO.getDetail())
                .phoneNumber(reqCreateShopDTO.getPhoneNumber())
                .shopStatus(ShopStatus.ACTIVE)
                .build();
        shopRepository.save(shop);
        String logoUrl;
        if (logoFile != null) {
            logoUrl = fileService.uploadFile(logoFile, "shop/" + shop.getShopId() + "/logo");
            shop.setLogoUrl(logoUrl);

        }
        String bannerUrl;
        if (bannerFile != null) {
            bannerUrl = fileService.uploadFile(bannerFile, "shop/" + shop.getShopId() + "/banner");
            shop.setBannerUrl(bannerUrl);
        }
        shopRepository.save(shop);
        shopEventProducer.send(
                CreateShopEvent.builder()
                        .shopId(shop.getShopId())
                        .ownerId(shop.getOwnerId())
                        .shopName(shop.getShopName())
                        .description(shop.getDescription())
                        .logoUrl(shop.getLogoUrl())
                        .bannerUrl(shop.getBannerUrl())
                        .shopStatus(shop.getShopStatus())
                        .province(shop.getProvince())
                        .ward(shop.getWard())
                        .detail(shop.getDetail())
                        .phoneNumber(shop.getPhoneNumber())
                        .createdAt(shop.getCreatedAt())
                        .updatedAt(shop.getUpdatedAt())
                        .build()
        );
        shopEventProducer.send(
                CreateShopCacheEvent.builder()
                        .shopId(shop.getShopId())
                        .build()
        );
    }

    @Transactional
    @Override
    public void updateShop(Long shopId, ReqUpdateShopDTO reqUpdateShopDTO, MultipartFile logoFile, MultipartFile bannerFile) {

        Long ownerId = userHelper.getCurrentUserId();

        Shop shop = shopRepository.findByShopIdAndOwnerId(shopId, ownerId)
                .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));

        shop.setShopName(reqUpdateShopDTO.getShopName());
        shop.setDescription(reqUpdateShopDTO.getDescription());
        shop.setProvince(reqUpdateShopDTO.getProvince());
        shop.setWard(reqUpdateShopDTO.getWard());
        shop.setDetail(reqUpdateShopDTO.getDetail());
        shop.setPhoneNumber(reqUpdateShopDTO.getPhoneNumber());

        if (logoFile != null) {
            String logoUrl = fileService.uploadFile(logoFile, "shop/" + shop.getShopId() + "/logo");
            shop.setLogoUrl(logoUrl);
        }
        if (bannerFile != null) {
            String bannerUrl = fileService.uploadFile(bannerFile, "shop/" + shop.getShopId() + "/banner");
            shop.setBannerUrl(bannerUrl);
        }
        if (logoFile == null && !FnCommon.isNotNullOrEmpty(reqUpdateShopDTO.getLogoUrl())) {
            shop.setLogoUrl(null);
            fileService.deleteFilesInDirectory("shop/" + shop.getShopId() + "/logo");
        }
        if (bannerFile == null && !FnCommon.isNotNullOrEmpty(reqUpdateShopDTO.getBannerUrl())) {
            shop.setBannerUrl(null);
            fileService.deleteFilesInDirectory("shop/" + shop.getShopId() + "/banner");
        }


        shopRepository.save(shop);
        shopEventProducer.send(
                CreateShopEvent.builder()
                        .shopId(shop.getShopId())
                        .ownerId(shop.getOwnerId())
                        .shopName(shop.getShopName())
                        .description(shop.getDescription())
                        .logoUrl(shop.getLogoUrl())
                        .bannerUrl(shop.getBannerUrl())
                        .shopStatus(shop.getShopStatus())
                        .province(shop.getProvince())
                        .ward(shop.getWard())
                        .detail(shop.getDetail())
                        .phoneNumber(shop.getPhoneNumber())
                        .createdAt(shop.getCreatedAt())
                        .updatedAt(shop.getUpdatedAt())
                        .build()
        );
    }

    @Override
    public void updateShopStatus(Long shopId, ReqUpdateShopStatusDTO reqUpdateShopStatusDTO) {
        Shop shop;
        Role currentUserRole = userHelper.getRole();
        if (currentUserRole == Role.OWNER) {
            Long ownerId = userHelper.getCurrentUserId();
            shop = shopRepository.findByShopIdAndOwnerId(shopId, ownerId)
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));

            if (reqUpdateShopStatusDTO.getShopStatus() == ShopStatus.SUSPENDED) {
                throw new IllegalStateException(MessageError.UNAUTHORIZED_ACTION);
            }
            if (shop.getShopStatus() == ShopStatus.SUSPENDED) {
                throw new IllegalStateException(MessageError.UNAUTHORIZED_ACTION);
            }

            if (reqUpdateShopStatusDTO.getShopStatus() == ShopStatus.ACTIVE &&
                    shop.getShopStatus() == ShopStatus.INACTIVE) {
                long shopCount = shopRepository.countByOwnerIdAndStatus(shop.getOwnerId());
                if (shopCount >= 50) {
                    throw new IllegalStateException(MessageError.EXCEED_MAX_SHOP_LIMIT);
                }
            }
        } else if (currentUserRole == Role.ADMIN) {
            shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
            if (reqUpdateShopStatusDTO.getShopStatus() == ShopStatus.INACTIVE) {
                throw new IllegalStateException(MessageError.UNAUTHORIZED_ACTION);
            }
        } else {
            throw new IllegalStateException(MessageError.UNAUTHORIZED_ACTION);
        }

        shop.setShopStatus(reqUpdateShopStatusDTO.getShopStatus());
        shopRepository.save(shop);
        shopEventProducer.send(
                UpdateShopStatusEvent.builder()
                        .shopId(shop.getShopId())
                        .shopStatus(shop.getShopStatus())
                        .build()
        );
    }


}
