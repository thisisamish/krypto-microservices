package com.groupeight.order_service.web;

import com.groupeight.order_service.application.AdminOrderService;
import com.groupeight.order_service.web.dto.OrderDetailDto;
import com.groupeight.order_service.web.dto.OrderSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Orders")
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrdersController {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "List orders (filters + sorting + pagination)")
    @GetMapping
    public Page<OrderSummaryDto> list(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @ParameterObject Pageable pageable
    ) {
        return adminOrderService.list(orderNumber, status, paymentStatus, userId, dateFrom, dateTo, pageable);
    }

    @Operation(summary = "Get order detail by orderNumber")
    @GetMapping("/{orderNumber}")
    public OrderDetailDto get(@PathVariable String orderNumber) {
        return adminOrderService.get(orderNumber);
    }
}
