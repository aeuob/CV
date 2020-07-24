package com.example.donation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadToCheck extends AppCompatActivity {

    private final int PERMISSION_CODE1 = 23;
    private final int PERMISSION_CODE2 = 34;
    private final int PDF_CODE1 = 147;
    private final int PDF_CODE2 = 212;

    private View identificationButton;
    private View statementButton;
    private View uploadButton;
    private View alternativeFiles;

    private Uri URIForpdf1;
    private Uri URIForpdf2;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private String lastPDF = "";

    private static boolean filesUploaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_to_check);

        statementButton =  findViewById(R.id.statement_button);
        identificationButton = findViewById(R.id.identification_button);
        uploadButton = findViewById(R.id.upload_button);
        alternativeFiles =findViewById(R.id.NoRequiredDocuments);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        statementButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // check the file's permission
                if(ContextCompat.checkSelfPermission(UploadToCheck.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    displayMessage("Please select a statement");
                    uploadpdf1();
                }
                else {
                    // request permission
                    ActivityCompat.requestPermissions(UploadToCheck.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE1);
                }
            }
        });

        identificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check the file's permission
                if(ContextCompat.checkSelfPermission(UploadToCheck.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    uploadpdf2();
                }
                else {
                    // request permission
                    ActivityCompat.requestPermissions(UploadToCheck.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE2);
                }

            }
        });


        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (URIForpdf1 == null || URIForpdf2==null) {
                    displayMessage("Both files need to be selected");
                }
                else{
                    uploadPDFToDatabase(URIForpdf1);
                    uploadPDFToDatabase(URIForpdf2);
                    filesUploaded = true;
                    displayMessage("Files Successfully Uploaded");
                    Intent intent = new Intent(UploadToCheck.this, MainAppPage.class);
                    startActivity(intent);
                }

            }
        });

        alternativeFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filesUploaded == true){
                    displayMessage("This are not necessary");
                }else {
                    Intent intent = new Intent(UploadToCheck.this, AlternativeFiles.class);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // if the permission is granted, then upload the pdf
        if (requestCode == PERMISSION_CODE1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            uploadpdf1();
        }
        else if(requestCode == PERMISSION_CODE1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            uploadpdf2();
        }
        else{
            displayMessage("Permission was not granted");
        }

    }

    private void uploadpdf1() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PDF_CODE1);
    }

    private void uploadpdf2() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PDF_CODE2);
    }

    // check if a pdf has been selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PDF_CODE1 && resultCode == RESULT_OK && data!=null){

            URIForpdf1 = data.getData();
            displayMessage("Statement selected");
        }
        else if(requestCode == PDF_CODE2 && resultCode == RESULT_OK && data!=null && requestCode !=PDF_CODE1){

            URIForpdf2 = data.getData();
            displayMessage("Identity selected");
        }

    }

    private void uploadPDFToDatabase(Uri uri){

        if(uri != null){
            final ProgressDialog progressDialog = new ProgressDialog(UploadToCheck.this);
            progressDialog.setTitle("Progress");
            progressDialog.show();

            // alternate between the wage,tax statement and the ID 
            if(lastPDF.equals("") || lastPDF.equals("ID"))
                lastPDF = "Statement";
            else
                lastPDF = "ID";
            if(Login.returnEmail()!=null){
                // store in the database
            storageReference.child("pdfs").child(Login.returnEmail()).child(lastPDF).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    // loading bar
                    int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Please wait, the files are loading...");
                    if (currentProgress == 100) {
                        Intent intent = new Intent(UploadToCheck.this, MainAppPage.class);
                        startActivity(intent);
                    }
                }
            });

            }else{
                // store in the database
                storageReference.child("pdfs").child(SignUp.returnEmail()).child(lastPDF).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        //loading bar
                        int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Please wait, the files are loading...");
                        if (currentProgress == 100) {
                            Intent intent = new Intent(UploadToCheck.this, MainAppPage.class);
                            startActivity(intent);
                        }
                    }
                });
            }

        }

        else {
            displayMessage("A pdf has not been selected");
        }

    }

    private void displayMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static boolean hasUploaded(){
        return filesUploaded;
    }
}
