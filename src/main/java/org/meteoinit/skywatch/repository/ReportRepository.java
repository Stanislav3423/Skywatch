package org.meteoinit.skywatch.repository;

import org.meteoinit.skywatch.dto.ReportDto;
import org.meteoinit.skywatch.model.Report;
import org.meteoinit.skywatch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> getReportsByUser(User user);
    Report getReportById(Long id);
    void deleteById(Long id);
}
