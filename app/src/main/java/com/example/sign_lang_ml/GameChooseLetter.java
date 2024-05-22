package com.example.sign_lang_ml;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameChooseLetter extends AppCompatActivity {

    private TextView tvPoints, tvTime, tvRound;
    private ImageView ivGesture;
    Button btn;
    private Button[] letterButtons = new Button[6];
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
    private Map<Integer, String> gestureToLetterMap = new HashMap<>();
    private int currentCorrectGesture;
    private List<Integer> remainingGestures = new ArrayList<>();
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_letter);

        tvPoints = findViewById(R.id.tvPoints);
        tvTime = findViewById(R.id.tvTime);
        tvRound = findViewById(R.id.tvRound);
        ivGesture = findViewById(R.id.ivGesture);

        for (int i = 0; i < letterButtons.length; i++) {
            int buttonId = getResources().getIdentifier("btnLetter" + (i + 1), "id", getPackageName());
            letterButtons[i] = findViewById(buttonId);
            letterButtons[i].setOnClickListener(this::onLetterButtonClick);
        }

        initializeGestureToLetterMap();
        initializeRemainingGestures();
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

    private void initializeGestureToLetterMap() {
        gestureToLetterMap.put(R.drawable.gesture_a, "А");
        gestureToLetterMap.put(R.drawable.gesture_b, "Б");
        gestureToLetterMap.put(R.drawable.gesture_c, "В");
        gestureToLetterMap.put(R.drawable.gesture_d, "Г");
        gestureToLetterMap.put(R.drawable.gesture_e, "Д");
        gestureToLetterMap.put(R.drawable.gesture_zh, "Ж");
        gestureToLetterMap.put(R.drawable.gesture_z, "З");
        gestureToLetterMap.put(R.drawable.gesture_i, "И");
        gestureToLetterMap.put(R.drawable.gesture_k, "К");
        gestureToLetterMap.put(R.drawable.gesture_l, "Л");
        gestureToLetterMap.put(R.drawable.gesture_m, "М");
        gestureToLetterMap.put(R.drawable.gesture_n, "Н");
        gestureToLetterMap.put(R.drawable.gesture_o, "О");
        gestureToLetterMap.put(R.drawable.gesture_p, "П");
        gestureToLetterMap.put(R.drawable.gesture_r, "Р");
        gestureToLetterMap.put(R.drawable.gesture_s, "С");
        gestureToLetterMap.put(R.drawable.gesture_t, "Т");
        gestureToLetterMap.put(R.drawable.gesture_y, "У");
        gestureToLetterMap.put(R.drawable.gesture_f, "Ф");
        gestureToLetterMap.put(R.drawable.gesture_h, "Х");
        gestureToLetterMap.put(R.drawable.gesture_c, "Ц");
        gestureToLetterMap.put(R.drawable.gesture_sha, "Ш");
        gestureToLetterMap.put(R.drawable.gesture_soft_sign, "Ь");
        gestureToLetterMap.put(R.drawable.gesture_ie, "Э");
        gestureToLetterMap.put(R.drawable.gesture_yu, "Ю");
        gestureToLetterMap.put(R.drawable.gesture_ya, "Я");
    }

    private void initializeRemainingGestures() {
        for (int gesture : correctGestures) {
            remainingGestures.add(gesture);
        }
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

        if (remainingGestures.isEmpty()) {
            endGame();
            return;
        }

        // Select a random gesture from the remaining gestures
        currentCorrectGesture = remainingGestures.remove(random.nextInt(remainingGestures.size()));
        ivGesture.setImageResource(currentCorrectGesture);

        // Ensure the correct letter for the current gesture is included
        String currentLetter = gestureToLetterMap.get(currentCorrectGesture);

        // Create a list of letters including the correct one
        List<String> letters = new ArrayList<>();
        letters.add(currentLetter);
        while (letters.size() < letterButtons.length) {
            String randomLetter = this.letters[random.nextInt(this.letters.length)];
            if (!letters.contains(randomLetter)) {
                letters.add(randomLetter);
            }
        }

        // Shuffle the letters to randomize their positions
        Collections.shuffle(letters);

        // Assign letters to buttons
        for (int i = 0; i < letterButtons.length; i++) {
            letterButtons[i].setText(letters.get(i));
        }
    }

    private void onLetterButtonClick(View view) {
        String clickedLetter = ((Button) view).getText().toString();
        String correctLetter = gestureToLetterMap.get(currentCorrectGesture);

        if (clickedLetter.equals(correctLetter)) {
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
        Intent intent = new Intent(GameChooseLetter.this, GameResultActivity.class);
        intent.putExtra("score", points);
        startActivity(intent);
        finish();
    }
}
