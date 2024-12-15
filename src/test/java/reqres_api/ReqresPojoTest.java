package reqres_api;

import config.ConfigProvider;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reqres_api.color.ColorData;
import reqres_api.registration.Registration;
import reqres_api.registration.SuccessfulRegistration;
import reqres_api.registration.UnsuccessfulRegistration;
import reqres_api.user.UserData;
import reqres_api.user.UserTime;
import reqres_api.user.UserTimeResponse;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresPojoTest {

    private final static String URL = ConfigProvider.getConfig().url();
    private final static String API = ConfigProvider.getConfig().api();

    /**
     * Ensure that a user with ID 23 does not exist on the website https://reqres.in/
     */
    @Test
    @DisplayName("User with ID 23 does not exist")
    public void checkUserNotFound() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecNotFound404());
        UserData user = given()
                .when()
                .contentType(ContentType.JSON)
                .get(API + "/users/23")
                .then().log().all()
                .extract().as(UserData.class);
        Assertions.assertNull(user.getId());
        Assertions.assertNull(user.getAvatar());
        Assertions.assertNull(user.getEmail());
        Assertions.assertNull(user.getFirst_name());
        Assertions.assertNull(user.getLast_name());
    }

    /**
     * 1. Get a list of users from the second page on the website https://reqres.in/
     * 2. Ensure that user IDs are included in their avatars;
     * 3. Ensure that user emails end with 'reqres.in';
     */
    @Test
    @DisplayName("Avatars contain user IDs")
    public void checkAvatarContainsIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get(API + "/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        users.forEach(user -> Assertions.assertTrue(user.getAvatar().contains(String.valueOf(user.getId()))));
        users.forEach(user -> Assertions.assertTrue(user.getEmail().endsWith("reqres.in")));
    }

    /**
     * 1. Use the service https://reqres.in/ to test user registration in the system
     * 2. Test for successful registration
     */
    @Test
    @DisplayName("Successful Registration")
    public void successUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Integer UserId = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Registration user = new Registration("eve.holt@reqres.in", "pistol");
        SuccessfulRegistration successUserReg = given()
                .body(user)
                .when()
                .post(API + "/register")
                .then().log().all()
                .extract().as(SuccessfulRegistration.class);
        Assertions.assertNotNull(successUserReg.getId());
        Assertions.assertNotNull(successUserReg.getToken());
        Assertions.assertEquals(UserId, successUserReg.getId());
        Assertions.assertEquals(token, successUserReg.getToken());
    }

    /**
     * 1. Use the service https://reqres.in/ to test user registration in the system
     * 2. Test for unsuccessful registration (no password provided)
     */
    @Test
    @DisplayName("Unsuccessful Registration")
    public void unsuccessfulUserRegistrationNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());

        Registration registration = new Registration("sydney@fife", "");
        UnsuccessfulRegistration unsuccessfulRegistration = given()
                .body(registration)
                .when()
                .post(API + "/register")
                .then()  //.assertThat().statusCode(400) check error status, if specification is not defined
                .log().body()
                .extract().as(UnsuccessfulRegistration.class);
        Assertions.assertNotNull(unsuccessfulRegistration.getError());
        Assertions.assertEquals("Missing password", unsuccessfulRegistration.getError());
    }

    /**
     * Using the service https://reqres.in/, ensure that the operation LIST<RESOURCE> returns data
     * sorted by year.
     */
    @Test
    @DisplayName("Years are sorted correctly")
    public void checkSortedYearsTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<ColorData> data = given()
                .when()
                .get(API + "/unknown")
                .then()
                .log().all()
                .extract().body().jsonPath().getList("data", ColorData.class);

        List<Integer> dataYears = data.stream().map(ColorData::getYear).collect(Collectors.toList());
        List<Integer> sortedDataYears = dataYears.stream().sorted().collect(Collectors.toList());
        Assertions.assertEquals(dataYears, sortedDataYears);
        System.out.println(dataYears);
        System.out.println(sortedDataYears);
    }

    /**
     * Test 4.1
     * Use the service https://reqres.in/ to try to delete the second user and compare the status code
     */
    @Test
    @DisplayName("Delete user")
    public void deleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(204));
        given()
                .when()
                .delete(API + "/users/2")
                .then()
                .log().all();
    }

    /**
     * Use the service https://reqres.in/ to update user information and compare the update date with
     * the current date on the machine.
     */
    @Test
    @DisplayName("Server and computer time match")
    public void checkServerAndPcDateTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        UserTime user = new UserTime("morpheus", "zion resident");

        UserTimeResponse userTimeResponse = given()
                .body(user)
                .when()
                .put(API + "/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        // Format both times to remove seconds and milliseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
        String serverTime = LocalDateTime.parse(userTimeResponse.getUpdatedAt(), DateTimeFormatter.ISO_DATE_TIME).format(formatter);
        String computerTime = LocalDateTime.now(ZoneOffset.UTC).format(formatter);
        Assertions.assertEquals(serverTime, computerTime);
    }
}


