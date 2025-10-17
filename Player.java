import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Player extends Thread {
    private final int playerNumber;
    private final List<Card> hand;
    private final CardDeck drawDeck;
    private final CardDeck discardDeck;
    private final ReentrantLock handLock;
    private final AtomicBoolean gameWon;
    private final AtomicBoolean hasWon;
    private PrintWriter outputWriter;
    private final String outputFilename;
    
    public Player(int playerNumber, CardDeck drawDeck, CardDeck discardDeck, AtomicBoolean gameWon) {
        this.playerNumber = playerNumber;
        this.hand = new ArrayList<>(4);
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
        this.handLock = new ReentrantLock();
        this.gameWon = gameWon;
        this.hasWon = new AtomicBoolean(false);
        this.outputFilename = "player" + playerNumber + "_output.txt";
        
        try {
            this.outputWriter = new PrintWriter(new FileWriter(outputFilename));
        } catch (IOException e) {
            System.err.println("Error creating output file for player " + playerNumber + ": " + e.getMessage());
        }
    }

    public void addCardToHand(Card card) {
        handLock.lock();
        try {
            if (hand.size() < 4) {
                hand.add(card);
            }
        } finally {
            handLock.unlock();
        }
    }

    public boolean hasWinningHand() {
        handLock.lock();
        try {
            if (hand.size() != 4) return false;
            
            int firstCardValue = hand.get(0).getDenomination();
            for (Card card : hand) {
                if (card.getDenomination() != firstCardValue) {
                    return false;
                }
            }
            return true;
        } finally {
            handLock.unlock();
        }
    }

    public String getHandAsString() {
        handLock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hand.size(); i++) {
                if (i > 0) sb.append(" ");
                sb.append(hand.get(i).getDenomination());
            }
            return sb.toString();
        } finally {
            handLock.unlock();
        }
    }

    public void writeInitialHand() {
        if (outputWriter != null) {
            outputWriter.println("player " + playerNumber + " initial hand " + getHandAsString());
            outputWriter.flush();
        }
    }

    private int selectCardToDiscard() {
        handLock.lock();
        try {
            // First, try to discard a card that is not the preferred denomination
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).getDenomination() != playerNumber) {
                    return i;
                }
            }
            
            // If all cards are preferred denomination, discard the first one
            // This prevents infinite holding of cards
            return 0;
        } finally {
            handLock.unlock();
        }
    }

    private boolean performTurn() {
        Card drawnCard = drawDeck.drawCard();
        if (drawnCard == null) {
            return false; // Deck is empty
        }
        
        handLock.lock();
        try {
            // Log draw action
            if (outputWriter != null) {
                outputWriter.println("player " + playerNumber + " draws a " + 
                                   drawnCard.getDenomination() + " from deck " + drawDeck.getDeckNumber());
                outputWriter.flush();
            }
            
            // Add drawn card to hand
            hand.add(drawnCard);
            
            // Select and remove card to discard
            int discardIndex = selectCardToDiscard();
            Card discardedCard = hand.remove(discardIndex);
            
            // Discard to the next deck
            discardDeck.discardCard(discardedCard);
            
            // Log discard action
            if (outputWriter != null) {
                outputWriter.println("player " + playerNumber + " discards a " + 
                                   discardedCard.getDenomination() + " to deck " + discardDeck.getDeckNumber());
                outputWriter.println("player " + playerNumber + " current hand is " + getHandAsString());
                outputWriter.flush();
            }
            
            return true;
        } finally {
            handLock.unlock();
        }
    }

    private void declareVictory() {
        if (gameWon.compareAndSet(false, true)) {
            hasWon.set(true);
            System.out.println("player " + playerNumber + " wins");
            
            if (outputWriter != null) {
                outputWriter.println("player " + playerNumber + " wins");
                outputWriter.println("player " + playerNumber + " exits");
                outputWriter.println("player " + playerNumber + " final hand: " + getHandAsString());
                outputWriter.flush();
            }
        }
    }

    public void handleGameEnd(int winningPlayer) {
        if (outputWriter != null && winningPlayer != playerNumber) {
            outputWriter.println("player " + winningPlayer + " has informed player " + 
                               playerNumber + " that player " + winningPlayer + " has won");
            outputWriter.println("player " + playerNumber + " exits");
            outputWriter.println("player " + playerNumber + " final hand: " + getHandAsString());
            outputWriter.flush();
        }
    }

    @Override
    public void run() {
        // Write initial hand
        writeInitialHand();
        
        // Check for immediate win
        if (hasWinningHand()) {
            declareVictory();
            closeOutputFile();
            return;
        }
        
        // Main game loop
        while (!gameWon.get() && !Thread.currentThread().isInterrupted()) {
            try {
                // Perform a turn (draw and discard)
                if (!performTurn()) {
                    // If we can't draw, wait a bit and try again
                    Thread.sleep(10);
                    continue;
                }
                
                // Check for win condition after each turn
                if (hasWinningHand()) {
                    declareVictory();
                    break;
                }
                
                // Small delay to prevent overwhelming the system
                Thread.sleep(1);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // If game was won by another player, handle gracefully
        if (gameWon.get() && !hasWon.get()) {
            // We'll handle this in the CardGame class when notifying all players
        }
        
        closeOutputFile();
    }

    private void closeOutputFile() {
        if (outputWriter != null) {
            outputWriter.close();
        }
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean isWinner() {
        return hasWon.get();
    }
}