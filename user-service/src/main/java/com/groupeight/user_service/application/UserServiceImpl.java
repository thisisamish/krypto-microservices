package com.groupeight.user_service.application;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groupeight.user_service.domain.User;
import com.groupeight.user_service.domain.UserRole;
import com.groupeight.user_service.exception.ResourceNotFoundException;
import com.groupeight.user_service.exception.UserAlreadyExistsException;
import com.groupeight.user_service.infrastructure.UserRepository;
import com.groupeight.user_service.web.UsersController.RoleFilter;
import com.groupeight.user_service.web.dto.AdminCreateRequestDto;
import com.groupeight.user_service.web.dto.UserDetailDto;
import com.groupeight.user_service.web.dto.UserProfileResponseDto;
import com.groupeight.user_service.web.dto.UserProfileUpdateRequestDto;
import com.groupeight.user_service.web.dto.UserRegistrationRequestDto;
import com.groupeight.user_service.web.dto.UserSummaryDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// ---------- Admin endpoints ----------

	@Override
	@Transactional(readOnly = true)
	public Page<UserSummaryDto> list(RoleFilter role, String q, Pageable pageable) {
		UserRole roleFilter = (role == null) ? null : UserRole.valueOf(role.name());
		String term = (q == null || q.isBlank()) ? null : q.trim();

		Page<User> page = userRepository.search(roleFilter, term, pageable);
		return page.map(this::toSummary);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetailDto getByUsername(String username) {
		User u = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
		return toDetail(u);
	}

	@Override
	public UserDetailDto createUser(@Valid AdminCreateRequestDto dto) {
		// Defensive unique checks for clearer errors before DB constraint triggers
		if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
			throw new UserAlreadyExistsException("Username already exists");
		}
		if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
			throw new UserAlreadyExistsException("Email already exists");
		}

		User u = new User();
		u.setUsername(dto.getUsername().trim());
		u.setPassword(passwordEncoder.encode(dto.getPassword()));
		u.setEmail(dto.getEmail().trim());
		u.setIsSuperAdmin(Boolean.FALSE);     // new admins are not superadmins
		u.setUserRole(UserRole.ADMIN);

		try {
			userRepository.save(u);
		} catch (DataIntegrityViolationException e) {
			// In case DB unique constraints race us
			throw new UserAlreadyExistsException("A user with the same username or email already exists.");
		}
		return toDetail(u);
	}

	@Override
	public UserDetailDto updateUser(String username, @Valid UserProfileUpdateRequestDto dto) {
		User u = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

		// Only profile-like fields here (not username/password/role)
		// Ensure email uniqueness if changed
		if (dto.email() != null) {
			Optional<User> byEmail = userRepository.findByEmailIgnoreCase(dto.email());
			if (byEmail.isPresent() && !byEmail.get().getId().equals(u.getId())) {
				throw new UserAlreadyExistsException("Email already exists");
			}
		}

		applyProfileUpdates(u, dto);
		userRepository.save(u);
		return toDetail(u);
	}

	@Override
	public void deleteUserByUsername(String username) {
		User u = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

		if (Boolean.TRUE.equals(u.getIsSuperAdmin())) {
			throw new IllegalStateException("Superadmin cannot be deleted");
		}
		userRepository.delete(u);
	}

	// ---------- Self-service profile ----------

	@Override
	@Transactional(readOnly = true)
	public UserProfileResponseDto getProfile(String username) {
		User user = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return toProfile(user);
	}

	@Override
	public UserProfileResponseDto updateProfile(String username, UserProfileUpdateRequestDto dto) {
		User user = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		// If user changes email, ensure uniqueness
		if (dto.email() != null) {
			Optional<User> byEmail = userRepository.findByEmailIgnoreCase(dto.email());
			if (byEmail.isPresent() && !byEmail.get().getId().equals(user.getId())) {
				throw new UserAlreadyExistsException("Email already exists");
			}
		}

		applyProfileUpdates(user, dto);
		userRepository.save(user);
		return toProfile(user);
	}

	// ---------- Public registration (if used elsewhere) ----------

	@Override
	public void registerUser(@Valid UserRegistrationRequestDto dto) {
		if (userRepository.existsByUsernameIgnoreCase(dto.username())) {
			throw new UserAlreadyExistsException("Username " + dto.username() + " is already taken.");
		}
		if (userRepository.existsByEmailIgnoreCase(dto.email())) {
			throw new UserAlreadyExistsException("Email " + dto.email() + " is already registered.");
		}

		User newUser = new User();
		newUser.setUsername(dto.username().trim());
		newUser.setPassword(passwordEncoder.encode(dto.password()));
		newUser.setFirstName(dto.firstName());
		newUser.setMiddleName(dto.middleName());
		newUser.setLastName(dto.lastName());
		newUser.setAddress(dto.address());
		newUser.setEmail(dto.email().trim());
		newUser.setContactNo(dto.contactNo());
		newUser.setIsSuperAdmin(false);
		newUser.setUserRole(UserRole.CUSTOMER);

		try {
			userRepository.save(newUser);
		} catch (DataIntegrityViolationException e) {
			throw new UserAlreadyExistsException("A user with the same username or email already exists.");
		}
	}

	// ---------- Mappers & helpers ----------

	private void applyProfileUpdates(User user, UserProfileUpdateRequestDto dto) {
		user.setFirstName(dto.firstName());
		user.setMiddleName(dto.middleName());
		user.setLastName(dto.lastName());
		user.setEmail(dto.email());
		user.setAddress(dto.address());
		user.setContactNo(dto.contactNo());
	}

	private UserSummaryDto toSummary(User u) {
		return UserSummaryDto.builder()
				.id(u.getId())
				.username(u.getUsername())
				.role(u.getUserRole())
				.email(u.getEmail())
				.createdAt(u.getCreatedAt())
				.build();
	}

	private UserDetailDto toDetail(User u) {
		return UserDetailDto.builder()
				.id(u.getId())
				.username(u.getUsername())
				.role(u.getUserRole())
				.email(u.getEmail())
				.firstName(u.getFirstName())
				.lastName(u.getLastName())
				.createdAt(u.getCreatedAt())
				.build();
	}

	private UserProfileResponseDto toProfile(User u) {
		return new UserProfileResponseDto(
				u.getUsername(),
				u.getFirstName(),
				u.getMiddleName(),
				u.getLastName(),
				u.getEmail(),
				u.getAddress(),
				u.getContactNo()
		);
	}
}
