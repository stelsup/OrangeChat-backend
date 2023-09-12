package com.maximus.chatbackendjavafx.controller;

import com.maximus.chatbackendjavafx.auth.MessageResponse;
import com.maximus.chatbackendjavafx.service.UserDetailsImpl;
import com.maximus.chatbackendjavafx.service.UserService;
import com.maximus.chatbackendjavafx.service.exceptions.LoginExistException;
import com.maximus.chatbackendjavafx.service.exceptions.LoginNotFoundException;
import com.maximus.chatdto.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Controller
public class UserController {

    private final UserService userService;
    private final SimpMessagingTemplate template;


    public UserController(UserService userService, SimpMessagingTemplate template){
        this.userService = userService;
        this.template = template;
    }

    // todo:logger
    //private static final Logger logger = LoggerFactory.getLogger(ChatRoomController.class);
//    @Autowired
//    private SimpMessageSendingOperations messagingTemplate;




    @MessageMapping("/profile")
    @SendTo("/queue")
    public void getProfile(Authentication authentication){
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("---------------getProfile()-------------");
        StompHeaders header = new StompHeaders();

        header.add("message-type", "PROFILE_INFO_TYPE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        this.template.convertAndSend("/queue", userService.getUserProfile(principal.getLogin()), headerMap);


    }



    @MessageMapping("/editProfile")
    @SendTo("/queue")
    public void editProfile(@Payload ProfileInfo profileInfo){
        StompHeaders header = new StompHeaders();

        try {
            userService.editUserProfile(profileInfo);
            header.add("message-type", "PROFILE_INFO_TYPE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue", profileInfo, headerMap);
            //dbg
            System.out.println("editProfile() ended");
            //dbg
        } catch (LoginExistException e) {
            e.printStackTrace();
            header.add("message-type", "MESSAGE_RESPONSE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue",
                    new MessageResponse(e.getMessage()), headerMap);
        }

    }


    @MessageMapping("/changePassword")
    @SendTo("/queue")
    public void changePassword(@Payload ProfilePassword password){
        userService.changePassword(password);

        StompHeaders header = new StompHeaders();

        header.add("message-type", "MESSAGE_RESPONSE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        this.template.convertAndSend("/queue",
                new MessageResponse("Пароль успешно изменен."), headerMap);
    }


    @MessageMapping("/changeEmail")
    @SendTo("/queue")
    public void changeEmail(@Payload ProfileEmail email){
        userService.changeEmail(email);

        StompHeaders header = new StompHeaders();

        header.add("message-type", "MESSAGE_RESPONSE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        this.template.convertAndSend("/queue",
                new MessageResponse("Email успешно изменен."), headerMap);
    }

    @MessageMapping("/changeOnlineStatus")
    @SendTo("/queue") ///// возможно to public!   | Возможно convertAndSend user
    public void changeOnlineStatus(@Payload OnlineStatusInfo status){

        userService.changeOnlineStatus(status);

        StompHeaders header = new StompHeaders();

        header.add("message-type", "ONLINE_STATUS");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        this.template.convertAndSend("/queue", status, headerMap);

    }


    @MessageMapping("/getUserByID")
    @SendTo("/queue")
    public void getUserByID(@Payload Long uniqueID){

        StompHeaders header = new StompHeaders();

        header.add("message-type", "USER_INFO_TYPE");
        Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
        this.template.convertAndSend("/queue",
                userService.getUserById(uniqueID), headerMap);

    }

    @MessageMapping("/getUserByLogin")
    @SendTo("/queue")
    public void getUserByLogin(@Payload String login){

        StompHeaders header = new StompHeaders();

        try {
            header.add("message-type", "USER_INFO_TYPE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue", userService.getUserByLogin(login), headerMap);
            //dbg
            System.out.println("editProfile() ended");
            //dbg
        } catch (LoginNotFoundException e) {
            e.printStackTrace();
            header.add("message-type", "MESSAGE_RESPONSE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue",
                    new MessageResponse(e.getMessage()), headerMap);
        }

    }

}
