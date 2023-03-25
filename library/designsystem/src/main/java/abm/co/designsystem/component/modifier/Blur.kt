package abm.co.designsystem.component.modifier

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

// Deprecated in Android 12
@Suppress("DEPRECATION")
fun Bitmap.blur(context: Context, radius: Float = 15f): Bitmap {
    val outputBitmap = Bitmap.createBitmap(this)
    val renderScript = RenderScript.create(context)
    val tmpIn: Allocation = Allocation.createFromBitmap(renderScript, this)
    val tmpOut: Allocation = Allocation.createFromBitmap(renderScript, outputBitmap)

    val theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    theIntrinsic.setRadius(radius)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    return outputBitmap
}
