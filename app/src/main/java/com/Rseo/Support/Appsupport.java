package com.Rseo.Support;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Rseo.R;


public class Appsupport {

    public static void showPrivacy(AppCompatActivity activity,String privacyPolicyUrl){

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, R.string.no_app_found_to_open_the_url, Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareApp(AppCompatActivity activity){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.hey_check_out_this_cool_note_app)+"https://play.google.com/store/apps/details?id=" + activity.getPackageName());
        sendIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.share_via)));
    }


    public static String getVersionName(AppCompatActivity activity) {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}
