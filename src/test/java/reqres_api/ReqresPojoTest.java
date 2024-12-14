package reqres_api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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

@Disabled
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
                .get("/reqres_api/users/23")
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
                .get("reqres_api/users?page=2")
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
                .post("reqres_api/register")
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
                .post("/reqres_api/register")
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
                .get("/reqres_api/unknown")
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
     * Тест 4.1
     * Используя сервис https://reqres.in/ попробовать удалить второго пользователя и сравнить статус-код
     */
    @Test
    @DisplayName("Удаление пользователя")
    public void deleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(204));
        given()
                .when()
                .delete("/reqres_api/users/2")
                .then()
                .log().all();
    }

    /**
     * Используя сервис https://reqres.in/ обновить информацию о пользователе и сравнить дату обновления с текущей датой на машине
     */
    @Test
    @DisplayName("Время сервера и компьютера совпадают")
    public void checkServerAndPcDateTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        UserTime user = new UserTime("morpheus", "zion resident");

        UserTimeResponse userTimeResponse = given()
                .body(user)
                .when()
                .put("/reqres_api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        // Format both times to remove seconds and milliseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String serverTime = LocalDateTime.parse(userTimeResponse.getUpdatedAt(), DateTimeFormatter.ISO_DATE_TIME).format(formatter);
        String computerTime = LocalDateTime.now(ZoneOffset.UTC).format(formatter);
        Assertions.assertEquals(serverTime, computerTime);
    }
}

