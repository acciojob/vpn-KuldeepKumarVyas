package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ConnectionServiceImpl implements ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Override
    public User connect(int userId, String countryName) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (user.getConnected()) {
            throw new Exception("Already connected");
        }

        CountryName countryNameEnum;
        try {
            countryNameEnum = CountryName.valueOf(countryName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Country not found");
        }

        boolean countryExists = false;
        ServiceProvider targetServiceProvider = null;

        for (ServiceProvider serviceProvider : user.getServiceProviderList()) {
            for (Country country : serviceProvider.getCountryList()) {
                if (country.getCountryName().equals(countryNameEnum)) {
                    countryExists = true;
                    targetServiceProvider = serviceProvider;
                    break;
                }
            }
            if (countryExists) {
                break;
            }
        }

        if (!countryExists) {
            throw new Exception("Unable to connect, country not supported");
        }

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(targetServiceProvider);

        user.setConnected(true);
        user.setMaskedIp(countryNameEnum.toCode() + "." + targetServiceProvider.getId() + "." + userId);

        if (user.getConnectionList() == null) {
            user.setConnectionList(new ArrayList<>());
        }
        user.getConnectionList().add(connection);

        if (targetServiceProvider.getConnectionList() == null) {
            targetServiceProvider.setConnectionList(new ArrayList<>());
        }
        targetServiceProvider.getConnectionList().add(connection);

        userRepository.save(user);
        serviceProviderRepository.save(targetServiceProvider);
        connectionRepository.save(connection);

        return user;
    }

    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (!user.getConnected()) {
            throw new Exception("User is not connected");
        }

        user.setConnected(false);
        user.setMaskedIp(null);

        if (user.getConnectionList() != null) {
            for (Connection connection : user.getConnectionList()) {
                connection.getServiceProvider().getConnectionList().remove(connection);
                connectionRepository.delete(connection);
            }
        }

        user.getConnectionList().clear();
        return userRepository.save(user);
    }

    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new Exception("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new Exception("Receiver not found"));

        String receiverCountryCode = receiver.getMaskedIp() != null
                ? receiver.getMaskedIp().split("\\.")[0]
                : receiver.getOriginalCountry().getCode();

        if (!receiverCountryCode.equals(sender.getOriginalCountry().getCode())) {
            sender = connect(senderId, receiver.getOriginalCountry().getCountryName().toString());
        }

        return sender;
    }
}
