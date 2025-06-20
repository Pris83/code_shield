package za.co.turbo.code_shield.model;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    private LocalDateTime createdAt;

    @ManyToOne
    private User assignee;

    public Task() {
    }

    public Task(User assignee, LocalDateTime createdAt, LocalDateTime dueDate, TaskStatus status, String description, String title) {
        this.assignee = assignee;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
        this.status = status;
        this.description = description;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Title is required") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Title is required") String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull(message = "Status is required") TaskStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status is required") TaskStatus status) {
        this.status = status;
    }

    public @NotNull(message = "Due date is required") LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(@NotNull(message = "Due date is required") LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }
}
