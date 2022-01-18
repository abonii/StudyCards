package abm.co.studycards.data.model

import android.graphics.*

class MyButton(private val text:String,
             private val imageResourceId:Int,
            private val color:Int, private val listener: MyButtonClickListener) {
    private var pos: Int = 0
    private var clickRegion: RectF? = null
    private val textSize:Int = 25
    fun onClick(x: Float, y: Float): Boolean {
        if (clickRegion != null && clickRegion!!.contains(x, y)) {
            listener.onClick(pos)
            return true
        }
        return false
    }

    fun onDraw(c: Canvas, rectF: RectF, pos: Int) {
        val p = Paint()
        val backgroundCornerOffset = 20f // to cover rounded corners of rectangle
        val cHeight = rectF.height()
        val cWidth = rectF.width()
        p.color = color
        c.drawRoundRect(
            rectF.left + backgroundCornerOffset / 4,
            rectF.top + backgroundCornerOffset / 2,
            rectF.right,
            rectF.bottom - backgroundCornerOffset / 2,
            backgroundCornerOffset, backgroundCornerOffset, p
        )
        //Text
        p.color = Color.WHITE
        p.textSize = textSize.toFloat()

        val rect = Rect()
        p.textAlign = Paint.Align.LEFT
        p.getTextBounds(text, 0, text.length, rect)
        if (imageResourceId == 0) {
            val x = cWidth / 2f - rect.width() / 2f - rect.left.toFloat()
            val y = cHeight / 2f + rect.height() / 2f - rect.bottom.toFloat()
            c.drawText(text, rectF.left + x, rectF.top + y, p)
        }
        clickRegion = rectF
        this.pos = pos
    }
}

interface MyButtonClickListener {
    fun onClick(pos:Int)
}
