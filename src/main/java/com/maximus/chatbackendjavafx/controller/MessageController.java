package com.maximus.chatbackendjavafx.controller;


import com.maximus.chatbackendjavafx.auth.MessageResponse;
import com.maximus.chatbackendjavafx.service.MessageService;
import com.maximus.chatbackendjavafx.service.exceptions.NoMessagesForRoomException;
import com.maximus.chatdto.MessageInfo;
import com.maximus.chatdto.MessagesReq;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    public MessageController(MessageService messageService, SimpMessagingTemplate template){
        this.messageService = messageService;
        this.template = template;
    }

    @MessageMapping("/{roomId}/sendMessage")
    @SendTo("/{roomId}")
    public MessageInfo handleChatMessage(@DestinationVariable String roomId, @Payload MessageInfo chatMessage,
                                         SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("roomId", roomId);

        //////////////////// че-то там ///////////////////////
//        if (currentRoomId != null) {
//            Message leaveMessage = new Message();
//            leaveMessage.setType(Message.MessageType.LEAVE);
//            leaveMessage.setSender(chatMessage.getSender());
//            messagingTemplate.convertAndSend(format("/chat-room/%s", currentRoomId), leaveMessage);
//        }
//        headerAccessor.getSessionAttributes().put("name", chatMessage.getSender());
        ////////////////////////////////////////////

        // messagingTemplate.convertAndSend(format("/chat-room/%s", roomId), chatMessage);

        return chatMessage;
    }


    @MessageMapping("/getLastMessages")
    @SendTo("/queue")
    public void getMessages(@Payload Long roomId){
        ///TODO передавать позицию
        List<MessageInfo> messages = null;
        StompHeaders header = new StompHeaders();

        try
        {
            header.add("message-type", "MESSAGE_INFO_LIST");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            messages = messageService.getMessages(roomId);
            this.template.convertAndSend("/queue", messages, headerMap);
        }
        catch(NoMessagesForRoomException | SQLException ex){
            ex.printStackTrace();
            header.add("message-type", "MESSAGE_RESPONSE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue",
                    new MessageResponse("Ни одного сообщения не найдено!"), headerMap );
        }

    }

    @MessageMapping("/getMessages")
    @SendTo("/queue")
    public void getMessages(@Payload MessagesReq req) {
        List<MessageInfo> messages = null;
        StompHeaders header = new StompHeaders();

        try
        {
            header.add("message-type", "MESSAGE_INFO_LIST");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            messages = messageService.getMessages(req.getRoomId(), req.getPosition());
            this.template.convertAndSend("/queue", messages, headerMap);
        }
        catch(NoMessagesForRoomException | SQLException ex){
            ex.printStackTrace();
            header.add("message-type", "MESSAGE_RESPONSE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue",
                    new MessageResponse("Ни одного сообщения не найдено!"), headerMap );
        }
    }


    @MessageMapping("/sendMessage")
    @SendTo("/queue")
    public void sendMessage(@Payload MessageInfo newMessage){

        StompHeaders header = new StompHeaders();
        try {
            header.add("message-type", "MESSAGE_INFO_TYPE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            MessageInfo messageInfo = messageService.sendMessage(newMessage);
            this.template.convertAndSend("/queue", messageInfo, headerMap);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    @MessageMapping("/getMessage")
    @SendTo("/queue")
    public void getMessage(@Payload Long messageId){

        StompHeaders header = new StompHeaders();
        try {
            header.add("message-type", "MESSAGE_INFO_TYPE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            MessageInfo messageInfo = messageService.getMessage(messageId);
            this.template.convertAndSend("/queue", messageInfo, headerMap);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }


    @MessageMapping("/changeMessage")
    @SendTo("/queue")
    public void changeMessage(@Payload MessageInfo changedMessage){

        StompHeaders header = new StompHeaders();
        try {
            header.add("message-type", "MESSAGE_INFO_TYPE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            messageService.changeMessage(changedMessage);
            this.template.convertAndSend("/queue", changedMessage, headerMap);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    @MessageMapping("/deleteMessage")
    @SendTo("/queue")
    public void deleteMessage(@Payload Long messageId){

        StompHeaders header = new StompHeaders();
        header.add("message-type", "MESSAGE_RESPONSE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        this.template.convertAndSend("/queue",
                new MessageResponse("Сообщение удалено"), headerMap );


    }



}
