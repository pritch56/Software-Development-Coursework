import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardGame {
    private int numPlayers;
    private List<Card> pack;
    private List<Player> players;
    private List<CardDeck> decks;
    private AtomicBoolean gameWon;

    public CardGame() {
        this.pack = new ArrayList<>();
        this.players = new ArrayList<>();
        this.decks = new ArrayList<>();
        this.gameWon = new AtomicBoolean(false);
    }

    private boolean readAndValidatePack(String filename) {
        pack.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                try {
                    int denomination = Integer.parseInt(line);
                    if (denomination < 0) {
                        System.out.println("Error: Card denominations must be non-negative. Found: " + denomination);
                        return false;
                    }
                    pack.add(new Card(denomination));
                    lineCount++;
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format in pack file: " + line);
                    return false;
                }
            }
            
            // Check if pack has exactly 8n cards
            int expectedCards = 8 * numPlayers;
            if (lineCount != expectedCards) {
                System.out.println("Error: Pack must contain exactly " + expectedCards + 
                                 " cards for " + numPlayers + " players. Found: " + lineCount + " cards.");
                return false;
            }
            
            return true;
            
        } catch (IOException e) {
            System.out.println("Error reading pack file: " + e.getMessage());
            return false;
        }
    }
    
    private int getValidPlayerCount(Scanner scanner) {
        while (true) {
            System.out.print("Please enter the number of players: ");
            try {
                String input = scanner.nextLine().trim();
                int players = Integer.parseInt(input);
                if (players > 0) {
                    return players;
                } else {
                    System.out.println("Number of players must be positive. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a positive integer.");
            }
        }
    }

    private String getValidPackFile(Scanner scanner) {
        while (true) {
            System.out.print("Please enter location of pack to load: ");
            String filename = scanner.nextLine().trim();
            
            if (readAndValidatePack(filename)) {
                System.out.println("Pack loaded successfully!");
                return filename;
            } else {
                System.out.println("Invalid pack file. Please try again.");
            }
        }
    }

    private void distributeCardsToPlayers() {
        int cardIndex = 0;
        
        // Distribute 4 cards to each player in round-robin fashion
        for (int round = 0; round < 4; round++) {
            for (int playerIndex = 0; playerIndex < numPlayers; playerIndex++) {
                if (cardIndex < pack.size()) {
                    players.get(playerIndex).addCardToHand(pack.get(cardIndex++));
                }
            }
        }
    }

    private void fillDecks() {
        int cardIndex = 4 * numPlayers; // Start after player cards
        
        // Distribute remaining cards to decks in round-robin fashion
        while (cardIndex < pack.size()) {
            for (int deckIndex = 0; deckIndex < numPlayers && cardIndex < pack.size(); deckIndex++) {
                decks.get(deckIndex).addCard(pack.get(cardIndex++));
            }
        }
    }

    private void createPlayersAndDecks() {
        // Create decks
        for (int i = 1; i <= numPlayers; i++) {
            decks.add(new CardDeck(i));
        }
        
        // Create players with ring topology
        for (int i = 1; i <= numPlayers; i++) {
            CardDeck drawDeck = decks.get(i - 1); // Player i draws from deck i
            CardDeck discardDeck = decks.get(i % numPlayers); // Player i discards to deck i+1 (with wraparound)
            
            Player player = new Player(i, drawDeck, discardDeck, gameWon);
            players.add(player);
        }
    }

    private void startGame() {
        System.out.println("Game starting with " + numPlayers + " players...");
        
        // Start all player threads
        for (Player player : players) {
            player.start();
        }
    }

    private void waitForGameEnd() {
        try {
            // Wait for all player threads to complete
            for (Player player : players) {
                player.join();
            }
            
            // Find the winner and notify all players
            Player winner = null;
            for (Player player : players) {
                if (player.isWinner()) {
                    winner = player;
                    break;
                }
            }
            
            // Notify all non-winning players about the game end
            if (winner != null) {
                for (Player player : players) {
                    if (!player.isWinner()) {
                        player.handleGameEnd(winner.getPlayerNumber());
                    }
                }
            }
            
            // Write deck contents to files
            writeDeckOutputFiles();
            
            System.out.println("Game completed successfully!");
            
        } catch (InterruptedException e) {
            System.err.println("Game interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void writeDeckOutputFiles() {
        for (int i = 0; i < decks.size(); i++) {
            CardDeck deck = decks.get(i);
            String filename = "deck" + (i + 1) + "_output.txt";
            deck.writeToFile(filename);
        }
    }

    public static void main(String[] args) {
        CardGame game = new CardGame();
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Get valid input from user
            game.numPlayers = game.getValidPlayerCount(scanner);
            game.getValidPackFile(scanner);
            
            // Set up the game
            game.createPlayersAndDecks();
            game.distributeCardsToPlayers();
            game.fillDecks();
            
            // Start and manage the game
            game.startGame();
            game.waitForGameEnd();
            
        } catch (Exception e) {
            System.err.println("An error occurred during the game: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}