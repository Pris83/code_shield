package za.co.turbo.code_shield.assertions;

import org.assertj.core.api.AbstractAssert;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TaskAssert extends AbstractAssert<TaskAssert, Task> {

    public TaskAssert(Task actual) {
        super(actual, TaskAssert.class);
    }

    public static TaskAssert assertThat(Task actual) {
        return new TaskAssert(actual);
    }

    public TaskAssert hasTitle(String expectedTitle) {
        isNotNull();
        if (!actual.getTitle().equals(expectedTitle)) {
            failWithMessage("Expected title to be <%s> but was <%s>", expectedTitle, actual.getTitle());
        }
        return this;
    }

    public TaskAssert hasDescription(String expectedDescription) {
        isNotNull();
        if (!actual.getDescription().equals(expectedDescription)) {
            failWithMessage("Expected description to be <%s> but was <%s>", expectedDescription, actual.getDescription());
        }
        return this;
    }

    public TaskAssert hasStatus(TaskStatus expectedStatus) {
        isNotNull();
        if (!actual.getStatus().equals(expectedStatus)) {
            failWithMessage("Expected status to be <%s> but was <%s>", expectedStatus, actual.getStatus());
        }
        return this;
    }

    public TaskAssert hasDueDate(LocalDateTime expectedDueDate) {
        isNotNull();
        LocalDateTime actualDueDate = actual.getDueDate().truncatedTo(ChronoUnit.MICROS);
        LocalDateTime expected = expectedDueDate.truncatedTo(ChronoUnit.MICROS);

        if (!actualDueDate.equals(expected)) {
            failWithMessage("Expected dueDate to be <%s> but was <%s>", expected, actualDueDate);
        }
        return this;
    }


    public TaskAssert hasCreatedAt(LocalDateTime expectedCreatedAt) {
        isNotNull();
        LocalDateTime actualCreatedAt = actual.getDueDate().truncatedTo(ChronoUnit.MICROS);
        LocalDateTime expected = expectedCreatedAt.truncatedTo(ChronoUnit.MICROS);

        if (!actualCreatedAt.equals(expected)) {
            failWithMessage("Expected createdAt to be <%s> but was <%s>", expected, actualCreatedAt);
        }
        return this;
    }

    public TaskAssert hasAssignee(User expectedAssignee) {
        isNotNull();
        if (!actual.getAssignee().equals(expectedAssignee)) {
            failWithMessage("Expected assignee to be <%s> but was <%s>", expectedAssignee, actual.getAssignee());
        }
        return this;
    }
}
