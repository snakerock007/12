package ru.itmentor.spring.boot_security.demo.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.repositories.RoleRepository;
import ru.itmentor.spring.boot_security.demo.repositories.UserRepository;
import ru.itmentor.spring.boot_security.demo.role.RoleEnum;
import ru.itmentor.spring.boot_security.demo.user.User;
import ru.itmentor.spring.boot_security.demo.user.UserDto;

import java.util.*;


@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> adminPage() {
        Map<String, Object> model = new HashMap<>();
        List<UserDto> userList = new LinkedList<>();
        userRepository.findAll().forEach(user -> userList.add(new UserDto(user)));
        model.put("users", userList);
        model.put("currentUser", getCurrentUser());
        model.put("user", new UserDto());
        return ResponseEntity.ok(model);
    }

    private UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new UserDto(userRepository.findByName(authentication.getName()));
    }

    @GetMapping(value = {"/index", "/", "/welcome"})
    public String viewMainPage() {
        //service.createTables();
        return "welcome";
    }

    @GetMapping("/addUser")
    public ResponseEntity<Map<String, Object>> addUser() {
        Map<String, Object> model = new HashMap<>();
        UserDto user = new UserDto();
        model.put("user", user);
        model.put("currentUser", getCurrentUser());
        return ResponseEntity.ok(model);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserDto updatedUser) {
        Optional<User> userOptional = userRepository.findById(updatedUser.getId());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (!updatedUser.getPassword().isBlank()) {
                existingUser.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
                existingUser.setRoles(updatedUser.getRoles());
            }
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setAge(updatedUser.getAge());
            userRepository.save(existingUser);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/saveUser")
    public ResponseEntity<Void> saveNewUser(@RequestBody @Valid UserDto userDto, @RequestParam(value = "userRoles", required = false) List<String> userRoles) {
        User presentUser = userRepository.findByName(userDto.getName());
        if (presentUser == null) {
            if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
                userDto.setRoles(List.of(RoleEnum.ROLE_USER));
            }
            presentUser = new User(userDto);
            userRepository.save(presentUser);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Void> deleteUser(@RequestBody UserDto userDto) {
        User user = userRepository.findByName(userDto.getName());
        if (user != null) {
            userRepository.delete(user);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}