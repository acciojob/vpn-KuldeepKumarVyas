package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        return adminRepository.save(admin);
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);

        if (admin.getServiceProviders() == null) {
            admin.setServiceProviders(new ArrayList<>());
        }
        admin.getServiceProviders().add(serviceProvider);
        serviceProviderRepository.save(serviceProvider);
        return adminRepository.save(admin);
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception {
        ServiceProvider serviceProvider = serviceProviderRepository.findById(serviceProviderId)
                .orElseThrow(() -> new RuntimeException("ServiceProvider not found with id: " + serviceProviderId));

        CountryName countryNameEnum;
        try {
            countryNameEnum = CountryName.valueOf(countryName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Country not found");
        }

        Country country = new Country();
        country.setCountryName(countryNameEnum);
        country.setCode(countryNameEnum.toCode());
        country.setServiceProvider(serviceProvider);

        if (serviceProvider.getCountryList() == null) {
            serviceProvider.setCountryList(new ArrayList<>());
        }
        serviceProvider.getCountryList().add(country);
        return serviceProviderRepository.save(serviceProvider);
    }
}
