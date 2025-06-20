package za.co.turbo.code_shield.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskReport {
    private Long completedCount;
    private Long inProgressCount;
    private Long todoCount;
    private Long cancelledCount;

}
