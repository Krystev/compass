package com.inveitix.android.compass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.inveitix.android.compass.database.FirebaseHelper;
import com.inveitix.android.compass.database.adapters.LocationDbAdapter;
import com.inveitix.android.compass.database.models.LocationModel;

public class LocationReceiver extends BroadcastReceiver {
float currentDegree;
    @Override
    public void onReceive(Context context, Intent intent) {
        currentDegree = intent.getFloatExtra("degree", 0);
        LocationModel model = new LocationModel(currentDegree, System.currentTimeMillis());
        LocationDbAdapter dbAdapter = new LocationDbAdapter(context);
        dbAdapter.insert(model);
        FirebaseHelper.getInstance().addLocation(model, context);
    }
}