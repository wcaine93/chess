import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author wcaine
 */
public class ChessBoard {
   /**
    * White chess piece characters arranged by keys K, Q, R, B, N, P.
    */
   public final Map<Character, String> blackPieces = new HashMap<>(Map.of('k', "♚",
                                                                            'q', "♛",
                                                                            'r', "♜",
                                                                            'b', "♝",
                                                                            'n', "♞",
                                                                            'p', "♟"));
   /**
    * Black chess piece characters arranged by keys k, q, r, b, n, p.
    */
   public final Map<Character, String> whitePieces = new HashMap<>(Map.of('K', "♔",
                                                                            'Q', "♕",
                                                                            'R', "♖",
                                                                            'B', "♗",
                                                                            'N', "♘",
                                                                            'P', "♙"));
   /**
    * White and black empty chess board square characters
    */
   public final String blackSquare = "■", whiteSquare = "□";
   private String[] boardState; // encoded as a FEN6
   private boolean gameState = true; // if the game is not ended
   
   
   /**
    * Assigns standard starting position as FEN6 to {@link #boardState this.boardState}.
    * @see FEN#getFEN6(String stdFEN)
    * 
    * @see ChessPlayer#newGame()
    */
   ChessBoard() {
      // if no FEN is entered, set the board with the standard chess starting position
      this.boardState = FEN.getFEN6("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
      System.out.println("Chessboard constructed!");
      
      System.out.println("Standard game initiated.");
   }
   
   /**
    * Assigns given position as FEN6 to {@link #boardState this.boardState} after validation by
    * {@link FEN#validFEN(String) FEN.validFEN}. If invalid, assigns false to 
    * {@link #boardState this.boardState} (default true) for user re-prompt via {@link ChessPlayer#newGame()}.
    * @see FEN#getFEN6(String stdFEN)
    */
   ChessBoard(String inputFEN) {
      if (FEN.validFEN(inputFEN)) {
         System.out.println("Valid FEN entered! Constructing board!");
         this.boardState = FEN.getFEN6(inputFEN);
         System.out.println("Chessboard constructed!");
      } else {
        // set gameState to false to stop ChessPlayer#newGame() if FEN is invalid
        this.gameState = false; // FIXME: Add re-prompt for user
      }
   }

   /**
    * @return the boardState
    */
   public String[] getBoardState() {
      return boardState.clone();
   }

   /**
    * @return the gameState
    */
   public boolean isGameState() {
      return gameState;
   }
   
   
   /**
    * Output a figurine chess board to the standard output based on {@link #boardState this.boardState}. 
    * Whichever color to move is placed on bottom (white rank 1, black rank 8).
    */
   public void display() {
      // for design of board, see execution below
      String header  = "\n+-------------------+\n| ";
      String endLine = " |\n| ";
      String footer  = "  |\n+-------------------+\n";
      
      
      
      // read in position string from FEN6[0]
      String position = this.boardState[0];
      
      // if black to move, display board upside down (black on bottom)
      char[] dummy = position.toCharArray();
      position = "";
      for (int i = dummy.length - 1; i >= 0; i--) {
         position += dummy[i];
      }
      
      String output = "";
      
      // chess board is composed of 8 ranks and 8 files
      // position string is in order of rank8/rank7/rank6 and .../file1file2file3.../...
      int charCount = 0;
      for (char c : position.toCharArray()) {
         charCount++;
         
         // place a space between each character horizontally to algin with the vertical
         output += " ";
         
         // at each slash, create a new line and add the side character
         if (c == '/') {
            output += endLine;
            continue;
         }
         
         // convert each character to its corresponding figurine (piece character) representation
         if (whitePieces.containsKey(c)) {
            output += whitePieces.get(c);
            continue;
         } 
         if (blackPieces.containsKey(c)) {
            output += blackPieces.get(c);
            continue;
         }
         
         // convert _ into blank spaces
         // a square is black if it is an odd manhattan distance from a8 and white otherwise
         if (c == '_') {
            // even manhattan distance = white square
            if (charCount % 2 == 0) {
               output += whiteSquare;
            } else output += blackSquare;
         }
      }
      
      System.out.println();
      System.out.println("Here's your board!");
      
      System.out.print(header);
      System.out.print(output);
      System.out.println(footer);
   }

   void doMove(String inputData) {
      FEN.updateFEN(inputData, this.boardState);
   }
}
