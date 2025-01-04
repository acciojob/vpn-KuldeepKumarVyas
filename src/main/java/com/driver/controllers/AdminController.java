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

    @PostMapping("/register")
    public ResponseEntity<Admin> registerAdmin(@RequestParam String username, @RequestParam String password) {
        // Create an admin and return
        Admin admin = adminService.register(username, password);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @PostMapping("/addProvider")
    public ResponseEntity<Admin> addServiceProvider(@RequestParam int adminId, @RequestParam String providerName) {
        // Add a service provider under the admin and return updated admin
        Admin admin = adminService.addServiceProvider(adminId, providerName);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @PostMapping("/addCountry")
    public ResponseEntity<ServiceProvider> addCountry(@RequestParam int serviceProviderId, @RequestParam String countryName) throws Exception {
        // Add a country under the service provider and return respective service provider
        ServiceProvider serviceProvider = adminService.addCountry(serviceProviderId, countryName);
        return new ResponseEntity<>(serviceProvider, HttpStatus.OK);
    }
}
