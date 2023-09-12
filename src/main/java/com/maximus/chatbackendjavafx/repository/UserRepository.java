package com.maximus.chatbackendjavafx.repository;

import com.maximus.chatbackendjavafx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    Optional<User> findByUniqueID(Long uniqueID);

    Optional<List<User>> findFirst10ByLoginContainingIgnoreCase(String login);
    Optional<List<User>> findFirst10ByFirstNameContainingIgnoreCase(String firstName);
    Optional<List<User>> findFirst10ByLastNameContainingIgnoreCase(String lastName);

    Boolean existsByLogin(String login);
    Boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.uniqueID = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);

    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.uniqueID = :userId")
    void updateEmail(@Param("userId") Long userId, @Param("email") String email);



}
