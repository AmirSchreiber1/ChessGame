package com.example.chessgame;

public class Move {
    private final Square fromSquare;
    private final Square toSquare;
    private String[][] boardAfterMove = null;

    public Move(Square fromSquare, Square toSquare, String[][] boardAfterMove) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.boardAfterMove = boardAfterMove;
    }

    public Square getFromSquare() {
        return fromSquare;
    }

    public Square getToSquare() {
        return toSquare;
    }

    public String[][] getBoardAfterMove() {
        return boardAfterMove;
    }
}
