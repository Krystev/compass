package com.inveitix.android.compass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.inveitix.android.compass.constants.Constants;
import com.inveitix.android.compass.database.FirebaseHelper;
import com.inveitix.android.compass.database.adapters.LocationDbAdapter;
import com.inveitix.android.compass.database.models.LocationModel;

public class LocationReceiver extends BroadcastReceiver {

    public static final int DEF_VALUE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        float currentDegree = sharedPref.getFloat(Constants.SP_DEGREE, DEF_VALUE);
        LocationModel model = new LocationModel(currentDegree, System.currentTimeMillis());
        LocationDbAdapter dbAdapter = new LocationDbAdapter(context);
        dbAdapter.insert(model);
        FirebaseHelper.getInstance().addLocation(model, context);
    }
}