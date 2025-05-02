package org.meteoinit.skywatch.repository;

import org.meteoinit.skywatch.model.Condition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
