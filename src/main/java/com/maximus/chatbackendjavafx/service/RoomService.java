package com.maximus.chatbackendjavafx.service;


import com.maximus.chatbackendjavafx.model.Room;
import com.maximus.chatbackendjavafx.repository.RoomRepository;
import com.maximus.chatbackendjavafx.repository.URRepository;
import com.maximus.chatbackendjavafx.repository.UserRoomRepository;
import com.maximus.chatbackendjavafx.service.exceptions.NoRoomsForUserException;
import com.maximus.chatdto.RoomInfo;
import com.maximus.chatdto.RoomTile;
import com.maximus.chatdto.UserInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final UserService userService;
    private final URRepository urRepository;

    public RoomService(RoomRepository roomRepository, UserRoomRepository userRoomRepository, UserService userService,
                       URRepository urRepository){
        this.roomRepository = roomRepository;
        this.userRoomRepository = userRoomRepository;
        this.userService = userService;
        this.urRepository = urRepository;
    }


    public List<RoomTile> getRooms(Long userId) throws NoRoomsForUserException {

        //------
        List<Long> userRoomNumbers = roomRepository.findUserRoomNumbers(userId);
        if(userRoomNumbers.size() == 0) {
            throw new NoRoomsForUserException("No rooms for userID=" + userId.toString());
        }

        //List<Room> rooms_Desc = roomRepository.findAllById(userRoomNumbers);
        List<Room> rooms_Desc = roomRepository.findAllByUniqueIDInOrderByDateOfModifyDesc(userRoomNumbers);

        List<RoomTile> rooms = new ArrayList<>();

        for(Room room : rooms_Desc){
            RoomTile roomTile = new RoomTile();
            roomTile.setUniqueID(room.getUniqueID());
            roomTile.setName(room.getName());
            roomTile.setAvatar(room.getAvatar());
            roomTile.setLastMessagePreview(room.getLastMessagePreview());

            //roomTile.setUnreadCount(room.get); // wtf!!!!

            //////test///////
//            if(room.getUniqueID() == 22) {
//                userRoomRepository.updateUserStatus(userId, room.getUniqueID(), 2 /*LocalDateTime.now()*/);
//                System.out.println("userPOS = " +   userRoomRepository.getUserPosition(userId, room.getUniqueID()));
//                System.out.println("userLAST_READ = " +   userRoomRepository.getUserLastRead(userId, room.getUniqueID()));
//                System.out.println("userSTATUS = " +   userRoomRepository.getUserStatus(userId, room.getUniqueID()));
//            }
            ///////////

            rooms.add(roomTile);
        }

        return rooms;
    }


    public RoomTile getRoom(Long uniqueId) throws NoSuchElementException{

        Room room = roomRepository.findByUniqueID(uniqueId).get();

        RoomTile roomTile = new RoomTile();
        roomTile.setUniqueID(room.getUniqueID());
        roomTile.setName(room.getName());
        roomTile.setAvatar(room.getAvatar());
        roomTile.setLastMessagePreview(room.getLastMessagePreview());
        //roomTile.setUnreadMessageCount();  Add
        return roomTile;
    }

    public RoomInfo getRoomInfo(Long uniqueId){

        Room room = roomRepository.findByUniqueID(uniqueId).get();
        List<Long> userNumbers = userRoomRepository.findUserNumbersByRoom(uniqueId);
        Set<UserInfo> members = new HashSet<>();

        for(Long userNumber : userNumbers){
            UserInfo member = userService.getUserById(userNumber);
            members.add(member);
        }

        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setUniqueID(room.getUniqueID());
        roomInfo.setName(room.getName());
        roomInfo.setAvatar(room.getAvatar());
        roomInfo.setDateOfModify(room.getDateOfModify());
        roomInfo.setOwnerId(room.getOwnerId());
        roomInfo.setMembers(members);
        return roomInfo;
    }



    public RoomInfo createRoom(RoomInfo newRoom){

        Room room = roomRepository.save(new Room(newRoom.getName(), newRoom.getAvatar(), newRoom.getOwnerId()));

        for(UserInfo member : newRoom.getMembers()){
            userRoomRepository.addRelationship(member.getUniqueID(), room.getUniqueID());
        }

        RoomInfo info = new RoomInfo();
        info.setUniqueID(room.getUniqueID());
        info.setName(room.getName());
        info.setAvatar(room.getAvatar());
        info.setDateOfModify(room.getDateOfModify());
        info.setOwnerId(room.getOwnerId());
        info.setMembers(newRoom.getMembers());
        return info;
    }


    public RoomTile createRoomTile(RoomInfo newRoom){

        Room room = roomRepository.save(new Room(newRoom.getName(), newRoom.getAvatar(), newRoom.getOwnerId()));

        for(UserInfo member : newRoom.getMembers()){
            userRoomRepository.addRelationship(member.getUniqueID(), room.getUniqueID());
           // urRepository.addRelationship(member.getUniqueID(), room.getUniqueID());
        }

        RoomTile tile = new RoomTile();
        tile.setUniqueID(room.getUniqueID());
        tile.setName(room.getName());
        tile.setAvatar(room.getAvatar());
        return tile;
    }


    public void changeRoom(RoomInfo newRoom){

        Room room = roomRepository.findByUniqueID(newRoom.getUniqueID()).get();

        room.setName(newRoom.getName());
        room.setAvatar(newRoom.getAvatar());
        room.setDateOfModify(newRoom.getDateOfModify());
        room.setOwnerId(newRoom.getOwnerId());
        roomRepository.save(room);

        userRoomRepository.removeAllRoomRelationships(room.getUniqueID());

        for(UserInfo member : newRoom.getMembers()){
            userRoomRepository.addRelationship(member.getUniqueID(), room.getUniqueID());
        }

    }

    public void deleteRoom(Long roomId){

        userRoomRepository.removeAllRoomRelationships(roomId);

        Room room = roomRepository.findByUniqueID(roomId).get();
        room.softDeleted();
        roomRepository.save(room);

    }



}
