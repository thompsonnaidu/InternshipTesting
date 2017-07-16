package com.test.internship.internshiptesting.adapter;

import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.test.internship.internshiptesting.model.DataUsage;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Thompson on 15-07-2017.
 */

public class NetworkUsage {

    private static final String TAG ="UID" ;
    Context context;
    public NetworkUsage(Context context)
    {
        this.context=context;
    }

    //code to check permission to access internet usage
    public boolean accesspermission()
    {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),context.getPackageName());
        }
        if (mode == AppOpsManager.MODE_ALLOWED) {

            return true;
        }
        return false;

    }


    // code to get the wifi data from the user device utilized by each application
    public DataUsage getusageData(int uid)
    {
        Calendar calendar=Calendar.getInstance();
        long to=calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH,-30);
        long from=calendar.getTimeInMillis();

        double total=0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkStatsManager service=context.getSystemService(NetworkStatsManager.class);
            NetworkStats bucket= null;
            try {
                bucket = service.queryDetailsForUid(ConnectivityManager.TYPE_WIFI,"",from,to,uid);
                double received=0;
                double send=0;

                //get the details along with the time of data usage of an application
                while (bucket.hasNextBucket())
                {
                    NetworkStats.Bucket tempbucket=new NetworkStats.Bucket();
                    bucket.getNextBucket(tempbucket);
                    received=(double)tempbucket.getRxBytes()+received;
                    send=(double)tempbucket.getTxBytes()+send;
                }
                bucket.close();
                DataUsage temp =new DataUsage();
                double uploaded=send/(1024*1024);
                double downloaded=received/(1024*1024);

                total=(received+send)/(1024*1024);

                temp.setDownload(""+String.format("%.2f",downloaded));
                temp.setUpload(""+String.format("%.2f",uploaded));
                temp.setTotal(""+String.format("%.2f",total));
                return  temp;

            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        return null;
    }



    public boolean requestUsagePermission()
    {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        context.startActivity(intent);
        return accesspermission();

    }



}
