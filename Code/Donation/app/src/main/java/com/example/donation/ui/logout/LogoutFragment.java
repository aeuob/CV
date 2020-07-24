package com.example.donation.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.donation.Login;
import com.example.donation.MainAppPage;
import com.example.donation.R;

public class LogoutFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        // go to the Login page
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);

    return root;
    }


}






