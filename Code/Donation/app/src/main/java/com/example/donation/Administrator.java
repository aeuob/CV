package com.example.donation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Administrator extends AppCompatActivity {

    private TextView textView;
    private Button map;
    private EditText name;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        textView = findViewById(R.id.verifying_info);
        map = findViewById(R.id.map_button);
        name = findViewById(R.id.name_verified);

        send = findViewById(R.id.change_verified_button);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("members");



        // redirects to the map activity
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Administrator.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        // send button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // going through every key from the database
                        for (DataSnapshot members : dataSnapshot.getChildren()) {
                            Object values = members.getValue();
                            // convert the value into a string
                            String stringValues = values.toString();
                            // split into multiple strings
                            String[] strings = stringToValues(stringValues);

                            Member member = new Member();
                            member.setAddress(strings[0]);
                            member.setFirstName(strings[1]);
                            member.setPassword(strings[2]);
                            member.setNegativeFeedback(Integer.parseInt(strings[3]));
                            member.setSecondName(strings[4]);
                            member.setCanReceive(true);
                            member.setEmail(strings[6]);
                            displayMessage(name.getText().toString() + " " + member.getEmail().toString());
                            // if the name is in the database
                            if(name.getText().toString().equals(member.getEmail().toString())) {
                                member.setAnonymous(Boolean.valueOf(strings[7].trim()));
                                member.setPositiveFeedback(Integer.parseInt(strings[8]));

                                String removedAt = member.getEmail().replace("@", "");
                                String removedFullStop = removedAt.replace(".", "I FullStop I");
                                String removedComma = removedFullStop.replace(",", "I Comma I");
                                String removedHashtag = removedComma.replace("#", "I Hashtag I");
                                String removedDollar = removedHashtag.replace("$", "I Dollar I");
                                String removedOpeningBracket = removedDollar.replace("[", "I OpeningBracket I");
                                String removedBrackets = removedOpeningBracket.replace("]", "I ClosingBracket I");

                                databaseReference.child(removedBrackets).setValue(member);
                            }
                            else{
                                displayMessage("Failed");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //
                    }


                });
            }
        });

    }

    public String[] stringToValues(String string){

        String[] strings = new String[9];
        String[] parts = string.split(", ");

        String address = parts[0];
        String firstName = parts[1];
        String password = parts[2];
        String negativeFeedback = parts[3];
        String secondName = parts[4];
        String verified = parts[5];
        String email = parts[6];
        String anonymous = parts[7];
        String positiveFeedback = parts[8];

        address = address.trim();
        strings[0] = address.substring(9);
        strings[1] = firstName.substring(10);
        strings[2] = password.substring(9);
        strings[3] = negativeFeedback.substring(17);
        strings[4] = secondName.substring(11);
        strings[5] = verified.substring(9);
        strings[6] = email.substring(6);
        strings[7] = anonymous.substring(10);
        strings[8] = positiveFeedback.substring(17, positiveFeedback.length()-1);


        return strings;
    }

    private void displayMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Administrator.this, Login.class);
        startActivity(intent);
    }
}
