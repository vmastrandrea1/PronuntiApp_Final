package it.uniba.dib.sms2324_4;

import static android.content.ContentValues.TAG;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;

import it.uniba.dib.sms2324_4.fragment.Calendario;
import it.uniba.dib.sms2324_4.fragment.Home;
import it.uniba.dib.sms2324_4.fragment.RegistrazioneBambini;


public class User extends AppCompatActivity {

    //Dichiarazione Oggetti Grafici del Layout
    TextView nameView, emailView;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;


    SessionManagement sessionManagement;
    FirebaseDatabase database;

    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(User.this);

        setContentView(R.layout.activity_user);

        FirebaseApp.initializeApp(this);
        //Istanza delle variabili
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.userDrawer);
        navigationView = findViewById(R.id.menuView);
        toolbar = findViewById(R.id.toolbar);

        View headerView = navigationView.getHeaderView(0);

        nameView = headerView.findViewById(R.id.nameView);
        emailView = headerView.findViewById(R.id.emailView);

        database = FirebaseDatabase.getInstance(getString(R.string.db_url));

        Query getDataFromSession = database.getReference("Utenti")
                .child("Genitori");
        getDataFromSession.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("cf").getValue().toString().compareTo(sessionManagement.getSession()) == 0) {
                        String nome = dataSnapshot.child("nome").getValue().toString();
                        String cognome = dataSnapshot.child("cognome").getValue().toString();
                        String cf = dataSnapshot.child("cf").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String password = dataSnapshot.child("password").getValue().toString();

                        nameView.setText(nome + " " + cognome);
                        emailView.setText(email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.nav_open , R.string.nav_close );
       drawerLayout.addDrawerListener(toggle);
       toggle.syncState();

       FragmentManager fragmentManager = getSupportFragmentManager();
       FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       fragmentTransaction.replace(R.id.menu_viewer , Home.newInstance(sessionManagement.getSession()));
       fragmentTransaction.commit();

       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               if(item.getItemId() == R.id.nav_logout){
                   sessionManagement.removeSession();
                   Toast.makeText(User.this,
                           getString(R.string.arrivederci) + sessionManagement.getNome(),
                           Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(getApplicationContext() , Login.class);
                   startActivity(intent);
                   finish();
                   return  true;
               }else if(item.getItemId() == R.id.nav_calendar){
                   drawerLayout.closeDrawer(GravityCompat.START);
                   FragmentManager fragmentManager = getSupportFragmentManager();
                   FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                   fragmentTransaction.replace(R.id.menu_viewer , new Calendario());
                   fragmentTransaction.commit();
               }else if(item.getItemId() == R.id.nav_home){
                   drawerLayout.closeDrawer(GravityCompat.START);
                   FragmentManager fragmentManager = getSupportFragmentManager();
                   FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                   fragmentTransaction.replace(R.id.menu_viewer , Home.newInstance(sessionManagement.getSession()));
                   fragmentTransaction.commit();
               }

               return false;
           }
       });
   }
}