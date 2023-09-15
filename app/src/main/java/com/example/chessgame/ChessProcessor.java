package com.example.chessgame;
import java.util.ArrayList;

public class ChessProcessor {
    String[][] board;
    private final ArrayList<ChessPiece> chessPieces; //for onBoard pieces
    King whiteKing;
    King blackKing;

    public ChessProcessor(String[][] board) {
        this.board = board;
        this.chessPieces = new ArrayList<>();
        setChessPieces();
    }

    public ArrayList<ChessPiece> getChessPieces() {
        return chessPieces;
    }

    public void setChessPieces() {
        for (int row = 0; row < 8; row ++) {
            for (int col = 0; col < 8; col ++) {
                if (!(board[row][col].equals("_"))) {
                    ChessPiece chessPiece = getChessPieceFromString(board[row][col], row, col);
                    chessPieces.add(chessPiece);
                }
            }
        }
        setRooksForKing(whiteKing);
        setRooksForKing(blackKing);
    }

    private void setRooksForKing(King king) {
        int row = king.getCurrentSquare().getRow();
        Rook leftRook = (Rook) getChessPieceBySquare(row, 0);
        Rook rightRook = (Rook) getChessPieceBySquare(row, 7);
        king.setRooks(leftRook, rightRook);
    }

    private ChessPiece getChessPieceBySquare(int row, int col) {
        ChessPiece chessPiece = null;
        for (ChessPiece cp : chessPieces) {
            int cpRow = cp.getCurrentSquare().getRow();
            int cpCol = cp.getCurrentSquare().getCol();
            if (cpRow == row && cpCol == col) {
                chessPiece = cp;
                break;
            }
        }
        return chessPiece;
    }

    private ChessPiece getChessPieceBySquare(Square square) {
        int row = square.getRow();
        int col = square.getCol();
        return getChessPieceBySquare(row, col);
    }

    private ChessPiece getChessPieceFromString(String squareContent, int row, int col) {
        char color = squareContent.charAt(0);
        char chessPieceType = squareContent.charAt(1);
        Square square = new Square(row, col);
        ChessPiece chessPiece = null;
        switch(chessPieceType) {
            case 'r':
                chessPiece = new Rook(color, square);
                break;
            case 'n':
                chessPiece = new Knight(color, square);
                break;
            case 'b':
                chessPiece = new Bishop(color, square);
                break;
            case 'q':
                chessPiece = new Queen(color, square);
                break;
            case 'k':
                chessPiece = new King(color, square);
                if (color == 'w') {
                    whiteKing = (King) chessPiece;
                } else {
                    blackKing = (King) chessPiece;
                }
                break;
            case 'p':
                int isBottom = 1;
                if (row == 1) {
                    isBottom = 0;
                }
                chessPiece = new Pawn(color, square, isBottom);
                break;
        }
        return chessPiece;
    }

    public ArrayList<Square> getPossibleSquares(Square pressedSquare) {
        ChessPiece chessPiece = getChessPieceBySquare(pressedSquare);
        ArrayList<Square> possibleSquares = chessPiece.getPossibleSquares(board);
        ArrayList<Square> squares = new ArrayList<>(possibleSquares); //copy array to iterate on (so changes and deletions on possibleSquares are possible).
        //check if own-"shakh" can occur due to possible move (bad scenario, invalid move)
        for (Square possibleSquare : squares) {
            String[][] possibleBoard = getBoardAfterPossibleMove(pressedSquare, possibleSquare);
            if (isCreatingOwnCheck(possibleBoard, chessPiece.getColor(), possibleSquare)) {
                possibleSquares.remove(possibleSquare);
            }
        }
        return possibleSquares;
    }

    private boolean isCreatingOwnCheck(String[][] possibleBoard, char ownColor, Square possibleSquare) {
        int possibleSquareRow = possibleSquare.getRow();
        int possibleSquareCol = possibleSquare.getCol();
        for (ChessPiece cp : chessPieces) { //cp is a piece probably being a threat and creating check on own king
            int cpRow = cp.getCurrentSquare().getRow();
            int cpCol = cp.getCurrentSquare().getCol();
            if (cpRow == possibleSquareRow && cpCol == possibleSquareCol) {
                continue; //no need to check for threat from possible "eaten" piece as it won't be a concern after the move
            }
            if (cp.getColor() != ownColor) {
                if (cp.isCheckingRivalKing(possibleBoard, ownColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String[][] getBoardAfterPossibleMove(Square pressedSquare, Square possibleSquare) {
        String[][] possibleBoard = new String[8][8];
        //copy current board to possible board:
        for (int i = 0; i < 8; i ++) {
            for (int j = 0; j < 8; j++) {
                possibleBoard[i][j] = board[i][j];
            }
        }
        //change possible board according to move:
        String squareContent = board[pressedSquare.getRow()][pressedSquare.getCol()];
        possibleBoard[possibleSquare.getRow()][possibleSquare.getCol()] = squareContent;
        possibleBoard[pressedSquare.getRow()][pressedSquare.getCol()] = "_";
        return possibleBoard;
    }

    public void makeMove(Square fromSquare, Square toSquare) {
        //clean chessPieces list from eaten piece if any:
        int toRow = toSquare.getRow(), toCol = toSquare.getCol();
        if (!(board[toRow][toCol].equals("_"))) {
            ChessPiece chessPiece = getChessPieceBySquare(toSquare);
            chessPiece.setAlive(false);
            chessPieces.remove(chessPiece);
        }
        //update board:
        int fromRow = fromSquare.getRow(), fromCol = fromSquare.getCol();
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = "_";
        //update chessPiece square field:
        ChessPiece chessPiece = getChessPieceBySquare(fromSquare);
        chessPiece.setCurrentSquare(toSquare);
        //if is rook/king/pawn, update "has not moved" field:
        if (board[toRow][toCol].charAt(1) == 'k') {
            ((King) chessPiece).setStillNotMoved(false);
        }
        if (board[toRow][toCol].charAt(1) == 'r') {
            ((Rook) chessPiece).setStillNotMoved(false);
        }
        if (board[toRow][toCol].charAt(1) == 'p') {
            ((Pawn) chessPiece).setStillNotMoved(false);
        }
        //TODO check if "mat" (and then test on real mat scenarios)
        //TODO check if pawn reached end of board (if so, it transforms into queen)
    }
}
