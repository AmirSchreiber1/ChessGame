package com.example.chessgame;

import java.util.ArrayList;

public class Knight extends ChessPiece {
    public Knight(char color, Square currentSquare) {
        super(color, currentSquare);
    }

    public Knight(Knight knight) {
        super(knight);
    }

    @Override
    public ArrayList<Square> getPossibleSquares(String[][] board) {
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = this.currentSquare.getRow();
        int col = this.currentSquare.getCol();
        //option 1:
        checkAndAddPossibleSquare(board, possibleSquares, row + 2, col + 1);
        //option 2:
        checkAndAddPossibleSquare(board, possibleSquares, row + 2, col - 1);
        //option 3:
        checkAndAddPossibleSquare(board, possibleSquares, row + 1, col + 2);
        //option 4:
        checkAndAddPossibleSquare(board, possibleSquares, row + 1, col - 2);
        //option 5:
        checkAndAddPossibleSquare(board, possibleSquares, row - 2, col + 1);
        //option 6:
        checkAndAddPossibleSquare(board, possibleSquares, row - 2, col - 1);
        //option 7:
        checkAndAddPossibleSquare(board, possibleSquares, row - 1, col + 2);
        //option 8:
        checkAndAddPossibleSquare(board, possibleSquares, row - 1, col - 2);

        return possibleSquares;
    }
}
