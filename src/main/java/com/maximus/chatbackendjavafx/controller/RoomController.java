package com.maximus.chatbackendjavafx.controller;

import com.maximus.chatbackendjavafx.auth.MessageResponse;
import com.maximus.chatbackendjavafx.service.RoomService;
import com.maximus.chatbackendjavafx.service.exceptions.NoRoomsForUserException;
import com.maximus.chatdto.RoomInfo;
import com.maximus.chatdto.RoomTile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class RoomController {

    private final SimpMessagingTemplate template;
    private final RoomService roomService;

    public RoomController(RoomService service, SimpMessagingTemplate template){
        this.roomService = service;
        this.template = template;
    }


    @MessageMapping("/getRooms")
    @SendTo("/queue")
    public void getRooms(@Payload Long userID)
    {
        List<RoomTile> rooms = null;
        StompHeaders header = new StompHeaders();

        try
        {
            header.add("message-type", "ROOM_TILE_LIST");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            rooms = roomService.getRooms(userID);
            this.template.convertAndSend("/queue", rooms, headerMap);
        }
        catch(NoRoomsForUserException e){
            e.printStackTrace();
            header.add("message-type", "MESSAGE_RESPONSE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue",
                   new MessageResponse("Не найдено ни одной беседы!"), headerMap );
        }

    }

    @MessageMapping("/getRoom")
    @SendTo("/queue")
    public void getRoom(@Payload Long uniqueId){

        StompHeaders header = new StompHeaders();

        try
        {
            header.add("message-type", "ROOM_TILE_TYPE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            RoomTile roomTile = roomService.getRoom(uniqueId);
            this.template.convertAndSend("/queue", roomTile, headerMap);
        }
        catch(NoSuchElementException e){
            e.printStackTrace();
            header.add("message-type", "MESSAGE_RESPONSE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue",
                    new MessageResponse("Беседа не найдена!"), headerMap );
        }


    }


    @MessageMapping("/getRoomInfo")
    @SendTo("/queue")
    public void getRoomInfo(@Payload Long uniqueId){

        RoomInfo roomInfo = roomService.getRoomInfo(uniqueId);
        StompHeaders header = new StompHeaders();
        header.add("message-type", "ROOM_INFO_TYPE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        /// Возможно /public!!!
        this.template.convertAndSend("/queue", roomInfo, headerMap );

    }


    @MessageMapping("/createRoom")
    @SendTo("/queue")
    public void createRoom(@Payload RoomInfo newRoom){

        RoomTile resultRoom = roomService.createRoomTile(newRoom);

        StompHeaders header = new StompHeaders();
        header.add("message-type", "ROOM_TILE_TYPE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        /// Возможно /public!!!
        this.template.convertAndSend("/queue", resultRoom, headerMap );

    }

    @MessageMapping("/changeRoom")
    @SendTo("/queue")
    public void changeRoom(@Payload RoomInfo newRoom){

        roomService.changeRoom(newRoom);

        StompHeaders header = new StompHeaders();
        header.add("message-type", "ROOM_INFO_TYPE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        /// Возможно /public!!!
        this.template.convertAndSend("/queue", newRoom, headerMap );
    }

    @MessageMapping("/deleteRoom")
    @SendTo("/queue")
    public void deleteRoom(@Payload Long roomId){

        StompHeaders header = new StompHeaders();
        header.add("message-type", "MESSAGE_RESPONSE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        /// Возможно ипользовать dto сущность notification
        this.template.convertAndSend("/queue",
                new MessageResponse("Беседа была удалена!"), headerMap );

        roomService.deleteRoom(roomId);

    }

}
