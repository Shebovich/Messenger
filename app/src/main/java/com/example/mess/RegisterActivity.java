package com.example.mess;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccountButton, PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView Allready;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private Map<String,Integer> translationsMap = new HashMap<>();
    private String resultStringLanguage =  null;
    private Map<Integer,String> languageMap = new HashMap<>();
    private ProgressDialog loading;
    private TextView chooseYourLanguage;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sessionManager = new SessionManager(this);
        languageMap.put(0,"ru");
        languageMap.put(1,"de");
        languageMap.put(2,"en");
        languageMap.put(3,"fr");
        languageMap.put(4,"ch");
        translationsMap.put("en", FirebaseTranslateLanguage.EN);
        translationsMap.put("ru", FirebaseTranslateLanguage.RU);
        translationsMap.put("de", FirebaseTranslateLanguage.DE);
        translationsMap.put("fr", FirebaseTranslateLanguage.FR);
        translationsMap.put("ch",FirebaseTranslateLanguage.ZH);
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        initializefields();
        Allready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
        CreateAccountButton.setOnClickListener(v -> CreateNewAccount());
        chooseYourLanguage.setOnClickListener(view ->setUpLanguageDialog()); {

        };
    }

    private void setUpLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your language...");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_language_picker, null);
        builder.setView(customLayout);


        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                RadioGroup radioGroup = customLayout.findViewById(R.id.languageGroup);
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioButtonID);
                int idx = radioGroup.indexOfChild(radioButton);
                resultStringLanguage = languageMap.get(idx);
                System.out.println("resultLanguage "+languageMap.get(idx));
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString().trim();
        String password = UserPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email) )
        {
            Toast.makeText(this,"Please enter email...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password) )
        {
            Toast.makeText(this,"Please enter email...",Toast.LENGTH_SHORT).show();
        } else if (resultStringLanguage==null){
            Toast.makeText(this,"Please choose language",Toast.LENGTH_SHORT).show();

        }
            else {
            loading.setTitle("Creating New Account");
            loading.setMessage("Please wait...");
            loading.setCanceledOnTouchOutside(true);
            loading.show();


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Map<String,String> hashMap = new HashMap<>();
                                hashMap.put("language",resultStringLanguage);
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserID).setValue("");
                                RootRef.child("Users").child(currentUserID).setValue(hashMap).addOnCompleteListener(task1 -> {
                                sessionManager.setLanguageUser(resultStringLanguage);
                                });
                                FirebaseModelManager modelManager = FirebaseModelManager.getInstance();
                                FirebaseTranslateRemoteModel frModel =
                                        new FirebaseTranslateRemoteModel.Builder(translationsMap.get(resultStringLanguage)).build();
                                FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                                        .build();
                                modelManager.download(frModel, conditions)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void v) {
                                                SendUserToMainActivity();
                                                sessionManager.setLogin(true);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Error.
                                            }
                                        });


                                Toast.makeText(RegisterActivity.this, "Account created Succesful!", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            } else {
                                String message = task.getException().toString();
                                System.out.println(message);
                                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }

                        }
                    });
        }
    }

    private void initializefields() {
        chooseYourLanguage = findViewById(R.id.choose_language);
        CreateAccountButton = findViewById(R.id.register_button);
        UserEmail = findViewById(R.id.register_email);
        UserPassword = findViewById(R.id.register_password);
        Allready = findViewById(R.id.alredy_have_acc_link);
        loading = new ProgressDialog(RegisterActivity.this);
    }
    private void SendUserToMainActivity(){
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.putExtra("language",resultStringLanguage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void SendUserToLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);

    }
}
