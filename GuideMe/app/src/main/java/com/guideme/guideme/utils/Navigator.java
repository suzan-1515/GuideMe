package com.guideme.guideme.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.guideme.guideme.R;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

public class Navigator {

    public static void navigate(Context context, Intent destIntent) {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.push_left_in, R.anim.push_left_out).toBundle();
        ContextCompat.startActivity(context, destIntent, bundle);
    }

}
