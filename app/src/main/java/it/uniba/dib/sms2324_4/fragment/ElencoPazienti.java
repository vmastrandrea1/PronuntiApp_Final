package it.uniba.dib.sms2324_4.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.dib.sms2324_4.MyAdapter_Pazienti;
import it.uniba.dib.sms2324_4.Paziente;
import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.SessionManagement;
import it.uniba.dib.sms2324_4.creazione.esercizi.creaEsercizio1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ElencoPazienti extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SESSION_KEY = "key";

    // TODO: Rename and change types of parameters
    private String userID;

    TextView childSelection;

    public ElencoPazienti() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userKey Parameter 1.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static ElencoPazienti newInstance(String userKey) {
        ElencoPazienti fragment = new ElencoPazienti();
        Bundle args = new Bundle();
        args.putString(SESSION_KEY, userKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(SESSION_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_elenco_pazienti, container, false);

        // Ottieni il riferimento al pulsante
        Button buttonRegistraPaziente = view.findViewById(R.id.AddButton);

        // Gestisci il click sul pulsante
        buttonRegistraPaziente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(container.getId() , RegistrazioneBambini.newInstance(userID));
                    fragmentTransaction.commit();
                }
            });

        SessionManagement sessionManagement = new SessionManagement(requireContext());
        RecyclerView recyclerView;
        MyAdapter_Pazienti myAdapter;
        ArrayList<Paziente> list;

        recyclerView = view.findViewById(R.id.elencoPazienti);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        list = new ArrayList<>();
        myAdapter = new MyAdapter_Pazienti(requireContext(),list,userID,container,getParentFragmentManager());
        recyclerView.setAdapter(myAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
        Query childExistant = database.getReference("Utenti")
                .child("Logopedisti")
                .child(sessionManagement.getSession())
                .child("Pazienti");
        childExistant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nome, cognome , cf , dataDiNascita,cfGenitore;
                int esperienza;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    nome = dataSnapshot.child("nome").getValue().toString();
                    cognome = dataSnapshot.child("cognome").getValue().toString();
                    cf = dataSnapshot.child("cf").getValue().toString();
                    dataDiNascita = dataSnapshot.child("dataDiNascita").getValue().toString();
                    cfGenitore = dataSnapshot.child("cfGenitore").getValue().toString();
                    esperienza = dataSnapshot.child("esperienza").getValue(Integer.class);

                    Paziente paziente = new Paziente(nome , cognome , cf , dataDiNascita , cfGenitore , esperienza);
                    list.add(paziente);


                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return  view;
    }
}