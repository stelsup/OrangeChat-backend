package com.maximus.chatbackendjavafx.repository;

import com.maximus.chatbackendjavafx.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByUniqueID(Long uniqueID);
    List<Message> findAllByRoomIdOrderByTimestampAsc(Long roomId);
    List<Message> findTop100ByRoomIdOrderByTimestampAsc(Long roomId);

    /// TODO запрос возвращающий 50 записей до и 50 записей после определенного элемента


    //"( select * from ( select * from messages_test where room_id = 1 AND timestamp <= '2023.04.23 23:55:00' order by uniqueid_t desc limit 50 ) as subq ) UNION ( select * from messages_test where room_id = 1 AND timestamp >= '2023.04.23 23:55:00' limit 50 ) order by uniqueid_t asc;"

    @Transactional
    @Query(value = "( select * from ( select * from messages where room_id = ?1 AND timestamp <= ?2 order by uniqueid desc limit 50 ) as subq ) UNION ( select * from messages where room_id = ?1 AND timestamp >= ?2 limit 50 ) order by uniqueid asc", nativeQuery = true)
    List<Message> findMessagesByRoomByPosition(Long roomID, LocalDateTime userPosition);


}
