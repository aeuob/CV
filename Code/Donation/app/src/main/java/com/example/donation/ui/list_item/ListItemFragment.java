package com.example.donation.ui.list_item;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.donation.Description;
import com.example.donation.ItemDetails;
import com.example.donation.Login;
import com.example.donation.MainAppPage;
import com.example.donation.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListItemFragment extends Fragment {

    private ImageView clothes;
    private ImageView electronics;
    private ImageView food;
    private ImageView toys;
    private ImageView other;
    private Intent intent;
    public static ItemDetails itemDetails;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_list_item, container, false);

        clothes = root.findViewById(R.id.clothes_button);
        electronics = root.findViewById(R.id.electronics_button);
        food = root.findViewById(R.id.food_button);
        toys = root.findViewById(R.id.toys_button);
        other = root.findViewById(R.id.other_button);

        clothes.setImageResource(R.drawable.clothes);
        electronics.setImageResource(R.drawable.electronics);
        food.setImageResource(R.drawable.food);
        toys.setImageResource(R.drawable.toys);
        other.setImageResource(R.drawable.other);
        
        itemDetails = new ItemDetails();

        intent = new Intent(getActivity(), Description.class);

        clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetails.setClothes(true);
                itemDetails.setElectronics(false);
                itemDetails.setFood(false);
                itemDetails.setToys(false);
                itemDetails.setOther(false);

                startActivity(intent);
            }
        });

        electronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetails.setClothes(false);
                itemDetails.setElectronics(true);
                itemDetails.setFood(false);
                itemDetails.setToys(false);
                itemDetails.setOther(false);

                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetails.setClothes(false);
                itemDetails.setElectronics(false);
                itemDetails.setFood(true);
                itemDetails.setToys(false);
                itemDetails.setOther(false);

                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        toys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetails.setClothes(false);
                itemDetails.setElectronics(false);
                itemDetails.setFood(false);
                itemDetails.setToys(true);
                itemDetails.setOther(false);

                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetails.setClothes(false);
                itemDetails.setElectronics(false);
                itemDetails.setFood(false);
                itemDetails.setToys(false);
                itemDetails.setOther(true);

                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });


        root.setFocusableInTouchMode(true);
        root.requestFocus();
        // if the back button is pressed, redirect the user to the MainAppPage
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    intent = new Intent(getActivity(), MainAppPage.class);
                    startActivity(intent);
                    (getActivity()).overridePendingTransition(0, 0);
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    public static ItemDetails getItemDetails(){
        return itemDetails;
    }


}
