package za.co.turbo.code_shield.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import za.co.turbo.code_shield.controller.TaskController;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.service.TaskService;

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


@WebMvcTest(TaskController.class)
public class TaskControllerTest {

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

        task = new Task();
        task.setId(1L);
        task.setTitle("Sample Task");
        task.setDescription("This is a test task");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        task.setAssignee(user);
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
        List<Task> tasks = List.of(task);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Task"));
    }

    @Test
    void testGetTask() throws Exception{
        Long id = task.getId();
        when(taskService.getTask(id)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Task"));
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

}
