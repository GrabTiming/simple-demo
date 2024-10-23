package com.Lnn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private Integer id;

    private String username;

    private String password;

}
