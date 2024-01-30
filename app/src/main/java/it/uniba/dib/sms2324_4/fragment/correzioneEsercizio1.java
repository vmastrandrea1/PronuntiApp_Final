package it.uniba.dib.sms2324_4.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio1;
import it.uniba.dib.sms2324_4.gioco.ui.gioco.GiocoFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link correzioneEsercizio1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class correzioneEsercizio1 extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String id_bambino;
    private String sessionKey;
    private String id_esercizio1;
    private String id_logopedista;
    private int pos_esercizio;

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ESERCIZIO_1 = "ESERCIZIO_1";
    private static final String ID_LOGOPEDISTA = "ID_LOGOPEDISTA";
    private static final String POS_ESERCIZIO = "POS_ESERCIZIO";

    private boolean isRecording = false;

    Esercizio1 esercizio1 = new Esercizio1();

    MediaRecorder mediaRecorder = new MediaRecorder();
    MediaPlayer mediaPlayer = new MediaPlayer();

    File directory;
    File audio_registrato;
    TextToSpeech textToSpeech = null;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

    public correzioneEsercizio1() throws IOException {
    }

    public static correzioneEsercizio1 newInstance(String id_bambino, String sessionKey, String id_esercizio1,
                                                   String id_logopedista , int pos_esercizio) throws IOException {
        correzioneEsercizio1 fragment = new correzioneEsercizio1();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY, sessionKey);
        args.putString(ESERCIZIO_1, id_esercizio1);
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
            id_esercizio1 = getArguments().getString(ESERCIZIO_1);
            id_logopedista = getArguments().getString(ID_LOGOPEDISTA);
            pos_esercizio = getArguments().getInt(POS_ESERCIZIO);
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_correzione_esercizio1, null, false);

        Button confirm_btn = view.findViewById(R.id.confirm_choice_btn);

        Query getExercise = database.getReference("Utenti")
                .child("Logopedisti")
                .child(id_logopedista)
                .child("Esercizi")
                .child(id_esercizio1);

        getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                esercizio1 = snapshot.getValue(Esercizio1.class);
                try {
                    esecuzioneEsercizio(esercizio1, view);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(esercizio1.isEseguito()){
                   dismiss();

                   // Dialog esercizio eseguito

                    /*
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
                           .child("esercizio_" + pos_esercizio);

                     */
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
                .setTitle("Riconosci l'immagine");

        return builder.create();
    }


    private void esecuzioneEsercizio(Esercizio1 esercizio1, View view) throws IOException {
        ImageView image_select_viewer = view.findViewById(R.id.image_select_viewer);

        Button record_solution_1 = view.findViewById(R.id.record_solution_1);
        Button stop_record_solution_1 = view.findViewById(R.id.stop_record_solution_1);
        Button aiuto1_btn = view.findViewById(R.id.aiuto1_btn);
        Button aiuto2_btn = view.findViewById(R.id.aiuto2_btn);
        Button aiuto3_btn = view.findViewById(R.id.aiuto3_btn);
        Button riproduci_audio_1 = view.findViewById(R.id.riproduci_audio_1);
        Button stop_riproduci_audio_1 = view.findViewById(R.id.stop_riproduci_audio_1);

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
        StorageReference storageReference = storage.getReference(esercizio1.getUriImage().substring(1));
        Log.d("Image_Path", "Image Path: " + storageReference.toString());

        ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.RECORD_AUDIO} , PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} , PackageManager.PERMISSION_GRANTED);

        //FETCH IMMAGINE
        try {
            File file = File.createTempFile("tempfile", ".jpg");

            storageReference.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            image_select_viewer.setImageBitmap(bitmap);
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        aiuto1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech = new TextToSpeech(v.getContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            // TextToSpeech è stato inizializzato con successo
                            textToSpeech.speak(esercizio1.getAiuto_1(), TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            // Errore durante l'inizializzazione di TextToSpeech
                        }
                    }
                });
            }
        });

        if (esercizio1.getAiuto_2().compareTo("") == 0) {
            aiuto2_btn.setVisibility(View.GONE);
        } else {
            aiuto2_btn.setVisibility(View.VISIBLE);
        }
        if (esercizio1.getAiuto_3().compareTo("") == 0) {
            aiuto3_btn.setVisibility(View.GONE);
        } else {
            aiuto3_btn.setVisibility(View.VISIBLE);
        }

        aiuto2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech = new TextToSpeech(v.getContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            // TextToSpeech è stato inizializzato con successo
                            textToSpeech.speak(esercizio1.getAiuto_2(), TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            // Errore durante l'inizializzazione di TextToSpeech
                        }
                    }
                });
            }
        });

        aiuto3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech = new TextToSpeech(v.getContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            // TextToSpeech è stato inizializzato con successo
                            textToSpeech.speak(esercizio1.getAiuto_3(), TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            // Errore durante l'inizializzazione di TextToSpeech
                        }
                    }
                });
            }
        });

        record_solution_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording){
                    try {
                        if(audio_registrato != null){
                            audio_registrato.delete();
                        }else{
                            audio_registrato = File.createTempFile("audio_registrato",".3gp",directory);
                        }
                        directory = v.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);

                        mediaRecorder = new MediaRecorder();

                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                        mediaRecorder.setOutputFile(audio_registrato);

                        mediaRecorder.prepare();
                        mediaRecorder.start();

                        isRecording = true;

                        record_solution_1.setVisibility(View.GONE);
                        stop_record_solution_1.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        stop_record_solution_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                mediaRecorder.release();

                isRecording = false;

                esercizio1.setEseguito(true);

                record_solution_1.setVisibility(View.VISIBLE);
                stop_record_solution_1.setVisibility(View.GONE);

                riproduci_audio_1.setVisibility(View.VISIBLE);
            }
        });

        riproduci_audio_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer = new MediaPlayer();

                    mediaPlayer.setDataSource(v.getContext() , Uri.fromFile(audio_registrato));

                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    riproduci_audio_1.setVisibility(View.GONE);
                    stop_riproduci_audio_1.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        stop_riproduci_audio_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                riproduci_audio_1.setVisibility(View.VISIBLE);
                stop_riproduci_audio_1.setVisibility(View.GONE);
            }
        });

    }
}