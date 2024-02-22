package it.uniba.dib.sms2324_4.logopedista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.accesso.Login_Logopedista;
import it.uniba.dib.sms2324_4.accesso.SessionManagement;
import it.uniba.dib.sms2324_4.logopedista.menu.Prenotazioni_Logopedista;
import it.uniba.dib.sms2324_4.logopedista.menu.RegistraPrenotazioni;
import it.uniba.dib.sms2324_4.logopedista.menu.ElencoEsercizi;
import it.uniba.dib.sms2324_4.logopedista.menu.ElencoPazienti;

public class User_Logopedista extends AppCompatActivity {

    //Dichiarazione Oggetti Grafici del Layout
    TextView nameView , emailView;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;


    SessionManagement sessionManagement;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(User_Logopedista.this);

        setContentView(R.layout.activity_user_logopedista);

        FirebaseApp.initializeApp(this);
        //Istanza delle variabili
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.logopedistaDrawer);
        navigationView = findViewById(R.id.menuView_logopedista);
        toolbar = findViewById(R.id.toolbar_logopedista);

        View headerView = navigationView.getHeaderView(0);

        nameView = headerView.findViewById(R.id.nameView);
        emailView = headerView.findViewById(R.id.emailView);

        database = FirebaseDatabase.getInstance(getString(R.string.db_url));

        Query getDataFromSession = database.getReference("Utenti").child("Logopedisti");
        getDataFromSession.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("cf").getValue().toString().compareTo(sessionManagement.getSession()) == 0){
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
        fragmentTransaction.replace(R.id.menu_logopedista_viewer , ElencoPazienti.newInstance(sessionManagement.getSession()));
        fragmentTransaction.commit();

        //Menu di Navigazione
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_logout_logo){
                    sessionManagement.removeSession();
                    Toast.makeText(User_Logopedista.this ,
                            getString(R.string.arrivederci) + sessionManagement.getNome(),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext() , Login_Logopedista.class);
                    startActivity(intent);
                    finish();
                    return  true;
                }else if(item.getItemId() == R.id.nav_calendar_logo){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.menu_logopedista_viewer , Prenotazioni_Logopedista.newInstance(sessionManagement.getSession()));
                    fragmentTransaction.commit();
                }else if(item.getItemId() == R.id.nav_elenco_pazienti){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.menu_logopedista_viewer , ElencoPazienti.newInstance(sessionManagement.getSession()));
                    fragmentTransaction.commit();
                }else if(item.getItemId() == R.id.nav_elenco_esercizi){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.menu_logopedista_viewer , ElencoEsercizi.newInstance(sessionManagement.getSession()));
                    fragmentTransaction.commit();
                }
                return false;
            }
        });
    }
}