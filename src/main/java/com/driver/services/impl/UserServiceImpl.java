package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Override
    public User register(String username, String password, String countryName) throws Exception {
        CountryName countryNameEnum;
        try {
            countryNameEnum = CountryName.valueOf(countryName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Country not found");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setConnected(false);

        Country country = new Country();
        country.setCountryName(countryNameEnum);
        country.setCode(countryNameEnum.toCode());
        country.setUser(user);

        user.setOriginalCountry(country);
        user.setOriginalIp(country.getCode() + "." + user.getId());

        // Save the country first, if required by your model
        // countryRepository.save(country);

        // Save the user, which should also save the country association if needed
        user = userRepository.save(user);

        // Update the original IP after saving to ensure the ID is set
        user.setOriginalIp(country.getCode() + "." + user.getId());
        user = userRepository.save(user); // Save again to update IP

        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ServiceProvider serviceProvider = serviceProviderRepository.findById(serviceProviderId)
                .orElseThrow(() -> new RuntimeException("ServiceProvider not found"));

        if (user.getServiceProviderList() == null) {
            user.setServiceProviderList(new ArrayList<>());
        }
        if (serviceProvider.getUsers() == null) {
            serviceProvider.setUsers(new ArrayList<>());
        }

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        userRepository.save(user);
        serviceProviderRepository.save(serviceProvider);

        return user;
    }
}
