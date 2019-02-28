package com.ifan112.demo.sc;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static User user;

    @Override
    public User createUser(String firstName, String lastName, int age) {
        user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);

        return user;
    }

    @Override
    public User getUser() {
        return user;
    }
}
