package in.ac.iitm.students.heritagetrails;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.ac.iitm.students.heritagetrails.IITMBusStops.bt_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.crc_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.fourth_cross_street_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.gajendra_circle_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.hsb_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.jam_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.kv_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.main_gate;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.Gurunath_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.tgh_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.vanvani_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.velachery_gate;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap mMap;
    private int trailCount = 0;
    private ClusterManager<ClusterMarkerLocation> mBusStopClusterManager = null;
    private ClusterManager<ClusterMarkerLocation> mTrailClusterManager = null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ArrayList<ClusterMarkerLocation> mTrailClusterMarkerArray = new ArrayList<>();
    private Marker mHeritageCenterMarker;
    private MarkerOptions mHeritageCenterMarkerOptions;
    private LocationRequest mLocationRequest;
    private AutoCompleteTextView searchField;
    private Polyline polyline;
    private PolylineOptions lineOptions;
    private Boolean isBusRouteShown = false, isDownloaded = false, isSearchBarShown = false, isSearchResultShown = false, isTrailShown = false, isTrail_2_Shown = false;
    private Toolbar myToolbar;
    private Menu myMenu;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<String> placesArray;
    private ProgressDialog progress, pDialog;
    private Context context;
    private float searchBarPosX, searchBarPosY;
    private KmlLayer layer = null;
    private String url;
    private ArrayList<Marker> searchResultMarkerArray = new ArrayList<>();
    private ArrayList<MarkerOptions> searchResultMarkerOptionsArray = new ArrayList<>();
    private LatLng heritage_center = new LatLng(12.9905663, 80.2322976);
    private ArrayList<MarkerOptions> trailMarkerOptionsArray = new ArrayList<>();
    private int optionalUpdateDialogCount = 0;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public int getTrailCount() {
        return trailCount;
    }

    @Override
    public void onResume() {
        super.onResume();

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            //Toast.makeText(this,version, Toast.LENGTH_SHORT).show();
            checkVersionMatch(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        context = this;
        pDialog = new ProgressDialog(context);
        progress = new ProgressDialog(context);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coord_layout);
        placesArray = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        getSuggestions();

        searchField = (AutoCompleteTextView) findViewById(R.id.auto_comp_tv_search);
        searchBarPosX = searchField.getX();
        searchBarPosY = searchField.getY();
        searchField.setVisibility(View.GONE);

        searchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                final String selection = (String) parent.getItemAtPosition(position);

                if (selection.length() > 2) {
                    pDialog.setMessage("Searching...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    searchField.setText("");
                    removeSearchResultMarkers();
                    searchField.setHint(selection);

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            doMySearch(selection);
                        }
                    };
                    thread.start();
                } else {
                    Toast.makeText(context, "Enter more than two characters", Toast.LENGTH_SHORT).show();
                    //Snackbar snackbar = Snackbar.make(coordinatorLayout, "Enter more than two characters", Snackbar.LENGTH_LONG);
                    //snackbar.show();
                }

            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                  @Override
                                                  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                                          final Editable selection = searchField.getText();

                                                          if (selection.toString().length() > 2) {
                                                              pDialog.setMessage("Searching...");
                                                              pDialog.setCancelable(false);
                                                              pDialog.show();
                                                              removeSearchResultMarkers();
                                                              searchField.setText("");
                                                              searchField.setHint(selection.toString());

                                                              Thread thread = new Thread() {
                                                                  @Override
                                                                  public void run() {
                                                                      doMySearch(selection.toString());
                                                                  }
                                                              };
                                                              thread.start();
                                                          } else {
                                                              Toast.makeText(context, "Enter more than two characters", Toast.LENGTH_SHORT).show();
                                                              //Snackbar snackbar = Snackbar.make(coordinatorLayout, "Enter more than two characters", Snackbar.LENGTH_LONG);
                                                              //snackbar.show();
                                                          }

                                                          return true;
                                                      }

                                                      return false;
                                                  }
                                              }

        );


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void checkVersionMatch(final String version) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_names.php
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("heritage")
                .appendPath("version_check.php");
        final String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsArray = new JSONArray(response);
                            JSONObject jsObject = jsArray.getJSONObject(0);
                            String ver = jsObject.getString("version");
                            FragmentManager fm = getSupportFragmentManager();
                            if (Integer.parseInt(ver) == 1) {
                                DialogFragment optionalUpdateDialogFragment = new OptionalUpdateDialogFragment();
                                optionalUpdateDialogFragment.setCancelable(false);
                                if (optionalUpdateDialogCount == 0) {
                                    optionalUpdateDialogFragment.show(fm, "OptionalUpdateDialogFragment");
                                    optionalUpdateDialogCount++;
                                }


                            } else if (Integer.parseInt(ver) == 2) {

                                DialogFragment forceUpdateDialogFragment = new ForceUpdateDialogFragment();
                                forceUpdateDialogFragment.setCancelable(false);
                                forceUpdateDialogFragment.show(fm, "ForceUpdateDialogFragment");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Internet Connection Failed.", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("version", version);
                return params;
            }
        };
// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void removeSearchResultMarkers() {
        for (Marker marker : searchResultMarkerArray) {
            marker.remove();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        myMenu = menu;
        // Associate searchable configuration with the SearchView

        return true;
    }

    public void doMySearch(String query) {


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_location.php?
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("heritage")
                .appendPath("get_location.php")
                .appendQueryParameter("locname", query);
        String url = builder.build().toString();

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    int i;
                    String locationName, locationDescription, latitude = "12.991780", longitude = "80.233772";
                    LatLng latLong;

                    if (searchResultMarkerArray != null) searchResultMarkerArray.clear();
                    if (searchResultMarkerOptionsArray != null)
                        searchResultMarkerOptionsArray.clear();

                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        locationName = jsonObject.getString("locname");
                        String location_url = jsonObject.getString("file_name");
                        String location_trail = jsonObject.getString("trail");
                        latitude = jsonObject.getString("lat");
                        longitude = jsonObject.getString("long");

                        latLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        MarkerOptions markerOption = new MarkerOptions()
                                .title(locationName)
                                .snippet("Click for more info...")
                                .position(latLong);
                        Marker currMarker = mMap.addMarker(markerOption);
                        currMarker.setTag(location_url);
                        if (jsonArray.length() == 1) {
                            currMarker.showInfoWindow();
                        }
                        isSearchResultShown = true;
                        searchResultMarkerOptionsArray.add(markerOption);
                        searchResultMarkerArray.add(currMarker);

                    }

                    if (pDialog.isShowing()) pDialog.dismiss();
                    LatLng latLngGC;
                    if (jsonArray.length() == 1) {
                        latLngGC = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngGC, 18));
                    } else {
                        latLngGC = new LatLng(12.991780, 80.233772);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngGC, 14));
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Showing all related results...", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (JSONException e) {

                    if (pDialog.isShowing()) pDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "No result found!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    e.printStackTrace();

                }

                hideKeyboard(MapsActivity.this);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (pDialog.isShowing()) pDialog.dismiss();
                VolleyLog.d("VolleyResponseError", error);
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Couldn't connect to the server.", Snackbar.LENGTH_LONG);
                snackbar.show();
                MenuItem search_item = myMenu.findItem(R.id.search_go_btn);
                search_item.setIcon(R.drawable.ic_search_deselected);
                hideKeyboard(MapsActivity.this);
                animateSearchOut();

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    private void getSuggestions() {


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_names.php
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("heritage")
                .appendPath("get_names.php");
        final String url = builder.build().toString();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {


                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    int i;
                    String locationName;
                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        locationName = jsonObject.getString("locname");
                        placesArray.add(locationName);
                    }
                    isDownloaded = true;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_dropdown_item_1line, placesArray);
                    searchField.setAdapter(adapter);
                } catch (JSONException e) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error getting data, try again later...", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //peace
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.intro_btn: {
                Intent i = new Intent(MapsActivity.this, WelcomeActivity.class);
                i.putExtra("who_called_me", "maps_activity");
                startActivity(i);
                // close this activity
                return true;
            }

            case R.id.help_btn: {
                Intent i = new Intent(MapsActivity.this, HelpActivity.class);
                startActivity(i);
                // close this activity
                return true;
            }

            case R.id.about_heritage: {
                Intent i = new Intent(MapsActivity.this, AboutHeritageActivity.class);
                startActivity(i);
                // close this activity
                return true;
            }

            case R.id.search_go_btn: {

                if (isSearchBarShown) {
                    animateSearchOut();
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setCompassEnabled(true);
                    removeSearchResultMarkers();
                    isSearchBarShown = false;
                    isSearchResultShown = false;
                    if (!isBusRouteShown) if (!mHeritageCenterMarker.isVisible())
                        mHeritageCenterMarker.setVisible(true);

                } else {
                    if (!isDownloaded) getSuggestions();
                    if (isTrailShown) {
                        destroyTrailClusterer();
                    }
                    animateSearchIn();
                    searchField.setHint(R.string.search_hint);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);
                    isSearchBarShown = true;
                }

                return true;
            }
            case R.id.trail_btn: {

                if (trailCount == 0) {
                    item.setIcon(R.drawable.ic_trail_selected);
                    trailCount = 1;
                    if (isBusRouteShown) {
                        destroyBusStopClusterer();
                    }
                    if (isSearchBarShown) {
                        if (isSearchResultShown) {
                            removeSearchResultMarkers();
                        }
                        MenuItem searchItem = myMenu.findItem(R.id.search_go_btn);
                        animateSearchOut();
                        searchItem.setIcon(R.drawable.ic_search_deselected);
                        isSearchResultShown = false;
                        isSearchBarShown = false;
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.getUiSettings().setCompassEnabled(true);

                    }
                    mMap.clear();
                    showCampusBoundary();
                    startTrail(trailCount);
                } else if (trailCount == 1) {
                    if (isTrailShown) {
                        trailCount = 2;
                        startTrail(trailCount);
                        item.setIcon(R.drawable.ic_trail_selected1);

                    }

                } else {
                    if (isTrail_2_Shown) {
                        destroyTrailClusterer();
                        if (!mHeritageCenterMarker.isVisible())
                            mHeritageCenterMarker.setVisible(true);
                    }
                }

                return true;
            }
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
            case R.id.bus_route:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                if (!isBusRouteShown) {

                    if (polyline != null) {
                        if (isTrailShown) {
                            destroyTrailClusterer();
                        }
                        mMap.addPolyline(lineOptions);
                        if (mHeritageCenterMarker.isVisible())
                            mHeritageCenterMarker.setVisible(false);
                        item.setIcon(R.drawable.ic_bus_selected);
                        setUpClusterer();

                    } else {
                        progress.setMessage("Getting bus route.");
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();

                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                        buildDirectionsUri();
                        if (isTrailShown) {
                            destroyTrailClusterer();

                            //Toast.makeText(context, "destroyed trail clusterer", Toast.LENGTH_SHORT).show();

                        }
                        setUpClusterer();
                        showBusRoute();
                        if (mHeritageCenterMarker.isVisible())
                            mHeritageCenterMarker.setVisible(false);

                    }
                    isBusRouteShown = true;

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(getResources().getDrawable(R.drawable.ic_bus_deselected, this.getTheme()));
                    }

                    polyline.setVisible(false);
                    item.setIcon(R.drawable.ic_bus_deselected);
                    isBusRouteShown = false;

                    destroyBusStopClusterer();
                    searchResultMarkerArray = new ArrayList<>();
                    if (isSearchResultShown) {
                        for (MarkerOptions markerOptions : searchResultMarkerOptionsArray) {
                            searchResultMarkerArray.add(mMap.addMarker(markerOptions));
                        }
                    }
                    if (!isSearchResultShown) if (!mHeritageCenterMarker.isVisible())
                        mHeritageCenterMarker.setVisible(true);

                }

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void setTrail_2_Shown(){
        isTrail_2_Shown=true;
    }

    private void destroyTrailClusterer() {
        mMap.clear();
        mMap.setOnCameraIdleListener(null);
        mMap.setOnMarkerClickListener(null);
        MenuItem item = myMenu.findItem(R.id.trail_btn);
        item.setIcon(R.drawable.ic_trail_deselected);
        isTrailShown = false;
        isTrail_2_Shown = false;
        trailCount = 0;
        showCampusBoundary();
        setInfoWindowClickListener();
    }

    private void destroyBusStopClusterer() {
        mMap.setOnCameraIdleListener(null);
        mMap.setOnMarkerClickListener(null);
        MenuItem item = myMenu.findItem(R.id.bus_route);
        item.setIcon(R.drawable.ic_bus_deselected);
        isBusRouteShown = false;
        mMap.clear();
        showCampusBoundary();
    }

    private void showCampusBoundary() {
        if (layer == null) {
            try {
                layer = new KmlLayer(mMap, R.raw.boundary, getApplicationContext());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            layer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Could not load boundary", Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Could not load boundary", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void buildDirectionsUri() {

        LatLng origin = main_gate;
        LatLng dest = jam_bus_stop;

        String str_origin = origin.latitude + "," + origin.longitude;

        String str_dest = dest.latitude + "," + dest.longitude;

        String waypoints = "12.991780,80.233772|12.990925,80.231896|12.990287,80.227627|12.987857,80.223127|12.989977,80.227707|12.988204,80.230125|12.986574,80.233254|12.988461,80.223328";

        // Sensor enabled
        String sensor_is = "false";

        //https://maps.googleapis.com/maps/api/directions/outputFormat?parameters
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("directions")
                .appendPath("json")
                .appendQueryParameter("origin", str_origin)
                .appendQueryParameter("destination", str_dest)
                .appendQueryParameter("waypoints", waypoints)
                .appendQueryParameter("sensor", sensor_is);

        // Building the url to the web service
        url = builder.build().toString();
    }

    private void startTrail(final Integer trailCounter) {

        trailMarkerOptionsArray = new ArrayList<>();
        mTrailClusterMarkerArray = new ArrayList<>();

        final String url = getString(R.string.trail_url) + trailCounter;
        // Request a string response from the provided URL.
        //Toast.makeText(MapsActivity.this,url, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            //Toast.makeText(MapsActivity.this,response, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject;
                            int i;
                            String locationName, latitude = "12.991780", longitude = "80.233772";
                            LatLng latLong;
                            for (i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                locationName = jsonObject.getString("locname");
                                String location_url = jsonObject.getString("file_name");
                                latitude = jsonObject.getString("lat");
                                longitude = jsonObject.getString("long");

                                latLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(locationName)
                                        .snippet("Click for more info...")
                                        .position(latLong);

                                trailMarkerOptionsArray.add(markerOptions);
                                mTrailClusterMarkerArray.add(new ClusterMarkerLocation(latLong, location_url));

                            }
                            setUpClusterer(trailMarkerOptionsArray);
                            if (pDialog.isShowing()) pDialog.dismiss();
                            LatLng latLngGC;
                            if (jsonArray.length() == 1) {
                                latLngGC = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngGC, 18));
                            } else {
                                LatLng latLngOAT = new LatLng(12.989281, 80.233585);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOAT, 16));
                            }

                            if (trailCount == 1) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Showing Trail (1/2)", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                isTrail_2_Shown = false;
                            } else if (trailCount == 2) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Showing Trail (2/2)", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                            isTrailShown = true;
                        } catch (JSONException e) {

                            if (pDialog.isShowing()) pDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "No result found!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //.makeText(MapsActivity.this,String.valueOf(e),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                isTrailShown = false;
                isTrail_2_Shown = false;
                trailCount = 0;
                MenuItem item = myMenu.findItem(R.id.trail_btn);
                item.setIcon(R.drawable.ic_trail_deselected);
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Internet Connection Failed", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

        int MY_SOCKET_TIMEOUT_MS = 10000;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void animateSearchOut() {
        if (!isSearchBarShown) return;
        searchField.animate()
                .translationX(searchBarPosX)
                .translationY(searchBarPosY)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        searchField.setVisibility(View.GONE);
                    }
                });
        MenuItem item = myMenu.findItem(R.id.search_go_btn);
        item.setIcon(R.drawable.ic_search_deselected);
        searchField.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    private void animateSearchIn() {
        if (isSearchBarShown) return;
        searchField.animate()
                .translationYBy(myToolbar.getHeight())
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        searchField.setVisibility(View.VISIBLE);
                    }
                });
        MenuItem item = myMenu.findItem(R.id.search_go_btn);
        item.setIcon(R.drawable.ic_search_selected);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(heritage_center, 15));

        mMap = googleMap;
        showCampusBoundary();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);
        }

        mHeritageCenterMarkerOptions = new MarkerOptions();
        mHeritageCenterMarkerOptions.position(heritage_center);
        mHeritageCenterMarkerOptions.title("Heritage Center");
        mHeritageCenterMarkerOptions.snippet("Click for more info...");
        mHeritageCenterMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mHeritageCenterMarker = mMap.addMarker(mHeritageCenterMarkerOptions);
        mHeritageCenterMarker.showInfoWindow();
        mHeritageCenterMarker.setTag("about.html");

        setInfoWindowClickListener();

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            mMap.setPadding(0, actionBarHeight, 0, 0);
        }

    }

    public void setInfoWindowClickListener() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String url_link = (String) marker.getTag();
                if (url_link != null) {
                    //Toast.makeText(MapsActivity.this,url_link,Toast.LENGTH_LONG).show();
                    Intent i = new Intent(MapsActivity.this, in.ac.iitm.students.heritagetrails.MySingleton.AboutActivity.class);
                    i.putExtra("url", url_link);
                    startActivity(i);
                }

            }
        });

    }

    protected void showBusRoute() {

        Request request = new JsonRequest<List<List<HashMap<String, String>>>>(Request.Method.GET, url, null, new Response.Listener<List<List<HashMap<String, String>>>>() {

            @Override
            public void onResponse(List<List<HashMap<String, String>>> response) {
                ArrayList<LatLng> points;
                lineOptions = null;

                // Traversing through all the routes
                if (response != null) {
                    for (int i = 0; i < response.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();
                        // Fetching i-th route
                        List<HashMap<String, String>> path = response.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(4);
                        lineOptions.color(ContextCompat.getColor(context, R.color.polyline_blue));


                    }
                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    polyline = mMap.addPolyline(lineOptions);
                    isBusRouteShown = true;
                    MenuItem item = myMenu.findItem(R.id.bus_route);
                    item.setIcon(R.drawable.ic_bus_selected);
                    if (progress.isShowing()) progress.dismiss();
                } else {
                    if (progress.isShowing()) {
                        isBusRouteShown = false;
                        progress.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Error getting route, try again later.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                // error.networkResponse.statusCode
                // error.networkResponse.data
                isBusRouteShown = false;

                MenuItem item = myMenu.findItem(R.id.bus_route);
                item.setIcon(R.drawable.ic_bus_deselected);
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Internet Connection Failed", Snackbar.LENGTH_LONG);
                snackbar.show();
                //Toast.makeText(MapsActivity.this, "Loading failed!", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Response<List<List<HashMap<String, String>>>> parseNetworkResponse(NetworkResponse response) {
                String jsonString = null;
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonString);
                    DataParser parser = new DataParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    if (progress.isShowing()) {
                        progress.dismiss();
                        isBusRouteShown = false;
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Error parsing data, try again later...", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    e.printStackTrace();
                }

                Response<List<List<HashMap<String, String>>>> result = Response.success(routes, HttpHeaderParser.parseCacheHeaders(response));
                return result;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(request);

        int MY_SOCKET_TIMEOUT_MS = 10000;
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(heritage_center));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Denying the permission to access your location will render some features of this app useless.", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            //Toast.makeText(context, "Permission already granted. yay", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                        //Toast.makeText(context, "Permission is granted: Location enabled", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.

                    //Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                    mMap.setMyLocationEnabled(false);
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    private void setUpClusterer() {

        if (mBusStopClusterManager == null) {
            // Initialize the manager with the context and the map.
            mBusStopClusterManager = new ClusterManager<>(this, mMap);

            // Add cluster items (markers) to the cluster manager.

            addBusStopMarkers();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gajendra_circle_bus_stop, 14));
        }
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mBusStopClusterManager);
        mMap.setOnMarkerClickListener(mBusStopClusterManager);
        mBusStopClusterManager.setRenderer(new BusStopIconRenderer(this, mMap, mBusStopClusterManager));

    }

    private void setUpClusterer(ArrayList<MarkerOptions> trailMarkerOptionsArray) {

        if (mTrailClusterManager == null) {
            // Initialize the manager with the context and the map.
            mTrailClusterManager = new ClusterManager<>(this, mMap);


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gajendra_circle_bus_stop, 15));
        }

        // Add cluster items (markers) to the cluster manager.

        mTrailClusterManager.clearItems();
        mTrailClusterManager.addItems(mTrailClusterMarkerArray);

        mMap.setOnInfoWindowClickListener(mTrailClusterManager);

        mTrailClusterManager
                .setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarkerLocation>() {
                    @Override
                    public void onClusterItemInfoWindowClick(ClusterMarkerLocation item) {
                        String url_link = item.getUrl();
                        //Toast.makeText(MapsActivity.this,url_link,Toast.LENGTH_LONG).show();
                        Intent i = new Intent(MapsActivity.this, in.ac.iitm.students.heritagetrails.MySingleton.AboutActivity.class);
                        i.putExtra("url", url_link);
                        startActivity(i);
                    }
                });

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mTrailClusterManager);
        mMap.setOnMarkerClickListener(mTrailClusterManager);

        mTrailClusterManager.setRenderer(new TrailIconRenderer(this, mMap, mTrailClusterManager, trailMarkerOptionsArray, this));

    }


    private void addBusStopMarkers() {

        ArrayList<ClusterMarkerLocation> items = new ArrayList<>();
        items.add(new ClusterMarkerLocation(main_gate));
        items.add(new ClusterMarkerLocation(gajendra_circle_bus_stop));
        items.add(new ClusterMarkerLocation(hsb_bus_stop));
        items.add(new ClusterMarkerLocation(bt_bus_stop));
        items.add(new ClusterMarkerLocation(velachery_gate));
        items.add(new ClusterMarkerLocation(crc_bus_stop));
        items.add(new ClusterMarkerLocation(tgh_bus_stop));
        items.add(new ClusterMarkerLocation(jam_bus_stop));
        items.add(new ClusterMarkerLocation(Gurunath_bus_stop));
        items.add(new ClusterMarkerLocation(fourth_cross_street_bus_stop));
        items.add(new ClusterMarkerLocation(kv_bus_stop));
        items.add(new ClusterMarkerLocation(vanvani_bus_stop));

        mBusStopClusterManager.addItems(items);

    }

}
