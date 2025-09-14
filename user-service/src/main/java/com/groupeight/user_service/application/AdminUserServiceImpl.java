package com.groupeight.user_service.application;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.groupeight.user_service.domain.User;
import com.groupeight.user_service.domain.UserRole;
import com.groupeight.user_service.exception.ResourceNotFoundException;
import com.groupeight.user_service.infrastructure.UserRepository;
import com.groupeight.user_service.web.dto.AdminCreateRequestDto;
import com.groupeight.user_service.web.dto.UserDetailDto;
import com.groupeight.user_service.web.dto.UserSummaryDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public Page<UserSummaryDto> list(String role, String q, Pageable pageable) {
		Page<User> page = userRepository.findAll(pageable);
		return page.map(u -> toSummary(u)).map(d -> d); // passthrough
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetailDto get(Long userId) {
		User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return toDetail(u);
	}

	@Override
	public UserDetailDto createAdmin(@Valid AdminCreateRequestDto dto) {
		if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
			throw new DataIntegrityViolationException("Username already exists");
		}
		User u = new User();
		u.setUsername(dto.getUsername());
		u.setPassword(passwordEncoder.encode(dto.getPassword()));
		u.setEmail(dto.getEmail());
		u.setUserRole(UserRole.ADMIN);
		userRepository.save(u);
		return toDetail(u);
	}

	@Override
	public void deleteUser(Long userId) {
		User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		if (Boolean.TRUE.equals(u.getIsSuperAdmin())) {
			throw new IllegalStateException("Superadmin cannot be deleted");
		}

		userRepository.delete(u);
	}

	private UserSummaryDto toSummary(User u) {
		if (!StringUtils.hasText(u.getUserRole().name())) {
			// Should never happen, but keep DTO defensive
		}
		return UserSummaryDto.builder().id(u.getId()).username(u.getUsername()).role(u.getUserRole())
				.email(u.getEmail()).createdAt(u.getCreatedAt()).build();
	}

	private UserDetailDto toDetail(User u) {
		return UserDetailDto.builder().id(u.getId()).username(u.getUsername()).role(u.getUserRole()).email(u.getEmail())
				.firstName(u.getFirstName()).lastName(u.getLastName()).createdAt(u.getCreatedAt()).build();
	}
}
