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
import com.bapplications.maplemobile.views.view_models.GameActivityViewModel;
import com.bapplications.maplemobile.views.windows.InventoryFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


public class GameActivityUIManager implements GameEngineListener {

    private GameActivity activity;
    private GameActivityViewModel viewModel;
    private final ActivityGameBinding binding;
    private List<UIKeyListener> listeners = new ArrayList<>();
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private HashMap<KeyAction, GameViewButton> controllers = new HashMap<>();
    private Fragment windowFragment;


    public GameActivityUIManager(GameActivity activity, ActivityGameBinding binding) {
        this.activity = activity;
        viewModel = new ViewModelProvider(activity)
                .get(GameActivityViewModel.class);
        this.binding = binding;
        binding.setViewModel(viewModel);

        initToolsWindow();
        setClickListeners();
        putControllers();

    }

    private void initToolsWindow() {
        // it is just for initialization to avoid fail on first time on transaction.remove(null)
        windowFragment = new Fragment();
        viewModel.getWindowState().observe(activity, windowState -> {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            switch (windowState) {
                case GONE:
                    transaction.remove(windowFragment);
                    windowFragment = null;
                    binding.toolsWindow.setVisibility(View.GONE);
                    break;
                case INVENTORY:
                    windowFragment = InventoryFragment.newInstance(activity.getGameEngine().getPlayer());
                    transaction.replace(R.id.tools_window, windowFragment);
                    binding.toolsWindow.setVisibility(View.VISIBLE);
                    break;
            }
            transaction.commit();
        });
    }

    private void setClickListeners() {
        binding.expressionsBtns.setOnClickListener(v ->
            StaticUtils.popViews(binding.expressionsBtns, binding.expressionsBtnsLayout, StaticUtils.PopDirection.UP)
        );
        binding.toolsBtn.setOnClickListener(v -> {
            viewModel.setWindowState(WindowState.GONE);
            StaticUtils.popViews(binding.toolsBtn,
                    Arrays.asList(binding.inventoryBtn, binding.equipedBtn,
                            binding.statsBtn, binding.skillsBtn),
                    StaticUtils.PopDirection.DOWN);
        });
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
        binding.inventoryBtn.animate().setInterpolator(interpolator).rotation(45).setDuration(300).start();
        if(viewModel.getWindowState().getValue() != WindowState.INVENTORY) {
            viewModel.setWindowState(WindowState.INVENTORY);
        } else {
            viewModel.setWindowState(WindowState.GONE);
        }
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

    public enum WindowState {
        GONE,
        INVENTORY;
    }
}
