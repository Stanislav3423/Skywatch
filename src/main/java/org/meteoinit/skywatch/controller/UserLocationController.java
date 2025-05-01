package org.meteoinit.skywatch.controller;

import org.meteoinit.skywatch.dto.LocationDto;
import org.meteoinit.skywatch.model.Location;
import org.meteoinit.skywatch.model.User;
import org.meteoinit.skywatch.repository.UserRepository;
import org.meteoinit.skywatch.service.UserLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/locations")
public class UserLocationController {

    @Autowired
    private UserLocationService locationService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{username}")
    public ResponseEntity<List<LocationDto>> getUserLocations(@PathVariable String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));
        List<LocationDto> locations = locationService.getUserLocations(user.getId());
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/search")
    public ResponseEntity<List<LocationDto>> searchLocations(@RequestParam("query") String query) {
        List<Location> locations = locationService.searchLocationsByName(query);

        List<LocationDto> locationDtos = locations.stream()
                .map(location -> new LocationDto(
                        location.getId(),
                        location.getName(),
                        location.getLon(),
                        location.getLat(),
                        location.getCountry().getCode(),
                        location.getId_json()
                        ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(locationDtos);
    }

    @PostMapping("/add-to-user")
    public ResponseEntity<?> addLocationToUser(@RequestParam("locationId") Long locationId,
                                               @RequestParam("username") String username) {
        locationService.addLocationToUser(locationId, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-from-user")
    public ResponseEntity<?> deleteLocationFromUser(@RequestParam("locationId") Long locationId,
                                               @RequestParam("username") String username) {
        locationService.deleteLocationFromUser(locationId, username);
        return ResponseEntity.ok().build();
    }
}
