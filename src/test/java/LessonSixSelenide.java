import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Epic;
import io.qameta.allure.selenide.AllureSelenide;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.linkText;

public class LessonSixSelenide {
    @Epic("Tests for lesson 6")

    @BeforeAll
    public static void setUp() {
        System.setProperty("allure.results.directory", "D:\\T1\\out-lesson5\\build");
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
        Configuration.browser = "chrome";
    }

    @AfterAll
    public static void tearDown() {
        closeWebDriver();
    }

    @BeforeEach
    public void prepare() {
        open("https://the-internet.herokuapp.com/");
    }

    /*
        Перейти на страницу Drag and Drop.
        Перетащить элемент A на элемент B.
        Задача на 10 баллов – сделать это, не прибегая к методу DragAndDrop();
        Проверить, что элементы поменялись местами
     */
    @DisplayName("Drag and drop test.")
    @Test
    public void dragAndDropTest() {
        $(linkText("Drag and Drop")).click();
        SelenideElement elementA = $(By.id("column-a"));
        SelenideElement elementB = $(By.id("column-b"));

        Actions actions = new Actions(Selenide.webdriver().object());
        actions.clickAndHold(elementA)
                .moveToElement(elementB)
                .release()
                .build()
                .perform();
        elementA.shouldHave(text("B"));
        elementB.shouldHave(text("A"));
    }

    /*
    Перейти на страницу Context menu.
    Нажать правой кнопкой мыши на отмеченной области и проверить,
    что JS Alert имеет ожидаемый текст.
     */
    @DisplayName("Context menu test.")
    @Test
    public void contextMenuTest() throws InterruptedException {
        $(linkText("Context Menu")).click();
        new Actions(Selenide.webdriver().object()).contextClick($(By.id("hot-spot"))).perform();
        assertEquals("You selected a context menu", switchTo().alert().getText(),
                "Текст ОJS Alert не соответствует ожидаемому");
    }

    /*
    Перейти на страницу Infinite Scroll.
    Проскролить страницу до текста «Eius», проверить, что текст в поле зрения.
    */
    @DisplayName("Infinite Scroll test.")
    @Test
    @Timeout(300)
    public void infiniteScrollTest() {
        $(linkText("Infinite Scroll")).click();
        String targetText = "Eius";
        SelenideElement targetElement = $$(className("jscroll-added")).last();
        while (!targetElement.getText().contains(targetText)) {
            executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        }
        System.out.println(targetElement.getText());
    }

    @DisplayName("Key Presses test.")
    @TestFactory
    public List<DynamicTest> keyPressesTest() {
        $(linkText("Key Presses")).click();
        List<DynamicTest> result = new ArrayList<>();
        // Передаем 3 значения. Название теста, что будет введено, что ожидаем в ответе.
        List<Triple<String, String, String>> collection = new ArrayList<>();
        char letter = 'A';
        for (int i = 0; i < 10; i++) {
            collection.add(Triple.of("Тест ввода буквы " + letter, String.valueOf(letter), String.valueOf(letter)));
            letter++;
        }
        collection.add(Triple.of("Тест ввода ENTER", Keys.ENTER.toString(), Keys.ENTER.name()));
        collection.add(Triple.of("Тест ввода CONTROL", Keys.CONTROL.toString(), Keys.CONTROL.name()));
        collection.add(Triple.of("Тест ввода ALT", Keys.ALT.toString(), Keys.ALT.name()));
        collection.add(Triple.of("Тест ввода TAB", Keys.TAB.toString(), Keys.TAB.name()));
        collection.forEach(test -> {
            result.add(DynamicTest.dynamicTest(test.getLeft(),
                    () -> {
                        actions().sendKeys(test.getMiddle()).perform();
                        SelenideElement WebResult = $(By.id("result"));
                        System.out.println("Key pressed: " + test.getMiddle());
                        WebResult.shouldHave(text(test.getRight()));

                    }));
        });
        return result;

    }

}
