import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

/**
 * Contains methods for FEN manipulation, validation and searching. See
 * <a href="https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation">Wikipedia
 * article on Forsyth-Edwards Notation</a> for details on FEN notation.
 * @author wcaine
 */
public class FEN {

   /**
    * Turns standard FEN into FEN6-style array, also converting to 
    * {@linkplain #easyFEN(java.lang.String) ezFEN}. 
    * <br>
    * {@link smallText() NOTE:} Inputs should already be passed through 
    * {@link #validFEN } for validation {@link smallText() (and be valid)} 
    * before conversion to FEN6.
    * <p>
    * FEN6 array strings, by index: 
    * <ol start="0">
    *    <li>position</li>
    *    <li>color to move</li>
    *    <li>castling ability</li>
    *    <li>en passant square</li>
    *    <li>halfmove clock</li>
    *    <li>(full) move number</li>
    * </ol>
    * See {@linkplain #regexFEN} for regular expressions of each string.
    * @see #easyFEN(String standardPosition)
    * 
    * @param stdFEN FEN in standard notation, from user input in 
    *               {@link ChessPlayer#main } after validation
    * @see ChessBoard#ChessBoard(String inputFEN) 
    * 
    * @return FEN6
    */ 
   public static String[] getFEN6(String stdFEN) {
      // break the FEN into individual parts
      Pattern split = Pattern.compile(" "); // parts delimited by spaces
      String[] FEN6 = split.split(stdFEN); // called FEN6 for the 6 parts:
      // position, color to move, castling ability, en passant square, halfmove clock, (full) move number
      
      FEN6[0] = easyFEN(FEN6[0]); // convert to ezFEN
      
      return FEN6;
   }

   /**
    * Turns digits in passed FEN position string into series of '_' of length corresponding to 
    * digit value. Ex: {@link smallText() ".../2pRN3/..."} -> 
    * {@link smallText() ".../__pRN___/..."}. 
    * <br>
    * {@link smallText() NOTE:} FEN should already be passed through 
    * {@link #validFEN } for validation {@link smallText() (and be valid)}
    * before conversion to ezFEN.
    * 
    * @param standardPosition FEN6 position string; {@linkplain #getFEN6 FEN6}[0]
    * 
    * @return ezFEN
    */
   private static String easyFEN(String standardPosition) {
      String ezFEN = "";
      for (char c : standardPosition.toCharArray()) {
         if (Pattern.matches("[1-8]", Character.toString(c))) {
            // if the character is a digit, replace with number of _
            for (int i = 0; i < Character.getNumericValue(c); i++) {
               ezFEN += '_';
            }
         } else {
            ezFEN += c;
         }
      }
      
      return ezFEN;
   }
   
   private static char valueAt(String location, String ezPosition) {
      // turn rank into slash number in position string (rank 8 is 0 slashes in, rank 1 is 7 in)
      int slash = 8 - Character.getNumericValue(location.charAt(1));
      // cut down position string to the relevant slash
      String relPos = ezPosition;
      for (int i = 0; i < slash; i++) {
         relPos = relPos.substring(relPos.indexOf('/') + 1);
      }
      
      // turn file letter into a numeric (a = 0, h = 8)
      int file = location.charAt(0) - 'a';
      
      
      // extract the character in the relevant file location
      return relPos.charAt(file);
   }
   
   // perform moves
   /**
    * Updates the passed FEN6 with the move passed. Validates move notation 
    * before processing via {@link #validMoveNotation} and ... [TK]
    * 
    * @param FEN6 see {@link #getFEN6}
    * @param move in extended algebraic notation, from user input {@link ChessPlayer#progressGame }
    * @see ChessBoard#doMove(String userInput)
    */
   public static void updateFEN(String move, String[] FEN6) {
      if (!validMoveNotation(move)) {
         String newMove = ChessPlayer.movePrompt(FEN6[1]);
         updateFEN(newMove, FEN6);
         return;
      }

      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
      
      // FIXME: re-prompt player for a new move
      
      /* check if correct piece is on start square
         if move contains "x", check if piece is on end square;
         If there is but no x, ask "There is a piece on the end position of your
         move, but no "x" in your notation to mark a capture.
         Are you sure this is the move you intended to make? Y/N"
         or if there is an x but no piece say "You marked a capture in your notation
         but there is no piece on the end square. Please input move correctly."
         and prompt player to move.
      */
      
      
      // add validation for piece movement and update javadoc
   }
   
   private static boolean validMoveNotation(String move) {
      // checks if move is in proper notation
      Pattern validMove = Pattern.compile("^[KQRBN]?([a-h]{1}[1-8]{1}){1}x?([a-h]{1}[1-8]{1}){1}(?:=[KQRBN])?[+#]?$");
      if (!Pattern.matches(validMove.pattern(), move)) {
         errorMessage("Either illegal characters entered or expanded algebraic notation formatted incorrectly");
         return false;
      }
      
      return true;
   }
   
   // Validation of FEN
   /**
    * Generates an error message output to the user in the form "Invalid FEN: {message}".
    * @param message to be displayed as {message}
    */
   public static void errorMessage(String message) {
      System.out.println("Invalid FEN: " + message);
   }
   
   /**
    * Checks if FEN formatting is valid using the methods found in the <b>See 
    * Also</b> section below. 
    * <br>
    * {@link smallText() NOTE:} Passed FEN should be a full string, not a 
    * {@link #getFEN6 FEN6} or {@link #easyFEN ezFEN}.
    * <p>
    * Accounts for logical errors of king beginning in 
    * check with the wrong color to move and en passant square identified with 
    * the wrong color to move.
    * 
    * @see #regexFEN(String[] FEN6)
    * @see #countFEN(String[] FEN6)
    * @see #epFEN(String[] FEN6)
    * 
    * @param testFEN user-inputted FEN (String), from {@link ChessPlayer#main } via {@link ChessBoard#ChessBoard(String inputFEN) }
    * 
    * @return true if valid, false if invalid
    */
   public static boolean validFEN(String testFEN) {
      // break the FEN into individual parts
      Pattern split = Pattern.compile(" "); // parts delimited by spaces
      String[] FEN6 = split.split(testFEN); // called FEN6 for the 6 parts
      
      // ensure FEN6 has 6 elements
      if (FEN6.length != 6) {
         errorMessage("FEN does not have the correct number of modules");
         return false;
      }
      
      // test the validity of the FEN data modules using regular expressions
      if (regexFEN(FEN6) == false) return false;
      
      // test if the board is valid (containing proper numbers of pieces and squares)
      if (countFEN(FEN6) == false) return false;
      
      
      // convert the position String to an easyFEN (numerics replaced with series of '_')
      FEN6[0] = easyFEN(FEN6[0]);
      
      // test if the en passant square contains a pawn in front
      if (epFEN(FEN6) == false) return false;
      
      // FIXME: include logical checks specified in javadoc
      
      return true;
   }
   
   private static boolean regexFEN(String[] FEN6) {
      // test the validity of the FEN data modules using regular expressions
      
      // regex of allowable characters and orientations in each part of a FEN:
      // position, color to move, castling ability, en passant square, halfmove clock, (full) move number
      final Pattern POSITION = Pattern.compile("^[KQRBNPkqrbnp[1-8]]{1,8}/[KQRBNPkqrbnp[1-8]]{1,8}/[KQRBNPkqrbnp[1-8]]{1,8}/[KQRBNPkqrbnp[1-8]]{1,8}/[KQRBNPkqrbnp[1-8]]{1,8}/[KQRBNPkqrbnp[1-8]]{1,8}/[KQRBNPkqrbnp[1-8]]{1,8}/[KQRBNPkqrbnp[1-8]]{1,8}$");
      final Pattern COLOR = Pattern.compile("[wb]");
      final Pattern CASTLING = Pattern.compile("-|K?Q?k?q?");
      final Pattern ENPASSANT = Pattern.compile("-|[a-h]{1}[1-8]{1}");
      final Pattern HALFMOVE = Pattern.compile("\\d{1,2}");
      final Pattern MOVENUM = Pattern.compile("\\d+");
      
      
      // test if the FEN parts conform to regex
      Pattern[] testPatterns = {POSITION, COLOR, CASTLING, ENPASSANT, HALFMOVE, MOVENUM};
      for (int i = 0; i < FEN6.length; i++) {
         // check each module of FEN6 for correct regex
         // if more modules than necessary exist, throw exception and notify user
         // if any module is does not match, return false and notify user
         boolean isInvalid;
         try {
            isInvalid = !(Pattern.matches(testPatterns[i].pattern(), FEN6[i]));
         } catch (ArrayIndexOutOfBoundsException e) {
            errorMessage("FEN must only contain 6 data modules");
            return false;
         }
         
         if (isInvalid) {
            errorMessage("Bad module #" + (i+1));
            return false;
         }
      }
      
      return true; // if no invalidations found
   }
   
   private static boolean countFEN(String[] FEN6) {
      // test if the board is valid (containing proper numbers of pieces and squares)
      String position = FEN6[0];
      
      // to test squares, initialize with 8 and count down to 0 for each rank
      int fileCount = 8;
      int rankCount = 1; // for error display
      Pattern emptySquares = Pattern.compile("[0-8]"); // to identify digits in the FEN
      
      // to test pieces, count down from a complete board
      Map<Character, Integer> totalPieces = new HashMap<>();
         totalPieces.put('K', 1);
         totalPieces.put('Q', 1);
         totalPieces.put('R', 2);
         totalPieces.put('B', 2);
         totalPieces.put('N', 2);
         totalPieces.put('P', 8);
         totalPieces.put('k', 1);
         totalPieces.put('q', 1);
         totalPieces.put('r', 2);
         totalPieces.put('b', 2);
         totalPieces.put('n', 2);
         totalPieces.put('p', 8);
      
      
      // if fileCount != 0 at the end of the rank, return false and notify user
      // if any piece numbers < 0, return false and notify user
      for (char c : position.toCharArray()) {
         // handle non-piece characters
         if (Pattern.matches(emptySquares.pattern(), Character.toString(c))) {
            // digits in FEN positions represent empty squares, subtract the value from fileCount
            fileCount -= Character.getNumericValue(c);
            continue;
         }
         if (c == '/') {
            // '/' marks rank end, if fileCount has not reached 0, return false and notify user
            if (fileCount != 0) {
               errorMessage("Incorrect number of files on rank " + rankCount);
            } else {
               fileCount = 8;
               rankCount++;
            }
            continue;
         }
         
         // handle piece characters
         totalPieces.put(c, totalPieces.get(c) - 1);
         fileCount--;
         if (totalPieces.get(c) < 0) {
            errorMessage("Too many pieces of type " + c);
            return false;
         }
      }
      
      return true;
   }
   
   private static boolean epFEN(String[] FEN6) {
      // test if the en passant square contains a pawn in front
      String epSquare = FEN6[3];
      
      // return true if there is no en passant square
      if (epSquare.equals("-")) return true;
      
      // if epSquare is on the 6th rank, and it is white to move, check the 5th rank for 'p'
      // if on the 3rd, and it is black to move, check the 4th for 'P'
      if (epSquare.charAt(1) == '6' && FEN6[1].equals("w")) {
         if (valueAt(epSquare.charAt(0) + "5", FEN6[0]) == 'p') {
            return true;
         }
      }
      if (epSquare.charAt(1) == '3' && FEN6[1].equals("b")) {
         if (valueAt(epSquare.charAt(0) + "4", FEN6[0]) == 'P') {
            return true;
         }
      }
      
      errorMessage("The en passant square is invalid for this situation");
      return false;
   }
}