package com.example.chessgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity {
    String[] colsToLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    String[][] startingBoardWhite =
            {{"br","bn","bb","bq","bk","bb","bn","br"},
            {"bp","bp","bp","bp","bp","bp","bp","bp"},
            {"_","_","_","_","_","_","_","_"},
            {"_","_","_","_","_","_","_","_"},
            {"_","_","_","_","_","_","_","_"},
            {"_","_","_","_","_","_","_","_"},
            {"wp","wp","wp","wp","wp","wp","wp","wp"},
            {"wr","wn","wb","wq","wk","wb","wn","wr"}};
    String[][] startingBoardBlack =
            {{"wr","wn","wb","wk","wq","wb","wn","wr"},
            {"wp","wp","wp","wp","wp","wp","wp","wp"},
            {"_","_","_","_","_","_","_","_"},
            {"_","_","_","_","_","_","_","_"},
            {"_","_","_","_","_","_","_","_"},
            {"_","_","_","_","_","_","_","_"},
            {"bp","bp","bp","bp","bp","bp","bp","bp"},
            {"br","bn","bb","bk","bq","bb","bn","br"}};
    String[][] board = null;
    Square currentlyPressed1 = new Square(-1, -1); //-1,-1 means there isn't a square pressed
    Square currentlyPressed2 =  new Square(-1, -1); //2 is for highlighting 2 squares after making a move
    int clockTimeChoice;
    TextView upperTimer;
    TextView bottomTimer;
    GridLayout chessBoard;
    CountDownTimer upperCounter;
    CountDownTimer bottomCounter;
    int isWhite;
    int isUnlimited = 0;
    int isBottomTurn = 0;
    ChessProcessor chessProcessor;

    ArrayList<Square> highlightedSquares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        clockTimeChoice = intent.getIntExtra("clockTimeChoice", 3);
        upperTimer = findViewById(R.id.upperTimer);
        bottomTimer = findViewById(R.id.bottomTimer);
        chessBoard = findViewById(R.id.chessBoard);
        isWhite = ThreadLocalRandom.current().nextInt(1, 2 + 1);
        if (isWhite == 1) {
            board = startingBoardWhite;
        }
        else {
            board = startingBoardBlack;}
        setBoard(board);
        chessProcessor = new ChessProcessor(board);
        setTimers();
    }
    private void setTimers(){
        Typeface timerFont = Typeface.createFromAsset(getAssets(), "font/digital-7.ttf");
        upperTimer.setTypeface(timerFont);
        bottomTimer.setTypeface(timerFont);
        String startingTime = null;
        int startingTimeMillis = 0;
        switch(clockTimeChoice) {
            case 1:
                startingTime = "02:00";
                startingTimeMillis = 2 * 60 * 1000;
                break;
            case 2:
                startingTime = "05:00";
                startingTimeMillis = 5 * 60 * 1000;
                break;
            case 3:
                startingTime = "10:00";
                startingTimeMillis = 10 * 60 * 1000;
                break;
            case 4:
                isUnlimited = 1;
                return;
        }
        upperTimer.setText(startingTime);
        bottomTimer.setText(startingTime);
        int bottomStartingTimeMillis, upperStartingTimeMillis;
        if (isWhite == 1)  {
            bottomStartingTimeMillis = startingTimeMillis + 100;
            upperStartingTimeMillis = startingTimeMillis;
        }
        else {
            bottomStartingTimeMillis = startingTimeMillis;
            upperStartingTimeMillis = startingTimeMillis + 100;
        }
        //set the counter (being the timers' indicators):
        upperCounter = new CountDownTimer(upperStartingTimeMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                upperTimer.setText(f.format(min) + ":" + f.format(sec));
            }
            public void onFinish() {
                upperTimer.setText("00:00");
            }
        };
        bottomCounter = new CountDownTimer(bottomStartingTimeMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                bottomTimer.setText(f.format(min) + ":" + f.format(sec));
            }
            public void onFinish() {
                bottomTimer.setText("00:00");
            }
        };
        if (isWhite == 1) {
            bottomCounter.start();
            isBottomTurn = 1;
        }
        else upperCounter.start();
    }

    private void setBoard(String[][] startingBoard){
        for (int row = 0; row <= 7; row++) {
            for (int col = 0; col <= 7; col++) {
                // each square (including chess piece if any) is a FrameLayout (enabling stacking 2 views)
                FrameLayout fl = new FrameLayout(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                //each FrameLayout is 1row-1column sized in the GridLayout:
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.width = 0;
                params.height = 0;
                fl.setLayoutParams(params);

                // set original (non pressed) square background according to index:
                setOriginalBackgroundByIndex(fl, row, col);

                //indexes (displayed via TextViews):
                if (col == 0) {
                    TextView index = new TextView(this);
                    index.setText(String.valueOf(8-row));
                    index.setTextSize(12);
                    FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    indexParams.setMargins(3,0,0,0);
                    indexParams.gravity = Gravity.START | Gravity.TOP;
                    index.setLayoutParams(indexParams);
                    fl.addView(index);
                }
                if (row == 7) {
                    TextView index = new TextView(this);
                    index.setText(colsToLetters[col]);
                    index.setTextSize(12);
                    FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    indexParams.setMargins(0,0,3,0);
                    indexParams.gravity = Gravity.END | Gravity.BOTTOM;
                    index.setLayoutParams(indexParams);
                    fl.addView(index);
                }

                // the chess pieces are ImageViews:
                String squareContent = startingBoard[row][col];
                if (!(squareContent.equals("_"))){
                    ImageView chessPiece = getPieceFromString(squareContent);
                    fl.addView(chessPiece);
                }

                //add the square with the piece to the gridLayout (generating the board that way):
                chessBoard.addView(fl);

                //add functionality:
                addFunctionality(fl);
            }
        }
    }

    private ImageView getPieceFromString(String squareContent) {
        ImageView chessPiece = new ImageView(this);
        switch(squareContent) {
            case "br":
                chessPiece.setImageResource(R.drawable.black_rook);
                break;
            case "bn":
                chessPiece.setImageResource(R.drawable.black_knight);
                break;
            case "bb":
                chessPiece.setImageResource(R.drawable.black_bishop);
                break;
            case "bq":
                chessPiece.setImageResource(R.drawable.black_queen);
                break;
            case "bk":
                chessPiece.setImageResource(R.drawable.black_king);
                break;
            case "bp":
                chessPiece.setImageResource(R.drawable.black_pawn);
                break;
            case "wr":
                chessPiece.setImageResource(R.drawable.white_rook);
                break;
            case "wn":
                chessPiece.setImageResource(R.drawable.white_knight);
                break;
            case "wb":
                chessPiece.setImageResource(R.drawable.white_bishop);
                break;
            case "wq":
                chessPiece.setImageResource(R.drawable.white_queen);
                break;
            case "wk":
                chessPiece.setImageResource(R.drawable.white_king);
                break;
            case "wp":
                chessPiece.setImageResource(R.drawable.white_pawn);
                break;
        }
        return chessPiece;
    }

    private void addFunctionality(FrameLayout fl) {
        int squareIndex = chessBoard.indexOfChild(fl);
        int row = squareIndex / 8, col = squareIndex % 8;
        fl.setOnClickListener(view -> {
            // background for chosen square:
            Drawable pressedSquareBg = ContextCompat.getDrawable(this, R.drawable.pressed_square);
            char firstCharOfColor = 0; //can be 'w' or 'b'
            if (isBottomTurn == 1) { //respond to clicks only when user's turn
                if (isWhite == 1) { //get first char of user's color (is used later):
                    firstCharOfColor = 'w';
                } else {
                    firstCharOfColor = 'b';
                }
                // fl is the frameLayout of the square just clicked
                // (row, col) is the index of the square corresponding to the last click
                if (currentlyPressed1.getRow() == -1) { //if no square is previously pressed:
                    // if no chess-piece on the just-pressed square, no need to do anything.
                    if (this.board[row][col].equals("_")) {
                        return;
                    } else { //else, a square with a chess piece was chosen. color that square accordingly:
                        currentlyPressed1.setRow(row);
                        currentlyPressed1.setCol(col);
                        fl.setBackground(pressedSquareBg);
                        if (board[row][col].charAt(0) == firstCharOfColor) {
                            //also, if that piece is of own color, highlight squares that piece can move to
                            highlightPossibleSquares(new Square(row, col));
                        }
                    }
                } //else, a square with a chess piece was previously chosen:
                else if (currentlyPressed1.getRow() == row && currentlyPressed1.getCol() == col) { //if already-chosen square is pressed, un-choose it:
                    cleanChoice(fl, row, col);
                } else { //if there is a chosen square and user chooses another one:
                    if (highlightedSquares.contains(new Square (row, col))) {
                        currentlyPressed2.setRow(row);
                        currentlyPressed2.setCol(col);
                        fl.setBackground(pressedSquareBg);
                        //TODO: update board visually and data-wise
                    }
                    // else, if an un-highlighted square with no chess-piece is chosen, clean previous choice:
                    else if (this.board[row][col].equals("_")) {
                        FrameLayout alreadyChosen_fl = (FrameLayout) chessBoard.getChildAt(currentlyPressed1.getRow()*8 + currentlyPressed1.getCol());
                        cleanChoice(alreadyChosen_fl, currentlyPressed1.getRow(), currentlyPressed1.getCol());
                    } //else, an un-highlighted square with chess piece was chosen:
                    else { //if another piece is chosen, change choice:
                        //clean previous choice:
                        FrameLayout alreadyChosen_fl = (FrameLayout) chessBoard.getChildAt(currentlyPressed1.getRow()*8 + currentlyPressed1.getCol());
                        cleanChoice(alreadyChosen_fl, currentlyPressed1.getRow(), currentlyPressed1.getCol());
                        //choose new square:
                        currentlyPressed1.setRow(row);
                        currentlyPressed1.setCol(col);
                        fl.setBackground(pressedSquareBg);
                        if (board[row][col].charAt(0) == firstCharOfColor) {
                            highlightPossibleSquares(new Square(row, col));
                        }
                    }
                }
            }
        });
    }

    private void highlightPossibleSquares(Square pressedSquare) {
        highlightedSquares = chessProcessor.getPossibleSquares(pressedSquare);
        for (Square square : highlightedSquares) {
            int row = square.getRow();
            int col = square.getCol();
            FrameLayout fl = (FrameLayout) chessBoard.getChildAt(row * 8 + col);
            TextView whiteCircle = createWhiteCircle(square);
            fl.addView(whiteCircle, 0);
        }
    }

    private void unhighlightSquares() {
        for (Square square : highlightedSquares) {
            int i = square.getRow();
            int j = square.getCol();
            FrameLayout fl = (FrameLayout) chessBoard.getChildAt(i * 8 + j);
            fl.removeViewAt(0);
        }
        highlightedSquares.clear();
    }

    private void cleanChoice(FrameLayout fl, int row, int col) {
        setOriginalBackgroundByIndex(fl, row, col);
        currentlyPressed1.setRow(-1);
        currentlyPressed1.setCol(-1);
        //also, unhighlight previously-highlighted squares
        unhighlightSquares();
    }

    TextView createWhiteCircle(Square square) {
        TextView whiteCircle = new TextView(this);
        Typeface iconsFont = Typeface.createFromAsset(getAssets(), "font/Icons South St.ttf");
        whiteCircle.setTypeface(iconsFont);
        whiteCircle.setGravity(Gravity.CENTER);
        whiteCircle.setText("e");
        whiteCircle.setTextColor(getColor(R.color.transparent_white));
        if (square.getIsEmpty() == 0) {
            whiteCircle.setTextSize(25);
        }
        return whiteCircle;
    }

    private void setOriginalBackgroundByIndex(FrameLayout fl, int row, int col) {
        if ((row + col) % 2 == 0) {
            Drawable lightSquareBg = ContextCompat.getDrawable(this, R.drawable.light_square);
            fl.setBackground(lightSquareBg);
        } else {
            Drawable darkSquareBg = ContextCompat.getDrawable(this, R.drawable.dark_square);
            fl.setBackground(darkSquareBg);
        }
    }

}