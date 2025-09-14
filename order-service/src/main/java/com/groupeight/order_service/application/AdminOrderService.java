package com.groupeight.order_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.groupeight.order_service.web.dto.OrderDetailDto;
import com.groupeight.order_service.web.dto.OrderSummaryDto;

public interface AdminOrderService {
    Page<OrderSummaryDto> list(String orderNumber, String status, String paymentStatus, String userId,
                               String dateFrom, String dateTo, Pageable pageable);
    OrderDetailDto get(String orderNumber);
}
