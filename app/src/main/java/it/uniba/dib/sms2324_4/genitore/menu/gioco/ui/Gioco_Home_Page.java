package it.uniba.dib.sms2324_4.genitore.menu.gioco.ui;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.classifica.ClassificaFragment;
import it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.gioco.GiocoFragment;
import it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.gioco.NoTherapyFragment;
import it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.gioco.TherapyFinishedFragment;
import it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.shop.ShopFragment;

public class Gioco_Home_Page extends Fragment {

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOPOPEDISTA";
    private String id_bambino;
    private String sessionKey;
    private String id_logopedista;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

    public Gioco_Home_Page() {
        // Required empty public constructor
    }

    public static Gioco_Home_Page newInstance(String id_bambino, String sessionKey, String id_logopedista) {
        Gioco_Home_Page fragment = new Gioco_Home_Page();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY, sessionKey);
        args.putString(ID_LOGOPEDISTA, id_logopedista);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_bambino = getArguments().getString(BAMBINO_ID);
            sessionKey = getArguments().getString(SESSION_KEY);
            id_logopedista = getArguments().getString(ID_LOGOPEDISTA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gioco__home__page, container, false);
        FragmentManager fragmentManager_gioco = requireActivity().getSupportFragmentManager();

        // Verifica se ci sono terapie da effettuare
        Query childExistant = database.getReference("Utenti")
                .child("Logopedisti")
                .child(id_logopedista)
                .child("Pazienti")
                .child(id_bambino)
                .child("Terapie")
                .child(getData());

        childExistant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean terapiePresenti = snapshot.exists();

                if (!terapiePresenti) {
                    // Se non ci sono terapie, mostra un fragment con il messaggio appropriato
                    showNoTherapyFragment();
                }else {
                    Query terapiaEseguita = database.getReference("Utenti")
                            .child("Logopedisti")
                            .child(id_logopedista)
                            .child("Pazienti")
                            .child(id_bambino)
                            .child("Terapie")
                            .child(getData())
                            .child("terpia_completata");
                    terapiaEseguita.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                showTherapyFinishedFragment();
                            }else{
                                showGioco();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gestisci eventuali errori di lettura dal database
            }
        });

        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottomBar);

        // Imposta il gioco selezionato di default
        bottomNavigationView.setSelectedItemId(R.id.gioco);



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.shop) {
                    ShopFragment fragmentShop = ShopFragment.newInstance(id_bambino, sessionKey, id_logopedista);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragmentContainer, fragmentShop)
                            .addToBackStack(null)
                            .commit();
                } else if (itemId == R.id.gioco) {
                    // Verifica se ci sono terapie da effettuare
                    Query childExistant = database.getReference("Utenti")
                            .child("Logopedisti")
                            .child(id_logopedista)
                            .child("Pazienti")
                            .child(id_bambino)
                            .child("Terapie")
                            .child(getData());

                    childExistant.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean terapiePresenti = snapshot.exists();

                            if (!terapiePresenti) {
                                // Se non ci sono terapie, mostra un fragment con il messaggio appropriato
                                showNoTherapyFragment();
                            }else{
                                Query terapiaEseguita = database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(id_logopedista)
                                        .child("Pazienti")
                                        .child(id_bambino)
                                        .child("Terapie")
                                        .child(getData())
                                        .child("terpia_completata");
                                terapiaEseguita.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            showTherapyFinishedFragment();
                                        }else{
                                            showGioco();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Gestisci eventuali errori di lettura dal database
                        }
                    });
                } else if (itemId == R.id.classifica) {
                    ClassificaFragment fragmentClassifica = ClassificaFragment.newInstance(id_bambino , sessionKey , id_logopedista);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragmentContainer, fragmentClassifica)
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getActivity() != null && getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    BottomNavigationView bottomNavigationView = requireView().findViewById(R.id.bottomBar);
                    bottomNavigationView.setSelectedItemId(R.id.gioco); // torna a "Gioco" quando si preme back
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .commit();
                } else {
                    requireActivity().finish(); // Se non ci sono fragment nello stack, esci dall'attività
                }
            }
        });
    }


    private void showNoTherapyFragment() {
        NoTherapyFragment noTherapyFragment = new NoTherapyFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, noTherapyFragment)
                .addToBackStack(null)
                .commit();
    }


    private void showGioco() {
        GiocoFragment fragmentGioco = GiocoFragment.newInstance(id_bambino, sessionKey, id_logopedista);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragmentGioco)
                .addToBackStack(null)
                .commit();
    }


    private void showTherapyFinishedFragment() {
        TherapyFinishedFragment therapyFinishedFragment = new TherapyFinishedFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, therapyFinishedFragment)
                .addToBackStack(null)
                .commit();
    }

    private String getData() {
        DateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);      // Imposta le ore a 0
        cal.set(Calendar.MINUTE, 0);            // Imposta i minuti a 0
        cal.set(Calendar.SECOND, 0);            // Imposta i secondi a 0
        cal.set(Calendar.MILLISECOND, 0);
        return formatoData.format(cal.getTime());
    }
}
