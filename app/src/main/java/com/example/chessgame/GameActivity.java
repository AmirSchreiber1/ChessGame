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
import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity {
    String[] colsToLetters = {"0", "a", "b", "c", "d", "e", "f", "g", "h"};
    int clockTimeChoice;
    TextView upperTimer;
    TextView bottomTimer;
    GridLayout chessBoard;
    CountDownTimer upperCounter;
    CountDownTimer bottomCounter;
    int isWhite;
    int isUnlimited = 0;
    int isBottomTurn = 0;

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
            setBoardWhite();
        }
        else {
            setBoardBlack();}
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
        //set the counter (being the timers indicators):
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
        if (isWhite == 1) bottomCounter.start();
        else upperCounter.start();
    }

    private void setBoardWhite(){
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                // each square (including chess piece if any) is a FrameLayout (enabling stacking 2 views)
                FrameLayout fl = new FrameLayout(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                //each FrameLayout is 1row-1column sized in the GridLayout:
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.width = 0;
                params.height = 0;
                fl.setLayoutParams(params);

                // the square itself, without the chess piece, is an ImageView
                ImageView square = new ImageView(this);
                if ((row + col) % 2 == 0) {
                    Drawable lightSquareBg = ContextCompat.getDrawable(this, R.drawable.light_square);
                    square.setBackground(lightSquareBg);
                } else {
                    Drawable darkSquareBg = ContextCompat.getDrawable(this, R.drawable.dark_square);
                    square.setBackground(darkSquareBg);
                }
                fl.addView(square);

                //indexes (displayed via TextViews):
                if (col == 1) {
                    TextView index = new TextView(this);
                    index.setText(String.valueOf(9-row));
                    index.setTextSize(12);
                    FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    indexParams.setMargins(3,0,0,0);
                    indexParams.gravity = Gravity.START | Gravity.TOP;
                    index.setLayoutParams(indexParams);
                    fl.addView(index);
                }
                if (row == 8) {
                    TextView index = new TextView(this);
                    index.setText(colsToLetters[col]);
                    index.setTextSize(12);
                    FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    indexParams.setMargins(0,0,3,0);
                    indexParams.gravity = Gravity.END | Gravity.BOTTOM;
                    index.setLayoutParams(indexParams);
                    fl.addView(index);
                }

                // the chess pieces are also ImageViews:
                //pawns:
                if (row == 2) {
                    ImageView blackPawn = new ImageView(this);
                    blackPawn.setImageResource(R.drawable.black_pawn);
                    fl.addView(blackPawn);
                }
                if (row == 7){
                    ImageView whitePawn = new ImageView(this);
                    whitePawn.setImageResource(R.drawable.white_pawn);
                    fl.addView(whitePawn);
                }
                //rooks:
                if (row == 1 && (col == 1 || col == 8)) {
                    ImageView blackRook = new ImageView(this);
                    blackRook.setImageResource(R.drawable.black_rook);
                    fl.addView(blackRook);
                }
                if (row == 8 && (col == 1 || col == 8)) {
                    ImageView whiteRook = new ImageView(this);
                    whiteRook.setImageResource(R.drawable.white_rook);
                    fl.addView(whiteRook);
                }
                //knights:
                if (row == 1 && (col == 2 || col == 7)) {
                    ImageView blackKnight = new ImageView(this);
                    blackKnight.setImageResource(R.drawable.black_knight);
                    fl.addView(blackKnight);
                }
                if (row == 8 && (col == 2 || col == 7)) {
                    ImageView whiteKnight = new ImageView(this);
                    whiteKnight.setImageResource(R.drawable.white_knight);
                    fl.addView(whiteKnight);
                }
                //bishops:
                if (row == 1 && (col == 3 || col == 6)) {
                    ImageView blackBishop = new ImageView(this);
                    blackBishop.setImageResource(R.drawable.black_bishop);
                    fl.addView(blackBishop);
                }
                if (row == 8 && (col == 3 || col == 6)) {
                    ImageView whiteBishop = new ImageView(this);
                    whiteBishop.setImageResource(R.drawable.white_bishop);
                    fl.addView(whiteBishop);
                }
                //queens:
                if (row == 1 && col == 4) {
                    ImageView blackQueen = new ImageView(this);
                    blackQueen.setImageResource(R.drawable.black_queen);
                    fl.addView(blackQueen);
                }
                if (row == 8 && (col == 4)) {
                    ImageView whiteQueen = new ImageView(this);
                    whiteQueen.setImageResource(R.drawable.white_queen);
                    fl.addView(whiteQueen);
                }
                //kings:
                if (row == 1 && col == 5) {
                    ImageView blackKing = new ImageView(this);
                    blackKing.setImageResource(R.drawable.black_king);
                    fl.addView(blackKing);
                }
                if (row == 8 && (col == 5)) {
                    ImageView whiteKing = new ImageView(this);
                    whiteKing.setImageResource(R.drawable.white_king);
                    fl.addView(whiteKing);
                }
                //add the square with the piece to the gridLayout (generating the board that way):
                chessBoard.addView(fl);
            }
        }
    }

    private void setBoardBlack(){
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                // each square (including chess piece if any) is a FrameLayout (enabling stacking 2 views)
                FrameLayout fl = new FrameLayout(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                //each FrameLayout is 1row-1column sized in the GridLayout:
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.width = 0;
                params.height = 0;
                fl.setLayoutParams(params);

                // the square itself, without the chess piece, is an ImageView
                ImageView square = new ImageView(this);
                if ((row + col) % 2 == 0) {
                    Drawable lightSquareBg = ContextCompat.getDrawable(this, R.drawable.light_square);
                    square.setBackground(lightSquareBg);
                } else {
                    Drawable darkSquareBg = ContextCompat.getDrawable(this, R.drawable.dark_square);
                    square.setBackground(darkSquareBg);
                }
                fl.addView(square);

                //indexes (displayed via TextViews):
                if (col == 1) {
                    TextView index = new TextView(this);
                    index.setText(String.valueOf(9-row));
                    index.setTextSize(12);
                    FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    indexParams.setMargins(3,0,0,0);
                    indexParams.gravity = Gravity.START | Gravity.TOP;
                    index.setLayoutParams(indexParams);
                    fl.addView(index);
                }
                if (row == 8) {
                    TextView index = new TextView(this);
                    index.setText(colsToLetters[col]);
                    index.setTextSize(12);
                    FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    indexParams.setMargins(0,0,3,0);
                    indexParams.gravity = Gravity.END | Gravity.BOTTOM;
                    index.setLayoutParams(indexParams);
                    fl.addView(index);
                }

                // the chess pieces are also ImageViews:
                //pawns:
                if (row == 7) {
                    ImageView blackPawn = new ImageView(this);
                    blackPawn.setImageResource(R.drawable.black_pawn);
                    fl.addView(blackPawn);
                }
                if (row == 2){
                    ImageView whitePawn = new ImageView(this);
                    whitePawn.setImageResource(R.drawable.white_pawn);
                    fl.addView(whitePawn);
                }
                //rooks:
                if (row == 8 && (col == 1 || col == 8)) {
                    ImageView blackRook = new ImageView(this);
                    blackRook.setImageResource(R.drawable.black_rook);
                    fl.addView(blackRook);
                }
                if (row == 1 && (col == 1 || col == 8)) {
                    ImageView whiteRook = new ImageView(this);
                    whiteRook.setImageResource(R.drawable.white_rook);
                    fl.addView(whiteRook);
                }
                //knights:
                if (row == 8 && (col == 2 || col == 7)) {
                    ImageView blackKnight = new ImageView(this);
                    blackKnight.setImageResource(R.drawable.black_knight);
                    fl.addView(blackKnight);
                }
                if (row == 1 && (col == 2 || col == 7)) {
                    ImageView whiteKnight = new ImageView(this);
                    whiteKnight.setImageResource(R.drawable.white_knight);
                    fl.addView(whiteKnight);
                }
                //bishops:
                if (row == 8 && (col == 3 || col == 6)) {
                    ImageView blackBishop = new ImageView(this);
                    blackBishop.setImageResource(R.drawable.black_bishop);
                    fl.addView(blackBishop);
                }
                if (row == 1 && (col == 3 || col == 6)) {
                    ImageView whiteBishop = new ImageView(this);
                    whiteBishop.setImageResource(R.drawable.white_bishop);
                    fl.addView(whiteBishop);
                }
                //queens:
                if (row == 8 && col == 4) {
                    ImageView blackQueen = new ImageView(this);
                    blackQueen.setImageResource(R.drawable.black_queen);
                    fl.addView(blackQueen);
                }
                if (row == 1 && (col == 4)) {
                    ImageView whiteQueen = new ImageView(this);
                    whiteQueen.setImageResource(R.drawable.white_queen);
                    fl.addView(whiteQueen);
                }
                //kings:
                if (row == 8 && col == 5) {
                    ImageView blackKing = new ImageView(this);
                    blackKing.setImageResource(R.drawable.black_king);
                    fl.addView(blackKing);
                }
                if (row == 1 && (col == 5)) {
                    ImageView whiteKing = new ImageView(this);
                    whiteKing.setImageResource(R.drawable.white_king);
                    fl.addView(whiteKing);
                }
                //add the square with the piece to the gridLayout (generating the board that way):
                chessBoard.addView(fl);
            }
        }
    }

}