package org.meteoinit.skywatch.repository;

import org.meteoinit.skywatch.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByNameContainingIgnoreCase(String name);
    Optional<Location> findLocationByIdJson(Long idJson);
}
