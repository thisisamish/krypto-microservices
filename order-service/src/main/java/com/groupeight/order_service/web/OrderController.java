package com.groupeight.order_service.web;

import com.groupeight.order_service.application.OrderService;
import com.groupeight.order_service.web.dto.OrderResponseDto;
import com.groupeight.order_service.web.dto.OrderSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderNumber}")
    public OrderResponseDto get(Authentication auth, @PathVariable String orderNumber) {
        return orderService.getMyOrder(orderNumber, resolveUserId(auth));
    }

    @GetMapping
    public Page<OrderSummaryDto> list(Authentication auth, @ParameterObject Pageable pageable) {
        return orderService.listMyOrders(resolveUserId(auth), pageable);
    }

    private Long resolveUserId(Authentication auth) {
        if (auth instanceof JwtAuthenticationToken jwt) {
            Object uid = jwt.getToken().getClaim("uid");
            if (uid == null) uid = jwt.getToken().getClaim("user_id");
            if (uid == null) uid = jwt.getToken().getSubject();
            return parseUserId(uid);
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            return parseUserId(ud.getUsername());
        }
        return parseUserId(auth.getName());
    }

    private Long parseUserId(Object raw) {
        if (raw == null) throw new IllegalStateException("User id not present in token");
        try {
            return Long.parseLong(raw.toString());
        } catch (NumberFormatException nfe) {
            throw new IllegalStateException("Invalid user id claim: " + raw);
        }
    }
}
