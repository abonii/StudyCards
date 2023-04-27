package abm.co.designsystem.component.modifier

import android.content.Context
import android.graphics.Bitmap

// Deprecated in Android 12
@Suppress("DEPRECATION")
fun Bitmap.blur(context: Context, radius: Float = 15f): Bitmap {
    val outputBitmap = Bitmap.createBitmap(this)
    val renderScript = android.renderscript.RenderScript.create(context)
    val tmpIn = android.renderscript.Allocation.createFromBitmap(renderScript, this)
    val tmpOut = android.renderscript.Allocation.createFromBitmap(renderScript, outputBitmap)

    val theIntrinsic = android.renderscript.ScriptIntrinsicBlur.create(renderScript, android.renderscript.Element.U8_4(renderScript))
    theIntrinsic.setRadius(radius)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    return outputBitmap
}
