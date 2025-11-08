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
import com.ecommerce.product.dto.ResShopDTO;
import com.ecommerce.product.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping()
    public ResponseEntity<BaseResponse<Void>> createShop(@Valid @RequestBody ReqCreateShopDTO reqCreateShopDTO) {
        shopService.createShop(reqCreateShopDTO);
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
    @PatchMapping("/{shopId}")
    public ResponseEntity<BaseResponse<Void>> updateShop(
            @PathVariable Long shopId,
            @Valid @RequestBody ReqUpdateShopDTO reqUpdateShopDTO)
    {
        shopService.updateShop(shopId, reqUpdateShopDTO);
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

    /**
     * Lấy danh sách shop với phân trang, filter và sort
     *
     * @param status Trạng thái của shop (optional)
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là createdAt)
     * @param sortDir Hướng sắp xếp (mặc định là desc)
     * @return Danh sách shop phù hợp
     */
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<ResShopDTO>>> getShops(
            @RequestParam(required = false) ShopStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {

        PageResponse<ResShopDTO> pageResponse = shopService.getShops(
                 status, keyword, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ResShopDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_SHOP_SUCCESS))
                .data(pageResponse)
                .build());
    }

    /**
     * Lấy danh sách shop của chủ nhân hiện tại với phân trang, filter và sort
     *
     * @param status Trạng thái của shop (optional)
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là createdAt)
     * @param sortDir Hướng sắp xếp (mặc định là desc)
     * @return Danh sách shop của chủ nhân hiện tại
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<ResShopDTO>>> getShopsByCurrentOwner(
            @RequestParam(required = false) ShopStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {

        PageResponse<ResShopDTO> pageResponse = shopService.getShopsByCurrentOwner(
                status, keyword, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ResShopDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_SHOP_SUCCESS))
                .data(pageResponse)
                .build());
    }

}
