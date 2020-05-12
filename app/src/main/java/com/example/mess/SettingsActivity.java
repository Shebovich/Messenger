package com.example.mess;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
private Button UpdateAccount;
private EditText username,userstatus;
private CircleImageView userProfileImage;
private String currentUserID;
private SessionManager sessionManager;
private FirebaseAuth mAuth;
private String languageUser;
private DatabaseReference RootRef;
private static final int GalleryPick =1;
private StorageReference UserProfileImageRef;
private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sessionManager = new SessionManager(this);
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        languageUser = intent.getStringExtra("language");
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        InitializeFields();
        username.setVisibility(View.INVISIBLE);

        UpdateAccount.setOnClickListener(v -> UpdateSettings());

        RetrieveUserInfo();
        userProfileImage.setOnClickListener(v -> {

            Intent gallery  = new Intent();
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            gallery.setType("image/*");
            startActivityForResult(gallery, GalleryPick);
        });
    }

    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {

                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                            username.setText(retrieveUserName);
                            userstatus.setText(retrievesStatus);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString();

                            username.setText(retrieveUserName);
                            userstatus.setText(retrievesStatus);
                        }
                        else
                        {
                            username.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void UpdateSettings() {
        String setUserName = username.getText().toString();
        String setUserStatus = userstatus.getText().toString();

        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(SettingsActivity.this,"Please write your username first...",Toast.LENGTH_SHORT).show();

        }
        if(TextUtils.isEmpty(setUserStatus))
        {
            Toast.makeText(SettingsActivity.this,"Please write your status...",Toast.LENGTH_SHORT).show();

        }
        else
        {
            HashMap<String,String> profilemap = new HashMap<>();
            profilemap.put("uid",currentUserID);
            profilemap.put("name",setUserName);
            profilemap.put("language",sessionManager.getLanguageUser());
            profilemap.put("status",setUserStatus);
            RootRef.child("Users").child(currentUserID).setValue(profilemap)
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful())
                        {
                            SendUserToMainActivity();
                            Toast.makeText(SettingsActivity.this,"Profile updated succesfuly...",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this,"Error +"+ message,Toast.LENGTH_SHORT).show();

                        }

                    });
        }
    }

    private void InitializeFields() {
        UpdateAccount = findViewById(R.id.update_settings_button);
        username = findViewById(R.id.set_user_name);
        userstatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please Wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                final Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    final String downloadUrl = uri.toString();
                    RootRef.child("Users").child(currentUserID).child("image").setValue(downloadUrl)
                            .addOnCompleteListener(task ->
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(SettingsActivity.this, "Profile image stored to firebase database successfully.", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                                else
                                {
                                    String message = Objects.requireNonNull(task.getException()).getMessage();
                                    Toast.makeText(SettingsActivity.this, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            });
                }));
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                assert result != null;
                Exception error = result.getError();
            }

        }
    }

            private void SendUserToMainActivity(){
        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
