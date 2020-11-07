package com.bapplications.maplemobile.ui

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.databinding.ActivityGameBinding
import com.bapplications.maplemobile.gameplay.GameMap
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.gameplay.player.PlayerViewModel
import com.bapplications.maplemobile.gameplay.player.look.Expression
import com.bapplications.maplemobile.input.ExpressionInputAction
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.ui.interfaces.GameEngineListener
import com.bapplications.maplemobile.ui.view_models.GameActivityViewModel
import com.bapplications.maplemobile.ui.windows.EquippedFragment
import com.bapplications.maplemobile.ui.windows.InventoryFragment
import com.bapplications.maplemobile.utils.StaticUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GameActivityUIManager(private var activity: GameActivity?, private val binding: ActivityGameBinding) : GameEngineListener {
    private var windowFragment: Fragment? = null
    private val toolsButtons = listOf<View>(binding.inventoryBtn, binding.equipedBtn,
                    binding.statsBtn, binding.skillsBtn)
    private val gameViewModel: GameActivityViewModel = ViewModelProvider(activity!!)
            .get(GameActivityViewModel::class.java)
    private val playerStatViewModel: PlayerViewModel = ViewModelProvider(activity!!)
            .get(PlayerViewModel::class.java)

    private fun viewModelsObservers() {
        playerStatViewModel.canLoot.observe(activity!!, { canLoot: Boolean -> popLootButton(canLoot) })
    }

    private fun initToolsWindow() {
        // it is just for initialization to avoid fail on first time on transaction.remove(null)
        windowFragment = Fragment()
        gameViewModel.windowState.observe(activity!!, { windowState: WindowState? ->
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            binding.toolsWindow.visibility = if(windowState == WindowState.GONE) View.GONE else View.VISIBLE
            when (windowState) {
                WindowState.GONE -> {
                    rotateToolsButtons(null)
                    transaction.remove(windowFragment!!)
                    windowFragment = null
                }
                WindowState.INVENTORY -> {
                    rotateToolsButtons(binding.inventoryBtn)
                    windowFragment = InventoryFragment.newInstance(activity!!.gameEngine.player!!)
                    transaction.replace(R.id.tools_window, windowFragment!!)
                }
                WindowState.EQUIPPED -> {
                    rotateToolsButtons(binding.equipedBtn)
                    windowFragment = EquippedFragment.newInstance(activity!!.gameEngine.player!!)
                    transaction.replace(R.id.tools_window, windowFragment!!)
                }
            }
            transaction.commit()
        })
    }

    private fun setClickListeners() {
        binding.expressionsBtns.setOnClickListener { StaticUtils.popViews(binding.expressionsBtns, binding.expressionsBtnsLayout, StaticUtils.PopDirection.UP) }
        binding.toolsBtn.setOnClickListener {
            gameViewModel.setWindowState(WindowState.GONE)
            StaticUtils.popViews(binding.toolsBtn, toolsButtons,
                    StaticUtils.PopDirection.DOWN)
        }
        binding.inventoryBtn.setOnClickListener {
            switchMenu(WindowState.INVENTORY) }
        binding.equipedBtn.setOnClickListener {
            switchMenu(WindowState.EQUIPPED) }
        binding.statsBtn.setOnClickListener {
            switchMenu(WindowState.STATS) }
        binding.skillsBtn.setOnClickListener {
            switchMenu(WindowState.SKILLS) }
    }

    private fun popLootButton(canLoot: Boolean) {
        StaticUtils.popViews(null, binding.ctrlLoot, StaticUtils.PopDirection.RIGHT, canLoot)
    }

    fun startLoadingMap() {
        activity!!.runOnUiThread { StaticUtils.alphaAnimateView(binding.progressOverlay, View.VISIBLE, 1f, 2000) }
    }

    fun finishLoadingMap() {
        activity!!.runOnUiThread { StaticUtils.alphaAnimateView(binding.progressOverlay, View.GONE, 0f, 2000) }
    }

    private fun initInputHandler() {
        GameViewController(binding.ctrlUpArrow, InputAction.UP_ARROW_KEY)
        GameViewController(binding.ctrlDownArrow, InputAction.DOWN_ARROW_KEY)
        GameViewController(binding.ctrlLeftArrow, InputAction.LEFT_ARROW_KEY)
        GameViewController(binding.ctrlRightArrow, InputAction.RIGHT_ARROW_KEY)
        GameViewController(binding.ctrlJump, InputAction.JUMP_KEY)
        GameViewController(binding.ctrlLoot, InputAction.LOOT_KEY)
    }

    private fun switchMenu(to: WindowState) {
        if (gameViewModel.windowState.value != to) {
            gameViewModel.windowState.value = to
        } else {
            gameViewModel.windowState.value = WindowState.GONE
        }
    }

    private fun setExpressions(expressions: Collection<Expression>?) {
        if (expressions == null) return
        activity!!.runOnUiThread {
            binding.expressionsBtnsLayout.removeAllViews()
            for (exp in expressions) {
                if (exp.resource == 0) continue
                val expButton = activity!!.layoutInflater.inflate(R.layout.expression_button_layout, null) as FloatingActionButton
                expButton.setImageResource(exp.resource)
                GameViewController(expButton, ExpressionInputAction(exp))
                binding.expressionsBtnsLayout.addView(expButton)
                setMargins(expButton, 5, 5, 0, 0)
            }
        }
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    override fun onGameStarted() {}
    fun onPause() {
        activity = null
    }

    fun setGameActivity(activity: GameActivity?) {
        this.activity = activity
    }

    override fun onPlayerLoaded(player: Player) {
        player.setStats(playerStatViewModel)
        setExpressions(player.expressions)
    }

    override fun onMapLoaded(map: GameMap) {
        finishLoadingMap()
    }

    override fun onChangedMap(mapId: Int) {
        startLoadingMap()
    }

    enum class WindowState {
        GONE, INVENTORY, EQUIPPED, STATS, SKILLS, ITEM_INFO
    }

    private fun rotateToolsButtons(clickedView: View?) {
        toolsButtons.forEach{ StaticUtils.rotateViewAnimation(it, it == clickedView)}
    }

    init {
        binding.gameViewModel = gameViewModel
        binding.playerViewModel = playerStatViewModel

        viewModelsObservers()

        initToolsWindow()
        setClickListeners()
        initInputHandler()
    }
}