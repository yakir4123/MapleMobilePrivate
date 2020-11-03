package com.bapplications.maplemobile.ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.bapplications.maplemobile.R;
import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.input.EventsQueue;
import com.bapplications.maplemobile.input.events.PlayerConnectEvent;
import com.bapplications.maplemobile.input.events.PlayerConnectedEvent;
import com.bapplications.maplemobile.utils.StaticUtils;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;
import com.bapplications.maplemobile.input.ExpressionInputAction;
import com.bapplications.maplemobile.input.InputAction;
import com.bapplications.maplemobile.ui.interfaces.UIKeyListener;
import com.bapplications.maplemobile.ui.windows.InventoryFragment;
import com.bapplications.maplemobile.gameplay.player.look.Expression;
import com.bapplications.maplemobile.ui.interfaces.GameEngineListener;
import com.bapplications.maplemobile.ui.view_models.GameActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


public class GameActivityUIManager implements GameEngineListener {

    private GameActivity activity;
    private GameActivityViewModel viewModel;
    private final ActivityGameBinding binding;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private Fragment windowFragment;


    public GameActivityUIManager(GameActivity activity, ActivityGameBinding binding) {
        this.activity = activity;
        viewModel = new ViewModelProvider(activity)
                .get(GameActivityViewModel.class);
        this.binding = binding;
        binding.setViewModel(viewModel);

        initToolsWindow();
        setClickListeners();
        initInputHandler();

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
        binding.equipedBtn.setOnClickListener(this::equipMenu);
        binding.statsBtn.setOnClickListener(this::statsMenu);

    }

    public void startLoadingMap() {
        activity.runOnUiThread(() ->
            StaticUtils.alphaAnimateView(binding.progressOverlay, View.VISIBLE, 1f, 2000));
    }

    public void finishLoadingMap() {
        activity.runOnUiThread(() ->
                StaticUtils.alphaAnimateView(binding.progressOverlay, View.GONE, 0, 2000));
    }

    private void initInputHandler() {
        new GameViewController(binding.ctrlUpArrow, InputAction.UP_ARROW_KEY);
        new GameViewController(binding.ctrlDownArrow, InputAction.DOWN_ARROW_KEY);
        new GameViewController(binding.ctrlLeftArrow, InputAction.LEFT_ARROW_KEY);
        new GameViewController(binding.ctrlRightArrow, InputAction.RIGHT_ARROW_KEY);
        new GameViewController(binding.ctrlJump, InputAction.JUMP_KEY);

    }

    private void inventoryMenu(View view) {
        binding.inventoryBtn.animate().setInterpolator(interpolator).rotation(45).setDuration(300).start();
        if(viewModel.getWindowState().getValue() != WindowState.INVENTORY) {
            viewModel.setWindowState(WindowState.INVENTORY);
        } else {
            viewModel.setWindowState(WindowState.GONE);
        }
    }

    private void equipMenu(View view) {
        EventsQueue.Companion.getInstance().enqueue(new PlayerConnectEvent(1));
    }

    private void statsMenu(View view) {
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
                new GameViewController(expButton, new ExpressionInputAction(exp));
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
//        viewModel.setHp(activity.getGameEngine().getCurrMap().getPlayer()
//                .getStat(PlayerStats.Id.HP));
//        viewModel.setMaxHp(activity.getGameEngine().getCurrMap()
//                .getPlayer().getStat(PlayerStats.Id.MAX_HP));
//        viewModel.setMp(activity.getGameEngine().getCurrMap().getPlayer()
//                .getStat(PlayerStats.Id.MP));
//        viewModel.setMaxMp(activity.getGameEngine().getCurrMap()
//                .getPlayer().getStat(PlayerStats.Id.MAX_MP));
//        viewModel.setExp(activity.getGameEngine().getCurrMap().getPlayer()
//                .getStat(PlayerStats.Id.EXP));
//        viewModel.setMaxExp(Constants.getExp(activity.getGameEngine()
//                .getCurrMap().getPlayer().getStat(PlayerStats.Id.LEVEL)));
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
