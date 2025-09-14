package com.groupeight.user_service.infrastructure;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.groupeight.user_service.domain.User;
import com.groupeight.user_service.domain.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsernameIgnoreCase(String username);
	boolean existsByEmailIgnoreCase(String email);

	Optional<User> findByUsername(String username);
	Optional<User> findByUsernameIgnoreCase(String username);
	Optional<User> findByEmailIgnoreCase(String email);

	@Query("""
		SELECT u
		FROM User u
		WHERE (:role IS NULL OR u.userRole = :role)
		  AND (
		       :term IS NULL
		       OR lower(u.username) LIKE lower(concat('%', :term, '%'))
		       OR lower(u.email)    LIKE lower(concat('%', :term, '%'))
		       OR lower(coalesce(u.firstName, '')) LIKE lower(concat('%', :term, '%'))
		       OR lower(coalesce(u.lastName,  '')) LIKE lower(concat('%', :term, '%'))
		  )
	""")
	Page<User> search(@Param("role") UserRole role, @Param("term") String term, Pageable pageable);
}
