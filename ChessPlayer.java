import java.util.Scanner;

/**
 *
 * @author wcaine
 */
public class ChessPlayer {

   private static final Scanner stdin = new Scanner(System.in);

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      newGame();
      
      // FIXME: implement chess board visualization + move numbering *
      // FIXME: implement FEN changes ***
      // FIXME: implement piece movements **
      // FIXME: implement attackedsquares hashsets
      // FIXME: implement check and checkmate
      // FIXME: implement basic e4d4 notation
      // FIXME: implement timed games
   }
   
   private static void newGame() {
      System.out.print("Enter starting position using FEN notation or click enter for standard position: ");
      String inputFEN = stdin.nextLine();
      
      // create chessboard based on FEN or standard game if none entered
      ChessBoard board;
      if (inputFEN.isBlank()) {
         board = new ChessBoard();
      } else {
         board = new ChessBoard(inputFEN.trim());
      }
      
      // reprompt user for FEN if ChessBoard construction fails
      if (!board.isGameState()) {
         System.out.println();
         newGame();
      };
      
      // inititate game upon user acceptance of rules
      System.out.println();
      System.out.println("""
                         Know all moves must be expressed in expanded algebraic notation.
                         If unsure about how to express a move, input "help" at any time.
                         In case of exit, enter "stop" at any time to pause the game and
                         receive the FEN to pick up the game at another time.""");
      System.out.println("Enter \"Y\" to begin game.");
      System.out.print("   ");
      String inputData = stdin.nextLine();
      inputData = inputData.trim();
      switch (inputData) {
         case "Y", "y" -> progressGame(board);
         default -> System.out.println("Unrecognized input. Terminating session.");
      }
   }
   
   /**
    * Displays the board, prompts a move to be made, sends it to the board, 
    * and while the gameState = true, recurses
    * @see ChessBoard#display() 
    * @see #movePrompt(String colorToMove)
    * @see ChessBoard#doMove(String inputData)
    * 
    * @param board chessboard to perform the actions on
    */
   public static void progressGame(ChessBoard board) {
      // display board and generate move prompt by color to move (FEN6[1])
      board.display();
      String inputData = movePrompt(board.getBoardState()[1]);
      inputData = inputData.trim();
      
      // end the game if the gameState is false or the user inputs "stop"
      if (!board.isGameState() || inputData.equals("stop") || inputData.equals("Stop")) {
         endGame(board, board.isGameState());
         return;
      }
      
      // update board with inputted move and recurse
      board.doMove(inputData);
      progressGame(board);
   }
   
   private static void endGame(ChessBoard board, boolean gameNotOver) {
      // convert the FEN6 into a FEN
      String endFEN = "";
      for (String s : board.getBoardState()) {
         endFEN += s;
         endFEN += " ";
      }
      
      // print the FEN and, if the game is still going on, instruct to save for 
      // reentry; if the game is over, prompt to play again
      System.out.println("Your FEN is:");
      System.out.println(endFEN.trim());
      if (gameNotOver) {
         System.out.println("   But the game's not over! Save the FEN and "
                 + "input it when you come back to keep playing!");
      } else {
         System.out.println("That was a great game! Play again? Y/N");
         System.out.print("   ");
         String inputData = stdin.next();
         inputData = inputData.trim();
         
         // begin a new game on Y, terminate on N or other character
         switch (inputData) {
            case "Y", "y" -> newGame();
            case "N", "n" -> System.out.println("Okay, terminating session.");
            default -> System.out.println("Unrecognized input. Terminating session.");
         }
      }
   }
   
   /**
    * Prompts the player to input a move in 
    * <a href="https://en.wikipedia.org/wiki/Algebraic_notation_(chess)#Long_algebraic_notation">expanded algebraic notation</a>.
    * Trims and reads to open {@link #helpMenu() help menu} if necessary.
    * @see #stdin
    * 
    * @param colorToMove from a FEN6, {@linkplain FEN#getFEN6 FEN6}[1]; <b>must be 'w' or 'b'</b>
    * 
    * @return move (user input, unvalidated)
    * @see #progressGame(ChessBoard board) 
    */
   public static String movePrompt(String colorToMove) {
      // moves must be in expanded algebraic notation
      
      if (colorToMove.equals("w")) {
         System.out.println("White to move.");
      } else {
         System.out.println("Black to move.");
      }
      System.out.print("Please enter your move in expanded algebraic notation: ");
      String inputData = stdin.nextLine();
      inputData = inputData.trim();
      
      // bring up the help menu and recurse on input "help")
      if (inputData.equals("help") || inputData.equals("Help")) {
         helpMenu();
         return movePrompt(colorToMove);
      }
      
      return inputData;
   }
   
   private static void helpMenu() {
      System.out.println("""
                         
                         -------------------------------------------------------
                         Expanded algebraic notation requires that you write
                         start and end position of every piece moved; if the
                         piece is not a pawn, the piece letter (K, Q, R, B, N)
                         should also be included before the move. If a move is
                         a capture, include "x" in between the start and end
                         positions. If a move gives check, add "+" to the very 
                         end of the notation (after special moves); if 
                         checkmate, add "#". 
                         See https://en.wikipedia.org/wiki/Chess_notation for
                         examples, under "Long algebraic."
                         
                         Special moves:
                         For castling, simply write "O-O" or "0-0" for kingside
                         and "O-O-O" or "0-0-0".
                         In the case of pawn promotion, add "=X" on the end of
                         the notation, where X is the promoted piece letter.
                         En passant should be recorded as a standard capture.
                         -------------------------------------------------------
                         """);
   }

}