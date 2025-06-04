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
import org.springframework.test.context.jdbc.Sql;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@AutoConfigureMockMvc(addFilters = false)
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

    @BeforeAll
    void initRestAssuredPort() {
        RestAssured.port = port;
    }

    @BeforeEach
    void setup() {
        user = userRepository.findAll().get(0);
        task = taskRepository.findAll().get(0);
    }

    @Test
    void createTask_shouldReturn200() {
        given()
                .log().all()                        // log full request
                .contentType(ContentType.JSON)
                .body(task)
                .when()
                .post("/api/tasks")
                .then()
                .log().all()                        // log full response
                .statusCode(200)
                .body("title", equalTo("Sample Task"));
    }

    @Test
    void createTask_missingTitle_shouldReturn400() {
        Task invalidTask = new Task();
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

        given()
                .log().all()
                .when()
                .get("/api/tasks")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(0));
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
    void createTask_withTaskMother_nonExistingAssignee_shouldReturn500() {
        Task invalidTask = TaskMother.taskWithNonExistingAssignee();

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(invalidTask)
                .when()
                .post("/api/tasks")
                .then()
                .log().all()
                .statusCode(500);
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
