package it.uniba.dib.sms2324_4.gioco.ui.classifica;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
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
import java.util.HashMap;
import java.util.Iterator;

import it.uniba.dib.sms2324_4.Paziente;
import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.gioco.ui.gioco.GiocoFragment;

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

    int esperienza = 0;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");


    private int index = 0;
    private ArrayList<Paziente> pazientes = new ArrayList<>();


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
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayout);

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
                index = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Paziente paziente = dataSnapshot.getValue(Paziente.class);
                    if(index+1 == 1){
                        // Trova il TextView del primo posto
                        TextView scoreTextView1 = rootView.findViewById(R.id.primo_posto);
                        // Imposta il testo del primo posto

                         Query getEsperienza = database.getReference("Utenti")
                                .child("Genitori")
                                .child(paziente.getCfGenitore())
                                .child("Bambini")
                                .child(paziente.getCf());
                        getEsperienza.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                esperienza = snapshot.child("esperienza").getValue(Integer.class);
                                scoreTextView1.setText(paziente.getNome() +
                                        paziente.getCognome() +
                                        " ( " + esperienza + ")");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });


                    }else if(index+1 == 2){
                        // Trova il TextView del secondo posto
                        TextView scoreTextView2 = rootView.findViewById(R.id.secondo_posto);
                        // Imposta il testo del secondo posto
                        Query getEsperienza = database.getReference("Utenti")
                                .child("Genitori")
                                .child(paziente.getCfGenitore())
                                .child("Bambini")
                                .child(paziente.getCf());
                        getEsperienza.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                esperienza = snapshot.child("esperienza").getValue(Integer.class);
                                scoreTextView2.setText(paziente.getNome() +
                                        paziente.getCognome() +
                                        " ( " + esperienza + ")");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else if (index+1 == 3) {
                        // Trova il TextView del terzo posto
                        TextView scoreTextView3 = rootView.findViewById(R.id.terzo_posto);
                        // Imposta il testo del terzo posto
                        scoreTextView3.setText(snapshot.child("nome").getValue(String.class) +
                                snapshot.child("cognome").getValue(String.class) +
                                " ( " + snapshot.child("esperienza").getValue(Integer.class) + ")");
                    }else{
                        // Crea una nuova riga
                        TableRow row;

                        // Aggiungi la cella alla riga
                        row = new TableRow(getActivity());


                        // Aggiungi la riga al TableLayout
                        tableLayout.addView(row);

                        // Crea una nuova cella utilizzando la funzione createCellView
                        View cellView = createCellView((index + 1) + ") " + snapshot.child("nome").getValue(String.class) +
                                snapshot.child("cognome").getValue(String.class) +
                                "                               " + snapshot.child("punteggio").getValue(Integer.class));


                        // Aggiungi la cella alla riga
                        row = new TableRow(getActivity());
                        row.addView(cellView);

                        // Aggiungi la riga al TableLayout
                        tableLayout.addView(row);
                    }
                    index++;
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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