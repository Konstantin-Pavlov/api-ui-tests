package reqres_api;

import config.ConfigProvider;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test class for verifying the functionality of the Reqres.in API without using POJOs.
 * <p>
 * The tests cover various endpoints of the API, including user registration, login,
 * resource retrieval, and deletion. Assertions validate the correctness of the responses
 * based on expected results.
 */
public class ReqresNoPojoTest {
    private final static String URL = ConfigProvider.getConfig().url();
    private final static String API = ConfigProvider.getConfig().api();

    /**
     * Test to check the avatars of users are not null and email follows the domain.
     */
    @Test
    @DisplayName("Check avatars and emails of users are correct")
    public void checkAvatarsNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get(API + "/users?page=2")
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

    /**
     * Test to check successful user registration.
     */
    @Test
    @DisplayName("Check successful user registration")
    public void successfulUserRegTestNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        Response response = given()
                .body(user)
                .when()
                .post(API + "/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        Assertions.assertEquals(4, id);
        Assertions.assertEquals("QpwL5tke4Pnpja7X4", token);
    }

    /**
     * Test to check unsuccessful user registration with missing parameters.
     */
    @Test
    @DisplayName("Check unsuccessful user registration")
    public void unsuccessfulUserRegNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(201));
        Map<String, String> user = Map.of(
                "name", "morpheus",
                "job", "leader"
        );
        Response response = given()
                .body(user)
                .when()
                .post(API + "/users")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        Assertions.assertEquals("morpheus", jsonPath.get("name"));
        Assertions.assertEquals("leader", jsonPath.get("job"));
    }

    /**
     * Test to check successful user deletion.
     */
    @Test
    @DisplayName("Check successful user deletion")
    public void successfulUserDeletionNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(204));
        given()
                .when()
                .delete(API + "/users/2")
                .then().log().all();
    }

    /**
     * Test to check successful user login.
     */
    @Test
    @DisplayName("Check successful user login")
    public void successfulUserLoginNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> user = Map.of(
                "email", "eve.holt@reqres.in",
                "password", "cityslicka"
        );
        Response response = given()
                .body(user)
                .when()
                .post(API + "/login")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        Assertions.assertEquals("QpwL5tke4Pnpja7X4", jsonPath.get("token"));
    }

    /**
     * Test to check unsuccessful user login with missing password.
     */
    @Test
    @DisplayName("Check unsuccessful user login")
    public void unsuccessfulUserLoginNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Map<String, String> user = Map.of(
                "email", "peter@klaven"
        );
        Response response = given()
                .body(user)
                .when()
                .post(API + "/login")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        Assertions.assertEquals("Missing password", jsonPath.get("error"));
    }

    /**
     * Test to check resource not found for invalid ID.
     */
    @Test
    @DisplayName("Check resource not found for invalid ID")
    public void resourceNotFoundNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecNotFound404());
        given()
                .when()
                .get(API + "/unknown/23")
                .then().log().all();
    }

    /**
     * Test to check if the years are sorted correctly in the response.
     */
    @Test
    @DisplayName("Check sorted years in response")
    public void checkSortedYearsTestNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get(API + "/unknown")
                .then().log().all()
                .body("data.year", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        List<Integer> years = jsonPath.get("data.year");
        List<Integer> sortedYears = years.stream().sorted().toList();

        Assertions.assertEquals(sortedYears, years);
    }

    /**
     * Test to check the response time for delayed response.
     */
    @Test
    @DisplayName("Check response time for delayed response")
    public void testDelayedResponse() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());

        Response response = given()
                .param("delay", "3") // Introduce a delay of 3 seconds in the response
                .when()
                .get("/api/users")
                .then().log().all()
                .extract()
                .response();

        long responseTime = response.getTime();
        System.out.println("Response time: " + responseTime + " ms");

        // Assert that the response time is greater than or equal to 3000 ms (3 seconds)
        Assertions.assertTrue(responseTime >= 3000, "Response time is less than expected delay");

        // Additional check to ensure the response contains valid data
        JsonPath jsonPath = response.jsonPath();
        String lastName = jsonPath.get("data[0].last_name");
        Assertions.assertEquals(lastName, "Bluth");
        response.then().body("data[0].first_name", equalTo("George"));
    }
}