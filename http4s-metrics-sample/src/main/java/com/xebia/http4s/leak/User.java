package com.xebia.http4s.leak;

import java.util.HashMap;
import java.util.Map;

public class User {

    private final static Map<User, Integer> map = new HashMap<>();

    public static User generateNewUser(String name) {
        User user = new User(name);
        map.put(user, 1);
        return user;
    }

    String name;

    User(String name) {
        this.name = name;
    }
}