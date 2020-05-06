package com.example.relevent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements DataAdapter.OnNoteListener {

    private ProgressDialog mProgressDialog;
    private String url = "https://www.google.com/search?q=";
    private ArrayList<String> venueList = new ArrayList<>();
    private ArrayList<String> dateList = new ArrayList<>();
    private ArrayList<String> eventList = new ArrayList<>();

    String city = "";
    String city2;
    String encoding = "UTF-8";

    org.jsoup.nodes.Document html;

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            city = extras.getString("city");
        }

        Button btn = (Button) findViewById(R.id.search);
        Button bt2 = (Button) findViewById(R.id.button2);
        final EditText et = (EditText) findViewById(R.id.editText2);

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("city", city2);
                startActivity(intent);
            }
        });


        requestPermission();

        FusedLocationProviderClient client;


        client = LocationServices.getFusedLocationProviderClient(this);

        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());

                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude,1);
                        if (city == "") {
                            city = addresses.get(0).getLocality();
                        }
                        city2 = addresses.get(0).getLocality();
                        new Scraper().execute();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = et.getText().toString();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onNoteClick(int position) {
        eventList.get(position);
        Elements eLink = html.select("div[class=o3NmVc]").select("a").eq(position);
        String url3 = eLink.attr("href");
        Intent intent = new Intent(MainActivity.this, NewActivity.class);
        intent.putExtra("url", url3);
        startActivity(intent);

    }

    private class Scraper extends AsyncTask<Void, Void, Void> implements DataAdapter.OnNoteListener {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("RelEvenT");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String searchText = "events in and around " + city;
                url = url + URLEncoder.encode(searchText, encoding);
                html = Jsoup.connect(url).get();
                Elements BlogDocument = html.select("div[class=BXE0fe]").select("a[class=c2hLV]");
                String url2 = BlogDocument.attr("href");
                html = Jsoup.connect(url2).get();

                int mPaginationSize = html.select("div[class=YOGjf]").size();

                for (int i = 0; i < mPaginationSize; i++) {
                    Elements eEvent = html.select("div[class=YOGjf]").eq(i);
                    String eventName = eEvent.text().trim().replace("\n", "").replace("\t", "").replace("\r", "").replace("\b", "");

                    Elements eDate = html.select("div[class=cEZxRc]").eq(i);
                    String date = eDate.text();

                    Elements eVenue = html.select("div[class=cEZxRc zvDXNd]").eq(i);
                    String venue = eVenue.text();

                    venueList.add("Venue : "+venue);
                    dateList.add("Date : "+date);
                    eventList.add("Event : "+eventName);
                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "NO EVENTS", Toast.LENGTH_LONG).show();

                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.act_recyclerview);

            DataAdapter mDataAdapter = new DataAdapter(MainActivity.this, eventList, venueList, dateList, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);

            mProgressDialog.dismiss();
        }

        @Override
        public void onNoteClick(int position) {
            eventList.get(position);
            Elements eLink = html.select("div[class=o3NmVc]").select("a").eq(position);
            String url3 = eLink.attr("href");
            Intent intent = new Intent(MainActivity.this, NewActivity.class);
            intent.putExtra("url", url3);
            startActivity(intent);

        }
    }
}
