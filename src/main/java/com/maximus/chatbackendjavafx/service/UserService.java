package com.maximus.chatbackendjavafx.service;

import com.maximus.chatbackendjavafx.model.OnlineStatus;
import com.maximus.chatbackendjavafx.model.User;
import com.maximus.chatbackendjavafx.repository.UserRepository;
import com.maximus.chatbackendjavafx.service.exceptions.LoginExistException;
import com.maximus.chatbackendjavafx.service.exceptions.LoginNotFoundException;
import com.maximus.chatdto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder =passwordEncoder;
    }


    @Transactional
    public ProfileInfo getUserProfile(String login){

        User user = userRepository.findByLogin(login).get();

        ProfileInfo profile = new ProfileInfo();
        profile.setUniqueID(user.getUniqueID());
        profile.setLogin(user.getLogin());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setAvatar(user.getAvatar());
        profile.setDateOfBirth(user.getDateOfBirth());
        profile.setEmail(user.getEmail());
        OnlineStatusInfo status = new OnlineStatusInfo(user.getOnlineStatus().getStatus(), user.getOnlineStatus().getLastTimeOnline());
        profile.setOnlineStatus(status);
        return profile;
    }

    @Transactional
    public void editUserProfile(ProfileInfo profileInfo) throws LoginExistException {

        User user = userRepository.findByUniqueID(profileInfo.getUniqueID()).get();

        if(!user.getLogin().equals(profileInfo.getLogin())){
            if(userRepository.existsByLogin(profileInfo.getLogin())){
                throw new LoginExistException("Ошибка : Пользователь с таким login уже существует!");
            }
            user.setLogin(profileInfo.getLogin());
        }

        user.setFirstName(profileInfo.getFirstName());
        user.setLastName(profileInfo.getLastName());
        user.setAvatar(profileInfo.getAvatar());
        user.setDateOfBirth(profileInfo.getDateOfBirth());

        userRepository.save(user);

    }

    @Transactional
    public void changePassword(ProfilePassword password){

        String encodedPassword = passwordEncoder.encode(password.getPassword());

        userRepository.updatePassword(password.getUserId(), encodedPassword);

    }

    @Transactional
    public void changeEmail(ProfileEmail email){

        userRepository.updateEmail(email.getUserId(), email.getEmail());
    }

    @Transactional
    public void changeOnlineStatus(OnlineStatusInfo status){
        Long userId = 0L; ////TODO Сделать сущьность или передавать ID через сессию
        //// Проблема N+1 запроса! Возможно стоит использовать UPDATE

        User user = userRepository.findByUniqueID(userId).get();
        user.setOnlineStatus(new OnlineStatus(status.getStatus(), status.getLastTimeOnline()));

        userRepository.save(user);
    }

    @Transactional
    public UserInfo getUserById(Long userId){
        User user = userRepository.findByUniqueID(userId).get();

        UserInfo userInfo = new UserInfo();
        userInfo.setUniqueID(user.getUniqueID());
        userInfo.setLogin(user.getLogin());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setAvatar(user.getAvatar());
        OnlineStatusInfo status = new OnlineStatusInfo(user.getOnlineStatus().getStatus(), user.getOnlineStatus().getLastTimeOnline());
        userInfo.setOnlineStatus(status);
        return userInfo;

    }

    @Transactional
    public UserInfo getUserByLogin(String login) throws LoginNotFoundException {
        Optional<User> userOptional = userRepository.findByLogin(login);

        if(userOptional.isEmpty()){
            throw new LoginNotFoundException("Ошибка : Пользователя с таким login не существует!");
        }
        User user = userOptional.get();

        UserInfo userInfo = new UserInfo();
        userInfo.setUniqueID(user.getUniqueID());
        userInfo.setLogin(user.getLogin());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setAvatar(user.getAvatar());
        OnlineStatusInfo status = new OnlineStatusInfo(user.getOnlineStatus().getStatus(), user.getOnlineStatus().getLastTimeOnline());
        userInfo.setOnlineStatus(status);
        return userInfo;

    }


}
