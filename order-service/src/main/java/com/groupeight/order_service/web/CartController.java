package com.groupeight.order_service.web;

import com.groupeight.order_service.application.CartService;
import com.groupeight.order_service.web.dto.CartItemRequestDto;
import com.groupeight.order_service.web.dto.CartItemUpdateRequestDto;
import com.groupeight.order_service.web.dto.CartResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public CartResponseDto addItem(Authentication auth, @Valid @RequestBody CartItemRequestDto req) {
        Long userId = resolveUserId(auth);
        return cartService.addItem(userId, req.getProductId(), req.getQuantity());
    }

    @DeleteMapping("/")
    public void clear(Authentication auth) {
        cartService.clearCart(resolveUserId(auth));
    }

    @GetMapping("/")
    public CartResponseDto getCart(Authentication auth) {
        return cartService.getMyCart(resolveUserId(auth));
    }

    @DeleteMapping("/items/{productId}")
    public void removeItem(Authentication auth, @PathVariable Long productId) {
        cartService.removeItem(resolveUserId(auth), productId);
    }

    @PutMapping("/items/{productId}")
    public CartResponseDto updateItem(
            Authentication auth,
            @PathVariable Long productId,
            @Valid @RequestBody CartItemUpdateRequestDto req
    ) {
        return cartService.updateItem(resolveUserId(auth), productId, req.getQuantity());
    }

    /** Resolve user id from JWT claims or principal name. */
    private Long resolveUserId(Authentication auth) {
        if (auth instanceof JwtAuthenticationToken jwt) {
            Object uid = jwt.getToken().getClaim("uid");
            if (uid == null) uid = jwt.getToken().getClaim("user_id");
            if (uid == null) uid = jwt.getToken().getSubject(); // "sub"
            return parseUserId(uid);
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            return parseUserId(ud.getUsername()); // if username is numeric id
        }
        return parseUserId(auth.getName()); // last resort
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
