package com.bapplications.maplemobile.ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.bapplications.maplemobile.R;
import com.bapplications.maplemobile.utils.StaticUtils;
import com.bapplications.maplemobile.constatns.Constants;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;
import com.bapplications.maplemobile.input.ExpressionInputAction;
import com.bapplications.maplemobile.input.InputAction;
import com.bapplications.maplemobile.input.InputHandler;
import com.bapplications.maplemobile.gameplay.player.look.Expression;
import com.bapplications.maplemobile.gameplay.player.PlayerStats;
import com.bapplications.maplemobile.ui.interfaces.GameEngineListener;
import com.bapplications.maplemobile.ui.interfaces.UIKeyListener;
import com.bapplications.maplemobile.ui.view_models.GameActivityViewModel;
import com.bapplications.maplemobile.ui.windows.InventoryFragment;
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
    private List<UIKeyListener> listeners = new ArrayList<>();
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private InputHandler inputHandler;
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
        inputHandler = new InputHandler();
        inputHandler
                .bindInput(
                        new GameViewController(binding.ctrlUpArrow, InputAction.Type.CONTINUES_CLICK),
                        player -> player.clickedButton(InputAction.UP_ARROW_KEY),
                        player -> player.releasedButtons(InputAction.UP_ARROW_KEY));
        inputHandler
                .bindInput(
                        new GameViewController(binding.ctrlLeftArrow, InputAction.Type.CONTINUES_CLICK),
                        player -> player.clickedButton(InputAction.LEFT_ARROW_KEY),
                        player -> player.releasedButtons(InputAction.LEFT_ARROW_KEY));
        inputHandler
                .bindInput(
                        new GameViewController(binding.ctrlRightArrow, InputAction.Type.CONTINUES_CLICK),
                        player -> player.clickedButton(InputAction.RIGHT_ARROW_KEY),
                        player -> player.releasedButtons(InputAction.RIGHT_ARROW_KEY));
        inputHandler
                .bindInput(
                        new GameViewController(binding.ctrlDownArrow, InputAction.Type.CONTINUES_CLICK),
                        player -> player.clickedButton(InputAction.DOWN_ARROW_KEY),
                        player -> player.releasedButtons(InputAction.DOWN_ARROW_KEY));
        inputHandler
                .bindInput(
                        new GameViewController(binding.ctrlJump, InputAction.Type.SINGLE_CLICK),
                        player -> player.clickedButton(InputAction.JUMP_KEY));
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
                inputHandler
                        .bindInput(
                                new GameViewController(expButton, InputAction.Type.SINGLE_CLICK),
                                player -> player.setExpression(new ExpressionInputAction(exp)));
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

    public InputHandler getInputHandler() {
        return inputHandler;
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
