package it.uniba.dib.sms2324_4.logopedista.menu;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.logopedista.adapter.MyAdapter_Appuntamenti;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Appuntamenti_Logopedista#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Appuntamenti_Logopedista extends Fragment {

    private static final String SESSION_KEY = "SESSION_KEY";
    private String sessionKey;

    private Calendar calendar = Calendar.getInstance();

    private RecyclerView recyclerView;
    private Button add_reservation;
    private MyAdapter_Appuntamenti myAdapter;
    private ArrayList<String> list;
    private ViewGroup container;


    public Appuntamenti_Logopedista() {
        // Required empty public constructor
    }

    public static Appuntamenti_Logopedista newInstance(String sessionKey) {
        Appuntamenti_Logopedista fragment = new Appuntamenti_Logopedista();
        Bundle args = new Bundle();
        args.putString(SESSION_KEY, sessionKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sessionKey = getArguments().getString(SESSION_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));

        // GESTIONE PULSANTE BACK
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(container.getId() , ElencoPazienti.newInstance(sessionKey))
                        .commit();
            }
        };

        // Aggiungi il callback al gestore dei pressioni del pulsante "back"
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);

        Query reservationExistant = database.getReference("Utenti")
                .child("Logopedisti")
                .child(sessionKey)
                .child("Appuntamenti");
        reservationExistant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Query reservationExistant = database.getReference("Utenti")
                            .child("Logopedisti")
                            .child(sessionKey)
                            .child("Appuntamenti");
                    reservationExistant.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String id_esercizio = null;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                list.add(dataSnapshot.getKey());
                            }
                            myAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(container.getId() , RegistraAppuntamenti.newInstance(sessionKey))
                        .commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prenotazioni_logopedista, container, false);

        recyclerView = view.findViewById(R.id.reservationList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        list = new ArrayList<>();
        myAdapter = new MyAdapter_Appuntamenti(view.getContext(),list,sessionKey,getParentFragmentManager(),container);
        recyclerView.setAdapter(myAdapter);
        add_reservation = view.findViewById(R.id.add_reservation);

        this.container = container;

        return view;
    }
}