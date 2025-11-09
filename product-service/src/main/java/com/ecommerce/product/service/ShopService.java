package com.ecommerce.product.service;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.ReqCreateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopStatusDTO;
import com.ecommerce.product.dto.ResShopDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ShopService {
    void createShop(ReqCreateShopDTO reqCreateShopDTO,
                    MultipartFile bannerUrl,
                    MultipartFile logoUrl);

    void updateShop(Long shopId, ReqUpdateShopDTO reqUpdateShopDTO, MultipartFile logoFile, MultipartFile bannerFile);

    void updateShopStatus(Long shopId, ReqUpdateShopStatusDTO reqUpdateShopStatusDTO);

    PageResponse<ResShopDTO> getShops(ShopStatus status,
                                      String keyword, int pageNo, int pageSize,
                                      String sortBy, String sortDir);

    PageResponse<ResShopDTO> getShopsByCurrentOwner(ShopStatus status,
                                                     String keyword, int pageNo, int pageSize,
                                                     String sortBy, String sortDir);

    ResShopDTO getShopById(Long shopId);
}
