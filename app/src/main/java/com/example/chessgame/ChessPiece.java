package com.example.chessgame;

import java.sql.Array;
import java.util.ArrayList;

public abstract class ChessPiece {
    protected final char color; //can be 'w' or 'b'
    protected Square currentSquare;
    private boolean isAlive;

    public ChessPiece(char color, Square currentSquare) {
        this.color = color;
        this.currentSquare = currentSquare;
        this.isAlive = true;
    }

    public ChessPiece(ChessPiece chessPiece) {
        this.color = chessPiece.getColor();
        this.currentSquare = new Square(chessPiece.getCurrentSquare());
        this.isAlive = chessPiece.isAlive();
    }

    public char getColor() {
        return color;
    }

    public Square getCurrentSquare() {
        return currentSquare;
    }

    public void setCurrentSquare(Square currentSquare) {
        this.currentSquare = currentSquare;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public abstract ArrayList<Square> getPossibleSquares(String[][] board);

    public ArrayList<Square> getPossibleSquares_upwardsVertical(String[][] board){
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = this.currentSquare.getRow();
        int col = this.currentSquare.getCol();
        for (int i = row - 1; i >= 0; i--) {
            if (board[i][col].equals("_")) {
                Square newSquare = new Square(i, col);
                possibleSquares.add(newSquare);
            }
            else if (board[i][col].charAt(0) == color) {
                break;
            }
            else {
                Square newSquare = new Square(i, col);
                newSquare.setIsEmpty(0); //means there is a rival piece in that square
                possibleSquares.add(newSquare);
                break;
            }
        }
        return possibleSquares;
    }

    public ArrayList<Square> getPossibleSquares_downwardsVertical(String[][] board){
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = currentSquare.getRow();
        int col = currentSquare.getCol();
        for (int i = row + 1; i < 8; i++) {
            if (board[i][col].equals("_")) {
                Square newSquare = new Square(i, col);
                possibleSquares.add(newSquare);
            }
            else if (board[i][col].charAt(0) == color) {
                break;
            }
            else {
                Square newSquare = new Square(i, col);
                newSquare.setIsEmpty(0);
                possibleSquares.add(newSquare);
                break;
            }
        }
        return possibleSquares;
    }

    public ArrayList<Square> getPossibleSquares_rightHorizontal(String[][] board){
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = currentSquare.getRow();
        int col = currentSquare.getCol();
        for (int j = col + 1; j < 8; j++) {
            if (board[row][j].equals("_")) {
                Square newSquare = new Square(row, j);
                possibleSquares.add(newSquare);
            }
            else if (board[row][j].charAt(0) == color) {
                break;
            }
            else {
                Square newSquare = new Square(row, j);
                newSquare.setIsEmpty(0);
                possibleSquares.add(newSquare);
                break;
            }
        }
        return possibleSquares;
    }

    public ArrayList<Square> getPossibleSquares_leftHorizontal(String[][] board){
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = currentSquare.getRow();
        int col = currentSquare.getCol();
        for (int j = col - 1; j >= 0; j--) {
            if (board[row][j].equals("_")) {
                Square newSquare = new Square(row, j);
                possibleSquares.add(newSquare);
            }
            else if (board[row][j].charAt(0) == color) {
                break;
            }
            else {
                Square newSquare = new Square(row, j);
                newSquare.setIsEmpty(0);
                possibleSquares.add(newSquare);
                break;
            }
        }
        return possibleSquares;
    }

    public ArrayList<Square> getPossibleSquares_downRightDiagonal(String[][] board){
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = currentSquare.getRow();
        int col = currentSquare.getCol();
        for (int i = row + 1, j = col + 1; i < 8 && j < 8; i++, j++) {
            if (board[i][j].equals("_")) {
                Square newSquare = new Square(i, j);
                possibleSquares.add(newSquare);
            }
            else if (board[i][j].charAt(0) == color) {
                break;
            }
            else {
                Square newSquare = new Square(i, j);
                newSquare.setIsEmpty(0);
                possibleSquares.add(newSquare);
                break;
            }
        }
        return possibleSquares;
    }

    public ArrayList<Square> getPossibleSquares_downLeftDiagonal(String[][] board){
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = currentSquare.getRow();
        int col = currentSquare.getCol();
        for (int i = row + 1, j = col - 1; i < 8 && j >= 0; i++, j--) {
            if (board[i][j].equals("_")) {
                Square newSquare = new Square(i, j);
                possibleSquares.add(newSquare);
            }
            else if (board[i][j].charAt(0) == color) {
                break;
            }
            else {
                Square newSquare = new Square(i, j);
                newSquare.setIsEmpty(0);
                possibleSquares.add(newSquare);
                break;
            }
        }
        return possibleSquares;
    }

    public ArrayList<Square> getPossibleSquares_upRightDiagonal(String[][] board){
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = currentSquare.getRow();
        int col = currentSquare.getCol();
        for (int i = row - 1, j = col + 1; i >= 0 && j < 8; i--, j++) {
            if (board[i][j].equals("_")) {
                Square newSquare = new Square(i, j);
                possibleSquares.add(newSquare);
            }
            else if (board[i][j].charAt(0) == color) {
                break;
            }
            else {
                Square newSquare = new Square(i, j);
                newSquare.setIsEmpty(0);
                possibleSquares.add(newSquare);
                break;
            }
        }
        return possibleSquares;
    }

    public ArrayList<Square> getPossibleSquares_upLeftDiagonal(String[][] board){
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = currentSquare.getRow();
        int col = currentSquare.getCol();
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j].equals("_")) {
                Square newSquare = new Square(i, j);
                possibleSquares.add(newSquare);
            }
            else if (board[i][j].charAt(0) == color) {
                break;
            }
            else {
                Square newSquare = new Square(i, j);
                newSquare.setIsEmpty(0);
                possibleSquares.add(newSquare);
                break;
            }
        }
        return possibleSquares;
    }

    protected void checkAndAddPossibleSquare(String[][] board, ArrayList<Square> possibleSquares , int possibleRow, int possibleCol) {
        if (possibleRow < 8 && possibleCol < 8 && possibleRow >= 0 && possibleCol >= 0) {
            if (board[possibleRow][possibleCol].equals("_")) {
                Square newSquare = new Square(possibleRow, possibleCol);
                possibleSquares.add(newSquare);
            }
            else if (board[possibleRow][possibleCol].charAt(0) != this.color) {
                Square newSquare = new Square(possibleRow, possibleCol);
                newSquare.setIsEmpty(0);
                possibleSquares.add(newSquare);
            }
        }
    }

    public boolean isCheckingRivalKing(String[][] board, char rivalColor) {
        String rivalKing = rivalColor + "k";
        ArrayList<Square> possibleSquares = getPossibleSquares(board);
        for (Square square : possibleSquares) {
            int i = square.getRow();
            int j = square.getCol();
            if (board[i][j].equals(rivalKing)) {
                return true;
            }
        }
        return false;
    }
}
