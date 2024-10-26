package org.mattpayne.toy.courses.course;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mattpayne.toy.courses.config.BaseIT;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;


public class CourseResourceTest extends BaseIT {

    @Test
    @Sql("/data/courseData.sql")
    void getAllCourses_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/courses")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1100));
    }

    @Test
    @Sql("/data/courseData.sql")
    void getAllCourses_filtered() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/courses?filter=1101")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1101));
    }

    @Test
    @Sql("/data/courseData.sql")
    void getCourse_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/courses/1100")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("name", Matchers.equalTo("Sed diam voluptua."));
    }

    @Test
    void getCourse_notFound() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/courses/1766")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createCourse_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/courseDTORequest.json"))
                .when()
                    .post("/api/courses")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, courseRepository.count());
    }

    @Test
    void createCourse_missingField() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/courseDTORequest_missingField.json"))
                .when()
                    .post("/api/courses")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("name"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/courseData.sql")
    void updateCourse_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/courseDTORequest.json"))
                .when()
                    .put("/api/courses/1100")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Duis autem vel.", courseRepository.findById(((long)1100)).orElseThrow().getName());
        assertEquals(2, courseRepository.count());
    }

    @Test
    @Sql("/data/courseData.sql")
    void deleteCourse_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/courses/1100")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, courseRepository.count());
    }

}
