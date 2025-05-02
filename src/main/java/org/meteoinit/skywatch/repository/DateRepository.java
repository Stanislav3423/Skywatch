package org.meteoinit.skywatch.repository;

import org.meteoinit.skywatch.model.DateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DateRepository extends JpaRepository<DateEntity, Long> {
}
