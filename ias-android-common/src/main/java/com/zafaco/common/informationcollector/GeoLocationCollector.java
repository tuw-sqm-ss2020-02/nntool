/*!
    \file ListenerGeoLocation.java
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.zafaco.common.informationcollector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zafaco.common.Tool;
import com.zafaco.common.interfaces.ModulesInterface;

import org.json.JSONObject;

/**
 * Class ListenerGeoLocation
 */
public class GeoLocationCollector {
    /**************************** Variables ****************************/

    private Context ctx;

    //Module Objects
    private Tool tool;
    private ModulesInterface interfaceCallback;
    private JSONObject jData;

    private FusedLocationProviderClient fusedLocationClient = null;

    private boolean mtGeoService = false;

    private int minTime;
    private int minDistance;
    private int minPrio;

    private Location lastLocation;

    /*******************************************************************/
    /**
     * Handler mLocationCallback
     */
    private LocationCallback locationCallback = new LocationCallback() {
        /**
         * Method onLocationResult
         * @param locationResult
         */
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // do work here
            getLocation(locationResult.getLastLocation());
        }
    };

    /**
     * Method ListenerGeoLocation
     *
     * @param ctx
     * @param intCall
     */
    public GeoLocationCollector(Context ctx, ModulesInterface intCall) {
        this.ctx = ctx;

        this.interfaceCallback = intCall;

        tool = new Tool();

        jData = new JSONObject();
    }

    /**
     * Method setGeoService
     */
    public void setGeoService() {
        mtGeoService = true;
    }

    /**
     * Method getPosition
     *
     * @param min_time
     * @param min_distance
     * @param min_prio
     */
    public void getPosition(int min_time, int min_distance, int min_prio) {
        this.minTime = min_time;
        this.minDistance = min_distance;
        this.minPrio = min_prio;

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

        // Create the location request to start receiving updates
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(this.minPrio);
        locationRequest.setInterval(this.minTime);
        locationRequest.setFastestInterval(this.minTime);
        locationRequest.setSmallestDisplacement(this.minDistance);

        getLastLocation(fusedLocationClient);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    /**
     * Method stopUpdates
     */
    public void stopUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Method getLastLocation
     *
     * @param mFusedLocationClient
     */
    private void getLastLocation(FusedLocationProviderClient mFusedLocationClient) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    /**
                     * Method onSuccess
                     * @param lastKnownLocation
                     */
                    @Override
                    public void onSuccess(Location lastKnownLocation) {
                        // GPS location can be null if GPS is switched off
                        if (lastKnownLocation != null) {
                            //onLocationChanged(location);
                            try {
                                jData = new JSONObject();

                                jData.put("app_latitude", lastKnownLocation.getLatitude());
                                jData.put("app_longitude", lastKnownLocation.getLongitude());
                                jData.put("app_altitude", lastKnownLocation.getAltitude());
                                jData.put("app_velocity", lastKnownLocation.getSpeed());
                                jData.put("app_accuracy", 100.0);

                                //Callback
                                interfaceCallback.receiveData(jData);
                            } catch (Exception ex) {
                                tool.printTrace(ex);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    /**
                     * Method onFailure
                     * @param e
                     */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    /**
     * Method getLocation
     *
     * @param location
     */
    private void getLocation(Location location) {
        double newAccuracy = location.getAccuracy();

        if (
                (
                        //If we are NOT in TestCase "Coverage", set Location only if Accuracy is smaller (better) then value before
                        newAccuracy != 0.0 &&
                                newAccuracy <= (lastLocation == null ? 0.0 : lastLocation.getAccuracy())
                ) ||
                        (
                                //If TestCase "Coverage" request normal Location updates
                                mtGeoService
                        )
        ) {
            try {
                jData = new JSONObject();

                jData.put("app_latitude", location.getLatitude());
                jData.put("app_longitude", location.getLongitude());
                jData.put("app_altitude", location.getAltitude());
                jData.put("app_velocity", location.getSpeed());
                jData.put("app_accuracy", newAccuracy);

                //Callback
                interfaceCallback.receiveData(jData);
            } catch (Exception ex) {
                tool.printTrace(ex);
            }

            lastLocation = location;
        }
    }
}
