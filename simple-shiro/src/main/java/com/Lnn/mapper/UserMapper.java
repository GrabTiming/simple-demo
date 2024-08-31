package com.Lnn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.Lnn.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * (User)表数据库访问层
 *
 * @author Liang2003
 * @since 2024-08-30 21:25:15
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    
}

