package abm.co.studycards.helpers;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

public abstract class TouchableSpan extends ClickableSpan {
    private final int mNormalTextColor;
    private final int mPressedTextColor;
    public boolean isPressed = false;

    public TouchableSpan(int normalTextColor, int pressedTextColor) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
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
        ds.setColor(isPressed ? mPressedTextColor : mNormalTextColor);
        ds.setUnderlineText(false);
    }
}
