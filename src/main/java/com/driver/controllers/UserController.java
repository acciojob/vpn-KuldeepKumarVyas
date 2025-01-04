package com.driver.controllers;

import com.driver.model.User;
import com.driver.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    // Endpoint: registerUser
    @PostMapping("/registerUser")
    public ResponseEntity<User> registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String countryName) throws Exception {
        // Create a user of the given country
        User user = userService.register(username, password, countryName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Endpoint: subscribe
    @PutMapping("/subscribe")
    public ResponseEntity<User> subscribe(@RequestParam Integer userId, @RequestParam Integer serviceProviderId) {
        // Subscribe the user to a service provider
        User user = userService.subscribe(userId, serviceProviderId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
