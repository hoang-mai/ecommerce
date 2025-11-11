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
        if(logoFile != null) {
            logoUrl = fileService.uploadFile(logoFile, "shop" + shop.getShopId() + "/logo");
            shop.setLogoUrl(logoUrl);

        }
        String bannerUrl;
        if(bannerFile != null) {
            bannerUrl = fileService.uploadFile(bannerFile, "shop" + shop.getShopId() + "/banner");
            shop.setBannerUrl(bannerUrl);
        }
        shopRepository.save(shop);
    }
    @Transactional
    @Override
    public void updateShop(Long shopId, ReqUpdateShopDTO reqUpdateShopDTO, MultipartFile logoFile, MultipartFile bannerFile) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));

        shop.setShopName(reqUpdateShopDTO.getShopName());
        shop.setDescription(reqUpdateShopDTO.getDescription());
        shop.setProvince(reqUpdateShopDTO.getProvince());
        shop.setWard(reqUpdateShopDTO.getWard());
        shop.setDetail(reqUpdateShopDTO.getDetail());
        shop.setPhoneNumber(reqUpdateShopDTO.getPhoneNumber());

        if(logoFile != null) {
            String logoUrl = fileService.uploadFile(logoFile, "shop" + shop.getShopId() + "/logo");
            shop.setLogoUrl(logoUrl);
        }
        if(bannerFile != null) {
            String bannerUrl = fileService.uploadFile(bannerFile, "shop" + shop.getShopId() + "/banner");
            shop.setBannerUrl(bannerUrl);
        }
        if(logoFile == null && !FnCommon.isNotNullOrEmpty(reqUpdateShopDTO.getLogoUrl())) {
            shop.setLogoUrl(null);
            fileService.deleteFilesInDirectory("shop" + shop.getShopId() + "/logo");
        }
        if(bannerFile == null && !FnCommon.isNotNullOrEmpty(reqUpdateShopDTO.getBannerUrl())) {
            shop.setBannerUrl(null);
            fileService.deleteFilesInDirectory("shop" + shop.getShopId() + "/banner");
        }


        shopRepository.save(shop);
    }

    @Override
    public void updateShopStatus(Long shopId, ReqUpdateShopStatusDTO reqUpdateShopStatusDTO) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));

        if (reqUpdateShopStatusDTO.getShopStatus() == ShopStatus.ACTIVE &&
            shop.getShopStatus() == ShopStatus.INACTIVE) {
            long shopCount = shopRepository.countByOwnerIdAndStatus(shop.getOwnerId());
            if (shopCount >= 50) {
                throw new IllegalStateException(MessageError.EXCEED_MAX_SHOP_LIMIT);
            }
        }

        shop.setShopStatus(reqUpdateShopStatusDTO.getShopStatus());
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

    @Override
    public ResShopDTO getShopById(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
        
        return convertToShopDTO(shop);
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
                .logoUrl(fileService.getPresignedUrl(shop.getLogoUrl()))
                .bannerUrl(fileService.getPresignedUrl(shop.getBannerUrl()))
                .rating(shop.getRating())
                .shopStatus(shop.getShopStatus())
                .province(shop.getProvince())
                .ward(shop.getWard())
                .detail(shop.getDetail())
                .phoneNumber(shop.getPhoneNumber())
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }
}
