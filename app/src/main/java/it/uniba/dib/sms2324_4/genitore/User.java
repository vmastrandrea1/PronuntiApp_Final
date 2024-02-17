package it.uniba.dib.sms2324_4.genitore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.uniba.dib.sms2324_4.accesso.Login;
import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.accesso.SessionManagement;
import it.uniba.dib.sms2324_4.genitore.menu.Calendario_Genitore;
import it.uniba.dib.sms2324_4.genitore.menu.Home;


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
                    fragmentTransaction.replace(R.id.menu_viewer , Calendario_Genitore.newInstance(sessionManagement.getSession()));
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

    @Override
    public void onBackPressed() {
        // Ottieni il fragment attualmente visualizzato
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.menu_viewer);

        // Verifica se il fragment attuale è il fragment Home
        if (currentFragment instanceof Home) {
            // Chiudi l'applicazione
            finish();
        } else {
            // Altrimenti, esegui l'azione di default (tornare al fragment precedente o chiudere l'activity)
            super.onBackPressed();
        }
    }


}