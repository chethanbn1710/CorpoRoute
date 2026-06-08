package com.corporoute.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.corporoute.entity.Company;
import com.corporoute.entity.User;
import com.corporoute.exception.InvalidRideStateException;
import com.corporoute.repository.CompanyRepository;
import com.corporoute.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.corporoute.enums.Role;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                   CompanyRepository companyRepository,
                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {

        if (user.getRole() == Role.EMPLOYEE) {
            if (user.getCompany() == null) {
                throw new RuntimeException("Employee must belong to a company");
        }

            Company company = companyRepository.findById(user.getCompany().getId())
            .orElseThrow(() -> new RuntimeException("Company not found"));

            user.setCompany(company);
        }

        if (user.getRole() == Role.DRIVER || user.getRole() == Role.ADMIN) {
            user.setCompany(null);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User updateUser(Long id, User userDetails) {

        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            user.setRole(userDetails.getRole());

            return userRepository.save(user);
        }

        return null;
    }

    public User goOnline(String email) {

        User driver = getUserByEmail(email);
        if (driver.getRole() != Role.DRIVER) {
            throw new InvalidRideStateException("Only drivers can go online");
        }
        driver.setAvailable(true);
        return userRepository.save(driver);
    }

    public User goOffline(String email) {
        User driver = getUserByEmail(email);
        if (driver.getRole() != Role.DRIVER) {
            throw new InvalidRideStateException("Only drivers can go offline");
        }
        driver.setAvailable(false);
        return userRepository.save(driver);
    }

    public User updateLocation(String email, String location) {
        User driver = getUserByEmail(email);
        if (driver.getRole() != Role.DRIVER) {
            throw new InvalidRideStateException("Only drivers can update location");
        }

        driver.setCurrentLocation(location);
        return userRepository.save(driver);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }
}