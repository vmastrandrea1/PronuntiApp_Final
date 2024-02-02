package it.uniba.dib.sms2324_4.gioco.ui.gioco;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio1;
import it.uniba.dib.sms2324_4.fragment.CorrezioneEsercizio1;


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

    public static GiocoFragment newInstance(String id_bambino, String sessionKey_genitore,String id_logopedista) {
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

        currentButtonIndex = 0;

        loadFlagVisibility();
        
        laodCurrentPosition();

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

        buttons = new ImageButton[]{ //bottoni per il regalo e le bandierine
                rootView.findViewById(R.id.bandierina_1),
                rootView.findViewById(R.id.bandierina_2),
                rootView.findViewById(R.id.bandierina_3),
                rootView.findViewById(R.id.bandierina_4),
                rootView.findViewById(R.id.bandierina_5),
                rootView.findViewById(R.id.regalo)

        };


        setButtonClickListeners();

        return rootView;
    }

    private void laodCurrentPosition() {
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
                        .setValue(overlayImage.getX())
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
                        .setValue(overlayImage.getY())
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


    private void setButtonClickListeners() {
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
                animateImage((buttons[5]));
                buttons[5].setVisibility(View.GONE);
            }
        });
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
        }
    }

    // Metodo per caricare lo stato delle bandierine visibili
    private void loadFlagVisibility() {
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
                        }
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}