package it.uniba.dib.sms2324_4.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.dib.sms2324_4.Bambini;
import it.uniba.dib.sms2324_4.Figli;
import it.uniba.dib.sms2324_4.MyAdapter;
import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.SessionManagement;
import it.uniba.dib.sms2324_4.Skin;
import it.uniba.dib.sms2324_4.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SESSION_KEY = "key";

    // TODO: Rename and change types of parameters
    private String userID;

    TextView childSelection;

    public Home() {
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
    public static Home newInstance(String userKey) {
        Home fragment = new Home();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SessionManagement sessionManagement = new SessionManagement(requireContext());
        RecyclerView recyclerView;
        MyAdapter myAdapter;
        ArrayList<Figli> list;

        recyclerView = view.findViewById(R.id.userList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(requireContext(),list,container,getParentFragmentManager(),userID);
        recyclerView.setAdapter(myAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
        Query childExistant = database.getReference("Utenti")
                .child("Genitori")
                .child(sessionManagement.getSession())
                .child("Bambini");


        // GESTIONE PULSANTE BACK
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(requireContext(), User.class);
                requireActivity().finish();
            }
        };

        // Aggiungi il callback al gestore dei pressioni del pulsante "back"
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);



        childExistant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String nome, cognome , cf , dataDiNascita  , cfLogopedista;
                    int esperienza , monete;
                    Skin skin = new Skin();

                    nome = dataSnapshot.child("nome").getValue().toString();
                    cognome = dataSnapshot.child("cognome").getValue().toString();
                    cf = dataSnapshot.child("cf").getValue().toString();
                    dataDiNascita = dataSnapshot.child("dataDiNascita").getValue().toString();
                    cfLogopedista =  dataSnapshot.child("cfLogopedista").getValue().toString();
                    esperienza =  dataSnapshot.child("esperienza").getValue(Integer.class);
                    monete =  dataSnapshot.child("monete").getValue(Integer.class);
                    /*
                    skin.setCosto(dataSnapshot.child("Skin").child("id_skin_base").child("costo").getValue(Integer.class));
                    skin.setIdSkin(dataSnapshot.child("Skin").child("id_skin_base").child("id_skin").getValue(String.class));
                     */

                    Figli figli = new Figli(nome , cognome , cf , dataDiNascita , cfLogopedista,esperienza,monete);
                    list.add(figli);


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