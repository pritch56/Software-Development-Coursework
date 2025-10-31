import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

@DisplayName("Player Class Tests")
public class PlayerTest {
    
    private Player player;
    private CardDeck drawDeck;
    private CardDeck discardDeck;
    private AtomicInteger winningPlayer;
    @BeforeEach
    public void setUp() {
        drawDeck = new CardDeck(1);
        discardDeck = new CardDeck(2);
        winningPlayer = new AtomicInteger(0);
        player = new Player(1, drawDeck, discardDeck, winningPlayer);
    }
    
    @AfterEach
    public void tearDown() {
        File playerFile = new File("player1_output.txt");
        if (playerFile.exists()) {
            playerFile.delete();
        }
    }
    @Test
    @DisplayName("create player with right number and state")
    public void testPlayerCreation() {
        assertEquals(1, player.getPlayerNumber());
    }
    
    @Test
    @DisplayName("add cards to hand")
    public void testAddingCardsToHand() {
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(2));
        player.addCardToHand(new Card(3));
        player.addCardToHand(new Card(4));
        
        String hand = player.getHandAsString();
        assertEquals("1 2 3 4", hand);
    }
    
    @Test
    @DisplayName("detect winning hand with all of same value")
    public void testWinningHandDetection() {
        player.addCardToHand(new Card(5));
        player.addCardToHand(new Card(5));
        player.addCardToHand(new Card(5));
        player.addCardToHand(new Card(5));
        
        assertTrue(player.hasWinningHand());
    }
    
    @Test
    @DisplayName("dont detect winning hand with dif values")
    public void testNonWinningHandDetection() {
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(2));
        player.addCardToHand(new Card(3));
        player.addCardToHand(new Card(4));
        
        assertFalse(player.hasWinningHand());
    }
    
    @Test
    @DisplayName("don't detect winning hand with incomplete hand")
    public void testIncompleteHandDetection() {
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        
        //only 3/4 cards
        
        assertFalse(player.hasWinningHand());
    }
    
    @Test
    @DisplayName("ensure hand size limited to 4 cards")
    public void testHandSizeLimit() {
        // attempt to add more than 4
        for (int i = 1; i <= 6; i++) {
            player.addCardToHand(new Card(i));
        }
        
        String hand = player.getHandAsString();
        String[] cards = hand.split(" ");
        assertEquals(4, cards.length);
    }
    
    @Test
    @DisplayName("create output file with initial hand")
    public void testFileOutputCreation() throws IOException, InterruptedException {
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(2));
        player.addCardToHand(new Card(3));
        player.addCardToHand(new Card(4));
        
        player.writeInitialHand();
    
        Thread.sleep(50);
        
        // check output
        File outputFile = new File("player1_output.txt");
        assertTrue(outputFile.exists());
        
        String content = new String(Files.readAllBytes(Paths.get("player1_output.txt")));
        assertTrue(content.contains("player 1 initial hand"));
        assertTrue(content.contains("1 2 3 4"));
    }
}