package com.example.donation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private EditText email;
    private String verifyEmail = "";
    private EditText password;
    private Button loginButton;
    private Button signUpButton;
    private int loginAttempts = 5;
    private static String usersEmail;
    private static String canReceive;
    public static ArrayList<String> infoForListing;
    public static ArrayList<String> user;
    public static ArrayList<String> descriptionArrayList;
    public static ArrayList<String> itemListingTimes;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private ArrayList blockedEmails = new ArrayList<String>();

    private boolean verified = false;
    private long time;
    public static Member member;
    public static String membersKey;
    public static ArrayList<String> categories;
    public static ArrayList<String> addresses;
    public static Member currentMember;
    public static Boolean finishedForAccountDetails = false;
    public static String firstName;
    public static String secondName;
    public static String address;
    public static String emailUser;
    public static Boolean anonymous;
    public static Boolean verifiedUser;
    public static float negativeFeedback;
    public static float positiveFeedback;
    public static String passwordUser;
    public static String retypedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.EmailBox);
        password = findViewById(R.id.PasswordBox);
        loginButton = findViewById(R.id.LoginButton);
        signUpButton = findViewById(R.id.SignUpButton);

        getItemDetails();

        // Get a reference to our posts
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("members");

        member = new Member();
        user = new ArrayList<>();
        descriptionArrayList = new ArrayList<>();
        itemListingTimes = new ArrayList<>();
        categories = new ArrayList<>();
        addresses = new ArrayList<>();
        infoForListing = new ArrayList<>();
        currentMember = new Member();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (blockedEmails.contains(email.getText().toString())) {
                    displayMessage("Try again later!");
                } else {
                    // compare the user's info to the database and save the information for other classes
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot members : dataSnapshot.getChildren()) {

                                saveKey(convertEmail(members.getKey()));
                                Object values = members.getValue();
                                String stringValues = values.toString();

                                String[] strings = stringToValues(stringValues);

                                String emailCheck = strings[6];
                                String passwordCheck = strings[2];

                                member.setAddress(strings[0]);
                                member.setFirstName(strings[1]);
                                member.setPassword(strings[2]);
                                member.setNegativeFeedback(Integer.parseInt(strings[3]));
                                member.setSecondName(strings[4]);
                                member.setCanReceive(Boolean.valueOf(strings[5].trim()));
                                member.setEmail(strings[6]);
                                member.setAnonymous(Boolean.valueOf(strings[7]));
                                member.setPositiveFeedback(Integer.parseInt(strings[8]));
                                String userAndAddress = convertEmail(strings[6]) + ";" + strings[0];

                                infoForListing.add(members.getKey() + "!" + strings[0] + "!" + strings[7] + " " + strings[6] + "?" + strings[8] + " " + strings[3]);

                                if (categories.toString().contains(convertEmail(strings[6]))) {
                                    addresses.add(userAndAddress);
                                }
                                member.setAnonymous(Boolean.valueOf(strings[7].trim()));
                                member.setPositiveFeedback(Integer.parseInt(strings[8]));

                                if (emailCheck.equals(email.getText().toString()) && passwordCheck.equals(password.getText().toString())) {
                                    displayMessage("Successful Login");
                                    currentMember = member;
                                    finishedForAccountDetails = true;
                                    setVerified(true);
                                    firstName = member.getFirstName();
                                    secondName =  member.getSecondName();
                                    address = member.getAddress() ;
                                    emailUser =  member.getEmail() ;
                                    anonymous = member.isAnonymous() ;
                                    verifiedUser = member.isVerified();
                                    negativeFeedback = member.getNegativeFeedback();
                                    passwordUser = member.getPassword();
                                    retypedPassword = member.getRetypedPassword();
                                    positiveFeedback = member.getPositiveFeedback();
                                    usersEmail = convertEmail(member.getEmail());
                                    startApp();
                                } else if (verified == false) {
                                    if (email.getText().toString().equals("admin") && password.getText().toString().equals("qwerty")) {
                                        startAdministrator();
                                    } else {
                                        displayMessage("invalid email or password");
                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //
                        }


                    });

                }

                blockEmail(email.getText().toString());

                if (verified == false) {
                    loginAttempts--;
                }

                }


        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

    }

    private void saveKey(String key) {
        membersKey = key;
    }

    private void startApp() {
        Intent intent = new Intent(Login.this, MainAppPage.class);
        startActivity(intent);
    }

    private void startAdministrator() {
        Intent intent = new Intent(Login.this, Administrator.class);
        startActivity(intent);
    }

    private void blockEmail(String email) {

        if (verifyEmail.equals(""))
            setEmail(email);
        else
            setEmail("");

        if (loginAttempts <= 0 && (email.equals(verifyEmail) || blockedEmails.contains(email))) {

            Context context = getApplicationContext();
            CharSequence text = "Your account is locked";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            blockedEmails.add(email);

        }
    }

    private void setEmail(String email) {
        verifyEmail = email;
    }

    private void displayMessage(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // convert into multiple strings
    public String[] stringToValues(String string) {

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

        //address = address.trim();

        strings[0] = address.substring(9);
        strings[1] = firstName.substring(10);
        strings[2] = password.substring(9);
        strings[3] = negativeFeedback.substring(17);
        strings[4] = secondName.substring(11);
        strings[5] = verified.substring(9);
        strings[6] = email.substring(6);
        strings[7] = anonymous.substring(10);
        strings[8] = positiveFeedback.substring(17, positiveFeedback.length() - 1);


        return strings;
    }

    // fetch the item details information from the database
    private void getItemDetails() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("item details");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot members : dataSnapshot.getChildren()) {
                    user.add(members.getKey());
                    itemListingTimes.add(members.getValue().toString().substring(1, 16));

                    String userAndCategory = members.getKey() + ";";
                    int category;
                    if (members.getValue().toString().contains("clothes=true")) {
                        category = 1;
                    } else if (members.getValue().toString().contains("electronics=true")) {
                        category = 2;
                    } else if (members.getValue().toString().contains("food=true")) {
                        category = 3;
                    } else if (members.getValue().toString().contains("toys=true")) {
                        category = 4;
                    } else {
                        category = 5;
                    }
                    categories.add(userAndCategory + category);
                    // length of ", description="
                    int additionalLength = 14;
                    int descriptionPosition1 = members.getValue().toString().indexOf(", description=") + additionalLength;
                    int descriptionPosition2 = members.getValue().toString().indexOf(", time=");

                    if (members.getValue() == null){

                    }
                    String itemsDescriptions = members.getValue().toString().substring(descriptionPosition1, descriptionPosition2);
                    descriptionArrayList.add(itemsDescriptions);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void setVerified(Boolean bool) {
        verified = bool;
    }

    // convert the email so it can be used as a key in the database
    private String convertEmail(String string) {
        String removedAt = string.replace("@", "");
        String removedFullStop = removedAt.replace(".", "I FullStop I");
        String removedComma = removedFullStop.replace(",", "I Comma I");
        String removedHashtag = removedComma.replace("#", "I Hashtag I");
        String removedDollar = removedHashtag.replace("$", "I Dollar I");
        String removedOpeningBracket = removedDollar.replace("[", "I OpeningBracket I");
        String removedBrackets = removedOpeningBracket.replace("]", "I ClosingBracket I");

        return removedBrackets;
    }

    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - time > 1500) {
            time = t;
            displayMessage("Press again to exit");
        } else {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }

    // the user's email
    public static String returnEmail() {
        return usersEmail;
    }

    // check if the user is verified
    public static String returnVerified() {
        return canReceive;
    }

// get the current user's details
    public static Member getDetails() {
        return currentMember;
    }

    // get a key for a member
    public static String getMemberKey() {
        return membersKey;
    }

    // get the users that have listings
    public static ArrayList<String> getListedUsers() {
        return user;
    }

    // get the listing descriptions
    public static ArrayList<String> getListedDescriptions() {
        return descriptionArrayList;
    }

    // store the categories(when fetching the info for item details)
    public static ArrayList<String> getCategories() {
        return categories;
    }

    // the users addresses
    public static ArrayList<String> getAddresses() {
        return addresses;
    }

    // the listing times
    public static ArrayList<String> getItemListingTimes() {
        return itemListingTimes;
    }

    // multiple details are stored in this array such as number of negative feedback, positive feedback, current user,...
    public static ArrayList<String> getInfoForListing() {
        return infoForListing;
    }

}

