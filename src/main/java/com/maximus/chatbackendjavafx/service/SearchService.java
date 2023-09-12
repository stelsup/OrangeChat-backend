package com.maximus.chatbackendjavafx.service;

import com.maximus.chatbackendjavafx.model.Room;
import com.maximus.chatbackendjavafx.model.User;
import com.maximus.chatbackendjavafx.repository.RoomRepository;
import com.maximus.chatbackendjavafx.repository.UserRepository;
import com.maximus.chatbackendjavafx.service.exceptions.NoSearchResultsException;
import com.maximus.chatdto.SearchTile;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public SearchService(RoomRepository roomRepository, UserRepository userRepository){
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }


    public List<SearchTile> searchByTitle(String title) throws NoSearchResultsException {

        List<SearchTile> results = new ArrayList<>();
        ArrayDeque<User> userByLoginList = null;
        ArrayDeque<User> userByFirstNameList = null;
        ArrayDeque<User> userByLastNameList = null;
        ArrayDeque<Room> roomByNameList = null;

        Optional<List<User>> usersByLogin = userRepository.findFirst10ByLoginContainingIgnoreCase(title);
        Optional<List<User>> usersByFirstName = userRepository.findFirst10ByFirstNameContainingIgnoreCase(title);
        Optional<List<User>> usersByLastName = userRepository.findFirst10ByLastNameContainingIgnoreCase(title);
        Optional<List<Room>> roomsByName = roomRepository.findFirst10ByNameContainingIgnoreCase(title);

        if(usersByLogin.isEmpty() && usersByFirstName.isEmpty() && usersByLastName.isEmpty() && roomsByName.isEmpty()){
            throw new NoSearchResultsException("No search results");
        }

        if(usersByLogin.isPresent())
            userByLoginList = new ArrayDeque<>(usersByLogin.get());
        if(usersByFirstName.isPresent())
            userByFirstNameList = new ArrayDeque<>(usersByFirstName.get());
        if(usersByLastName.isPresent())
            userByLastNameList = new ArrayDeque<>(usersByLastName.get());
        if(roomsByName.isPresent())
            roomByNameList = new ArrayDeque<>(roomsByName.get());


        while(true) {
            boolean bAdded1 = addResult(results, userByLoginList);
            boolean bAdded2 = addResult(results, userByFirstNameList);
            boolean bAdded3 = addResult(results, userByLastNameList);
            boolean bAdded4 = addResult(results, roomByNameList);

            if(!bAdded1 && !bAdded2 && !bAdded3 && !bAdded4 )
                break;
        }

        return results;
    }

    protected <T> boolean addResult(List<SearchTile> dst, ArrayDeque<T> src) {
        if(src != null) {
            if(src.size() > 0 && dst.size() <= 20) {
                Object obj = src.pop();
                if(obj instanceof User) {
                    SearchTile searchUser = new SearchTile();
                    User user = (User)obj;
                    searchUser.setSearchTileFromUserTile(user.getUniqueID(), user.getAvatar(), user.getLogin(), user.getFirstName(), user.getLastName());
                    dst.add(searchUser);
                    return true;
                }
                if(obj instanceof Room) {
                    SearchTile searchUser = new SearchTile();
                    Room room = (Room)obj;
                    searchUser.setSearchTileFromRoomTile(room.getUniqueID(), room.getAvatar(), room.getName());
                    dst.add(searchUser);
                    return true;
                }
            }
        }

        return false;
    }

}
