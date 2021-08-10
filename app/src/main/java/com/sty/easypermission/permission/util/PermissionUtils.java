package com.sty.easypermission.permission.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.collection.SimpleArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sty.easypermission.permission.menu.DefaultStartSettings;
import com.sty.easypermission.permission.menu.IMenu;
import com.sty.easypermission.permission.menu.OPPOStartSettings;
import com.sty.easypermission.permission.menu.VIVOStartSettings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Author: ShiTianyi
 * Time: 2021/7/19 0019 19:53
 * Description:
 */
public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();

    //定义八种权限
    private static SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS;

    static {
        MIN_SDK_PERMISSIONS = new SimpleArrayMap<>(8);
        MIN_SDK_PERMISSIONS.put("com.anroid.voicemail.permission.ADD_VOICEMAIL", 14);
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23);
    }

    private static HashMap<String, Class<? extends IMenu>> permissionMenu = new HashMap<>();
    private static final String MANUFACTURE_DEFAULT = "Default"; //默认
    private static final String MANUFACTURE_HUAWEI = "huawei"; //华为
    private static final String MANUFACTURE_MEIZU = "meizu"; //魅族
    private static final String MANUFACTURE_XIAOMI = "xiaomi"; //小米
    private static final String MANUFACTURE_SONY = "sony"; //索尼
    private static final String MANUFACTURE_OPPO = "oppo"; //OPPO
    private static final String MANUFACTURE_LG = "lg"; //LG
    private static final String MANUFACTURE_VIVO = "VIVO"; //VIVO
    private static final String MANUFACTURE_SAMSUNG = "samsung"; //三星
    private static final String MANUFACTURE_LETV = "letv"; //乐视
    private static final String MANUFACTURE_ZET = "zte"; //中兴
    private static final String MANUFACTURE_YULONG = "yulong"; //酷派
    private static final String MANUFACTURE_LENOVO = "lenovo"; //联想

    static {
        permissionMenu.put(MANUFACTURE_DEFAULT, DefaultStartSettings.class);
        permissionMenu.put(MANUFACTURE_OPPO, OPPOStartSettings.class);
        permissionMenu.put(MANUFACTURE_VIVO, VIVOStartSettings.class);
    }

    /**
     * 检查是否需要去请求权限，此方法目的：就是检查是否已经授权了
     * @param context
     * @param permissions
     * @return 返回false代表需要请求权限，返回true代表不需要请求权限就可以接收MyPermissionActivity了
     */
    public static boolean hasPermissionRequest(Context context, String... permissions) {
        for (String permission : permissions) {
            if(permissionExists(permission) && isPermissionRequest(context, permission) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前SDK权限是否存在，如果存在返回true
     * @param permission
     * @return
     */
    private static boolean permissionExists(String permission) {
        Integer minVersion = MIN_SDK_PERMISSIONS.get(permission);

        return minVersion == null || minVersion <= Build.VERSION.SDK_INT;
    }

    /**
     * 判断参数中传递进去的权限是否已经被授权了
     * @param context
     * @param permission
     * @return
     */
    private static boolean isPermissionRequest(Context context, String permission) {
        try {
            int checkSelfPermission = ContextCompat.checkSelfPermission(context, permission);
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断是否真正授权成功
     * @param grantedResult
     * @return
     */
    public static boolean requestPermissionSuccess(int... grantedResult) {
        if(grantedResult == null || grantedResult.length <= 0) {
            return false;
        }

        for (int permissionValue : grantedResult) {
            if(permissionValue != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 当用户点击了“不再提示”，这种情况需要考虑
     * 说白了：就是用户被拒绝过一次，然后又弹出这个框，【需要给用户一个解释，为什么要授权，就需要执行此方法判断】
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 专门去 callback invoke ---》 MainActivity  被注解的方法
     * @param object
     * @param annotationClass
     */
    public static void invokeAnnotation(Object object, Class annotationClass) {
        //获取object的Class 对象
        Class<?> objectClass = object.getClass();

        //遍历所有的方法
        Method[] methods = objectClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true); //让虚拟机不要去检测private

            //判断方法，是否有被annotationClass注解的方法
            boolean annotationPresent = method.isAnnotationPresent(annotationClass);

            if(annotationPresent) {
                //当前方法代表包含了 annotationClass注解的
                try {
                    method.invoke(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void startAndroidSettings(Context context) {
        //拿到当前手机品牌制造商，来获取具体细节
        Class aClass = permissionMenu.get(Build.MANUFACTURER.toLowerCase());

        if(aClass == null) {
            aClass = permissionMenu.get(MANUFACTURE_DEFAULT);
        }

        try {
            Object newInstance = aClass.newInstance();//new OPPOStartSettings()

            IMenu iMenu = (IMenu) newInstance; //IMenu iMenu = (IMenu) OPPOStartSettings;

            //高层，面向抽象，而不是面向细节
            Intent startActivityIntent = iMenu.getStartActivity(context);
            if(startActivityIntent != null) {
                context.startActivity(startActivityIntent);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
