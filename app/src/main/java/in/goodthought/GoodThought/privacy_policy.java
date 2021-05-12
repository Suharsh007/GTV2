package in.goodthought.GoodThought;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.webkit.WebView;


public class privacy_policy extends AppCompatActivity {
WebView privacy_id;
private String file_name = "privacy.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
 privacy_id = findViewById(R.id.privacy_id);
 privacy_id.getSettings().setJavaScriptEnabled(true);
 privacy_id.loadUrl("file:////android_asset/"+file_name);



    }
}