package com.bapplications.maplemobile.ui

import android.util.Log
import android.view.View
import android.view.LayoutInflater
import androidx.core.view.doOnLayout
import com.bapplications.maplemobile.R
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import android.view.ViewGroup.MarginLayoutParams
import com.bapplications.maplemobile.gameplay.GameMap
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.databinding.ActivityGameBinding
import com.bapplications.maplemobile.gameplay.player.PlayerViewModel
import com.bapplications.maplemobile.gameplay.player.look.Expression
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.ExpressionInputAction
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.ui.adapters.PageViewerToolsAdapter
import com.bapplications.maplemobile.ui.adapters.PageViewerToolsAdapter.WindowTool
import com.bapplications.maplemobile.ui.interfaces.GameEngineListener
import com.bapplications.maplemobile.ui.windows.ItemInfoFragment
import com.bapplications.maplemobile.utils.StaticUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class GameActivityUIManager(private var activity: GameActivity?, private val binding: ActivityGameBinding) : GameEngineListener, EventListener {
    private val playerStatViewModel: PlayerViewModel = ViewModelProvider(activity!!)
            .get(PlayerViewModel::class.java)

    private fun viewModelsObservers() {
        playerStatViewModel.canLoot.observe(activity!!, { canLoot: Boolean -> popLootButton(canLoot) })
    }

    private fun setTools() {

        binding.toolsWindow.isUserInputEnabled = false
        binding.toolsBtn.setOnClickListener {
            if(binding.toolsWindow.adapter == null) {
                binding.toolsWindow.apply {
                    adapter = PageViewerToolsAdapter(activity!!, activity!!.gameEngine.player!!)
                }

                TabLayoutMediator(binding.toolsTab, binding.toolsWindow) { _, _ -> }.attach()

                // TabLayout cant be horizontal so I need to rotate and translate it.
                // doOnLayout activate only if the view is visible so on the xml its already
                // visible but without children views
                // so when it knows how to layout it I turn the visibility off and than pop it in
                binding.toolsTab.doOnLayout {
                    it.rotation = -90f
                    it.translationY = (binding.toolsTab.width - binding.toolsTab.height) / 2f +
                            it.context.resources.getDimension(R.dimen.pops_down_or_left_translation)
                    it.visibility = View.GONE
                    StaticUtils.popViews(binding.toolsBtn, binding.toolsTab,
                            StaticUtils.PopDirection.DOWN)
                }

                // null mean i dont want to have view in null cases
                // the order is made that way because those inventoryItemInfo need to be already
                // instantiated when its on the inventory screen.
                // the same with the equipped
                val iconList = listOf(null, R.drawable.bag, null, R.drawable.armor_button, R.drawable.skillbook, R.drawable.stats)
                for (i in iconList.indices) {
                    iconList[i]?.let {
                        binding.toolsTab.getTabAt(i)?.customView = LayoutInflater.from(activity)
                                .inflate(R.layout.tools_tab, binding.toolsWindow, false).apply {
                                    (this as FloatingActionButton).setImageDrawable(ContextCompat.getDrawable(this.context, it))
                                }
                    }
                }
            } else {
                StaticUtils.popViews(binding.toolsBtn, binding.toolsTab,
                        StaticUtils.PopDirection.DOWN)
            }
            binding.toolsWindow.visibility = if(binding.toolsWindow.visibility == View.GONE) {
                View.VISIBLE
            } else {
                binding.toolsWindow.currentItem = WindowTool.NONE.ordinal
                View.GONE
            }
        }

        binding.toolsTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.let { StaticUtils.rotateViewAnimation(it, true) }
                if(tab.position != 2) {
                    (activity?.supportFragmentManager
                            ?.findFragmentByTag("f" + 2) as ItemInfoFragment?)
                            ?.setItem(null)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.let { StaticUtils.rotateViewAnimation(it, false) }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                binding.toolsWindow.currentItem = 0
            }

        })

    }

    private fun popLootButton(canLoot: Boolean) {
        StaticUtils.popViews(null, binding.ctrlLoot, StaticUtils.PopDirection.UP, canLoot)
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

    private fun setListeners() {
        EventsQueue.instance.registerListener(EventType.ItemDropped, this)
        EventsQueue.instance.registerListener(EventType.EquipItem, this)
        EventsQueue.instance.registerListener(EventType.UnequipItem, this)
    }

    private fun setExpressions(expressions: Collection<Expression>?) {
        binding.expressionsBtns.setOnClickListener { StaticUtils.popViews(binding.expressionsBtns, binding.expressionsBtnsLayout, StaticUtils.PopDirection.UP) }
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

    init {
        binding.playerViewModel = playerStatViewModel

        viewModelsObservers()
        setTools()
        initInputHandler()
        setListeners()
    }

    override fun onEventReceive(event: Event) {
        activity?.runOnUiThread {
            when (event) {
                is ItemDroppedEvent, is EquipItemEvent -> {
                    binding.toolsWindow.currentItem = WindowTool.INVENTORY.ordinal
                }
                is UnequipItemEvent -> {
                    binding.toolsWindow.currentItem = WindowTool.EQUIPPED.ordinal
                }

            }
        }
    }

}