package in.goodthought.GoodThought;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

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