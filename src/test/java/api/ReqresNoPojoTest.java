package api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ReqresNoPojoTest {
    private final static String URL = "https://reqres.in/";

    @Test
    public void checkAvatarsNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");

        Assertions.assertEquals(emails.size(), ids.size());
        Assertions.assertEquals(emails.size(), avatars.size());

        for (int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(ids.get(i).toString()));
        }

        Assertions.assertTrue(emails.stream().allMatch(x -> x.endsWith("@reqres.in")));
    }

    @Test
    public void successfulUserRegTestNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        Assertions.assertEquals(4, id);
        Assertions.assertEquals("QpwL5tke4Pnpja7X4", token);
    }

    @Test
    public void unsuccessfulUserRegNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(201));
        Map<String, String> user = Map.of(
                "name", "morpheus",
                "job", "leader"
        );
        Response response = given()
                .body(user)
                .when()
                .post("api/users")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        Assertions.assertEquals("morpheus", jsonPath.get("name"));
        Assertions.assertEquals("leader", jsonPath.get("job"));
    }

    @Test
    public void successfulUserDeletionNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void successfulUserLoginNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> user = Map.of(
                "email", "eve.holt@reqres.in",
                "password", "cityslicka"
        );
        Response response = given()
                .body(user)
                .when()
                .post("api/login")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        Assertions.assertEquals("QpwL5tke4Pnpja7X4", jsonPath.get("token"));
    }

    @Test
    public void unsuccessfulUserLoginNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Map<String, String> user = Map.of(
                "email", "peter@klaven"
        );
        Response response = given()
                .body(user)
                .when()
                .post("api/login")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        Assertions.assertEquals("Missing password", jsonPath.get("error"));
    }

    @Test
    public void resourceNotFoundNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecNotFound404());
        Map<String, String> user = Map.of(
                "email", "peter@klaven"
        );
        given()
                .when()
                .get("api/unknown/23")
                .then().log().all();
    }

    @Test
    public void checkSortedYearsTestNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .body("data.year", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        List<Integer> years = jsonPath.get("data.year");
        List<Integer> sortedYears = years.stream().sorted().toList();

        Assertions.assertEquals(sortedYears, years);
    }

}
