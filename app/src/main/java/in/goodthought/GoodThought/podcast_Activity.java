package in.goodthought.GoodThought;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class podcast_Activity extends AppCompatActivity implements recyclerv.onItemClickListener {
    private recyclerv adapter;
    private RecyclerView recyclerView;
    private static ArrayList<PodcastModel> podcastList = new ArrayList<>();
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;

    private NavigationView navigationView;
    private Button signout,tryAgain;





    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference podcastDB = db.collection("podcast");

    private ImageView podcastImage;


    private Parcelable listState;
    private Boolean iCheck =true;
    private boolean firstCheck =false;
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback networkCallback;
    private Source source = Source.SERVER;
    private TextView itext;
    private boolean hasRestarted=false;

    private EditText search;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

        if(Build.VERSION.SDK_INT >=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.btnColor,this.getTheme()));
        }

        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback =new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                firstCheck=true;
                if(!iCheck){
                    Toast.makeText(podcast_Activity.this, "Internet Available", Toast.LENGTH_SHORT).show();
                    iCheck=true;
                }

            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                firstCheck=false;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(tryAgain.getVisibility() != View.VISIBLE){
                            recyclerView.setVisibility(View.GONE);
                            itext.setVisibility(View.VISIBLE);
                            tryAgain.setVisibility(View.VISIBLE);}
                    }
                });
            }
        };
        cm.registerDefaultNetworkCallback(networkCallback);








        Toolbar toolbar = findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        signout = findViewById(R.id.signout_button);
        drawerLayout = findViewById(R.id.drawerLayout_ULE);
        toggle = new ActionBarDrawerToggle(podcast_Activity.this, drawerLayout, R.string.Drawer_open, R.string.Drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nv_2);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        podcastImage = findViewById(R.id.imageView);
        itext = findViewById(R.id.iu);
        tryAgain =findViewById(R.id.tryAgainButton);

        search=findViewById(R.id.search_2);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              startSearch(charSequence.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.myAccount_id: startActivity(new Intent(podcast_Activity.this, myAccount.class));
                        podcast_Activity.this.finish();
                        break;
                    case R.id.contactUs: startActivity(new Intent(podcast_Activity.this, contact_activity.class));
                        podcast_Activity.this.finish();
                        break;
                    case R.id.podcasts: startActivity(new Intent(podcast_Activity.this, podcast_Activity.class));
                        podcast_Activity.this.finish();
                        break;
                    case R.id.tnc:
                      /*  Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                        openURL.setData(Uri.parse("https://decib.in/terms-and-conditions/"));
                        startActivity(openURL);
                        hasRestarted=true;*/
                        startActivity(new Intent(podcast_Activity.this,terms_and_conditions.class));
                        break;
                    case R.id.privacyPolicy:
                      /*  Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("https://decib.in/privacy-policy/"));
                        startActivity(intent);
                        hasRestarted=true;*/
                        startActivity(new Intent(podcast_Activity.this,privacy_policy.class));
                        break;
                    case R.id.markedList:
                        startActivity(new Intent(podcast_Activity.this, MarkedList.class));
                        podcast_Activity.this.finish();
                        break;
                }
                return false;
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth auth=FirebaseAuth.getInstance();
                auth.signOut();
                Intent i = new Intent(podcast_Activity.this,SignIn_Activity.class);
                startActivity(i);
                podcast_Activity.this.finish();
            }
        });

        db.collection("Notice").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                    String displayNotice = (String) snapshot.get("notice");
                    if(displayNotice!=null){
                        if(!displayNotice.isEmpty()){
                            Snackbar.make(podcast_Activity.this,podcast_Activity.this.findViewById(R.id.imageView),displayNotice,10000).show();


                        }
                    }
                }
            }
        });

    }

    private void startSearch(String searchText) {
        ArrayList<PodcastModel> searchList = new ArrayList<>();
        if(podcastList!=null){
            if(!podcastList.isEmpty()){
                for(PodcastModel podcast:podcastList){
                    if(podcast.getSpeakerName().toLowerCase().contains(searchText) || podcast.getAboutTopic().toLowerCase().contains(searchText) || podcast.getTopic().toLowerCase().contains(searchText) || podcast.getDate().toLowerCase().contains(searchText)){
                         searchList.add(podcast);
                    }
                }
                setAdapter(searchList);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();


        listPosterLoad();


    }

    private void setAdapter(List<PodcastModel> pL) {
        adapter = new recyclerv(pL, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static ArrayList<PodcastModel> getPodcastList()
    {
        return podcastList;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClick(int position) {
        if(firstCheck){
            Intent intent = new Intent(podcast_Activity.this, podcastPlayer.class);
            intent.putExtra("index",position);
            listState= Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
            startActivity(intent);
            hasRestarted=true;
        }


    }



    @Override
    protected void onPause() {
        super.onPause();
        if(cm!=null && networkCallback!=null && !hasRestarted ){
            try{
                cm.unregisterNetworkCallback(networkCallback);}catch (Exception e){


            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if(cm!=null && networkCallback!=null){
            try{
                cm.unregisterNetworkCallback(networkCallback);}catch (Exception e){

            }
        }


    }

    public void podcastLoad(View view) {
        if(firstCheck) {
            Intent load = new Intent(podcast_Activity.this, podcast_Activity.class);
            startActivity(load);

            podcast_Activity.this.finish();
        }
    }

    public void listPosterLoad(){
        if(firstCheck) {

            recyclerView.setVisibility(View.VISIBLE);
            db.collection("podcastImage").document("Image").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String url = "";
                    if ((url = (String) documentSnapshot.get("imageUrl")) != null) {
                        if (!url.isEmpty())
                            Picasso.get().load(url).fit().into(podcastImage);
                    }
                }
            });

            podcastList.clear();
            podcastDB.orderBy("orderBy", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            PodcastModel m = queryDocumentSnapshot.toObject(PodcastModel.class);

                            podcastList.add(m);
                        }

                        setAdapter(podcastList);
                        if (listState != null) {
                            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(podcast_Activity.this, "Problem occurred", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            if(tryAgain.getVisibility() != View.VISIBLE){
                recyclerView.setVisibility(View.GONE);
                itext.setVisibility(View.VISIBLE);
                tryAgain.setVisibility(View.VISIBLE);}
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hasRestarted=false;

    }


}