package it.uniba.dib.sms2324_4.creazione.esercizi;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import it.uniba.dib.sms2324_4.fragment.ElencoEsercizi;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link creaEsercizio1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class creaEsercizio1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SESSION_KEY = "session key";

    // TODO: Rename and change types of parameters
    private String userID;

    Esercizio1 esercizio1 = new Esercizio1();
    ImageView image_viewer;

    String uriImage = null;


    public creaEsercizio1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment creaEsercizio1.
     */
    // TODO: Rename and change types and number of parameters
    public static creaEsercizio1 newInstance(String userID) {
        creaEsercizio1 fragment = new creaEsercizio1();
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
        View v = inflater.inflate(R.layout.fragment_crea_esercizio1, container, false);
        EditText aiuto1=(EditText)v.findViewById(R.id.aiuto1);
        EditText aiuto2=(EditText)v.findViewById(R.id.aiuto2);
        EditText aiuto3=(EditText)v.findViewById(R.id.aiuto3);
        EditText id_eserczio1 = (EditText)v.findViewById(R.id.id_esercizio1);
        Button scegli_immagine = (Button)v.findViewById(R.id.scegliImmagine_btn);
        Button annulla = (Button)v.findViewById(R.id.annulla_btn);
        Button creazione_esercizio = (Button)v.findViewById(R.id.buttonCreaEs1);
        image_viewer = (ImageView) v.findViewById(R.id.preview_immagine_es1);


        // GESTIONE PULSANTE BACK
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(container.getId() , CreaEsercizi.newInstance(userID))
                        .commit();
            }
        };

        // Aggiungi il callback al gestore dei pressioni del pulsante "back"
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);


        scegli_immagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iGallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery , 1);
            }
        });

        creazione_esercizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definisci la regex per consentire solo caratteri alfanumerici e spazi
                String regex = "^[a-zA-Z0-9]*$";

                // Crea un oggetto Pattern
                Pattern pattern = Pattern.compile(regex);

                // Crea un oggetto Matcher
                Matcher matcher = pattern.matcher(id_eserczio1.getText().toString());

                if(TextUtils.isEmpty(aiuto1.getText().toString())){
                    Toast.makeText(getContext(), R.string.inserisci_almeno_un_aiuto, Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(aiuto2.getText().toString()) &&
                        !TextUtils.isEmpty(aiuto3.getText().toString())){
                    Toast.makeText(getContext(), R.string.aiuto_2_vuoto, Toast.LENGTH_SHORT).show();
                }else if(uriImage == null){
                    Toast.makeText(getContext(), R.string.inserisci_un_immagine, Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(id_eserczio1.getText().toString()) ||
                        !matcher.matches()){
                    Toast.makeText(v.getContext(), R.string.inserisci_un_nome_valido, Toast.LENGTH_SHORT).show();
                }else{
                    esercizio1.setAiuto_1(aiuto1.getText().toString());
                    esercizio1.setAiuto_2(aiuto2.getText().toString());
                    esercizio1.setAiuto_3(aiuto3.getText().toString());
                    esercizio1.setUriImage(uriImage);
                    esercizio1.setId_esercizio("1_" + id_eserczio1.getText().toString());

                    FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
                    Query exercise = database.getReference("Utenti")
                            .child("Logopedisti")
                            .child(userID)
                            .child("Esercizi")
                            .child(esercizio1.getId_esercizio());
                    exercise.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(v.getContext(), R.string.nome_esercizio_gia_usato, Toast.LENGTH_SHORT).show();
                            }else{
                                database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(userID)
                                        .child("Esercizi")
                                        .child(esercizio1.getId_esercizio())
                                        .setValue(esercizio1);
                                Toast.makeText(v.getContext(), R.string.esercizio_creato, Toast.LENGTH_SHORT).show();
                                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                                fragmentTransaction.replace(container.getId() , CreaEsercizi.newInstance(userID));
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
                fragmentTransaction.replace(container.getId() , CreaEsercizi.newInstance(userID));
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && data != null){
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
            Uri file = data.getData();
            StorageReference storageReference = storage.getReference("Immagini_Logopedista/" + file.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(requireContext(), R.string.immagine_non_inserita_nello_storage, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    uriImage = storageReference.getPath();
                    image_viewer.setImageURI(file);
                    Toast.makeText(getContext(), R.string.immagine_acquisita, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(requireContext(), R.string.immagine_non_acquisita, Toast.LENGTH_SHORT).show();
        }
    }
}