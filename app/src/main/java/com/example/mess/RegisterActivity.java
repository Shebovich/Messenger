package com.example.mess;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccountButton, PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView Allready;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        initializefields();
        Allready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString().trim();
        String password = UserPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email) )
        {
            Toast.makeText(this,"Please enter email...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password) )
        {
            Toast.makeText(this,"Please enter email...",Toast.LENGTH_SHORT).show();
        } else
        {
            loading.setTitle("Creating New Account");
            loading.setMessage("Please wait...");
            loading.setCanceledOnTouchOutside(true);
            loading.show();
        }
        {
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {
                               String currentUserID = mAuth.getCurrentUser().getUid();
                               RootRef.child("Users").child(currentUserID).setValue("");

                               SendUserToMainActivity();

                               Toast.makeText(RegisterActivity.this,"Account created Succesful!",Toast.LENGTH_SHORT).show();
                               loading.dismiss();
                           }
                           else
                           {
                               String message = task.getException().toString();
                               Toast.makeText(RegisterActivity.this,"Error",Toast.LENGTH_SHORT).show();
                               loading.dismiss();
                           }

                        }
                    });
        }
    }

    private void initializefields() {
        CreateAccountButton = findViewById(R.id.register_button);
        UserEmail = findViewById(R.id.register_email);
        UserPassword = findViewById(R.id.register_password);
        Allready = findViewById(R.id.alredy_have_acc_link);
        loading = new ProgressDialog(RegisterActivity.this);
    }
    private void SendUserToMainActivity(){
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void SendUserToLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);

    }
}
