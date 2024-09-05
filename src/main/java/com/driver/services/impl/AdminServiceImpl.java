package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        return adminRepository1.save(admin);
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Optional<Admin> adminOpt = adminRepository1.findById(adminId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            ServiceProvider serviceProvider = new ServiceProvider();
            serviceProvider.setName(providerName);
            serviceProvider.setAdmin(admin);
            serviceProvider.setCountryList(new ArrayList<>());
            serviceProviderRepository1.save(serviceProvider);
            return admin;
        }
        return null;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception {
        Optional<ServiceProvider> spOpt = serviceProviderRepository1.findById(serviceProviderId);
        if (spOpt.isPresent()) {
            ServiceProvider serviceProvider = spOpt.get();
            Country country = new Country();
            country.setCountryName(CountryName.valueOf(countryName.toUpperCase()));
            country.setServiceProvider(serviceProvider);
            countryRepository1.save(country);
            serviceProvider.getCountryList().add(country);
            return serviceProvider;
        } else {
            throw new Exception("Service provider not found");
        }
    }
}
