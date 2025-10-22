import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerTest {
    
    private Player player;
    private CardDeck drawDeck;
    private CardDeck discardDeck;
    private AtomicBoolean gameWon;
    private int totalTests = 0;
    private int passedTests = 0;
    
    // Manual assertion methods
    private void assertEquals(Object expected, Object actual) {
        if ((expected == null && actual != null) || 
            (expected != null && !expected.equals(actual))) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
    
    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true, but was false");
        }
    }
    
    private void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false, but was true");
        }
    }
    
    public void setUp() {
        drawDeck = new CardDeck(1);
        discardDeck = new CardDeck(2);
        gameWon = new AtomicBoolean(false);
        player = new Player(1, drawDeck, discardDeck, gameWon);
    }
    
    public void tearDown() {
        // Clean up test files
        File playerFile = new File("player1_output.txt");
        if (playerFile.exists()) {
            playerFile.delete();
        }
    }
    
    public void testPlayerCreation() {
        System.out.println("Testing player creation...");
        try {
            setUp();
            assertEquals(1, player.getPlayerNumber());
            assertFalse(player.isWinner());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testAddingCardsToHand() {
        System.out.println("Testing adding cards to hand...");
        try {
            setUp();
            player.addCardToHand(new Card(1));
            player.addCardToHand(new Card(2));
            player.addCardToHand(new Card(3));
            player.addCardToHand(new Card(4));
            
            String hand = player.getHandAsString();
            assertEquals("1 2 3 4", hand);
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testWinningHandDetection() {
        System.out.println("Testing winning hand detection - all same value...");
        try {
            setUp();
            // Add four cards of the same value
            player.addCardToHand(new Card(5));
            player.addCardToHand(new Card(5));
            player.addCardToHand(new Card(5));
            player.addCardToHand(new Card(5));
            
            assertTrue(player.hasWinningHand());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testNonWinningHandDetection() {
        System.out.println("Testing non-winning hand detection - different values...");
        try {
            setUp();
            player.addCardToHand(new Card(1));
            player.addCardToHand(new Card(2));
            player.addCardToHand(new Card(3));
            player.addCardToHand(new Card(4));
            
            assertFalse(player.hasWinningHand());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testIncompleteHandDetection() {
        System.out.println("Testing incomplete hand detection...");
        try {
            setUp();
            player.addCardToHand(new Card(1));
            player.addCardToHand(new Card(1));
            player.addCardToHand(new Card(1));
            // Only 3 cards, not 4
            
            assertFalse(player.hasWinningHand());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testHandSizeLimit() {
        System.out.println("Testing hand size limit...");
        try {
            setUp();
            // Try to add more than 4 cards
            for (int i = 1; i <= 6; i++) {
                player.addCardToHand(new Card(i));
            }
            
            // Should only have 4 cards
            String hand = player.getHandAsString();
            String[] cards = hand.split(" ");
            assertEquals(4, cards.length);
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testFileOutputCreation() {
        System.out.println("Testing file output creation...");
        try {
            setUp();
            player.addCardToHand(new Card(1));
            player.addCardToHand(new Card(2));
            player.addCardToHand(new Card(3));
            player.addCardToHand(new Card(4));
            
            // Write initial hand
            player.writeInitialHand();
            
            // Give player some time to write
            Thread.sleep(50);
            
            // Check if output file was created
            File outputFile = new File("player1_output.txt");
            assertTrue(outputFile.exists());
            
            // Check if file contains initial hand
            String content = new String(Files.readAllBytes(Paths.get("player1_output.txt")));
            assertTrue(content.contains("player 1 initial hand"));
            assertTrue(content.contains("1 2 3 4"));
            System.out.println("PASSED");
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void runAllTests() {
        System.out.println("Running Player class tests...");
        testPlayerCreation();
        testAddingCardsToHand();
        testWinningHandDetection();
        testNonWinningHandDetection();
        testIncompleteHandDetection();
        testHandSizeLimit();
        testFileOutputCreation();
        
        System.out.println("Player tests completed: " + passedTests + "/" + totalTests + " passed");
    }
    
    public int getTotalTests() {
        return totalTests;
    }
    
    public int getPassedTests() {
        return passedTests;
    }
    
    public static void main(String[] args) {
        PlayerTest test = new PlayerTest();
        test.runAllTests();
    }
}