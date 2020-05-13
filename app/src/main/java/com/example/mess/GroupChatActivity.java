package com.example.mess;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mess.Adapters.GroupChatMessagesAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar nToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private GroupChatMessagesAdapter adapter;
    private RecyclerView recyclerView;
    private List<MessageEntity> messageEntityList;
    private SessionManager sessionManager;
    private LinearLayoutManager layoutManager;
    private Map<String,Integer> translationsMap = new HashMap<>();
    private TextView displayTextMessages;
    private String currentGroupName, currentUserID,currentUserName,currentDate, currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, GroupNameRef, GroupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        sessionManager = new SessionManager(this);
        translationsMap.put("en", FirebaseTranslateLanguage.EN);
        translationsMap.put("ru", FirebaseTranslateLanguage.RU);
        translationsMap.put("de", FirebaseTranslateLanguage.DE);
        translationsMap.put("fr", FirebaseTranslateLanguage.FR);

        currentGroupName = getIntent().getExtras().get("groupName").toString();


        mAuth= FirebaseAuth.getInstance();
currentUserID = mAuth.getCurrentUser().getUid();
UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);




    }

    @Override
    protected void onResume() {
        super.onResume();
        messageEntityList = new ArrayList<>();
        InitializeFields();
        GetUserInfo();
        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageInfoToDatabase();
                userMessageInput.setText("");
                recyclerView.scrollToPosition(0);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void DisplayMessages(DataSnapshot dataSnapshot) {


            MessageEntity messageEntity = dataSnapshot.getValue(MessageEntity.class);


            System.out.println("TIME TIME "+ messageEntity.getTime());

            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(translationsMap.get(messageEntity.getLanguage()))
                            .setTargetLanguage(translationsMap.get(sessionManager.getLanguageUser()))
                            .build();
            final FirebaseTranslator translator =
                    FirebaseNaturalLanguage.getInstance().getTranslator(options);
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                    .build();
            translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void v) {
                                    translator.translate(messageEntity.getMessage())
                                            .addOnSuccessListener(
                                                    translatedText -> {
                                                        messageEntity.setMessage(translatedText);
                                                        messageEntityList.add(messageEntity);
                                                        Collections.sort(messageEntityList);
                                                        Collections.reverse(messageEntityList);
                                                        adapter.notifyDataSetChanged();
                                                    })
                                            .addOnFailureListener(
                                                    e -> {

                                                    });
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Model couldn’t be downloaded or other internal error.
                                    // ...
                                }
                            });



    }

    private void SaveMessageInfoToDatabase() {
        String message = userMessageInput.getText().toString();
        String messageKey = GroupNameRef.push().getKey();
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this,"Please, write message...",Toast.LENGTH_SHORT).show();

        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentDate = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);
            GroupMessageKeyRef = GroupNameRef.child(messageKey);
            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("time", System.currentTimeMillis());
            messageInfoMap.put("language",sessionManager.getLanguageUser());
            messageInfoMap.put("from",currentUserID);
            GroupMessageKeyRef.updateChildren(messageInfoMap);




        }

    }

    private void GetUserInfo() {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void InitializeFields() {
        nToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(nToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        SendMessageButton = findViewById(R.id.send_message_btn);
        userMessageInput = findViewById(R.id.input_message);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new GroupChatMessagesAdapter(messageEntityList,this);
        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);



    }
}
