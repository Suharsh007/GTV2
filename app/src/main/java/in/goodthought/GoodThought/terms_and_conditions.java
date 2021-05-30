package in.goodthought.GoodThought;


import androidx.appcompat.app.AppCompatActivity;



import android.os.Bundle;

import android.webkit.WebView;


public class terms_and_conditions extends AppCompatActivity {
    WebView tnc_id;
    private String file_name = "terms_and_conditions.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        tnc_id = findViewById(R.id.tnc_id);
        tnc_id.getSettings().setJavaScriptEnabled(true);
        tnc_id.loadUrl("file:////android_asset/"+file_name);



    }

}