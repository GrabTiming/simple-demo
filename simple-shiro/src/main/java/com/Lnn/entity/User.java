package com.Lnn.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (User)表实体类
 *
 * @author Liang2003
 * @since 2024-08-30 21:25:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User  {
    
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;
    
    private String password;
    
    private String salt;
    
    
}
