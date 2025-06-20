package za.co.turbo.code_shield.unit.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import za.co.turbo.code_shield.model.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskApiPage {

    private final RequestSpecification request;

    public TaskApiPage(String baseUri) {
        this.request = RestAssured.given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON);
    }

    public Response createTask(Task task) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("title", task.getTitle());
        jsonMap.put("description", task.getDescription());
        jsonMap.put("status", task.getStatus().toString());
        jsonMap.put("createdAt", task.getCreatedAt().toString());
        jsonMap.put("dueDate", task.getDueDate().toString());
        jsonMap.put("assigneeId", task.getAssignee().getId());

        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(jsonMap)
                .post("/api/tasks")
                .then()
                .extract()
                .response();
    }


    public Response createInvalidTask(Task task) {
        return request
                .log().all()
                .body(task)
                .post("/api/tasks")
                .then()
                .log().body()
                .extract().response();
    }

    public Response getAllTasks() {
        return request
                .log().all()
                .get("/api/tasks")
                .then()
                .log().all()
                .extract().response();
    }

    public Response getTaskById(Long id) {
        return request
                .log().all()
                .get("/api/tasks/" + id)
                .then()
                .log().all()
                .extract().response();
    }

    public Response updateTask(Long id, Task task) {
        return request
                .log().all()
                .body(task)
                .put("/api/tasks/" + id)
                .then()
                .log().all()
                .extract().response();
    }

    public Response updateInvalidTask(Long id, Task task) {
        return request
                .log().all()
                .body(task)
                .put("/api/tasks/" + id)
                .then()
                .log().body()
                .extract().response();
    }

    public Response deleteTask(Long id) {
        return request
                .log().all()
                .delete("/api/tasks/" + id)
                .then()
                .log().all()
                .extract().response();
    }

    public Response deleteNonExistentTask(Long id) {
        return request
                .log().all()
                .delete("/api/tasks/" + id)
                .then()
                .log().all()
                .extract().response();
    }
}
