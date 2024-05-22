package com.example.sign_lang_ml;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameResultActivity extends AppCompatActivity {

    private TextView gameResult;
    private Button buttonMainMenu;
    private Button buttonPlayAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        gameResult = findViewById(R.id.game_result);
        buttonMainMenu = findViewById(R.id.button_main_menu);
        buttonPlayAgain = findViewById(R.id.button_play_again);

        int score = getIntent().getIntExtra("score", 0);
        gameResult.setText("Ваши очки: " + score);

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameResultActivity.this, GamePlayActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
