package com.creadigol.drivehere.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.drivehere.Model.UserItem;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.Network.LoginResponse;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.PreferenceSettings;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText edt_email,edt_password;
    TextView tvForgot;
    PreferenceSettings preferenceSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceSettings=MyApplication.getInstance().getPreferenceSettings();
        if(preferenceSettings.getIsLogin()){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_login);
            edt_email=(EditText)findViewById(R.id.edt_email);
            edt_password=(EditText)findViewById(R.id.edt_password);
            findViewById(R.id.cl_login).setOnClickListener(this);
            findViewById(R.id.cl_Singup).setOnClickListener(this);
            findViewById(R.id.tvForgot).setOnClickListener(this);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cl_login:
                String stEmail,stPassword;
                stEmail=edt_email.getText().toString();
                stPassword=edt_password.getText().toString();
                if(!CommonFunctions.isValidEmail(stEmail))
                {
                    edt_email.setError("Enter valid email id");
                }else if(stPassword.equalsIgnoreCase("")){
                    edt_password.setError("Enter password");
                }else{
                    checklLogin(stEmail,stPassword);
                }
                break;
            case R.id.tvForgot:
                Intent intent  = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.cl_Singup:
                Intent reg  = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(reg);
                break;
        }
    }

    void checklLogin(final String email,final String password){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Signing in...");
        pDialog.show();
        pDialog.setCancelable(false);
        String url = AppUrl.URL_LOGIN;

        final StringRequest reqSearchCarList = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("reqList", " Response:"+response.toString());
                try {
                    LoginResponse loginResponse= LoginResponse.parseJSON(response);

                    if (loginResponse.getStatusCode() == 1) {
                        UserItem userItem= loginResponse.getUser();
                        preferenceSettings.setUserId(userItem.getId());
                        preferenceSettings.setIslogin(true);
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (loginResponse.getStatusCode() == 2) {
                        loginAlert(loginResponse.getMessage());
                    }else if (loginResponse.getStatusCode() == 0) {
                            loginAlert(loginResponse.getMessage());
                    }else if((loginResponse.getStatusCode() == 3)) {
                        loginAlert(loginResponse.getMessage());
                    }else if(loginResponse.getStatusCode() == 4){
                        loginAlert(loginResponse.getMessage());
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
                showTryAgainAlert("Network error", "please check your internet connection try again",email,password);

                //showTryAgainAlert("Info", "Network error, Please try again!");
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.Email, email);
                params.put(ParamsKey.Password, password);
                params.put(ParamsKey.APP_TYPE, MyApplication.APP_TYPE);
                Log.e("reqSearchCarList", "Posting params: " + params.toString());

                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(reqSearchCarList, "carScanList");
    }


    private void loginAlert(final String massage) {

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.login_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        TextView tverrorMassgae= (TextView) dialog.findViewById(R.id.errorMassgae);
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

    public void showTryAgainAlert(String title, String message, final String email,final String password) {
        CommonFunctions.showAlertWithNegativeButton(LoginActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonFunctions.isNetworkConnected(LoginActivity.this)) {
                    dialog.dismiss();
                 checklLogin(email,password);
                } else
                    CommonFunctions.showToast(getApplicationContext(),"Please check your internet connection");
            }
        });
    }
}
