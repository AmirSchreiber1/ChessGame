package com.example.chessgame;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    Button playButton;
    Button clockBtn;
    TextView welcomeMsg;
    TextView logoTxt;
    TextView clockTimeTV;
    int currChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = findViewById(R.id.playBtn);
        clockBtn = findViewById(R.id.clockBtn);
        welcomeMsg = findViewById(R.id.welcomeMsg);
        logoTxt = findViewById(R.id.logoTxt);
        clockTimeTV = findViewById(R.id.clockTime);
        setFonts();
        currChoice = 3;
        clockBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, SetClockActivity.class);
            intent.putExtra("currChoice", currChoice);
            startActivityForResult(intent, 2);
        });
        playButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("clockTimeChoice", currChoice);
            startActivity(intent);
        });
    }

    private void setFonts(){
        Typeface mainFont = Typeface.createFromAsset(getAssets(), "font/Farmhouse.otf");
        Typeface welcomeFont = Typeface.createFromAsset(getAssets(), "font/Writer.otf");
        playButton.setTypeface(mainFont);
        clockBtn.setTypeface(mainFont);
        welcomeMsg.setTypeface(welcomeFont);
        logoTxt.setTypeface(mainFont);
        clockTimeTV.setTypeface(mainFont);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            if (data == null) return;
            currChoice=data.getIntExtra("clockTimeChoice", 3);
            String clockTimeStr = null;
            switch(currChoice) {
                case 1:
                    clockTimeStr = "Clock Time: 02:00";
                    break;
                case 2:
                    clockTimeStr = "Clock Time: 05:00";
                    break;
                case 3:
                    clockTimeStr = "Clock Time: 10:00";
                    break;
                case 4:
                    clockTimeStr = "Clock Time: unlimited";
                    break;
            }
            clockTimeTV.setText(clockTimeStr);
        }
    }
}