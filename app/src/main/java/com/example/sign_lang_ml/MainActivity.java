package com.example.sign_lang_ml;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sign_lang_ml.Utils.MaskTransformation;
import com.example.sign_lang_ml.data.profile_info;
import com.example.sign_lang_ml.data.user_info;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MainActivity extends AppCompatActivity {

    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 12;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 13;
    Button btn;
    RelativeLayout btnR;
    user_info userInfo;
    ImageView photo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        setContentView(R.layout.activity_main);
        TextView name = findViewById(R.id.name);
        photo = findViewById(R.id.photoMain);

        userInfo = profile_info.getInstance().getMy_user_info();
        name.setText(userInfo.getName());
        String image = userInfo.getPhoto();
        if (!image.equals("")){
            final Transformation transformation = new MaskTransformation(MainActivity.this, R.drawable.rounded_convers_transformation);
            Picasso.get()
                    .load(image)
                    .resize(200, 200) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .transform(transformation)
                    .into(photo);
        }

        // Recognition Button
        btn = findViewById(R.id.recognition);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecognitionActivity.class);
                startActivity(intent);
            }
        });


        // Learning Button

        btnR = findViewById(R.id.learn);
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignLanguageActivity.class);
                startActivity(intent);
            }
        });

        btn = findViewById(R.id.training);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
                startActivity(intent);
            }
        });

        btnR = findViewById(R.id.trueOrFalse);
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GamePlayActivity.class);
                startActivity(intent);
            }
        });

        btnR = findViewById(R.id.chooseGeasture);
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameChooseSign.class);
                startActivity(intent);
            }
        });
        btnR = findViewById(R.id.chooseLetter);
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameChooseLetter.class);
                startActivity(intent);
            }
        });
        btnR = findViewById(R.id.teoryaLiner);
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TheoryActivity.class);
                startActivity(intent);
            }
        });


        btnR = findViewById(R.id.learnWords);
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TeoryWords.class);
                startActivity(intent);
            }
        });

        btn = findViewById(R.id.profile);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });



    }


}
