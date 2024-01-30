package it.uniba.dib.sms2324_4.creazione.esercizi;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.fragment.Home;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link creaEsercizio1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class creaEsercizio2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SESSION_KEY = "session key";

    // TODO: Rename and change types of parameters
    private String userID;

    Esercizio2 esercizio2 = new Esercizio2();
    ImageView image_viewer;

    String uriImage = null;


    public creaEsercizio2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment creaEsercizio1.
     */
    // TODO: Rename and change types and number of parameters
    public static creaEsercizio2 newInstance(String userID) {
        creaEsercizio2 fragment = new creaEsercizio2();
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
        View v = inflater.inflate(R.layout.fragment_crea_esercizio2, container, false);
        EditText frase1 = (EditText) v.findViewById(R.id.frase1);
        EditText frase2 = (EditText) v.findViewById(R.id.frase2);
        EditText frase3 = (EditText) v.findViewById(R.id.frase3);
        EditText id_eserczio2 = (EditText) v.findViewById(R.id.id_esercizio2);
        Button annulla = (Button) v.findViewById(R.id.annulla_btn_2);
        Button creazione_esercizio = (Button) v.findViewById(R.id.crea_esercizio_2);


        creazione_esercizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definisci la regex per consentire solo caratteri alfanumerici e spazi
                String regex = "^[a-zA-Z0-9]*$";

                // Crea un oggetto Pattern
                Pattern pattern = Pattern.compile(regex);

                // Crea un oggetto Matcher
                Matcher matcher = pattern.matcher(id_eserczio2.getText().toString());

                if (TextUtils.isEmpty(frase1.getText().toString()) ||
                        TextUtils.isEmpty(frase2.getText().toString()) ||
                        TextUtils.isEmpty(frase3.getText().toString())) {
                    Toast.makeText(getContext(), "Inserisci 3 Frasi", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(id_eserczio2.getText().toString()) ||
                        !matcher.matches()) {
                    Toast.makeText(v.getContext(), "Inserisci un ID ESERCIZIO VALIDO", Toast.LENGTH_SHORT).show();
                } else {
                    esercizio2.setFrase_1(frase1.getText().toString());
                    esercizio2.setFrase_2(frase2.getText().toString());
                    esercizio2.setFrase_3(frase3.getText().toString());
                    esercizio2.setAudio_soluzione("null");
                    esercizio2.setId_esercizio("2_" + id_eserczio2.getText().toString());

                    FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
                    Query exercise = database.getReference("Utenti")
                            .child("Logopedisti")
                            .child(userID)
                            .child("Esercizi")
                            .child(esercizio2.getId_esercizio());
                    exercise.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(v.getContext(), "Nome Esercizio Già usato", Toast.LENGTH_SHORT).show();
                            } else {
                                database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(userID)
                                        .child("Esercizi")
                                        .child(esercizio2.getId_esercizio())
                                        .setValue(esercizio2);
                                Toast.makeText(v.getContext(), "Esercizio Creato", Toast.LENGTH_SHORT).show();
                                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                fragmentTransaction.replace(container.getId(), CreaEsercizi.newInstance(userID));
                                fragmentTransaction.commit();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(container.getId(), CreaEsercizi.newInstance(userID));
                fragmentTransaction.commit();
            }
        });

        return v;
    }
}