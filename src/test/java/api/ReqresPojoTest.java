package api;

import api.color.ColorData;
import api.registration.Registration;
import api.registration.SuccessfulRegistration;
import api.registration.UnsuccessfulRegistration;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresPojoTest {

    private final static String URL = "https://reqres.in/";

    /**
     * Убедиться что пользователя с ID 23 не существует на сайте https://reqres.in/
     */
    @Test
    @DisplayName("Пользователь с id 23 не существует")
    public void checkUserNotFound() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecNotFound404());
        UserData user = given()
                .when()
                .contentType(ContentType.JSON)
                .get("/api/users/23")
                .then().log().all()
                .extract().as(UserData.class);
        Assertions.assertNull(user.getId());
        Assertions.assertNull(user.getAvatar());
        Assertions.assertNull(user.getEmail());
        Assertions.assertNull(user.getFirst_name());
        Assertions.assertNull(user.getLast_name());
    }

    /**
     * 1. Получить список пользователей со второй страница на сайте https://reqres.in/
     * 2. Убедиться что id пользователей содержаться в их avatar;
     * 3. Убедиться, что email пользователей имеет окончание reqres.in;
     */
    @Test
    @DisplayName("Аватары содержат айди пользователей")
    public void checkAvatarContainsIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        users.forEach(user -> Assertions.assertTrue(user.getAvatar().contains(String.valueOf(user.getId()))));
        users.forEach(user -> Assertions.assertTrue(user.getEmail().endsWith("reqres.in")));
    }

    /**
     * 1. Используя сервис https://reqres.in/ протестировать регистрацию пользователя в системе
     * 2. Тест для успешной регистрации
     */
    @Test
    @DisplayName("Успешная регистрация")
    public void successUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Integer UserId = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Registration user = new Registration("eve.holt@reqres.in", "pistol");
        SuccessfulRegistration successUserReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessfulRegistration.class);
        Assertions.assertNotNull(successUserReg.getId());
        Assertions.assertNotNull(successUserReg.getToken());
        Assertions.assertEquals(UserId, successUserReg.getId());
        Assertions.assertEquals(token, successUserReg.getToken());
    }

    /**
     * 1. Используя сервис https://reqres.in/ протестировать регистрацию пользователя в системе
     * 2. Тест для неуспешной регистрации (не введен пароль)
     */
    @Test
    @DisplayName("Не успешная регистрация")
    public void unsuccessfulUserRegistrationNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());

        Registration registration = new Registration("sydney@fife", "");
        UnsuccessfulRegistration unsuccessfulRegistration = given()
                .body(registration)
                .when()
                .post("/api/register")
                .then()  //.assertThat().statusCode(400) проверить статус ошибки, если не указана спецификация
                .log().body()
                .extract().as(UnsuccessfulRegistration.class);
        Assertions.assertNotNull(unsuccessfulRegistration.getError());
        Assertions.assertEquals("Missing password", unsuccessfulRegistration.getError());
    }

    /**
     * Используя сервис https://reqres.in/ убедиться, что операция LIST<RESOURCE> возвращает данные,
     * отсортированные по годам.
     */
    @Test
    @DisplayName("Года правильно отсортированы")
    public void checkSortedYearsTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<ColorData> data = given()
                .when()
                .get("/api/unknown")
                .then()
                .log().all()
                .extract().body().jsonPath().getList("data", ColorData.class);

        List<Integer> dataYears = data.stream().map(ColorData::getYear).collect(Collectors.toList());
        List<Integer> sortedDataYears = dataYears.stream().sorted().collect(Collectors.toList());
        Assertions.assertEquals(dataYears, sortedDataYears);
        System.out.println(dataYears);
        System.out.println(sortedDataYears);
    }
}

