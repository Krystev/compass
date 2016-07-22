package com.inveitix.android.compass;

import android.view.Surface;

public class LocationCalculationHelper {

    static final float ALPHA = 0.05f;
    public static final int REVERSED_PORTRAIT = 180;
    public static final int LANDSCAPE_DEGREES_DIFFERENCE = 90;

    private static float getPortraitDegrees(float[] orientation) {
        return Math.round(-orientation[0] * 360 / (2 * 3.14159f));
    }

    private static float getReversedLandscapeDegrees(float[] orientation) {
        return Math.round(-orientation[0] * 360 / (2 * 3.14159f)
                + LANDSCAPE_DEGREES_DIFFERENCE);
    }

    private static float getReversedPortraitDegrees(float[] orientation) {
        return Math.round(-orientation[0] * 360 / (2 * 3.14159f)
                + REVERSED_PORTRAIT);
    }

    private static float getLandscapeDegrees(float[] orientation) {
        return Math.round(-orientation[0] * 360 / (2 * 3.14159f)
                - LANDSCAPE_DEGREES_DIFFERENCE);
    }

    public static float getDegreesByRotation(int rotation, float[] orientation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                return LocationCalculationHelper.getPortraitDegrees(orientation);
            case Surface.ROTATION_90:
                return LocationCalculationHelper.getLandscapeDegrees(orientation);
            case Surface.ROTATION_180:
                return LocationCalculationHelper.getReversedPortraitDegrees(orientation);
            case Surface.ROTATION_270:
                return LocationCalculationHelper.getReversedLandscapeDegrees(orientation);
        }
        return 0;
    }

    public static float[] lowPassFilterSensitive(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

}
