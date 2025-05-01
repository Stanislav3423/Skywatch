package org.meteoinit.skywatch.repository;

import org.meteoinit.skywatch.model.Location;
import org.meteoinit.skywatch.model.User;
import org.meteoinit.skywatch.model.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    @Query(value = "SELECT l.* FROM UserLocations ul JOIN Locations l ON l.id = ul.location_id WHERE ul.user_id = :userId",
            nativeQuery = true)
    List<Location> findUserLocationsByUserId(@Param("userId") Long userId);

    Optional<UserLocation> findByUserAndLocation(User user, Location location);
}