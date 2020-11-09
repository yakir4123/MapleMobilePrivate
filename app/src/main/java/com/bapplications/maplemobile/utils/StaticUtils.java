package com.bapplications.maplemobile.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;

import com.bapplications.maplemobile.R;

import java.util.Collections;
import java.util.List;

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

    public enum PopDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public static void popViews(View poper, View pops, PopDirection direc) {
        popViews(poper, Collections.singletonList(pops), direc);
    }

    public static void popViews(View poper, View pops, PopDirection direc, boolean popin) {
        popViews(poper, Collections.singletonList(pops), direc, popin);
    }

    public static void popViews(View poper, List<View> pops, PopDirection direc) {
        popViews(poper, pops, direc, pops.get(0).getVisibility() == View.GONE);
    }

    public static void popViews(View poper, List<View> pops, PopDirection direc, boolean popin) {
        if(poper != null) {
            StaticUtils.rotateViewAnimation(poper, popin).start();
        }
        for (View pop : pops)
            StaticUtils.popUpView(pop, direc).start();
    }

    public static ViewPropertyAnimator rotateViewAnimation(final View view, boolean popIn) {
        OvershootInterpolator interpolator = new OvershootInterpolator();
        return rotateViewAnimation(view, interpolator, popIn ? 45 : -45, 300);
    }

    public static ViewPropertyAnimator rotateViewAnimation(final View view, TimeInterpolator interpolator,
                                                           int rotation, int duration) {
        return view.animate()
                .setInterpolator(interpolator)
                .rotationBy(rotation)
                .setDuration(duration);
    }

    public static ViewPropertyAnimator popUpView(final View view, PopDirection direc) {
        float translation = 0;
        switch (direc) {
            case UP:
            case RIGHT:
                translation = view.getContext().getResources()
                        .getDimension(R.dimen.pops_up_or_right_translation);
                break;
            case DOWN:
            case LEFT:
                translation = view.getContext().getResources()
                        .getDimension(R.dimen.pops_down_or_left_translation);
                break;
        }
        translation = view.getVisibility() != View.GONE ? translation : -translation;
        OvershootInterpolator interpolator = new OvershootInterpolator();
        return popUpView(view, interpolator, direc, translation, 300);
    }

    public static ViewPropertyAnimator popUpView(final View view, TimeInterpolator interpolator,
                                                 PopDirection direc, float translation, int duration) {
        boolean visibleBeforeAnimation = view.getVisibility() == View.VISIBLE;
        if (!visibleBeforeAnimation) {
            view.setVisibility(View.VISIBLE);
        }

        float translationX, translationY;
        translationY = translationX = 0;
        switch (direc){
            case DOWN:
            case UP:
                translationY = translation;
                break;
            case RIGHT:
            case LEFT:
                translationX = translation;
                break;
        }

        return view.animate()
                    .translationYBy(translationY)
                    .translationXBy(translationX)
                    .setInterpolator(interpolator)
                    .alpha(visibleBeforeAnimation ? 0 : 1)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if ( visibleBeforeAnimation ) view.setVisibility(View.GONE);
                            }

                        });
    }


    public static void alphaAnimateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(toVisibility);
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

    public static Rect locateView(View v)
    {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try
        {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe)
        {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
