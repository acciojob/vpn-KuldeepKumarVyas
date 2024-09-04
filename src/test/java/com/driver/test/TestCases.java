package com.driver.test;

import com.driver.model.*;
import com.driver.repository.*;
import com.driver.services.impl.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestCases {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ServiceProviderRepository serviceProviderRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private AdminServiceImpl adminService;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    @Test
    public void testUserRegistration() throws Exception {
        String username = "testUser";
        String password = "testPass";
        String countryName = "IND";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setConnected(false);

        Country country = new Country();
        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.IND.toCode());
        user.setOriginalCountry(country);

        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.register(username, password, countryName);

        assertNotNull(registeredUser);
        assertEquals(username, registeredUser.getUsername());
        assertEquals(password, registeredUser.getPassword());
        assertEquals(countryName, registeredUser.getOriginalCountry().getCountryName().name());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testAddServiceProvider() {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setUsername("admin");
        admin.setPassword("adminPass");

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName("Provider1");

        when(adminRepository.findById(anyInt())).thenReturn(Optional.of(admin));
        when(serviceProviderRepository.save(any(ServiceProvider.class))).thenReturn(serviceProvider);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        Admin updatedAdmin = adminService.addServiceProvider(1, "Provider1");

        assertNotNull(updatedAdmin);
        assertTrue(updatedAdmin.getServiceProviders().stream().anyMatch(sp -> sp.getName().equals("Provider1")));
        verify(serviceProviderRepository, times(1)).save(any(ServiceProvider.class));
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    public void testConnectUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setConnected(false);

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setId(1);
        Country country = new Country();
        country.setCountryName(CountryName.USA);
        serviceProvider.getCountryList().add(country);
        user.getServiceProviderList().add(serviceProvider);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(connectionRepository.save(any(Connection.class))).thenReturn(new Connection());
        when(serviceProviderRepository.save(any(ServiceProvider.class))).thenReturn(serviceProvider);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User connectedUser = connectionService.connect(1, "USA");

        assertNotNull(connectedUser);
        assertTrue(connectedUser.getConnected());
        assertNotNull(connectedUser.getMaskedIp());
        verify(userRepository, times(1)).save(any(User.class));
        verify(serviceProviderRepository, times(1)).save(any(ServiceProvider.class));
        verify(connectionRepository, times(1)).save(any(Connection.class));
    }

    @Test
    public void testDisconnectUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setConnected(true);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User disconnectedUser = connectionService.disconnect(1);

        assertNotNull(disconnectedUser);
        assertFalse(disconnectedUser.getConnected());
        assertNull(disconnectedUser.getMaskedIp());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCommunicate() throws Exception {
        User sender = new User();
        sender.setId(1);
        sender.setConnected(false);

        Country senderCountry = new Country();
        senderCountry.setCountryName(CountryName.IND);
        sender.setOriginalCountry(senderCountry);

        User receiver = new User();
        receiver.setId(2);
        receiver.setConnected(false);

        Country receiverCountry = new Country();
        receiverCountry.setCountryName(CountryName.USA);
        receiver.setOriginalCountry(receiverCountry);

        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(userRepository.save(any(User.class))).thenReturn(sender);

        User updatedSender = connectionService.communicate(1, 2);

        assertNotNull(updatedSender);
        assertTrue(updatedSender.getConnected());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
