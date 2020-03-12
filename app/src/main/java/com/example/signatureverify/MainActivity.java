package com.example.signatureverify;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        Context context =getApplicationContext ();

        String cert_sha1="59F8A6B86A367F0586F1A15DDDB63D75263C5D62"; // 通过调试提前获取apk的sha1签名
        boolean is_org_app = false;
        try {
            is_org_app = isOrgApp(context,cert_sha1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace ();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ();
        }

        if(!is_org_app){
            new AlertDialog.Builder (this)
                    .setTitle ("警告")
                    .setMessage ("检测到您正在使用非正版软件，请下载正版软件")
                    .setNegativeButton ("关闭",new DialogInterface.OnClickListener (){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing - it will close on its own
                        android.os.Process.killProcess ((android.os.Process.myPid ()));
                        //如果签名不一致，说明程序被修改了，直接退出
            }}).show ();



        }

        Intent intent =new Intent (MainActivity.this,TextShow.class);
        startActivity (intent);
    }
//比较签名
    private boolean isOrgApp(Context context, String cert_sha1) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
        String current_sha1=getAppSha1(context);
        current_sha1=current_sha1.replace (":","");
        return cert_sha1.equals (current_sha1);
    }
//生成sha1的签名
    private String getAppSha1(Context context) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
        PackageInfo info=context.getPackageManager ().getPackageInfo (context.getPackageName (),PackageManager.GET_SIGNATURES);
        byte[] cert =info.signatures[0].toByteArray ();
        MessageDigest md =MessageDigest.getInstance ("SHA1");
        byte[] publicKey=md.digest (cert);
        StringBuffer hexString =new StringBuffer ();
        for(int i=0;i<publicKey.length;i++){
            String appendString=Integer.toHexString (0xFF&publicKey[i]).toUpperCase(Locale.US);
            if(appendString.length ()==1){
                hexString.append("0");
            }
            hexString.append(appendString);//签名的格式是11:22,所以需要加上":"
            hexString.append (":");
        }
        String result=hexString.toString ();
        return result.substring (0,result.length ()-1);
    }

}
