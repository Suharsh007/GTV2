package in.goodthought.GoodThought;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
public Boolean startCheck = false;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startCheck = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.btnColor, this.getTheme()));


        }



       Thread thread = new Thread()
        {
            @Override
            public void run() {
                try {

                    sleep(2000);

                }
                catch (Exception e)
                {

                }
                finally {
                    if(startCheck)
                    {
                        finish();
                    }
                    else
                    {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                        Intent i1 = new Intent(MainActivity.this,podcast_Activity.class);
                        startActivity(i1);

                        finish();





                    }else {

                        Intent i2 = new Intent(MainActivity.this,SignIn_Activity.class);
                        startActivity(i2);

                        finish();

                    }



                }
            }}
        };
        thread.start();

    }
    @Override
    protected void onPause() {
        super.onPause();
        startCheck=true;
        finish();
    }

}