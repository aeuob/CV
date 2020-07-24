package com.example.donation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donation.ui.list_item.ListItemFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Description extends AppCompatActivity {

    private EditText text;
    private Button toAddingPhotos;
    public static ItemDetails itemDetails;
    private long days = 1;
    private long hours = 0;
    private long minutes = 0;
    private long seconds = 0;
    private String currentDateTime;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);


        text = findViewById(R.id.description_text);
        toAddingPhotos = findViewById(R.id.to_photos);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("item details");

        toAddingPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // keep the description smaller than 250 characters
                if (text.getText().toString().length()>250){
                    displayMessage("Characters exceeded: " + text.getText().toString().length() + "/250");
                } else {
                    // set all of the Item Details
                    itemDetails = ListItemFragment.getItemDetails();
                    itemDetails.setDescription(text.getText().toString());
                    itemDetails.setBidders("");
                    itemDetails.setNumberOfBidders(0);
                    itemDetails.setTime(DateUtils.DAY_IN_MILLIS * days +
                            DateUtils.HOUR_IN_MILLIS * hours +
                            DateUtils.MINUTE_IN_MILLIS * minutes +
                            DateUtils.SECOND_IN_MILLIS * seconds);

                    sendToDatabase();

                    Intent intent = new Intent(Description.this, AddPhotos.class);
                    startActivity(intent);
                }
            }
        });

    }

    public static ItemDetails getItemDetails(){
        return itemDetails;
    }

    private void displayMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void sendToDatabase(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // get the user's email in the specific format
                String userDBFormat;
                if(Login.returnEmail() == null){
                    userDBFormat = changeToDBFormat(SignUp.returnEmail());
                }else {
                    userDBFormat = changeToDBFormat(Login.returnEmail());
                }
                // upload the description as a new item(using the date and time as a key)
                if (snapshot.child(userDBFormat).hasChild(userDBFormat) == false) {
                    String currentDateAndTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    currentDateTime = currentDateAndTime;
                    databaseReference.child(userDBFormat).child(currentDateAndTime). setValue(itemDetails);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }

        });
    }
    public String changeToDBFormat(String email){
        String removedAt = email.replace("@", "");
        String removedFullStop = removedAt.replace(".", "I FullStop I");
        String removedComma = removedFullStop.replace(",", "I Comma I");
        String removedHashtag = removedComma.replace("#", "I Hashtag I");
        String removedDollar = removedHashtag.replace("$", "I Dollar I");
        String removedOpeningBracket = removedDollar.replace("[", "I OpeningBracket I");
        String removedBrackets = removedOpeningBracket.replace("]", "I ClosingBracket I");
        return  removedBrackets;
    }

    @Override
    public void onBackPressed() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String userDBFormat = changeToDBFormat(Login.returnEmail());
                    databaseReference.child(userDBFormat).child(currentDateTime).removeValue();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }

        });
        Intent intent = new Intent(Description.this, MainAppPage.class);
        startActivity(intent);
    }
}
