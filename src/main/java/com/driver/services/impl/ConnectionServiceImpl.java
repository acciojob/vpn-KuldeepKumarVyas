package com.driver.services.impl;

import com.driver.model.Connection;
import com.driver.model.Country;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConnectionServiceImpl implements ConnectionService {

    @Autowired
    UserRepository userRepository2;

    @Autowired
    ServiceProviderRepository serviceProviderRepository2;

    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception {
        Optional<User> userOpt = userRepository2.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getConnected()) {
                throw new Exception("User already connected to VPN");
            }

            // Logic to find the Service Provider offering the VPN in the specified country
            for (ServiceProvider serviceProvider : user.getServiceProviderList()) {
                for (Country country : serviceProvider.getCountryList()) {
                    if (country.getCountryName().toString().equalsIgnoreCase(countryName)) {
                        Connection connection = new Connection();
                        connection.setUser(user);
                        connection.setServiceProvider(serviceProvider);
                        connectionRepository2.save(connection);

                        user.setConnected(true);
                        userRepository2.save(user);
                        return user;
                    }
                }
            }
            throw new Exception("Service not available in the specified country");
        }
        throw new Exception("User not found");
    }

    @Override
    public User disconnect(int userId) throws Exception {
        Optional<User> userOpt = userRepository2.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.getConnected()) {
                throw new Exception("User is not connected to any VPN");
            }
            user.setConnected(false);
            return userRepository2.save(user);
        }
        throw new Exception("User not found");
    }

    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        Optional<User> senderOpt = userRepository2.findById(senderId);
        Optional<User> receiverOpt = userRepository2.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            // Check if both users are in the same country or connected to the same country via VPN
            if (sender.getOriginalCountry().equals(receiver.getOriginalCountry())) {
                return sender; // Communication allowed
            }
            for (Connection senderConn : sender.getConnectionList()) {
                for (Connection receiverConn : receiver.getConnectionList()) {
                    if (senderConn.getServiceProvider().equals(receiverConn.getServiceProvider())) {
                        return sender; // Communication allowed
                    }
                }
            }
            throw new Exception("Communication not allowed: Different countries");
        }
        throw new Exception("User not found");
    }
}
