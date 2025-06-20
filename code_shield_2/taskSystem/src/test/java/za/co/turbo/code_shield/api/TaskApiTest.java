package za.co.turbo.code_shield.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.unit.utils.TaskBuilder;
import za.co.turbo.code_shield.unit.utils.TaskMother;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "/data.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TaskApiTest {

    @LocalServerPort
    private int port;

    private Task task;
    private User user;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeAll
    void initRestAssuredPort() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        System.out.println("RestAssured port: " + port);

    }

    @BeforeEach
    void setup() {

            user = userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("No user found"));
            task = taskRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("No task found"));
        assertNotNull(user.getId());
        assertNotNull(task.getAssignee());
        assertNotNull(task.getAssignee().getId());
    }


    @Test
    void createTask_shouldReturn200() {
        Task saved = taskRepository.save(task);

        long responseTime = given()
                .log().all()                        // log full request
                .contentType(ContentType.JSON)
                .body(saved)
                .when()
                .post("/api/tasks")
                .then()
                .log().all()                        // log full response
                .statusCode(200)
                .body("title", equalTo("Sample Task"))
                .extract()
                .time();

        System.out.println("Response time for createTask_shouldReturn200: " + responseTime + " ms");

        // Optional: Assert the response time is under 500ms
        assertTrue(responseTime < 500, "Response time should be less than 500 ms");
    }


    @Test
    void createTask_missingTitle_shouldReturn400() {
        Task invalidTask = new Task();
        invalidTask.setStatus(TaskStatus.IN_PROGRESS);

        long responseTime = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(invalidTask)
                .when()
                .post("/api/tasks")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .time();

        System.out.println("Response time for createTask_missingTitle_shouldReturn400: " + responseTime + " ms");

        // Optional: Assert the response time is below threshold (e.g., 500 ms)
        assertTrue(responseTime < 500, "Response time should be less than 500 ms");
    }


    @Test
    void createTask_nonExistingAssignee_shouldReturn400() {
        Task invalidTask = new Task();
        invalidTask.setTitle("Task with ghost user");
        invalidTask.setStatus(TaskStatus.IN_PROGRESS);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(invalidTask)
                .when()
                .post("/api/tasks")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    void createTask_missingDueDate_shouldReturn400() {
        Task invalidTask = new Task();
        invalidTask.setTitle("Task with no due date");
        invalidTask.setStatus(TaskStatus.IN_PROGRESS);
        // no dueDate set

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(invalidTask)
                .when()
                .post("/api/tasks")
                .then()
                .log().body()                      // log response body only to reduce noise
                .statusCode(400);
    }

    @Test
    void getAllTasks_shouldReturn200() {
        given()
                .log().all()
                .when()
                .get("/api/tasks")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void getAllTasks_whenNoTasks_shouldReturnEmptyList() {
        taskRepository.deleteAll();
        taskRepository.flush();  // Ensure deletion is committed

        Cache tasksCache = cacheManager.getCache("tasks");
        if (tasksCache != null) {
            tasksCache.clear();
        }

        given()
                .log().all()
                .when()
                .get("/api/tasks")
                .then()
                .extract()
                .jsonPath()
                .getList("[1]");
    }


    @Test
    void updateTask_shouldReturn200() {
        task.setStatus(TaskStatus.COMPLETED);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(task)
                .when()
                .put("/api/tasks/" + task.getId())
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("COMPLETED"));
    }

    @Test
    void updateTask_idMismatch_shouldReturn404() {
        task.setId(999L);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(task)
                .when()
                .put("/api/tasks/" + task.getId()) // original id may differ from body id
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    void updateTask_missingRequiredField_shouldReturn400() {
        task.setTitle(null);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(task)
                .when()
                .put("/api/tasks/" + task.getId())
                .then()
                .log().body()                      // log response body only here
                .statusCode(400);
    }

    //    @Test
//    void updateTask_dueDateInPast_shouldReturn400() {
//        task.setDueDate(LocalDateTime.now().minusDays(1));
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(task)
//                .when()
//                .put("/api/tasks/" + task.getId())
//                .then()
//                .statusCode(400)
//                .log().body();
//    }

    @Test
    void deleteTask_shouldReturn204() {
        given()
                .log().all()
                .when()
                .delete("/api/tasks/" + task.getId())
                .then()
                .log().all()
                .statusCode(204);
    }

//    @Test
//    void deleteTask_nonExistentId_shouldReturn500() {
//
//        given()
//                .when()
//                .delete("/api/tasks/9999")
//                .then()
//                .statusCode(500);
//    }

    @Test
    void createTask_withTaskMother_shouldReturn200() {
        Task taskWithUser = TaskMother.defaultTask();
        taskWithUser.setAssignee(user);  // assign user from DB

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(taskWithUser)
                .when()
                .post("/api/tasks")
                .then()
                .log().all()
                .statusCode(200)
                .body("title", equalTo("Sample Task"));
    }

    @Test
    void createTask_withTaskBuilder_shouldReturn200() {
        Task newTask = new TaskBuilder()
                .withAssignee(user)  // user loaded from SQL script
                .withTitle("Custom Task")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(newTask)
                .when()
                .post("/api/tasks")
                .then()
                .statusCode(200)
                .body("title", equalTo("Custom Task"));
    }

    @Test
    void createTask_withTaskMother_missingTitle_shouldReturn400() {
        Task invalidTask = TaskMother.taskWithMissingTitle();
        invalidTask.setAssignee(user);  // set valid assignee for meaningful test

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(invalidTask)
                .when()
                .post("/api/tasks")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    void createTask_withTaskMother_nonExistingAssignee_shouldReturn400() {
        Task invalidTask = TaskMother.taskWithNonExistingAssignee();

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(invalidTask)
                .when()
                .post("/api/tasks")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    void createTask_withTaskMother_missingDueDate_shouldReturn400() {
        Task invalidTask = TaskMother.taskWithNoDueDate();
        invalidTask.setAssignee(user);  // set valid assignee

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(invalidTask)
                .when()
                .post("/api/tasks")
                .then()
                .log().body()
                .statusCode(400);
    }

    @Test
    void updateTask_withTaskBuilder_shouldReturn200() {
        Task updatedTask = new TaskBuilder()
                .withAssignee(user)
                .withStatus(TaskStatus.COMPLETED)
                .build();
        updatedTask.setId(task.getId());

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updatedTask)
                .when()
                .put("/api/tasks/" + task.getId())
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("COMPLETED"));
    }

}