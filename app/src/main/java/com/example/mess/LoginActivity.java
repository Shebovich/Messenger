package com.example.mess;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity {

    private Button loginbutton, PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink, ForgetPasswordLink;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUser;
    private SessionManager sessionManager;
    private ProgressDialog loading;

    public LoginActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        initializeFields();
        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPassword1Activity.class);
                startActivity(intent);
            }
        });
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });
    }

    private void AllowUserToLogin() {
        String email = UserEmail.getText().toString().trim();
        String password = UserPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email) )
        {
            Toast.makeText(this,"Please enter email...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password) )
        {
            Toast.makeText(this,"Please enter email...",Toast.LENGTH_SHORT).show();
        }else
        {

            loading.setTitle("Sign in....");
            loading.setMessage("Please wait...");
            loading.setCanceledOnTouchOutside(true);
            loading.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                RootRef= FirebaseDatabase.getInstance().getReference();
                                currentUser = mAuth.getCurrentUser().getUid();
                                RetrieveUserInfo();
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this,"Logged in Succesful...",Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
        }
    }

    private void initializeFields() {
        loginbutton = findViewById(R.id.login_button);
        PhoneLoginButton = findViewById(R.id.phone_login_button);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        NeedNewAccountLink = findViewById(R.id.need_new_account_link);
        ForgetPasswordLink = findViewById(R.id.forget_password_link);
        loading = new ProgressDialog(this);
    }



    private void SendUserToMainActivity(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void SendUserToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        sessionManager.setLanguageUser(dataSnapshot.child("language").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public void initFcm(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        sessionManager.setFirebaseToken(token);
                        // Log and toast;


                    }
                });
    }
}
