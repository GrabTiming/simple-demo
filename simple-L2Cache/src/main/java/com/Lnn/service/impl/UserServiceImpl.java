package com.Lnn.service.impl;

import com.Lnn.entity.User;
import com.Lnn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Cacheable(value = "userList")
    @Override
    public List<User> getUsers() {
        User user1 = new User(1,"小明","123");
        User user2 = new User(2,"小红","123");
        User user3 = new User(3,"小刚","123");
        List<User> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        log.info("不走缓存"+list);
        return list;
    }

    @Override
    @Cacheable(value = "userDetail",key = "'users_'+#id")
    public User getUserById(Integer id) {
        User tmpUser = new User(id,"XXX","XXX");
        return tmpUser;
    }
}
