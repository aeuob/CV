package com.example.donation.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.donation.IndividualListView;
import com.example.donation.ItemListing;
import com.example.donation.Login;
import com.example.donation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ListView listView;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private int i;
    private ArrayList<String> imagesFromDatabase;
    private int counter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        listView = root.findViewById(R.id.list_view);

        // the list of users that have listings
        final ArrayList<String> user = Login.getListedUsers();
        // the list of descriptions
        final ArrayList<String> listedDescriptions = Login.getListedDescriptions();

        imagesFromDatabase = new ArrayList<>();
        counter = 0;

		// covert the array list into an array
        final String[] descriptions = new String[listedDescriptions.size() + 1];
        for (int i = 0; i < listedDescriptions.size(); i++) {
            descriptions[i] = listedDescriptions.get(i);

        }

        storage = FirebaseStorage.getInstance();

        // for every user do the following
        for (i = 0; i < user.size(); i++) {
        	// the path to the users images from the database
            String location = "images/" + user.get(i) + "/1";
            // the storage database
            storageReference = storage.getReference(location);
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
            	// the image path from the database 
                String profileImageUrl = task.getResult().toString();
                imagesFromDatabase.add(profileImageUrl);
                counter++;
                // finish this method when all the users have been stored in the database
                if(counter == user.size()){
                    afterCompletedTasks(imagesFromDatabase, descriptions);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error: ", e.getMessage());
            }
        });

        }

        return root;
    }

private void afterCompletedTasks(ArrayList<String> imagesFromDatabase, String[] descriptions){
    final String[] imagesFromDB = new String[imagesFromDatabase.size() + 1];
    for (int i = imagesFromDatabase.size()-1; i >= 0; i--) {
        imagesFromDB[i] = imagesFromDatabase.get(i);
    }


    IndividualListView individualListView = new IndividualListView(getContext(), descriptions, imagesFromDB);
    listView.setAdapter(individualListView);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	//current position
            ItemListing.setPosition(position);
            Intent intent = new Intent(getActivity(), ItemListing.class);
            startActivity(intent);
        }
    });

}
}
