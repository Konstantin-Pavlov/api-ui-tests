import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class SelenideExampleGoogleTest {
    @Test
    void searchInGoogle() {
        // Открыть страницу
        open("https://www.google.com");

        // Ввести текст в поисковую строку и нажать Enter
        $("[name='q']").setValue("Selenide").pressEnter();

        // Проверить, что первый результат содержит текст "selenide.org"
        $$("#search .g").first().shouldHave(text("selenide.org"));
    }
}
