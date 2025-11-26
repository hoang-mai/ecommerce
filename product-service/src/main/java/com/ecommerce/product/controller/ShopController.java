package com.ecommerce.product.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.ReqCreateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopDTO;
import com.ecommerce.product.dto.ReqUpdateShopStatusDTO;
import com.ecommerce.product.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Constant.SHOP)
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;
    private final MessageService messageService;

    /**
     * Tạo shop mới
     *
     * @param reqCreateShopDTO Dữ liệu tạo shop
     * @return Tạo mới thành công
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<Void>> createShop(
            @RequestParam(value = "bannerUrl", required = false) MultipartFile bannerUrl,
            @RequestParam(value = "logoUrl", required = false) MultipartFile logoUrl,
            @Valid @RequestPart("data") ReqCreateShopDTO reqCreateShopDTO) {
        shopService.createShop(reqCreateShopDTO, logoUrl, bannerUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.CREATE_SHOP_SUCCESS))
                .build());
    }

    /**
     * Cập nhật thông tin shop
     *
     * @param reqUpdateShopDTO Dữ liệu cập nhật shop
     * @return Cập nhật thành công
     */
    @PatchMapping(value = "/{shopId}",consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<Void>> updateShop(
            @PathVariable Long shopId,
            @RequestParam(value = "bannerUrl", required = false) MultipartFile bannerUrl,
            @RequestParam(value = "logoUrl", required = false) MultipartFile logoUrl,
            @Valid @RequestPart("data") ReqUpdateShopDTO reqUpdateShopDTO)
    {
        shopService.updateShop(shopId, reqUpdateShopDTO, logoUrl, bannerUrl);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.UPDATE_SHOP_SUCCESS))
                .build());
    }

    /**
     * Cập nhật trạng thái shop
     *
     * @param shopId ID của shop
     * @param reqUpdateShopStatusDTO Dữ liệu cập nhật trạng thái
     * @return Cập nhật thành công
     */
    @PatchMapping("/{shopId}/status")
    public ResponseEntity<BaseResponse<Void>> updateShopStatus(
            @PathVariable Long shopId,
            @Valid @RequestBody ReqUpdateShopStatusDTO reqUpdateShopStatusDTO)
    {
        shopService.updateShopStatus(shopId, reqUpdateShopStatusDTO);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.UPDATE_SHOP_STATUS_SUCCESS))
                .build());
    }

}
