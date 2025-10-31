import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Card Game Test Suite")
@SelectClasses({
    CardTest.class,
    CardDeckTest.class,
    PlayerTest.class,
    CardGameTest.class
})
public class CardGameTestSuite {
    // suite runner
}