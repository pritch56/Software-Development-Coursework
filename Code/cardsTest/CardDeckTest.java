import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CardDeckTest {
    
    private CardDeck deck;
    private Card card1, card2, card3;
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
    
    private void assertNull(Object actual) {
        if (actual != null) {
            throw new AssertionError("Expected null, but was: " + actual);
        }
    }
    
    public void setUp() {
        deck = new CardDeck(1);
        card1 = new Card(5);
        card2 = new Card(10);
        card3 = new Card(15);
    }
    
    public void tearDown() {
        // Clean up test files
        File testFile = new File("deck1_output.txt");
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    public void testDeckCreation() {
        System.out.println("Testing deck creation...");
        try {
            setUp();
            CardDeck newDeck = new CardDeck(5);
            assertEquals(5, newDeck.getDeckNumber());
            assertTrue(newDeck.isEmpty());
            assertEquals(0, newDeck.size());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testAddingCards() {
        System.out.println("Testing adding cards to deck...");
        try {
            setUp();
            deck.addCard(card1);
            deck.addCard(card2);
            
            assertFalse(deck.isEmpty());
            assertEquals(2, deck.size());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testFIFOBehavior() {
        System.out.println("Testing FIFO behavior - draw and discard...");
        try {
            setUp();
            deck.addCard(card1);
            deck.addCard(card2);
            deck.addCard(card3);
            
            // Draw cards should come in FIFO order
            Card drawn1 = deck.drawCard();
            Card drawn2 = deck.drawCard();
            
            assertEquals(card1, drawn1);
            assertEquals(card2, drawn2);
            assertEquals(1, deck.size());
            
            // Discard a card
            Card newCard = new Card(20);
            deck.discardCard(newCard);
            
            assertEquals(2, deck.size());
            
            // Next draw should be card3, then newCard
            Card drawn3 = deck.drawCard();
            Card drawn4 = deck.drawCard();
            
            assertEquals(card3, drawn3);
            assertEquals(newCard, drawn4);
            assertTrue(deck.isEmpty());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testDrawFromEmptyDeck() {
        System.out.println("Testing drawing from empty deck...");
        try {
            setUp();
            assertTrue(deck.isEmpty());
            Card drawn = deck.drawCard();
            assertNull(drawn);
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testDeckThreadSafety() {
        System.out.println("Testing deck thread safety...");
        try {
            setUp();
            final int numThreads = 10;
            final int operationsPerThread = 100;
            final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            final AtomicInteger addedCards = new AtomicInteger(0);
            final AtomicInteger drawnCards = new AtomicInteger(0);
            
            // Add some initial cards
            for (int i = 0; i < 50; i++) {
                deck.addCard(new Card(i));
            }
            
            // Create threads that add and draw cards concurrently
            for (int i = 0; i < numThreads; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    for (int j = 0; j < operationsPerThread; j++) {
                        // Add a card
                        deck.addCard(new Card(threadId * operationsPerThread + j));
                        addedCards.incrementAndGet();
                        
                        // Try to draw a card
                        Card drawn = deck.drawCard();
                        if (drawn != null) {
                            drawnCards.incrementAndGet();
                        }
                    }
                });
            }
            
            executor.shutdown();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
            
            // Verify that all operations completed without corruption
            assertEquals(numThreads * operationsPerThread, addedCards.get());
            assertTrue(drawnCards.get() > 0);
            
            // Final deck size should be initial + added - drawn
            int expectedSize = 50 + addedCards.get() - drawnCards.get();
            assertEquals(expectedSize, deck.size());
            System.out.println("PASSED");
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        } finally {
            tearDown();
            totalTests++;
        }
    }
    
    public void testDeckFileOutput() {
        System.out.println("Testing deck file output...");
        try {
            setUp();
            deck.addCard(new Card(1));
            deck.addCard(new Card(3));
            deck.addCard(new Card(3));
            deck.addCard(new Card(7));
            
            String filename = "deck1_output.txt";
            deck.writeToFile(filename);
            
            // Verify file was created and has correct content
            File outputFile = new File(filename);
            assertTrue(outputFile.exists());
            
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            String expectedContent = "deck1 contents: 1 3 3 7";
            // Normalize content by trimming whitespace
            String normalizedContent = content.trim();
            assertEquals(expectedContent, normalizedContent);
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
        System.out.println("Running CardDeck class tests...");
        testDeckCreation();
        testAddingCards();
        testFIFOBehavior();
        testDrawFromEmptyDeck();
        testDeckThreadSafety();
        testDeckFileOutput();
        
        System.out.println("CardDeck tests completed: " + passedTests + "/" + totalTests + " passed");
    }
    
    public int getTotalTests() {
        return totalTests;
    }
    
    public int getPassedTests() {
        return passedTests;
    }
    
    public static void main(String[] args) {
        CardDeckTest test = new CardDeckTest();
        test.runAllTests();
    }
}