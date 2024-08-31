package com.Lnn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Lnn.mapper.UserMapper;
import com.Lnn.entity.User;
import com.Lnn.service.UserService;
import org.springframework.stereotype.Service;

/**
 * (User)表服务实现类
 *
 * @author Liang2003
 * @since 2024-08-30 21:25:19
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getUserByUsername(String username) {

        return lambdaQuery()
                .eq(User::getUsername,username)
                .one();
    }
}

