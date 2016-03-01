package com.theironyard;

import java.util.ArrayList;

/**
 * Created by alexanderhughes on 2/24/16.
 */
public class User {
    String name;
    String password;
    int id;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(int id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }
}
