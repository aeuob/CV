package com.example.donation.ui.verification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.UpdateLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.donation.Login;
import com.example.donation.MainAppPage;
import com.example.donation.R;
import com.example.donation.UploadToCheck;

public class VerificationFragment extends Fragment implements View.OnClickListener{

    private VerificationViewModel verificationViewModel ;
    private Button verificationButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        verificationViewModel = ViewModelProviders.of(this).get(VerificationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_verification, container, false);

        verificationButton = root.findViewById(R.id.verification_button);

        // if the user has not submitted other documents
        if (!UploadToCheck.hasUploaded() || !Login.returnVerified().equals("true")){
            verificationButton.setOnClickListener(this);
        }else {
            displayMessage("You have already submitted");
        }

        return root;
    }

    @Override
    public void onClick(View v) {
        // redirect to Upload to check
        Intent i = new Intent(getActivity(), UploadToCheck.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);

    }

    private void displayMessage(CharSequence text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT );

    }

}