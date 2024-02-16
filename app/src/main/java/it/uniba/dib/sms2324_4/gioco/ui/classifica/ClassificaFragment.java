package it.uniba.dib.sms2324_4.gioco.ui.classifica;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.uniba.dib.sms2324_4.Paziente;
import it.uniba.dib.sms2324_4.R;

public class ClassificaFragment extends Fragment {

    public ClassificaFragment() {
        // Required empty public constructor
    }

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static  final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOGOPEDISTA";
    private String id_bambino;
    private String sessionKey_genitore;
    private String id_logopedista;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");


    private int index = 1;
    private Paziente paziente;


    public static ClassificaFragment newInstance(String id_bambino, String sessionKey_genitore, String id_logopedista) {
        ClassificaFragment fragment = new ClassificaFragment();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY,sessionKey_genitore);
        args.putString(ID_LOGOPEDISTA,id_logopedista);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_bambino = getArguments().getString(BAMBINO_ID);
            sessionKey_genitore = getArguments().getString(SESSION_KEY);
            id_logopedista = getArguments().getString(ID_LOGOPEDISTA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_classifica, container, false);

        // Ottieni il riferimento alla TableLayout all'interno del fragment
        ScrollView scrollView = rootView.findViewById(R.id.scrollable);
        LinearLayout tableLayout = (LinearLayout) scrollView.getChildAt(0);

        // Aggiungi del margine al TableLayout
        int tableLayoutMarginTop = 32; // Imposta il margine superiore a tuo piacimento
        ViewGroup.MarginLayoutParams tableLayoutParams = (ViewGroup.MarginLayoutParams) tableLayout.getLayoutParams();
        tableLayoutParams.topMargin = tableLayoutMarginTop;

        Query classifica = database.getReference("Utenti")
                .child("Logopedisti")
                .child(id_logopedista)
                .child("Pazienti")
                .orderByChild("esperienza");

        classifica.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                index = 1;
                List<Paziente> pazientiList = new ArrayList<>(); // Lista per mantenere i pazienti in ordine decrescente
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Paziente paziente = new Paziente();
                    paziente.setCfGenitore(dataSnapshot.child("cfGenitore").getValue(String.class));
                    paziente.setCf(dataSnapshot.child("cf").getValue(String.class));
                    paziente.setNome(dataSnapshot.child("nome").getValue(String.class));
                    paziente.setCognome(dataSnapshot.child("cognome").getValue(String.class));
                    paziente.setEsperienza(dataSnapshot.child("esperienza").getValue(Integer.class));
                    pazientiList.add(paziente);
                }

                // Ordina la lista dei pazienti in ordine decrescente
                Collections.sort(pazientiList, new Comparator<Paziente>() {
                    @Override
                    public int compare(Paziente paziente1, Paziente paziente2) {
                        return Integer.valueOf(paziente2.getEsperienza())
                                .compareTo(Integer.valueOf(paziente1.getEsperienza()));
                    }
                });

                for (Paziente paziente : pazientiList) {
                    if (index == 1) {
                        // Imposta il testo del primo posto
                        TextView scoreTextView1 = rootView.findViewById(R.id.primo_posto);
                        scoreTextView1.setText(paziente.getNome() + " " +
                                paziente.getCognome() +
                                " ( " + paziente.getEsperienza() + " )");
                    } else if (index == 2) {
                        // Imposta il testo del secondo posto
                        TextView scoreTextView2 = rootView.findViewById(R.id.secondo_posto);
                        scoreTextView2.setText(paziente.getNome() + " " +
                                paziente.getCognome() +
                                " ( " + paziente.getEsperienza() + " )");
                    } else if (index == 3) {
                        // Imposta il testo del terzo posto
                        TextView scoreTextView3 = rootView.findViewById(R.id.terzo_posto);
                        scoreTextView3.setText(paziente.getNome() + " " +
                                paziente.getCognome() +
                                " ( " + paziente.getEsperienza() + " )");
                    } else {
                        // Aggiungi la riga al TableLayout
                        TableRow row = new TableRow(getActivity());
                        tableLayout.addView(row);

                        // Crea una nuova cella utilizzando la funzione createCellView
                        View cellView = createCellView((index) + ") " + paziente.getNome() +
                                paziente.getCognome() +
                                "  " +
                                "                             " +
                                paziente.getEsperienza());
                        row.addView(cellView);
                    }
                    index++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        return rootView;
    }

    private View createCellView(String prefix) {
        // Inflate il layout della cella
        View cellView = LayoutInflater.from(getActivity()).inflate(R.layout.cella_layout, null);

        // Trova il TextView all'interno della cella
        TextView textView = cellView.findViewById(R.id.testoTextView);

        // Imposta il testo nel TextView con il prefisso e il punteggio
        textView.setText(prefix + " " );

        return cellView;
    }

}