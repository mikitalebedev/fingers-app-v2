package com.example.sign_lang_ml;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sign_lang_ml.R;
import com.example.sign_lang_ml.SignLanguageAdapter;
import com.example.sign_lang_ml.SignLanguageCard;

import java.util.ArrayList;
import java.util.List;

public class SignLanguageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SignLanguageAdapter signLanguageAdapter;
    private List<SignLanguageCard> signLanguageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_language);
        ImageView back = findViewById(R.id.glasses);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignLanguageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        signLanguageList = new ArrayList<>();
        // Populate the sign language list with letters and image resources

        signLanguageList.add(new SignLanguageCard("А", R.drawable.gesture_a));
        signLanguageList.add(new SignLanguageCard("Б", R.drawable.gesture_b));
        signLanguageList.add(new SignLanguageCard("В", R.drawable.gesture_v));
        signLanguageList.add(new SignLanguageCard("Г", R.drawable.gesture_g));
        signLanguageList.add(new SignLanguageCard("Д", R.drawable.gesture_d));
        signLanguageList.add(new SignLanguageCard("Е", R.drawable.gesture_e));
        signLanguageList.add(new SignLanguageCard("Ж", R.drawable.gesture_zh));
        signLanguageList.add(new SignLanguageCard("З", R.drawable.gesture_z));
        signLanguageList.add(new SignLanguageCard("И", R.drawable.gesture_i));
        signLanguageList.add(new SignLanguageCard("К", R.drawable.gesture_k));
        signLanguageList.add(new SignLanguageCard("Л", R.drawable.gesture_l));
        signLanguageList.add(new SignLanguageCard("М", R.drawable.gesture_m));
        signLanguageList.add(new SignLanguageCard("Н", R.drawable.gesture_n));
        signLanguageList.add(new SignLanguageCard("О", R.drawable.gesture_o));
        signLanguageList.add(new SignLanguageCard("П", R.drawable.gesture_p));
        signLanguageList.add(new SignLanguageCard("Р", R.drawable.gesture_r));
        signLanguageList.add(new SignLanguageCard("С", R.drawable.gesture_s));
        signLanguageList.add(new SignLanguageCard("Т", R.drawable.gesture_t));
        signLanguageList.add(new SignLanguageCard("У", R.drawable.gesture_u));
        signLanguageList.add(new SignLanguageCard("Ф", R.drawable.gesture_f));
        signLanguageList.add(new SignLanguageCard("Х", R.drawable.gesture_h));
        signLanguageList.add(new SignLanguageCard("Ц", R.drawable.gesture_c));
        signLanguageList.add(new SignLanguageCard("Ч", R.drawable.gesture_c));
        signLanguageList.add(new SignLanguageCard("Ш", R.drawable.gesture_sh));
        signLanguageList.add(new SignLanguageCard("Щ", R.drawable.gesture_sha));
        signLanguageList.add(new SignLanguageCard("Ь", R.drawable.gesture_soft_sign));
        signLanguageList.add(new SignLanguageCard("Ы", R.drawable.gesture_y));
        signLanguageList.add(new SignLanguageCard("Э", R.drawable.gesture_ie));
        signLanguageList.add(new SignLanguageCard("Ю", R.drawable.gesture_yu));
        signLanguageList.add(new SignLanguageCard("Я", R.drawable.gesture_ya));

        signLanguageAdapter = new SignLanguageAdapter(this, signLanguageList, new SignLanguageAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(View view, SignLanguageCard signLanguageCard) {
                handleCardClick(view, signLanguageCard);
            }
        });
        recyclerView.setAdapter(signLanguageAdapter);
    }

    private void handleCardClick(View view, SignLanguageCard signLanguageCard) {
        flipCard(view, signLanguageCard);
    }

    private void flipCard(View view, SignLanguageCard signLanguageCard) {
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);

        TransitionManager.beginDelayedTransition((ViewGroup) view);
        if (imageView.getVisibility() == View.VISIBLE) {
            imageView.setVisibility(View.GONE);
            textView.setText(signLanguageCard.getLetter());
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
