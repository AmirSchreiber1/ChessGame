package com.example.chessgame;

import java.util.ArrayList;

public class Bishop extends ChessPiece{
    public Bishop(char color, Square currentSquare) {
        super(color, currentSquare);
    }

    public Bishop(Bishop bishop) {
        super(bishop);
    }

    @Override
    public ArrayList<Square> getPossibleSquares(String[][] board) {
        ArrayList<Square> possibleSquares = new ArrayList<>();
        possibleSquares.addAll(getPossibleSquares_upRightDiagonal(board));
        possibleSquares.addAll(getPossibleSquares_upLeftDiagonal(board));
        possibleSquares.addAll(getPossibleSquares_downRightDiagonal(board));
        possibleSquares.addAll(getPossibleSquares_downLeftDiagonal(board));
        return possibleSquares;
    }
}
