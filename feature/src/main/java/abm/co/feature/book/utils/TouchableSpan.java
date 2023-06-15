package abm.co.feature.book.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

public abstract class TouchableSpan extends ClickableSpan {
    private final int mNormalTextColor;
//    private final int mPressedTextColor;
    private boolean isPressed = false;

    public TouchableSpan(int normalTextColor) {
        mNormalTextColor = normalTextColor;
//        mPressedTextColor = pressedTextColor;
    }

    public void setMyPressed(){
        isPressed = !isPressed;
    }
    public boolean getMyPressed(){
        return isPressed;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mNormalTextColor);
        ds.setUnderlineText(false);
    }
}
