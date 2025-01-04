package com.driver.controllers;

import com.driver.model.User;
import com.driver.services.impl.ConnectionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/connection")
public class ConnectionController {
    @Autowired
    ConnectionServiceImpl connectionService;

    @PostMapping("/connect")
    public ResponseEntity<User> connect(@RequestParam int userId, @RequestParam String countryName) throws Exception {
        // Connect the user to a VPN
        User user = connectionService.connect(userId, countryName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/disconnect")
    public ResponseEntity<User> disconnect(@RequestParam int userId) throws Exception {
        // Disconnect the user from a VPN
        User user = connectionService.disconnect(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/communicate")
    public ResponseEntity<User> communicate(@RequestParam int senderId, @RequestParam int receiverId) throws Exception {
        // Establish communication between users
        User updatedSender = connectionService.communicate(senderId, receiverId);
        return new ResponseEntity<>(updatedSender, HttpStatus.OK);
    }
}
