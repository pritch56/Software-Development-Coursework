import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class CardGame {
    private int numPlayers;
    private List<Card> pack;
    private List<Player> players;
    private List<CardDeck> decks;
    private AtomicInteger winningPlayer;

    public CardGame() {
        this.pack = new ArrayList<>();
        this.players = new ArrayList<>();
        this.decks = new ArrayList<>();
        this.winningPlayer = new AtomicInteger(0); // 0 means no winner yet
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
            
            //check if pack has 8n cards
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
        
        //give 4 each in round robin
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
        
        //remaining cards to the decks by round robin
        while (cardIndex < pack.size()) {
            for (int deckIndex = 0; deckIndex < numPlayers && cardIndex < pack.size(); deckIndex++) {
                decks.get(deckIndex).addCard(pack.get(cardIndex++));
            }
        }
    }

    private void createPlayersAndDecks() {
        for (int i = 1; i <= numPlayers; i++) {
            decks.add(new CardDeck(i));
        }
        
        // make players via ring topology
        for (int i = 1; i <= numPlayers; i++) {
            CardDeck drawDeck = decks.get(i - 1);
            CardDeck discardDeck = decks.get(i % numPlayers); //wrapping structure
            
            Player player = new Player(i, drawDeck, discardDeck, winningPlayer);
            players.add(player);
        }
    }

    private void startGame() {
        System.out.println("Game starting with " + numPlayers + " players...");
        
        for (Player player : players) {
            player.start();
        }
    }

    private void waitForGameEnd() {
        try {
            //wait for other threads to complete
            for (Player player : players) {
                player.join();
            }
            
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
            game.numPlayers = game.getValidPlayerCount(scanner);
            game.getValidPackFile(scanner);

            game.createPlayersAndDecks();
            game.distributeCardsToPlayers();
            game.fillDecks();
            
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