import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

@DisplayName("Player Class Tests")
public class PlayerTest {
    
    private Player player;
    private CardDeck drawDeck;
    private CardDeck discardDeck;
    private AtomicBoolean gameWon;
    @BeforeEach
    public void setUp() {
        drawDeck = new CardDeck(1);
        discardDeck = new CardDeck(2);
        gameWon = new AtomicBoolean(false);
        player = new Player(1, drawDeck, discardDeck, gameWon);
    }
    
    @AfterEach
    public void tearDown() {
        // Clean up test files
        File playerFile = new File("player1_output.txt");
        if (playerFile.exists()) {
            playerFile.delete();
        }
    }
    @Test
    @DisplayName("Should create player with correct number and initial state")
    public void testPlayerCreation() {
        assertEquals(1, player.getPlayerNumber());
        assertFalse(player.isWinner());
    }
    
    @Test
    @DisplayName("Should add cards to hand correctly")
    public void testAddingCardsToHand() {
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(2));
        player.addCardToHand(new Card(3));
        player.addCardToHand(new Card(4));
        
        String hand = player.getHandAsString();
        assertEquals("1 2 3 4", hand);
    }
    
    @Test
    @DisplayName("Should detect winning hand with all same values")
    public void testWinningHandDetection() {
        // Add four cards of the same value
        player.addCardToHand(new Card(5));
        player.addCardToHand(new Card(5));
        player.addCardToHand(new Card(5));
        player.addCardToHand(new Card(5));
        
        assertTrue(player.hasWinningHand());
    }
    
    @Test
    @DisplayName("Should not detect winning hand with different values")
    public void testNonWinningHandDetection() {
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(2));
        player.addCardToHand(new Card(3));
        player.addCardToHand(new Card(4));
        
        assertFalse(player.hasWinningHand());
    }
    
    @Test
    @DisplayName("Should not detect winning hand with incomplete hand")
    public void testIncompleteHandDetection() {
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        // Only 3 cards, not 4
        
        assertFalse(player.hasWinningHand());
    }
    
    @Test
    @DisplayName("Should limit hand size to 4 cards")
    public void testHandSizeLimit() {
        // Try to add more than 4 cards
        for (int i = 1; i <= 6; i++) {
            player.addCardToHand(new Card(i));
        }
        
        // Should only have 4 cards
        String hand = player.getHandAsString();
        String[] cards = hand.split(" ");
        assertEquals(4, cards.length);
    }
    
    @Test
    @DisplayName("Should create output file with initial hand")
    public void testFileOutputCreation() throws IOException, InterruptedException {
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
    }
}