package com.llx278.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by llx on 16-5-5.
 */
public class DPUtils {

    public  static int getDP(int i,Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, context.getResources()
                .getDisplayMetrics());
    }

}
