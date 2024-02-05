package it.uniba.dib.sms2324_4.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio3;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio3;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CorrezioneEsercizio3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CorrezioneEsercizio3 extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String id_bambino;
    private String sessionKey;
    private String id_esercizio3;
    private String id_logopedista;
    private int pos_esercizio;

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ESERCIZIO_3 = "ESERCIZIO_3";
    private static final String ID_LOGOPEDISTA = "ID_LOGOPEDISTA";
    private static final String POS_ESERCIZIO = "POS_ESERCIZIO";

    private boolean isRecording = false;

    Esercizio3 esercizio3 = new Esercizio3();


    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

    public CorrezioneEsercizio3() throws IOException {
    }

    public static CorrezioneEsercizio3 newInstance(String id_bambino, String sessionKey, String id_esercizio3,
                                                   String id_logopedista , int pos_esercizio) throws IOException {
        CorrezioneEsercizio3 fragment = new CorrezioneEsercizio3();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY, sessionKey);
        args.putString(ESERCIZIO_3, id_esercizio3);
        args.putString(ID_LOGOPEDISTA, id_logopedista);
        args.putInt(POS_ESERCIZIO,pos_esercizio);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            id_bambino = getArguments().getString(BAMBINO_ID);
            sessionKey = getArguments().getString(SESSION_KEY);
            id_esercizio3 = getArguments().getString(ESERCIZIO_3);
            id_logopedista = getArguments().getString(ID_LOGOPEDISTA);
            pos_esercizio = getArguments().getInt(POS_ESERCIZIO);
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_correzione_esercizio3, null, false);

        Query getExercise = database.getReference("Utenti")
                .child("Logopedisti")
                .child(id_logopedista)
                .child("Esercizi")
                .child(id_esercizio3);

        ImageView image_correct_select_viewer = view.findViewById(R.id.image_correct_select_viewer);
        ImageView image_wrong_select_viewer = view.findViewById(R.id.image_wrong_select_viewer);

        getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                esercizio3 = snapshot.getValue(Esercizio3.class);

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                StorageReference storageReference = storage.getReference(esercizio3.getUriImage_corretta().substring(1));
                Log.d("Image_Path", "Image Path: " + storageReference.toString());

                //FETCH IMMAGINE
                try {
                    File file = File.createTempFile("tempfile", ".jpg");

                    storageReference.getFile(file)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    image_correct_select_viewer.setImageBitmap(bitmap);
                                }
                            });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                storageReference = storage.getReference(esercizio3.getUriImage_sbagliata().substring(1));
                Log.d("Image_Path", "Image Path: " + storageReference.toString());

                //FETCH IMMAGINE
                try {
                    File file = File.createTempFile("tempfile", ".jpg");

                    storageReference.getFile(file)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    image_wrong_select_viewer.setImageBitmap(bitmap);
                                }
                            });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                image_correct_select_viewer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(esercizio3.isEseguito()){
                            dismiss();

                            // Dialog esercizio eseguito
                            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_esercizio3_giusto, null);

                            TextView textViewMonete = dialogView.findViewById(R.id.textViewMonete);
                            TextView textViewEsperienza = dialogView.findViewById(R.id.textViewEsperienza);

                            textViewMonete.setText((esercizio3.getMonete()+50) + " Monete");
                            textViewEsperienza.setText((esercizio3.getEsperienza()+100) + " Punti Esperienza");

                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                                    .setView(dialogView)
                                    .setPositiveButton("OK",null);

                            builder.show();

                            DateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, 0);      // Imposta le ore a 0
                            cal.set(Calendar.MINUTE, 0);            // Imposta i minuti a 0
                            cal.set(Calendar.SECOND, 0);            // Imposta i secondi a 0
                            cal.set(Calendar.MILLISECOND, 0);

                            Query insert_prova = database.getReference("Utenti")
                                    .child("Logopedisti")
                                    .child(id_logopedista)
                                    .child("Pazienti")
                                    .child(id_bambino)
                                    .child("Terapie")
                                    .child(formatoData.format(cal.getTime()))
                                    .child("esercizio_" + pos_esercizio)
                                    .child(esercizio3.getId_esercizio());
                            insert_prova.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.child("eseguito").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });

                                    snapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });

                                    snapshot.child("esito").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query aggiorna_progressi_monete = database.getReference("Utenti")
                                    .child("Genitori")
                                    .child(sessionKey)
                                    .child("Bambini")
                                    .child(id_bambino)
                                    .child("monete");
                            aggiorna_progressi_monete.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int monete = snapshot.getValue(Integer.class);

                                    snapshot.getRef().setValue(monete+esercizio3.getMonete() + 50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query aggiorna_progressi_exp = database.getReference("Utenti")
                                    .child("Genitori")
                                    .child(sessionKey)
                                    .child("Bambini")
                                    .child(id_bambino)
                                    .child("esperienza");
                            aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int esperienza = snapshot.getValue(Integer.class);

                                    snapshot.getRef().setValue(esperienza+esercizio3.getEsperienza() + 100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }else{
                            new MaterialAlertDialogBuilder(v.getContext())
                                    .setTitle("Dove vai?")
                                    .setMessage("Devi eseguire prima l'esercizio (• ᴖ •｡)")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        image_wrong_select_viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(esercizio3.isEseguito()){
                    dismiss();

                    // Dialog esercizio eseguito
                    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_esercizio3_sbagliato, null);

                    TextView textViewMonete = dialogView.findViewById(R.id.textViewMonete);
                    TextView textViewEsperienza = dialogView.findViewById(R.id.textViewEsperienza);

                    textViewMonete.setText((esercizio3.getMonete()+25)  + " Monete");
                    textViewEsperienza.setText((esercizio3.getEsperienza()+50) + " Punti Esperienza");

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                            .setView(dialogView)
                            .setPositiveButton("OK",null);

                    builder.show();

                    DateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, 0);      // Imposta le ore a 0
                    cal.set(Calendar.MINUTE, 0);            // Imposta i minuti a 0
                    cal.set(Calendar.SECOND, 0);            // Imposta i secondi a 0
                    cal.set(Calendar.MILLISECOND, 0);

                    Query insert_prova = database.getReference("Utenti")
                            .child("Logopedisti")
                            .child(id_logopedista)
                            .child("Pazienti")
                            .child(id_bambino)
                            .child("Terapie")
                            .child(formatoData.format(cal.getTime()))
                            .child("esercizio_" + pos_esercizio)
                            .child(esercizio3.getId_esercizio());
                    insert_prova.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.child("eseguito").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                            snapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                            snapshot.child("esito").getRef().setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Query aggiorna_progressi_monete = database.getReference("Utenti")
                            .child("Genitori")
                            .child(sessionKey)
                            .child("Bambini")
                            .child(id_bambino)
                            .child("monete");
                    aggiorna_progressi_monete.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int monete = snapshot.getValue(Integer.class);

                            snapshot.getRef().setValue(monete+esercizio3.getMonete() + 25).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Query aggiorna_progressi_exp = database.getReference("Utenti")
                            .child("Genitori")
                            .child(sessionKey)
                            .child("Bambini")
                            .child(id_bambino)
                            .child("esperienza");
                    aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int esperienza = snapshot.getValue(Integer.class);

                            snapshot.getRef().setValue(esperienza+esercizio3.getEsperienza() + 50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }else{
                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle("Dove vai?")
                            .setMessage("Devi eseguire prima l'esercizio (• ᴖ •｡)")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

        // Configura il dialog con il layout personalizzato
        builder.setView(view)
                .setTitle("Riconosci l'immagine corretta");

        return builder.create();

    }

}