package com.ecommerce.product.service;

import com.ecommerce.product.dto.ReqCreateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopStatusDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ShopService {
    void createShop(ReqCreateShopDTO reqCreateShopDTO,
                    MultipartFile bannerUrl,
                    MultipartFile logoUrl);

    void updateShop(Long shopId, ReqUpdateShopDTO reqUpdateShopDTO, MultipartFile logoFile, MultipartFile bannerFile);

    void updateShopStatus(Long shopId, ReqUpdateShopStatusDTO reqUpdateShopStatusDTO);


}
