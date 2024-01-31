package it.uniba.dib.sms2324_4.gioco.ui.gioco;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import it.uniba.dib.sms2324_4.fragment.correzioneEsercizio1;

public class GiocoFragment extends Fragment {

    private ImageView overlayImage;
    int currentButtonIndex = 0;
    private ImageButton[] buttons;
    private boolean[] isFlagVisible = new boolean[6]; // Un array di boolean per memorizzare lo stato di visibilità delle bandierine

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOGOPEDISTA";

    private String id_bambino;
    private String sessionKey_genitore;
    private String id_logopedista;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

    private int id_sfondo_selezionato = 0;
    private int id_personaggio_selezionato = 0;
    private int id_map_selezionato = 0;

    public static GiocoFragment newInstance(String id_bambino, String sessionKey_genitore, String id_logopedista) {
        GiocoFragment fragment = new GiocoFragment();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY, sessionKey_genitore);
        args.putString(ID_LOGOPEDISTA, id_logopedista);
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

    // Metodo per salvare lo stato delle bandierine visibili
    private void saveFlagVisibility(boolean[] isFlagVisible) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FlagPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < isFlagVisible.length; i++) {
            editor.putBoolean("Flag" + i, isFlagVisible[i]);
        }
        editor.apply();
    }

    // Metodo per caricare lo stato delle bandierine visibili
    private void loadFlagVisibility() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FlagPrefs", Context.MODE_PRIVATE);
        for (int i = 0; i < isFlagVisible.length; i++) {
            isFlagVisible[i] = sharedPreferences.getBoolean("Flag" + i, true); // Preimposta lo stato delle bandierine a true se non esiste alcun valore salvato
            buttons[i].setVisibility(isFlagVisible[i] ? View.VISIBLE : View.GONE);
        }
    }

    // Metodo per salvare le coordinate del personaggio
    private void saveCharacterCoordinates(PointF coordinates) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CharacterPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("CharacterX", coordinates.x);
        editor.putFloat("CharacterY", coordinates.y);
        editor.apply();
    }

    // Metodo per caricare le coordinate del personaggio
    private PointF loadCharacterCoordinates() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CharacterPrefs", Context.MODE_PRIVATE);
        float characterX = sharedPreferences.getFloat("CharacterX", 0);
        float characterY = sharedPreferences.getFloat("CharacterY", 0);
        return new PointF(characterX, characterY);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Carica lo stato delle bandierine visibili
        loadFlagVisibility();

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

                    ConstraintLayout constraintLayout = view.findViewById(R.id.constraintLayout);
                    switch (id_map_selezionato){
                        case 8:
                            constraintLayout.setBackgroundResource(R.drawable.map_flying_island);
                            break;
                        case 9:
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

                    ConstraintLayout wallpaper_game = view.findViewById(R.id.wallpaper_game);

                    switch (id_sfondo_selezionato){
                        case 11:
                            wallpaper_game.setBackgroundResource(R.drawable.sfondo_spazioviola);
                            break;
                        case 12:
                            wallpaper_game.setBackgroundResource(R.drawable.sfondo_onde_di_colore);
                            break;
                        case 13:
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

        overlayImage = view.findViewById(R.id.overlayImage);

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
                            Glide.with(view.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_troll)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 2:
                            Glide.with(view.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_dinosauro)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 3:
                            Glide.with(view.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_gelato)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 4:
                            Glide.with(view.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_stregone)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 5:
                            Glide.with(view.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_polpo)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        case 6:
                            Glide.with(view.getContext())
                                    .asBitmap()
                                    .load(R.drawable.skin_robot)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                            break;
                        default:
                            Glide.with(view.getContext())
                                    .asBitmap()
                                    .load(R.drawable.bimbo)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Carica le coordinate del personaggio
        PointF characterCoordinates = loadCharacterCoordinates();
        overlayImage.setX(characterCoordinates.x);
        overlayImage.setY(characterCoordinates.y);

        // Imposta i listener per il click sulle bandiere
        for (int i = 0; i < buttons.length - 1; i++) { // Escludi l'ultimo bottone (regalo)
            final int position = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveCharacter(position); // Muovi il personaggio sulla bandiera cliccata
                }
            });
        }

        overlayImage.setVisibility(View.VISIBLE);
    }

    // Metodo per muovere il personaggio su una bandiera
    private void moveCharacter(int newPosition) {
        if (newPosition == currentButtonIndex) {
            // Calcola le nuove coordinate del personaggio
            float destinationX = buttons[newPosition].getX() + buttons[newPosition].getWidth() / 2 - overlayImage.getWidth() / 2;
            float destinationY = buttons[newPosition].getY() + buttons[newPosition].getHeight() / 2 - overlayImage.getHeight() / 2;

            // Crea e avvia un animatore per muovere il personaggio alle nuove coordinate
            ValueAnimator animatorX = ValueAnimator.ofFloat(overlayImage.getX(), destinationX);
            ValueAnimator animatorY = ValueAnimator.ofFloat(overlayImage.getY(), destinationY);

            animatorX.setDuration(500);
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

            // Aggiorna la posizione attuale del personaggio
            currentButtonIndex = newPosition;

            // Nascondi la bandierina appena superata
            isFlagVisible[newPosition] = false;
            buttons[newPosition].setVisibility(View.GONE);

            // Salva lo stato delle bandierine visibili
            saveFlagVisibility(isFlagVisible);

            // Salva le nuove coordinate del personaggio
            saveCharacterCoordinates(new PointF(destinationX, destinationY));
        } else {
            // Gestisci il caso in cui si cerca di muovere il personaggio su una bandiera non valida
            // Ad esempio, se si cerca di saltare una bandiera o tornare indietro
            // In questo caso, puoi mostrare un messaggio di errore o ignorare l'azione
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gioco, container, false);

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
                        case 8:
                            constraintLayout.setBackgroundResource(R.drawable.map_flying_island);
                            break;
                        case 9:
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
                        case 11:
                            wallpaper_game.setBackgroundResource(R.drawable.sfondo_spazioviola);
                            break;
                        case 12:
                            wallpaper_game.setBackgroundResource(R.drawable.sfondo_onde_di_colore);
                            break;
                        case 13:
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

        buttons = new ImageButton[]{
                rootView.findViewById(R.id.bandierina_1),
                rootView.findViewById(R.id.bandierina_2),
                rootView.findViewById(R.id.bandierina_3),
                rootView.findViewById(R.id.bandierina_4),
                rootView.findViewById(R.id.bandierina_5),
                rootView.findViewById(R.id.regalo)
        };

        // Imposta i listener per il click sui bottoni
        setButtonClickListeners();

        // Carica lo stato delle bandierine visibili e la posizione del personaggio
        loadFlagVisibility();
        PointF characterCoordinates = loadCharacterCoordinates();
        overlayImage.setX(characterCoordinates.x);
        overlayImage.setY(characterCoordinates.y);

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
                        default:
                            Glide.with(rootView.getContext())
                                    .asBitmap()
                                    .load(R.drawable.bimbo)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disabilita la cache per le GIF per evitare problemi di riproduzione
                                    .into(overlayImage);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

    private void setButtonClickListeners() {
        DateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);      // Imposta le ore a 0
        cal.set(Calendar.MINUTE, 0);            // Imposta i minuti a 0
        cal.set(Calendar.SECOND, 0);            // Imposta i secondi a 0
        cal.set(Calendar.MILLISECOND, 0);

        Query childExistant = database.getReference("Utenti")
                .child("Logopedisti")
                .child(id_logopedista)
                .child("Pazienti")
                .child(id_bambino)
                .child("Terapie")
                .child(formatoData.format(cal.getTime()));

        buttons[5].setVisibility(View.GONE);

        childExistant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            final int buttonIndex = i;
                            buttons[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isFlagVisible[buttonIndex]) { // Verifica se la bandierina è visibile
                                        // Calcola le nuove coordinate del personaggio
                                        float destinationX = buttons[buttonIndex].getX() + buttons[buttonIndex].getWidth() / 2 - overlayImage.getWidth() / 2;
                                        float destinationY = buttons[buttonIndex].getY() + buttons[buttonIndex].getHeight() / 2 - overlayImage.getHeight() / 2;

                                        // Esegui l'animazione per muovere il personaggio
                                        animateImage(destinationX, destinationY);

                                        // Nascondi la bandierina appena superata
                                        isFlagVisible[buttonIndex] = false;
                                        buttons[buttonIndex].setVisibility(View.GONE);

                                        //Apre l'esercizio
                                        try {
                                            apriEsercizio(dataSnapshot1 , buttonIndex);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }

                                        // Salva lo stato delle bandierine visibili
                                        saveFlagVisibility(isFlagVisible);

                                        // Salva le nuove coordinate del personaggio
                                        saveCharacterCoordinates(new PointF(destinationX, destinationY));
                                    }
                                }
                            });
                        }
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Aggiungi un listener per il bottone regalo
        buttons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gestisci il click sul bottone regalo se necessario
            }
        });
    }

    private void animateImage(float destinationX, float destinationY) {
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

    private void apriEsercizio(DataSnapshot dataSnapshot1 , int i) throws IOException {
        if (dataSnapshot1.child("id_esercizio").
                getValue().toString().startsWith("1_")) {
            Esercizio1 esercizio1 = new Esercizio1();

            esercizio1.setId_esercizio(dataSnapshot1
                    .child("id_esercizio").getValue().toString());

            correzioneEsercizio1 correzioneEsercizio1 = new correzioneEsercizio1();
            correzioneEsercizio1.newInstance(id_bambino,
                            sessionKey_genitore,esercizio1.getId_esercizio(),id_logopedista , i)
                    .show(getParentFragmentManager() , "Dimmi che immagine è rappresentata");
        }
    }

}
