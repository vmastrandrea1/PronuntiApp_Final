package it.uniba.dib.sms2324_4.gioco.ui.gioco;

import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio1;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio2;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio3;
import it.uniba.dib.sms2324_4.fragment.CorrezioneEsercizio1;
import it.uniba.dib.sms2324_4.fragment.CorrezioneEsercizio2;
import it.uniba.dib.sms2324_4.fragment.CorrezioneEsercizio3;
import it.uniba.dib.sms2324_4.fragment.Home;


public class GiocoFragment extends Fragment{

    private ImageView overlayImage;
    int currentButtonIndex = 0;
    private ImageButton[] buttons;

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static  final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOGOPEDISTA";
    private String id_bambino;
    private String sessionKey_genitore;
    private String id_logopedista;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");


    private int id_sfondo_selezionato = 0 ;
    private int id_personaggio_selezionato = 0;
    private int id_map_selezionato = 0;

    private boolean[] isFlagVisible = new boolean[6]; // Un array di boolean per memorizzare lo stato di visibilità delle bandierine

    public static GiocoFragment newInstance(String id_bambino, String sessionKey_genitore,
                                            String id_logopedista) {
        GiocoFragment fragment = new GiocoFragment();
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


    public int getId_personaggio_selezionato() {
        return id_personaggio_selezionato;
    }


    public int getId_map_selezionato() {
        return id_map_selezionato;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gioco, container, false);

        // GESTIONE PULSANTE BACK
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Nascondi la navbar
                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomBar);
                bottomNavigationView.setVisibility(View.GONE);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(container.getId() , Home.newInstance(sessionKey_genitore))
                        .commit();
            }
        };

        // Aggiungi il callback al gestore dei pressioni del pulsante "back"
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);

        currentButtonIndex = 0;

        loadFlagVisibility(rootView);
        
        loadCurrentPosition();

        Query id_map = database.getReference("Utenti")
                .child("Genitori")
                .child(sessionKey_genitore)
                .child("Bambini")
                .child(id_bambino)
                .child("id_mappa_selezionata");
        id_map.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    id_map_selezionato = snapshot.getValue(Integer.class);

                    ConstraintLayout constraintLayout = rootView.findViewById(R.id.constraintLayout);
                    switch (id_map_selezionato){
                        case 9:
                            constraintLayout.setBackgroundResource(R.drawable.map_flying_island);
                            break;
                        case 10:
                            constraintLayout.setBackgroundResource(R.drawable.map_mars);
                            break;
                        default:
                            constraintLayout.setBackgroundResource(R.drawable.map_sentiero);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        overlayImage = rootView.findViewById(R.id.overlayImage);

        Query id_personaggio = database.getReference("Utenti")
                .child("Genitori")
                .child(sessionKey_genitore)
                .child("Bambini")
                .child(id_bambino)
                .child("id_personaggio_selezionato");
        id_personaggio.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    id_personaggio_selezionato = snapshot.getValue(Integer.class);

                    switch (id_personaggio_selezionato){
                        case 1:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_troll)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 2:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_dinosauro)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 3:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_gelato)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 4:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_stregone)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 5:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_polpo)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 6:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_robot)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 7:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_cubot)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        default:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_bimbo)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query id_sfondo = database.getReference("Utenti")
                .child("Genitori")
                .child(sessionKey_genitore)
                .child("Bambini")
                .child(id_bambino)
                .child("id_sfondo_selezionato");
        id_sfondo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    id_sfondo_selezionato = snapshot.getValue(Integer.class);

                    ConstraintLayout wallpaper_game = rootView.findViewById(R.id.wallpaper_game);

                    switch (id_sfondo_selezionato){
                        case 12:
                            wallpaper_game.setBackgroundResource(R.drawable.sfondo_spazioviola);
                            break;
                        case 13:
                            wallpaper_game.setBackgroundResource(R.drawable.sfondo_onde_di_colore);
                            break;
                        case 14:
                            wallpaper_game.setBackgroundResource(R.drawable.sfondo_natura);
                            break;
                        default:
                            wallpaper_game.setBackgroundResource(R.drawable.sfondo_cielo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttons = new ImageButton[]{ //bottoni per il regalo e le bandierine
                rootView.findViewById(R.id.bandierina_1),
                rootView.findViewById(R.id.bandierina_2),
                rootView.findViewById(R.id.bandierina_3),
                rootView.findViewById(R.id.bandierina_4),
                rootView.findViewById(R.id.bandierina_5),
                rootView.findViewById(R.id.regalo)

        };


        setButtonClickListeners(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Mostra la navbar
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomBar);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }


    private void loadCurrentPosition() {
        Query get_position = database.getReference("Utenti")
                .child("Genitori")
                .child(sessionKey_genitore)
                .child("Bambini")
                .child(id_bambino);
        get_position.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("X_Position").exists()){
                    overlayImage.setX(snapshot.child("X_Position").getValue(Float.class));
                    overlayImage.setY(snapshot.child("Y_Position").getValue(Float.class));
                }
                else{
                    overlayImage.setX(270);
                    overlayImage.setY(1400);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void animateImage(final ImageButton button) {
        float destinationX = button.getX() + button.getWidth() / 2 - overlayImage.getWidth() / 2;
        float destinationY = button.getY() + button.getHeight() / 2 - overlayImage.getHeight() / 2;

        ValueAnimator animatorX = ValueAnimator.ofFloat(overlayImage.getX(), destinationX);
        ValueAnimator animatorY = ValueAnimator.ofFloat(overlayImage.getY(), destinationY);

        animatorX.setDuration(500); // Durata dell'animazione in millisecondi
        animatorY.setDuration(500);

        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                overlayImage.setX((Float) animation.getAnimatedValue());

                database.getReference("Utenti")
                        .child("Genitori")
                        .child(sessionKey_genitore)
                        .child("Bambini")
                        .child(id_bambino)
                        .child("X_Position")
                        .setValue(destinationX)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });

        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                overlayImage.setY((Float) animation.getAnimatedValue());

                database.getReference("Utenti")
                        .child("Genitori")
                        .child(sessionKey_genitore)
                        .child("Bambini")
                        .child(id_bambino)
                        .child("Y_Position")
                        .setValue(destinationY)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });

        animatorX.start();
        animatorY.start();

    }


    private void setButtonClickListeners(View rootview) {
        String data_terapia = getData();

        Query childExistant = database.getReference("Utenti")
                .child("Logopedisti")
                .child(id_logopedista)
                .child("Pazienti")
                .child(id_bambino)
                .child("Terapie")
                .child(data_terapia);

        buttons[5].setVisibility(View.GONE);

        childExistant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            int buttonIndex = i;
                            if(dataSnapshot1.child("eseguito").exists()){
                                currentButtonIndex++;
                            }else{
                                buttons[i].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (currentButtonIndex == buttonIndex) {
                                            animateImage(buttons[buttonIndex]);

                                            buttons[buttonIndex].setVisibility(View.GONE);
                                            try {
                                                apriEsercizio(dataSnapshot1,buttonIndex+1);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            currentButtonIndex++;
                                        }
                                        if(currentButtonIndex == 5){
                                            buttons[5].setVisibility(View.VISIBLE);
                                        }


                                    }
                                });
                            }
                        }
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateGift(buttons[5]);

                buttons[5].setVisibility(View.GONE);

                ImageView regalo = rootview.findViewById(R.id.image_regalo);
                regalo.setVisibility(View.GONE);

                Query aggiorna_progressi_monete = database.getReference("Utenti")
                        .child("Genitori")
                        .child(sessionKey_genitore)
                        .child("Bambini")
                        .child(id_bambino)
                        .child("monete");
                aggiorna_progressi_monete.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int monete = snapshot.getValue(Integer.class);

                        snapshot.getRef().setValue(monete + 100).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                        .child(sessionKey_genitore)
                        .child("Bambini")
                        .child(id_bambino)
                        .child("esperienza");
                aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int esperienza = snapshot.getValue(Integer.class);

                        snapshot.getRef().setValue(esperienza + 500).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                        database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(id_logopedista)
                                .child("Pazienti")
                                .child(id_bambino)
                                .child("esperienza")
                                .setValue(esperienza + 500).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });

                        //Rimozione del valore X e Y
                        database.getReference("Utenti")
                                .child("Genitori")
                                .child(sessionKey_genitore)
                                .child("Bambini")
                                .child(id_bambino)
                                .child("X_Position")
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });

                        database.getReference("Utenti")
                                .child("Genitori")
                                .child(sessionKey_genitore)
                                .child("Bambini")
                                .child(id_bambino)
                                .child("Y_Position")
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });

                        // Suono da riprodurre quando il dialog appare
                        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.win);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release(); // Rilascia il MediaPlayer dopo che il suono è stato riprodotto completamente
                            }
                        });
                        mediaPlayer.start();

                        View dialogView = LayoutInflater.from(rootview.getContext()).inflate(R.layout.dialog_regalo_riscosso, null);
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogButtonStyle)
                                .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_dialog_background))
                                .setView(dialogView)
                                .setPositiveButton("OK",null);
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void animateGift(ImageButton button) {
        float destinationX = button.getX() + button.getWidth() / 2 - overlayImage.getWidth() / 2;
        float destinationY = button.getY() + button.getHeight() / 2 - overlayImage.getHeight() / 2;

        ValueAnimator animatorX = ValueAnimator.ofFloat(overlayImage.getX(), destinationX);
        ValueAnimator animatorY = ValueAnimator.ofFloat(overlayImage.getY(), destinationY);

        animatorX.setDuration(500); // Durata dell'animazione in millisecondi
        animatorY.setDuration(500);

        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                overlayImage.setX((Float) animation.getAnimatedValue());
            }
        });

        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                overlayImage.setY((Float) animation.getAnimatedValue());
            }
        });

        animatorX.start();
        animatorY.start();
    }

    private String getData() {
        DateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);      // Imposta le ore a 0
        cal.set(Calendar.MINUTE, 0);            // Imposta i minuti a 0
        cal.set(Calendar.SECOND, 0);            // Imposta i secondi a 0
        cal.set(Calendar.MILLISECOND, 0);
        return formatoData.format(cal.getTime());
    }

    private void apriEsercizio(DataSnapshot dataSnapshot1 , int i) throws IOException {
        if (dataSnapshot1.child("id_esercizio").
                getValue().toString().startsWith("1_")) {
            Esercizio1 esercizio1 = new Esercizio1();

            esercizio1.setId_esercizio(dataSnapshot1
                    .child("id_esercizio").getValue().toString());

            CorrezioneEsercizio1 correzioneEsercizio1 = new CorrezioneEsercizio1();
            correzioneEsercizio1.newInstance(id_bambino,
                            sessionKey_genitore,esercizio1.getId_esercizio(),id_logopedista , i)
                    .show(getParentFragmentManager() , "Dimmi che immagine è rappresentata");
        }else if (dataSnapshot1.child("id_esercizio").
                getValue().toString().startsWith("2_")) {
            Esercizio2 esercizio2 = new Esercizio2();

            esercizio2.setId_esercizio(dataSnapshot1
                    .child("id_esercizio").getValue().toString());

            CorrezioneEsercizio2 correzioneEsercizio2 = new CorrezioneEsercizio2();
            correzioneEsercizio2.newInstance(id_bambino,
                            sessionKey_genitore,esercizio2.getId_esercizio(),id_logopedista , i)
                    .show(getParentFragmentManager() , "Dimmi che immagine è rappresentata");
        }else if (dataSnapshot1.child("id_esercizio").
                getValue().toString().startsWith("3_")) {
            Esercizio3 esercizio3 = new Esercizio3();

            esercizio3.setId_esercizio(dataSnapshot1
                    .child("id_esercizio").getValue().toString());

            CorrezioneEsercizio3 correzioneEsercizio3 = new CorrezioneEsercizio3();
            correzioneEsercizio3.newInstance(id_bambino,
                            sessionKey_genitore,esercizio3.getId_esercizio(),id_logopedista , i)
                    .show(getParentFragmentManager() , "Riconosci l'immagine corretta");
        }
    }

    // Metodo per caricare lo stato delle bandierine visibili
    private void loadFlagVisibility(View rootView) {
        ImageView regalo = rootView.findViewById(R.id.image_regalo);

        Query fetch_visibilty = database.getReference("Utenti")
                .child("Logopedisti")
                .child(id_logopedista)
                .child("Pazienti")
                .child(id_bambino)
                .child("Terapie")
                .child(getData());
        fetch_visibilty.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.child("eseguito").exists()){
                            isFlagVisible[i] = false; // Preimposta lo stato delle bandierine a true se non esiste alcun valore salvato
                            buttons[i].setVisibility(View.GONE);
                        }else{
                            isFlagVisible[i] = true; // Preimposta lo stato delle bandierine a true se non esiste alcun valore salvato
                            buttons[i].setVisibility(View.VISIBLE);
                            regalo.setVisibility(View.VISIBLE);
                        }
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Query gift_visibilty = database.getReference("Utenti")
                .child("Genitori")
                .child(sessionKey_genitore)
                .child("Bambini")
                .child(id_bambino)
                .child("X_Position");
        gift_visibilty.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    isFlagVisible[5] = false;
                    buttons[5].setVisibility(View.GONE);

                    regalo.setVisibility(View.GONE);
                }else{
                    regalo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}