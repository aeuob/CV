package com.example.donation;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static String[] cities = {"Bath","Birmingham","Bradford","Brighton","Bristol","Cambridge","Canterbury","Carlisle","Chester","Chichester","Coventry","Derby"
            ,"Durham","Ely","Exeter","Gloucester","Hereford","Hull","Lancaster","Leeds","Leicester","Lichfield","Lincoln"
            ,"Liverpool","London","Manchester","Newcastle","Norwich","Nottingham","Oxford","Peterborough","Plymouth","Portsmouth","Preston","Ripon","Salford"
            ,"Salisbury","Sheffield","Southampton","St Albans","Stoke","Sunderland","Truro","Wakefield","Wells","Westminster","Winchester","Wolverhampton","Worcester"
            ,"York","Bangor","Cardiff","Newport","St Davids","Swansea","Aberdeen","Dundee","Edinburgh","Glasgow","Inverness","Stirling","Armagh","Belfast","Londonderry","Lisburn","Newry"};

    private double[] zones = {
            49.589421,-7.97776969, 49.589421, -2.0451467,
            49.589421, -2.0451467,49.589421, 2.231467,

            52.089421,-7.97776969, 52.089421, -2.0451467,
            52.089421, -2.0451467, 52.089421, 2.231467,

            53.589421,-7.97776969, 53.589421, -2.0451467,
            53.589421, -2.0451467, 53.589421, 2.231467,

            55.089421, -7.97776969, 55.089421, -2.0451467,
            55.089421, -2.0451467, 55.089421,2.231467,

            56.589421,-7.97776969,56.589421,-4.224687,
            56.589421, -4.224687, 56.589421, 2.231467,

            58.089421,-7.97776969,58.089421, -4.224687,
            58.089421, -4.224687,58.089421, 2.231467,

            59.589421, -7.97776969,59.589421, -4.224687,
            59.589421, -4.224687, 59.589421, 2.231467,
    };

    private ArrayList<LatLng> cityCoordinates = new ArrayList<>();
    private ArrayList<Integer> clothes = new ArrayList<>();
    private ArrayList<Integer> electronics = new ArrayList<>();
    private ArrayList<Integer> food = new ArrayList<>();
    private ArrayList<Integer> toys = new ArrayList<>();
    private ArrayList<Integer> other = new ArrayList<>();
    private Member member;
    public String items;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ArrayList<String> categories = new ArrayList<>(Login.getCategories());
        ArrayList<String> addresses = new ArrayList<>(Login.getAddresses());
        String items = new String();

        for(int i = 0;i< categories.size();i++) {

            int cityPosition = getCityPosition(addresses.get(i).toLowerCase());

            if (cityPosition < 66) {
                String category = categories.get(i).substring(categories.get(i).length() - 1);
                if (category.equals("1")) {
                    clothes.add(cityPosition);
                } else if (category.equals("2")) {
                    electronics.add(cityPosition);
                } else if (category.equals("3")) {
                    food.add(cityPosition);
                } else if (category.equals("4")) {
                    toys.add(cityPosition);
                } else {
                    other.add(cityPosition);
                }
            }
        }

        int numberOfZones = 15;
        //double median = zone.size() / numberOfZones;
        int i;

        //match the coordinates to the city array
        cityCoordinates.add(new LatLng(51.380001,-2.360000));
        cityCoordinates.add(new LatLng(52.4862, -1.9336703));
        cityCoordinates.add(new LatLng(53.799999,	-1.750000));
        cityCoordinates.add(new LatLng(50.827778,	-0.152778));
        cityCoordinates.add(new LatLng(51.454514,-2.587910));
        cityCoordinates.add(new LatLng(52.205276,	0.119167));
        cityCoordinates.add(new LatLng(51.279999,	1.080000));
        cityCoordinates.add(new LatLng(54.890999,	-2.944000));
        cityCoordinates.add(new LatLng(53.189999,	-2.890000));
        cityCoordinates.add(new LatLng(50.836498,	-0.779200));
        //coventry
        cityCoordinates.add(new LatLng(52.408054,	-1.510556));
        cityCoordinates.add(new LatLng(52.916668,	-1.466667));
        cityCoordinates.add(new LatLng(54.776100,	-1.573300));
        cityCoordinates.add(new LatLng(52.398056,	0.262222));
        cityCoordinates.add(new LatLng(50.716667,	-3.533333));
        cityCoordinates.add(new LatLng(51.864445,	-2.244444));
        cityCoordinates.add(new LatLng(52.056499,	-2.716000));
        cityCoordinates.add(new LatLng(53.747372,	-0.338653));
        cityCoordinates.add(new LatLng(54.047001,	-2.801000));
        //leeds
        cityCoordinates.add(new LatLng(53.801277,	-1.548567));
        cityCoordinates.add(new LatLng(52.633331,	-1.133333));
        cityCoordinates.add(new LatLng(52.683498,	-1.826530));
        cityCoordinates.add(new LatLng(53.234444,	-0.538611));
        cityCoordinates.add(new LatLng(53.400002,	-2.983333));
        cityCoordinates.add(new LatLng(51.509865,	-0.118092));
        cityCoordinates.add(new LatLng(	53.483959,	-2.244644));
        cityCoordinates.add(new LatLng(54.966667,	-1.600000));
        cityCoordinates.add(new LatLng(52.6309, 1.2974));
        cityCoordinates.add(new LatLng(52.950001,	-1.150000));
        cityCoordinates.add(new LatLng(51.752022,	-1.257677));
        cityCoordinates.add(new LatLng(52.573921,	-0.250830));
        cityCoordinates.add(new LatLng(50.376289,	-4.143841));
        cityCoordinates.add(new LatLng(50.805832,	-1.087222));
        cityCoordinates.add(new LatLng(53.765762,	-2.692337));
        cityCoordinates.add(new LatLng(54.138000,	-1.524000));
        cityCoordinates.add(new LatLng(53.483002,	-2.293100));
        cityCoordinates.add(new LatLng(51.068787,	-1.794472));
        cityCoordinates.add(new LatLng(53.3811, 1.4701));
        cityCoordinates.add(new LatLng(50.909698,	-1.404351));
        cityCoordinates.add(new LatLng(51.7527, 0.3394));
        cityCoordinates.add(new LatLng(53.002666,	-2.179404));
        cityCoordinates.add(new LatLng(54.906101,	-1.381130));
        cityCoordinates.add(new LatLng(50.259998,	-5.051000));
        //wakefield
        cityCoordinates.add(new LatLng(53.680000,	-1.490000));
        cityCoordinates.add(new LatLng(51.209000,	-2.647000));
        cityCoordinates.add(new LatLng(51.4975, 0.1357));
        cityCoordinates.add(new LatLng(51.063202,	-1.308000));
        cityCoordinates.add(new LatLng(52.591370,	-2.110748));
        cityCoordinates.add(new LatLng(52.192001,	-2.220000));
        cityCoordinates.add(new LatLng(53.958332,	-1.080278));
        cityCoordinates.add(new LatLng(53.2274, 4.1293));
        cityCoordinates.add(new LatLng(51.481583,	-3.179090));
        cityCoordinates.add(new LatLng(51.5842, 2.9977));
        cityCoordinates.add(new LatLng(51.8812, 5.2660));
        cityCoordinates.add(new LatLng(51.6214, 3.9436));
        cityCoordinates.add(new LatLng(57.149651,	-2.099075));
        cityCoordinates.add(new LatLng(56.462002,	-2.970700));
        cityCoordinates.add(new LatLng(	55.953251,	-3.188267));
        cityCoordinates.add(new LatLng(55.860916,	-4.251433));
        cityCoordinates.add(new LatLng(57.477772,	-4.224721));
        cityCoordinates.add(new LatLng(56.1165, 3.9369));
        cityCoordinates.add(new LatLng(54.3503, 6.6528));
        cityCoordinates.add(new LatLng(54.607868,	-5.926437));
        cityCoordinates.add(new LatLng(55.006763,	-7.318268));
        cityCoordinates.add(new LatLng(54.509720,	-6.037400));
        cityCoordinates.add(new LatLng(54.175999,	-6.349000));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        /*categories
         *1 = clothes
         * 2= electronics
         * 3 = food
         * 4 = toys
         * 5 = other
         *  */

        //list clothes
        for(int i = 0; i<clothes.size();i++){
            LatLng position = cityCoordinates.get(clothes.get(i));
            Marker clothes = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
        }

        //list electronics
        for(int i = 0; i<electronics.size();i++){
            LatLng position = cityCoordinates.get(electronics.get(i));
            Marker electronics = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker)));
        }

        //list food
        for(int i = 0; i<food.size();i++){
            LatLng position = cityCoordinates.get(food.get(i));
            Marker food = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_marker)));
        }

        //list toys
        for(int i = 0; i<toys.size();i++){
            LatLng position = cityCoordinates.get(toys.get(i));
            Marker toys = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
        }

        //list other
        for(int i = 0; i<other.size();i++){
            LatLng position = cityCoordinates.get(other.get(i));
            Marker other = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.black_marker)));
        }

        items = "";
        Log.i("sssssz", clothes.toString());
        visualiseOnMap(clothes, "clothes");
        visualiseOnMap(electronics, "electronics");
        visualiseOnMap(food, "food");
        visualiseOnMap(toys, "toys");
        visualiseOnMap(other, "other");

        mMap.moveCamera(CameraUpdateFactory.newLatLng(cityCoordinates.get(1)));
    }

    private int getCityPosition(String string){
        String city = "";
        int i=0;
        while(i<cities.length){
            if (string.contains(cities[i].toLowerCase())) {
                break;
            }
            i++;
        }

        return i;
    }

    public void visualiseOnMap(ArrayList<Integer> arrayList, String message){

        int[] counterZone = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i =0; i<arrayList.size();i++) {
            double a = cityCoordinates.get(arrayList.get(i)).latitude;
            double b = cityCoordinates.get(arrayList.get(i)).longitude;

            //zones without Northern Ireland
            if(arrayList.get(i) < cityCoordinates.size()-5) {

                for (int j = 0; j < 7; j++) {
                    Log.i("ssssszz1", String.valueOf(a) + ":" + String.valueOf(zones[j*4]) + "  "+String.valueOf(zones[j*4+8]) );
                    Log.i("ssssszz1", String.valueOf(b) + ":" + String.valueOf(zones[j*4+1]) + "  "+String.valueOf(zones[j*4+3]) );
                    Log.i("ssssszz1", String.valueOf(a) + ":" + String.valueOf(zones[j*4]) + "  "+String.valueOf(zones[j*4+8]) );
                    Log.i("ssssszz1", String.valueOf(b) + ":" + String.valueOf(zones[j*4+5]) + "  "+String.valueOf(zones[j*4+7]) );
                    Log.i("ssssszz1", "--------");
                    if (a >= zones[j * 4] && a <= zones[j * 4 + 8] && b >= zones[j * 4 + 1] && b <= zones[j * 4 + 3]) {
                        counterZone[j]++;
                    } else if((a >= zones[j * 4] && a <= zones[j * 4 + 8] && b >= zones[j * 4 + 5] && b <= zones[j * 4 + 7])){
                        counterZone[j+1]++;
                    }

                }
                //Northern Ireland
            } else{
                counterZone[14]++;
            }

        }



        for(int i = 0;i<counterZone.length;i++){
            // There are 15 zones
            Log.i("zzzzz", String.valueOf(counterZone[i]));
            if (counterZone[i] < (float) (arrayList.size()/15)){

                items.concat(message + ",");
            }

        }
        
        Log.i("zzzzz1", items);
        //if(counterZone1<median){
        addNotification(items);
        //}
    }

    // Creates and displays a notification
    private void addNotification(String string) {
        // Builds your notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.donation)
                .setContentTitle("Donation App")
                .setContentText(string);

        // Creates the intent needed to show the notification
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
