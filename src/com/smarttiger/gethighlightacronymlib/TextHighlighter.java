package com.smarttiger.gethighlightacronymlib;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;


/**
 * Highlights the text in a text field.
 */
public class TextHighlighter {
    private final String TAG = TextHighlighter.class.getSimpleName();

    private int highlightColor;

    public TextHighlighter(Context context) {
//    	highlightColor = context.getResources().getColor(R.color.x_highlight_blue_text_color);
    	highlightColor = 0xff1499f7;
    }

    /**
     * Sets the text on the given text view, highlighting the word that matches the given prefix.
     *
     * @param view the view on which to set the text
     * @param text the string to use as the text
     * @param prefix the prefix to look for
     */
    public void setPrefixText(TextView view, String text, String prefix) {
        view.setText(applyPrefixHighlight(text, prefix));
    }


    /**
     * Applies highlight span to the text.
     * @param text Text sequence to be highlighted.
     * @param start Start position of the highlight sequence.
     * @param end End position of the highlight sequence.
     */
    public void applyMaskingHighlight(SpannableString text, int start, int end) {
        /** Sets text color of the masked locations to be highlighted. */
       // text.setSpan(getStyleSpan(), start, end, 0);
      //XUI add by yanxz1 at 2015.1.4 
        text.setSpan(new ForegroundColorSpan(highlightColor), start, end, 0);
    }
    
    //add by zhuxh2 at 2015-11-16
    public void setMaskedHighlightView(TextView view, String text, int start, int end) {
    	SpannableString spanText = new SpannableString(text);
    	spanText.setSpan(new ForegroundColorSpan(highlightColor), start, end, 0);
		view.setText(spanText);
    }
    
    /**
     * Returns a CharSequence which highlights the given prefix if found in the given text.
     *
     * @param text the text to which to apply the highlight
     * @param prefix the prefix to look for
     */
    public CharSequence applyPrefixHighlight(CharSequence text, String prefix) {
        if (prefix == null) {
            return text;
        }

        // Skip non-word characters at the beginning of prefix.
        int prefixStart = 0;
        while (prefixStart < prefix.length() &&
                !Character.isLetterOrDigit(prefix.charAt(prefixStart))) {
            prefixStart++;
        }
        final String trimmedPrefix = prefix.substring(prefixStart);

        int index = FormatUtils.indexOfWordPrefix(text, trimmedPrefix);
        if (index != -1) {
            final SpannableString result = new SpannableString(text);
           // result.setSpan(mTextStyleSpan, index, index + trimmedPrefix.length(), 0 /* flags */);
            result.setSpan(new ForegroundColorSpan(highlightColor), index, index + trimmedPrefix.length(), 0);
            return result;
        } else {
            return text;
        }
    }
}
