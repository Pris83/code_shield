package za.co.turbo.code_shield.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import za.co.turbo.code_shield.BaseTest;
import za.co.turbo.code_shield.unit.utils.TaskBuilder;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.service.TaskService;
import za.co.turbo.code_shield.unit.utils.TaskMother;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
public class TaskControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@email.com");

        task = new TaskBuilder()
                .withTitle("Sample Task")
                .withDescription("This is a test task")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDueDate(LocalDateTime.now().plusDays(1))
                .withCreatedAt(LocalDateTime.now())
                .withAssignee(user)
                .build();

        task.setId(1L);
    }

    @Test
    void testCreateTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Task"));
    }

    @Test
    void testCreateTask_MissingTitle_ShouldReturnBadRequest() throws Exception {
        Task invalidTask = TaskMother.taskWithMissingTitle();

        when(taskService.createTask(any(Task.class))).thenThrow(new IllegalArgumentException("Title is required"));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTask_MissingDueDate_ShouldReturnBadRequest() throws Exception {
        Task invalidTask = TaskMother.taskWithNoDueDate();

        when(taskService.createTask(any(Task.class))).thenThrow(new IllegalArgumentException("Due date is required"));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTask_WithNonExistingAssignee_ShouldReturnBadRequest() throws Exception {
        Task invalidTask = TaskMother.taskWithNonExistingAssignee();

        when(taskService.createTask(any(Task.class))).thenThrow(new IllegalArgumentException("Assignee not found"));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTask() throws Exception {
        task.setTitle("Updated Task");
        task.setStatus(TaskStatus.CANCELLED);
        Long id = task.getId();

        when(taskService.updateTask(eq(id), any(Task.class))).thenReturn(task);

        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    void testDeleteTask() throws Exception{
        Long id = task.getId();
        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllTasks_Success() throws Exception {
        // Arrange: create a list with a single task
        List<Task> tasks = List.of(task);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // Act & Assert: perform GET request and verify response
        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1][0].title").exists());
    }


    @Test
    void testGetTask() throws Exception{
        Long id = task.getId();
        when(taskService.getTask(id)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Task"));
    }

    @Test
    void testGetTask_NonExistingId_ShouldReturnNotFound() throws Exception {
        Long nonExistentId = 999L;
        when(taskService.getTask(nonExistentId)).thenThrow(new EntityNotFoundException("Task not found"));

        mockMvc.perform(get("/api/tasks/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

}
