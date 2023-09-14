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
            setBoard(startingBoardWhite);
        }
        else {
            setBoard(startingBoardBlack);}
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
}