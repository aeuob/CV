package com.example.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.DataCollectionConfigStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static com.example.donation.Login.getListedUsers;
import static com.example.donation.Login.getMemberKey;

public class ItemListing extends AppCompatActivity {

    public static int position;
    private TextView timer;
    private Button biddingButton;
    private int numberOfBids = 0;
    private long days = 5;
    private long hours = 0;
    private long minutes = 0;
    private long seconds = 0;
    private String location;
    private int timesPressed;
    private Boolean finished = false;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    private ItemDetails updatedItemDetails;

    private long mTimeLeftInMillis;
    private long mEndTime;
    // one day in milliseconds
    private static final long START_TIME_IN_MILLIS = 1;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private ImageView imageListing1;
    private ImageView imageListing2;
    private TextView userDetails;
    private ImageView userImage;
    private ImageView positive;
    private ImageView negative;
    private TextView listingDescription;
    private Button sendMessage;
    private String currentItemNumber;
    private String currentUser;
    private String bidders = "";
    private int numberOfBidders = 0;
    private String winner;
    private String currentItemUserEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_listing);

        // fetch the items date and time
        Login.getMemberKey();
        timer = findViewById(R.id.time_listing);
        imageListing1 = findViewById(R.id.image_listing_1);
        imageListing2 = findViewById(R.id.image_listing2);
        listingDescription = findViewById(R.id.listing_description);
        userDetails = findViewById(R.id.details_in_listing);
        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        userImage = findViewById(R.id.user_image);
        sendMessage = findViewById(R.id.message_button);

        storage = FirebaseStorage.getInstance();

        positive.setVisibility(View.INVISIBLE);
        negative.setVisibility(View.INVISIBLE);

        timesPressed = 0;
        Login.getItemListingTimes();

        // prepare the information that will be displayed, by collecting it from other classes
        // the items date and time
        currentItemNumber = String.valueOf(getCurrentTimeLeft(Login.getItemListingTimes().get(position)));
        // the current user(in the specific format)
        currentUser = Login.getListedUsers().get(position);

        // prepare the time for the listing (listing time - current time)
        prepareTimer(getCurrentTimeLeft(Login.getItemListingTimes().get(position)));
        startTimer();
        biddingButton = findViewById(R.id.bid_button);

        // the listing's user
        String userToCity = Login.getListedUsers().get(position);
        // the city where the user is from
        String city = getCityFromUser(Login.getInfoForListing(), userToCity);

        String anonymousAndEmail = getAddressFromUser(Login.getInfoForListing(), userToCity);

        //separating the information received from the different classes
        anonymousAndEmail = anonymousAndEmail.substring(1);
        int spacePosition = anonymousAndEmail.indexOf(" ");
        String anonymous = anonymousAndEmail.substring(0, spacePosition);
        int startOfNumber = anonymousAndEmail.lastIndexOf("?");
        // the users email
        final String email = anonymousAndEmail.substring(spacePosition+1, startOfNumber);


        int middleSpace = anonymousAndEmail.lastIndexOf(" ");

        final String positiveFeedback = anonymousAndEmail.substring(startOfNumber+1, middleSpace);
        String negativeFeedback = anonymousAndEmail.substring(middleSpace+1);
        float feedback = 0;
        // formula for when there is no positive feedback
        if(!positiveFeedback.equals("0")){
            feedback = Integer.parseInt(positiveFeedback) % (Integer.parseInt(negativeFeedback) + Integer.parseInt(positiveFeedback));
        }

        // add the city if the user is not anonymous
        if(anonymous.equals("true")){
            userDetails.setText("Location: " + city + "\nFeedback: " + feedback + "%");
        }
        else{
            userDetails.setText("Email: " +  email.substring(0,email.length()/2) + "***" + "\n" + "Location: " + city + "\nFeedback: " + feedback + "%");
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("item details");
        // get the current user's info from Login.java
        final Member member = new Member();
        member.setAddress(Login.address);
        member.setFirstName(Login.firstName);
        member.setPassword(Login.passwordUser);
        member.setNegativeFeedback(Login.negativeFeedback);
        member.setSecondName(Login.secondName);
        member.setCanReceive(Login.verifiedUser);
        member.setEmail(Login.emailUser);
        member.setAnonymous(Login.anonymous);
        member.setPositiveFeedback(Login.positiveFeedback);

        // if the user is verified
        if(member.isVerified()){
            // if the current user is the same as the listing's user
            if(currentUser.equals(convertEmail(member.getEmail()))){
                displayMessage("This is your item");
            } else if (biddingButton.isEnabled() && mTimerRunning == true ) {

                biddingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add 1 to the number of bids and add the current users name to the bidders
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//
                                for (DataSnapshot members : dataSnapshot.getChildren()) {
                                    // the time the item was listed at
                                    Object values = members.getValue();
                                    String itemNumber = null;

                                    String[] strings = stringToValues(values.toString());
                                    itemNumber = values.toString().substring(1, 16);
//
                                    updatedItemDetails = new ItemDetails();
                                    updatedItemDetails.setElectronics(Boolean.valueOf(strings[0]));
                                    updatedItemDetails.setDescription(strings[1]);
                                    updatedItemDetails.setTime(Long.parseLong(strings[2]));
                                    updatedItemDetails.setFood(Boolean.valueOf(strings[3]));
                                    updatedItemDetails.setOther(Boolean.valueOf(strings[4]));
                                    if(!strings[5].contains(member.getEmail())) {
                                        updatedItemDetails.setBidders(strings[5] + ";" + convertEmail(member.getEmail()));
                                        bidders = strings[5] + ";" + convertEmail(member.getEmail());
                                    }
                                    else{
                                        break;
                                    }
                                    numberOfBids = Integer.parseInt(strings[6]) + 1;
                                    numberOfBidders = numberOfBids;
                                    updatedItemDetails.setNumberOfBidders(numberOfBids);
                                    updatedItemDetails.setClothes(Boolean.valueOf(strings[7]));
                                    updatedItemDetails.setToys(Boolean.valueOf(strings[8]));
//

                                    String userListing = Login.getListedUsers().get(position);

                                    if (userListing == convertEmail(Login.returnEmail())) {
                                        displayMessage("It's your listing!");
                                    } else {
                                        //bidders contains the current users email
                                        // make sure the user does not add the name to the bidders for many times
                                        if (strings[5].contains(convertEmail(Login.returnEmail())) == false && timesPressed < 1) {
                                            databaseReference.child(userListing).child(itemNumber).setValue(updatedItemDetails);
                                            timesPressed++;

                                        } else if (timesPressed != 0) {
                                            displayMessage("Bid submitted");
                                        }
                                    }
//
//
                                }
                            }
                            //
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //
                            }


                            // }
                            //else {
                            // displayMessage("You are not verified");
                            //}


                        });

                    }
                });
            } else{
                displayMessage("Item Ended");
            }
        } else{


            biddingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayMessage("You need to verify!");
                }
            });
        }

        // getting the information after the bidding has finished
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//
                for (DataSnapshot members : dataSnapshot.getChildren()) {
                    // the time the item was listed at
                    Object values = members.getValue();
                    String itemNumber = null;
                    String[] strings = stringToValues(values.toString());
                    itemNumber = values.toString().substring(1, 16);
                    if(itemNumber.equals( Login.getItemListingTimes().get(position))) {
                        bidders = strings[5];
                        numberOfBidders = Integer.parseInt(strings[6]);
                    }
                }
            }
            //
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }

        });
        final ArrayList<String> user = Login.getListedUsers();
        final ArrayList<String> listedDescriptions = Login.getListedDescriptions();

        String[] descriptions = new String[listedDescriptions.size()];
        for(int i =0;i< listedDescriptions.size();i++){

            descriptions[i] = listedDescriptions.get(i);

        }
        //uploading the images from the database
        if(descriptions.length > 0) {
            location = "images/" + user.get(position) + "/1";
            storageReference = storage.getReference(location);
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    String profileImageUrl = task.getResult().toString();
                    Glide.with(getApplicationContext()).load(profileImageUrl).into(imageListing1);
                    listingDescription.setText(listedDescriptions.get(position ));


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("error: ", e.getMessage());
                }
            });

            location = "images/" + user.get(position) + "/2";
            storageReference = storage.getReference(location);
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    try{
                        String profileImageUrl = task.getResult().toString();
                        Glide.with(getApplicationContext()).load(profileImageUrl).into(imageListing2);
                    } catch (Exception e){
                        int profileImageUrl = R.drawable.not_available;
                        Glide.with(getApplicationContext()).load(profileImageUrl).into(imageListing2);
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("error: ", e.getMessage());
                }
            });

        }
        // send a message by opening an email app
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
                startActivity(Intent.createChooser(intent, ""));
            }
        });


        // add 1 the number of positive feedback and remove the item 
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finished == false) {
                    removeListing();
                }else if (finished == true) {
                    databaseReference = database.getReference("members");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//
                            for (DataSnapshot members : dataSnapshot.getChildren()) {
                                // the time the item was listed at
                                Object values = members.getValue();
                                final String itemNumber = null;
                                String[] strings = memberStringToValues(values.toString());

                                Member member = new Member();
                                member.setPositiveFeedback(Integer.valueOf(strings[8]) + 1);

                                final String userListing = Login.getListedUsers().get(position);
                                member.setAddress(strings[0]);
                                member.setFirstName(strings[1]);
                                member.setPassword(strings[2]);
                                member.setNegativeFeedback(Integer.parseInt(strings[3]));
                                member.setSecondName(strings[4]);
                                member.setCanReceive(Boolean.valueOf(strings[5].trim()));
                                member.setEmail(strings[6]);
                                member.setAnonymous(Boolean.valueOf(strings[7]));
                                databaseReference.child(userListing).setValue(member);
                                //displayMessage("Feedback Sent");


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //
                        }


                        // }
                        //else {
                        // displayMessage("You are not verified");
                        //}


                    });
                }
            }
        });

        // add 1 to the negative feeback in the database and remove the item
        negative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (finished == true){
                    databaseReference = database.getReference("members");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//
                            for (DataSnapshot members : dataSnapshot.getChildren()) {
                                // the time the item was listed at
                                Object values = members.getValue();
                                String itemNumber = null;
                                String[] strings = memberStringToValues(values.toString());

                                String userListing = Login.getListedUsers().get(position);

                                Member member = new Member();
                                member.setPositiveFeedback(Integer.valueOf(strings[8]));
                                member.setAddress(strings[0]);
                                member.setFirstName(strings[1]);
                                member.setPassword(strings[2]);
                                member.setNegativeFeedback(Integer.parseInt(strings[3]));
                                member.setSecondName(strings[4]);
                                member.setCanReceive(Boolean.valueOf(strings[5].trim()));
                                member.setEmail(strings[6]);
                                member.setAnonymous(Boolean.valueOf(strings[7]));

                                databaseReference.child(userListing).setValue(member);
                                //displayMessage("Feedback Sent");

                                databaseReference = database.getReference("members");
                            }
                        }

                        //
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //
                        }


                        // }
                        //else {
                        // displayMessage("You are not verified");
                        //}


                    });
                }
            }

        });



    }

    // convert into multiple string for the item details
    private String[] stringToValues(String string) {
        String[] strings = new String[9];
        String[] parts = string.split(", ");

        String electronics = parts[0];
        String description = parts[1];
        String time = parts[2];
        String other = parts[3];
        String food = parts[4];
        String bidders = parts[5];
        String numberOfBidders = parts[6];
        String clothes = parts[7];
        String toys = parts[8];


        electronics = electronics.trim();
        strings[0] = electronics.substring(30);
        strings[1] = description.substring(12);
        strings[2] = time.substring(5);
        strings[3] = food.substring(5);
        strings[4] = other.substring(6);
        // add the members key/ID to the bidders
        strings[5] = bidders.substring(8);
        strings[6] = numberOfBidders.substring(16);
        strings[7] = clothes.substring(8);
        strings[8] = toys.substring(5, toys.length()-1);

        return strings;
    }

// convert into multiple string for the members
    public String[] memberStringToValues(String string) {

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

    private void displayMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

  
    // timer methods
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                timer.setText("The listing has ended");
                positive.setVisibility(View.VISIBLE);
                negative.setVisibility(View.VISIBLE);

                // get a random winnner
                if(numberOfBidders!=0) {
                    winner = getWinner(bidders, numberOfBidders);

                    // remove all the bidders and upload the winner instead in the database
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot members : dataSnapshot.getChildren()) {
                                // the time the item was listed at
                                Object values = members.getValue();
                                String itemNumber = null;
                                String[] strings = stringToValues(values.toString());
                                itemNumber = values.toString().substring(1, 16);
//
                                updatedItemDetails = new ItemDetails();
                                updatedItemDetails.setElectronics(Boolean.valueOf(strings[0]));
                                updatedItemDetails.setDescription(strings[1]);
                                updatedItemDetails.setTime(Long.parseLong(strings[2]));
                                updatedItemDetails.setFood(Boolean.valueOf(strings[3]));
                                updatedItemDetails.setOther(Boolean.valueOf(strings[4]));

                                updatedItemDetails.setBidders(";"+winner);
                                numberOfBids = Integer.parseInt(strings[6]);
                                numberOfBidders = numberOfBids;
                                updatedItemDetails.setNumberOfBidders(numberOfBids);
                                updatedItemDetails.setClothes(Boolean.valueOf(strings[7]));
                                updatedItemDetails.setToys(Boolean.valueOf(strings[8]));

                                databaseReference.child(currentUser).child(itemNumber).setValue(updatedItemDetails);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                // convert the listing user's email back to an email 
                if(winner.equals(convertEmail(Login.emailUser))) {
                    String user = Login.getListedUsers().get(position);
                    String removedFullStop = user.replace("I FullStop I", ".");
                    String removedComma = removedFullStop.replace( "I Comma I", ",");
                    String removedHashtag = removedComma.replace("I Hashtag I", "#");
                    String removedDollar = removedHashtag.replace("I Dollar I", "$");
                    String removedOpeningBracket = removedDollar.replace("I OpeningBracket I", "[");
                    String removedBrackets = removedOpeningBracket.replace("I ClosingBracket I", "]");
                    userDetails.setText("You have won!, the owner is " + removedBrackets + "\n please email him");

                } else{
                    // hide the feedback button in case you did not win
                    userDetails.setText("You have not won");
                    positive = findViewById(R.id.positive);
                    negative = findViewById(R.id.negative);
                    positive.setVisibility(View.INVISIBLE);
                    negative.setVisibility(View.INVISIBLE);

                }
            }
        }.start();

        mTimerRunning = true;
    }

    private void prepareTimer(int time) {
        mTimeLeftInMillis = time;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis - (3600 * hours * 1000)) / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        timer.setText(timeLeftFormatted);
    }


    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis <= 1000) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
            } else {
                startTimer();
            }
        }
    }

    private String convertEmail(String string){
        String removedAt = string.replace("@", "");
        String removedFullStop = removedAt.replace(".", "I FullStop I");
        String removedComma = removedFullStop.replace(",", "I Comma I");
        String removedHashtag = removedComma.replace("#", "I Hashtag I");
        String removedDollar = removedHashtag.replace("$", "I Dollar I");
        String removedOpeningBracket = removedDollar.replace("[", "I OpeningBracket I");
        String removedBrackets = removedOpeningBracket.replace("]", "I ClosingBracket I");

        return removedBrackets;
    }

    // find out the time remaining by checking the time and date when the listing was created and the current time
    private int getCurrentTimeLeft(String itemTimeNumber){

        int timeRemaining;
        String itemTimeCreated = itemTimeNumber.substring(9);
        String currentDateAndTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String currentTime = currentDateAndTime.substring(9);

        String yearAndMonth1 = itemTimeNumber.substring(0,6);
        String yearAndMonth2 = currentDateAndTime.substring(0,6);

        int day1 = Integer.parseInt(itemTimeNumber.substring(6,8));
        int day2 = Integer.parseInt(currentDateAndTime.substring(6,8));



        if ((day2 -day1 > 1 && yearAndMonth1.equals(yearAndMonth2)) || (day1 != day2  && !yearAndMonth1.equals(yearAndMonth2))){
            timeRemaining = 1;
        }
        else {


            int time1 = Integer.parseInt(itemTimeCreated);

            // converting the time into hours, minutes and seconds
            int hours1 = time1 / 10000;
            int minutes1 = (time1 - hours1 * 10000) / 100;
            int seconds1 = (time1 - hours1 * 10000) % 100;
            //time in milliseconds
            int timeInSeconds1 = seconds1 + 60 * minutes1 + 3600 * hours1;

            int time2 = Integer.parseInt(currentTime);
            // converting the time into hours, minutes and seconds
            int hours2 = time2 / 10000;
            int minutes2 = (time2 - hours2 * 10000) / 100;
            int seconds2 = (time2 - hours2 * 10000) % 100;
            // time in millisends
            int timeInSeconds2 = seconds2 + 60 * minutes2 + 3600 * hours2;

            // 86400000 represents 1 day
            if(timeInSeconds1 < timeInSeconds2) {
                timeRemaining = 86400000 - Math.abs(timeInSeconds1 - timeInSeconds2) * 1000;
            } else{
                timeRemaining = Math.abs(timeInSeconds1 - timeInSeconds2) * 1000;
            }

        }
        return timeRemaining;
    }

    public static void setPosition(int index){
        position = index;
    }

    // find the string after the address
    private String getAddressFromUser(ArrayList<String> arrayList, String string){
        for (int i = 0; i< arrayList.size();i++){
            if (arrayList.get(i).contains(string)){
                int anonymousAndEmail = arrayList.get(i).lastIndexOf("!");
                return arrayList.get(i).substring(anonymousAndEmail);
            }
        }
        return string;
    }

    // find an address for a user
    private String getCityFromUser(ArrayList<String> arrayList, String string){
        String city = "1";
        for (int i = 0; i< arrayList.size();i++){
            if (arrayList.get(i).contains(string)){
                city = getCity(arrayList.get(i));
                return city;
            }
        }
        return city;
    }
    // find the user's city
    private String getCity(String address){
        int arrayLength = MapsActivity.cities.length;
        String city = "";
        for(int i = 0; i < arrayLength; i++){

            if (address.toLowerCase().contains(MapsActivity.cities[i].toLowerCase())){
                return MapsActivity.cities[i];
            }
        }
        return city;
    }

    // remove a listing and hide the feedback buttons
    private void removeListing(){
        databaseReference = database.getReference("item details");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userListing = Login.getListedUsers().get(position);
                String currentItemNumber1 = Login.getItemListingTimes().get(position);
                databaseReference.child(userListing).child(currentItemNumber1).removeValue();
                negative.setVisibility(View.INVISIBLE);
                positive.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        finished = true;
    }

    // find a random winner (the bidders are store as ;a ;b ;c and will return a, b or c at random)
    private String getWinner(String bidders, int numberOfBidders){
        Random rand = new Random();
        // Generate random integers in range 0 to 999
        int randomBidder = rand.nextInt(numberOfBidders);
        int counter = 0;
        while (counter <= randomBidder){

            if(randomBidder == counter){
                if(bidders.indexOf(";")<0){
                    bidders = bidders.substring(bidders.indexOf(";")+1);
                    bidders = bidders.substring(0,bidders.indexOf(";"));
                    counter++;
                }else{
                    bidders = bidders.substring(bidders.indexOf(";")+1);
                    counter++;
                }
            }else{
                bidders = bidders.substring(bidders.indexOf(";")+1);
                counter++;
            }

        }
        return bidders;
    }


}
