package za.co.turbo.code_shield.service;

import za.co.turbo.code_shield.model.TaskReport;

import java.time.LocalDate;

public interface TaskReportService {
    TaskReport generateSummary(Long userId, LocalDate from, LocalDate to);

}
