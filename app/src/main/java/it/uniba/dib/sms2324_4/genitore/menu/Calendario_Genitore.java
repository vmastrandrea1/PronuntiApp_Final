package it.uniba.dib.sms2324_4.genitore.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.dib.sms2324_4.genitore.adapter.MyAdapter_Appuntamenti_Genitore;
import it.uniba.dib.sms2324_4.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Calendario_Genitore#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendario_Genitore extends Fragment {

    private static final String SESSION_KEY = "SESSION_KEY";
    private String sessionKey;


    public Calendario_Genitore() {
        // Required empty public constructor
    }

    public static Calendario_Genitore newInstance(String sessionKey) {
        Calendario_Genitore fragment = new Calendario_Genitore();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_calendario_genitore, container, false);


        // GESTIONE PULSANTE BACK
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(container.getId() , Home.newInstance(sessionKey))
                        .commit();
            }
        };

        // Aggiungi il callback al gestore dei pressioni del pulsante "back"
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);


        FirebaseDatabase database = FirebaseDatabase.getInstance(view.getContext().getString(R.string.db_url));

        RecyclerView recyclerView;
        MyAdapter_Appuntamenti_Genitore myAdapter;
        ArrayList<String> list;

        recyclerView = view.findViewById(R.id.reservations_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        list = new ArrayList<>();
        myAdapter = new MyAdapter_Appuntamenti_Genitore(view.getContext(),list,sessionKey,getParentFragmentManager(),container);
        recyclerView.setAdapter(myAdapter);

        Query reservationExistant = database.getReference("Utenti")
                .child("Genitori")
                .child(sessionKey)
                .child("Appuntamenti");
        reservationExistant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        list.add(dataSnapshot.getKey());
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}