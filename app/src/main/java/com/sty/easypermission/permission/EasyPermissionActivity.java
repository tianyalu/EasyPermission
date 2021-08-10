package com.sty.easypermission.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.sty.easypermission.R;
import com.sty.easypermission.permission.core.IPermission;
import com.sty.easypermission.permission.util.PermissionUtils;

/**
 * Author: ShiTianyi
 * Time: 2021/6/28 0028 20:50
 * Description: 空白的Activity，权限库里面的，专门用来申请用的
 */
public class EasyPermissionActivity extends Activity {
    //定义权限处理的标记 -- 接收用户传递进来的
    public static final String PARAM_PERMISSION = "param_permission"; //权限名
    public static final String PARAM_PERMISSION_CODE = "param_permission_code"; //权限码
    public static final int PARAM_REQUEST_CODE_DEFAULT = -1;

    //真正接收存储的变量
    private String[] permissions; //终于接收到了权限
    private int requestCode; //终于接收到了 权限Code
    private static IPermission iPermissionListener;  //这个Activity已经授权、取消授权、被拒绝授权

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_permission);
        initData();
    }

    private void initData() {
        permissions = getIntent().getStringArrayExtra(PARAM_PERMISSION);
        requestCode = getIntent().getIntExtra(PARAM_PERMISSION_CODE, PARAM_REQUEST_CODE_DEFAULT);

        if(permissions == null || requestCode < 0 || iPermissionListener == null) {
            this.finish();
            return;
        }

        //能走到这里，就开始去检查，是否已经授权了
        boolean permissionRequest = PermissionUtils.hasPermissionRequest(this, permissions);
        if(permissionRequest) { //已经授权了，无需再申请
            //通过监听接口，告诉外界已经授权了
            iPermissionListener.granted();
            this.finish();
            return;
        }

        //能走到这里就证明还需要去申请权限
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    //申请权限之后的结果方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //返回的结果，需要去验证一下是否完全成功了
        if(PermissionUtils.requestPermissionSuccess(grantResults)) { //真正申请成功了
            //通过监听接口，告诉外界，已经授权成功
            iPermissionListener.granted();
            this.finish();
            return;
        }

        //如果用户点击了“拒绝”，“不再提示”打钩等操作，告诉外界
        if(!PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
            //用户拒绝，不再提醒
            //通过接口监听，告诉外界被拒绝，“不再提示”被打钩
            iPermissionListener.denied();
            this.finish();
            return;
        }

        //如果执行到这里了，就证明权限被取消了
        iPermissionListener.cancel();
        this.finish();
    }

    //专门处理当前Activity结束的时候，不需要有动画效果
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    //把当前整个Activity暴露给外界使用
    public static void requestPermissionAction(Context context, String[] permissions, int requestCode, IPermission iPermission) {
        iPermissionListener = iPermission;

        Intent intent = new Intent(context, EasyPermissionActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_PERMISSION_CODE, requestCode);
        bundle.putStringArray(PARAM_PERMISSION, permissions);

        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
