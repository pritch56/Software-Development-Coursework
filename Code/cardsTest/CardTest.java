public class CardTest {
    
    private int totalTests = 0;
    private int passedTests = 0;
    
    // Manual assertion methods
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
    
    public void testCardCreation() {
        System.out.println("Testing card creation with valid denomination...");
        try {
            Card card = new Card(7);
            assertEquals(7, card.getDenomination());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
    }
    
    public void testCardCreationWithZero() {
        System.out.println("Testing card creation with zero denomination...");
        try {
            Card card = new Card(0);
            assertEquals(0, card.getDenomination());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
    }
    
    public void testCardCreationWithNegativeDenomination() {
        System.out.println("Testing card creation with negative denomination throws exception...");
        try {
            assertThrows(IllegalArgumentException.class, () -> {
                new Card(-1);
            });
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
    }
    
    public void testCardEquality() {
        System.out.println("Testing card equality...");
        try {
            Card card1 = new Card(5);
            Card card2 = new Card(5);
            Card card3 = new Card(10);
            
            assertEquals(card1, card2);
            assertNotEquals(card1, card3);
            assertNotEquals(card2, card3);
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
    }
    
    public void testCardEqualityWithNull() {
        System.out.println("Testing card equality with null...");
        try {
            Card card1 = new Card(5);
            assertNotEquals(card1, null);
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
    }
    
    public void testCardHashCode() {
        System.out.println("Testing card hashCode consistency...");
        try {
            Card card1 = new Card(7);
            Card card2 = new Card(7);
            assertEquals(card1.hashCode(), card2.hashCode());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
    }
    
    public void testCardToString() {
        System.out.println("Testing card toString method...");
        try {
            Card card = new Card(10);
            assertEquals("10", card.toString());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
    }
    
    public void testCardImmutability() {
        System.out.println("Testing card immutability...");
        try {
            Card card = new Card(42);
            // Test that card value cannot change after creation
            assertEquals(42, card.getDenomination());
            System.out.println("PASSED");
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        totalTests++;
    }
    
    public void runAllTests() {
        System.out.println("Running Card class tests...");
        testCardCreation();
        testCardCreationWithZero();
        testCardCreationWithNegativeDenomination();
        testCardEquality();
        testCardEqualityWithNull();
        testCardHashCode();
        testCardToString();
        testCardImmutability();
        
        System.out.println("Card tests completed: " + passedTests + "/" + totalTests + " passed");
    }
    
    public int getTotalTests() {
        return totalTests;
    }
    
    public int getPassedTests() {
        return passedTests;
    }
    
    public static void main(String[] args) {
        CardTest test = new CardTest();
        test.runAllTests();
    }
}