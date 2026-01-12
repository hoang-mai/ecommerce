package com.ecommerce.flash.sale.service;

import com.ecommerce.flash.sale.dto.ResCreateOrderDTO;
import jakarta.validation.Valid;

public interface OrderFlashSaleService {
    void createOrder(@Valid ResCreateOrderDTO request);
}
