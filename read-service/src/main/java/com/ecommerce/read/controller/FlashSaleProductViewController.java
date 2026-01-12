package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.FlashSaleStatisticDTO;
import com.ecommerce.read.entity.FlashSaleProductView;
import com.ecommerce.read.service.FlashSaleProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = Constant.FLASH_SALE_PRODUCT_VIEW)
@RequiredArgsConstructor
@Tag(name = "Flash Sale Product View", description = "APIs for viewing flash sale products")
public class FlashSaleProductViewController {
    private final FlashSaleProductService flashSaleProductService;
    private final MessageService messageService;

    /**
     * Lấy danh sách flash sale products với phân trang và bộ lọc
     *
     * @param flashSaleCampaignId ID của flash sale campaign (optional)
     * @param shopId              ID của shop (optional)
     * @param isOwner             Xác định người dùng hiện tại có phải là chủ sở hữu không (optional)
     * @param pageNo              Số trang (mặc định là 0)
     * @param pageSize            Kích thước trang (mặc định là 10)
     * @param sortBy              Trường sắp xếp (mặc định là score)
     * @param sortDir             Hướng sắp xếp (mặc định là desc)
     * @return Danh sách flash sale products phù hợp
     */
    @GetMapping
    @Operation(summary = "Get flash sale products", description = "Retrieve flash sale products with pagination and filters")
    public ResponseEntity<BaseResponse<PageResponse<FlashSaleProductView>>> getFlashSaleProducts(
        @RequestParam(required = false) String flashSaleCampaignId,
        @RequestParam(required = false) String shopId,
        @RequestParam(required = false) Boolean isOwner,
        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
        @RequestParam(value = "sortBy", defaultValue = "score", required = false) String sortBy,
        @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir
    ) {
        PageResponse<FlashSaleProductView> page = flashSaleProductService.getFlashSaleProducts(
            flashSaleCampaignId,
            shopId,
            isOwner,
            pageNo,
            pageSize,
            sortBy,
            sortDir
        );

        return ResponseEntity.ok(
            BaseResponse.<PageResponse<FlashSaleProductView>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_PRODUCT_SUCCESS))
                .data(page)
                .build()
        );
    }

    /**
     * Lấy danh sách flash sale products hiện tại đang diễn ra
     * Sắp xếp theo score lớn nhất (mặc định)
     *
     * @param pageNo   Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy   Trường sắp xếp (mặc định là score)
     * @param sortDir  Hướng sắp xếp (mặc định là desc)
     * @return Danh sách flash sale products hiện tại
     */
    @GetMapping("/current")
    @Operation(summary = "Get current flash sale products", description = "Retrieve flash sale products that are currently active, sorted by highest score")
    public ResponseEntity<BaseResponse<PageResponse<FlashSaleProductView>>> getCurrentFlashSaleProducts(
        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
        @RequestParam(value = "sortBy", defaultValue = "score", required = false) String sortBy,
        @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir
    ) {
        PageResponse<FlashSaleProductView> page = flashSaleProductService.getCurrentFlashSaleProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDir
        );

        return ResponseEntity.ok(
            BaseResponse.<PageResponse<FlashSaleProductView>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_PRODUCTS_RETRIEVED_SUCCESS))
                .data(page)
                .build()
        );
    }

    @GetMapping("/statistic")
    @Operation(summary = "Get flash sale product statistics", description = "Retrieve statistics of flash sale products")
    public ResponseEntity<BaseResponse<FlashSaleStatisticDTO>> getFlashSaleProductStatistics(
        @RequestParam String flashSaleCampaignId,
        @RequestParam(required = false) Boolean isOwner
    ) {
        FlashSaleStatisticDTO statistics = flashSaleProductService.getFlashSaleProductStatistics(
            flashSaleCampaignId,
            isOwner
        );

        return ResponseEntity.ok(
            BaseResponse.<FlashSaleStatisticDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_PRODUCTS_RETRIEVED_SUCCESS))
                .data(statistics)
                .build()
        );
    }
}
