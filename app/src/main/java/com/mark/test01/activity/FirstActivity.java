package com.mark.test01.activity;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mark.test01.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        pers = new ArrayList<>();
        pers.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        pers.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void nextClick(View view) {
        // 先判断是否有权限。
        if(AndPermission.hasPermission(this, Permission.STORAGE)) {
            // 有权限，直接do anything.
            startActivity(new Intent(this,MainActivity.class));
        } else {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(100)
                    .callback(listener)
                    .permission(Permission.STORAGE)
                    .start();
        }

    }

    public List<String> pers;

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if(requestCode == 100) {
                startActivity(new Intent(FirstActivity.this,MainActivity.class));
            }
        }


        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(FirstActivity.this,  pers)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(FirstActivity.this, 400).show();

                // 第二种：用自定义的提示语。
                // AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                // .setTitle("权限申请失败")
                // .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                // .setPositiveButton("好，去设置")
                // .show();

                // 第三种：自定义dialog样式。
                // SettingService settingService =
                //    AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
                // 你的dialog点击了确定调用：
                // settingService.execute();
                // 你的dialog点击了取消调用：
                // settingService.cancel();
            }
        }
    };
}
