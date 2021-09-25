package com.armjld.busaty.Utill;

import Models.User;

public class UserInFormation {

    public static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        UserInFormation.user = user;
    }

    public static void clearUser() {
        setUser(null);
    }
}
