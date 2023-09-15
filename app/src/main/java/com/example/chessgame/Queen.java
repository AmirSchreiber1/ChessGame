package com.example.chessgame;

import java.util.ArrayList;

public class Queen extends ChessPiece {

    public Queen(char color, Square currentSquare) {
        super(color, currentSquare);
    }

    @Override
    public ArrayList<Square> getPossibleSquares(String[][] board) {
        ArrayList<Square> possibleSquares = new ArrayList<>();
        possibleSquares.addAll(getPossibleSquares_upwardsVertical(board));
        possibleSquares.addAll(getPossibleSquares_downwardsVertical(board));
        possibleSquares.addAll(getPossibleSquares_rightHorizontal(board));
        possibleSquares.addAll(getPossibleSquares_leftHorizontal(board));
        possibleSquares.addAll(getPossibleSquares_upRightDiagonal(board));
        possibleSquares.addAll(getPossibleSquares_upLeftDiagonal(board));
        possibleSquares.addAll(getPossibleSquares_downRightDiagonal(board));
        possibleSquares.addAll(getPossibleSquares_downLeftDiagonal(board));
        return possibleSquares;
    }
}
