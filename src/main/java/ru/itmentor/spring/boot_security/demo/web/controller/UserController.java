package ru.itmentor.spring.boot_security.demo.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.repositories.RoleRepository;
import ru.itmentor.spring.boot_security.demo.repositories.UserRepository;
import ru.itmentor.spring.boot_security.demo.user.User;
import ru.itmentor.spring.boot_security.demo.user.UserDto;

@RestController
public class UserController {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/user")
    public UserDto userPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new UserDto(userRepository.findByName(authentication.getName()));
    }
}
