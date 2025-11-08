package com.ecommerce.user.service;

import com.ecommerce.user.dto.ReqCreateAddressDTO;
import com.ecommerce.user.dto.ReqUpDateAddressDTO;
import com.ecommerce.user.dto.ResInfoAddressDTO;

import java.util.List;

public interface AddressService {

    /**
     * Tạo địa chỉ mới cho người dùng hiện tại
     *
     * @param reqCreateAddressDTO DTO chứa thông tin địa chỉ cần tạo
     */
    void createAddress(ReqCreateAddressDTO reqCreateAddressDTO);

    /**
     * Lấy danh sách địa chỉ của người dùng hiện tại
     *
     * @return Danh sách địa chỉ của người dùng
     */
    List<ResInfoAddressDTO> getCurrentUserAddresses();

    /**
     * Cập nhật địa chỉ theo ID
     *
     * @param addressId ID của địa chỉ cần cập nhật
     * @param reqUpdateAddressDTO DTO chứa thông tin cập nhật địa chỉ
     */
    void updateAddress(Long addressId,ReqUpDateAddressDTO reqUpdateAddressDTO);

    /**
     * Xoá địa chỉ theo ID
     *
     * @param addressId ID của địa chỉ cần xoá
     */
    void deleteAddress(Long addressId);

    /**
     * Thay đổi trạng thái mặc định của địa chỉ
     *
     * @param addressId ID của địa chỉ cần đặt làm mặc định
     */
    void setDefaultAddress(Long addressId);
}
