package com.star.cityfinder;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final int CITY_ZOOM_IN = 4;

    private GoogleMap mGoogleMap;
    private LatLng mMyLocation;

    private Spinner mCitySpinner;
    private Button mClearButton;

    private List<City> mCities;
    private List<String> mCityNames;

    private PolylineOptions mPolylineOptions;
    private List<Polyline> mPolylines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCitySpinner = (Spinner) findViewById(R.id.city_spinner);
        mCities = new ArrayList<>();
        mCityNames = new ArrayList<>();

        mPolylineOptions = new PolylineOptions();
        mPolylines = new ArrayList<>();

        mClearButton = (Button) findViewById(R.id.clear_button);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        readCities();
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                                MainActivity.this,
                                android.R.layout.simple_spinner_item,
                                mCityNames
                        );
                        mCitySpinner.setAdapter(cityAdapter);
                        mCitySpinner.setSelection(0, true);
                        mCitySpinner.setPrompt(getResources().getString(R.string.spinner_prompt));
                        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mGoogleMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mCities.get(position).getLatLng(),
                                                CITY_ZOOM_IN));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                LatLng markerLatLng = marker.getPosition();
                                mPolylines.add(mGoogleMap.addPolyline(mPolylineOptions
                                        .add(markerLatLng)));
                                return true;
                            }
                        });

                        mClearButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (Polyline polyline : mPolylines) {
                                    polyline.remove();
                                }
                                mPolylines.clear();
                                mPolylineOptions = new PolylineOptions();
                            }
                        });

                        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(latLng.latitude + " " + latLng.longitude));
                                marker.showInfoWindow();
                            }
                        });

                        mMyLocation = getMyLocation();

                        if (mMyLocation == null) {
                            Toast.makeText(MainActivity.this,
                                    "Unable to access your location. " +
                                            "Consider enabling Location in your device's Settings.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(mMyLocation)
                                    .title("ME!"));
                        }
                    }
                });
            }
        });
    }

    public LatLng getMyLocation() {

        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            return new LatLng(latitude, longitude);
        }

        return null;
    }

    private void readCities() {

        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.cities));

        while (scanner.hasNext()) {
            String name = scanner.nextLine();

            if (TextUtils.isEmpty(name)) {
                break;
            }

            double latitude = Double.parseDouble(scanner.nextLine());
            double longitude = Double.parseDouble(scanner.nextLine());

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(name));

            City city = new City(name, latitude, longitude);

            mCities.add(city);
            mCityNames.add(name);
        }
    }
}
