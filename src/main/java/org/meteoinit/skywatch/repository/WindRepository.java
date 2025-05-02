package org.meteoinit.skywatch.repository;

import org.meteoinit.skywatch.model.Wind;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WindRepository extends JpaRepository<Wind, Long> {
}
