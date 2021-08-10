package com.sty.easypermission.permission.core;

/**
 * Author: ShiTianyi
 * Time: 2021/6/28 0028 20:56
 * Description:
 */
public interface IPermission {

    void granted(); //已经被授权

    void cancel();  //取消授权

    void denied();  //拒绝权限
}
