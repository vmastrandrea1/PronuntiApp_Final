package it.uniba.dib.sms2324_4.gioco.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import it.uniba.dib.sms2324_4.R;

public class ShopFragment extends Fragment {

    private int monete;
    private ItemAdapter itemAdapter;
    private TextView textViewMonete;

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static  final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOGOPEDISTA";
    private String id_bambino;
    private String sessionKey;
    private String id_logopedista;

    public static ShopFragment newInstance(String id_bambino,String sessionKey,String id_logopedista) {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY,sessionKey);
        args.putString(ID_LOGOPEDISTA,id_logopedista);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_bambino = getArguments().getString(BAMBINO_ID);
            sessionKey = getArguments().getString(SESSION_KEY);
            id_logopedista = getArguments().getString(ID_LOGOPEDISTA);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Creazione della lista di personaggi
        ArrayList<Item> personaggi = creaListaPersonaggi();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

        for (Iterator<Item> it = personaggi.iterator(); it.hasNext(); ) {
            Item item = it.next();
            Query item_acquistato = database.getReference("Utenti")
                    .child("Genitori")
                    .child(sessionKey)
                    .child("Bambini")
                    .child(id_bambino)
                    .child("Skin")
                    .child(String.valueOf(item.getId()));
            item_acquistato.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        item.setAcquistato(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // Configurazione della GridView con un adattatore personalizzato
        GridView gridViewPersonaggi = view.findViewById(R.id.gridViewPersonaggi);
        itemAdapter = new ItemAdapter(getContext(), personaggi, this ,id_bambino,sessionKey,id_logopedista,container,getParentFragmentManager());
        gridViewPersonaggi.setAdapter(itemAdapter);

        // Inizializza il TextView per visualizzare il contatore delle monete
        textViewMonete = view.findViewById(R.id.textViewMonete);
        updateMoneteText();

        return view;
    }

    private ArrayList<Item> creaListaPersonaggi() {
        ArrayList<Item> item = new ArrayList<>();

        Optional<Integer> animazioneIdOptional = Optional.of(123); // Esempio di ID animazione


        item.add(new Item(0,"Tony", R.drawable.skin_bimbo, 0, getString(R.string.descrizione_item_0),Item.ItemType.PERSONAGGIO));
        item.get(0).setAcquistato(true);
        item.add(new Item(1,"Torvaldo", R.drawable.skin_troll, 500, getString(R.string.descrizione_item_1),Item.ItemType.PERSONAGGIO,R.drawable.animation_troll));
        item.add(new Item(2,"Rex Fulminatus", R.drawable.skin_dinosauro, 600, getString(R.string.descrizione_item_2),Item.ItemType.PERSONAGGIO,R.drawable.animation_dinosauro));
        item.add(new Item(3,"Gelatiello", R.drawable.skin_gelato, 700, getString(R.string.descrizione_item_3),Item.ItemType.PERSONAGGIO,R.drawable.animation_gelato));
        item.add(new Item(4,"Trombone", R.drawable.skin_stregone, 800, getString(R.string.descrizione_item_4),Item.ItemType.PERSONAGGIO,R.drawable.animation_stregone));
        item.add(new Item(5,"Oculopodio", R.drawable.skin_polpo, 900, getString(R.string.descrizione_item_5),Item.ItemType.PERSONAGGIO,R.drawable.animation_polpo));
        item.add(new Item(6,"Robotelmo", R.drawable.skin_robot, 1000, getString(R.string.descrizione_item_6),Item.ItemType.PERSONAGGIO,R.drawable.animation_robot));
        item.add(new Item(61,"Cubot", R.drawable.skin_cubot, 3000, getString(R.string.descrizione_item_61),Item.ItemType.PERSONAGGIO,R.drawable.animation_cubot));


        item.add(new Item(7,"Sentiero", R.drawable.map_sentiero, 0, getString(R.string.descrizione_item_7),Item.ItemType.SCENARIO));
        item.get(7).setAcquistato(true);
        item.add(new Item(8,"Flying island", R.drawable.map_flying_island, 600, getString(R.string.descrizione_item_8),Item.ItemType.SCENARIO));
        item.add(new Item(9,"Marte", R.drawable.map_mars, 700, getString(R.string.descrizione_item_9),Item.ItemType.SCENARIO));

        item.add(new Item(10,"Cielo", R.drawable.sfondo_cielo, 0, getString(R.string.descrizione_item_10),Item.ItemType.SFONDO));
        item.get(10).setAcquistato(true);
        item.add(new Item(11,"Spazio viola", R.drawable.sfondo_spazioviola, 600, getString(R.string.descrizione_item_11),Item.ItemType.SFONDO));
        item.add(new Item(12,"Onde di colore", R.drawable.sfondo_onde_di_colore, 500, getString(R.string.descrizione_item_12),Item.ItemType.SFONDO));
        item.add(new Item(13,"Natura", R.drawable.sfondo_natura, 800, getString(R.string.descrizione_item_13),Item.ItemType.SFONDO));

        return item;
    }

    public void updateMoneteText() {
        textViewMonete.setText("" + monete);
    }

    public int getMonete() {
        return monete;
    }

    public void decrementaMonete(int prezzo) {
        monete -= prezzo;
        updateMoneteText();
    }

    // metodo per impostare il valore delle monete
    public void setMonete(int monete) {
        this.monete = monete;
    }

    //metodo per cambiare lo sfondo
    public void cambiaSfondo(Item item){

    }

}

