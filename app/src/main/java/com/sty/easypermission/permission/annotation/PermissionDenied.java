package com.sty.easypermission.permission.annotation;

import com.sty.easypermission.permission.EasyPermissionActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: ShiTianyi
 * Time: 2021/6/28 0028 21:00
 * Description: 权限拒绝的方法
 */

@Target(ElementType.METHOD)  //方法上
@Retention(RetentionPolicy.RUNTIME)  //运行期
public @interface PermissionDenied {
    int requestCode() default EasyPermissionActivity.PARAM_REQUEST_CODE_DEFAULT;
}
