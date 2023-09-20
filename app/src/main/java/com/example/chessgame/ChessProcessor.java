package com.example.chessgame;
import android.graphics.Color;
import android.util.Log;
import android.widget.FrameLayout;

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
        ChessPiece possibleLeftRook = getChessPieceBySquare(row, 0);
        if (possibleLeftRook instanceof Rook && possibleLeftRook.color == king.getColor()) {
            king.setLeftRook((Rook) possibleLeftRook);
        }
        ChessPiece possibleRightRook = getChessPieceBySquare(row, 7);
        if (possibleRightRook instanceof Rook && possibleRightRook.color == king.getColor()) {
            king.setRightRook((Rook) possibleRightRook);
        }
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
                if (GameActivity.isWhite == 1) {
                    if (color == 'b') {
                        isBottom = 0;
                    }
                } else {
                    if (color == 'w') {
                        isBottom = 0;
                    }
                }
                chessPiece = new Pawn(color, square, isBottom);
                break;
        }
        return chessPiece;
    }

    public ArrayList<Square> getPossibleSquares(Square pressedSquare, String[][] board) {
        ChessPiece chessPiece = getChessPieceBySquare(pressedSquare);
        return getPossibleSquaresWithNoOwnCheck(chessPiece, board);
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

    private String[][] getBoardAfterPossibleMove(Square pressedSquare, Square possibleSquare, String[][] currBoard) {
        String[][] possibleBoard = new String[8][8];
        //copy current board to possible board:
        for (int i = 0; i < 8; i ++) {
            for (int j = 0; j < 8; j++) {
                possibleBoard[i][j] = currBoard[i][j];
            }
        }
        int fromRow = pressedSquare.getRow(), fromCol = pressedSquare.getCol();
        int toRow = possibleSquare.getRow(), toCol = possibleSquare.getCol();
        //change possible board according to move:
        String squareContent = currBoard[fromRow][fromCol];
        if ((toRow == 0 || toRow == 7) && squareContent.charAt(1) == 'p') {
            possibleBoard[toRow][toCol] = squareContent.charAt(0) + "q";
        } else {
            possibleBoard[toRow][toCol] = squareContent;
        }
        possibleBoard[pressedSquare.getRow()][pressedSquare.getCol()] = "_";
        //if move is right castling:
        if (fromRow == toRow && toCol - fromCol == 2 &&
                possibleBoard[toRow][toCol].charAt(1) == 'k') {
            int col = toCol - 1; //of rook after castling
            possibleBoard[toRow][col] = possibleBoard[toRow][toCol].charAt(0) + "r";
            possibleBoard[toRow][7] = "_";
            }
        if (fromRow == toRow && fromCol - toCol == 2 &&
                possibleBoard[toRow][toCol].charAt(1) == 'k') {
            int col = toCol + 1; //of rook after castling
            possibleBoard[toRow][col] = possibleBoard[toRow][toCol].charAt(0) + "r";
            possibleBoard[toRow][0] = "_";
        }
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
        chessPiece.setCurrentSquare(new Square(toRow, toCol));
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
    }

    public void transformIntoQueen(Square square) {
        char color = 0;
        for (ChessPiece cp : chessPieces) {
            if (cp.getCurrentSquare().getRow() == square.getRow() && cp.getCurrentSquare().getCol() == square.getCol()) {
                color = cp.getColor();
                chessPieces.remove(cp);
                break;
            }
        }
        board[square.getRow()][square.getCol()] = color + "q";
        Queen queen = new Queen(color, square);
        chessPieces.add(queen);
    }

    public boolean hasMovesToDo(char ownColor, String[][] board) { //checks if passed color player has moves to do
        for (ChessPiece cp : chessPieces) {
            if (cp.color == ownColor && getPossibleSquaresWithNoOwnCheck(cp, board).size() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isBeingChecked(char ownColor, String[][] board) {
        char rivalColor = ownColor == 'w'? 'b' : 'w';
        for (ChessPiece cp : chessPieces) {
            if (cp.color == rivalColor && cp.isCheckingRivalKing(board, ownColor)) return true;
        }
        return false;
    }

    public boolean isMated(char ownColor, String[][] board) {
        return isBeingChecked(ownColor, board) && !(hasMovesToDo(ownColor, board));
    }

    public boolean isInStaleMate(char ownColor, String[][] board) {
        return !(isBeingChecked(ownColor, board)) && !(hasMovesToDo(ownColor, board));
    }

    public boolean isDeadPosition() {
        if (chessPieces.size() == 2) return true; //only kings
        if (chessPieces.size() == 3) { //only kings and one bishop:
            for (ChessPiece cp : chessPieces) {
                if (cp instanceof Bishop || cp instanceof Knight) return true;
            }
            return false;
        }
        if (chessPieces.size() == 4) { //only kings and two rival bishops on the same square color:
            if (numOfBishopsOnBoard() == 2) {
                Bishop bishop1 = null;
                Bishop bishop2 = null;
                for (ChessPiece cp : chessPieces) {
                    if (cp instanceof Bishop) {
                        if (bishop1 == null) bishop1 = (Bishop) cp;
                        else bishop2 = (Bishop) cp;
                    }
                }
                int row1 = bishop1.getCurrentSquare().getRow(), col1 = bishop1.getCurrentSquare().getCol(),
                        row2 = bishop2.currentSquare.getRow(), col2 = bishop2.getCurrentSquare().getCol();
                if (bishop1.color != bishop2.color && (row1+col1+row2+col2)%2==0) return true;
            }
        }
        return false;
    }

    private int numOfBishopsOnBoard() {
        int num = 0;
        for (ChessPiece cp : chessPieces) {
            if (cp instanceof Bishop) num++;
        }
        return num;
    }

    private ArrayList<Square> getPossibleSquaresWithNoOwnCheck(ChessPiece chessPiece, String[][] board) {
        ArrayList<Square> possibleSquares = chessPiece.getPossibleSquares(board);
        ArrayList<Square> squares = new ArrayList<>(possibleSquares); //copy array to iterate on (so changes and deletions on possibleSquares are possible).
        //check if own-"shakh" can occur due to possible move (bad scenario, invalid move)
        for (Square possibleSquare : squares) {
            String[][] possibleBoard = getBoardAfterPossibleMove(chessPiece.getCurrentSquare(), possibleSquare, board);
            if (isCreatingOwnCheck(possibleBoard, chessPiece.getColor(), possibleSquare)) {
                possibleSquares.remove(possibleSquare);
            }
        }
        return possibleSquares;
    }

    public double negaMax(char color, String[][] board, Square fromSquare, Square toSquare, int depth, int n) {
        if (depth == 0) {
            return evaluateChancesToWin(color, board);
        }
        double max = Double.NEGATIVE_INFINITY;
        ArrayList<Move> allMoves = getAllPossibleMoves(color, board);
        for (Move move : allMoves) {
            String[][] possibleBoard = move.getBoardAfterMove();
            ChessProcessor chessProcessorRec = new ChessProcessor(possibleBoard);
            if (chessProcessorRec.evaluateChancesToWin(color, possibleBoard) < max) continue;
            double score = (-1) * chessProcessorRec.negaMax(getRivalColor(color), possibleBoard, fromSquare, toSquare, depth - 1, n);
            if (score > max) {
                max = score;
                if (depth == n) { //changing fromSquare&toSquare only in the outer layer of the recursion
                    fromSquare.setRow(move.getFromSquare().getRow());
                    fromSquare.setCol(move.getFromSquare().getCol());
                    toSquare.setRow(move.getToSquare().getRow());
                    toSquare.setCol(move.getToSquare().getCol());
                }
            }
        }
        return max;
    }

    public double evaluateChancesToWin(char color, String[][] board) {
        double piecesValues = sumValueOfPieces(color, board) - sumValueOfPieces(getRivalColor(color), board);
        double piecesMobility = getAllPossibleMoves(color, board).size() - getAllPossibleMoves(getRivalColor(color), board).size();
        double kingSafety = evaluateKingSafety(color, board);
        double checkValue = isBeingChecked(getRivalColor(color), board)? 8 : 0;
        double ownCheckValue = isBeingChecked(color, board)? -8 : 0;
        double mateValue = isMated(getRivalColor(color), board)? Double.POSITIVE_INFINITY : 0;
        double ownMateValue = isMated(color, board)? Double.NEGATIVE_INFINITY : 0;
        double piecesDeveloping = sumValueOfPieces(color, board);
        double rivalPiecesDeveloping = sumValueOfPieces(getRivalColor(color), board);
        //TODO continue
        return piecesValues + 0.1*piecesMobility + 0.1*checkValue + 0.4*ownCheckValue + mateValue + ownMateValue + piecesDeveloping - rivalPiecesDeveloping;
    }

    private double piecesDeveloping(char color, String[][] board) {
        double sumOfPiecesInMidBoard = 0;
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].charAt(0) == color) sumOfPiecesInMidBoard += 1;
            }
        }
        return sumOfPiecesInMidBoard;
    }

    private double sumValueOfPieces(char color, String[][] board) {
        double sum = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].charAt(0) == color) {
                    sum += getPieceValue(board[i][j].charAt(1));
                }
            }
        }
        return sum;
    }

    private double getPieceValue(char pieceType) {
        double value = 0;
        switch(pieceType) {
            case 'r':
                value = 5;
                break;
            case 'n':
            case 'b':
                value = 3;
                break;
            case 'q':
                value = 9;
                break;
            case 'k':
                value = 200;
                break;
            case 'p':
                value = 1;
                break;
        }
        return value;
    }

    private double evaluateKingSafety(char color, String[][] board) {
        double safety = 0;
        int kingRow = 0, kingCol = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].equals(color + "k")) {
                    kingRow = i;
                    kingCol = j;
                    break;
                }
            }
        }
        int fromRowIndex = Math.max(0, kingRow - 2);
        int fromColIndex = Math.max(0, kingCol - 2);
        int toRowIndex = Math.min(7, kingRow + 2);
        int toColIndex = Math.min(7, kingCol + 2);
        for (int i = fromRowIndex; i <= toRowIndex; i++) {
            for (int j = fromColIndex; j <= toColIndex; j++) {
                if (board[i][j].equals(color + "p")) safety+=50;
            }
        }
        for (int i = fromRowIndex; i <= toRowIndex; i++) {
            for (int j = 0; j <= 7; j++) {
                if (board[i][j].charAt(0) != color) safety-=50;
            }
        }

        return safety;
    }

    private char getRivalColor(char color) {
        return color == 'w'? 'b' : 'w';
    }

    private ArrayList<Move> getAllPossibleMoves(char color, String[][] board) {
        ArrayList<Move> allMoves = new ArrayList<>();
        for (ChessPiece cp : chessPieces) {
            if (cp.color == color) {
                ArrayList<Square> possibleSquares = getPossibleSquaresWithNoOwnCheck(cp, board);
                addPossibleMovesOfCP(allMoves, possibleSquares, cp, board);
            }
        }
        return allMoves;
    }

    private void addPossibleMovesOfCP(ArrayList<Move> allMoves, ArrayList<Square> possibleSquares, ChessPiece cp, String[][] currBoard) {
        Square fromSquare = cp.getCurrentSquare();
        for (Square toSquare : possibleSquares) {
            String[][] possibleBoard = getBoardAfterPossibleMove(fromSquare, toSquare, currBoard);
            Move move = new Move(fromSquare, toSquare, possibleBoard);
            allMoves.add(move);
        }
    }
}
