package com.example.sign_lang_ml.data;

import java.util.ArrayList;

public class profile_info {
    private static profile_info dataObject = null;


    user_info my_user_info = new user_info();


    public user_info getMy_user_info() {
        return my_user_info;
    }

    public void setMy_user_info(user_info my_user_info) {
        this.my_user_info = my_user_info;
    }



    public static profile_info getInstance() {
        if (dataObject == null)
            dataObject = new profile_info();
        return dataObject;
    }
}

