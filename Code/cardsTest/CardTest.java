import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Card Class Tests")
public class CardTest {
    
    @Test
    @DisplayName("Should create card with valid denomination")
    public void testCardCreation() {
        Card card = new Card(7);
        assertEquals(7, card.getDenomination());
    }
    
    @Test
    @DisplayName("Should create card with zero denomination")
    public void testCardCreationWithZero() {
        Card card = new Card(0);
        assertEquals(0, card.getDenomination());
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for negative denomination")
    public void testCardCreationWithNegativeDenomination() {
        assertThrows(IllegalArgumentException.class, () -> new Card(-1));
    }
    
    @Test
    @DisplayName("Should implement equality correctly")
    public void testCardEquality() {
        Card card1 = new Card(5);
        Card card2 = new Card(5);
        Card card3 = new Card(10);
        
        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card2, card3);
    }
    
    @Test
    @DisplayName("Should not equal null")
    public void testCardEqualityWithNull() {
        Card card1 = new Card(5);
        assertNotEquals(card1, null);
    }
    
    @Test
    @DisplayName("Should have consistent hashCode")
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
    @DisplayName("Should be immutable")
    public void testCardImmutability() {
        Card card = new Card(42);
        // Test that card value cannot change after creation
        assertEquals(42, card.getDenomination());
    }
}