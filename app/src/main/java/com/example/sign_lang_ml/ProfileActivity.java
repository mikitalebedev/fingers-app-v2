package com.example.sign_lang_ml;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sign_lang_ml.Utils.MaskTransformation;
import com.example.sign_lang_ml.data.FireBaseLoad;
import com.example.sign_lang_ml.data.profile_info;
import com.example.sign_lang_ml.data.user_info;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser aUser;

    private FirebaseAuth mAuth;
    private  FirebaseFirestore db;
    FirebaseUser user;
    private String uid;
    private String uploadedImageUrl;
    StorageReference storageReference;
    private static final int SELECT_PICTURE = 1;
    FirebaseStorage storage;
    Bitmap galleryPic = null;
    user_info userInfo;
    ImageView trash_image, photo, pas;
    FireBaseLoad data;
    private EditText editName;
    private  TextView editEmail, editPassword;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView btn, exit, yes, search, testing, learning;

        editName =findViewById(R.id.name);
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        trash_image = findViewById(R.id.addImage);
        photo = findViewById(R.id.photo);
        pas = findViewById(R.id.imageView7);

        exit = findViewById(R.id.exit);
        yes = findViewById(R.id.yes);
        search = findViewById(R.id.search);
        testing = findViewById(R.id.testing);
        learning = findViewById(R.id.learning);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        data = new FireBaseLoad(this);
        aUser = FirebaseAuth.getInstance().getCurrentUser();
         uid = aUser.getUid();

        userInfo = profile_info.getInstance().getMy_user_info();

        editName.setText(userInfo.getName());
        editEmail.setText(userInfo.getEmail());
        yes.setText(userInfo.getYes()+"/10 очков");
        search.setText(userInfo.getSearch()+"/10 очков");
        testing.setText(userInfo.getTesting()+"/10 очков");
        learning.setText(userInfo.getLearnig()+"% материала");

        if (!userInfo.getPhoto().equals("")){
            final Transformation transformation = new MaskTransformation(ProfileActivity.this, R.drawable.rounded_convers_transformation);
            Picasso.get()
                    .load(userInfo.getPhoto())
                    .resize(200, 200) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .transform(transformation)
                    .into(photo);

        }


        trash_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PICTURE);

            }
        });

        pas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.sendPasswordResetEmail(userInfo.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Отправлено письмо на почту для смены пароля!", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

            }
        });

        btn = findViewById(R.id.recognition);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecognitionActivity.class);
                startActivity(intent);
            }
        });

        btn = findViewById(R.id.btnSave);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String pass  = editPassword.getText().toString();
                userInfo.setName(name);
               setDataFirebase(name);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentExit myDialogFragment = new DialogFragmentExit(ProfileActivity.this);
                FragmentManager manager = getSupportFragmentManager();
                //myDialogFragment.show(manager, "dialog");

                FragmentTransaction transaction = manager.beginTransaction();
                myDialogFragment.show(transaction, "dialog");
            }
        });


        // Learning Button
        btn = findViewById(R.id.main_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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






    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        switch (requestCode) {
            case SELECT_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        galleryPic = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.d("sdsdsd", selectedImage.toString());
                    uploadImage(selectedImage);

                   // photo.setImageBitmap(galleryPic);

                }
        }
    }


    private void uploadImage(Uri selectedImage) {



        if(selectedImage != null) {
            final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setTitle("Загрузка...");
            progressDialog.show();

            StorageReference ref = storageReference.child( UUID.randomUUID().toString());


            ref.putFile(selectedImage)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                        }

                    })

                    .addOnFailureListener(new OnFailureListener() {

                        @Override

                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();

                        }

                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override

                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot

                                    .getTotalByteCount());

                            progressDialog.setMessage("Uploaded " + (int) progress + "%");

                        }

                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    uploadedImageUrl = task.getResult().toString();
                                    final Transformation transformation = new MaskTransformation(ProfileActivity.this, R.drawable.rounded_convers_transformation);
                                    Picasso.get()
                                            .load(uploadedImageUrl)
                                            .resize(200, 200) // resizes the image to these dimensions (in pixel)
                                            .centerCrop()
                                            .transform(transformation)
                                            .into(photo);
                                    Log.d("map", uploadedImageUrl);
                                    setFirebasePhoto();
                                    userInfo.setPhoto(uploadedImageUrl);
                                }
                            });
                        }
                    });

        }
    }

    private  void setFirebasePhoto(){


        CollectionReference cities = db.collection("users");

        final Map<String, Object> addUserToItem = new HashMap<>();
        addUserToItem.put("photo", uploadedImageUrl);

        cities.document(uid).update(addUserToItem);




    }

    private void  setDataFirebase (String name){
        CollectionReference cities = db.collection("users");

        final Map<String, Object> addUserToItem = new HashMap<>();
        addUserToItem.put("name", name);
        Toast.makeText(getApplicationContext(), "Данные изменены!", Toast.LENGTH_LONG).show();

        cities.document(uid).update(addUserToItem);
    }

}


