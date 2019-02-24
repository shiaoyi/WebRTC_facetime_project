package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/4/21.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.SparseArray;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Explaination of the code can be found on my medium account:
 * https://medium.com/@ali.muzaffar/android-developers-create-your-own-credit-card-edittext-view-e508c758e86c
 * @author Ali Muzaffar (http://alimuzaffar.com/)
 */
public class CreditCardEditText extends AppCompatEditText {

    private SparseArray<Pattern> mCCPatterns = null;

    private final char mSeparator = ' ';

    private final int mDefaultDrawableResId = R.drawable.creditcard; //default credit card image
    private int mCurrentDrawableResId = 0;
    private Drawable mCurrentDrawable;

    public CreditCardEditText(Context context) {
        super(context);
        init();
    }

    public CreditCardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mCCPatterns == null) {
            mCCPatterns = new SparseArray<>();
            // Without spaces for credit card masking
            mCCPatterns.put(R.drawable.visa, Pattern.compile("^4[0-9]{2,12}(?:[0-9]{3})?$"));
            mCCPatterns.put(R.drawable.mastercard, Pattern.compile("^5[1-5][0-9]{1,14}$"));
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

        if (mCCPatterns == null) {
            init();
        }

        int mDrawableResId = 0;
        for (int i = 0; i < mCCPatterns.size(); i++) {
            int key = mCCPatterns.keyAt(i);
            // get the object by the key.
            Pattern p = mCCPatterns.get(key);

            Matcher m = p.matcher(text);
            if (m.find()) {
                mDrawableResId = key;
                break;
            }
        }
        if (mDrawableResId > 0 && mDrawableResId != mCurrentDrawableResId) {
            mCurrentDrawableResId = mDrawableResId;
        } else if (mDrawableResId == 0) {
            mCurrentDrawableResId = mDefaultDrawableResId;
        }

        mCurrentDrawable = getResources().getDrawable(mCurrentDrawableResId);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurrentDrawable == null) {
            return;
        }

        int rightOffset = 0;
        if (getError() != null && getError().length() > 0) {
            rightOffset = (int) getResources().getDisplayMetrics().density * 32;
        }

        int right = getWidth() - getPaddingRight() - rightOffset;

        int top = getPaddingTop();
        int bottom = getHeight() - getPaddingBottom();
        float ratio = (float) mCurrentDrawable.getIntrinsicWidth() / (float) mCurrentDrawable.getIntrinsicHeight();
        //int left = right - mCurrentDrawable.getIntrinsicWidth(); //If images are correct size.
        int left = (int) (right - ((bottom - top) * ratio)); //scale image depeding on height available.
        mCurrentDrawable.setBounds(left, top, right, bottom);

        mCurrentDrawable.draw(canvas);

    }
}
