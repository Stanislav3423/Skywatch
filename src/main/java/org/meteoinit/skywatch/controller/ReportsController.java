package org.meteoinit.skywatch.controller;

import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.dto.ReportDto;
import org.meteoinit.skywatch.dto.ReportRequest;
import org.meteoinit.skywatch.model.Report;
import org.meteoinit.skywatch.model.User;
import org.meteoinit.skywatch.repository.ReportRepository;
import org.meteoinit.skywatch.repository.UserRepository;
import org.meteoinit.skywatch.service.ReportService;
import org.meteoinit.skywatch.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ReportService reportService;
    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestBody ReportRequest dto, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", principal.getName())
        ));
        Report report = new Report();
        report.setName(dto.getName());
        report.setLocation(dto.getLocation());
        report.setUser(user);
        report.setStartDate(dto.getStartDate());
        report.setEndDate(dto.getEndDate());
        report.setAvgTemp(dto.getAvgTemp());
        report.setAvgHumidity(dto.getAvgHumidity());
        report.setAvgPressure(dto.getAvgPressure());
        report.setAvgWindSpeed(dto.getAvgWindSpeed());
        report.setMaxTemp(dto.getMaxTemp());
        report.setMinTemp(dto.getMinTemp());
        report.setAvgCloudiness(dto.getAvgCloudiness());

        reportRepository.save(report);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all-for-user")
    public ResponseEntity<List<ReportDto>> allReportsForUser(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", principal.getName())
        ));
        List<ReportDto> reportDtoList = reportService.getReportDtosByUser(user);
        return  ResponseEntity.ok(reportDtoList);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ReportDto> downloadReport(@PathVariable Long id) {
        ReportDto report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
