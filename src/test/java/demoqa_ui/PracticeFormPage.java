package demoqa_ui;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;

import java.io.File;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class PracticeFormPage {
    private final SelenideElement formButtonMainPage = $x("//h5[text()='Forms']");
    private final SelenideElement formButtonLeftSide = $x("//span[text()='Practice Form']");
    private final SelenideElement firstName = $x("//input[@id='firstName']");
    private final SelenideElement lastName = $("#lastName");
    private final SelenideElement email = $("#userEmail");
    private final SelenideElement phoneNumber = $("#userNumber");
    private final SelenideElement subject = $("#subjectsInput");
    private final SelenideElement currentAddress = $("#currentAddress");

    public PracticeFormPage(String url) {
        Selenide.open(url);
    }

    public void openFormsPage() {
        formButtonMainPage.click();
    }

    public void pressFormButtonLeftSide() {
        formButtonLeftSide.click();
    }

    public void setFirstName(String firstName) {
        System.out.println(this.firstName.exists());
        this.firstName.setValue(firstName);
    }

    public void setLastName(String lastName) {
        System.out.println(this.lastName.exists());
        this.lastName.setValue(lastName);
    }

    public void setEmail(String email) {
        System.out.println(this.email.exists());
        this.email.setValue(email);
    }

    public void selectGender(String gender) {
        $x("//label[text()='" + gender + "']").click();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.setValue(phoneNumber);
    }

    public void setDateOfBirth(String year, String month, String day) {
        $("#dateOfBirthInput").click();
        $(".react-datepicker__month-select").selectOption(month);
        $(".react-datepicker__year-select").selectOption(year);
        $(".react-datepicker__day--0" + day).click();
    }

    public void setSubject(String subject) {
        this.subject.setValue(subject).pressEnter();
    }

    public void setHobie(String hobie) {
        $("#hobbiesWrapper").$(byText(hobie)).click();
    }

    public void setCurrentAddress(String address) {
        currentAddress.setValue(address);
    }

    public void setCurrentState(String state) {
        $("#react-select-3-input").setValue(state).pressEnter();
    }

    public void setCurrentCity(String city) {
        $("#react-select-4-input").setValue(city).pressEnter();
    }

    public void uploadFile(String filepath, String filename) {
        $("#uploadPicture").uploadFile(new File(filepath + filename));
    }

    public void submitForm() {
        Selenide.sleep(2000);
        $("#submit").scrollIntoView(true).click();
    }

    public void checkFormTitle() {
        //Проверяем заполнение анкеты
        $("#example-modal-sizes-title-lg").shouldHave(text("Thanks for submitting the form"));
    }

    public void checkTable(String checkname, String actualResult) {
        $$(".table-responsive tr").filterBy(text(checkname)).shouldHave(texts(actualResult));
    }

    @Attachment(value = "Screenshot", type = "image/png")
    @Step("Taking a screenshot")
    public byte[] takeScreenshot() {
        return Selenide.screenshot(OutputType.BYTES);
    }
}
