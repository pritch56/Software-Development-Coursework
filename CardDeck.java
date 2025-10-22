import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CardDeck {
    private final ConcurrentLinkedQueue<Card> cards;
    private final int deckNumber;
    private final ReentrantLock lock;
    
    public CardDeck(int deckNumber) {
        this.cards = new ConcurrentLinkedQueue<>();
        this.deckNumber = deckNumber;
        this.lock = new ReentrantLock();
    }

    public Card drawCard() {
        lock.lock();
        try {
            return cards.poll(); // Returns null if empty
        } finally {
            lock.unlock();
        }
    }

    public void discardCard(Card card) {
        lock.lock();
        try {
            cards.offer(card);
        } finally {
            lock.unlock();
        }
    }

    public void addCard(Card card) {
        cards.offer(card);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    public int getDeckNumber() {
        return deckNumber;
    }

    public void writeToFile(String filename) {
        lock.lock();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            StringBuilder contents = new StringBuilder();
            contents.append("deck").append(deckNumber).append(" contents:");
            
            for (Card card : cards) {
                contents.append(" ").append(card.getDenomination());
            }
            
            writer.println(contents.toString());
        } catch (IOException e) {
            System.err.println("Error writing deck " + deckNumber + " to file: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Deck ").append(deckNumber).append(": [");
            boolean first = true;
            for (Card card : cards) {
                if (!first) sb.append(", ");
                sb.append(card.getDenomination());
                first = false;
            }
            sb.append("]");
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }
}