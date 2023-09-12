package com.maximus.chatbackendjavafx.repository;

import com.maximus.chatbackendjavafx.model.Room;
import com.maximus.chatbackendjavafx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    //
    Optional<Room> findByName(String name);
    Optional<Room> findByUniqueID(Long uniqueID);

    Optional<List<Room>> findFirst10ByNameContainingIgnoreCase(String name);

    List<Room> findAllByUniqueIDInOrderByDateOfModifyDesc(Iterable<Long> roomsIds);

    @Query(value = "SELECT room_id FROM user_rooms WHERE user_id = ?1", nativeQuery = true)
    List<Long> findUserRoomNumbers(Long userID);


}
