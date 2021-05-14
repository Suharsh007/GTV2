package in.goodthought.GoodThought;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MarkedList extends AppCompatActivity implements recyclerv.onItemClickListener {
    private recyclerv adapter;
    private RecyclerView recyclerView;
    private static ArrayList<PodcastModel> podcastList = new ArrayList<>();
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;

    private NavigationView navigationView;
    private Button signout,tryAgain;
    private FirebaseUser user;





    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference podcastDB = db.collection("podcast");




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
        setContentView(R.layout.activity_marked_list);

        user = FirebaseAuth.getInstance().getCurrentUser();



   search=findViewById(R.id.search_3);

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
                    Toast.makeText(MarkedList.this, "Internet Available", Toast.LENGTH_SHORT).show();
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








        Toolbar toolbar = findViewById(R.id.toolbar_ML);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        signout = findViewById(R.id.signout_button_ML);
        drawerLayout = findViewById(R.id.drawerLayout_ML);
        toggle = new ActionBarDrawerToggle(MarkedList.this, drawerLayout, R.string.Drawer_open, R.string.Drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black,MarkedList.this.getTheme()));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nv_ML);
        recyclerView = findViewById(R.id.recyclerview_ML);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        itext = findViewById(R.id.iu_ML);
        tryAgain =findViewById(R.id.tryAgainButton_ML);





        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.myAccount_id: startActivity(new Intent(MarkedList.this, myAccount.class));
                        MarkedList.this.finish();
                        break;
                    case R.id.contactUs: startActivity(new Intent(MarkedList.this, contact_activity.class));
                        MarkedList.this.finish();
                        break;
                    case R.id.podcasts: startActivity(new Intent(MarkedList.this, podcast_Activity.class));
                        MarkedList.this.finish();
                        break;
                    case R.id.tnc:

                        startActivity(new Intent( MarkedList.this,terms_and_conditions.class));
                        break;
                    case R.id.privacyPolicy:

                        startActivity(new Intent(MarkedList.this,privacy_policy.class));
                        break;
                    case R.id.markedList:
                        startActivity(new Intent(MarkedList.this, MarkedList.class));
                        MarkedList.this.finish();
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
                Intent i = new Intent(MarkedList.this,SignIn_Activity.class);
                startActivity(i);
                MarkedList.this.finish();
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

    public static ArrayList<PodcastModel> getMLList()
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
            Intent intent = new Intent(MarkedList.this, podcastPlayer.class);
            intent.putExtra("indexML",position);
            intent.putExtra("ML","ML");
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

    public void MLLoad(View view) {
        if(firstCheck) {
            Intent load = new Intent(MarkedList.this, MarkedList.class);
            startActivity(load);

            MarkedList.this.finish();
        }
    }

    public void listPosterLoad(){
        if(firstCheck) {

            recyclerView.setVisibility(View.VISIBLE);
            podcastList.clear();
            podcastDB.orderBy("orderBy", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        final boolean[] check = {false};


                        final ArrayList<QueryDocumentSnapshot> d=new ArrayList<>();
                        QuerySnapshot allPodcastSnapshots = queryDocumentSnapshots;




                        db.collection("UserDetails").whereEqualTo("personUid",user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                if (error==null){
                                    if (value!=null){
                                        if (!value.isEmpty()) {
                                            for (QueryDocumentSnapshot documentSnapshot:value) {


                                                d.add(documentSnapshot);

                                                break;
                                            }
                                            if (!check[0]){



                                                db.collection("UserDetails").document(d.get(0).getId()).collection("MarkedList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        check[0]=true;
                                                        ArrayList<String> Ids = new ArrayList<>();

                                                        for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots){

                                                            Ids.add(queryDocumentSnapshot.getId());




                                                        }
                                                        if (allPodcastSnapshots != null && allPodcastSnapshots.size() > 0) {
                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : allPodcastSnapshots) {
                                                                for(int i =0;i<Ids.size();i++){
                                                                    if(queryDocumentSnapshot.getId().equals(Ids.get(i))){
                                                                        PodcastModel m = queryDocumentSnapshot.toObject(PodcastModel.class);

                                                                        podcastList.add(m);
                                                                        Ids.remove(i); //check
                                                                        break;
                                                                    }
                                                                }

                                                            }

                                                            setAdapter(podcastList);
                                                            if (listState != null) {
                                                                Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
                                                            }

                                                        }
                                                        d.clear();
                                                    }
                                                });

                                            }
                                        }
                                    }
                                }
                            }
                        });



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MarkedList.this, "Problem occurred", Toast.LENGTH_SHORT).show();
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