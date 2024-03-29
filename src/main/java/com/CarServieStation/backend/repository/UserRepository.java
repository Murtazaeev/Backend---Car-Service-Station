package com.CarServieStation.backend.repository;



import com.CarServieStation.backend.entity.Role;
import com.CarServieStation.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(Role role);

    List<User> findByFirstnameOrLastname(String firstname, String lastname);

}
