package razrsword.mapping;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import razrsword.main.R;

public class UMassMapActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Context context;
    private ProgressDialog pDialog;
    private Marker startMarker;
    MapView map;
    private int zoomLevel = 14;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_umass_map);
        OpenStreetMapTileProviderConstants.setUserAgentValue("Razrsword's UMass App V.05");
        map = (MapView) findViewById(R.id.map);
        GeoPoint viewPoint = new GeoPoint(42.38955, -72.52817);
        GeoPoint markerPoint;
        IMapController mapController = map.getController();

        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);

        if(savedInstanceState != null){
            viewPoint = new GeoPoint(savedInstanceState.getDouble("viewLat"),savedInstanceState.getDouble("viewLon"));
            zoomLevel = savedInstanceState.getInt("Zoom");
            if(savedInstanceState.getDouble("Latitude") != 0){
                markerPoint = new GeoPoint(savedInstanceState.getDouble("Latitude"),savedInstanceState.getDouble("Longitude"));
                startMarker = new Marker(map);
                startMarker.setPosition(markerPoint);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(startMarker);
            }
            mapController.setCenter(viewPoint);
            mapController.setZoom(zoomLevel);
        }else {
            mapController.setCenter(viewPoint);
            mapController.setZoom(zoomLevel);
        }

        final ImageButton button = (ImageButton) findViewById(R.id.hideKeyboard);
        final AutoCompleteTextView edittext = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        final List<Place> listOfPlace = getPlaces();


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });


        //Create Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, placesToNames(listOfPlace));
        //Find TextView control
        //Set the number of characters the user must type before the drop down list is shown
        edittext.setThreshold(1);
        //Set the adapter
        edittext.setAdapter(adapter);

        edittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                int listPosition = 0;
                for (int i = 0; i < listOfPlace.size(); i++) {
                    if (listOfPlace.get(i).getName().compareTo(edittext.getText().toString()) == 0) {
                        listPosition = i;
                        break;
                    }
                }
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                GeoPoint goalLocation = new GeoPoint(Double.valueOf(listOfPlace.get(listPosition).getLatitude()), Double.valueOf(listOfPlace.get(listPosition).getLongitude()));

                //Toast toast = Toast.makeText(UMassMapActivity.this,listOfPlace.get(listPosition).getLatitude() + " ", Toast.LENGTH_LONG);
                //toast.show();
                startMarker = new Marker(map);
                startMarker.setPosition(goalLocation);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                startMarker.setTitle(listOfPlace.get(listPosition).getName());
                //map.getController().setCenter(goalLocation);
                map.getOverlays().clear();
                map.getController().setZoom(16);
                map.getOverlays().add(startMarker);
                map.invalidate();
                map.getController().animateTo(goalLocation);

                Log.v("Map stuff", goalLocation.getLatitude() + " " + listOfPlace.get(listPosition).getLatitude());
                Log.v("Map stuff 2", goalLocation.getLongitude() + " " + listOfPlace.get(listPosition).getLongitude());
                Log.v("Map stuff 3", listPosition + " ");
            }
        });

        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {


                    Toast toast = Toast.makeText(UMassMapActivity.this, "fudge", Toast.LENGTH_LONG);
                    toast.show();
                    /*Marker startMarker = new Marker(map);
                    startMarker.setPosition(GeoPoint.fromIntString(edittext.getText().toString()));
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(startMarker);*/


                    return true;
                }
                return false;
            }
        });

    }

    public List<Place> getPlaces() {
        List<Place> places = null;
        XMLPullParseHandler parser = new XMLPullParseHandler();
        InputStream is = null;
        try {
            is = getAssets().open("locations.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is));
        places = parser.parse(is);
        return places;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        if(this.startMarker != null) {
            savedInstanceState.putDouble("Latitude", UMassMapActivity.this.startMarker.getPosition().getLatitude());
            savedInstanceState.putDouble("Longitude", UMassMapActivity.this.startMarker.getPosition().getLongitude());
        } else {
            savedInstanceState.putDouble("Latitude", 0);
            savedInstanceState.putDouble("Longitude", 0);
        }
        Log.v("onSaveInstanceState", " saving data between instances" + UMassMapActivity.this.map.getMapCenter().getLatitude() + " " + UMassMapActivity.this.map.getZoomLevel());
        savedInstanceState.putDouble("viewLat", UMassMapActivity.this.map.getMapCenter().getLatitude());
        savedInstanceState.putDouble("viewLon", UMassMapActivity.this.map.getMapCenter().getLongitude());
        savedInstanceState.putInt("Zoom", UMassMapActivity.this.map.getZoomLevel());
        // etc.
    }
    /*
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        double Latitude = savedInstanceState.getDouble("Latitude");
        double Longitude = savedInstanceState.getDouble("Longitude");
        double viewLat = savedInstanceState.getDouble("viewLat");
        double viewLon = savedInstanceState.getDouble("viewLon");
        int zoom = savedInstanceState.getInt("Zoom");
    }*/

    public List<String> placesToNames(List<Place> places) {
        List<String> placenames = new ArrayList<>();
        ;
        assert places != null;
        Iterator<Place> location = places.iterator();
        while (location.hasNext()) {
            placenames.add(location.next().getName());
        }
        return placenames;
    }


    public Context getContext() {
        return context;
    }


}