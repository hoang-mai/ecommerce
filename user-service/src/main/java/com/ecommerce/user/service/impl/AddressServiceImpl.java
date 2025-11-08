package com.ecommerce.user.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.user.dto.ReqCreateAddressDTO;
import com.ecommerce.user.dto.ReqUpDateAddressDTO;
import com.ecommerce.user.dto.ResInfoAddressDTO;
import com.ecommerce.user.entity.Address;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.AddressRepository;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final UserHelper userHelper;

    @Override
    @Transactional
    public void createAddress(ReqCreateAddressDTO reqCreateAddressDTO) {
        User user = userRepository.findById(userHelper.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));

        Address address = Address.builder()
                .user(user)
                .province(reqCreateAddressDTO.getProvince())
                .ward(reqCreateAddressDTO.getWard())
                .detail(reqCreateAddressDTO.getDetail())
                .receiverName(reqCreateAddressDTO.getReceiverName())
                .phoneNumber(reqCreateAddressDTO.getPhoneNumber())
                .build();
        if (FnCommon.isNotNull(reqCreateAddressDTO.getIsDefault()) && reqCreateAddressDTO.getIsDefault()) {
            address.setIsDefault(true);
            Address defaultAddress = addressRepository.findByUserUserIdAndIsDefault(user.getUserId(), true);
            if (FnCommon.isNotNull(defaultAddress)) {
                defaultAddress.setIsDefault(false);
                addressRepository.save(defaultAddress);
            }
        }
        addressRepository.save(address);
    }

    @Override
    public List<ResInfoAddressDTO> getCurrentUserAddresses() {
        List<Address> addresses = addressRepository.findByUserUserId(userHelper.getCurrentUserId());
        return addresses.stream().map(address -> ResInfoAddressDTO.builder()
                .addressId(address.getAddressId())
                .province(address.getProvince())
                .ward(address.getWard())
                .detail(address.getDetail())
                .receiverName(address.getReceiverName())
                .phoneNumber(address.getPhoneNumber())
                .isDefault(address.getIsDefault())
                .build()).toList();
    }

    @Override
    @Transactional
    public void updateAddress(Long addressId, ReqUpDateAddressDTO reqUpdateAddressDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(MessageError.ADDRESS_NOT_FOUND));

        address.setProvince(reqUpdateAddressDTO.getProvince());
        address.setWard(reqUpdateAddressDTO.getWard());
        address.setDetail(reqUpdateAddressDTO.getDetail());
        address.setReceiverName(reqUpdateAddressDTO.getReceiverName());
        address.setPhoneNumber(reqUpdateAddressDTO.getPhoneNumber());
        addressRepository.save(address);

    }

    @Override
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(MessageError.ADDRESS_NOT_FOUND));
        if (address.getIsDefault()) {
            throw new IllegalStateException(MessageError.CANNOT_DELETE_DEFAULT_ADDRESS);
        }
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(MessageError.ADDRESS_NOT_FOUND));

        if (!address.getUser().getUserId().equals(userHelper.getCurrentUserId())) {
            throw new NotFoundException(MessageError.ADDRESS_NOT_FOUND);
        }

        if (address.getIsDefault()) {
            return;
        }

        // Tìm và bỏ trạng thái mặc định của địa chỉ hiện tại
        Address currentDefaultAddress = addressRepository.findByUserUserIdAndIsDefault(userHelper.getCurrentUserId(), true);
        if (FnCommon.isNotNull(currentDefaultAddress)) {
            currentDefaultAddress.setIsDefault(false);
            addressRepository.save(currentDefaultAddress);
        }

        address.setIsDefault(true);
        addressRepository.save(address);
    }
}
