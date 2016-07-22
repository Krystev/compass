package com.inveitix.android.compass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private static final float ANIMATION_DELAY = 200;
    private long lastAnimationTimestamp;

    @BindView(R.id.txt_heading)
    TextView txtHeading;
    @BindView(R.id.img_compass)
    ImageView imgCompass;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float currentDegree;
    private float currentImageDegree;
    private float[] gravity;
    private float[] geomagnetic;
    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

        if(magnetometer == null) {
            showNoMagneticSensorDialog();
        }
    }

    private void init() {
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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
            txtHeading.setText("Heading: " + degree + " degrees");

            Log.e(TAG, "Rotation change from:" + currentDegree + " to " + degree);
            if(System.currentTimeMillis() - lastAnimationTimestamp > ANIMATION_DELAY
                    && Math.abs(currentImageDegree - degree) > 5) {
                AnimationUtils.animateCompass(currentImageDegree, degree, imgCompass);
                lastAnimationTimestamp = System.currentTimeMillis();
                currentImageDegree = degree;
            }
            currentDegree = -degree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
