package com.practice.userapi.Controller;

import com.practice.userapi.Entity.User;
import com.practice.userapi.Repository.UserRepository;
import com.practice.userapi.Service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Validated
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;






    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam @NotBlank(message = "Query parameter cannot be blank") String query) {
       return userService.searchUsers(query);
    }

    @GetMapping("/{idOrEmail}")
    public User getUserByIdOrEmail(@PathVariable @NotBlank(message = "ID or email cannot be blank") String idOrEmail) {
       return userService.getUserByIdOrEmail(idOrEmail);
    }
}