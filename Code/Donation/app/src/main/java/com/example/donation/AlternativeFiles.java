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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AlternativeFiles extends AppCompatActivity {

    private final int PERMISSION_CODE = 54;
    private final int PDF_CODE = 832;

    private StorageReference storageReference;
    private FirebaseStorage storage;

    private View selectedAlternativeFile;
    private Button uploadAlternativeFilesButton;

    private Uri URIForpdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_files2);

        selectedAlternativeFile = findViewById(R.id.select_alt_file);
        uploadAlternativeFilesButton = findViewById(R.id.upload_alt_file);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        selectedAlternativeFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // check the file's permission
                if(ContextCompat.checkSelfPermission(AlternativeFiles.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    uploadpdf();
                    displayMessage("Please select a statement");

                }
                else {
                    // request permission
                    ActivityCompat.requestPermissions(AlternativeFiles.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
                }
            }
        });

        uploadAlternativeFilesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadPDFToDatabase(URIForpdf);
                Intent intent = new Intent(AlternativeFiles.this, MainAppPage.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // if the permission is granted, then upload the pdf
        if (requestCode == PERMISSION_CODE && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            uploadpdf();
        }
        else{
            displayMessage("Permission was not granted");
        }

    }

    private void uploadpdf() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PDF_CODE);
    }

    // check if a pdf has been selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PDF_CODE && resultCode == RESULT_OK && data!=null){
            URIForpdf = data.getData();
            displayMessage("File selected");
        }
    }

    private void uploadPDFToDatabase(Uri uri){

        if(uri != null){
            final ProgressDialog progressDialog = new ProgressDialog(AlternativeFiles.this);
            progressDialog.setTitle("Progress");
            progressDialog.show();

            if(Login.returnEmail()!=null){
            storageReference.child("pdfs").child(Login.returnEmail()).child("Homelessness document").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    displayMessage("File Successfully Uploaded");
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    // loading progress(to the database)
                    int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Please wait, the files are loading...");
                    if (currentProgress == 100) {
                        Intent intent = new Intent(AlternativeFiles.this, MainAppPage.class);
                        startActivity(intent);
                    }
                }
            });
        }
            else {
                storageReference.child("pdfs").child(SignUp.returnEmail()).child("Homelessness document").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        displayMessage("File Successfully Uploaded");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        // loading progress(to the database)
                        int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Please wait, the files are loading...");
                        if (currentProgress == 100) {
                            Intent intent = new Intent(AlternativeFiles.this, MainAppPage.class);
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
}
