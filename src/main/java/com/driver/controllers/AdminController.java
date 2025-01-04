package com.driver.controllers;

import com.driver.model.Admin;
import com.driver.model.ServiceProvider;
import com.driver.services.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminServiceImpl adminService;

    // Register Admin endpoint
    @PostMapping("/registerAdmin")
    public ResponseEntity<Admin> registerAdmin(@RequestParam String username, @RequestParam String password) {
        try {
            Admin admin = adminService.register(username, password);
            return new ResponseEntity<>(admin, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Add Service Provider endpoint
    @PostMapping("/addServiceProvider")
    public ResponseEntity<Admin> addServiceProvider(@RequestParam int adminId, @RequestParam String providerName) {
        try {
            Admin admin = adminService.addServiceProvider(adminId, providerName);
            return new ResponseEntity<>(admin, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Add Country endpoint
    @PostMapping("/addCountry")
    public ResponseEntity<ServiceProvider> addCountry(@RequestParam int serviceProviderId, @RequestParam String countryName) {
        try {
            ServiceProvider serviceProvider = adminService.addCountry(serviceProviderId, countryName);
            return new ResponseEntity<>(serviceProvider, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
