package com.sty.easypermission.permission.menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Author: ShiTianyi
 * Time: 2021/7/15 0015 20:46
 * Description:
 */
public class VIVOStartSettings implements IMenu{
    @Override
    public Intent getStartActivity(Context context) {
        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("coom.iqoo.secure");
        if(appIntent != null && Build.VERSION.SDK_INT < 23) {
            context.startActivity(appIntent);
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }
}
