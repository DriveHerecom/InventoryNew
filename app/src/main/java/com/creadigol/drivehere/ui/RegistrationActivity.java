package com.creadigol.drivehere.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.UserItem;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.LoginResponse;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.Network.UserDataResponse;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CommonFunctions;

import java.util.HashMap;
import java.util.Map;

public class
RegistrationActivity extends AppCompatActivity  implements View.OnClickListener{
    EditText edt_name,edt_email,edt_password,edt_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edt_email=(EditText)findViewById(R.id.edt_email);
        edt_name=(EditText)findViewById(R.id.edt_name);
        edt_password=(EditText)findViewById(R.id.edt_password);
        edt_phone=(EditText)findViewById(R.id.edt_number);
        findViewById(R.id.cl_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cl_register:
                String name,email,password,phone;
                name=edt_name.getText().toString();
                email=edt_email.getText().toString();
                password=edt_password.getText().toString();
                phone=edt_phone.getText().toString();
                if(name.equalsIgnoreCase("")){
                    edt_name.setError("Enter user name");
                }else if(!CommonFunctions.isValidEmail(email)){
                    edt_email.setError("Enter valid email id");
                }else if(password.length()<8){
                    edt_password.setError("Enter minimum 8 digit");
                }else if(phone.length()<10){
                    edt_phone.setError("Enter valid number");
                }else{
                    reqRegister(name,email,password,phone);
                }

        }
    }
    void reqRegister(final String name,final String email,final String password,final String phone){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Register you...");
        pDialog.show();
        pDialog.setCancelable(false);
        String url = AppUrl.URL_SINGUP;

        final StringRequest reqSearchCarList = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqList", " Response:"+response.toString());
                try {
                    UserDataResponse userDataResponse= UserDataResponse.parseJSON(response);

                    if (userDataResponse.getStatusCode() == 1) {
                        UserItem userItem= userDataResponse.getUserItem();
                        MyApplication.getInstance().getPreferenceSettings().setUserId(userItem.getId());
                        MyApplication.getInstance().getPreferenceSettings().setUserName(userItem.getName());
                        MyApplication.getInstance().getPreferenceSettings().setEmail(userItem.getEmail());
                        MyApplication.getInstance().getPreferenceSettings().setPassword(userItem.getPassword());
                        Toast.makeText(RegistrationActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                        finish();

                    } else if (userDataResponse.getStatusCode() == 2) //for email aleady exist
                    {
                        registrationAlert(userDataResponse.getMessage());
                    }else if (userDataResponse.getStatusCode() == 0)//for parameter missing
                    {
                        registrationAlert(userDataResponse.getMessage());
                    }else if((userDataResponse.getStatusCode() == 3)) //for error in api
                    {
                        registrationAlert(userDataResponse.getMessage());
                    }
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("reqSearchCarList", "catch" );
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("reqSearchCarList", "Error Response: " + error.getMessage());
                showTryAgainAlert("Network error", "please check your internet connection try again",name,email,password,phone);
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }

        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.Email, email);
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                params.put(ParamsKey.Password, password);
                params.put(ParamsKey.Name, name);
                params.put(ParamsKey.phoneNumber, phone);
                Log.e("reqSearchCarList", "Posting params: " + params.toString());

                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqSearchCarList, "carScanList");
    }

    private void registrationAlert(final String massage) {

        final Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.login_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        TextView tverrorMassgae= (TextView) dialog.findViewById(R.id.errorMassgae);
        TextView tvTitle= (TextView) dialog.findViewById(R.id.tv_title);
        tvTitle.setText("Registration failed!");
        tverrorMassgae.setText(massage);
        // if decline button is clicked, close the custom dialog
        TextView tvcancel= (TextView) dialog.findViewById(R.id.tvcancel);
        // if decline button is clicked, close the custom dialog
        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void showTryAgainAlert(String title, String message, final String name,final String email,final String password,final String phone) {
        CommonFunctions.showAlertWithNegativeButton(RegistrationActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonFunctions.isNetworkConnected(RegistrationActivity.this)) {
                    dialog.dismiss();
                    reqRegister(name,email,password,phone);
                } else
                    CommonFunctions.showToast(getApplicationContext(),"Please check your internet connection");
            }
        });
    }
}
