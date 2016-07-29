package com.inveitix.android.compass.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.inveitix.android.compass.AnimationUtils;
import com.inveitix.android.compass.LocationCalculationHelper;
import com.inveitix.android.compass.LocationReceiver;
import com.inveitix.android.compass.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final int ANIMATION_BUFFER = 5;
    private static final String TAG = "MainActivity";
    private static final float ANIMATION_DELAY = 200;
    private static final long SAVE_TO_DB_DELAY = 60 * 1000;
    @BindView(R.id.txt_heading)
    TextView txtHeading;
    @BindView(R.id.img_compass)
    ImageView imgCompass;

    AlarmManager alarmMgr;
    private long lastAnimationTimestamp;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float currentDegree;
    private float currentImageDegree;
    private float[] gravity;
    private float[] geomagnetic;
    private Display display;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

        if (magnetometer == null) {
            showNoMagneticSensorDialog();
        }
    }

    private void init() {
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.sharedPref = this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        saveToDbWithDelay();
    }

    private void saveToDbWithDelay() {
        if (alarmMgr == null) {
            alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        }

        Intent intent = new Intent(this, LocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), SAVE_TO_DB_DELAY,
                pendingIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (alarmMgr != null) {
            cancelSavingHistory();
        }
    }

    private void cancelSavingHistory() {
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.cancel(pendingIntent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = LocationCalculationHelper.lowPassFilterSensitive(event.values.clone(), gravity);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = LocationCalculationHelper.lowPassFilterSensitive(event.values.clone(), geomagnetic);
        }
        if (gravity != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            float degree = 0;
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            if (success) {
                currentDegree = LocationCalculationHelper.getDegreesByRotation(display.getRotation(), orientation);
                degree = currentDegree;
            }
            txtHeading.setText(getString(com.inveitix.android.compass.R.string.degree_message, -degree));

            //Animate image if necessary
            if (System.currentTimeMillis() - lastAnimationTimestamp > ANIMATION_DELAY
                    && Math.abs(currentImageDegree - degree) > ANIMATION_BUFFER) {
                AnimationUtils.animateCompass(currentImageDegree, degree, imgCompass);
                lastAnimationTimestamp = System.currentTimeMillis();
                currentImageDegree = degree;
            }
            currentDegree = -degree;
            saveDegreeValue(currentDegree);
        }
    }

    private void saveDegreeValue(float degree) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("degree", degree);
        editor.apply();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @OnClick(R.id.btn_view_history)
    public void onViewHistoryClicked() {
        startActivity(new Intent(this, LocationHistoryActivity.class));
    }

    public void showNoMagneticSensorDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.no_magnetic_field_title));
        alertDialog.setMessage(getString(R.string.cant_detect_magnetic));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this, DirectionActivity.class));
                    }
                });
        alertDialog.show();
    }
}
