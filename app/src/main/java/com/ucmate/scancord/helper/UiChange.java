package com.ucmate.scancord.helper;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ucmate.scancord.R;

public class UiChange {
    public static void Change(Context context, Window window){
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getResources().getColor(R.color.secondary_white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
