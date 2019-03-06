package com.ifan112.demo.sc.service;

import com.ifan112.demo.sc.entity.User;

public interface UserService {

    User createUser(String firstName, String lastName, int age);

    User getUser();
}
