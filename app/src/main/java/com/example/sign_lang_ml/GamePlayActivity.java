package com.example.sign_lang_ml;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GamePlayActivity extends AppCompatActivity {

    private ImageView gestureImage, back;
    private TextView letterText, scoreLabel, feedbackText, timerText, roundLabel;
    private Button buttonYes, buttonNo;
    private int score = 0;
    private int round = 1;
    private static final int TOTAL_ROUNDS = 11;
    private static final int ROUND_TIME = 10000; // 5 seconds in milliseconds
    private CountDownTimer timer;

    private int[] gestureImages = {
            R.drawable.gesture_a, R.drawable.gesture_b, R.drawable.gesture_c, R.drawable.gesture_d, R.drawable.gesture_e,
            R.drawable.gesture_zh,R.drawable.gesture_z,R.drawable.gesture_i, R.drawable.gesture_k,R.drawable.gesture_l,
            R.drawable.gesture_m,R.drawable.gesture_n,R.drawable.gesture_o,R.drawable.gesture_p,R.drawable.gesture_r,
            R.drawable.gesture_s,R.drawable.gesture_t,R.drawable.gesture_y,R.drawable.gesture_f,R.drawable.gesture_h,
            R.drawable.gesture_c,R.drawable.gesture_c,R.drawable.gesture_c,R.drawable.gesture_s,R.drawable.gesture_sha,
            R.drawable.gesture_soft_sign, R.drawable.gesture_ie,R.drawable.gesture_yu,R.drawable.gesture_ya // replace with actual drawable resources
    };
    private String[] letters = {"А", "Б", "В", "Г", "Д", "Е","Ж", "З", "И", "К", "Л", "М","Н", "О", "П", "Р", "С", "Т","У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ь", "Э", "Ю","Я"};

    private Random random = new Random();
    private int currentGestureIndex = 0;
    private int currentLetterIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        gestureImage = findViewById(R.id.gesture_image);
        letterText = findViewById(R.id.letter_text);
        scoreLabel = findViewById(R.id.score_label);
        feedbackText = findViewById(R.id.feedback_text);
        timerText = findViewById(R.id.timer_text);
        buttonYes = findViewById(R.id.trueOrFalse);
        buttonNo = findViewById(R.id.button_no);
        back = findViewById(R.id.glasses);
        roundLabel = findViewById(R.id.raund_label);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamePlayActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        loadNewRound();
    }

    private void checkAnswer(boolean userAnswer) {
        timer.cancel();
        boolean correctAnswer = (currentGestureIndex == currentLetterIndex);
        if (userAnswer == correctAnswer) {
            score++;
        } else {
        }
        scoreLabel.setText("Очки: " + score);
        round++;
        if (round < TOTAL_ROUNDS) {
            loadNewRound();
        } else {
            showResult();
        }
    }

    private void loadNewRound() {
        currentGestureIndex = random.nextInt(gestureImages.length);
        currentLetterIndex = random.nextInt(letters.length);
        gestureImage.setImageResource(gestureImages[currentGestureIndex]);
        letterText.setText(letters[currentLetterIndex]);

        timerText.setText("Время: 5");
        roundLabel.setText("Раунд: " + round);
        feedbackText.setText("");

        timer = new CountDownTimer(ROUND_TIME, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Время: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                feedbackText.setText("Время вышло!");
                round++;
                if (round < TOTAL_ROUNDS) {
                    loadNewRound();
                } else {
                    showResult();
                }
            }
        }.start();
    }

    private void showResult() {
        Intent intent = new Intent(GamePlayActivity.this, GameResultActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }
}
