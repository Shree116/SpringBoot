package com.practice.userapi;

import com.practice.userapi.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserApiApplication implements CommandLineRunner {


    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(UserApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userService.loadUsersFromExternalApi();
    }

}
