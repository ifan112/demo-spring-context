package com.ifan112.demo.sc;

public interface UserService {

    User createUser(String firstName, String lastName, int age);

    User getUser();
}
