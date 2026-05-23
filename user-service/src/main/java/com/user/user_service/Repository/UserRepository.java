package com.user.user_service.Repository;

import com.user.user_service.Entities.User;
import com.user.user_service.Enums.RoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"addresses"})
    @Query("SELECT u FROM User u")
    Page<User> findAllWithAddresses(Pageable pageable);

    @EntityGraph(attributePaths = {"addresses"})
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdWithAddresses(@Param("userId") Long userId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByRole(RoleStatus role);
}
