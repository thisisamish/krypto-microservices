package com.groupeight.order_service.web;

import com.groupeight.order_service.application.CheckoutService;
import com.groupeight.order_service.web.dto.CheckoutRequestDto;
import com.groupeight.order_service.web.dto.OrderResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/place-order")
    public OrderResponseDto placeOrder(Authentication auth, @Valid @RequestBody CheckoutRequestDto req) {
        return checkoutService.placeOrder(resolveUserId(auth), req);
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
