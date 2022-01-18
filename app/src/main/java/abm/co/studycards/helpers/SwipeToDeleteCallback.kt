package abm.co.studycards.helpers

import abm.co.studycards.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

open class SwipeToDeleteCallback(context: Context):
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private var icon: Drawable? = null
    private var background: ColorDrawable? = null

    init {
        icon = ContextCompat.getDrawable(
            context,
            R.drawable.ic_delete
        )
        icon!!.setTint(context.getColor(R.color.white))
        background = ColorDrawable(context.getColor(R.color.red))
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(
            c, recyclerView, viewHolder, dX,
            dY, actionState, isCurrentlyActive
        )
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 0 // to cover rounded corners of rectangle
        val iconMargin: Int = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop: Int = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
        val iconBottom = iconTop + icon!!.intrinsicHeight
        val iconStart = icon!!.intrinsicWidth / 2 + iconMargin
        when {
            dX < 0 -> { // Swiping to the left
                val iconLeft: Int = itemView.right - iconMargin - icon!!.intrinsicWidth
                val iconRight: Int = itemView.right - iconMargin
                background!!.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
                if (dX < -iconStart) {
                    icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                } else {
                    icon!!.setBounds(0, 0, 0, 0)
                }
            }
            else -> { // view is unSwiped
                background!!.setBounds(0, 0, 0, 0)
            }
        }

        background!!.draw(c)
        icon!!.draw(c)
    }
}