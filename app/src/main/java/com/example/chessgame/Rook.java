package com.example.chessgame;

import java.util.ArrayList;

public class Rook extends ChessPiece {
    private boolean stillNotMoved;

    public Rook(char color, Square currentSquare) {
        super(color, currentSquare);
        this.stillNotMoved = true;
    }

    public boolean getStillNotMoved() {
        return stillNotMoved;
    }

    public void setStillNotMoved(boolean stillNotMoved) {
        this.stillNotMoved = stillNotMoved;
    }

    @Override
    public ArrayList<Square> getPossibleSquares(String[][] board) {
        ArrayList<Square> possibleSquares = new ArrayList<>();
        possibleSquares.addAll(getPossibleSquares_upwardsVertical(board));
        possibleSquares.addAll(getPossibleSquares_downwardsVertical(board));
        possibleSquares.addAll(getPossibleSquares_rightHorizontal(board));
        possibleSquares.addAll(getPossibleSquares_leftHorizontal(board));
        return possibleSquares;
    }
}
