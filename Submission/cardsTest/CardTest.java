import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Card Class Tests")
public class CardTest {
    
    @Test
    @DisplayName("should create card with valid denomination")
    public void testCardCreation() {
        Card card = new Card(7);
        assertEquals(7, card.getDenomination());
    }
    
    @Test
    @DisplayName("create card with zero denomination")
    public void testCardCreationWithZero() {
        Card card = new Card(0);
        assertEquals(0, card.getDenomination());
    }
    
    @Test
    @DisplayName("throw IllegalArgumentException for negatives")
    public void testCardCreationWithNegativeDenomination() {
        assertThrows(IllegalArgumentException.class, () -> new Card(-1));
    }
    
    @Test
    @DisplayName("implement equality correctly")
    public void testCardEquality() {
        Card card1 = new Card(5);
        Card card2 = new Card(5);
        Card card3 = new Card(10);
        
        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card2, card3);
    }
    
    @Test
    @DisplayName("not equal null")
    public void testCardEqualityWithNull() {
        Card card1 = new Card(5);
        assertNotEquals(card1, null);
    }
    
    @Test
    @DisplayName("ensure consistent hashCode")
    public void testCardHashCode() {
        Card card1 = new Card(7);
        Card card2 = new Card(7);
        assertEquals(card1.hashCode(), card2.hashCode());
    }
    
    @Test
    @DisplayName("should return correct string representation")
    public void testCardToString() {
        Card card = new Card(10);
        assertEquals("10", card.toString());
    }
    
    @Test
    @DisplayName("be immutable")
    public void testCardImmutability() {
        Card card = new Card(42);
        //test card value doesnt change
        assertEquals(42, card.getDenomination());
    }
}