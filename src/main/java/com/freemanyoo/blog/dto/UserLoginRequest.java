package com.freemanyoo.blog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    private String emailOrUsername;
    private String password;
}
