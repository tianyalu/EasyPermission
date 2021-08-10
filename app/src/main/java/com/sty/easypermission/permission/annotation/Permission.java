package com.sty.easypermission.permission.annotation;

import com.sty.easypermission.permission.EasyPermissionActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: ShiTianyi
 * Time: 2021/6/28 0028 20:46
 * Description: 权限申请的注解
 */

@Target(ElementType.METHOD)  //方法上
@Retention(RetentionPolicy.RUNTIME)  //运行期
public @interface Permission {
    //默认是可以多个权限的
    String[] value(); //具体的申请权限

    int requestCode() default EasyPermissionActivity.PARAM_REQUEST_CODE_DEFAULT;
}
