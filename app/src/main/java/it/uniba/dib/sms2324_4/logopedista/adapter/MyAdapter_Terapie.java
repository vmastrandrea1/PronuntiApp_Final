package it.uniba.dib.sms2324_4.logopedista.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio1;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio2;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio3;
import it.uniba.dib.sms2324_4.logopedista.menu.ElencoPazienti;

public class MyAdapter_Terapie extends RecyclerView.Adapter<MyAdapter_Terapie.MyViewHolder> {

    Context context;

    ArrayList<String> list;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public ViewGroup getContainer() {
        return container;
    }

    public void setContainer(ViewGroup container) {
        this.container = container;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    String sessionKey;
    ViewGroup container;
    FragmentManager fragmentManager;
    Dialog backDialog;
    String cfPaziente;
    String cfGenitore;


    public MyAdapter_Terapie(Context context, ArrayList<String> list, String sessionKey,
                             ViewGroup container, FragmentManager fragmentManager , Dialog backDialog , String cfPaziente, String cfGenitore) {
        this.context = context;
        this.list = list;
        this.sessionKey = sessionKey;
        this.container = container;
        this.fragmentManager = fragmentManager;
        this.backDialog = backDialog;
        this.cfPaziente = cfPaziente;
        this.cfGenitore = cfGenitore;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.therapy,parent,false);
        return  new MyViewHolder(v , list , sessionKey , fragmentManager , container , backDialog , cfPaziente, cfGenitore);
    }

    private static String formatDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Gestione dell'eccezione in caso di errore nella formattazione della data
            return null;
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String terapie = list.get(position);
        holder.tvTherapyName.setText(formatDate(terapie));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTherapyName;

        int conta_assegnazioni = 0;

        boolean eseguito = false;

        MediaPlayer mediaPlayer = new MediaPlayer();

        private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

        public MyViewHolder(@NonNull View itemView , ArrayList<String> list ,String  sessionKey ,
                            FragmentManager fragmentManager ,ViewGroup container , Dialog backDialog , String cfPaziente, String cfGenitore) {
            super(itemView);

            tvTherapyName = itemView.findViewById(R.id.tvTherapyName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        RecyclerView recyclerView;
                        MyAdapter_Esercizi myAdapter;
                        ArrayList<Object> list_es;

                        Dialog dialog = new Dialog(backDialog.getContext());
                        Button delete_therapy;

                        backDialog.dismiss();
                        dialog.setContentView(R.layout.view_therapy_patient);

                        delete_therapy = dialog.findViewById(R.id.delete_therapy);
                        delete_therapy.setVisibility(View.VISIBLE);

                        recyclerView = dialog.findViewById(R.id.therapy_recycler_view);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

                        list_es = new ArrayList<>();
                        myAdapter = new MyAdapter_Esercizi(dialog.getContext(),list_es,sessionKey,container,fragmentManager);
                        recyclerView.setAdapter(myAdapter);

                        Query exerciseExistant = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(sessionKey)
                                .child("Pazienti")
                                .child(cfPaziente)
                                .child("Terapie")
                                .child(list.get(pos));
                        exerciseExistant.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String id_esercizio = null;
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                        try{
                                            id_esercizio = dataSnapshot1.child("id_esercizio").getValue().toString();
                                            Log.d("PROVA" , "ID_Esercizio = " + id_esercizio);
                                        }catch (NullPointerException e){
                                            Log.d("PROVA" , "ID_Esercizio = " + null);
                                        }
                                        Query exercise = database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Esercizi")
                                                .child(id_esercizio);
                                        exercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if(snapshot1.child("id_esercizio").getValue().toString().startsWith("1_")){
                                                    Esercizio1 esercizio1 = new Esercizio1();

                                                    esercizio1.setId_esercizio(snapshot1.child("id_esercizio").getValue().toString());
                                                    esercizio1.setAiuto_1(snapshot1.child("aiuto_1").getValue().toString());
                                                    esercizio1.setAiuto_2(snapshot1.child("aiuto_2").getValue().toString());
                                                    esercizio1.setAiuto_3(snapshot1.child("aiuto_3").getValue().toString());
                                                    esercizio1.setUriImage(snapshot1.child("uriImage").getValue().toString());
                                                    esercizio1.setMonete(snapshot1.child("monete").getValue(Integer.class));
                                                    esercizio1.setEsperienza(snapshot1.child("esperienza").getValue(Integer.class));
                                                    esercizio1.setConta_assegnazioni(snapshot1.child("conta_assegnazioni").getValue(Integer.class));

                                                    list_es.add(esercizio1);
                                                }else if(snapshot1.child("id_esercizio").getValue().toString().startsWith("2_")){
                                                    Esercizio2 esercizio2 = new Esercizio2();

                                                    esercizio2.setId_esercizio(snapshot1.child("id_esercizio").getValue().toString());
                                                    esercizio2.setParola_1(snapshot1.child("parola_1").getValue().toString());
                                                    esercizio2.setParola_2(snapshot1.child("parola_2").getValue().toString());
                                                    esercizio2.setParola_3(snapshot1.child("parola_3").getValue().toString());
                                                    esercizio2.setMonete(snapshot1.child("monete").getValue(Integer.class));
                                                    esercizio2.setEsperienza(snapshot1.child("esperienza").getValue(Integer.class));
                                                    esercizio2.setConta_assegnazioni(snapshot1.child("conta_assegnazioni").getValue(Integer.class));

                                                    list_es.add(esercizio2);
                                                }else if(snapshot1.child("id_esercizio").getValue().toString().startsWith("3_")) {
                                                    Esercizio3 esercizio3 = new Esercizio3();

                                                    esercizio3.setId_esercizio(snapshot1.child("id_esercizio").getValue().toString());
                                                    esercizio3.setUriImage_sbagliata(snapshot1.child("uriImage_sbagliata").getValue().toString());
                                                    esercizio3.setUriImage_corretta(snapshot1.child("uriImage_corretta").getValue().toString());
                                                    esercizio3.setMonete(snapshot1.child("monete").getValue(Integer.class));
                                                    esercizio3.setEsperienza(snapshot1.child("esperienza").getValue(Integer.class));
                                                    esercizio3.setConta_assegnazioni(snapshot1.child("conta_assegnazioni").getValue(Integer.class));

                                                    list_es.add(esercizio3);
                                                }

                                                myAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //ELIMINA TERAPIA

                        delete_therapy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog delete_item = new Dialog(dialog.getContext());

                                dialog.dismiss();
                                delete_item.setContentView(R.layout.confirm_delete_therapy);
                                Button confirm , discard;

                                confirm = delete_item.findViewById(R.id.confirm_delete);
                                discard = delete_item.findViewById(R.id.discard_delete);

                                confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //DE-ASSEGNA ESERCIZIO 1
                                        Query get_id_esercizio_1 = database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Pazienti")
                                                .child(cfPaziente)
                                                .child("Terapie")
                                                .child(list.get(pos).toString())
                                                .child("esercizio_1");

                                        get_id_esercizio_1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String id_esercizio_1 = dataSnapshot.child("id_esercizio").getValue().toString();
                                                    Query count_assign = database.getReference("Utenti")
                                                            .child("Logopedisti")
                                                            .child(sessionKey)
                                                            .child("Esercizi")
                                                            .child(id_esercizio_1)
                                                            .child("conta_assegnazioni");

                                                    count_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                            conta_assegnazioni = snapshot1.getValue(Integer.class);
                                                            conta_assegnazioni--;
                                                            snapshot1.getRef().setValue(conta_assegnazioni);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    if(dataSnapshot.child("audio_soluzione").exists()){
                                                        String audio_path = dataSnapshot.child("audio_soluzione").getValue(String.class);

                                                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                        StorageReference storageReference = storage.getReference(audio_path.substring(1));
                                                        storageReference.delete();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        conta_assegnazioni = 0;

                                        //DE-ASSEGNA ESERCIZIO 2
                                        Query get_id_esercizio_2 = database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Pazienti")
                                                .child(cfPaziente)
                                                .child("Terapie")
                                                .child(list.get(pos).toString())
                                                .child("esercizio_2");

                                        get_id_esercizio_2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String id_esercizio_2 = dataSnapshot.child("id_esercizio").getValue().toString();
                                                    Query count_assign = database.getReference("Utenti")
                                                            .child("Logopedisti")
                                                            .child(sessionKey)
                                                            .child("Esercizi")
                                                            .child(id_esercizio_2)
                                                            .child("conta_assegnazioni");

                                                    count_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                            conta_assegnazioni = snapshot1.getValue(Integer.class);
                                                            conta_assegnazioni--;
                                                            snapshot1.getRef().setValue(conta_assegnazioni);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    if(dataSnapshot.child("audio_soluzione").exists()){
                                                        String audio_path = dataSnapshot.child("audio_soluzione").getValue(String.class);

                                                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                        StorageReference storageReference = storage.getReference(audio_path.substring(1));
                                                        storageReference.delete();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        conta_assegnazioni = 0;

                                        //DE-ASSEGNA ESERCIZIO 3
                                        Query get_id_esercizio_3 = database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Pazienti")
                                                .child(cfPaziente)
                                                .child("Terapie")
                                                .child(list.get(pos).toString())
                                                .child("esercizio_3");

                                        get_id_esercizio_3.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String id_esercizio_3 = dataSnapshot.child("id_esercizio").getValue().toString();
                                                    Query count_assign = database.getReference("Utenti")
                                                            .child("Logopedisti")
                                                            .child(sessionKey)
                                                            .child("Esercizi")
                                                            .child(id_esercizio_3)
                                                            .child("conta_assegnazioni");

                                                    count_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                            conta_assegnazioni = snapshot1.getValue(Integer.class);
                                                            conta_assegnazioni--;
                                                            snapshot1.getRef().setValue(conta_assegnazioni);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    if(dataSnapshot.child("audio_soluzione").exists()){
                                                        String audio_path = dataSnapshot.child("audio_soluzione").getValue(String.class);

                                                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                        StorageReference storageReference = storage.getReference(audio_path.substring(1));
                                                        storageReference.delete();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        conta_assegnazioni = 0;

                                        //DE-ASSEGNA ESERCIZIO 4
                                        Query get_id_esercizio_4 = database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Pazienti")
                                                .child(cfPaziente)
                                                .child("Terapie")
                                                .child(list.get(pos).toString())
                                                .child("esercizio_4");

                                        get_id_esercizio_4.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String id_esercizio_4 = dataSnapshot.child("id_esercizio").getValue().toString();
                                                    Query count_assign = database.getReference("Utenti")
                                                            .child("Logopedisti")
                                                            .child(sessionKey)
                                                            .child("Esercizi")
                                                            .child(id_esercizio_4)
                                                            .child("conta_assegnazioni");

                                                    count_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                            conta_assegnazioni = snapshot1.getValue(Integer.class);
                                                            conta_assegnazioni--;
                                                            snapshot1.getRef().setValue(conta_assegnazioni);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    if(dataSnapshot.child("audio_soluzione").exists()){
                                                        String audio_path = dataSnapshot.child("audio_soluzione").getValue(String.class);

                                                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                        StorageReference storageReference = storage.getReference(audio_path.substring(1));
                                                        storageReference.delete();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        conta_assegnazioni = 0;

                                        //DE-ASSEGNA ESERCIZIO 5
                                        Query get_id_esercizio_5 = database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Pazienti")
                                                .child(cfPaziente)
                                                .child("Terapie")
                                                .child(list.get(pos).toString())
                                                .child("esercizio_5");

                                        get_id_esercizio_5.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String id_esercizio_5 = dataSnapshot.child("id_esercizio").getValue().toString();
                                                    Query count_assign = database.getReference("Utenti")
                                                            .child("Logopedisti")
                                                            .child(sessionKey)
                                                            .child("Esercizi")
                                                            .child(id_esercizio_5)
                                                            .child("conta_assegnazioni");

                                                    count_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                            conta_assegnazioni = snapshot1.getValue(Integer.class);
                                                            conta_assegnazioni--;
                                                            snapshot1.getRef().setValue(conta_assegnazioni);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    if(dataSnapshot.child("audio_soluzione").exists()){
                                                        String audio_path = dataSnapshot.child("audio_soluzione").getValue(String.class);

                                                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                        StorageReference storageReference = storage.getReference(audio_path.substring(1));
                                                        storageReference.delete();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        conta_assegnazioni = 0;

                                        database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Pazienti")
                                                .child(cfPaziente)
                                                .child("Terapie")
                                                .child(list.get(pos).toString())
                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });

                                        Toast.makeText(v.getContext(), R.string.terapia_eliminata, Toast.LENGTH_SHORT).show();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(container.getId() , ElencoPazienti.newInstance(sessionKey));
                                        fragmentTransaction.commit();
                                        delete_item.dismiss();
                                    }
                                });

                                discard.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        delete_item.dismiss();
                                        dialog.dismiss();
                                    }
                                });

                                delete_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                delete_item.show();
                            }
                        });

                        Button verify_therapy = dialog.findViewById(R.id.verify_therapy);

                        Query correzione_terapia = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(sessionKey)
                                .child("Pazienti")
                                .child(cfPaziente)
                                .child("Terapie")
                                .child(list.get(pos));
                        correzione_terapia.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                        if(dataSnapshot1.child("eseguito").exists()) {
                                            eseguito = true;
                                        }
                                    }
                                }

                                if(eseguito){
                                    verify_therapy.setVisibility(View.VISIBLE);
                                }else{
                                    verify_therapy.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        verify_therapy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Dialog dialog1 = new Dialog(dialog.getContext());

                                dialog.dismiss();
                                dialog1.setContentView(R.layout.view_exercises_results);

                                TextView TVesercizio_1 = dialog1.findViewById(R.id.TVesercizio_1);
                                TextView TVesercizio_2 = dialog1.findViewById(R.id.TVesercizio_2);
                                TextView TVesercizio_3 = dialog1.findViewById(R.id.TVesercizio_3);
                                TextView TVesercizio_4 = dialog1.findViewById(R.id.TVesercizio_4);
                                TextView TVesercizio_5 = dialog1.findViewById(R.id.TVesercizio_5);

                                Query getExercises = database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(sessionKey)
                                        .child("Pazienti")
                                        .child(cfPaziente)
                                        .child("Terapie")
                                        .child(list.get(pos));

                                getExercises.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                if(dataSnapshot.getKey().compareTo("esercizio_1") == 0){
                                                    TVesercizio_1.setText(dataSnapshot1.child("id_esercizio").getValue(String.class));
                                                }
                                                if(dataSnapshot.getKey().compareTo("esercizio_2") == 0){
                                                    TVesercizio_2.setText(dataSnapshot1.child("id_esercizio").getValue(String.class));
                                                }
                                                if(dataSnapshot.getKey().compareTo("esercizio_3") == 0){
                                                    TVesercizio_3.setText(dataSnapshot1.child("id_esercizio").getValue(String.class));
                                                }
                                                if(dataSnapshot.getKey().compareTo("esercizio_4") == 0){
                                                    TVesercizio_4.setText(dataSnapshot1.child("id_esercizio").getValue(String.class));
                                                }
                                                if(dataSnapshot.getKey().compareTo("esercizio_5") == 0){
                                                    TVesercizio_5.setText(dataSnapshot1.child("id_esercizio").getValue(String.class));
                                                }
                                            }
                                        }

                                        //VISUALIZZAZIONE ANDAMENTO ESERCIZI
                                        TVesercizio_1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Query getInfoRisoluzione = database.getReference("Utenti")
                                                        .child("Logopedisti")
                                                        .child(sessionKey)
                                                        .child("Pazienti")
                                                        .child(cfPaziente)
                                                        .child("Terapie")
                                                        .child(list.get(pos))
                                                        .child("esercizio_1");
                                                getInfoRisoluzione.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                            if(dataSnapshot.child("eseguito").exists() &&
                                                                    !TVesercizio_1.getText().toString().startsWith("3_")){
                                                                Dialog info_dialog = new Dialog(dialog1.getContext());
                                                                //dialog1.dismiss();
                                                                info_dialog.setContentView(R.layout.esercizio_svolto_popup);

                                                                TextView id_esercizio1_popup = info_dialog.findViewById(R.id.id_esercizio1_correzione_popup);
                                                                Button riproduci_soluzione = info_dialog.findViewById(R.id.riproduci_soluzione);
                                                                Button ferma_riproduzione = info_dialog.findViewById(R.id.ferma_riproduzione);
                                                                Button visualizza_esercizio = info_dialog.findViewById(R.id.visualizza_esercizio);

                                                                TextView esercizio_svolto_text_close = info_dialog.findViewById(R.id.esercizio_svolto_text_close);

                                                                esercizio_svolto_text_close.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        info_dialog.dismiss();
                                                                    }
                                                                });

                                                                TextView correzione = info_dialog.findViewById(R.id.correzione);
                                                                //BOTTONI CORREZIONE
                                                                Button esercizio_corretto = info_dialog.findViewById(R.id.esercizio_corretto);
                                                                Button esercizio_sbagliato = info_dialog.findViewById(R.id.esercizio_sbagliato);

                                                                id_esercizio1_popup.setText(dataSnapshot.child("id_esercizio").getValue(String.class));

                                                                FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                StorageReference storageReference = storage.getReference(dataSnapshot.child("audio_soluzione").getValue(String.class).substring(1));

                                                                riproduci_soluzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        riproduci_soluzione.setVisibility(View.GONE);
                                                                        ferma_riproduzione.setVisibility(View.VISIBLE);
                                                                        try {
                                                                            File file = File.createTempFile("tempfile" , ".3gp");
                                                                            mediaPlayer = new MediaPlayer();
                                                                            storageReference.getFile(file)
                                                                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                            try {
                                                                                                mediaPlayer.setDataSource(info_dialog.getContext() , Uri.fromFile(file));
                                                                                                mediaPlayer.prepare();
                                                                                                mediaPlayer.start();
                                                                                            } catch (
                                                                                                    IOException e) {
                                                                                                throw new RuntimeException(e);
                                                                                            }
                                                                                        }
                                                                                    });

                                                                        } catch (IOException e) {
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                    }
                                                                });

                                                                ferma_riproduzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        mediaPlayer.stop();
                                                                        mediaPlayer.release();

                                                                        ferma_riproduzione.setVisibility(View.GONE);
                                                                        riproduci_soluzione.setVisibility(View.VISIBLE);
                                                                    }
                                                                });

                                                                if(dataSnapshot.child("esito").exists()){
                                                                    if(dataSnapshot.child("esito").getValue(Boolean.class)){
                                                                        correzione.setText(view.getResources().getText(R.string.correzione_corretto));
                                                                    }else{
                                                                        correzione.setText(view.getResources().getText(R.string.correzione_sbagliato));
                                                                    }

                                                                    esercizio_corretto.setVisibility(View.GONE);
                                                                    esercizio_sbagliato.setVisibility(View.GONE);
                                                                }

                                                                esercizio_corretto.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+50).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }
                                                                });

                                                                esercizio_sbagliato.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+20).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_sbagliato);

                                                                    }
                                                                });

                                                                visualizza_esercizio.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("1_")){
                                                                            Dialog mDialog , delete_item;
                                                                            TextView txtClose , id_esercizio , aiuto_1 , aiuto_2 , aiuto_3;
                                                                            ImageView imageView;

                                                                            mDialog = new Dialog(v.getContext());

                                                                            mDialog.setContentView(R.layout.exercise1_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio1_popup);
                                                                            aiuto_1 = (TextView) mDialog.findViewById(R.id.aiuto1_tv);
                                                                            aiuto_2 = (TextView) mDialog.findViewById(R.id.aiuto2_tv);
                                                                            aiuto_3 = (TextView) mDialog.findViewById(R.id.aiuto3_tv);
                                                                            imageView = (ImageView) mDialog.findViewById(R.id.ex1_image_viewer);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_1.getText().toString());

                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio1 esercizio1 = snapshot.getValue(Esercizio1.class);

                                                                                    id_esercizio.setText(esercizio1.getId_esercizio());
                                                                                    aiuto_1.setText(view.getResources().getText(R.string.aiuto_1_colon) + esercizio1.getAiuto_1());
                                                                                    aiuto_2.setText(view.getResources().getText(R.string.aiuto_2_colon) + esercizio1.getAiuto_2());
                                                                                    aiuto_3.setText(view.getResources().getText(R.string.aiuto_3_colon) + esercizio1.getAiuto_3());


                                                                                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                                    StorageReference storageReference = storage.getReference(esercizio1.getUriImage().substring(1));
                                                                                    Log.d("Image_Path", "Image Path: " + storageReference.toString());

                                                                                    try {
                                                                                        File file = File.createTempFile("tempfile" , ".jpg");

                                                                                        storageReference.getFile(file)
                                                                                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                                                                                        imageView.setImageBitmap(bitmap);
                                                                                                    }
                                                                                                });

                                                                                    } catch (IOException e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }

                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });

                                                                            //ESERCIZIO 2
                                                                        }else if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("2_")){
                                                                            Dialog mDialog;
                                                                            TextView txtClose , id_esercizio ,  parola_1  , parola_2 , parola_3 , cfPaziente;

                                                                            mDialog = new Dialog(v.getContext());
                                                                            mDialog.setContentView(R.layout.exercise2_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise2_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio2_popup);
                                                                            parola_1 = (TextView) mDialog.findViewById(R.id.parola1_tv);
                                                                            parola_2 = (TextView) mDialog.findViewById(R.id.parola2_tv);
                                                                            parola_3 = (TextView) mDialog.findViewById(R.id.parola3_tv);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_1.getText().toString());;
                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio2 esercizio2 = snapshot.getValue(Esercizio2.class);

                                                                                    id_esercizio.setText(esercizio2.getId_esercizio());
                                                                                    parola_1.setText(view.getResources().getText(R.string.parola_1_colon) + esercizio2.getParola_1());
                                                                                    parola_2.setText(view.getResources().getText(R.string.parola_2_colon) + esercizio2.getParola_2());
                                                                                    parola_3.setText(view.getResources().getText(R.string.parola_3_colon) + esercizio2.getParola_3());

                                                                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");


                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });

                                                                info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                info_dialog.show();
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                        TVesercizio_2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Query getInfoRisoluzione = database.getReference("Utenti")
                                                        .child("Logopedisti")
                                                        .child(sessionKey)
                                                        .child("Pazienti")
                                                        .child(cfPaziente)
                                                        .child("Terapie")
                                                        .child(list.get(pos))
                                                        .child("esercizio_2");
                                                getInfoRisoluzione.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                            if(dataSnapshot.child("eseguito").exists() &&
                                                                    !TVesercizio_2.getText().toString().startsWith("3_")){
                                                                Dialog info_dialog = new Dialog(dialog1.getContext());
                                                                //dialog1.dismiss();
                                                                info_dialog.setContentView(R.layout.esercizio_svolto_popup);

                                                                TextView id_esercizio1_popup = info_dialog.findViewById(R.id.id_esercizio1_correzione_popup);
                                                                Button riproduci_soluzione = info_dialog.findViewById(R.id.riproduci_soluzione);
                                                                Button ferma_riproduzione = info_dialog.findViewById(R.id.ferma_riproduzione);
                                                                Button visualizza_esercizio = info_dialog.findViewById(R.id.visualizza_esercizio);

                                                                TextView esercizio_svolto_text_close = info_dialog.findViewById(R.id.esercizio_svolto_text_close);

                                                                esercizio_svolto_text_close.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        info_dialog.dismiss();
                                                                    }
                                                                });

                                                                TextView correzione = info_dialog.findViewById(R.id.correzione);
                                                                //BOTTONI CORREZIONE
                                                                Button esercizio_corretto = info_dialog.findViewById(R.id.esercizio_corretto);
                                                                Button esercizio_sbagliato = info_dialog.findViewById(R.id.esercizio_sbagliato);

                                                                id_esercizio1_popup.setText(dataSnapshot.child("id_esercizio").getValue(String.class));

                                                                FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                StorageReference storageReference = storage.getReference(dataSnapshot.child("audio_soluzione").getValue(String.class).substring(1));

                                                                riproduci_soluzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        riproduci_soluzione.setVisibility(View.GONE);
                                                                        ferma_riproduzione.setVisibility(View.VISIBLE);
                                                                        try {
                                                                            File file = File.createTempFile("tempfile" , ".3gp");
                                                                            mediaPlayer = new MediaPlayer();
                                                                            storageReference.getFile(file)
                                                                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                            try {
                                                                                                mediaPlayer.setDataSource(info_dialog.getContext() , Uri.fromFile(file));
                                                                                                mediaPlayer.prepare();
                                                                                                mediaPlayer.start();
                                                                                            } catch (
                                                                                                    IOException e) {
                                                                                                throw new RuntimeException(e);
                                                                                            }
                                                                                        }
                                                                                    });

                                                                        } catch (IOException e) {
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                    }
                                                                });

                                                                ferma_riproduzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        mediaPlayer.stop();
                                                                        mediaPlayer.release();

                                                                        ferma_riproduzione.setVisibility(View.GONE);
                                                                        riproduci_soluzione.setVisibility(View.VISIBLE);
                                                                    }
                                                                });

                                                                if(dataSnapshot.child("esito").exists()){
                                                                    if(dataSnapshot.child("esito").getValue(Boolean.class)){
                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }else{
                                                                        correzione.setText(R.string.correzione_sbagliato);
                                                                    }

                                                                    esercizio_corretto.setVisibility(View.GONE);
                                                                    esercizio_sbagliato.setVisibility(View.GONE);
                                                                }

                                                                esercizio_corretto.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+50).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }
                                                                });

                                                                esercizio_sbagliato.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+20).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_sbagliato);
                                                                    }
                                                                });

                                                                visualizza_esercizio.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("1_")){
                                                                            Dialog mDialog , delete_item;
                                                                            TextView txtClose , id_esercizio , aiuto_1 , aiuto_2 , aiuto_3;
                                                                            ImageView imageView;

                                                                            mDialog = new Dialog(v.getContext());

                                                                            mDialog.setContentView(R.layout.exercise1_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio1_popup);
                                                                            aiuto_1 = (TextView) mDialog.findViewById(R.id.aiuto1_tv);
                                                                            aiuto_2 = (TextView) mDialog.findViewById(R.id.aiuto2_tv);
                                                                            aiuto_3 = (TextView) mDialog.findViewById(R.id.aiuto3_tv);
                                                                            imageView = (ImageView) mDialog.findViewById(R.id.ex1_image_viewer);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_2.getText().toString());

                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio1 esercizio1 = snapshot.getValue(Esercizio1.class);

                                                                                    id_esercizio.setText(esercizio1.getId_esercizio());
                                                                                    aiuto_1.setText(view.getResources().getText(R.string.aiuto_1_colon) + esercizio1.getAiuto_1());
                                                                                    aiuto_2.setText(view.getResources().getText(R.string.aiuto_2_colon) + esercizio1.getAiuto_2());
                                                                                    aiuto_3.setText(view.getResources().getText(R.string.aiuto_3_colon) + esercizio1.getAiuto_3());


                                                                                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                                    StorageReference storageReference = storage.getReference(esercizio1.getUriImage().substring(1));
                                                                                    Log.d("Image_Path", "Image Path: " + storageReference.toString());

                                                                                    try {
                                                                                        File file = File.createTempFile("tempfile" , ".jpg");

                                                                                        storageReference.getFile(file)
                                                                                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                                                                                        imageView.setImageBitmap(bitmap);
                                                                                                    }
                                                                                                });

                                                                                    } catch (IOException e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }

                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });

                                                                            //ESERCIZIO 2
                                                                        }else if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("2_")){
                                                                            Dialog mDialog;
                                                                            TextView txtClose , id_esercizio ,  parola_1  , parola_2 , parola_3 , cfPaziente;

                                                                            mDialog = new Dialog(v.getContext());
                                                                            mDialog.setContentView(R.layout.exercise2_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise2_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio2_popup);
                                                                            parola_1 = (TextView) mDialog.findViewById(R.id.parola1_tv);
                                                                            parola_2 = (TextView) mDialog.findViewById(R.id.parola2_tv);
                                                                            parola_3 = (TextView) mDialog.findViewById(R.id.parola3_tv);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_2.getText().toString());;
                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio2 esercizio2 = snapshot.getValue(Esercizio2.class);

                                                                                    id_esercizio.setText(esercizio2.getId_esercizio());
                                                                                    parola_1.setText(view.getResources().getText(R.string.parola_1_colon) + esercizio2.getParola_1());
                                                                                    parola_2.setText(view.getResources().getText(R.string.parola_2_colon) + esercizio2.getParola_2());
                                                                                    parola_3.setText(view.getResources().getText(R.string.parola_3_colon) + esercizio2.getParola_3());

                                                                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");


                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });

                                                                info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                info_dialog.show();
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                        TVesercizio_3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Query getInfoRisoluzione = database.getReference("Utenti")
                                                        .child("Logopedisti")
                                                        .child(sessionKey)
                                                        .child("Pazienti")
                                                        .child(cfPaziente)
                                                        .child("Terapie")
                                                        .child(list.get(pos))
                                                        .child("esercizio_3");
                                                getInfoRisoluzione.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                            if(dataSnapshot.child("eseguito").exists() &&
                                                                    !TVesercizio_3.getText().toString().startsWith("3_")){
                                                                Dialog info_dialog = new Dialog(dialog1.getContext());
                                                                //dialog1.dismiss();
                                                                info_dialog.setContentView(R.layout.esercizio_svolto_popup);

                                                                TextView id_esercizio1_popup = info_dialog.findViewById(R.id.id_esercizio1_correzione_popup);
                                                                Button riproduci_soluzione = info_dialog.findViewById(R.id.riproduci_soluzione);
                                                                Button ferma_riproduzione = info_dialog.findViewById(R.id.ferma_riproduzione);
                                                                Button visualizza_esercizio = info_dialog.findViewById(R.id.visualizza_esercizio);

                                                                TextView esercizio_svolto_text_close = info_dialog.findViewById(R.id.esercizio_svolto_text_close);

                                                                esercizio_svolto_text_close.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        info_dialog.dismiss();
                                                                    }
                                                                });

                                                                TextView correzione = info_dialog.findViewById(R.id.correzione);
                                                                //BOTTONI CORREZIONE
                                                                Button esercizio_corretto = info_dialog.findViewById(R.id.esercizio_corretto);
                                                                Button esercizio_sbagliato = info_dialog.findViewById(R.id.esercizio_sbagliato);

                                                                id_esercizio1_popup.setText(dataSnapshot.child("id_esercizio").getValue(String.class));

                                                                FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                StorageReference storageReference = storage.getReference(dataSnapshot.child("audio_soluzione").getValue(String.class).substring(1));

                                                                riproduci_soluzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        riproduci_soluzione.setVisibility(View.GONE);
                                                                        ferma_riproduzione.setVisibility(View.VISIBLE);
                                                                        try {
                                                                            File file = File.createTempFile("tempfile" , ".3gp");
                                                                            mediaPlayer = new MediaPlayer();
                                                                            storageReference.getFile(file)
                                                                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                            try {
                                                                                                mediaPlayer.setDataSource(info_dialog.getContext() , Uri.fromFile(file));
                                                                                                mediaPlayer.prepare();
                                                                                                mediaPlayer.start();
                                                                                            } catch (
                                                                                                    IOException e) {
                                                                                                throw new RuntimeException(e);
                                                                                            }
                                                                                        }
                                                                                    });

                                                                        } catch (IOException e) {
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                    }
                                                                });

                                                                ferma_riproduzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        mediaPlayer.stop();
                                                                        mediaPlayer.release();

                                                                        ferma_riproduzione.setVisibility(View.GONE);
                                                                        riproduci_soluzione.setVisibility(View.VISIBLE);
                                                                    }
                                                                });

                                                                if(dataSnapshot.child("esito").exists()){
                                                                    if(dataSnapshot.child("esito").getValue(Boolean.class)){
                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }else{
                                                                        correzione.setText(R.string.correzione_sbagliato);
                                                                    }

                                                                    esercizio_corretto.setVisibility(View.GONE);
                                                                    esercizio_sbagliato.setVisibility(View.GONE);
                                                                }

                                                                esercizio_corretto.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+50).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }
                                                                });

                                                                esercizio_sbagliato.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+20).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_sbagliato);
                                                                    }
                                                                });

                                                                visualizza_esercizio.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("1_")){
                                                                            Dialog mDialog , delete_item;
                                                                            TextView txtClose , id_esercizio , aiuto_1 , aiuto_2 , aiuto_3;
                                                                            ImageView imageView;

                                                                            mDialog = new Dialog(v.getContext());

                                                                            mDialog.setContentView(R.layout.exercise1_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio1_popup);
                                                                            aiuto_1 = (TextView) mDialog.findViewById(R.id.aiuto1_tv);
                                                                            aiuto_2 = (TextView) mDialog.findViewById(R.id.aiuto2_tv);
                                                                            aiuto_3 = (TextView) mDialog.findViewById(R.id.aiuto3_tv);
                                                                            imageView = (ImageView) mDialog.findViewById(R.id.ex1_image_viewer);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_3.getText().toString());

                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio1 esercizio1 = snapshot.getValue(Esercizio1.class);

                                                                                    id_esercizio.setText(esercizio1.getId_esercizio());
                                                                                    aiuto_1.setText(view.getResources().getText(R.string.aiuto_1_colon) + esercizio1.getAiuto_1());
                                                                                    aiuto_2.setText(view.getResources().getText(R.string.aiuto_2_colon) + esercizio1.getAiuto_2());
                                                                                    aiuto_3.setText(view.getResources().getText(R.string.aiuto_3_colon) + esercizio1.getAiuto_3());


                                                                                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                                    StorageReference storageReference = storage.getReference(esercizio1.getUriImage().substring(1));
                                                                                    Log.d("Image_Path", "Image Path: " + storageReference.toString());

                                                                                    try {
                                                                                        File file = File.createTempFile("tempfile" , ".jpg");

                                                                                        storageReference.getFile(file)
                                                                                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                                                                                        imageView.setImageBitmap(bitmap);
                                                                                                    }
                                                                                                });

                                                                                    } catch (IOException e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }

                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });

                                                                            //ESERCIZIO 2
                                                                        }else if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("2_")){
                                                                            Dialog mDialog;
                                                                            TextView txtClose , id_esercizio ,  parola_1  , parola_2 , parola_3 , cfPaziente;

                                                                            mDialog = new Dialog(v.getContext());
                                                                            mDialog.setContentView(R.layout.exercise2_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise2_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio2_popup);
                                                                            parola_1 = (TextView) mDialog.findViewById(R.id.parola1_tv);
                                                                            parola_2 = (TextView) mDialog.findViewById(R.id.parola2_tv);
                                                                            parola_3 = (TextView) mDialog.findViewById(R.id.parola3_tv);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_3.getText().toString());;
                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio2 esercizio2 = snapshot.getValue(Esercizio2.class);

                                                                                    id_esercizio.setText(esercizio2.getId_esercizio());
                                                                                    parola_1.setText(view.getResources().getText(R.string.parola_1_colon) + esercizio2.getParola_1());
                                                                                    parola_2.setText(view.getResources().getText(R.string.parola_2_colon) + esercizio2.getParola_2());
                                                                                    parola_3.setText(view.getResources().getText(R.string.parola_3_colon) + esercizio2.getParola_3());

                                                                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");


                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });

                                                                info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                info_dialog.show();
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                        TVesercizio_4.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Query getInfoRisoluzione = database.getReference("Utenti")
                                                        .child("Logopedisti")
                                                        .child(sessionKey)
                                                        .child("Pazienti")
                                                        .child(cfPaziente)
                                                        .child("Terapie")
                                                        .child(list.get(pos))
                                                        .child("esercizio_4");
                                                getInfoRisoluzione.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                            if(dataSnapshot.child("eseguito").exists() &&
                                                                    !TVesercizio_4.getText().toString().startsWith("3_")){
                                                                Dialog info_dialog = new Dialog(dialog1.getContext());
                                                                //dialog1.dismiss();
                                                                info_dialog.setContentView(R.layout.esercizio_svolto_popup);

                                                                TextView id_esercizio1_popup = info_dialog.findViewById(R.id.id_esercizio1_correzione_popup);
                                                                Button riproduci_soluzione = info_dialog.findViewById(R.id.riproduci_soluzione);
                                                                Button ferma_riproduzione = info_dialog.findViewById(R.id.ferma_riproduzione);
                                                                Button visualizza_esercizio = info_dialog.findViewById(R.id.visualizza_esercizio);

                                                                TextView esercizio_svolto_text_close = info_dialog.findViewById(R.id.esercizio_svolto_text_close);

                                                                esercizio_svolto_text_close.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        info_dialog.dismiss();
                                                                    }
                                                                });

                                                                TextView correzione = info_dialog.findViewById(R.id.correzione);
                                                                //BOTTONI CORREZIONE
                                                                Button esercizio_corretto = info_dialog.findViewById(R.id.esercizio_corretto);
                                                                Button esercizio_sbagliato = info_dialog.findViewById(R.id.esercizio_sbagliato);

                                                                id_esercizio1_popup.setText(dataSnapshot.child("id_esercizio").getValue(String.class));

                                                                FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                StorageReference storageReference = storage.getReference(dataSnapshot.child("audio_soluzione").getValue(String.class).substring(1));

                                                                riproduci_soluzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        riproduci_soluzione.setVisibility(View.GONE);
                                                                        ferma_riproduzione.setVisibility(View.VISIBLE);
                                                                        try {
                                                                            File file = File.createTempFile("tempfile" , ".3gp");
                                                                            mediaPlayer = new MediaPlayer();
                                                                            storageReference.getFile(file)
                                                                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                            try {
                                                                                                mediaPlayer.setDataSource(info_dialog.getContext() , Uri.fromFile(file));
                                                                                                mediaPlayer.prepare();
                                                                                                mediaPlayer.start();
                                                                                            } catch (
                                                                                                    IOException e) {
                                                                                                throw new RuntimeException(e);
                                                                                            }
                                                                                        }
                                                                                    });

                                                                        } catch (IOException e) {
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                    }
                                                                });

                                                                ferma_riproduzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        mediaPlayer.stop();
                                                                        mediaPlayer.release();

                                                                        ferma_riproduzione.setVisibility(View.GONE);
                                                                        riproduci_soluzione.setVisibility(View.VISIBLE);
                                                                    }
                                                                });

                                                                if(dataSnapshot.child("esito").exists()){
                                                                    if(dataSnapshot.child("esito").getValue(Boolean.class)){
                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }else{
                                                                        correzione.setText(R.string.correzione_sbagliato);
                                                                    }

                                                                    esercizio_corretto.setVisibility(View.GONE);
                                                                    esercizio_sbagliato.setVisibility(View.GONE);
                                                                }

                                                                esercizio_corretto.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+50).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }
                                                                });

                                                                esercizio_sbagliato.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+20).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_sbagliato);
                                                                    }
                                                                });

                                                                visualizza_esercizio.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("1_")){
                                                                            Dialog mDialog , delete_item;
                                                                            TextView txtClose , id_esercizio , aiuto_1 , aiuto_2 , aiuto_3;
                                                                            ImageView imageView;

                                                                            mDialog = new Dialog(v.getContext());

                                                                            mDialog.setContentView(R.layout.exercise1_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio1_popup);
                                                                            aiuto_1 = (TextView) mDialog.findViewById(R.id.aiuto1_tv);
                                                                            aiuto_2 = (TextView) mDialog.findViewById(R.id.aiuto2_tv);
                                                                            aiuto_3 = (TextView) mDialog.findViewById(R.id.aiuto3_tv);
                                                                            imageView = (ImageView) mDialog.findViewById(R.id.ex1_image_viewer);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_4.getText().toString());

                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio1 esercizio1 = snapshot.getValue(Esercizio1.class);

                                                                                    id_esercizio.setText(esercizio1.getId_esercizio());
                                                                                    aiuto_1.setText(view.getResources().getText(R.string.aiuto_1_colon) + esercizio1.getAiuto_1());
                                                                                    aiuto_2.setText(view.getResources().getText(R.string.aiuto_2_colon) + esercizio1.getAiuto_2());
                                                                                    aiuto_3.setText(view.getResources().getText(R.string.aiuto_3_colon) + esercizio1.getAiuto_3());


                                                                                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                                    StorageReference storageReference = storage.getReference(esercizio1.getUriImage().substring(1));
                                                                                    Log.d("Image_Path", "Image Path: " + storageReference.toString());

                                                                                    try {
                                                                                        File file = File.createTempFile("tempfile" , ".jpg");

                                                                                        storageReference.getFile(file)
                                                                                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                                                                                        imageView.setImageBitmap(bitmap);
                                                                                                    }
                                                                                                });

                                                                                    } catch (IOException e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }

                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });

                                                                            //ESERCIZIO 2
                                                                        }else if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("2_")){
                                                                            Dialog mDialog;
                                                                            TextView txtClose , id_esercizio ,  parola_1  , parola_2 , parola_3 , cfPaziente;

                                                                            mDialog = new Dialog(v.getContext());
                                                                            mDialog.setContentView(R.layout.exercise2_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise2_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio2_popup);
                                                                            parola_1 = (TextView) mDialog.findViewById(R.id.parola1_tv);
                                                                            parola_2 = (TextView) mDialog.findViewById(R.id.parola2_tv);
                                                                            parola_3 = (TextView) mDialog.findViewById(R.id.parola3_tv);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_4.getText().toString());;
                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio2 esercizio2 = snapshot.getValue(Esercizio2.class);

                                                                                    id_esercizio.setText(esercizio2.getId_esercizio());
                                                                                    parola_1.setText(view.getResources().getText(R.string.parola_1_colon) + esercizio2.getParola_1());
                                                                                    parola_2.setText(view.getResources().getText(R.string.parola_2_colon) + esercizio2.getParola_2());
                                                                                    parola_3.setText(view.getResources().getText(R.string.parola_3_colon) + esercizio2.getParola_3());

                                                                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");


                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });

                                                                info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                info_dialog.show();
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                        TVesercizio_5.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Query getInfoRisoluzione = database.getReference("Utenti")
                                                        .child("Logopedisti")
                                                        .child(sessionKey)
                                                        .child("Pazienti")
                                                        .child(cfPaziente)
                                                        .child("Terapie")
                                                        .child(list.get(pos))
                                                        .child("esercizio_5");
                                                getInfoRisoluzione.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                            if(dataSnapshot.child("eseguito").exists() &&
                                                                    !TVesercizio_5.getText().toString().startsWith("3_")){
                                                                Dialog info_dialog = new Dialog(dialog1.getContext());
                                                                //dialog1.dismiss();
                                                                info_dialog.setContentView(R.layout.esercizio_svolto_popup);

                                                                TextView id_esercizio1_popup = info_dialog.findViewById(R.id.id_esercizio1_correzione_popup);
                                                                Button riproduci_soluzione = info_dialog.findViewById(R.id.riproduci_soluzione);
                                                                Button ferma_riproduzione = info_dialog.findViewById(R.id.ferma_riproduzione);
                                                                Button visualizza_esercizio = info_dialog.findViewById(R.id.visualizza_esercizio);

                                                                TextView esercizio_svolto_text_close = info_dialog.findViewById(R.id.esercizio_svolto_text_close);

                                                                esercizio_svolto_text_close.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        info_dialog.dismiss();
                                                                    }
                                                                });

                                                                TextView correzione = info_dialog.findViewById(R.id.correzione);
                                                                //BOTTONI CORREZIONE
                                                                Button esercizio_corretto = info_dialog.findViewById(R.id.esercizio_corretto);
                                                                Button esercizio_sbagliato = info_dialog.findViewById(R.id.esercizio_sbagliato);

                                                                id_esercizio1_popup.setText(dataSnapshot.child("id_esercizio").getValue(String.class));

                                                                FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                StorageReference storageReference = storage.getReference(dataSnapshot.child("audio_soluzione").getValue(String.class).substring(1));

                                                                riproduci_soluzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        riproduci_soluzione.setVisibility(View.GONE);
                                                                        ferma_riproduzione.setVisibility(View.VISIBLE);
                                                                        try {
                                                                            File file = File.createTempFile("tempfile" , ".3gp");
                                                                            mediaPlayer = new MediaPlayer();
                                                                            storageReference.getFile(file)
                                                                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                            try {
                                                                                                mediaPlayer.setDataSource(info_dialog.getContext() , Uri.fromFile(file));
                                                                                                mediaPlayer.prepare();
                                                                                                mediaPlayer.start();
                                                                                            } catch (
                                                                                                    IOException e) {
                                                                                                throw new RuntimeException(e);
                                                                                            }
                                                                                        }
                                                                                    });

                                                                        } catch (IOException e) {
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                    }
                                                                });

                                                                ferma_riproduzione.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        mediaPlayer.stop();
                                                                        mediaPlayer.release();

                                                                        ferma_riproduzione.setVisibility(View.GONE);
                                                                        riproduci_soluzione.setVisibility(View.VISIBLE);
                                                                    }
                                                                });

                                                                if(dataSnapshot.child("esito").exists()){
                                                                    if(dataSnapshot.child("esito").getValue(Boolean.class)){
                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }else{
                                                                        correzione.setText(R.string.correzione_sbagliato);
                                                                    }

                                                                    esercizio_corretto.setVisibility(View.GONE);
                                                                    esercizio_sbagliato.setVisibility(View.GONE);
                                                                }

                                                                esercizio_corretto.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+50).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+100).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_corretto);
                                                                    }
                                                                });

                                                                esercizio_sbagliato.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        esercizio_corretto.setVisibility(View.GONE);
                                                                        esercizio_sbagliato.setVisibility(View.GONE);

                                                                        dataSnapshot.child("esito").getRef().setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        dataSnapshot.child("corretto").getRef().setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                        Query getMonete = database.getReference("Utenti")
                                                                                .child("Genitori")
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("monete");
                                                                        getMonete.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int monete = snapshot.getValue(Integer.class);
                                                                                snapshot.getRef().setValue(monete+20).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                .child(cfGenitore)
                                                                                .child("Bambini")
                                                                                .child(cfPaziente)
                                                                                .child("esperienza");
                                                                        aggiorna_progressi_exp.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                int esperienza = snapshot.getValue(Integer.class);

                                                                                snapshot.getRef().setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                });

                                                                                database.getReference("Utenti")
                                                                                        .child("Logopedisti")
                                                                                        .child(sessionKey)
                                                                                        .child("Pazienti")
                                                                                        .child(cfPaziente)
                                                                                        .child("esperienza")
                                                                                        .setValue(esperienza+50).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        correzione.setText(R.string.correzione_sbagliato);
                                                                    }
                                                                });

                                                                visualizza_esercizio.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("1_")){
                                                                            Dialog mDialog , delete_item;
                                                                            TextView txtClose , id_esercizio , aiuto_1 , aiuto_2 , aiuto_3;
                                                                            ImageView imageView;

                                                                            mDialog = new Dialog(v.getContext());

                                                                            mDialog.setContentView(R.layout.exercise1_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio1_popup);
                                                                            aiuto_1 = (TextView) mDialog.findViewById(R.id.aiuto1_tv);
                                                                            aiuto_2 = (TextView) mDialog.findViewById(R.id.aiuto2_tv);
                                                                            aiuto_3 = (TextView) mDialog.findViewById(R.id.aiuto3_tv);
                                                                            imageView = (ImageView) mDialog.findViewById(R.id.ex1_image_viewer);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_5.getText().toString());

                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio1 esercizio1 = snapshot.getValue(Esercizio1.class);

                                                                                    id_esercizio.setText(esercizio1.getId_esercizio());
                                                                                    aiuto_1.setText("Aiuto 1: " + esercizio1.getAiuto_1());
                                                                                    aiuto_2.setText("Aiuto 2: " + esercizio1.getAiuto_2());
                                                                                    aiuto_3.setText("Aiuto 3: " + esercizio1.getAiuto_3());


                                                                                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                                                                    StorageReference storageReference = storage.getReference(esercizio1.getUriImage().substring(1));
                                                                                    Log.d("Image_Path", "Image Path: " + storageReference.toString());

                                                                                    try {
                                                                                        File file = File.createTempFile("tempfile" , ".jpg");

                                                                                        storageReference.getFile(file)
                                                                                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                                                                                        imageView.setImageBitmap(bitmap);
                                                                                                    }
                                                                                                });

                                                                                    } catch (IOException e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }

                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });

                                                                            //ESERCIZIO 2
                                                                        }else if(dataSnapshot.child("id_esercizio").getValue(String.class).startsWith("2_")){
                                                                            Dialog mDialog;
                                                                            TextView txtClose , id_esercizio ,  parola_1  , parola_2 , parola_3 , cfPaziente;

                                                                            mDialog = new Dialog(v.getContext());
                                                                            mDialog.setContentView(R.layout.exercise2_info_popup);

                                                                            txtClose = (TextView) mDialog.findViewById(R.id.exercise2_text_close);
                                                                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio2_popup);
                                                                            parola_1 = (TextView) mDialog.findViewById(R.id.parola1_tv);
                                                                            parola_2 = (TextView) mDialog.findViewById(R.id.parola2_tv);
                                                                            parola_3 = (TextView) mDialog.findViewById(R.id.parola3_tv);

                                                                            Query getExercise = database.getReference("Utenti")
                                                                                    .child("Logopedisti")
                                                                                    .child(sessionKey)
                                                                                    .child("Esercizi")
                                                                                    .child(TVesercizio_5.getText().toString());;
                                                                            getExercise.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Esercizio2 esercizio2 = snapshot.getValue(Esercizio2.class);

                                                                                    id_esercizio.setText(esercizio2.getId_esercizio());
                                                                                    parola_1.setText(view.getResources().getText(R.string.parola_1_colon) + esercizio2.getParola_1());
                                                                                    parola_2.setText(view.getResources().getText(R.string.parola_2_colon) + esercizio2.getParola_2());
                                                                                    parola_3.setText(view.getResources().getText(R.string.parola_3_colon) + esercizio2.getParola_3());

                                                                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");


                                                                                    txtClose.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            mDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                    mDialog.show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });

                                                                info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                info_dialog.show();
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog1.show();
                            }
                        });

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                }
            });

        }
    }

}