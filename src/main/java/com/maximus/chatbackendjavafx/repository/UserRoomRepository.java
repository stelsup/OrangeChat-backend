package com.maximus.chatbackendjavafx.repository;


import com.maximus.chatbackendjavafx.model.Room;
import com.maximus.chatbackendjavafx.model.UserChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "SELECT room_id FROM user_rooms WHERE user_id = ?1", nativeQuery = true)
    List<Long> findRoomNumbersByUser(Long userID);

    @Query(value = "SELECT user_id FROM user_rooms WHERE room_id = ?1", nativeQuery = true)
    List<Long> findUserNumbersByRoom(Long roomID);

    @Query(value = "SELECT user_position FROM user_rooms WHERE user_id = ?1 AND room_id = ?2", nativeQuery = true)
    LocalDateTime getUserPosition(Long userID, Long roomID);

    @Query(value = "SELECT user_last_read FROM user_rooms WHERE user_id = ?1 AND room_id = ?2", nativeQuery = true)
    LocalDateTime getUserLastRead(Long userID, Long roomID);

    @Query(value = "SELECT user_status FROM user_rooms WHERE user_id = ?1 AND room_id = ?2", nativeQuery = true)
    Integer getUserStatus(Long userID, Long roomID);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_rooms (user_id, room_id, user_position, user_status) VALUES (:userID, :roomID, NOW(), 1, NOW())", nativeQuery = true)
    void addRelationship(@Param("userID") Long userID, @Param("roomID") Long roomID);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_rooms WHERE user_id = ?1 AND room_id = ?2", nativeQuery = true)
    void removeRelationship(Long userID, Long roomID);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_rooms WHERE room_id = ?1", nativeQuery = true)
    void removeAllRoomRelationships(Long roomID);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_rooms SET user_position = :userPosition WHERE user_id = :userID AND room_id = :roomID", nativeQuery = true)
    void updateUserPosition(@Param("userID") Long userID, @Param("roomID") Long roomID, @Param("userPosition") LocalDateTime userPosition);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_rooms SET user_last_read = :userLastRead WHERE user_id = :userID AND room_id = :roomID", nativeQuery = true)
    void updateUserLastRead(@Param("userID") Long userID, @Param("roomID") Long roomID, @Param("userLastRead") LocalDateTime userLastRead);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_rooms SET user_status = :userStatus WHERE user_id = :userID AND room_id = :roomID", nativeQuery = true)
    void updateUserStatus(@Param("userID") Long userID, @Param("roomID") Long roomID, @Param("userStatus") Integer userStatus);
}
