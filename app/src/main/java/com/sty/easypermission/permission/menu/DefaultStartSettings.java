package com.sty.easypermission.permission.menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * Author: ShiTianyi
 * Time: 2021/7/15 0015 20:34
 * Description:
 */
public class DefaultStartSettings implements IMenu{
    @Override
    public Intent getStartActivity(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }
}
