package in.ninos.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import in.ninos.R;

/**
 * Created by smeesala on 7/3/2017.
 */

public class FontTextView extends TextView {


    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try {
            TypedArray tA = context.obtainStyledAttributes(attrs, R.styleable.FontTextView);

            String font = tA.getString(R.styleable.FontTextView_fontValue);

            if (font == null) {
                font = "Orbitron-Regular.ttf";
            }

            String fontPath = String.format("fonts/%s", font);


            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), fontPath);
            setTypeface(myTypeface);
            tA.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
