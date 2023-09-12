package com.maximus.chatbackendjavafx.controller;

import com.maximus.chatbackendjavafx.auth.MessageResponse;
import com.maximus.chatbackendjavafx.service.SearchService;
import com.maximus.chatbackendjavafx.service.exceptions.NoSearchResultsException;
import com.maximus.chatdto.SearchTile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    private final SimpMessagingTemplate template;
    private final SearchService searchService;

    public SearchController(SimpMessagingTemplate simpMessagingTemplate, SearchService searchService){
        this.template = simpMessagingTemplate;
        this.searchService = searchService;
    }


    @MessageMapping("/generalSearch")
    @SendTo("/queue")
    public void generalSearch(@Payload String title)
    {
        List<SearchTile> searchTiles = null;
        StompHeaders header = new StompHeaders();

        try
        {
            header.add("message-type", "SEARCH_TILE_LIST");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            searchTiles = searchService.searchByTitle(title);
            this.template.convertAndSend("/queue", searchTiles, headerMap);
        }
        catch(NoSearchResultsException e){
            e.printStackTrace();
            header.add("message-type", "MESSAGE_RESPONSE");
            Map<String,Object> headerMap = new HashMap<>(header.toSingleValueMap());
            this.template.convertAndSend("/queue",
                    new MessageResponse("Ничего не найдено!"), headerMap );
        }

    }





}
