package com.example.sign_lang_ml;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameChooseSign extends AppCompatActivity {

    private TextView tvPoints, tvTime, tvRound, tvLetter;
    private Button[] gestureButtons = new Button[6];
    Button btn;
    private int points = 0;
    private int round = 1;
    private Handler timerHandler = new Handler();
    private int secondsElapsed = 0;
    private String[] letters = {"А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У",
            "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ь", "Э", "Ю", "Я"}; // Add more letters as needed
    private int[] correctGestures = {R.drawable.gesture_a, R.drawable.gesture_b, R.drawable.gesture_c, R.drawable.gesture_d, R.drawable.gesture_e,
            R.drawable.gesture_zh, R.drawable.gesture_z, R.drawable.gesture_i, R.drawable.gesture_k, R.drawable.gesture_l,
            R.drawable.gesture_m, R.drawable.gesture_n, R.drawable.gesture_o, R.drawable.gesture_p, R.drawable.gesture_r,
            R.drawable.gesture_s, R.drawable.gesture_t, R.drawable.gesture_y, R.drawable.gesture_f, R.drawable.gesture_h,
            R.drawable.gesture_c, R.drawable.gesture_c, R.drawable.gesture_c, R.drawable.gesture_s, R.drawable.gesture_sha,
            R.drawable.gesture_soft_sign, R.drawable.gesture_ie, R.drawable.gesture_yu, R.drawable.gesture_ya}; // replace with actual drawable resources
    private Map<String, Integer> letterToGestureMap = new HashMap<>();
    private int currentCorrectGesture;
    private List<String> remainingLetters = new ArrayList<>();
    private Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sign);

        tvPoints = findViewById(R.id.tvPoints);
        tvTime = findViewById(R.id.tvTime);
        tvRound = findViewById(R.id.tvRound);
        tvLetter = findViewById(R.id.tvLetter);


        for (int i = 0; i < gestureButtons.length; i++) {
            int buttonId = getResources().getIdentifier("btnGesture" + (i + 1), "id", getPackageName());
            gestureButtons[i] = findViewById(buttonId);
            gestureButtons[i].setOnClickListener(this::onGestureButtonClick);
        }

        initializeLetterToGestureMap();
        initializeRemainingLetters();
        startTimer();
        updateGame();


        btn = findViewById(R.id.back_icon);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeLetterToGestureMap() {
        letterToGestureMap.put("А", R.drawable.gesture_a);
        letterToGestureMap.put("Б", R.drawable.gesture_b);
        letterToGestureMap.put("В", R.drawable.gesture_c);
        letterToGestureMap.put("Г", R.drawable.gesture_d);
        letterToGestureMap.put("Д", R.drawable.gesture_e);
        letterToGestureMap.put("Е", R.drawable.gesture_e);
        letterToGestureMap.put("Ж", R.drawable.gesture_zh);
        letterToGestureMap.put("З", R.drawable.gesture_z);
        letterToGestureMap.put("И", R.drawable.gesture_i);
        letterToGestureMap.put("К", R.drawable.gesture_k);
        letterToGestureMap.put("Л", R.drawable.gesture_l);
        letterToGestureMap.put("М", R.drawable.gesture_m);
        letterToGestureMap.put("Н", R.drawable.gesture_n);
        letterToGestureMap.put("О", R.drawable.gesture_o);
        letterToGestureMap.put("П", R.drawable.gesture_p);
        letterToGestureMap.put("Р", R.drawable.gesture_r);
        letterToGestureMap.put("С", R.drawable.gesture_s);
        letterToGestureMap.put("Т", R.drawable.gesture_t);
        letterToGestureMap.put("У", R.drawable.gesture_y);
        letterToGestureMap.put("Ф", R.drawable.gesture_f);
        letterToGestureMap.put("Х", R.drawable.gesture_h);
        letterToGestureMap.put("Ц", R.drawable.gesture_c);
        letterToGestureMap.put("Ч", R.drawable.gesture_c);
        letterToGestureMap.put("Ш", R.drawable.gesture_sha);
        letterToGestureMap.put("Щ", R.drawable.gesture_c);
        letterToGestureMap.put("Ь", R.drawable.gesture_soft_sign);
        letterToGestureMap.put("Э", R.drawable.gesture_ie);
        letterToGestureMap.put("Ю", R.drawable.gesture_yu);
        letterToGestureMap.put("Я", R.drawable.gesture_ya);
    }

    private void initializeRemainingLetters() {
        Collections.addAll(remainingLetters, letters);
    }

    private void startTimer() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                secondsElapsed++;
                tvTime.setText("Время: " + secondsElapsed);
                timerHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updateGame() {
        if (round > 10) {
            endGame();
            return;
        }

        tvRound.setText("Раунд: " + round);

        if (remainingLetters.isEmpty()) {
            endGame();
            return;
        }

        // Select a random letter from the remaining letters
        String currentLetter = remainingLetters.remove(random.nextInt(remainingLetters.size()));
        tvLetter.setText(currentLetter);

        // Ensure the correct gesture for the current letter is included
        currentCorrectGesture = letterToGestureMap.get(currentLetter);

        // Create a list of gestures including the correct one
        List<Integer> gestures = new ArrayList<>();
        gestures.add(currentCorrectGesture);
        while (gestures.size() < gestureButtons.length) {
            int randomGesture = correctGestures[random.nextInt(correctGestures.length)];
            if (!gestures.contains(randomGesture)) {
                gestures.add(randomGesture);
            }
        }

        // Shuffle the gestures to randomize their positions
        Collections.shuffle(gestures);

        // Assign gestures to buttons
        for (int i = 0; i < gestureButtons.length; i++) {
            gestureButtons[i].setBackgroundResource(gestures.get(i));
        }
    }

    private void onGestureButtonClick(View view) {
        int clickedGesture = ((Button) view).getBackground().getConstantState().hashCode();
        int correctGestureHash = getResources().getDrawable(currentCorrectGesture).getConstantState().hashCode();

        if (clickedGesture == correctGestureHash) {
            points++;
            updatePointsDisplay();
        }

        round++;
        updateGame();
    }

    private void updatePointsDisplay() {
        tvPoints.setText("Очки: " + points);
    }

    private void endGame() {
        Intent intent = new Intent(GameChooseSign.this, GameResultActivity.class);
        intent.putExtra("score", points);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
