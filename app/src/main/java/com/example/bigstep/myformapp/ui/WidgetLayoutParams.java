/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.example.bigstep.myformapp.ui;

import android.os.Build;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * A configuration which can offer rich linear layout params.
 */
public class WidgetLayoutParams {


    /**
     * Method to get Linear layout params with MATCH_PARENT Width and WRAP_CONTENT Height.
     *
     * @return Returns the Linear Layout params.
     */
    public static LinearLayout.LayoutParams getFullWidthLayoutParams() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Method to get Linear layout params with Custom Width and Height.
     *
     * @return Returns the Linear Layout params.
     */
    public static LinearLayout.LayoutParams getCustomWidthHeightLayoutParams(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    /**
     * Method to set html data into text view.
     *
     * @param editText View in which text is to be shown.
     * @param value    Value which need to be set.
     */
    public static void setEditText(EditText editText, String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            editText.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY));
        } else {
            //non-inspection deprecation
            editText.setText(Html.fromHtml(value));
        }
    }


}
