package com.ecommerce.user.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.user.dto.ReqCreateAddressDTO;
import com.ecommerce.user.dto.ReqUpDateAddressDTO;
import com.ecommerce.user.dto.ResInfoAddressDTO;
import com.ecommerce.user.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constant.ADDRESS)
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final MessageService messageService;

    /**
     * Tạo địa chỉ mới
     *
     * @param reqCreateAddressDTO Thông tin tạo địa chỉ mới
     * @return Trả về thành công
     */
    @PostMapping()
    public ResponseEntity<BaseResponse<Void>> createAddress(@Valid @RequestBody ReqCreateAddressDTO reqCreateAddressDTO) {
        addressService.createAddress(reqCreateAddressDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.ADDRESS_CREATED_SUCCESS))
                .build());
    }

    /**
     * Lấy danh sách địa chỉ của người dùng hiện tại
     *
     * @return Danh sách địa chỉ
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<List<ResInfoAddressDTO>>> getCurrentUserAddresses() {
        List<ResInfoAddressDTO> addresses = addressService.getCurrentUserAddresses();
        return ResponseEntity.ok(BaseResponse.<List<ResInfoAddressDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_USER_ADDRESSES_SUCCESS))
                .data(addresses)
                .build());
    }

    /**
     * Cập nhật địa chỉ theo ID
     *
     * @param addressId ID của địa chỉ
     * @param reqUpdateAddressDTO Thông tin cập nhật địa chỉ
     * @return Trả về thành công
     */
    @PatchMapping("/{addressId}")
    public ResponseEntity<BaseResponse<Void>> updateAddress(@PathVariable Long addressId, @Valid @RequestBody ReqUpDateAddressDTO reqUpdateAddressDTO) {
        addressService.updateAddress(addressId, reqUpdateAddressDTO);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.ADDRESS_UPDATED_SUCCESS))
                .build());
    }

    /**
     * Xoá địa chỉ theo ID
     *
     * @param addressId ID của địa chỉ
     * @return Trả về thành công
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<BaseResponse<Void>> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.ADDRESS_DELETED_SUCCESS))
                .build());
    }

    /**
     * Đặt địa chỉ làm mặc định
     *
     * @param addressId ID của địa chỉ
     * @return Trả về thành công
     */
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<BaseResponse<Void>> setDefaultAddress(@PathVariable Long addressId) {
        addressService.setDefaultAddress(addressId);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.ADDRESS_SET_DEFAULT_SUCCESS))
                .build());
    }

}
