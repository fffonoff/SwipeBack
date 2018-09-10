package com.fffonoff.swipeback;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public final class SwipeBackHelper {

    private SwipeBackHelper() {
    }

    @SuppressLint("CommitTransaction")
    public static FragmentTransaction createTransactionWithAnimation(@NonNull FragmentManager fragmentManager) {
        return fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_from_right,
                0,
                0,
                R.anim.slide_out_to_right_with_fade_out
        );
    }
}
