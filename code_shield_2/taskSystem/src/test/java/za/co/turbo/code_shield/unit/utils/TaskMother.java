package za.co.turbo.code_shield.unit.utils;

import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;

public class TaskMother {
    public static Task defaultTask() {
        return new TaskBuilder().build();
    }

    public static Task completedTask() {
        return new TaskBuilder()
                .withStatus(TaskStatus.COMPLETED)
                .build();
    }

    public static Task taskWithNoDueDate() {
        return new TaskBuilder()
                .withDueDate(null)
                .build();
    }

    public static Task taskWithMissingTitle() {
        return new TaskBuilder()
                .withTitle(null)
                .build();
    }

    public static Task taskWithNonExistingAssignee() {
        User ghostUser = new User();
        ghostUser.setId(999L);
        return new TaskBuilder()
                .withAssignee(ghostUser)
                .build();
    }
}
