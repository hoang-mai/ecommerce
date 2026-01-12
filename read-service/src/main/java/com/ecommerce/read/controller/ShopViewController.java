package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.NewShopViewStatisticDTO;
import com.ecommerce.read.dto.OwnerViewStatisticDTO;
import com.ecommerce.read.dto.ShopViewStatisticDTO;
import com.ecommerce.read.entity.ShopView;
import com.ecommerce.read.service.ShopViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = Constant.SHOP_VIEW)
@RequiredArgsConstructor
public class ShopViewController {

    private final MessageService messageService;
    private final ShopViewService shopViewService;

    /**
     * Lấy danh sách shop của chủ nhân hiện tại với phân trang, filter và sort
     *
     * @param status   Trạng thái của shop (optional)
     * @param keyword  Từ khóa tìm kiếm (optional)
     * @param pageNo   Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy   Trường sắp xếp (mặc định là createdAt)
     * @param sortDir  Hướng sắp xếp (mặc định là desc)
     * @return Danh sách shop của chủ nhân hiện tại
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<ShopView>>> getShopsByCurrentOwner(
            @RequestParam(required = false) ShopStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {

        PageResponse<ShopView> pageResponse = shopViewService.getShopsByCurrentOwner(
                status, keyword, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ShopView>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_SHOP_SUCCESS))
                .data(pageResponse)
                .build());
    }

    /**
     * Lấy danh sách shop với phân trang, filter và sort
     *
     * @param status   Trạng thái của shop (optional)
     * @param keyword  Từ khóa tìm kiếm (optional)
     * @param pageNo   Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy   Trường sắp xếp (mặc định là createdAt)
     * @param sortDir  Hướng sắp xếp (mặc định là desc)
     * @return Danh sách shop phù hợp
     */
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<ShopView>>> getShops(
            @RequestParam(required = false) ShopStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {

        PageResponse<ShopView> pageResponse = shopViewService.getShops(
                status, keyword, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ShopView>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_SHOP_SUCCESS))
                .data(pageResponse)
                .build());
    }

    /**
     * Lấy chi tiết shop theo ID
     *
     * @param shopId ID của shop
     * @return Chi tiết shop
     */
    @GetMapping("/{shopId}")
    public ResponseEntity<BaseResponse<ShopView>> getShopById(
            @PathVariable Long shopId,
            @RequestParam(value = "isOwner", defaultValue = "false", required = false) boolean isOwner) {
        ShopView shopDTO = shopViewService.getShopById(shopId,isOwner);

        return ResponseEntity.ok(BaseResponse.<ShopView>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_SHOP_SUCCESS))
                .data(shopDTO)
                .build());
    }

    /**
     * Lấy tổng doanh thu, đơn hàng, sản phẩm, shop của chủ nhân hiện tại
     *
     * @return Thống kê tổng quan
     */
    @GetMapping("/statistic")
    public ResponseEntity<BaseResponse<OwnerViewStatisticDTO>> getOverviewStatistics() {

        OwnerViewStatisticDTO stats = shopViewService.getOverviewStatistics();

        return ResponseEntity.ok(BaseResponse.<OwnerViewStatisticDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_STATISTIC_SUCCESS))
                .data(stats)
                .build());
    }

    /**
     * Thống kê top 5 shop theo doanh thu cao hoặc bán chạy trong tháng hiện tại
     *
     * @param nowDate Thời điểm hiện tại để xác định tháng (optional, mặc định là thời điểm hiện tại)
     * @param type    Loại thống kê: "sold" (bán chạy - mặc định) hoặc "revenue" (doanh thu cao)
     * @return Danh sách top 5 shop
     */
    @GetMapping("/statistic/top-revenue")
    public ResponseEntity<BaseResponse<List<ShopViewStatisticDTO>>> getTopShopsByRevenue(
        @RequestParam(required = false) Boolean isOwner,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant nowDate,
            @RequestParam(required = false, defaultValue = "revenue") String type) {

        List<ShopViewStatisticDTO> stats = shopViewService.getTopShopsByRevenue(isOwner,nowDate, type);

        return ResponseEntity.ok(BaseResponse.<List<ShopViewStatisticDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_STATISTIC_SUCCESS))
                .data(stats)
                .build());
    }

    @GetMapping("/statistic/date-range")
    public ResponseEntity<BaseResponse<List<NewShopViewStatisticDTO>>> getStatisticsByDateRange(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        List<NewShopViewStatisticDTO> stats = shopViewService.getStatisticsByDateRange(fromDate, toDate);
        return ResponseEntity.ok(BaseResponse.<List<NewShopViewStatisticDTO>>builder()
            .statusCode(HttpStatus.OK.value())
            .message(messageService.getMessage(MessageSuccess.GET_STATISTIC_SUCCESS))
            .data(stats)
            .build());
    }
}
