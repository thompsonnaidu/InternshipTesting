package com.test.internship.internshiptesting.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.test.internship.internshiptesting.R;
import com.test.internship.internshiptesting.adapter.DisplayAdater;
import com.test.internship.internshiptesting.adapter.NetworkUsage;
import com.test.internship.internshiptesting.model.DataUsage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayData extends AppCompatActivity {

    public RecyclerView usagelist;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);
        usagelist= (RecyclerView) findViewById(R.id.recyclerview);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);

        new Background().execute("");

    }

    class Background extends AsyncTask<String,String,String>
    {

        ArrayList<DataUsage> details;
        NetworkUsage networkUsage;
        Boolean usageaccess=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            details=new ArrayList<>();
            networkUsage=new NetworkUsage(DisplayData.this);
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DisplayData.this);
            DisplayAdater adater=new DisplayAdater(details,DisplayData.this);
            usagelist.setLayoutManager(mLayoutManager);
            progressBar.setVisibility(View.GONE);
            usagelist.setAdapter(adater);

        }

        @Override
        protected String doInBackground(String... params) {

            // get all the package a package manager
            PackageManager packageManager=getPackageManager();


            // get the list of all application installed in the device
            List<ApplicationInfo> appInfo=packageManager.getInstalledApplications(0);

            //loop through each application
            for (ApplicationInfo application:appInfo)
            {
                try {
                    //get the package permission of the application
                    PackageInfo packageInfo= packageManager.getPackageInfo(application.packageName,PackageManager.GET_PERMISSIONS);

                    //get the list of the permission of an application requested for
                    String [] permissions=packageInfo.requestedPermissions;

                    if(permissions !=null)
                    {

                        for(String permission:permissions)
                        {
                            // check if the applicationn has request for internet permission
                       ;
                            if(permission.equals("android.permission.INTERNET"))
                            {
                                String packname=application.packageName;
                                DataUsage temp= networkUsage.getusageData(application.uid);
                                temp.setIcon(application.loadIcon(packageManager));
                                temp.setName(packname);

                                details.add(temp);
                            }
                        }

                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

            return ""+details.size();
        }







    }

}
