package com.example.sign_lang_ml;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sign_lang_ml.data.FireBaseLoad;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity {

    private FirebaseUser aUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d("activity", "splash");


        aUser = FirebaseAuth.getInstance().getCurrentUser();


        if (aUser != null) {
            // User is signed in
            String uid = aUser.getUid();
            FireBaseLoad data = new FireBaseLoad(this);
            Log.d("USERS",uid);
            data.getUserInfo(uid);





            if (isNetworkAvailable(this)) {

                new Handler().postDelayed(new Runnable() {

                    @Override

                    public void run() {




                       // user_info userInfo = profile_info.getInstance().getMy_user_info();
                        // Log.d("USERS", userInfo.getName());
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();


                    }




                }, 4000);




                //  Log.d("map", String.valueOf(data.getMarkersInfo()));




            }



        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

    }



    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


}


