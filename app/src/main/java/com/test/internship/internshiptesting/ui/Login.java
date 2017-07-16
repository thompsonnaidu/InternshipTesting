package com.test.internship.internshiptesting.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.test.internship.internshiptesting.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    EditText email,password;
    Button signin;
    TextInputLayout emailLayout,passwordTextLayout;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailLayout= (TextInputLayout) findViewById(R.id.emailTextInput);
        passwordTextLayout= (TextInputLayout) findViewById(R.id.passTextInput);
        email= (EditText) findViewById(R.id.emailid);
        password= (EditText) findViewById(R.id.password);
        signin= (Button) findViewById(R.id.login);
        checksession();
    }

    public void onLogin(View view)
    {
        if(email.getText().length()>0)
        {
            emailLayout.setError(null);
        }

        if(password.getText().length()>0)
        {
            passwordTextLayout.setError(null);
        }
        if(email.getText().toString().equals(""))
        {
            emailLayout.setError("Enter username");
        }
        else if(password.getText().toString().equals(""))
        {
            passwordTextLayout.setError("Enter password");
        }
        else
        {
            progressDialog=new ProgressDialog(view.getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            check_login(email.getText().toString(),password.getText().toString());
        }

        //progressDialog.dismiss();
    }


    public void check_login(String user,String pass)
    {
        RequestQueue requestQueue;
        String url=getResources().getString(R.string.login_url);
        requestQueue=Volley.newRequestQueue(getApplicationContext());

        //creating  parameter to be past to server
        HashMap<String,String> parameter=new HashMap<String, String>();
        parameter.put("email",user);
        parameter.put("password",pass);

        // creating a request and definig how it to handle it
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, new JSONObject(parameter), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    //processing the given request
                    JSONObject data=response.getJSONObject("data");
                    String name=data.getString("username");
                    String bio =data.getString("bio");
                    String pic=data.getString("pic");
                    String email=data.getString("email");
                    Log.d("responsedata",name+" "+bio+" "+pic+" "+email);
                    storesessiondata(name,pic,bio,email);
                    Intent intent=new Intent(getApplicationContext(),Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No connection please try later",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        requestQueue.add(request);


    }


    private void storesessiondata(String name,String pic,String bio,String email)
    {
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("session",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("login","success");
        editor.putString("name",name);
        editor.putString("bio",bio);
        editor.putString("pic",pic);
        editor.putString("email",email);
        editor.commit();
    }


    private void checksession()
    {
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("session",0);
        String login=sharedPreferences.getString("login",null);

        if(login !=null)
        {
            Intent intent=new Intent(getApplicationContext(),Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }
}
