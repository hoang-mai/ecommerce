package com.ecommerce.flash.sale.service.impl;

import com.ecommerce.flash.sale.dto.ResPurchaseLimitCheckDTO;
import com.ecommerce.flash.sale.entity.FlashSaleProduct;
import com.ecommerce.flash.sale.entity.UserPurchaseLimit;
import com.ecommerce.flash.sale.repository.FlashSaleProductRepository;
import com.ecommerce.flash.sale.repository.UserPurchaseLimitRepository;
import com.ecommerce.flash.sale.service.UserPurchaseLimitService;
import com.ecommerce.library.component.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPurchaseLimitServiceImpl implements UserPurchaseLimitService {

    private final UserPurchaseLimitRepository userPurchaseLimitRepository;
    private final FlashSaleProductRepository flashSaleProductRepository;
    private final UserHelper userHelper;

    @Override
    public List<ResPurchaseLimitCheckDTO> checkPurchaseLimit(List<Long> productVariantIds) {
        return null;
    }
}

