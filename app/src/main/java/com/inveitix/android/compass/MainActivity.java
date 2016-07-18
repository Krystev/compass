package com.inveitix.android.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final int REVERSED_PORTRAIT = 180;
    public static final int LANDSCAPE_DEGREES_DIFFERENCE = 90;
    public static final float PIVOT_X_VALUE = 0.5f;
    public static final float PIVOT_Y_VALUE = 0.5f;
    static final float ALPHA = 0.05f;

    @BindView(R.id.txt_heading)
    TextView txtHeading;
    @BindView(R.id.img_compass)
    ImageView imgCompass;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float currentDegree;
    private float[] gravity;
    private float[] geomagnetic;
    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = lowPassFilterSensitive(event.values.clone(), gravity);
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = lowPassFilterSensitive(event.values.clone(), geomagnetic);
        if (gravity != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            float degree = 0;
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            if (success) {
                getDegreesByRotation(orientation);
                degree = currentDegree;
            }
            txtHeading.setText("Heading: " + degree + " degrees");
            animateCompass(degree);
            currentDegree = -degree;
        }
    }

    private void animateCompass(float degree) {
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, PIVOT_X_VALUE,
                Animation.RELATIVE_TO_SELF, PIVOT_Y_VALUE);
        ra.setDuration(210);
        ra.setFillAfter(true);
        imgCompass.startAnimation(ra);
    }

    private void getDegreesByRotation(float[] orientation) {
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                getPortraitDegrees(orientation);
                break;
            case Surface.ROTATION_90:
                getLandscapeDegrees(orientation);
                break;
            case Surface.ROTATION_180:
                getReversedPortraitDegrees(orientation);
                break;
            case Surface.ROTATION_270:
                getReversedLandscapeDegrees(orientation);
                break;
        }
    }

    protected float[] lowPassFilterSensitive(float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    private void getPortraitDegrees(float[] orientation) {
        currentDegree = Math.round(-orientation[0] * 360 / (2 * 3.14159f));
    }

    private void getReversedLandscapeDegrees(float[] orientation) {
        currentDegree = Math.round(-orientation[0] * 360 / (2 * 3.14159f)
                + LANDSCAPE_DEGREES_DIFFERENCE);
    }

    private void getReversedPortraitDegrees(float[] orientation) {
        currentDegree = Math.round(-orientation[0] * 360 / (2 * 3.14159f)
                + REVERSED_PORTRAIT);
    }

    private void getLandscapeDegrees(float[] orientation) {
        currentDegree = Math.round(-orientation[0] * 360 / (2 * 3.14159f)
                - LANDSCAPE_DEGREES_DIFFERENCE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
