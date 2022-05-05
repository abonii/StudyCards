package abm.co.studycards.ui.home

import android.view.MotionEvent
import android.view.View

import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class RVClickHandler(private val mRecyclerView: RecyclerView) : View.OnTouchListener {
    private var mStartX = 0f
    private var mStartY = 0f
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        var isConsumed = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                mStartY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                if (detectClick(mStartX, mStartY, endX, endY)) {
                    //Ideally it would never be called when a child View is clicked.
                    //I am not so sure about this.
                    val itemView: View? = mRecyclerView.findChildViewUnder(endX, endY)
                    if (itemView == null) {
                        //RecyclerView clicked
                        mRecyclerView.performClick()
                        isConsumed = true
                    }
                }
            }
        }
        return isConsumed
    }

    companion object {
        private fun detectClick(startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
            return abs(startX - endX) < 3.0 && abs(startY - endY) < 3.0
        }
    }
}