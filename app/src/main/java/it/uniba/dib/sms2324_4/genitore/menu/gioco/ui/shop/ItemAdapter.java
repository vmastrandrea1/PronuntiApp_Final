package it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.genitore.bambino.Skin;
import it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.gioco.GiocoFragment;

public class ItemAdapter extends ArrayAdapter<Item> {

    private final ShopFragment shopFragment;
    private final SharedPreferences sharedPreferences;
    String id_bambino;
    String sessionKey;
    String id_logopedista;
    ViewGroup container;
    FragmentManager fragmentManager;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

    public ItemAdapter(Context context, ArrayList<Item> items, ShopFragment shopFragment, String id_bambino,
                       String sessionKey,String id_logopedista , ViewGroup container, FragmentManager fragmentManager) {
        super(context, 0, items);
        this.shopFragment = shopFragment;
        this.sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        this.id_bambino = id_bambino;
        this.sessionKey = sessionKey;
        this.container = container;
        this.fragmentManager = fragmentManager;
        this.id_logopedista = id_logopedista;

        // Carica i dati dell'utente all'inizializzazione dell'adapter
        loadUserData();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Item item = getItem(position);

        // Verifica se convertView è null o se il tipo di item è cambiato
        if (convertView == null || !convertView.getTag().equals(item.getTipo())) {
            // Se convertView è null o il tipo di item è cambiato, infla il layout appropriato
            if (item.getTipo() == Item.ItemType.PERSONAGGIO) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_personaggio, parent, false);
            } else if (item.getTipo() == Item.ItemType.SCENARIO) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_scenario, parent, false);
            }
            else if (item.getTipo() == Item.ItemType.SFONDO) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sfondo, parent, false);
            }

            // Imposta il tag per conservare il tipo di item
            convertView.setTag(item.getTipo());
        }

        ImageView previewImageShopItem = convertView.findViewById(R.id.previewImageShopItem);
        TextView nomeShopItem = convertView.findViewById(R.id.nomeShopItem);
        Button buttonPrezzoShopItem = convertView.findViewById(R.id.buttonPrezzoShopItem);
        TextView textViewAcquistato = convertView.findViewById(R.id.textViewAcquistatoShopItem);

        assert item != null;
        // Imposta l'immagine PNG nell'ImageView
        previewImageShopItem.setImageResource(item.getImmagineId());

        nomeShopItem.setText(item.getNome());

        if (item.getPrezzo() == 0){
            buttonPrezzoShopItem.setText(convertView.getResources().getText(R.string.gratis));
        }
        else buttonPrezzoShopItem.setText(String.valueOf(item.getPrezzo()));

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
                    buttonPrezzoShopItem.setVisibility(View.GONE);
                    textViewAcquistato.setVisibility(View.VISIBLE);
                } else {
                    buttonPrezzoShopItem.setVisibility(View.VISIBLE);
                    textViewAcquistato.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonPrezzoShopItem.setOnClickListener(view -> {
            //SALVATGGIO SKIN
            Query monete_sufficienti = database.getReference("Utenti")
                    .child("Genitori")
                    .child(sessionKey)
                    .child("Bambini")
                    .child(id_bambino)
                    .child("monete");

            monete_sufficienti.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(Integer.class) >= item.getPrezzo()) {
                        shopFragment.decrementaMonete(item.getPrezzo());
                        mostraAcquistatoDialog(item);
                    } else {
                        mostraMoneteInsufficientiDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        convertView.setOnClickListener(view -> mostraDettagliDialog(item));

        return convertView;
    }

    private void mostraAcquistatoDialog(Item item) {
        // Imposta lo stato "acquistato" dell'item
        item.setAcquistato(true);

        // Salva i dati dell'utente dopo l'acquisto
        saveUserData();

        // Notifica all'adapter che i dati sono cambiati
        notifyDataSetChanged();

        Skin skin_acquistata = new Skin();
        skin_acquistata.setCosto(item.getPrezzo());
        skin_acquistata.setIdSkin(item.getId());

        database.getReference("Utenti")
                .child("Genitori")
                .child(sessionKey)
                .child("Bambini")
                .child(id_bambino)
                .child("Skin")
                .child(String.valueOf(skin_acquistata.getIdSkin()))
                .setValue(skin_acquistata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

        saveUserData();

        new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogButtonStyle)
                .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_dialog_background))
                .setTitle(R.string.acquisto_riuscito)
                //.setMessage("Hai acquistato " + item.getNome())
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void mostraMoneteInsufficientiDialog() {
        new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogButtonStyle)
                .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_dialog_background))
                .setTitle(R.string.monete_insufficienti)
                .setMessage(R.string.non_hai_abbastanza_monete)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void mostraDettagliDialog(Item item) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_dettaglio_item, null);

        ImageView imageViewFullScreen = dialogView.findViewById(R.id.imageViewFullScreen);
        TextView textViewNome = dialogView.findViewById(R.id.textViewNomeFullScreen);
        TextView textViewPrezzo = dialogView.findViewById(R.id.textViewPrezzoFullScreen);
        TextView textViewDescrizione = dialogView.findViewById(R.id.textViewDescrizioneFullScreen);

        textViewNome.setText(item.getNome());

        if (item.getPrezzo() == 0) {
            textViewPrezzo.setText(dialogView.getResources().getText(R.string.gratis));

        }
        else textViewPrezzo.setText(String.valueOf(item.getPrezzo()));

        textViewDescrizione.setText(item.getDescrizione());


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogButtonStyle)
                .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_dialog_background))
                .setView(dialogView)
                .setPositiveButton(R.string.ok, null);

        // Aggiungi il pulsante "Imposta come predefinito" solo se l'oggetto è già stato acquistato
        if (item.isAcquistato()) {
            builder.setNegativeButton(R.string.imposta_come_predefinito, (dialog, which) -> {
                
        if(item.getId() < 8){
                    database.getReference("Utenti")
                            .child("Genitori")
                            .child(sessionKey)
                            .child("Bambini")
                            .child(id_bambino)
                            .child("id_personaggio_selezionato")
                            .setValue(item.getId());

                }else if(item.getId() < 11){
                    database.getReference("Utenti")
                            .child("Genitori")
                            .child(sessionKey)
                            .child("Bambini")
                            .child(id_bambino)
                            .child("id_mappa_selezionata")
                            .setValue(item.getId());
                }else{
                    database.getReference("Utenti")
                            .child("Genitori")
                            .child(sessionKey)
                            .child("Bambini")
                            .child(id_bambino)
                            .child("id_sfondo_selezionato")
                            .setValue(item.getId());
                }

                // Salva i dati dell'utente dopo l'impostazione come predefinito
                saveUserData();

                // Notifica all'adapter che i dati sono cambiati
                notifyDataSetChanged();

                // Mostra un messaggio di conferma
                Toast.makeText(getContext(), R.string.oggetto_impostato_come_predefinito, Toast.LENGTH_SHORT).show();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(container.getId(), GiocoFragment.newInstance(id_bambino,sessionKey,id_logopedista));
                fragmentTransaction.commit();
            });
        }

        // Verifica se l'oggetto è di tipo PERSONAGGIO e applica l'animazione con Glide
        if (item.getTipo() == Item.ItemType.PERSONAGGIO) {
            if (item.getNome().equals("Tony")) {
                imageViewFullScreen.setImageResource(item.getImmagineId());
            } else {
                // Utilizza Glide per caricare la GIF animata nel layout del dialog
                Glide.with(getContext())
                        .asGif()
                        .load(item.getAnimazioneId())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageViewFullScreen);
            }
        } else {
            // Visualizza direttamente la risorsa per gli scenari e gli sfondi (PNG)
            imageViewFullScreen.setImageResource(item.getImmagineId());
        }


        builder.show();
    }

    private void saveUserData() {
        database.getReference("Utenti")
                    .child("Genitori")
                    .child(sessionKey)
                    .child("Bambini")
                    .child(id_bambino)
                    .child("monete")
                    .setValue(shopFragment.getMonete()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

        shopFragment.getMonete();
        /*
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Monete", shopFragment.getMonete());

        for (int i = 0; i < getCount(); i++) {
            Item item = getItem(i);
            if (item != null) {
                editor.putBoolean(item.getNome() + "_acquistato", item.isAcquistato());
            }
        }

        editor.apply();

         */
        // Notifica all'adapter che i dati sono cambiati
        notifyDataSetChanged();
    }

    private void loadUserData() {

        Query fetch_monete = database.getReference("Utenti")
                        .child("Genitori")
                        .child(sessionKey)
                        .child("Bambini")
                        .child(id_bambino)
                        .child("monete");
        fetch_monete.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopFragment.setMonete(snapshot.getValue(Integer.class));
                shopFragment.updateMoneteText();
                /*
                for (int i = 0; i < getCount(); i++) {
                    Item item = getItem(i);
                    if (item != null) {
                        item.setAcquistato(sharedPreferences.getBoolean(item.getNome() + "_acquistato", false));
                    }
                }

                 */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
