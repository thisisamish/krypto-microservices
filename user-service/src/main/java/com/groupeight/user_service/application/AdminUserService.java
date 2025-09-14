package com.groupeight.user_service.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.groupeight.user_service.web.dto.AdminCreateRequestDto;
import com.groupeight.user_service.web.dto.UserDetailDto;
import com.groupeight.user_service.web.dto.UserSummaryDto;

public interface AdminUserService {
    Page<UserSummaryDto> list(String role, String q, Pageable pageable);
    UserDetailDto get(Long userId);
    UserDetailDto createAdmin(AdminCreateRequestDto dto);
    void deleteUser(Long userId);
}
