import java.util.Scanner;

/**
 * Responsible for determining the gamemode (1- or 2-player), running the game, and handling game exit.
 *
 * @author Mckenna Cisler
 * @version 11.23.2015
 */
public class GameRunner {
    // game constants
    public static final int MIN_BOARD_SIZE = 7;
    public static final int MAX_BOARD_SIZE = 12;

    // define globally used variables
    private static Scanner input = new Scanner(System.in);
    private static boolean isPlayer1 = true;

    // define an easily accesible "end" variable
    private static boolean endGameNow = false;

    public static void main(String[] args) {
        // generate basic board and setup
        Board board = new Board(askBoardSize());

        // define abstract classes, to be assigned a concrete class after deciding gamemode
        Player player1;
        Player player2;

        GameMode gameMode = askGameMode();

        if (gameMode == GameMode.HUMAN_VS_HUMAN) {
            player1 = new HumanPlayer(true);
            player2 = new HumanPlayer(false);
        } else if (gameMode == GameMode.HUMAN_VS_AI) {
            player1 = new HumanPlayer(true);
            player2 = new AIPlayer(false);
        } else if (gameMode == GameMode.AI_VS_AI) {
            player1 = new AIPlayer(true);
            player2 = new AIPlayer(false);
        } else {
            System.out.println("No game mode selected");
            return;
        }
        clearScreen();

        while (!endGame(board)) {
            if (isPlayer1) {
                board = player1.getMove(board);
            } else {
                board = player2.getMove(board);
            }

            // switch players and flip board for next player
            isPlayer1 = !isPlayer1;
            //board = board.getFlippedBoard();
        }
    }

    private static int askBoardSize() {
        while (true) {
            // display message
            clearScreen();
            System.out.println("*******Welcome to checkers!*******\n");
            System.out.println("Please enter board size: ");
            System.out.println("Minimal size - " + MIN_BOARD_SIZE);
            System.out.println("Maximal size - " + MAX_BOARD_SIZE);
            System.out.println("\nEnter a number: ");

            String response = input.nextLine();
            int boardSize;
            try {
                boardSize = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                System.out.println("Please input correct number below");
                continue;
            }
            if (boardSize < MIN_BOARD_SIZE || boardSize > MAX_BOARD_SIZE) {
                System.out.println("Please input correct number below");
            } else {
                return boardSize;
            }

        }
    }

    /**
     * Queries the user to determine the requested gamemode
     *
     * @return Returns chosen GameMode
     */
    private static GameMode askGameMode() {
        // keep asking to get a valid response
        while (true) {
            // display message
            clearScreen();
            System.out.println("Enter 'exit' to exit at any point (or 0 when moving a piece).\n");
            System.out.println("We offer three game modes:");
            System.out.println("[1] 1 Player Mode (vs Computer) - EXPERIMENTAL");
            System.out.println("[2] 2 Player Mode");
            System.out.println("[3] 2 Computer v Computer Mode");
            System.out.println("\nWhich one would you like to play? Enter a number: ");

            // ask for String, but only accept "1", "2" or "3"
            String response = input.nextLine();
            switch (response.trim()) {
                case "1":
                    return GameMode.HUMAN_VS_AI;
                case "2":
                    return GameMode.HUMAN_VS_HUMAN;
                case "3":
                    return GameMode.AI_VS_AI;
                case "exit":
                    endGameNow();
                    return null;
                default:
                    System.out.println("Please input correct number below");
            }
        }
    }

    /**
     * Determines whether the game has been completed or is in a stalemate
     *
     * @param board The board to check to determine if we're at an endgame point.
     */
    private static boolean endGame(Board board) {
        // have an emergency trigger for endgame
        if (endGameNow) {
            return true;
        } else {
            // otherwise, search the board for pieces of both colors, and if none of one color are present,
            // the other player has won.
            int movableWhiteNum = 0;
            int movableBlackNum = 0;
            for (int pos = 0; pos < board.size * board.size; pos++) {
                // make sure the piece exists, and if so sum movable pieces for each color)
                Piece pieceHere = board.getValueAt(pos);
                if (pieceHere != null) {
                    // only consider a piece if it has possible moves
                    Move[] movesHere = pieceHere.getAllPossibleMoves(board);
                    if (movesHere != null && movesHere.length > 0) {
                        if (pieceHere.isWhite)
                            movableWhiteNum++;
                        else if (!pieceHere.isWhite)
                            movableBlackNum++;
                    }
                }
            }

            // determine if anyone won (or if no one had any moves left)
            if (movableWhiteNum + movableBlackNum == 0)
                System.out.println("The game was a stalemate...");
            else if (movableWhiteNum == 0)
                System.out.println("Congratulations, Black, you have won the game gloriously!");
            else if (movableBlackNum == 0)
                System.out.println("Congratulations, White, you have won the game gloriously!");
            else
                return false;

            // we can only make it here if any of the above conditions are hit
            return true;
        }
    }

    /**
     * Responsible for quickly ending the game
     */
    public static void endGameNow() {
        endGameNow = true;
    }

    /**
     * Clears the terminal screen
     */
    public static void clearScreen() {
        // see http://stackoverflow.com/a/32008479/3155372
        System.out.print("\033[2J\033[1;1H");
    }
}
