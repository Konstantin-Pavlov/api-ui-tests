package demoqa_ui;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class PracticeFormPage {
    private final SelenideElement firstName = $x("//input[@id='firstName']");
    private final SelenideElement lastName = $("#lastName");
    private final SelenideElement email = $("#userEmail");
    private final SelenideElement gender = $("#gender-radio-3");

    public PracticeFormPage(String url) {
        Selenide.open(url);
        firstName.shouldBe(visible);
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


    public String getFirstNameValue() {
        return firstName.getValue();
    }

    public String getLastNameValue() {
        return lastName.getValue();
    }
}
