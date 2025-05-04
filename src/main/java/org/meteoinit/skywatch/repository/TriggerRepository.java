package org.meteoinit.skywatch.repository;

import org.meteoinit.skywatch.model.Trigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriggerRepository extends JpaRepository<Trigger, Long> {
    List<Trigger> findAllByUserUsername(String username);
}
