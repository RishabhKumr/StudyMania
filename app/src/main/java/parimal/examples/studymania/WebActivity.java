package parimal.examples.studymania;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {
    private WebView set_qns_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        //inflate webview and set the url to google.com
        set_qns_webview=(WebView)findViewById(R.id.set_qns_webview);
        set_qns_webview.setWebViewClient(new WebViewClient());
        set_qns_webview.loadUrl("https://www.google.com/");

    }

    @Override
    public void onBackPressed() {
        //check if there is a previous page togo to in webview and go back to it.
        if(set_qns_webview.canGoBack())
        {
            set_qns_webview.goBack();
        }
        //otherwise go to previous activity
        else
        {
            super.onBackPressed();
        }
    }
}
