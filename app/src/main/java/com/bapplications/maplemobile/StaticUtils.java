package com.bapplications.maplemobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

public class StaticUtils {


    public static float lerp(float first, float second, float alpha)
    {
        return alpha <= 0.0f ? first
                : alpha >= 1.0f ? second
                : first == second ? first
                : ((1.0f - alpha) * first + alpha * second);
    }

    public static String extendId(int id, int length) {
        StringBuilder strid = new StringBuilder("" + id);
        for(int i = 0; strid.length() < length; i++)
            strid.insert(0, '0');
        return strid.toString();
    }

    public static int orDefault(String number, int def) {
        try
        {
            return Integer.parseInt(number);
        }
        catch (NumberFormatException ex)
        {
            return def;
        }
    }

    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }
}
