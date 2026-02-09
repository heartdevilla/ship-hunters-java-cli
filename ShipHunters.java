/* Author: De Villa, Princes Heart F.
 *  Date: November 2025
 *  Program: BSCpE101A
 *  Project: Battleship Game "Ship Hunters" using Java
 *
 * Game Description:
 *  A console-based game style. Two players (a human and an opponent) each have a board and a
 * fleet of ships. Players take turns with a one shot per turn until one player's ship are all sunk.
 * The opponent uses a simple hunt and target strategy to try to find and sink ships.
 *
 * Notes:
 * - The ShipHunters class contains console utilities like color/theme, and clear the terminal.
 * - Board handles the 10x10 grid, ship placement and shooting logic.
 * - Ship represents an individual ship and tracks its positions and hits.
 * - Player wraps a board and the player's fleet, and track stats.
 * - AI provides a simple opponent using availableTargets and a targetQueue.
 * - GameEngine it contains the phases, setupPhase (placing fleet) and battlePhase (take turns).
 */

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ShipHunters {
    private static final Scanner scn = new Scanner(System.in);

    //public ANSI color constants so all classes in this file can use consistent colors.
    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m"; // purple/magenta
    public static final String ANSI_PINK   = "\u001B[95m"; // bright pink
    public static final String ANSI_BLUE = "\u001B[34m"; // blue

    //application entry point. Sets console theme  and show welcome.
    //show menu repeatedly until the user exits.
    public static void main(String[] args){
        setConsoleThemePurplePink(); //try to tint console output
        clearTerminal();   // clear the screen for welcome box
        showWelcome();     // print game title and subtitle

        while (true){
            showMenu(); // main menu options
            int choice = getUserChoice(); //read and parse user's menu input

            switch (choice){
                case 1:
                    playGame(); // start a new game
                    break;
                case 2:
                    showHighScores(); // show scores, but disregard this because the showHighScores options removed.
                    break;
                case 3:
                    resetConsoleColors(); // restore console colors on exit
                    System.out.println("\nThank you for playing SHIPS HUNTER!");
                    scn.close(); //close scanner before exit
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please Try again.");


            }
        }
    }

    private static void showWelcome() {
        String title = "S H I P S  H U N T E R";
        String subtitle = "Deploy Your Fleet, Sink All Ships";

        int padding = 4; // spaces between text and vertical border
        int innerWidth = Math.max(title.length(), subtitle.length());
        int width = innerWidth + padding * 2; // number of '═' characters inside the box

        // build top/bottom/empty line strings for the  boxed title
        String top = "╔" + "═".repeat(width) + "╗";
        String bottom = "╚" + "═".repeat(width) + "╝";
        String emptyLine = "║" + " ".repeat(width) + "║";

        System.out.println();
        System.out.println(top);
        System.out.println(emptyLine);

        // Center title
        int left = (width - title.length()) / 2;
        int right = width - left - title.length();
        System.out.println("║" + " ".repeat(left) + title + " ".repeat(right) + "║");

        // Center subtitle
        left = (width - subtitle.length()) / 2;
        right = width - left - subtitle.length();
        System.out.println("║" + " ".repeat(left) + subtitle + " ".repeat(right) + "║");

        System.out.println(emptyLine);
        System.out.println(bottom);
        System.out.println();
    }

    private static void showMenu() {
        // Note: Most consoles can't detect raw ESC keypress without extra libraries,
        // so the literal strings "esc" / "exit" and whitespace only input as exit signals.
        System.out.println("\n⫘⫘⫘⫘⫘⫘⫘⫘⫘  MAIN MENU ⫘⫘⫘⫘⫘⫘⫘⫘⫘\n");
        System.out.println("Press Enter to PLAY\n");
        System.out.println("Press whitespace then Enter to EXIT"); // or type exit or esc
        System.out.println("\n⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘⫘\n\n");
        System.out.print("Your input: ");
    }
    /* Reads the user's menu input and maps it to a numeric choice:
    * - empty line -> 1 (play)
    * - whitespace-only -> 3 (exit)
    * - "esc" or "exit" -> 3 (exit)
    * - integer parse for numeric entries
    * - invalid parse -> -1
    */
    private static int getUserChoice() {
        try {
            String line = scn.nextLine();
            if (line == null) return -1;

            // If user just pressed Enter (empty string) -> start game (choice 1)
            if (line.length() == 0) {
                return 1;
            }

            // If input contains only whitespace (space/tab etc.) -> exit (choice 3)
            if (line.trim().length() == 0) {
                return 3;
            }

            String trimmed = line.trim();

            // Accept "esc" or "exit" (case-insensitive) to exit (choice 3)
            if (trimmed.equalsIgnoreCase("esc") || trimmed.equalsIgnoreCase("exit")) {
                return 3;
            }

            // Allow numeric entry as before (e.g., "1" to play)
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // clears the terminal and starts a new game
    private static void playGame() {
        clearTerminal();
        startNewGame();
    }

    private static void startNewGame() {
        System.out.print("\nEnter your name: ");
        String playerName = scn.nextLine();

        Player player = new Player(playerName, false); // human player
        Player aiOpp = new Player("AI Opponent", true); // AI Opponent

        GameEngine game = new GameEngine(player, aiOpp);
        game.setupPhase();
        game.battlePhase();
    }

    private static void showHighScores() {
        clearTerminal();
            System.out.println("\n========== HIGH SCORES ==========");
            System.out.println("\nPress Enter to continue...");
            scn.nextLine();
    }

    public static void clearTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                // Run 'cls' command to clear windows console
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // ANSI clear for Unix-like terminals
                System.out.print("\\033[H\\033[2J");
                System.out.flush();
            }
        } catch (Exception e){
            //if clearing doesn't work, just print new lines
            for (int i=0; i<50; i++){
                System.out.println();
            }
        }
    }
    // allows other classes to reuse the single scanner.
    public static Scanner getScanner() {
        return scn;
    }

    // Set entire console theme to a purple/pink-ish combination.
    public static void setConsoleThemePurplePink() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            try {
                // Use cmd color: background (first) and foreground (second)
                // 0 = Black, 5 = Purple, D = Light Purple (bright magenta)
                // "0D" = black background + light purple text (good contrast)
                new ProcessBuilder("cmd", "/c", "color", "0D").inheritIO().start().waitFor();
            } catch (Exception e) {
                // ignore if it fails — we'll still continue without breaking the game
            }
        } else {
            final String ANSI_BG_MAGENTA = "\u001B[45m"; // magenta background
            final String ANSI_FG_BRIGHT_MAGENTA = "\u001B[95m";
            System.out.print(ANSI_BG_MAGENTA + ANSI_FG_BRIGHT_MAGENTA);
        }
    }

    // Reset colors back to default. Call when quitting the program.
    public static void resetConsoleColors() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            try {
                // restore to default (07 = black background, gray text)
                new ProcessBuilder("cmd", "/c", "color", "07").inheritIO().start().waitFor();
            } catch (Exception e) {
                // ignore
            }
        } else {
            System.out.print("\u001B[0m"); // ANSI reset
        }
    }
}

class Board{
    private static final int size = 10;
    private static final char water = '☐';
    private static final char shipCharac = '⬤';
    private static final char hit = '◉';
    private static final char miss = '☒';

    //grid stores the visible representation for each cell
    private char[][] grid;
    //shipPositions stores where ship pieces actually are (used for placement and hits)
    private boolean[][] shipPositions;

    // constructor initializes the grid arrays and populates with water
    public Board(){
        grid = new char[size][size];
        shipPositions = new boolean[size][size];
        initializeBoard();
    }

    private void initializeBoard(){
        for(int i=0; i<size; i++){
            for (int a=0; a<size; a++){
                grid[i][a] = water;
                shipPositions[i][a] = false;
            }
        }
    }

    public void display(boolean showShips){
        String BLUE   = ShipHunters.ANSI_BLUE;   // border color
        String PINK   = ShipHunters.ANSI_PINK;   // letters / numbers / hits
        String PURPLE = ShipHunters.ANSI_PURPLE; // ships / misses
        String RESET  = ShipHunters.ANSI_RESET;

        int rowLabelWidth = 3;                // "10 " -> 3 chars (two digits + space)
        int cellWidth = 2;                    // each cell prints: symbol + space
        int innerWidth = size * cellWidth + 1; // +1 for the leading space after left border (matches row printing)

        // Print column letters aligned to each cell
        System.out.println();
        // Number of spaces before first letter: row label width + left border char + trailing space ("│ ")
        int lettersPrefix = rowLabelWidth + 2; // rowLabelWidth (e.g. "10 ") + "│ " (2 visible chars)
        System.out.print(" ".repeat(Math.max(0, lettersPrefix)));
        for (char c = 'A'; c < 'A' + size; c++) {
            System.out.print(PINK + c + RESET + " ");
        }
        System.out.println();

        // Top border aligned to row label width
        System.out.println(" ".repeat(rowLabelWidth) + BLUE + "┌" + "─".repeat(innerWidth) + "┐" + RESET);

        for (int i = 0; i < size; i++) {
            String rowNum = String.format("%2d", (i + 1)); // width 2
            // Print row number (pink), then a space then left border (blue) then a space to match inner layout
            System.out.print(PINK + rowNum + RESET + " " + BLUE + "│ " + RESET);

            for (int a = 0; a < size; a++) {
                char cell = grid[i][a];
                String out;
                if (!showShips && cell == shipCharac) {
                    out = String.valueOf(water);
                } else {
                    if (cell == shipCharac) {
                        out = PURPLE + shipCharac + RESET;      // visible ship = purple
                    } else if (cell == hit) {
                        out = PINK + hit + RESET;              // hit = pink
                    } else if (cell == miss) {
                        out = PURPLE + miss + RESET;           // miss = purple
                    } else {
                        out = String.valueOf(water);           // water = default
                    }
                }
                System.out.print(out + " ");
            }

            // closing vertical border in BLUE
            System.out.println(BLUE + "│" + RESET);
        }

        // Bottom border in BLUE
        System.out.println(" ".repeat(rowLabelWidth) + BLUE + "└" + "─".repeat(innerWidth) + "┘" + RESET);
    }

    public boolean placeShip(Ship ship, String startCoord, boolean horizontal) {
        int[] coords = parseCoordinate(startCoord);
        if (coords == null) return false;

        int row = coords[0];
        int col = coords[1];
        int length = ship.getLength();

        if (horizontal) {
            if (col + length > size) return false;
        } else {
            if (row + length > size) return false;
        }

        for (int i = 0; i < length; i++) {
            int checkRow = horizontal ? row : row + i;
            int checkCol = horizontal ? col + i : col;
            if (shipPositions[checkRow][checkCol]) {
                return false;
            }
        }

        for (int i = 0; i < length; i++) {
            int placeRow = horizontal ? row : row + i;
            int placeCol = horizontal ? col + i : col;
            grid[placeRow][placeCol] = shipCharac;
            shipPositions[placeRow][placeCol] = true;
            ship.addPosition(placeRow, placeCol);
        }

        return true;
    }

    public String shoot(String coordinate) {
        int[] coords = parseCoordinate(coordinate);
        if (coords == null) return "INVALID";

        int row = coords[0];
        int col = coords[1];

        if (grid[row][col] == hit || grid[row][col] == miss) {
            return "ALREADY_SHOT";
        }

        if (shipPositions[row][col]) {
            grid[row][col] = hit;
            return "HIT";
        } else {
            grid[row][col] = miss;
            return "MISS";
        }
    }

    public boolean allShipsSunk() {
        for (int i = 0; i < size; i++) {
            for (int a = 0; a < size; a++) {
                if (grid[i][a] == shipCharac) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[] parseCoordinate(String coord) {
        if (coord == null || coord.length() < 2 || coord.length() > 3) {
            return null;
        }

        coord = coord.toUpperCase();
        char colChar = coord.charAt(0);
        String rowStr = coord.substring(1);

        if (colChar < 'A' || colChar > 'J') {
            return null;
        }

        try {
            int row = Integer.parseInt(rowStr) - 1;
            int col = colChar - 'A';

            if (row < 0 || row >= size || col < 0 || col >= size) {
                return null;
            }

            return new int[]{row, col};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int getSize() {
        return size;
    }
}

/**
 * Ship class representing a battleship with its positions and status
 */
class Ship {
    private String name;
    private int length;
    private List<int[]> positions;
    private int hits;

    public Ship(String name, int length) {
        this.name = name;
        this.length = length;
        this.positions = new ArrayList<>();
        this.hits = 0;
    }

    public void addPosition(int row, int col) {
        positions.add(new int[]{row, col});
    }

    public void hit() {
        hits++;
    }

    public boolean isSunk() {
        return hits >= length;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public List<int[]> getPositions() {
        return positions;
    }

    public int getHits() {
        return hits;
    }
}

/**
 * Player class representing a human or AI player
 */
class Player {
    private String name;
    private boolean isAI;
    private Board board;
    private List<Ship> ships;
    private int shotsFired;
    private int shotsHit;

    public Player(String name, boolean isAI) {
        this.name = name;
        this.isAI = isAI;
        this.board = new Board();
        this.ships = new ArrayList<>();
        this.shotsFired = 0;
        this.shotsHit = 0;
        initializeShips();
    }

    private void initializeShips() {
        ships.clear();
        ships.add(new Ship("Carrier", 5));     // length 5
        ships.add(new Ship("Battleship", 4));  // length 4
        ships.add(new Ship("Destroyer", 3));     // length 3
    }

    public String getName() {
        return name;
    }

    public boolean isAI() {
        return isAI;
    }

    public Board getBoard() {
        return board;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public void incrementShotsFired() {
        shotsFired++;
    }

    public void incrementShotsHit() {
        shotsHit++;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public int getShotsHit() {
        return shotsHit;
    }

    public double getAccuracy() {
        if (shotsFired == 0) return 0.0;
        return (double) shotsHit / shotsFired * 100.0;
    }

    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }
}

/**
 * AI class implementing smart opponent behavior
 * Uses hunt and target mode for strategic ship hunting
 */
class AI {
    private Random random;
    private List<String> availableTargets;
    private List<String> targetQueue;
    private boolean huntMode;
    private String lastHit;

    public AI() {
        random = new Random();
        availableTargets = new ArrayList<>();
        targetQueue = new ArrayList<>();
        huntMode = true;
        initializeTargets();
    }

    private void initializeTargets() {
        for (char col = 'A'; col <= 'J'; col++) {
            for (int row = 1; row <= 10; row++) {
                availableTargets.add(col + "" + row);
            }
        }
    }

    public void placeShips(Player aiPlayer) {
        for (Ship ship : aiPlayer.getShips()) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(10);
                char col = (char) ('A' + random.nextInt(10));
                boolean horizontal = random.nextBoolean();
                String coord = col + "" + (row + 1);
                placed = aiPlayer.getBoard().placeShip(ship, coord, horizontal);
            }
        }
    }

    public String makeMove() {
        String target;

        if (!targetQueue.isEmpty()) {
            // Target mode - follow up on hits
            target = targetQueue.remove(0);
        } else {
            // Hunt mode - random selection with checkerboard pattern
            if (availableTargets.isEmpty()) {
                return null;
            }
            int index = random.nextInt(availableTargets.size());
            target = availableTargets.get(index);
        }

        availableTargets.remove(target);
        return target;
    }

    public void processResult(String target, String result) {
        if (result.equals("HIT")) {
            lastHit = target;
            huntMode = false;
            addAdjacentTargets(target);
        } else if (result.equals("MISS")) {
            if (targetQueue.isEmpty()) {
                huntMode = true;
            }
        }
    }

    private void addAdjacentTargets(String coord) {
        int[] parsed = parseCoordinate(coord);
        if (parsed == null) return;

        int row = parsed[0]; // 0-indexed (0-9)
        int col = parsed[1]; // 0-indexed (0-9)

        // Add adjacent cells (up, down, left, right)
        // addTargetIfValid expects 1-indexed row (1-10)
        addTargetIfValid((char) ('A' + col), row); // up: row-1 in 0-indexed = row in 1-indexed
        addTargetIfValid((char) ('A' + col), row + 2); // down: row+1 in 0-indexed = row+2 in 1-indexed
        addTargetIfValid((char) ('A' + col - 1), row + 1); // left
        addTargetIfValid((char) ('A' + col + 1), row + 1); // right
    }

    private void addTargetIfValid(char col, int row) {
        if (col >= 'A' && col <= 'J' && row >= 1 && row <= 10) {
            String target = col + "" + row;
            if (availableTargets.contains(target) && !targetQueue.contains(target)) {
                targetQueue.add(target);
            }
        }
    }

    private int[] parseCoordinate(String coord) {
        if (coord == null || coord.length() < 2) return null;

        char colChar = coord.charAt(0);
        try {
            int row = Integer.parseInt(coord.substring(1)) - 1;
            int col = colChar - 'A';
            return new int[]{row, col};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<String> getAvailableTargets() {
        return availableTargets;
    }

    public void setAvailableTargets(List<String> targets) {
        this.availableTargets = targets;
    }

    public List<String> getTargetQueue() {
        return targetQueue;
    }

    public void setTargetQueue(List<String> queue) {
        this.targetQueue = queue;
    }
}

/**
 * GameEngine class managing game flow and logic
 */
class GameEngine {
    private Player player1;
    private Player player2;
    private AI aiOpponent;
    private Scanner scanner;
    private int turnCount;

    public GameEngine(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.scanner = ShipHunters.getScanner();
        this.turnCount = 0;
        if (player2.isAI()) {
            aiOpponent = new AI();
        }
    }

    public void setupPhase() {
        ShipHunters.clearTerminal();
        System.out.println("\n⫘⫘⫘⫘⫘⫘⫘⫘⫘ SETUP PHASE ⫘⫘⫘⫘⫘⫘⫘⫘⫘");

        // Player 1 setup
        System.out.println("\n" + player1.getName() + ", deploy your fleet!");
        setupPlayerShips(player1);

        // Player 2 setup (AI placement is silent)
        if (player2.isAI()) {
            aiOpponent.placeShips(player2);
        } else {
            System.out.println("\n" + player2.getName() + ", place your ships!");
            setupPlayerShips(player2);
        }

        System.out.println("\nAll ships placed!");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }


    private void setupPlayerShips(Player player) {
        for (Ship ship : player.getShips()) {
            boolean placed = false;
            while (!placed) {
                ShipHunters.clearTerminal();
                player.getBoard().display(true);

                System.out.println("\nPlace your " + ship.getName() + " (Length: " + ship.getLength() + ")");
                System.out.print("\nEnter your starting coordinate (A1): ");
                String coord = scanner.nextLine().toUpperCase();

                System.out.print("Horizontal or Vertical? (H/V): ");
                String direction = scanner.nextLine().toUpperCase();
                boolean horizontal = direction.equals("H");

                placed = player.getBoard().placeShip(ship, coord, horizontal);

                if (!placed) {
                    System.out.println("\nInvalid placement! Try again.");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                }
            }
        }

        // Show player's final board and pause briefly (no Enter required)
        ShipHunters.clearTerminal();
        System.out.println("\n" + player.getName() + "'s final board:");
        player.getBoard().display(true);

        // brief pause so user can see the final board before continuing
        try {
            Thread.sleep(2500); // 1000 ms = 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void battlePhase() {
        ShipHunters.clearTerminal();
        System.out.println("\n========== BATTLE PHASE ==========");

        Player currentPlayer = player1;
        Player opponent = player2;

        while (!player1.allShipsSunk() && !player2.allShipsSunk()) {
            turnCount++;
            ShipHunters.clearTerminal();

            System.out.println("\n⫘⫘⫘⫘⫘⫘⫘⫘⫘  TURN " + turnCount + " ⫘⫘⫘⫘⫘⫘⫘⫘⫘");
            System.out.println("         " + currentPlayer.getName() + "'s turn");

            if (currentPlayer.isAI()) {
                aiTurn(currentPlayer, opponent);
            } else {
                playerTurn(currentPlayer, opponent);
            }

            // Check for winner
            if (opponent.allShipsSunk()) {
                endGame(currentPlayer);
                return;
            }

            // Switch players
            Player temp = currentPlayer;
            currentPlayer = opponent;
            opponent = temp;

            // If next player is AI, short pause for readability; if human, proceed immediately
            if (currentPlayer.isAI()) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void playerTurn(Player player, Player opponent) {
        System.out.println("\nYour board:");
        player.getBoard().display(true);

        System.out.println("\nOpponent's board:");
        opponent.getBoard().display(false);

        boolean validShot = false;
        while (!validShot) {
            System.out.print("\nEnter your target (A1): ");
            String target = scanner.nextLine().toUpperCase();

            String result = opponent.getBoard().shoot(target);

            if (result.equals("INVALID")) {
                System.out.println("Invalid target! Try again.");
            } else if (result.equals("ALREADY_SHOT")) {
                System.out.println("You already shot there! Try again.");
            } else {
                validShot = true;
                player.incrementShotsFired();

                if (result.equals("HIT")) {
                    player.incrementShotsHit();
                    System.out.println("\n*** HIT! ***");
                    checkForSunkenShip(opponent, target);
                } else {
                    System.out.println("\n*** MISS! ***");
                }

                System.out.println("\nOpponent's board after your shot:");
                opponent.getBoard().display(false);
            }
        }
    }

    private void aiTurn(Player ai, Player opponent) {
        System.out.println("\nAI is thinking...");

        String target = aiOpponent.makeMove();
        if (target == null) {
            return;
        }

        String result = opponent.getBoard().shoot(target);
        ai.incrementShotsFired();

        System.out.println("\nAI shoots at " + target + "...");

        if (result.equals("HIT")) {
            ai.incrementShotsHit();
            System.out.println("*** AI HIT your ship at " + target + "! ***");
            aiOpponent.processResult(target, "HIT");
            checkForSunkenShip(opponent, target);
        } else {
            System.out.println("AI missed at " + target + ".");
            aiOpponent.processResult(target, "MISS");
        }

        System.out.println("\nYour board after AI's shot:");
        opponent.getBoard().display(true);
    }

    private void checkForSunkenShip(Player player, String target) {
        int[] coords = player.getBoard().parseCoordinate(target);
        if (coords == null) return;

        for (Ship ship : player.getShips()) {
            for (int[] pos : ship.getPositions()) {
                if (pos[0] == coords[0] && pos[1] == coords[1]) {
                    ship.hit();
                    if (ship.isSunk()) {
                        System.out.println("*** " + ship.getName() + " has been SUNK! ***");
                    }
                    return;
                }
            }
        }
    }

    private void endGame(Player winner) {
        ShipHunters.clearTerminal();

        String PURPLE = ShipHunters.ANSI_PURPLE;
        String PINK = ShipHunters.ANSI_PINK;
        String RESET = ShipHunters.ANSI_RESET;

        // Choose the boxed message based on the winner
        String boxedMessage = !winner.isAI() ? "CONGRATS YOU WIN!" : ("*** " + winner.getName() + " WINS! ***");

        // Build box sized to fit the message
        int innerWidth = Math.max(20, boxedMessage.length());
        String top = "╔" + "═".repeat(innerWidth) + "╗";
        String bottom = "╚" + "═".repeat(innerWidth) + "╝";
        String emptyLine = "║" + " ".repeat(innerWidth) + "║";

        // Print boxed header with colors (border = purple, message = pink)
        System.out.println();
        System.out.println(PURPLE + top + RESET);
        System.out.println(PURPLE + emptyLine + RESET);

        int left = (innerWidth - boxedMessage.length()) / 2;
        int right = innerWidth - left - boxedMessage.length();
        System.out.println(PURPLE + "║" + RESET + PINK + " ".repeat(left) + boxedMessage + " ".repeat(right) + RESET + PURPLE + "║" + RESET);

        System.out.println(PURPLE + emptyLine + RESET);
        System.out.println(PURPLE + bottom + RESET);
        System.out.println();

        // Statistics (heading in pink)
        System.out.println(PINK + "⫘⫘⫘⫘⫘⫘⫘⫘⫘  GAME STATISTICS ⫘⫘⫘⫘⫘⫘⫘⫘⫘" + RESET);
        System.out.println(PINK + winner.getName() + ":" + RESET);
        System.out.println(PINK + "  Shots Fired: " + RESET + winner.getShotsFired());
        System.out.println(PINK + "  Shots Hit: " + RESET + winner.getShotsHit());
        System.out.println(PINK + "  Accuracy: " + RESET + String.format("%.2f", winner.getAccuracy()) + "%");
        System.out.println(PINK + "  Turns to Win: " + RESET + turnCount);

        // Prompt once, then reset colors so the terminal returns to normal
        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();

        // Reset per-text ANSI and global Windows color (if used)
        System.out.print(ShipHunters.ANSI_RESET);
        ShipHunters.resetConsoleColors();
    }
}

/* Game Logic
*  AI decides where to shoot. It keeps a list of all available cells (availableTargets).
*  If it recently hit a ship it switches to target mode and tries neighbors via targetQueue.
* Otherwise it selects a random cell from availableTargets (hunt mode).
* */