package com.example.donation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class AddPhotos extends AppCompatActivity {

    private final int PERMISSION_CODE1 = 43;
    private final int PERMISSION_CODE2 = 54;
    private final int IMG_CODE1 = 167;
    private final int IMG_CODE2 = 232;

    private View uploadImage1;
    private View uploadImage2;
    private View createButton;

    private Uri URIForImg1;
    private Uri URIForImg2;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private int lastImage;

    public static ItemDetails itemDetails;
    private ImageView noImage;

    private ArrayList<String> arrayList = new ArrayList<>();

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    public static int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);


        uploadImage1 =  findViewById(R.id.upload_img1_button);
        uploadImage2 = findViewById(R.id.upload_img2_button);
        createButton = findViewById(R.id.create_post_button);

        // Firebase real-time database variables
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("item details");

        // Firebase storage database variables
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        itemDetails = Description.getItemDetails();

        // upload the first image
        uploadImage1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // check the file's permission
                if(ContextCompat.checkSelfPermission(AddPhotos.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    uploadimg1();
                }
                else {
                    // request permission
                    ActivityCompat.requestPermissions(AddPhotos.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE1);
                }
            }
        });

        uploadImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check the file's permission
                if(ContextCompat.checkSelfPermission(AddPhotos.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    uploadimg2();
                }
                else {
                    // request permission
                    ActivityCompat.requestPermissions(AddPhotos.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE2);
                }

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // upload the image to the database
                if (URIForImg1!= null) {
                    uploadImageToDatabase(URIForImg1);
                }

                // upload the image to the database
                if(URIForImg2!=null) {
                    uploadImageToDatabase(URIForImg2);
                }

                // The Uri's for each image
                itemDetails.setPhoto1(URIForImg1);
                itemDetails.setPhoto2(URIForImg2);

                // refresh the page if only one image is uploaded
                if (URIForImg1==null || URIForImg2==null){
                    displayMessage("Please Upload Both images");
                    Intent intent = new Intent(AddPhotos.this, Login.class);
                    startActivity(intent);
                }else{
                Intent intent = new Intent(AddPhotos.this, MainAppPage.class);
                startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // if the permission is granted, then upload the pdf
        if (requestCode == PERMISSION_CODE1 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            uploadimg1();
        }
        else if(requestCode == PERMISSION_CODE1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            uploadimg2();
        }
        else{
            displayMessage("Permission was not granted");
        }

    }

    private void uploadimg1() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_CODE1);
    }

    private void uploadimg2() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_CODE2);
    }

    // check if a pdf has been selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == IMG_CODE1 && resultCode == RESULT_OK && data!=null){

            URIForImg1 = data.getData();
            displayMessage("image selected");
        }
        else if(requestCode == IMG_CODE2 && resultCode == RESULT_OK && data!=null && requestCode !=IMG_CODE1){

            URIForImg2 = data.getData();
            displayMessage("image selected");
        }

    }

    private void uploadImageToDatabase(Uri uri){

        if(uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(AddPhotos.this);
            progressDialog.setTitle("Progress");
            progressDialog.show();

            if (lastImage == 1) {
                lastImage = 2;
            } else {
                lastImage = 1;
            }
            if (Login.returnEmail() != null) {
                // upload the images with to the storage database
                storageReference.child("images").child(changeToDBFormat(Login.returnEmail())).child(String.valueOf(lastImage)).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        displayMessage("Images Successfully Uploaded");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        // let the user see how the loading process is happening 
                        int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Please wait, loading...");
                        if (currentProgress == 100) {
                            Intent intent = new Intent(AddPhotos.this, Login.class);
                            startActivity(intent);
                            if (URIForImg1==null || URIForImg2==null){
                                displayMessage("Please Upload Both images");

                            }
                        }
                    }
                });
            } else {
                storageReference.child("images").child(changeToDBFormat(SignUp.returnEmail())).child(String.valueOf(lastImage)).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        displayMessage("Images Successfully Uploaded");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        // let the user see how the loading process is happening
                        int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Please wait, loading...");
                        if (currentProgress == 100) {
                            Intent intent = new Intent(AddPhotos.this, Login.class);
                            startActivity(intent);
                            if (URIForImg1==null || URIForImg2==null){
                                displayMessage("Please Upload Both images");

                            }
                        }
                    }
                });
            }
        }


    }

    // the format which everything is uploaded as(because firebase does not accept @ and I wanted to use an email)
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

    // a toast method
    private void displayMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
