package com.nutrition.dss.repository;

import com.nutrition.dss.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ============================================================
 *  PERSON 1 - REPOSITORY LAYER
 *  This talks to the database for User records.
 *  Spring auto-generates all the SQL — you just call methods!
 *
 *  HOW TO TINKER:
 *  - Add: List<User> findByRole(String role);
 *    → This auto-generates: SELECT * FROM users WHERE role = ?
 *  - Add: long countByRole(String role);
 *    → Counts users with a given role
 * ============================================================
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring auto-writes: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Spring auto-writes: SELECT * FROM users WHERE email = ? AND password = ?
    Optional<User> findByEmailAndPassword(String email, String password);

    // TINKER: Uncomment to add more queries
    // List<User> findByRole(String role);
    // boolean existsByEmail(String email);
}
