package za.co.turbo.code_shield.service;

import org.springframework.stereotype.Service;
import za.co.turbo.code_shield.model.TaskReport;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskReportServiceImpl implements TaskReportService{

    private final TaskRepository taskRepository;

    public TaskReportServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskReport generateSummary(Long userId, LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        Map<TaskStatus, Long> statusCounts = countByStatusForUserBetweenDates(userId, fromDateTime, toDateTime);

        return new TaskReport(
                statusCounts.getOrDefault(TaskStatus.COMPLETED, 0L),
                statusCounts.getOrDefault(TaskStatus.IN_PROGRESS, 0L),
                statusCounts.getOrDefault(TaskStatus.TODO, 0L),
                statusCounts.getOrDefault(TaskStatus.CANCELLED, 0L)
        );
    }

    public Map<TaskStatus, Long> countByStatusForUserBetweenDates(Long userId, LocalDateTime from, LocalDateTime to) {
        List<Object[]> results = taskRepository.countByStatusForUserBetweenDates(userId, from, to);

        Map<TaskStatus, Long> statusCounts = new EnumMap<>(TaskStatus.class);
        for (Object[] row : results) {
            TaskStatus status = (TaskStatus) row[0];
            Long count = (Long) row[1];
            statusCounts.put(status, count);
        }
        return statusCounts;
    }
}
