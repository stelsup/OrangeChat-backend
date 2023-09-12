package com.maximus.chatbackendjavafx;

import com.maximus.chatbackendjavafx.auth.LoginRequest;

public class Utils {

    public static boolean checkCred(LoginRequest user){
        String name = "Max";
        String password = "12345";
        boolean isCorrectName = false;
        boolean isCorrectPass = false;

        if(user.getUserName().equals(name)) isCorrectName = true;
        if(user.getPassword().equals(password)) isCorrectPass = true;

        return isCorrectName && isCorrectPass;
    }

}
