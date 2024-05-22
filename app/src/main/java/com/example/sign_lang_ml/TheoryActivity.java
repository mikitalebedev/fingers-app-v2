package com.example.sign_lang_ml;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TheoryActivity extends AppCompatActivity {

    private int currentIndex = 0;
    Button btn;
    private ProgressBar progressBar;
    private TextView tvSignLetter;
    private ImageView ivSignImage;
    private Button btnContinue;

    private int[] gestureImages = {
            R.drawable.gesture_a, R.drawable.gesture_b, R.drawable.gesture_c, R.drawable.gesture_d, R.drawable.gesture_e,
            R.drawable.gesture_zh, R.drawable.gesture_z, R.drawable.gesture_i, R.drawable.gesture_k, R.drawable.gesture_l,
            R.drawable.gesture_m, R.drawable.gesture_n, R.drawable.gesture_o, R.drawable.gesture_p, R.drawable.gesture_r,
            R.drawable.gesture_s, R.drawable.gesture_t, R.drawable.gesture_y, R.drawable.gesture_f, R.drawable.gesture_h,
            R.drawable.gesture_c, R.drawable.gesture_c, R.drawable.gesture_c, R.drawable.gesture_s, R.drawable.gesture_sha,
            R.drawable.gesture_soft_sign, R.drawable.gesture_ie, R.drawable.gesture_yu, R.drawable.gesture_ya
    };

    private String[] letters = {"А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ь", "Э", "Ю", "Я"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory);

        progressBar = findViewById(R.id.progressBar);
        tvSignLetter = findViewById(R.id.tvSignLetter);
        ivSignImage = findViewById(R.id.ivSignImage);
        btnContinue = findViewById(R.id.btnContinue);

        updateUI();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex++;
                if (currentIndex < letters.length) {
                    updateUI();
                } else {
                    Intent intent = new Intent(TheoryActivity.this, EndActivity.class);
                    startActivity(intent);
                }
            }
        });

        btn = findViewById(R.id.back_icon);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI() {
        tvSignLetter.setText(letters[currentIndex]);
        ivSignImage.setImageResource(gestureImages[currentIndex]);
        progressBar.setProgress(currentIndex + 1);
    }
}
