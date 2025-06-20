package za.co.turbo.code_shield.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeId(Long userId);

    List<Task> findByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("date") LocalDateTime date);

    Task findTaskById(Long id);

    Task findTaskByTitle(String title);

    @Query("SELECT t.status, COUNT(t) FROM Task t " +
            "WHERE t.assignee.id = :userId " +
            "AND t.dueDate BETWEEN :from AND :to " +
            "GROUP BY t.status")
    List<Object[]> countByStatusForUserBetweenDates(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

}
