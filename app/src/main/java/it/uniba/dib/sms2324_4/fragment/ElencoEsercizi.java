package it.uniba.dib.sms2324_4.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import it.uniba.dib.sms2324_4.MyAdapter_Esercizi;
import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.SessionManagement;
import it.uniba.dib.sms2324_4.creazione.esercizi.CreaEsercizi;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio1;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio2;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio3;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ElencoEsercizi#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ElencoEsercizi extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SESSION_KEY = "session key";

    // TODO: Rename and change types of parameters
    private String userID;

    public ElencoEsercizi() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ElencoEsercizi.
     */
    // TODO: Rename and change types and number of parameters
    public static ElencoEsercizi newInstance(String userID) {
        ElencoEsercizi fragment = new ElencoEsercizi();
        Bundle args = new Bundle();
        args.putString(SESSION_KEY, userID);
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
        View view = inflater.inflate(R.layout.fragment_elenco_esercizi, container, false);

        // Ottieni il riferimento al pulsante
        Button buttonCreaEsercizio = view.findViewById(R.id.AddButton);

        // Gestisci il click sul pulsante
        buttonCreaEsercizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(container.getId() , CreaEsercizi.newInstance(userID));
                fragmentTransaction.commit();
            }
        });

        SessionManagement sessionManagement = new SessionManagement(requireContext());
        RecyclerView recyclerView;
        MyAdapter_Esercizi myAdapter;
        ArrayList<Object> list;

        recyclerView = view.findViewById(R.id.elencoEsercizi);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        list = new ArrayList<>();
        myAdapter = new MyAdapter_Esercizi(requireContext(),list,userID,container,getParentFragmentManager());
        recyclerView.setAdapter(myAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
        Query childExistant = database.getReference("Utenti")
                .child("Logopedisti")
                .child(sessionManagement.getSession())
                .child("Esercizi");
        childExistant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("id_esercizio").getValue().toString().startsWith("1_")){
                        Esercizio1 esercizio1 = new Esercizio1();

                        esercizio1.setId_esercizio(dataSnapshot.child("id_esercizio").getValue().toString());
                        esercizio1.setAiuto_1(dataSnapshot.child("aiuto_1").getValue().toString());
                        esercizio1.setAiuto_2(dataSnapshot.child("aiuto_2").getValue().toString());
                        esercizio1.setAiuto_3(dataSnapshot.child("aiuto_3").getValue().toString());
                        esercizio1.setUriImage(dataSnapshot.child("uriImage").getValue().toString());
                        esercizio1.setMonete(dataSnapshot.child("monete").getValue(Integer.class));
                        esercizio1.setEsperienza(dataSnapshot.child("esperienza").getValue(Integer.class));

                        list.add(esercizio1);
                    }else if(dataSnapshot.child("id_esercizio").getValue().toString().startsWith("2_")){
                        Esercizio2 esercizio2 = new Esercizio2();

                        esercizio2.setId_esercizio(dataSnapshot.child("id_esercizio").getValue().toString());
                        esercizio2.setParola_1(dataSnapshot.child("parola_1").getValue().toString());
                        esercizio2.setParola_2(dataSnapshot.child("parola_2").getValue().toString());
                        esercizio2.setParola_3(dataSnapshot.child("parola_3").getValue().toString());
                        esercizio2.setMonete(dataSnapshot.child("monete").getValue(Integer.class));
                        esercizio2.setEsperienza(dataSnapshot.child("esperienza").getValue(Integer.class));

                        list.add(esercizio2);
                    }else if(dataSnapshot.child("id_esercizio").getValue().toString().startsWith("3_")){
                        Esercizio3 esercizio3 = new Esercizio3();

                        esercizio3.setId_esercizio(dataSnapshot.child("id_esercizio").getValue().toString());
                        esercizio3.setUriImage_sbagliata(dataSnapshot.child("uriImage_sbagliata").getValue().toString());
                        esercizio3.setUriImage_corretta(dataSnapshot.child("uriImage_corretta").getValue().toString());
                        esercizio3.setMonete(dataSnapshot.child("monete").getValue(Integer.class));
                        esercizio3.setEsperienza(dataSnapshot.child("esperienza").getValue(Integer.class));
                        esercizio3.setParola_immagine(dataSnapshot.child("parola_immagine").getValue().toString());

                        list.add(esercizio3);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}