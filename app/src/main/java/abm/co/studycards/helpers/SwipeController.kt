package abm.co.studycards.helpers

import abm.co.studycards.data.model.MyButton
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView


@SuppressLint("ClickableViewAccessibility")
abstract class SwipeController(
    context: Context, private val recyclerView: RecyclerView,
    private val buttonWidth: Int = 200
) :
    ItemTouchHelper.SimpleCallback(0, LEFT) {
    private var buttonList: HashSet<MyButton> = HashSet()
    private lateinit var gestureDetector: GestureDetector
    private var swipeThreshold = 5f
    private var currentViewHolder = -1
    private var lastViewHolder = -1
    private var buttonBuffer: MutableMap<Int, MutableList<MyButton>> = HashMap()
    abstract fun instantiateMyButton(
        viewHolder: RecyclerView.ViewHolder,
        buffer: MutableList<MyButton>
    )

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (e != null) {
                for (button in buttonList) {
                    if (button.onClick(e.x, e.y)) {
                        buttonList.clear()
                        break
                    }
                }
            }
            return true
        }
    }
    private val onTouchListener = View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE
            || event.action == MotionEvent.ACTION_UP
        ) {
            gestureDetector.onTouchEvent(event)
        }
        false
    }

    init {
        this.gestureDetector = GestureDetector(context, gestureListener)
        this.recyclerView.setOnTouchListener(onTouchListener)
        this.buttonBuffer = HashMap()
        attachSwipe()
    }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (lastViewHolder != -1 && lastViewHolder != currentViewHolder) {
            recyclerView.adapter?.notifyItemChanged(lastViewHolder)
        }
        lastViewHolder = currentViewHolder
        buttonList.addAll(buttonBuffer[pos]!!)
        buttonBuffer.clear()
    }
//
//    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
//        return super.getSwipeVelocityThreshold(defaultValue/swipeThreshold)
//    }
//
//    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
//        return super.getSwipeEscapeVelocity(defaultValue/swipeThreshold)
//    }
//

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (actionState == ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<MyButton> = ArrayList()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateMyButton(viewHolder, buffer)
                    buttonBuffer[pos] = buffer
                } else {
                    buffer = buttonBuffer[pos]!!
                }
                translationX =
                    dX * buffer.size.toFloat() * (buttonWidth * 1.5).toFloat() / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
                currentViewHolder = pos
            } else if (dX == 0f) {
                buttonList.clear()
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun drawButton(
        c: Canvas,
        itemView: View,
        buffer: MutableList<MyButton>,
        pos: Int,
        translationX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -translationX / buffer.size
        for (b in buffer) {
            val left = right - dButtonWidth
            b.onDraw(
                c,
                RectF(left + 10, itemView.top.toFloat(), right, itemView.bottom.toFloat()),
                pos
            )
            right = left
        }
    }
}