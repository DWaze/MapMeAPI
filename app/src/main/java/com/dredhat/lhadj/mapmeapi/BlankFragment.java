package com.dredhat.lhadj.mapmeapi;


import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.util.List;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    MapView mapView;
    TileCache tileCache;
    private List<LatLong> stepsR;
    final int MY_PERMISSIONS_REQUEST_ALL = 12;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    com.google.android.gms.location.LocationListener locationListener;
    TileRendererLayer mTileRendererLayer;
    String[] permissions;



    // Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                            .setFastestInterval(1 * 1000); // 1 second, in milliseconds

                    mGoogleApiClient.connect();

                    locationListener = new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            Drawable mDrawable = getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
                            Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(mDrawable);
                            bitmap.scaleTo(130,130);
                            Marker marker = new Marker(new LatLong(currentLatitude,currentLongitude),bitmap,0,-bitmap.getHeight()/2);
                            mapView.getLayerManager().getLayers().add(marker);
                            ExecuteRoot executeRoot = new ExecuteRoot();
                            LatLong source = new LatLong(currentLatitude,currentLongitude);
                            LatLong destination =new LatLong(36.2850684,6.6575333);
                            executeRoot.execute(source,destination);
                        }
                    };

                    File file = new File(Environment.getExternalStorageDirectory(), "constantine.map");
                    MapDataStore mapDataStore = new MapFile(file);
                    mTileRendererLayer = new TileRendererLayer(tileCache, mapDataStore, mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
                    mTileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
                    mapView.getLayerManager().getLayers().add(mTileRendererLayer);
                    mapView.setCenter(new LatLong(36.269349,6.6900963));
                    mapView.setZoomLevel((byte) 12);

                } else {
                    getActivity().finish();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        AndroidGraphicFactory.createInstance(this.getActivity().getApplication());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        mapView = view.findViewById(R.id.mapView);


        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setZoomLevelMin((byte) 10);
        mapView.setZoomLevelMax((byte) 20);

        this.tileCache = AndroidUtil.createTileCache(getContext(), "mapcache", mapView.getModel().displayModel.getTileSize(), 1f, this.mapView.getModel().frameBufferModel.getOverdrawFactor());



        //Checking & Asking Permissions
        if (checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
               checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
               checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions,
                    MY_PERMISSIONS_REQUEST_ALL);
        }else{
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds

            mGoogleApiClient.connect();

            locationListener = new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    Drawable mDrawable = getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
                    Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(mDrawable);
                    bitmap.scaleTo(130,130);
                    Marker marker = new Marker(new LatLong(currentLatitude,currentLongitude),bitmap,0,-bitmap.getHeight()/2);
                    mapView.getLayerManager().getLayers().add(marker);

                    
                    ExecuteRoot executeRoot = new ExecuteRoot();
                    LatLong source = new LatLong(currentLatitude,currentLongitude);
                    LatLong destination =new LatLong(36.2850684,6.6575333);


                    // L'itiniraire

                    executeRoot.execute(source,destination);
                }
            };
            mapView.setCenter(new LatLong(36.269349,6.6900963));
            mapView.setZoomLevel((byte) 12);
            File file = new File(Environment.getExternalStorageDirectory(), "constantine.map");
            MapDataStore mapDataStore = new MapFile(file);
            mTileRendererLayer = new TileRendererLayer(tileCache, mapDataStore, mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
            mTileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
            mapView.getLayerManager().getLayers().add(mTileRendererLayer);
        }



        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    //Litiniraire


    public class ExecuteRoot extends GetJsonRouteData{


        public void execute(LatLong source, LatLong destination){
            StartRun run = new StartRun();
            run.execute("https://maps.googleapis.com/maps/api/directions/json?origin="+ source.latitude+","+source.longitude+"&destination="+destination.latitude+","+destination.longitude+"&key=AIzaSyBdzsN0NOLELB76kscaXCUt-QNNFZSd0e0");
        }

        public class StartRun extends getData {
            @Override
            protected void onPostExecute(String Rinformation) {
                super.onPostExecute(Rinformation);
                stepsR =getSteps();
                Paint mPaint = AndroidGraphicFactory.INSTANCE.createPaint();
                mPaint.setColor(Color.RED);
                mPaint.setStrokeWidth(15);
                mPaint.setStyle(Style.STROKE);
                org.mapsforge.map.layer.overlay.Polyline polyline = new org.mapsforge.map.layer.overlay.Polyline(mPaint,AndroidGraphicFactory.INSTANCE);
                List<LatLong> coordinateList = polyline.getLatLongs();
                if(stepsR!=null){
                    for (int z = 0; z < stepsR.size(); z++) {
                        LatLong point = stepsR.get(z);
                        coordinateList.add(point);
                    }
                    mapView.getLayerManager().getLayers().add(polyline);
                }
            }
        }
    }
}
