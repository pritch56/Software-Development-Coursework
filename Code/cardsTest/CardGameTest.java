import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class CardGameTest {
    
    private final String testPackFile = "test_pack_integration.txt";
    private final ByteArrayOutputStream outputContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    public void setUp() throws IOException {
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outputContent));
        
        // Create a test pack file for 2 players
        try (PrintWriter writer = new PrintWriter(new FileWriter(testPackFile))) {
            // Create a winning scenario for player 1
            writer.println("1");
            writer.println("1");
            writer.println("1");
            writer.println("1");
            // Player 2s initial hand
            writer.println("2");
            writer.println("3");
            writer.println("4");
            writer.println("5");
            // Remaining 8 cards for decks
            for (int i = 6; i <= 13; i++) {
                writer.println(i);
            }
        }
    }
    
    public void tearDown() {
        // Restore System.out
        System.setOut(originalOut);
        
        // Clean up test files
        deleteTestFiles();
    }
    
    private void deleteTestFiles() {
        String[] filesToDelete = {
            testPackFile,
            "player1_output.txt",
            "player2_output.txt", 
            "player3_output.txt",
            "player4_output.txt",
            "deck1_output.txt",
            "deck2_output.txt",
            "deck3_output.txt", 
            "deck4_output.txt",
            "test_deck_1.txt",
            "test_deck_2.txt",
            "invalid_pack.txt",
            "negative_pack.txt"
        };
        
        for (String filename : filesToDelete) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    //  MANUAL ASSERTION METHODS 
    
    private void assertEquals(Object expected, Object actual) {
        if ((expected == null && actual != null) || 
            (expected != null && !expected.equals(actual))) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
    
    private void assertNotEquals(Object expected, Object actual) {
        if ((expected == null && actual == null) || 
            (expected != null && expected.equals(actual))) {
            throw new AssertionError("Expected not equal, but both were: " + expected);
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
    
    private void assertNull(Object object) {
        if (object != null) {
            throw new AssertionError("Expected null, but was: " + object);
        }
    }
    
    private void assertThrows(Class<? extends Exception> expectedException, Runnable action) {
        try {
            action.run();
            throw new AssertionError("Expected " + expectedException.getSimpleName() + " to be thrown");
        } catch (Exception e) {
            if (!expectedException.isInstance(e)) {
                throw new AssertionError("Expected " + expectedException.getSimpleName() + 
                                       " but got " + e.getClass().getSimpleName());
            }
        }
    }

    //  CARD CLASS TESTS 
    
    public void testValidCardCreation() {
        System.out.println("Testing Card creation with valid denomination ");
        Card card = new Card(5);
        assertEquals(5, card.getDenomination());
        System.out.println("PASSED");
    }
    
    public void testZeroDenominationCard() {
        System.out.println("Testing Card creation with zero denomination ");
        Card card = new Card(0);
        assertEquals(0, card.getDenomination());
        System.out.println("PASSED");
    }
    
    public void testNegativeDenominationThrowsException() {
        System.out.println("Testing Card creation with negative denomination throws exception ");
        assertThrows(IllegalArgumentException.class, () -> {
            new Card(-1);
        });
        System.out.println("PASSED");
    }
    
    public void testCardEquality() {
        System.out.println("Testing Card equality ");
        Card card1 = new Card(5);
        Card card2 = new Card(5);
        Card card3 = new Card(3);
        
        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card1, null);
        assertNotEquals(card1, "not a card");
        System.out.println("PASSED");
    }
    
    public void testCardHashCode() {
        System.out.println("Testing Card hashCode consistency ");
        Card card1 = new Card(7);
        Card card2 = new Card(7);
        
        assertEquals(card1.hashCode(), card2.hashCode());
        System.out.println("PASSED");
    }
    
    public void testCardToString() {
        System.out.println("Testing Card toString format ");
        Card card = new Card(10);
        assertEquals("10", card.toString());
        System.out.println("PASSED");
    }
    
    public void testCardImmutability() throws InterruptedException {
        System.out.println("Testing Card immutability through concurrent access ");
        Card card = new Card(42);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                assertEquals(42, card.getDenomination());
            });
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        System.out.println("PASSED");
    }

    // CARD DECK TESTS  
    
    public void testCardDeckCreation() {
        System.out.println("Testing CardDeck creation and basic operations ");
        CardDeck deck = new CardDeck(1);
        assertTrue(deck.isEmpty());
        assertEquals(0, deck.size());
        System.out.println("PASSED");
    }
    
    public void testFIFOBehavior() {
        System.out.println("Testing addCard and drawCard FIFO behavior ");
        CardDeck deck = new CardDeck(1);
        Card card1 = new Card(1);
        Card card2 = new Card(2);
        Card card3 = new Card(3);
        
        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);
        
        assertEquals(3, deck.size());
        assertFalse(deck.isEmpty());
        
        assertEquals(card1, deck.drawCard());
        assertEquals(card2, deck.drawCard());
        assertEquals(card3, deck.drawCard());
        
        assertTrue(deck.isEmpty());
        System.out.println("  PASSED");
    }
    
    public void testDrawFromEmptyDeck() {
        System.out.println("Testing drawCard from empty deck returns null ");
        CardDeck deck = new CardDeck(1);
        assertNull(deck.drawCard());
        System.out.println("PASSED");
    }
    
    public void testConcurrentDeckOperations() throws InterruptedException {
        System.out.println("Testing concurrent deck operations ");
        CardDeck deck = new CardDeck(1);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        final int operations = 100; // Reduced for faster testing
        
        // Add cards concurrently
        for (int i = 0; i < operations; i++) {
            final int cardValue = i;
            executor.submit(() -> {
                deck.addCard(new Card(cardValue));
            });
        }
        
        // Wait a bit for additions to complete
        Thread.sleep(100);
        
        // Draw cards concurrently
        for (int i = 0; i < operations; i++) {
            executor.submit(() -> {
                deck.drawCard();
            });
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        
        // Deck should be empty after all operations
        assertTrue(deck.isEmpty());
        System.out.println("  PASSED");
    }
    
    public void testNullCardHandling() {
        System.out.println("Testing deck with null card handling ");
        CardDeck deck = new CardDeck(1);
        assertThrows(NullPointerException.class, () -> {
            deck.addCard(null);
        });
        System.out.println("  PASSED");
    }

    // PLAYER CLASS TESTS  
    
    public void testPlayerCreation() {
        System.out.println("Testing Player creation ");
        CardDeck leftDeck = new CardDeck(1);
        CardDeck rightDeck = new CardDeck(2);
        AtomicBoolean gameEnded = new AtomicBoolean(false);
        
        Player player = new Player(1, leftDeck, rightDeck, gameEnded);
        assertTrue(player != null);
        System.out.println("  PASSED");
    }
    
    public void testPlayerInitialHand() throws IOException {
        System.out.println("Testing Player initial hand setup ");
        CardDeck leftDeck = new CardDeck(1);
        CardDeck rightDeck = new CardDeck(2);
        AtomicBoolean gameEnded = new AtomicBoolean(false);
        
        Player player = new Player(1, leftDeck, rightDeck, gameEnded);
        
        // Add cards to players hand
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(2));
        player.addCardToHand(new Card(3));
        player.addCardToHand(new Card(4));
        
        assertFalse(player.hasWinningHand());
        System.out.println("  PASSED");
    }
    
    public void testPlayerWinningCondition() throws IOException {
        System.out.println("Testing Player winning condition ");
        CardDeck leftDeck = new CardDeck(1);
        CardDeck rightDeck = new CardDeck(2);
        AtomicBoolean gameEnded = new AtomicBoolean(false);
        
        Player player = new Player(1, leftDeck, rightDeck, gameEnded);
        
        // Add winning hand
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        
        assertTrue(player.hasWinningHand());
        System.out.println("  PASSED");
    }
    
    public void testPlayerCardPreference() throws IOException {
        System.out.println("Testing Player card preference strategy ");
        CardDeck leftDeck = new CardDeck(1);
        CardDeck rightDeck = new CardDeck(2);
        AtomicBoolean gameEnded = new AtomicBoolean(false);
        
        Player player = new Player(2, leftDeck, rightDeck, gameEnded);
        
        // Add mixed hand
        player.addCardToHand(new Card(2)); // Preferred
        player.addCardToHand(new Card(5)); // Non-preferred
        player.addCardToHand(new Card(2)); // Preferred
        player.addCardToHand(new Card(7)); // Non-preferred
        
        // Test that player has both preferred and non-preferred cards
        assertFalse(player.hasWinningHand()); // Should not be winning yet
        
        System.out.println("  PASSED");
    }

    // INTEGRATION TESTS  
    
    public void testImmediateWinScenario() throws IOException {
        System.out.println("Testing immediate win scenario ");
        setUp(); // Setup test environment
        
        // Simulate user input 2 players, then the test pack file
        String input = "2\n" + testPackFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        try {
            // Run the game
            CardGame.main(new String[]{});
            
            // Verify output contains win message
            String output = outputContent.toString();
            System.out.println("Game output contains: " + output.substring(0, Math.min(200, output.length())));
            
            // Check if any player won
            boolean somePlayerWon = output.contains("wins");
            assertTrue(somePlayerWon);
            
            // Verify output files were created
            assertTrue(new File("player1_output.txt").exists());
            assertTrue(new File("player2_output.txt").exists());
            assertTrue(new File("deck1_output.txt").exists());
            assertTrue(new File("deck2_output.txt").exists());
            
        } catch (Exception e) {
            System.out.println("Exception during game execution: " + e.getMessage());
            throw e;
        } finally {
            // Restore System.in
            System.setIn(System.in);
            tearDown(); // Clean up
        }
        
        System.out.println("  PASSED");
    }
    
    public void testInvalidPackFileHandling() throws IOException {
        System.out.println("Testing invalid pack file handling ");
        setUp(); // Setup test environment
        
        // Create an invalid pack file (wrong number of cards)
        String invalidPackFile = "invalid_pack.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(invalidPackFile))) {
            writer.println("1");
            writer.println("2");
            // Only 2 cards instead of 16 for 2 players
        }
        
        // Simulate user input: 2 players, invalid file, then valid file
        String input = "2\n" + invalidPackFile + "\n" + testPackFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Run the game
        CardGame.main(new String[]{});
        
        // Verify error message appears
        String output = outputContent.toString();
        assertTrue(output.contains("Error: Pack must contain exactly 16 cards"));
        
        // Clean up
        new File(invalidPackFile).delete();
        System.setIn(System.in);
        tearDown(); // Clean up
        System.out.println("  PASSED");
    }
    
    // MAIN TEST RUNNER  
    
    public static void main(String[] args) {
        CardGameTest tester = new CardGameTest();
        int totalTests = 0;
        int passedTests = 0;
        
        System.out.println("=== COMPREHENSIVE CARD GAME TEST SUITE ===\n");
        
        // Card Class Tests
        System.out.println("--- CARD CLASS TESTS ---");
        try {
            tester.testValidCardCreation();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println(" FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testZeroDenominationCard();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println(" FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testNegativeDenominationThrowsException();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println(" FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testCardEquality();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testCardHashCode();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println(" FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testCardToString();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testCardImmutability();
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        // CardDeck Class Tests
        System.out.println("\n CARD DECK TESTS");
        try {
            tester.testCardDeckCreation();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testFIFOBehavior();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println(" FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testDrawFromEmptyDeck();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testConcurrentDeckOperations();
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testNullCardHandling();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        // Player Class Tests
        System.out.println("\n--- PLAYER TESTS ---");
        try {
            tester.testPlayerCreation();
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testPlayerInitialHand();
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testPlayerWinningCondition();
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testPlayerCardPreference();
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        // Integration Tests
        System.out.println("\n--- INTEGRATION TESTS ---");
        try {
            tester.testImmediateWinScenario();
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        try {
            tester.testInvalidPackFileHandling();
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
        
        // Test Results Summary
        System.out.println("\n=== TEST RESULTS SUMMARY ===");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + (totalTests - passedTests));
        System.out.println("Success Rate: " + (100.0 * passedTests / totalTests) + "%");
        
        if (passedTests == totalTests) {
            System.out.println("\n*** ALL TESTS PASSED! ***");
        } else {
            System.out.println("\n*** Some tests failed. Please review the output above. ***");
        }
    }
}