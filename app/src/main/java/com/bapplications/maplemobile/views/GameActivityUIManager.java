package com.bapplications.maplemobile.views;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.bapplications.maplemobile.R;
import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.constatns.Constants;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;
import com.bapplications.maplemobile.gameplay.player.Expression;
import com.bapplications.maplemobile.gameplay.player.PlayerStats;
import com.bapplications.maplemobile.views.interfaces.GameEngineListener;
import com.bapplications.maplemobile.views.interfaces.UIKeyListener;
import com.bapplications.maplemobile.views.popup.InventoryPopup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;


public class GameActivityUIManager implements GameEngineListener {

    private GameActivity activity;
    private GameActivityViewModel viewModel;
    private final ActivityGameBinding binding;
    private List<UIKeyListener> listeners = new ArrayList<>();
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private HashMap<KeyAction, GameViewButton> controllers = new HashMap<>();


    public GameActivityUIManager(GameActivity activity, ActivityGameBinding binding) {
        this.activity = activity;
        viewModel = new ViewModelProvider(activity)
                .get(GameActivityViewModel.class);
        this.binding = binding;
        binding.setViewModel(viewModel);

        setClickListeners();
        putControllers();

    }

    public void setClickListeners() {
        binding.expressionsBtns.setOnClickListener(v ->
            StaticUtils.popViews(binding.expressionsBtns, binding.expressionsBtnsLayout, StaticUtils.PopDirection.UP)
        );
        binding.toolsBtn.setOnClickListener(v -> StaticUtils.popViews(binding.toolsBtn, binding.inventoryBtn, StaticUtils.PopDirection.DOWN));
        binding.inventoryBtn.setOnClickListener(this::inventoryMenu);

    }

    public void startLoadingMap() {
        activity.runOnUiThread(() ->
            StaticUtils.alphaAnimateView(binding.progressOverlay, View.VISIBLE, 1f, 2000));
    }

    public void finishLoadingMap() {
        activity.runOnUiThread(() ->
                StaticUtils.alphaAnimateView(binding.progressOverlay, View.GONE, 0, 2000));
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
        controllers.put(key, new GameViewButton(key, view, this));
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

    private void inventoryMenu(View view) {
        InventoryPopup popUpClass = new InventoryPopup();
        popUpClass.showPopupWindow(view, activity);
        popUpClass.setOnClickListener(v -> {
            binding.inventoryBtn.animate().setInterpolator(interpolator).rotation(0).setDuration(300).start();
            popUpClass.dismiss();
        });
        binding.inventoryBtn.animate().setInterpolator(interpolator).rotation(45).setDuration(300).start();

    }

    public void setExpressions(Collection<Expression> expressions) {
        if(expressions == null)
            return;
        activity.runOnUiThread(() -> {
            binding.expressionsBtnsLayout.removeAllViews();
            for(Expression exp : expressions){
                if ( exp.getResource() == 0)
                    continue;
                FloatingActionButton expButton = (FloatingActionButton) activity.getLayoutInflater().inflate(R.layout.expression_button_layout, null);
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

    @Override
    public void onGameStarted() {
        viewModel.setHp(activity.getGameEngine().getCurrMap().getPlayer()
                .getStat(PlayerStats.Id.HP));
        viewModel.setMaxHp(activity.getGameEngine().getCurrMap()
                .getPlayer().getStat(PlayerStats.Id.MAX_HP));
        viewModel.setMp(activity.getGameEngine().getCurrMap().getPlayer()
                .getStat(PlayerStats.Id.MP));
        viewModel.setMaxMp(activity.getGameEngine().getCurrMap()
                .getPlayer().getStat(PlayerStats.Id.MAX_MP));
        viewModel.setExp(activity.getGameEngine().getCurrMap().getPlayer()
                .getStat(PlayerStats.Id.EXP));
        viewModel.setMaxExp(Constants.getExp(activity.getGameEngine()
                .getCurrMap().getPlayer().getStat(PlayerStats.Id.LEVEL)));
    }

    public GameActivityViewModel getViewModel() {
        return viewModel;
    }

    public void onPause() {
        activity = null;
    }

    public void setGameActivity(GameActivity activity) {
        this.activity = activity;
    }

}
