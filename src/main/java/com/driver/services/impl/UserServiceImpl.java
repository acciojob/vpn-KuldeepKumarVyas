package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.model.CountryName;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;

    @Autowired
    ServiceProviderRepository serviceProviderRepository3;

    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception {
        CountryName countryEnum = CountryName.valueOf(countryName.toUpperCase());
        Country country = new Country();
        country.setCountryName(countryEnum);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setOriginalCountry(country);
        user.setConnected(false);

        countryRepository3.save(country);
        return userRepository3.save(user);
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        Optional<User> userOpt = userRepository3.findById(userId);
        Optional<ServiceProvider> spOpt = serviceProviderRepository3.findById(serviceProviderId);

        if (userOpt.isPresent() && spOpt.isPresent()) {
            User user = userOpt.get();
            ServiceProvider serviceProvider = spOpt.get();
            user.getServiceProviderList().add(serviceProvider);
            serviceProvider.getUsers().add(user);
            userRepository3.save(user);
            return user;
        }
        return null;
    }
}
