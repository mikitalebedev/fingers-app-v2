package com.example.sign_lang_ml.data;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FireBaseLoad {

    Context context;

    user_info user = new user_info();


    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser userDB= mAuth.getCurrentUser();




    FirebaseFirestore db = FirebaseFirestore.getInstance();
    profile_info profileInfo = new profile_info();

      public FireBaseLoad(Context context){
          this.context=context;
      }





    public user_info getUserInfo(String UserToken) {
        user_info userInfo = new user_info();

        DocumentReference docRef = db.collection("users").document(UserToken);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        String email = notNullStrForUsers(document, "email");
                        String name = notNullStrForUsers(document, "name");
                        String id = notNullStrForUsers(document, "id");
                        String password = notNullStrForUsers(document, "password");
                        String photo = notNullStrForUsers(document, "photo");
                        String yes = notNullStrForUsers(document, "yesno");
                        String search = notNullStrForUsers(document, "search");
                        String tisting = notNullStrForUsers(document, "testing");
                        String learning = notNullStrForUsers(document, "learning");

                        userInfo.setEmail(email);
                        userInfo.setName(name);
                        userInfo.setLearnig(learning);
                        userInfo.setSearch(search);
                        userInfo.setTesting(tisting);
                        userInfo.setYes(yes);
                        userInfo.setPhoto(photo);
                        userInfo.setId(document.getId());

                        profileInfo.getInstance().setMy_user_info(userInfo);
                        Log.d("map", "setMy_user_info");

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });
        return userInfo;

    }







    public List<String> stringToArrList(String s){
        s = s.replaceAll("[\\[\\]]", "");
        List<String> arr = new ArrayList<String>(Arrays.asList(s.split(",")));
          return arr;
    }


    public String notNullStr(QueryDocumentSnapshot document, String tokenName) {
        String dd = "";
        try {
            dd = document.getData().get(tokenName).toString();
        } catch (Exception e) {

        }
        return dd;
    }


    public String notNullStrForUsers(DocumentSnapshot document, String tokenName) {
        String dd = "";
        try {
            dd = document.getData().get(tokenName).toString();
        } catch (Exception e) {

        }
        return dd;
    }

    public void removeVoice (String s) {

        db.collection("voice").document(s).delete();
    }


}
