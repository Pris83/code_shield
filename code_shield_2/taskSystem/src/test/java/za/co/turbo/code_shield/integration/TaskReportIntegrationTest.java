package za.co.turbo.code_shield.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import za.co.turbo.code_shield.model.TaskReport;
import za.co.turbo.code_shield.service.TaskReportService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Sql("classpath:report.sql")
public class TaskReportIntegrationTest {

    @Autowired
    private TaskReportService reportService;

    @Test
    void shouldReturnSummaryForUserWithinDateRange() {
        // Given
        LocalDate from = LocalDate.of(2025, 6, 1);
        LocalDate to = LocalDate.of(2025, 6, 18);
        Long userId = 1L;

        // When
        TaskReport report = reportService.generateSummary(userId, from, to);

        // Then
        assertEquals(15, report.getCompletedCount());
        assertEquals(5, report.getInProgressCount());
        assertEquals(2, report.getTodoCount());
        assertEquals(1, report.getCancelledCount());
    }
}
