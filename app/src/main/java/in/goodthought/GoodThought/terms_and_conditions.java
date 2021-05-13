package in.goodthought.GoodThought;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
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