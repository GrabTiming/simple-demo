package com.Lnn.service;

import com.Lnn.entity.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User getUserById(Integer id);

}
