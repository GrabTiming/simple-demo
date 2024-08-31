package com.Lnn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.Lnn.entity.User;

/**
 * (User)表服务接口
 *
 * @author Liang2003
 * @since 2024-08-30 21:25:18
 */
public interface UserService extends IService<User> {

    User getUserByUsername(String username);

}

