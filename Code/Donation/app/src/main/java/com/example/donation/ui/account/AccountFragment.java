package com.example.donation.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.donation.Login;
import com.example.donation.Member;
import com.example.donation.R;
import com.example.donation.SignUp;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private TextView accountInformation;
    private TextView changedDetail;
    private Button changeButton;
    private Member member;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_account, container, false);

        accountInformation = root.findViewById(R.id.account_information);
        changedDetail = root.findViewById(R.id.detail_changed);
        changeButton = root.findViewById(R.id.change_button);

        Log.d("444441", Login.getDetails().getEmail() + " " + Login.getDetails().getPassword());
        Log.d("444441", Login.currentMember.getEmail() + " " + Login.currentMember.getPassword());
        Log.d("444441",String.valueOf(Login.finishedForAccountDetails));
         if(Login.finishedForAccountDetails == true){
            member = Login.getDetails();
        }

        // The string that will be displayed
        String string = "First name: " + Login.firstName +
                "\nSecond name: " + Login.secondName +
                "\nAddress: " +Login.address +
                "\nEmail: " + Login.emailUser +
                "\nAnonymous: " + Login.anonymous +
                "\nVerified: " + Login.verifiedUser+ "\n" ;

        accountInformation.setText(string);

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "The request will be done in 24h", Toast.LENGTH_SHORT);
            }
        });
    return root;
    }
}






