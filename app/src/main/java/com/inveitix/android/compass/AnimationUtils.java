package com.inveitix.android.compass;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;


public class AnimationUtils {

    public static final float PIVOT_X_VALUE = 0.5f;
    public static final float PIVOT_Y_VALUE = 0.5f;

    public static void animateCompass(float currentDegree, float degree, View view) {
        RotateAnimation ra = new RotateAnimation(currentDegree, degree,
                Animation.RELATIVE_TO_SELF, PIVOT_X_VALUE,
                Animation.RELATIVE_TO_SELF, PIVOT_Y_VALUE);
        ra.setDuration(200);
        ra.setFillAfter(true);
        view.startAnimation(ra);
    }
}
