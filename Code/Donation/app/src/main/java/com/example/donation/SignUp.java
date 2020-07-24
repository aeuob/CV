package com.example.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private EditText email;
    private EditText firstName;
    private EditText secondName;
    private EditText password;
    private EditText retypedPassword;
    private EditText address;
    boolean anonymous = false;
    private boolean canReceive = false;
    public static String emailUser ="";
    public static Member details;

    private Button anonymousButton;
    private Button signUpButton;

    private Map<String, Member> members = new HashMap<>();

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private long numberOfMembers = 0;
    private Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = (EditText)findViewById(R.id.EmailBox);
        firstName = (EditText)findViewById(R.id.FirstNameBox);
        secondName = (EditText)findViewById(R.id.SecondNameBox);
        password = (EditText)findViewById(R.id.PasswordBox);
        retypedPassword = (EditText)findViewById(R.id.RetypePasswordBox);
        address = (EditText)findViewById(R.id.AddressBox);

        anonymousButton = (Button)findViewById(R.id.AnonymousButton);
        signUpButton = (Button)findViewById(R.id.SignUpButton);

        member = new Member();
        details=new Member();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("members");

        // check the user has selected the anonymous button
        anonymousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(anonymous == false) {
                    anonymous = true;
                    displayMessage("You're anonymous");
                }else{
                    anonymous = false;
                    displayMessage("You're not anonymous");
                }
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	// emails format
                if(!checkEmail(email.getText().toString())){
                    displayMessage("Invalid email");
                }

                // names format (no numbers or special characters)
                else if(!checkName(firstName.getText().toString()) || !checkName(secondName.getText().toString())){
                    displayMessage("Invalid name(s)");
                }

                // check if the passwords match
                else if(!checkPasswords(password.getText().toString(), retypedPassword.getText().toString())){
                    displayMessage("Passwords do not match");
                }

                // the password has to contain at least 5 letter
                else if(!checkPasswordLength(password.getText().toString())){
                    displayMessage("The password needs to have at least 5 characters");
                }

                else if(! checkAddress(address.getText().toString()) ){
                    displayMessage("Invalid Address");
                } else if (!checkCity(address.getText().toString())){
                    displayMessage("Please add a valid city to the address");
                }
                else {
                	// create a member object and add the details that have been inputted
                    member.setEmail(email.getText().toString());
                    member.setFirstName(firstName.getText().toString());
                    member.setSecondName(secondName.getText().toString());
                    member.setPassword(password.getText().toString());
                    String addressReplacement = address.getText().toString().replace(',',';');
                    member.setAddress(addressReplacement);
                    member.setAnonymous(anonymous);
                    member.setCanReceive(canReceive);
                    member.setNegativeFeedback(0);
                    member.setPositiveFeedback(0);

                    emailUser = email.getText().toString();


                    // remove special characters that cannot be used in a key
                    String removedAt = member.getEmail().replace("@", "");
                    String removedFullStop = removedAt.replace(".", "I FullStop I");
                    String removedComma = removedFullStop.replace(",", "I Comma I");
                    String removedHashtag = removedComma.replace("#", "I Hashtag I");
                    String removedDollar = removedHashtag.replace("$", "I Dollar I");
                    String removedOpeningBracket = removedDollar.replace("[", "I OpeningBracket I");
                    final String removedBrackets = removedOpeningBracket.replace("]", "I ClosingBracket I");

                    details.setEmail(email.getText().toString());
                    details.setFirstName(firstName.getText().toString());
                    details.setSecondName(secondName.getText().toString());
                    details.setAnonymous(anonymous);
                    details.setCanReceive(canReceive);
                    details.setAddress(address.getText().toString());

                    // add the user to the database
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (!snapshot.hasChild(removedBrackets)) {
                                databaseReference.child(removedBrackets).setValue(member);
                                displayMessage("Login successful");
                                startApp();
                            }
                            else{
                             displayMessage("Email has been used");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //
                        }
                    });

                }
            }
        });


    }

    private Boolean checkEmail(String email) {

        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private Boolean checkName(String name) {
        return ((name != null)
                && (!name.equals(""))
                && (name.matches("^[a-zA-Z]*$")));
    }

    private Boolean checkPasswords(String password1, String password2) {
        return password1.equals(password2);
    }

    private Boolean checkPasswordLength(String password) {
        if(password.length()<5)
            return false;
        else
            return true;
    }

    private Boolean checkAddress(String address) {
        return ((address != null)
                && (!address.equals(""))
                && (address.matches("^[a-zA-Z0-9 ,]+$")));
    }

    private Boolean checkCity(String address){
        int arrayLength = MapsActivity.cities.length;
        boolean check = false;
        for(int i = 0; i < arrayLength; i++){

            if (address.contains(MapsActivity.cities[i])){
                check = true;
            }
        }

        return check;
    }
    private void displayMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void startApp(){
        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
    }

    public static String returnEmail(){
        return emailUser;
    }


    public static Member signUpDetails(){
        return details;
    }
}
