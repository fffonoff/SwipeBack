package com.fffonoff.swipeback;

import android.content.res.Resources;


public final class Density {

    private static final float density = Resources.getSystem().getDisplayMetrics().density;


    private Density() {
    }

    public static float dpToPx(int dp) {
        return dp * density;
    }
}
