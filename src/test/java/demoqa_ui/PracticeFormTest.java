package demoqa_ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PracticeFormTest extends BaseSelenideTest {
    private final static String BASE_URL = "https://demoqa.com/automation-practice-form";
    private final static String SEARCH_STRING = "Чем iPhone 13 отличается от iPhone 12";
    private final static String EXPECTED_WORD = "iphone-12";

    @Test
    public void testFillTheForm() {
        PracticeFormPage practiceFormPage = new PracticeFormPage(BASE_URL);
        practiceFormPage.selectGender("Male");
        practiceFormPage.setFirstName("Tom");
        practiceFormPage.setLastName("Brown");
        practiceFormPage.setEmail("Brown@email.com");
        String firstNameActualValue = practiceFormPage.getFirstNameValue();
        String lastNameActualValue = practiceFormPage.getLastNameValue();
        Assertions.assertEquals("Tom", firstNameActualValue);
        Assertions.assertEquals("Brown", lastNameActualValue);
    }
}
