package com.example.messagingappexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private ImageButton sendButton;

    private RecyclerView recView;
    private RecViewAdapter adapter;

    private FirebaseDatabase db;
    private FirebaseAuth auth;

    private DatabaseReference messagesRef;

    private ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = findViewById(R.id.message_edittext);
        sendButton = findViewById(R.id.send_button);

        db = FirebaseDatabase.getInstance("https://messagingappexample-4a072-default-rtdb.europe-west1.firebasedatabase.app");
        auth = FirebaseAuth.getInstance();

        messagesRef = db.getReference().child("messages");

        // RecyclerView
        recView = findViewById(R.id.recview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recView.setLayoutManager(layoutManager);

        adapter = new RecViewAdapter(this);
        recView.setAdapter(adapter);

        // Listeners
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString();

                if (!messageText.equals("")) {
                    Message message = new Message(messageText, auth.getCurrentUser().getEmail());

                    messagesRef.push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            messageEditText.setText("");
                        }
                    });
                }
            }
        });

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                for (DataSnapshot messageSnap : snapshot.getChildren()) {

                    Message message = new Message(
                            messageSnap.child("text").getValue().toString(),
                            messageSnap.child("senderEmail").getValue().toString()
                    );
                    messages.add(message);
                }

                adapter.setMessages(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}