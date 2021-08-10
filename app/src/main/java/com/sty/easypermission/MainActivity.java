package com.sty.easypermission;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sty.easypermission.permission.annotation.Permission;
import com.sty.easypermission.permission.annotation.PermissionCancel;
import com.sty.easypermission.permission.annotation.PermissionDenied;

/**
 * 用户只需要关心注解就行了
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test(View view) {

    }

    public void permissionRequestTest(View view) {
        testRequest();
    }

    //申请权限 函数名可以随便取
    @Permission(value = Manifest.permission.READ_EXTERNAL_STORAGE, requestCode = 200)
    private void testRequest() {
        //增加代码 增加他的字节码 是为了他自己的控制
        Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show();
        //增加代码 增加他的字节码 是为了他自己的控制
        return; //end == proceed
    }

    //权限被取消  函数名可以随便取
    @PermissionCancel
    public void testCancel() {
        Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    //多次拒绝，用户勾选了“不再提示”
    @PermissionDenied
    public void testDenied() {
        Toast.makeText(this, "权限被拒绝（用户勾选了不再提示）", Toast.LENGTH_SHORT).show();
    }
}