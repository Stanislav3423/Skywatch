package org.meteoinit.skywatch.service;

import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.dto.ReportDto;
import org.meteoinit.skywatch.model.Report;
import org.meteoinit.skywatch.model.User;
import org.meteoinit.skywatch.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    public List<ReportDto> getReportDtosByUser(User user) {
        List<Report> reports = reportRepository.getReportsByUser(user);
        return reports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ReportDto convertToDto(Report report) {
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setName(report.getName());
        dto.setLocation(report.getLocation());
        dto.setUsername(report.getUser().getUsername()); // Припускаючи, що User має поле username
        dto.setStartDate(report.getStartDate());
        dto.setEndDate(report.getEndDate());
        dto.setAvgTemp(report.getAvgTemp());
        dto.setAvgHumidity(report.getAvgHumidity());
        dto.setMaxTemp(report.getMaxTemp());
        dto.setMinTemp(report.getMinTemp());
        dto.setAvgCloudiness(report.getAvgCloudiness());
        dto.setAvgPressure(report.getAvgPressure());
        dto.setAvgWindSpeed(report.getAvgWindSpeed());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }

    public ReportDto getReportById(Long id) {
        Report report = reportRepository.getReportById(id);
        return convertToDto(report);
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}
