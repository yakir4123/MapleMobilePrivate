package com.bapplications.maplemobile.views;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.bapplications.maplemobile.R;
import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;
import com.bapplications.maplemobile.gameplay.player.Expression;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class UIControllers {

    private GameActivity activity;
    private boolean isExpMenuOpen = false;
    private final ActivityGameBinding binding;
    private Collection<Expression> expressions;
    private List<UIKeyListener> listeners = new ArrayList<>();
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private HashMap<KeyAction, GameViewController> controllers = new HashMap<>();


    public UIControllers(GameActivity activity, ActivityGameBinding binding) {
        this.activity = activity;
        this.binding = binding;
        binding.fabMain.setOnClickListener(view -> {
            expMenu();
        });
        putControllers();
    }

    public void loading() {
        activity.runOnUiThread(() ->
            StaticUtils.animateView(binding.progressOverlay, View.VISIBLE, 1f, 2000));

    }

    public void finishLoading() {
        activity.runOnUiThread(() ->
                StaticUtils.animateView(binding.progressOverlay, View.GONE, 0, 2000));
    }

    private void putControllers() {
        put(KeyAction.UP_ARROW_KEY, binding.upArrowKey);
        put(KeyAction.DOWN_ARROW_KEY, binding.downArrowKey);
        put(KeyAction.LEFT_ARROW_KEY, binding.leftArrowKey);
        put(KeyAction.RIGHT_ARROW_KEY, binding.rightArrowKey);
        put(KeyAction.JUMP_KEY, binding.jumpKey);
    }

    public void put(KeyAction key, View view) {
        if(controllers.containsKey(key)){
            throw new IllegalArgumentException("Controllers already has this key");
        }

        controllers.put(key, new GameViewController(key, view, this));
    }

    public boolean isPressed(KeyAction key) {
        return controllers.get(key).isPressed();
    }

    public void registerListener(UIKeyListener listener) {
        listeners.add(listener);
    }

    public void onClick(KeyAction key) {
        for(UIKeyListener listener: listeners){
            listener.onAction(key);
        }
    }

    private void expMenu() {
        isExpMenuOpen = !isExpMenuOpen;
        float alpha;
        float rotation;
        float translation;
        if(isExpMenuOpen) {
            rotation = 45;
            translation = 0;
            alpha = 1;
        } else {
            rotation = 0;
            translation = 100;
            alpha = 0;
        }
        binding.fabMain.animate().setInterpolator(interpolator).rotation(rotation).setDuration(300).start();
        for(int i = 0; i < binding.expressionsBtnsLayout.getChildCount(); i++){
            binding.expressionsBtnsLayout.getChildAt(i)
                    .animate().translationY(translation).alpha(alpha)
                    .setInterpolator(interpolator)
                    .setDuration(300).start();
        }


    }

    public void setExpressions(Collection<Expression> expressions) {
        this.expressions = expressions;
        if(expressions == null)
            return;
        activity.runOnUiThread(() -> {
            binding.expressionsBtnsLayout.removeAllViews();
            for(Expression exp : expressions){
                if ( exp.getResource() == 0)
                    continue;
                FloatingActionButton expButton = (FloatingActionButton) activity.getLayoutInflater().inflate(R.layout.expression_button_layout, null);
                expButton.setAlpha(0f);
                expButton.setTranslationY(100);
                expButton.setImageResource(exp.getResource());
                expButton.setOnClickListener((view -> {
                    activity.getGameEngine().getCurrMap().getPlayer().getLook().setExpression(exp);
                }));
                binding.expressionsBtnsLayout.addView(expButton);
                setMargins(expButton, 5, 5, 0, 0);
            }
        });
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public interface UIKeyListener {
        void onAction(KeyAction key);
    }
}
