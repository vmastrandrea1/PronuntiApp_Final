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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link creaEsercizio1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class creaEsercizio3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SESSION_KEY = "session key";

    // TODO: Rename and change types of parameters
    private String userID;

    Esercizio3 esercizio3 = new Esercizio3();
    ImageView imageView_wrong , imageView_right;
    EditText parola_da_ascoltare;
    String uriImage_wrong = null;
    String uriImage_right = null;

    Button select_imageView_wrong;
    Button select_imageView_right;
    Button annulla;
    Button creazione_esercizio;
    EditText id_esercizio3;

    ViewGroup container;


    public creaEsercizio3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment creaEsercizio1.
     */
    // TODO: Rename and change types and number of parameters
    public static creaEsercizio3 newInstance(String userID) {
        creaEsercizio3 fragment = new creaEsercizio3();
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
    public void onResume() {
        super.onResume();

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


        creazione_esercizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definisci la regex per consentire solo caratteri alfanumerici e spazi
                String regex = "^[a-zA-Z0-9]*$";

                // Crea un oggetto Pattern
                Pattern pattern = Pattern.compile(regex);

                // Crea un oggetto Matcher
                Matcher matcher = pattern.matcher(id_esercizio3.getText().toString());

                if (uriImage_wrong == null || uriImage_right == null){
                    Toast.makeText(getContext(), R.string.inserisci_le_immagini, Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(id_esercizio3.getText().toString()) ||
                        !matcher.matches()){
                    Toast.makeText(getContext(), R.string.inserisci_un_nome_valido, Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(parola_da_ascoltare.getText().toString())
                        || !pattern.matcher(parola_da_ascoltare.getText().toString()).matches()){
                    Toast.makeText(getContext(), R.string.inserisci_una_parola_valida, Toast.LENGTH_SHORT).show();
                }else{
                    esercizio3.setId_esercizio("3_" + id_esercizio3.getText().toString());
                    esercizio3.setUriImage_corretta(uriImage_right);
                    esercizio3.setUriImage_sbagliata(uriImage_wrong);
                    esercizio3.setParola_immagine(parola_da_ascoltare.getText().toString());

                    FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
                    Query exercise = database.getReference("Utenti")
                            .child("Logopedisti")
                            .child(userID)
                            .child("Esercizi")
                            .child(esercizio3.getId_esercizio());
                    exercise.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(getContext(), R.string.nome_esercizio_gia_usato, Toast.LENGTH_SHORT).show();
                            } else {
                                database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(userID)
                                        .child("Esercizi")
                                        .child(esercizio3.getId_esercizio())
                                        .setValue(esercizio3);
                                Toast.makeText(getContext(), R.string.esercizio_creato, Toast.LENGTH_SHORT).show();
                                FragmentManager fragmentManager = getParentFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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

        select_imageView_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iGallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery , 1);
            }
        });

        select_imageView_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iGallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery , 2);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crea_esercizio3, container, false);

        imageView_wrong = (ImageView) v.findViewById(R.id.image_viewer_wrong);
        imageView_right = (ImageView) v.findViewById(R.id.image_viewer_right);
        parola_da_ascoltare = (EditText) v.findViewById(R.id.parola_da_ascoltare);
        select_imageView_wrong = (Button) v.findViewById(R.id.select_image_wrong_button);
        select_imageView_right = (Button) v.findViewById(R.id.select_image_right_button);
        annulla = (Button) v.findViewById(R.id.annulla_btn_3);
        creazione_esercizio = (Button) v.findViewById(R.id.crea_esercizio_3);
        id_esercizio3 = (EditText) v.findViewById(R.id.id_esercizio3);

        this.container = container;

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
                    Toast.makeText(getContext(), R.string.immagine_non_inserita_nello_storage, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    uriImage_wrong = storageReference.getPath();
                    imageView_wrong.setImageURI(file);
                    Toast.makeText(getContext(), R.string.immagine_acquisita, Toast.LENGTH_SHORT).show();
                }
            });
        }else if(requestCode == 2 && data != null){
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
            Uri file = data.getData();
            StorageReference storageReference = storage.getReference("Immagini_Logopedista/" + file.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getContext(), R.string.immagine_non_inserita_nello_storage, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    uriImage_right = storageReference.getPath();
                    imageView_right.setImageURI(file);
                    Toast.makeText(getContext(), R.string.immagine_acquisita, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getContext(), R.string.immagine_non_acquisita, Toast.LENGTH_SHORT).show();
        }
    }
}