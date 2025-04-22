package com.practice.userapi.DTO;

import com.practice.userapi.Entity.User;
import lombok.Data;
import java.util.List;

@Data
public class UserResponse {

    private List<User> users;

}
