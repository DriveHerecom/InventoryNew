package com.creadigol.drivehere.ui;

import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creadigol.drivehere.Network.AppUrl;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CommonFunctions;


public class ForgotPasswordActivity extends AppCompatActivity {
    WebView webView;
    //String url="https://drivehere.com/inventory2/users/forgot_password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ToolBar();
        if(CommonFunctions.isNetworkConnected(getApplicationContext())) {
            webView = (WebView) findViewById(R.id.webView);
            webView.setWebViewClient(new SSLTolerentWebViewClient());
            webView.loadUrl(AppUrl.URL_FORGOT_PASSWORD);
        }else{
            showTryAgainAlert("Network error", "please check your internet connection try again");
        }
    }
    void ToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarForgot);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Forgot Password");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
            builder.setMessage(R.string.notification_error_ssl_cert_invalid);
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
    public void showTryAgainAlert(String title, String message) {
        CommonFunctions.showAlertWithNegativeButton(ForgotPasswordActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonFunctions.isNetworkConnected(ForgotPasswordActivity.this)) {
                    dialog.dismiss();
                    webView = (WebView) findViewById(R.id.webView);
                    webView.setWebViewClient(new SSLTolerentWebViewClient());
                    webView.loadUrl(AppUrl.URL_FORGOT_PASSWORD);
                } else
                    CommonFunctions.showToast(getApplicationContext(),"Please check your internet connection");
            }
        });
    }
}
