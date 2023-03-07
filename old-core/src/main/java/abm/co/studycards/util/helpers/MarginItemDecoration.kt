package abm.co.studycards.util.helpers

import abm.co.studycards.R
import abm.co.studycards.util.px
import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView


class MarginItemDecoration(context: Context, orientation: Int, private var space: Int) :
    DividerItemDecoration(context, orientation) {

    init {
        val divider = ContextCompat.getDrawable(context, R.drawable.divider)
        divider?.let { setDrawable(it) }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = space.px()
        outRect.top = space.px()
    }
}