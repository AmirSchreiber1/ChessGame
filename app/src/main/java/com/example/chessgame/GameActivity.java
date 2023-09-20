package com.example.chessgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
    Square currentlyPressedRival1 = new Square(-1, -1);
    Square currentlyPressedRival2 = new Square(-1, -1);
    int clockTimeChoice;
    Drawable pressedSquareBg;

    CountDownTimer timer;
    TextView upperTimerTV;
    TextView bottomTimerTV;
    long timeLeftBottom;
    long timeLeftUp;
    GridLayout chessBoard;
    public static int isWhite;
    int isUnlimited = 0;
    int isBottomTurn = 0;
    ChessProcessor chessProcessor;

    ArrayList<Square> highlightedSquares;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        clockTimeChoice = intent.getIntExtra("clockTimeChoice", 3);
        upperTimerTV = findViewById(R.id.upperTimer);
        bottomTimerTV = findViewById(R.id.bottomTimer);
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
        pressedSquareBg = ContextCompat.getDrawable(this, R.drawable.pressed_square);

    }
    private void setTimers(){
        Typeface timerFont = Typeface.createFromAsset(getAssets(), "font/digital-7.ttf");
        upperTimerTV.setTypeface(timerFont);
        bottomTimerTV.setTypeface(timerFont);
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
                if (isWhite == 1) {
                    playOwnTurn();
                } else {
                    playRivalTurn();
                }
                return;
        }
        upperTimerTV.setText(startingTime);
        bottomTimerTV.setText(startingTime);
        if (isWhite == 1)  {
            timeLeftBottom = startingTimeMillis + 100;
            timeLeftUp = startingTimeMillis;
            playOwnTurn();
        }
        else {
            timeLeftBottom = startingTimeMillis;
            timeLeftUp = startingTimeMillis + 100;
            playRivalTurn();
        }
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

                // make sure the frameLayouts aren't blocking/hiding their children (the chess pieces):
                fl.setClipChildren(false);
                fl.setElevation(5f);

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
            char ownColor = 0; //can be 'w' or 'b'
            if (isBottomTurn == 1) { //respond to clicks only when user's turn
                if (isWhite == 1) { //get first char of user's color (is used later):
                    ownColor = 'w';
                } else {
                    ownColor = 'b';
                }

                // fl is the frameLayout of the square just clicked
                // (row, col) is the index of the square corresponding to the last click


                if (currentlyPressed1.getRow() == -1) { //if no square is previously pressed:
                    //don't let user recolor last square rival moved a piece to
                    if (new Square(row,col).equals(currentlyPressedRival2) &&
                            !(highlightedSquares.contains(currentlyPressedRival2))) {
                        return;
                    }

                    // if no chess-piece on the just-pressed square, no need to do anything.
                    if (this.board[row][col].equals("_")) {
                        return;
                    } else { //else, a square with a chess piece was chosen. color that square accordingly:
                        currentlyPressed1.setRow(row);
                        currentlyPressed1.setCol(col);
                        fl.setBackground(pressedSquareBg);
                        if (board[row][col].charAt(0) == ownColor) {
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
                        unhighlightSquares(); //no more need to highlight possible squares after doing a valid move
                        generateMove(fl);
                    }
                    // else, if an un-highlighted square with no chess-piece is chosen, clean previous choice:
                    else if (this.board[row][col].equals("_")) {
                        FrameLayout alreadyChosen_fl = (FrameLayout) chessBoard.getChildAt(currentlyPressed1.getRow()*8 + currentlyPressed1.getCol());
                        cleanChoice(alreadyChosen_fl, currentlyPressed1.getRow(), currentlyPressed1.getCol());
                    }
                    else { //else, another piece is chosen, change choice:
                        if (new Square(row,col).equals(currentlyPressedRival2) &&
                                !(highlightedSquares.contains(currentlyPressedRival2))) {
                            FrameLayout alreadyChosen_fl = (FrameLayout) chessBoard.getChildAt(currentlyPressed1.getRow()*8 + currentlyPressed1.getCol());
                            cleanChoice(alreadyChosen_fl, currentlyPressed1.getRow(), currentlyPressed1.getCol());
                            return;
                        }
                        // clean previous choice:
                        FrameLayout alreadyChosen_fl = (FrameLayout) chessBoard.getChildAt(currentlyPressed1.getRow()*8 + currentlyPressed1.getCol());
                        cleanChoice(alreadyChosen_fl, currentlyPressed1.getRow(), currentlyPressed1.getCol());
                        //choose new square:
                        currentlyPressed1.setRow(row);
                        currentlyPressed1.setCol(col);
                        fl.setBackground(pressedSquareBg);
                        if (board[row][col].charAt(0) == ownColor) {
                            highlightPossibleSquares(new Square(row, col));
                        }
                    }
                }
            }
        });
    }

    private void highlightPossibleSquares(Square pressedSquare) {
        highlightedSquares = chessProcessor.getPossibleSquares(pressedSquare, board);
        for (Square square : highlightedSquares) {
            int row = square.getRow();
            int col = square.getCol();
            FrameLayout fl = (FrameLayout) chessBoard.getChildAt(row * 8 + col);
            TextView whiteCircle = createWhiteCircle(square);
            fl.addView(whiteCircle, 0);
        }
    }

    private void unhighlightSquares() {
        if (highlightedSquares == null) return;
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
        if (!(board[square.getRow()][square.getCol()].equals("_"))) {
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

    private void generateMove(FrameLayout fl) {
        Square fromSquare = new Square(currentlyPressed1), toSquare = new Square(currentlyPressed2);
        chessProcessor.makeMove(fromSquare, toSquare);
        updateBoardVisually(fromSquare, toSquare, fl);
        //if the move is castling (right side), make the required steps with the rook:
        if (fromSquare.getRow() == toSquare.getRow() &&
                toSquare.getCol() - fromSquare.getCol() == 2 &&
                board[toSquare.getRow()][toSquare.getCol()].charAt(1) == 'k') {
            int row = fromSquare.getRow();
            int col = toSquare.getCol() - 1; //of rook after castling
            Square originSquare = new Square(row, 7), targetSquare = new Square(row, col);
            chessProcessor.makeMove(originSquare, targetSquare);
            FrameLayout target_fl = (FrameLayout) chessBoard.getChildAt(row * 8 + col);
            updateBoardVisually(originSquare, targetSquare, target_fl);
        }
        //if the move is castling (left side), make the required steps with the rook:
        if (fromSquare.getRow() == toSquare.getRow() &&
                fromSquare.getCol() - toSquare.getCol() == 2 &&
                board[toSquare.getRow()][toSquare.getCol()].charAt(1) == 'k') {
            int row = fromSquare.getRow();
            int col = toSquare.getCol() + 1; //of rook after castling
            Square originSquare = new Square(row, 0), targetSquare = new Square(row, col);
            chessProcessor.makeMove(originSquare, targetSquare);
            FrameLayout target_fl = (FrameLayout) chessBoard.getChildAt(row * 8 + col);
            updateBoardVisually(originSquare, targetSquare, target_fl);
        }
    }

    private void updateBoardVisually(Square fromSquare, Square toSquare, FrameLayout to_fl) {
        //when rival is making his move, reset own previously chosen squares, and color rival's chosen squares:
        if (isBottomTurn == 0) {
            reSetPreviouslyChosenSquares(currentlyPressed1, currentlyPressed2, false);
            colorRivalChosenSquares();
        } else { //when user is making his move, reset rival's previously chosen squares:
            if (currentlyPressedRival1.getRow() != -1) reSetPreviouslyChosenSquares(currentlyPressedRival1, currentlyPressedRival2, true);
        }
        //the animation itself:
        FrameLayout parentView = findViewById(R.id.parent_view);
        int fromRow = fromSquare.getRow(), fromCol = fromSquare.getCol(), toRow = toSquare.getRow(), toCol = toSquare.getCol();
        char color = board[toRow][toCol].charAt(0);
        FrameLayout origin_fl = (FrameLayout) chessBoard.getChildAt(fromRow * 8 + fromCol);
        ImageView cpView = (ImageView) origin_fl.getChildAt(origin_fl.getChildCount() - 1);
        addViewToParentViewGroup(cpView, origin_fl);
        View targetView = chessBoard.getChildAt(toRow* 8 + toCol);
        int[] targetLocation = new int[2];
        targetView.getLocationOnScreen(targetLocation);
        ImageView newViewForBoard = getPieceFromString(board[toRow][toCol]);
        cpView.animate()
                .translationX(targetLocation[0])
                .translationY(targetLocation[1] - chessBoard.getHeight()/12)
                .setDuration(250) // Set the duration of the animation in milliseconds
                .withEndAction(() -> {
                    parentView.removeView(cpView);
                    if (to_fl.getChildCount() != 0) { //delete "eaten" piece:
                        if (to_fl.getChildCount() > 1) {
                            to_fl.removeViewAt(to_fl.getChildCount() - 1);
                        } else {
                            if (to_fl.getChildAt(0) instanceof ImageView) { //make sure the view we are about to delete is a chess piece and not an index
                                to_fl.removeViewAt(0);
                            }
                        }
                    }
                    //if the move is a pawn reaching the other (rival side) end of board, transform it into a queen:
                    if ((toRow == 0 || toRow == 7) && board[toRow][toCol].charAt(1) == 'p') {
                        ImageView queenIV = getPieceFromString(color + "q");
                        to_fl.addView(queenIV);
                        chessProcessor.transformIntoQueen(toSquare);
                    } else { //otherwise, restore the same piece into the gridlayout:
                        to_fl.addView(newViewForBoard);
                    }
                    //check if mate/draw/stale-mate:
                    char possibleLoserColor = board[toRow][toCol].charAt(0) == 'w'? 'b' : 'w';
                    if (chessProcessor.isMated(possibleLoserColor, board)) {
                        announceWinner(color);
                        return;
                    }
                    if (chessProcessor.isInStaleMate(possibleLoserColor, board) || chessProcessor.isDeadPosition()) {
                        announceDraw();
                        return;
                    }
                    // if not, change turns:
                    changeTurns();
                })
                .start();
    }

    private void addViewToParentViewGroup(View cpView, FrameLayout origin_fl) {
        FrameLayout parentView = findViewById(R.id.parent_view);
        int width = cpView.getWidth(), height = cpView.getHeight();
        int[] originalPosition = new int[2];
        cpView.getLocationOnScreen(originalPosition);
        int originalX = originalPosition[0];
        int originalY = originalPosition[1] - chessBoard.getHeight()/12;
        origin_fl.removeView(cpView);
        ViewGroup.LayoutParams layoutParams = cpView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        cpView.setLayoutParams(layoutParams);
        cpView.setX(originalX);
        cpView.setY(originalY);
        parentView.addView(cpView);
    }

    private void playRivalTurn() {
        isBottomTurn = 0;
        if (!(isUnlimited == 1)) resumeTimer(timeLeftUp, false);
        char rivalColor = isWhite == 1? 'b' : 'w';
        new Thread(() -> {
            Square fromSquare = new Square(-1, -1), toSquare = new Square(-1, -1);
            //get next move into fromSquare, toSquare:
            int depth = 3;
            chessProcessor.negaMax(rivalColor, board, fromSquare, toSquare, depth, depth);
            int fromRow = fromSquare.getRow(), fromCol = fromSquare.getCol(),
                    toRow = toSquare.getRow(), toCol = toSquare.getCol();
            chessProcessor.makeMove(fromSquare, toSquare);
            this.runOnUiThread(() -> {
                currentlyPressedRival1.setRow(fromRow);
                currentlyPressedRival1.setCol(fromCol);
                currentlyPressedRival2.setRow(toRow);
                currentlyPressedRival2.setCol(toCol);
                updateBoardVisually(fromSquare, toSquare, (FrameLayout) chessBoard.getChildAt(toRow * 8 + toCol));
            });
            //if the move is castling (right side), make the required steps with the rook:
            if (fromSquare.getRow() == toSquare.getRow() &&
                    toSquare.getCol() - fromSquare.getCol() == 2 &&
                    board[toSquare.getRow()][toSquare.getCol()].charAt(1) == 'k') {
                int row = fromSquare.getRow();
                int col = toSquare.getCol() - 1; //of rook after castling
                Square originSquare = new Square(row, 7), targetSquare = new Square(row, col);
                chessProcessor.makeMove(originSquare, targetSquare);
                FrameLayout target_fl = (FrameLayout) chessBoard.getChildAt(row * 8 + col);
                updateBoardVisually(originSquare, targetSquare, target_fl);
            }
            //if the move is castling (left side), make the required steps with the rook:
            if (fromSquare.getRow() == toSquare.getRow() &&
                    fromSquare.getCol() - toSquare.getCol() == 2 &&
                    board[toSquare.getRow()][toSquare.getCol()].charAt(1) == 'k') {
                int row = fromSquare.getRow();
                int col = toSquare.getCol() + 1; //of rook after castling
                Square originSquare = new Square(row, 0), targetSquare = new Square(row, col);
                chessProcessor.makeMove(originSquare, targetSquare);
                FrameLayout target_fl = (FrameLayout) chessBoard.getChildAt(row * 8 + col);
                updateBoardVisually(originSquare, targetSquare, target_fl);
            }
        }).start();
    }

    private void playOwnTurn() {
        isBottomTurn = 1;
        if (!(isUnlimited == 1)) resumeTimer(timeLeftBottom, true);

    }

    private void pauseTimer() {
        this.timer.cancel();
    }

    private void resumeTimer(long timeLeft, boolean isBottom) {
        startTimer(timeLeft, isBottom);
    }

    private void startTimer(long timeLeft, boolean isBottomTimer) {
        timer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                if (isBottomTimer) timeLeftBottom = millisUntilFinished;
                else timeLeftUp = millisUntilFinished;
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                if (isBottomTimer) {
                    bottomTimerTV.setText(f.format(min) + ":" + f.format(sec));
                }
                else {
                    upperTimerTV.setText(f.format(min) + ":" + f.format(sec));
                }

            }
            public void onFinish() {
                char winnerColor = 0;
                if (isBottomTimer) {
                    bottomTimerTV.setText("00:00");
                    if (isWhite == 1) winnerColor = 'b';
                    else winnerColor = 'w';
                } else {
                    upperTimerTV.setText("00:00");
                    if (isWhite == 1) winnerColor = 'w';
                    else winnerColor = 'b';
                }
                announceWinner(winnerColor);
            }
        }.start();
    }

    private void changeTurns() {
        if (isBottomTurn == 1) {
            if (!(isUnlimited == 1)) pauseTimer();
            playRivalTurn();
        } else {
            if (!(isUnlimited == 1)) pauseTimer();
            playOwnTurn();
        }
    }

    private void reSetPreviouslyChosenSquares(Square square1, Square square2, boolean isOfRival) {
        int row1 = square1.getRow(), col1 = square1.getCol(),
                row2 = square2.getRow(), col2 = square2.getCol();
        FrameLayout fl1 = (FrameLayout) chessBoard.getChildAt(row1 * 8 + col1),
                fl2 = (FrameLayout) chessBoard.getChildAt(row2 * 8 + col2);
        if (fl1 != null) {
            setOriginalBackgroundByIndex(fl1, row1, col1);
        }
        if (fl2 != null ) {
            if (!isOfRival || !(currentlyPressed2.equals(square2))) {
                setOriginalBackgroundByIndex(fl2, row2, col2);
            }
        }
        square1.setRow(-1);
        square1.setCol(-1);
        square2.setRow(-1);
        square2.setCol(-1);
    }

    private void colorRivalChosenSquares() {
        int row1 = currentlyPressedRival1.getRow(), col1 = currentlyPressedRival1.getCol(),
                row2 = currentlyPressedRival2.getRow(), col2 = currentlyPressedRival2.getCol();
        FrameLayout fl1 = (FrameLayout) chessBoard.getChildAt(row1 * 8 + col1),
                fl2 = (FrameLayout) chessBoard.getChildAt(row2 * 8 + col2);
        fl1.setBackground(pressedSquareBg);
        fl2.setBackground(pressedSquareBg);
    }

    private void announceWinner(char color) {
        //stoptimers:
        this.timer.cancel();
        unhighlightSquares();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_box);
        isBottomTurn = 0; //make sure board isn't available for user to press after game ended
        //set the views:
        String winnerColor = null;
        if (color == 'w') {
            winnerColor = "White";
        } else {
            winnerColor = "Black";
        }
        setViewsForDialog(winnerColor + " wins!", dialog);
        //create and start the dialog:
        displayDialog(dialog);
    }
    private void announceDraw() {
        //stoptimers:
        this.timer.cancel();
        unhighlightSquares();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_box);
        isBottomTurn = 0; //make sure board isn't available for user to press after game ended
        //set the views:
        setViewsForDialog("Draw!", dialog);
        //create and start the dialog:
        displayDialog(dialog);
    }

    private void setViewsForDialog(String announcementString, Dialog dialog) {
        TextView announcementTV = dialog.findViewById(R.id.announcementText);
        announcementTV.setText(announcementString);
        Typeface mainFont = Typeface.createFromAsset(getAssets(), "font/Farmhouse.otf");
        announcementTV.setTypeface(mainFont);
        setRematchButton(mainFont, dialog);
        TextView cancelButtonTV = dialog.findViewById(R.id.cancelButton);
        setCancelTV(cancelButtonTV, mainFont, dialog);
    }

    private void setRematchButton(Typeface font, Dialog dialog) {
        Button rematchButton = dialog.findViewById(R.id.rematchButton);
        rematchButton.setTypeface(font);
        rematchButton.setOnClickListener(view -> {
            Intent intent = getIntent();
            finish();
            dialog.dismiss();
            startActivity(intent);
        });
    }

    private void setCancelTV(TextView tv, Typeface font, Dialog dialog) {
        tv.setTypeface(font);
        tv.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }


    private void displayDialog(Dialog dialog) {
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.show();
    }

    //TODO add sounds

}