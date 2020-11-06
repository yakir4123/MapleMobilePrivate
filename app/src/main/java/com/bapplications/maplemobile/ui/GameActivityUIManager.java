package com.bapplications.maplemobile.ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.bapplications.maplemobile.R;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;
import com.bapplications.maplemobile.gameplay.GameMap;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.gameplay.player.PlayerViewModel;
import com.bapplications.maplemobile.input.EventsQueue;
import com.bapplications.maplemobile.utils.Randomizer;
import com.bapplications.maplemobile.utils.StaticUtils;
import com.bapplications.maplemobile.input.InputAction;
import com.bapplications.maplemobile.input.ExpressionInputAction;
import com.bapplications.maplemobile.ui.windows.InventoryFragment;
import com.bapplications.maplemobile.input.events.PlayerConnectEvent;
import com.bapplications.maplemobile.gameplay.player.look.Expression;
import com.bapplications.maplemobile.ui.interfaces.GameEngineListener;
import com.bapplications.maplemobile.ui.view_models.GameActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Collection;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


public class GameActivityUIManager implements GameEngineListener {

    private GameActivity activity;
    private Fragment windowFragment;
    private final ActivityGameBinding binding;
    private GameActivityViewModel gameViewModel;
    private PlayerViewModel playerStatViewModel;

    OvershootInterpolator interpolator = new OvershootInterpolator();


    public GameActivityUIManager(GameActivity activity, ActivityGameBinding binding) {
        this.activity = activity;
        gameViewModel = new ViewModelProvider(activity)
                .get(GameActivityViewModel.class);
        playerStatViewModel = new ViewModelProvider(activity)
                .get(PlayerViewModel.class);

        this.binding = binding;
        binding.setGameViewModel(gameViewModel);
        binding.setPlayerViewModel(playerStatViewModel);
        viewModelsObservers();
        initToolsWindow();
        setClickListeners();
        initInputHandler();

    }

    private void viewModelsObservers() {
        playerStatViewModel.getCanLoot().observe(activity, this::popLootButton);
    }

    private void initToolsWindow() {
        // it is just for initialization to avoid fail on first time on transaction.remove(null)
        windowFragment = new Fragment();
        gameViewModel.getWindowState().observe(activity, windowState -> {
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
            gameViewModel.setWindowState(WindowState.GONE);
            StaticUtils.popViews(binding.toolsBtn,
                    Arrays.asList(binding.inventoryBtn, binding.equipedBtn,
                            binding.statsBtn, binding.skillsBtn),
                    StaticUtils.PopDirection.DOWN);
        });
        binding.inventoryBtn.setOnClickListener(this::inventoryMenu);
        binding.equipedBtn.setOnClickListener(this::equipMenu);
        binding.statsBtn.setOnClickListener(this::statsMenu);
    }

    public void popLootButton(boolean canLoot) {
        StaticUtils.popViews(null, binding.ctrlLoot, StaticUtils.PopDirection.RIGHT, canLoot);
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
        new GameViewController(binding.ctrlLoot, InputAction.LOOT_KEY);
    }

    private void inventoryMenu(View view) {
        binding.inventoryBtn.animate().setInterpolator(interpolator).rotation(45).setDuration(300).start();
        if(gameViewModel.getWindowState().getValue() != WindowState.INVENTORY) {
            gameViewModel.setWindowState(WindowState.INVENTORY);
        } else {
            gameViewModel.setWindowState(WindowState.GONE);
        }
    }

    private void equipMenu(View view) {
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
    public void onGameStarted() {    }

    public GameActivityViewModel getGameViewModel() {
        return gameViewModel;
    }

    public void onPause() {
        activity = null;
    }

    public void setGameActivity(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onPlayerLoaded(Player player) {
        player.setStats(playerStatViewModel);
        setExpressions(player.getExpressions());
    }

    @Override
    public void onMapLoaded(GameMap map) {
        finishLoadingMap();
    }

    @Override
    public void onChangedMap(int mapId) {
        startLoadingMap();
    }

    public enum WindowState {
        GONE,
        INVENTORY;
    }
}
