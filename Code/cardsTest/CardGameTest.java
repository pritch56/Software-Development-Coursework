import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@DisplayName("Card Game Integration Tests")
public class CardGameTest {
    
    private final String testPackFile = "test_pack_integration.txt";
    private final ByteArrayOutputStream outputContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
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
    
    @AfterEach
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

    @Test
    @DisplayName("Should create valid card with positive denomination")
    public void testValidCardCreation() {
        Card card = new Card(5);
        assertEquals(5, card.getDenomination());
    }
    
    @Test
    @DisplayName("Should create card with zero denomination")
    public void testZeroDenominationCard() {
        Card card = new Card(0);
        assertEquals(0, card.getDenomination());
    }
    
    @Test
    @DisplayName("Should throw exception for negative denomination")
    public void testNegativeDenominationThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Card(-1));
    }
    
    @Test
    @DisplayName("Should implement card equality correctly")
    public void testCardEquality() {
        Card card1 = new Card(5);
        Card card2 = new Card(5);
        Card card3 = new Card(3);
        
        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card1, null);
        assertNotEquals(card1, "not a card");
    }
    
    @Test
    @DisplayName("Should maintain consistent hashCode")
    public void testCardHashCode() {
        Card card1 = new Card(7);
        Card card2 = new Card(7);
        
        assertEquals(card1.hashCode(), card2.hashCode());
    }
    
    @Test
    @DisplayName("Should return correct string representation")
    public void testCardToString() {
        Card card = new Card(10);
        assertEquals("10", card.toString());
    }
    
    @Test
    @DisplayName("Should maintain immutability under concurrent access")
    public void testCardImmutability() throws InterruptedException {
        Card card = new Card(42);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> assertEquals(42, card.getDenomination()));
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Should create deck with correct initial state")
    public void testCardDeckCreation() {
        CardDeck deck = new CardDeck(1);
        assertTrue(deck.isEmpty());
        assertEquals(0, deck.size());
    }
    
    @Test
    @DisplayName("Should maintain FIFO behavior for cards")
    public void testFIFOBehavior() {
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
    }
    
    @Test
    @DisplayName("Should return null when drawing from empty deck")
    public void testDrawFromEmptyDeck() {
        CardDeck deck = new CardDeck(1);
        assertNull(deck.drawCard());
    }
    
    @Test
    @DisplayName("Should handle concurrent operations safely")
    public void testConcurrentDeckOperations() throws InterruptedException {
        CardDeck deck = new CardDeck(1);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        final int operations = 100;
        
        // Add cards concurrently
        for (int i = 0; i < operations; i++) {
            final int cardValue = i;
            executor.submit(() -> deck.addCard(new Card(cardValue)));
        }
        
        // Wait for additions to complete
        Thread.sleep(100);
        
        // Draw cards concurrently
        for (int i = 0; i < operations; i++) {
            executor.submit(() -> deck.drawCard());
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        
        assertTrue(deck.isEmpty());
    }

    @Test
    @DisplayName("Should create player with correct initial state")
    public void testPlayerCreation() {
        CardDeck leftDeck = new CardDeck(1);
        CardDeck rightDeck = new CardDeck(2);
        AtomicBoolean gameEnded = new AtomicBoolean(false);
        
        Player player = new Player(1, leftDeck, rightDeck, gameEnded);
        assertNotNull(player);
        assertEquals(1, player.getPlayerNumber());
        assertFalse(player.isWinner());
    }
    
    @Test
    @DisplayName("Should handle initial hand setup correctly")
    public void testPlayerInitialHand() {
        CardDeck leftDeck = new CardDeck(1);
        CardDeck rightDeck = new CardDeck(2);
        AtomicBoolean gameEnded = new AtomicBoolean(false);
        
        Player player = new Player(1, leftDeck, rightDeck, gameEnded);
        
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(2));
        player.addCardToHand(new Card(3));
        player.addCardToHand(new Card(4));
        
        assertFalse(player.hasWinningHand());
    }
    
    @Test
    @DisplayName("Should detect winning condition correctly")
    public void testPlayerWinningCondition() {
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
    }

    @Test
    @DisplayName("Should handle immediate win scenario")
    public void testImmediateWinScenario() throws IOException {
        // Simulate user input 2 players, then the test pack file
        String input = "2\n" + testPackFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        try {
            // Run the game
            CardGame.main(new String[]{});
            
            // Verify output contains win message
            String output = outputContent.toString();
            
            // Check if any player won
            assertTrue(output.contains("wins"));
            
            // Verify output files were created
            assertTrue(new File("player1_output.txt").exists());
            assertTrue(new File("player2_output.txt").exists());
            assertTrue(new File("deck1_output.txt").exists());
            assertTrue(new File("deck2_output.txt").exists());
            
        } finally {
            // Restore System.in
            System.setIn(System.in);
        }
    }
    
    @Test
    @DisplayName("Should handle invalid pack file gracefully")
    public void testInvalidPackFileHandling() throws IOException {
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
        
        try {
            // Run the game
            CardGame.main(new String[]{});
            
            // Verify error message appears
            String output = outputContent.toString();
            assertTrue(output.contains("Error: Pack must contain exactly 16 cards"));
        } finally {
            // Clean up
            new File(invalidPackFile).delete();
            System.setIn(System.in);
        }
    }
}