package com.sty.easypermission.permission.aspect;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.sty.easypermission.permission.EasyPermissionActivity;
import com.sty.easypermission.permission.annotation.Permission;
import com.sty.easypermission.permission.annotation.PermissionCancel;
import com.sty.easypermission.permission.annotation.PermissionDenied;
import com.sty.easypermission.permission.core.IPermission;
import com.sty.easypermission.permission.util.PermissionUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Author: ShiTianyi
 * Time: 2021/6/28 0028 21:07
 * Description: AspectJ 典型的面向切面的编程思想 AOP
 */

@Aspect
public class PermissionAspect {

    @Pointcut("execution(@com.sty.nepermission.permission.annotation.Permission * *(..)) && @annotation(permission)")
    public void pointActionMethod(Permission permission) {
        //内部不做任何事情，只为了@Pointcut服务
    }

    //对方法环绕监听
    @Around("pointActionMethod(permission)")
    public void aProceedingJoinPoint(final ProceedingJoinPoint point, Permission permission) throws Throwable{
        //先定义一个上下文操作环境
        Context context = null;

        final Object thisObject = point.getThis(); //如果有兼容问题，thisObject == null

        //给context初始化
        if(thisObject instanceof Context) {
            context = (Context) thisObject;
        } else if(thisObject instanceof Fragment) {
            context = ((Fragment) thisObject).getActivity();
        }

        //判断是否为null
        if(null == context || permission == null) {
            throw new IllegalAccessException("null == context || permission == null is null");
        }

        //调用权限处理的Activity申请检测处理权限操作 permission.value() == Manifest.permission.READ_EXTERNAL_STORAGE
        final Context finalContext = context;
        EasyPermissionActivity.requestPermissionAction(context, permission.value(), permission.requestCode(), new IPermission(){

            @Override
            public void granted() { //申请成功，授权成功
                //让@Permission的方法正常地执行下去
                try {
                    point.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void cancel() { //被拒绝
                //调用被@PermissionCancel的方法
                PermissionUtils.invokeAnnotation(thisObject, PermissionCancel.class);
            }

            @Override
            public void denied() { //严重拒绝，勾选了“不再提醒”
                //调用 被 @PermissionDenied 的方法
                PermissionUtils.invokeAnnotation(thisObject, PermissionDenied.class);

                //不仅仅要提醒用户，还需要自动跳转到手机设置界面
                PermissionUtils.startAndroidSettings(finalContext);
            }
        });
    }
}
