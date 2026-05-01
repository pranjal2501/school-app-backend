package com.example.school_management.parent.repository;

import com.example.school_management.parent.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByEmail(String email);

    Optional<Parent> findByMobilePrimary(String mobilePrimary);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Parent p " +
            "WHERE p.mobilePrimary = :requestedMobile " +
            "AND (p.email = :loggedInUsername OR p.mobilePrimary = :loggedInUsername)")
    boolean isParentAuthorized(@Param("requestedMobile") String requestedMobile, @Param("loggedInUsername") String loggedInUsername);

    @Query("SELECT p.mobilePrimary FROM Parent p WHERE p.email = :username OR p.mobilePrimary = :username")
    Optional<String> findPrimaryMobileByUsername(@Param("username") String username);

    Optional<Parent> findByEmailOrMobilePrimary(String email, String mobilePrimary);
}