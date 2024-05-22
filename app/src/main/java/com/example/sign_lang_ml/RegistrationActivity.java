package com.example.sign_lang_ml;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    TextView btnRegistration ;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    EditText editName,editEmail, editPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        editName = findViewById(R.id.name);
        editEmail = findViewById(R.id.email);
        btnRegistration = findViewById(R.id.btnRegistration);
        editPassword = findViewById(R.id.password);




        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();







        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                String name = editName.getText().toString();
                String password = editPassword.getText().toString();


                if(isValidEmail(email)){

                    if (!email.equals("") && !name.equals("") && !password.equals(""))
                    {
                        createAccount(email , password ,name);

                    }
                }
            }
        });


    }


    private void createAccount(String email, String password, String name) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            Log.d("UID", uid);
                            try {
                                setUserInfo( uid , email , name);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }


                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Error"+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
        // [END create_user_with_email]
    }



    public  void setUserInfo (String uid , String email ,String name) throws Exception {
        CollectionReference cities = db.collection("users");
        String yes = "0";
        String search = "0";
        String testing = "0";
        String learning = "0";

        Map<String, Object> data1 = new HashMap<>();

        System.out.println(name+ email);
        Log.d("USERS", name+email);

        data1.put("email", email);
        data1.put("photo", "");
        data1.put("name",  name);
        data1.put("yesno",  yes);
        data1.put("search",  search);
        data1.put("testing",  testing);
        data1.put("learning",  learning);
        data1.put("id",  uid);

        ;


        cities.document(uid).set(data1);

        Intent intent = new Intent(RegistrationActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();


    }

    public String createTransactionID() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }



    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }






}