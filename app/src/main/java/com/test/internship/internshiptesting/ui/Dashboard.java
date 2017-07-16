package com.test.internship.internshiptesting.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.test.internship.internshiptesting.R;
import com.test.internship.internshiptesting.adapter.NetworkUsage;
import com.test.internship.internshiptesting.model.DataUsage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    private String name,email,bio,pic;
    private ImageView avatar,avatarprofile;
    private TextView avatarname,avatarbio,avataremail;
    private Button network;
    ArrayList<DataUsage> passdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dashboard);
        SharedPreferences sessionData= getApplicationContext().getSharedPreferences("session",0);
        name=sessionData.getString("name",null);
        email=sessionData.getString("email",null);
        bio=sessionData.getString("bio",null);
        pic=sessionData.getString("pic",null);
        avatar= (ImageView) findViewById(R.id.avatar);
        avatarprofile= (ImageView) findViewById(R.id.avatarimage);
        avatarname= (TextView) findViewById(R.id.avatarname);
        avatarbio= (TextView) findViewById(R.id.avatarbio);
        avataremail= (TextView) findViewById(R.id.avataremail);
        network=(Button)findViewById(R.id.networkbtn);
        Picasso.with(Dashboard.this).load(pic).placeholder(R.drawable.placeholder).into(avatar);
        Picasso.with(this).load(pic).placeholder(R.drawable.upwards).into(avatarprofile);
        new Background().execute("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        avatarname.setText(name.toUpperCase());
        avatarbio.setText(bio);
        avataremail.setText(email);

    }


    public void displaydata(View v)
    {
        Intent i=new Intent(Dashboard.this, DisplayData.class);
        startActivity(i);
    }



    public void logout(View v)
    {
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("session",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent=new Intent(getApplicationContext(),Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
            networkUsage=new NetworkUsage(Dashboard.this);
            if(!networkUsage.accesspermission())
            {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
            else
            {
                usageaccess=true;
            }
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            if(usageaccess)
            {
                sendInternetData();

            }
            else
            {
                    Toast.makeText(Dashboard.this,"We need usage permission ", Toast.LENGTH_LONG).show();

            }

        }

        @Override
        protected String doInBackground(String... params) {

            if(!usageaccess)
            {
                return "noaccess";
            }
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
            passdata=details;
            return ""+details.size();
        }


        protected void sendInternetData()
        {
            if(details != null)
            {
                String log_data=parseData();
                Log.d("log_data",log_data);
                final RequestQueue requestQueue;
                String url=getResources().getString(R.string.data_url);
                requestQueue= Volley.newRequestQueue(getApplicationContext());

                //creating  parameter to be past to server
                HashMap<String,String> parameter=new HashMap<String, String>();
                parameter.put("log_type","wifi_usage_data");
                parameter.put("log_data",log_data);

                // creating a request and definig how it to handle it
                JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, new JSONObject(parameter), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"No connection please try later",Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(request);


            }

        }


        protected String parseData()
        {

            StringBuffer temp=new StringBuffer();
            for (DataUsage usage:details)
            {

                String t="Name--> "+usage.getName()+" : upload-->"+usage.getUpload()+"MB : Download-->"+usage.getDownload()+"MB : total-->"+usage.getTotal()+" MB \n";
                temp.append(t);
            }
            return temp.toString();
        }
    }



}
