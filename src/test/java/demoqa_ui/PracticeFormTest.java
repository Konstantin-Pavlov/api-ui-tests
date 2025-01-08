package demoqa_ui;

import com.github.javafaker.Faker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled
public class PracticeFormTest extends BaseSelenideTest {
    Faker faker = new Faker();

    private final static String BASE_URL = "https://demoqa.com/";
    private final static String FILENAME = "cat.jpeg";
    private final static String FILEPATH = "src/test/resources/files/";

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String year;
    private String month;
    private String day;
    private String subject;
    private String hobbies;
    private String currentAddress;
    private String state;
    private String city;
    private String gender;

    @BeforeEach
    public void setup() {
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
        phone = faker.phoneNumber().subscriberNumber(10);
        currentAddress = faker.address().fullAddress();
        gender = "Male";
        year = "1973";
        month = "February";
        day = "08";
        subject = "Computer Science";
        hobbies = "Reading";
        state = "Uttar Pradesh";
        city = "Merrut";
    }

    @Epic("Forms Testing")
    @Feature("Practice Form Submission")
    @Story("Fill and validate the Practice Form")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test Filling and Submitting the Practice Form")
//    @RepeatedTest(2)
    @Test
    public void testFillTheForm() {
        PracticeFormPage practiceFormPage = new PracticeFormPage(BASE_URL);
        practiceFormPage.openFormsPage();
        practiceFormPage.pressFormButtonLeftSide();
        practiceFormPage.setFirstName(firstName);
        practiceFormPage.setLastName(lastName);
        practiceFormPage.setEmail(email);
        practiceFormPage.setPhoneNumber(phone);
        practiceFormPage.setDateOfBirth(year, month, day);
        practiceFormPage.setSubject(subject);
        practiceFormPage.setHobie(hobbies);
        practiceFormPage.uploadFile(FILEPATH, FILENAME);
        practiceFormPage.setCurrentAddress(currentAddress);
        practiceFormPage.setCurrentState(state);
        practiceFormPage.setCurrentCity(city);
        practiceFormPage.selectGender(gender);

        practiceFormPage.submitForm();
        practiceFormPage.takeScreenshot();

        practiceFormPage.checkFormTitle();
        practiceFormPage.checkTable("Student name", firstName + " " + lastName);
        practiceFormPage.checkTable("Student Email", email);
        practiceFormPage.checkTable("Gender", this.gender);
        practiceFormPage.checkTable("Mobile", phone);
        practiceFormPage.checkTable("Date of Birth", day + " " + month + "," + year);
        practiceFormPage.checkTable("Subjects", subject);
        practiceFormPage.checkTable("Hobbies", hobbies);
        practiceFormPage.checkTable("Picture", FILENAME);
        practiceFormPage.checkTable("Address", currentAddress);
        practiceFormPage.checkTable("State and City", state + " " + city);
    }

}
