import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Player extends Thread {
    private final int playerNumber;
    private final List<Card> hand;
    private final CardDeck drawDeck;
    private final CardDeck discardDeck;
    private final ReentrantLock handLock;
    private final AtomicInteger winningPlayer;
    private PrintWriter outputWriter;
    private final String outputFilename;
    
    public Player(int playerNumber, CardDeck drawDeck, CardDeck discardDeck, AtomicInteger winningPlayer) {
        this.playerNumber = playerNumber;
        this.hand = new ArrayList<>(4);
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
        this.handLock = new ReentrantLock();
        this.winningPlayer = winningPlayer;
        this.outputFilename = "player" + playerNumber + "_output.txt";
        
        try {
            this.outputWriter = new PrintWriter(new FileWriter(outputFilename));
        } catch (IOException e) {
            System.err.println("fai creating output file for player " + playerNumber + ": " + e.getMessage());
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
            // try to get rid of crap cards first
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).getDenomination() != playerNumber) {
                    return i;
                }
            }
            
            // if no rubbish cards can return any
            return 0;
        } finally {
            handLock.unlock();
        }
    }

    private boolean performTurn() {
        Card drawnCard = drawDeck.drawCard();
        // if deck's empty can't go (shouldn't happen though)
        if (drawnCard == null) {
            return false;
        }
        
        handLock.lock();
        try {
            if (outputWriter != null) {
                outputWriter.println("player " + playerNumber + " draws a " + 
                                   drawnCard.getDenomination() + " from deck " + drawDeck.getDeckNumber());
                outputWriter.flush();
            }
            
            hand.add(drawnCard);
            
            int discardIndex = selectCardToDiscard();
            Card discardedCard = hand.remove(discardIndex);
            
            discardDeck.discardCard(discardedCard);
            
            // log for debugging just incase yk
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
        if (winningPlayer.compareAndSet(0, playerNumber)) {
            System.out.println("player " + playerNumber + " wins");
            
            if (outputWriter != null) {
                outputWriter.println("player " + playerNumber + " wins");
                outputWriter.println("player " + playerNumber + " exits");
                outputWriter.println("player " + playerNumber + " final hand: " + getHandAsString());
                outputWriter.flush();
            }
        }
    }

    private void handleGameEnd(int winner) {
        if (outputWriter != null && winner != playerNumber) {
            outputWriter.println("player " + winner + " has informed player " + 
                               playerNumber + " that player " + winner + " has won");
            outputWriter.println("player " + playerNumber + " exits");
            outputWriter.println("player " + playerNumber + " final hand: " + getHandAsString());
            outputWriter.flush();
        }
    }

    @Override
    public void run() {
        writeInitialHand();
        
        // check if won already (does say in spec not do but hey)
        if (hasWinningHand()) {
            declareVictory();
            closeOutputFile();
            return;
        }
        
        while (winningPlayer.get() == 0 && !Thread.currentThread().isInterrupted()) {
            try {
                if (!performTurn()) {
                    // if unable to draw (probably due to lock) then wait and retry
                    Thread.sleep(10);
                    continue;
                }
                
                if (hasWinningHand()) {
                    declareVictory();
                    break;
                }
                
                // thread saftey, maybe someone else won
                int winner = winningPlayer.get();
                if (winner != 0 && winner != playerNumber) {
                    handleGameEnd(winner);
                    break;
                }
                
                Thread.sleep(1);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // check again incase exit due to an interruption
        int winner = winningPlayer.get();
        if (winner != 0 && winner != playerNumber) {
            handleGameEnd(winner);
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
}