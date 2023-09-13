package com.example.chessgame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetClockActivity extends AppCompatActivity {
    TextView bulletSymbol;
    TextView bulletTime;
    TextView lightningSymbol;
    TextView blitzTime;
    TextView clockSymbol;
    TextView rapidTime;
    TextView infinitySymbol;
    TextView unlimitedTime;
    LinearLayout bulletCell;
    LinearLayout blitzCell;
    LinearLayout rapidCell;
    LinearLayout unlimitedCell;
    Button saveButton;
    int currChoice; //1=bullet 2=blitz 3=rapid 4=unlimited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_clock);
        bulletSymbol = findViewById(R.id.bullet);
        bulletTime = findViewById(R.id.bullet_time);
        lightningSymbol = findViewById(R.id.lightning);
        blitzTime = findViewById(R.id.blitz_time);
        clockSymbol = findViewById(R.id.clock);
        rapidTime = findViewById(R.id.rapid_time);
        infinitySymbol = findViewById(R.id.infinity);
        unlimitedTime = findViewById(R.id.unlimited_time);
        saveButton = findViewById(R.id.save_button);
        setFonts();
        bulletCell = findViewById(R.id.bullet_cell);
        blitzCell = findViewById(R.id.blitz_cell);
        rapidCell = findViewById(R.id.rapid_cell);
        unlimitedCell = findViewById(R.id.unlimited_cell);
        //set background color for pre-chosen (or default if no change previously) clockTime cell:
        setPreChosenCellColor();
        //set onClickListeners for the 4 options displayed on screen:
        setOnClickListeners();
    }

    private void setFonts() {
        Typeface mainFont = Typeface.createFromAsset(getAssets(), "font/Farmhouse.otf");
        Typeface bulletFont = Typeface.createFromAsset(getAssets(), "font/Special Forces.ttf");
        Typeface lightning_clock_font = Typeface.createFromAsset(getAssets(), "font/Icons South St.ttf");
        bulletTime.setTypeface(mainFont);
        blitzTime.setTypeface(mainFont);
        rapidTime.setTypeface(mainFont);
        unlimitedTime.setTypeface(mainFont);
        bulletSymbol.setTypeface(bulletFont);
        lightningSymbol.setTypeface(lightning_clock_font);
        clockSymbol.setTypeface(lightning_clock_font);
        saveButton.setTypeface(mainFont);
    }

    private void setPreChosenCellColor() {
        LinearLayout chosenCell = null;
        Intent intent = getIntent();
        currChoice = intent.getIntExtra("currChoice", 2);
        switch (currChoice) {
            case 1:
                chosenCell = bulletCell;
                break;
            case 2:
                chosenCell = blitzCell;
                break;
            case 3:
                chosenCell = rapidCell;
                break;
            case 4:
                chosenCell = unlimitedCell;
                break;
        }
        chosenCell.setBackgroundColor(getColor(R.color.light_gray));
    }

    private void setOnClickListeners() {
        bulletCell.setOnClickListener(view -> {
            changeChoice(1);
        });
        blitzCell.setOnClickListener(view -> {
            changeChoice(2);
        });
        rapidCell.setOnClickListener(view -> {
            changeChoice(3);
        });
        unlimitedCell.setOnClickListener(view -> {
            changeChoice(4);
        });
        saveButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("clockTimeChoice", currChoice);
            setResult(2, intent);
            finish();
        });
    }

    private void changeChoice(int newChoice) {
        if (currChoice == newChoice) return;
        LinearLayout toWhiteCell = null;
        LinearLayout toGrayCell = null;
        switch (currChoice) {
            case 1:
                toWhiteCell = bulletCell;
                break;
            case 2:
                toWhiteCell = blitzCell;
                break;
            case 3:
                toWhiteCell = rapidCell;
                break;
            case 4:
                toWhiteCell = unlimitedCell;
                break;
        }

        switch (newChoice) {
            case 1:
                toGrayCell = bulletCell;
                break;
            case 2:
                toGrayCell = blitzCell;
                break;
            case 3:
                toGrayCell = rapidCell;
                break;
            case 4:
                toGrayCell = unlimitedCell;
                break;
        }

        changeToWhite(toWhiteCell);
        changeToGray(toGrayCell);
        currChoice = newChoice;
    }

    private void changeToWhite(LinearLayout toWhiteCell) {
        int colorFrom = getColor(R.color.light_gray);
        int colorTo = getColor(R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(100); // milliseconds
        colorAnimation.addUpdateListener(animator -> toWhiteCell.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    private void changeToGray(LinearLayout toGrayCell) {
        int colorFrom = getColor(R.color.white);
        int colorTo = getColor(R.color.light_gray);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(150); // milliseconds
        colorAnimation.addUpdateListener(animator -> toGrayCell.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}