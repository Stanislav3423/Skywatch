package org.meteoinit.skywatch.service;

import org.meteoinit.skywatch.dto.LocationDto;
import org.meteoinit.skywatch.model.Location;
import org.meteoinit.skywatch.model.User;
import org.meteoinit.skywatch.model.UserLocation;
import org.meteoinit.skywatch.repository.LocationRepository;
import org.meteoinit.skywatch.repository.UserLocationRepository;
import org.meteoinit.skywatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserLocationService {
    @Autowired
    private UserLocationRepository userLocationRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;

    public List<LocationDto> getUserLocations(Long userId) {
        return userLocationRepository.findUserLocationsByUserId(userId)
                .stream()
                .map(loc -> new LocationDto(loc.getId(), loc.getName(), loc.getLon(), loc.getLat(), loc.getCountry().getCode(), loc.getId_json()))
                .collect(Collectors.toList());
    }

    public List<Location> searchLocationsByName(String query) {
        return locationRepository.findByNameContainingIgnoreCase(query);
    }

    public void addLocationToUser(Long locationId, String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        UserLocation userLocation = new UserLocation();
        userLocation.setUser(user);
        userLocation.setLocation(location);

        user.getUserLocations().add(userLocation);
        userLocationRepository.save(userLocation);
    }

    public void deleteLocationFromUser(Long locationId, String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        UserLocation userLocation = userLocationRepository.findByUserAndLocation(user, location)
                .orElseThrow(() -> new RuntimeException("UserLocation not found"));

        userLocationRepository.delete(userLocation);
    }
}