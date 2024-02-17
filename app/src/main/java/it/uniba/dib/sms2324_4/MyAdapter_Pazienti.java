package it.uniba.dib.sms2324_4;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import it.uniba.dib.sms2324_4.fragment.ElencoEsercizi;
import it.uniba.dib.sms2324_4.fragment.ElencoPazienti;

public class MyAdapter_Pazienti extends RecyclerView.Adapter<MyAdapter_Pazienti.MyViewHolder> {


    ArrayList<Paziente> list;
    Context context;
    String sessionKey;
    ViewGroup container;
    FragmentManager fragmentManager;


    public MyAdapter_Pazienti(Context context, ArrayList<Paziente> list,String sessionKey,ViewGroup container , FragmentManager fragmentManager) {
        this.context = context;
        this.list = list;
        this.sessionKey = sessionKey;
        this.container = container;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.pazienti,parent,false);
        return  new MyViewHolder(v , list , sessionKey,container,fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Paziente paziente = list.get(position);
        String nomeCognome = paziente.getNome() + " " + paziente.getCognome();
        holder.firstName.setText(nomeCognome);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView firstName, lastName, dataDiNascita , cfGenitore;
        String therapy_day_select;
        int count_assegnazioni;

        private final static FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

        public MyViewHolder(@NonNull View itemView , ArrayList<Paziente> list,String sessionKey,ViewGroup container,
                            FragmentManager fragmentManager) {
            super(itemView);
            firstName = itemView.findViewById(R.id.tvpatientName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Dialog mDialog;
                        TextView txtClose , child_name_tv , child_birthdate_tv , child_owner;

                        mDialog = new Dialog(v.getContext());
                        mDialog.setContentView(R.layout.patient_info_popup);
                        txtClose = (TextView) mDialog.findViewById(R.id.txtClose_logo);
                        child_name_tv = (TextView) mDialog.findViewById(R.id.patient_name_tv);
                        child_birthdate_tv = (TextView) mDialog.findViewById(R.id.patient_birthdate_tv);
                        child_owner = (TextView) mDialog.findViewById(R.id.child_owner);

                        String nomeCognome = list.get(pos).getNome() + " "  + list.get(pos).getCognome();
                        String dataDiNascita = list.get(pos).getDataDiNascita();
                        String cfGenitore = list.get(pos).getCfGenitore();

                        child_name_tv.setText(nomeCognome);
                        child_birthdate_tv.setText(dataDiNascita);
                        child_owner.setText(v.getResources().getText(R.string.cf_genitore) + cfGenitore);



                        txtClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });

                        Button assegna_terapia_btn = mDialog.findViewById(R.id.add_therapy_patient);
                        TextView therapy_not_ready = mDialog.findViewById(R.id.therapy_not_ready);

                        //SE NON CI SONO ESERCIZI CREATI
                        Query conta_esercizi = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(sessionKey)
                                .child("Esercizi");
                        conta_esercizi.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int count = 0;
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    count++;
                                }
                                if(count<5){

                                    int eserciziMancanti = 5 - count;
                                    assegna_terapia_btn.setVisibility(View.GONE);
                                    therapy_not_ready.setVisibility(View.VISIBLE);
                                    therapy_not_ready.setText(v.getResources().getText(R.string.aggiungi) + " " + eserciziMancanti + v.getResources().getText(R.string.esercizi_per_creare_una_nuova_terapia));
                                    if(count==4){
                                        therapy_not_ready.setText(v.getResources().getText(R.string.aggiungi_un_altro_esercizio_per_creare_una_nuova_terapia));

                                    }

                                }else{
                                    assegna_terapia_btn.setVisibility(View.VISIBLE);
                                    therapy_not_ready.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Button view_therapy_patient = mDialog.findViewById(R.id.view_therapy_patient);

                        //SWITCH VISUALIZZA TERAPIA
                        Query therapy_exists = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(sessionKey)
                                .child("Pazienti")
                                .child(list.get(pos).getCf())
                                .child("Terapie");
                        therapy_exists.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    view_therapy_patient.setVisibility(View.VISIBLE);
                                }else{
                                    view_therapy_patient.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //ELIMINA PAZIENTE
                        Button elimina_paziente = mDialog.findViewById(R.id.delete_patient);

                        Query conta_terapie = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(sessionKey)
                                .child("Pazienti")
                                .child(list.get(pos).getCf())
                                .child("Terapie");
                        conta_terapie.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    elimina_paziente.setVisibility(View.GONE);
                                }else{
                                    elimina_paziente.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        elimina_paziente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog delete_patient = new Dialog(mDialog.getContext());
                                mDialog.dismiss();
                                delete_patient.setContentView(R.layout.confirm_delete_patient);

                                Button confirm , discard;

                                confirm = delete_patient.findViewById(R.id.confirm_delete);
                                discard = delete_patient.findViewById(R.id.discard_delete);

                                confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Query recupero_esercizi = database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Pazienti")
                                                .child(list.get(pos).getCf())
                                                .child("Terapie");
                                        recupero_esercizi.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                        dataSnapshot1.child("cfPaziente").getRef().setValue("null").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        });

                                                        dataSnapshot1.child("data_assegnazione").getRef().setValue("null").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                        database.getReference("Utenti")
                                                .child("Logopedisti")
                                                .child(sessionKey)
                                                .child("Pazienti")
                                                .child(list.get(pos).getCf())
                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });

                                        database.getReference("Utenti")
                                                .child("Genitori")
                                                .child(cfGenitore)
                                                .child("Bambini")
                                                .child(list.get(pos).getCf())
                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });


                                        delete_patient.dismiss();
                                        mDialog.dismiss();

                                        Toast.makeText(v.getContext(), R.string.paziente_eliminato, Toast.LENGTH_SHORT).show();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(container.getId() , ElencoPazienti.newInstance(sessionKey));
                                        fragmentTransaction.commit();

                                    }
                                });

                                discard.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        delete_patient.dismiss();
                                        mDialog.dismiss();
                                    }
                                });

                                delete_patient.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                delete_patient.show();
                            }
                        });

                        //ASSEGNA TERAPIA
                        Dialog assegna_terapia_dialog = new Dialog(mDialog.getContext());

                        assegna_terapia_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                assegna_terapia_dialog.setContentView(R.layout.assegna_terapia_popup);

                                Button confirm_therapy = assegna_terapia_dialog.findViewById(R.id.confirm_therapy);
                                Button discard_therapy = assegna_terapia_dialog.findViewById(R.id.discard_therapy);

                                Spinner select_exercise1_spinner = assegna_terapia_dialog.findViewById(R.id.select_exercise1_spinner);
                                Spinner select_exercise2_spinner = assegna_terapia_dialog.findViewById(R.id.select_exercise2_spinner);
                                Spinner select_exercise3_spinner = assegna_terapia_dialog.findViewById(R.id.select_exercise3_spinner);
                                Spinner select_exercise4_spinner = assegna_terapia_dialog.findViewById(R.id.select_exercise4_spinner);
                                Spinner select_exercise5_spinner = assegna_terapia_dialog.findViewById(R.id.select_exercise5_spinner);

                                ArrayAdapter<String> id_esercizi_1 = new ArrayAdapter<>(v.getContext() , android.R.layout.simple_list_item_1);
                                ArrayAdapter<String> id_esercizi_2 = new ArrayAdapter<>(v.getContext() , android.R.layout.simple_list_item_1);
                                ArrayAdapter<String> id_esercizi_3 = new ArrayAdapter<>(v.getContext() , android.R.layout.simple_list_item_1);
                                ArrayAdapter<String> id_esercizi_4 = new ArrayAdapter<>(v.getContext() , android.R.layout.simple_list_item_1);
                                ArrayAdapter<String> id_esercizi_5 = new ArrayAdapter<>(v.getContext() , android.R.layout.simple_list_item_1);

                                //SETTING SPINNER
                                FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");
                                Query fetch_exercises = database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(sessionKey)
                                        .child("Esercizi");
                                fetch_exercises.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.exists()){
                                            Toast.makeText(v.getContext(), R.string.nessun_esercizio, Toast.LENGTH_SHORT).show();
                                            assegna_terapia_dialog.dismiss();
                                            mDialog.dismiss();
                                        }else{
                                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                String id_esercizio = dataSnapshot.child("id_esercizio").getValue().toString();
                                                id_esercizi_1.add(id_esercizio);
                                                id_esercizi_2.add(id_esercizio);
                                                id_esercizi_3.add(id_esercizio);
                                                id_esercizi_4.add(id_esercizio);
                                                id_esercizi_5.add(id_esercizio);
                                            }

                                            select_exercise1_spinner.setAdapter(id_esercizi_1);
                                            select_exercise2_spinner.setAdapter(id_esercizi_2);
                                            select_exercise3_spinner.setAdapter(id_esercizi_3);
                                            select_exercise4_spinner.setAdapter(id_esercizi_4);
                                            select_exercise5_spinner.setAdapter(id_esercizi_5);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                //CALENDAR VIEW
                                CalendarView therapy_day = assegna_terapia_dialog.findViewById(R.id.therapy_day);
                                therapy_day.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                                    @Override
                                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                                        therapy_day_select = year + "-" + (month + 1) + "-" + dayOfMonth; // Formato: "YYYY-MM-DD"
                                    }
                                });

                                //CONFERMA / NON CONFERMA TERAPIA

                                confirm_therapy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        HashSet<String> set = new HashSet<>();
                                        if(set.add(select_exercise1_spinner.getSelectedItem().toString())){
                                            set.add(select_exercise1_spinner.getSelectedItem().toString());
                                        }
                                        if(set.add(select_exercise2_spinner.getSelectedItem().toString())){
                                            set.add(select_exercise2_spinner.getSelectedItem().toString());
                                        }
                                        if(set.add(select_exercise3_spinner.getSelectedItem().toString())){
                                            set.add(select_exercise3_spinner.getSelectedItem().toString());
                                        }
                                        if(set.add(select_exercise4_spinner.getSelectedItem().toString())){
                                            set.add(select_exercise4_spinner.getSelectedItem().toString());
                                        }
                                        if(set.add(select_exercise5_spinner.getSelectedItem().toString())){
                                            set.add(select_exercise5_spinner.getSelectedItem().toString());
                                        }


                                        // Ottieni la data odierna
                                        Calendar cal = Calendar.getInstance();
                                        cal.set(Calendar.HOUR_OF_DAY, 0);      // Imposta le ore a 0
                                        cal.set(Calendar.MINUTE, 0);            // Imposta i minuti a 0
                                        cal.set(Calendar.SECOND, 0);            // Imposta i secondi a 0
                                        cal.set(Calendar.MILLISECOND, 0);
                                        Date dataOdierna = cal.getTime();

                                        // Converti la stringa in un oggetto Date
                                        Date therapy_date = null;
                                        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
                                        try {
                                            if(therapy_day_select != null){
                                                therapy_date = formatoData.parse(therapy_day_select);
                                                therapy_day_select = formatoData.format(therapy_date);
                                            }else{
                                                therapy_date = dataOdierna;
                                                therapy_day_select = formatoData.format(dataOdierna);
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if(set.size()!=5){
                                            Toast.makeText(v.getContext(), R.string.gli_esercizi_devono_essere_diversi_fra_loro, Toast.LENGTH_SHORT).show();
                                        }else if( therapy_day_select != null &&
                                                (therapy_date.after(dataOdierna) || therapy_date.equals(dataOdierna)) ){
                                            Query day_selected_false = database.getReference("Utenti")
                                                    .child("Logopedisti")
                                                    .child(sessionKey)
                                                    .child("Pazienti")
                                                    .child(list.get(pos).getCf())
                                                    .child("Terapie")
                                                    .child(therapy_day_select);
                                            day_selected_false.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        Toast.makeText(v.getContext(), R.string.giorno_non_disponibile, Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        //ASSEGNA ESERCIZIO 1
                                                        database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Pazienti")
                                                                .child(list.get(pos).getCf())
                                                                .child("Terapie")
                                                                .child(therapy_day_select)
                                                                .child("esercizio_1")
                                                                .child(select_exercise1_spinner.getSelectedItem().toString())
                                                                .child("id_esercizio")
                                                                .setValue(select_exercise1_spinner.getSelectedItem().toString())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });

                                                        Query conta_assegnazioni = database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Esercizi")
                                                                .child(select_exercise1_spinner.getSelectedItem().toString())
                                                                .child("conta_assegnazioni");

                                                        conta_assegnazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                count_assegnazioni = snapshot.getValue(Integer.class);
                                                                count_assegnazioni++;
                                                                snapshot.getRef().setValue(count_assegnazioni);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                        count_assegnazioni = 0;

                                                        //ASSEGNA ESERCIZIO 2
                                                        database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Pazienti")
                                                                .child(list.get(pos).getCf())
                                                                .child("Terapie")
                                                                .child(therapy_day_select)
                                                                .child("esercizio_2")
                                                                .child(select_exercise2_spinner.getSelectedItem().toString())
                                                                .child("id_esercizio")
                                                                .setValue(select_exercise2_spinner.getSelectedItem().toString())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });

                                                        conta_assegnazioni = database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Esercizi")
                                                                .child(select_exercise2_spinner.getSelectedItem().toString())
                                                                .child("conta_assegnazioni");

                                                        conta_assegnazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                count_assegnazioni = snapshot.getValue(Integer.class);
                                                                count_assegnazioni++;
                                                                snapshot.getRef().setValue(count_assegnazioni);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                        count_assegnazioni = 0;

                                                        //ASSEGNA ESERCIZIO 3
                                                        database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Pazienti")
                                                                .child(list.get(pos).getCf())
                                                                .child("Terapie")
                                                                .child(therapy_day_select)
                                                                .child("esercizio_3")
                                                                .child(select_exercise3_spinner.getSelectedItem().toString())
                                                                .child("id_esercizio")
                                                                .setValue(select_exercise3_spinner.getSelectedItem().toString())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });
                                                        conta_assegnazioni = database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Esercizi")
                                                                .child(select_exercise3_spinner.getSelectedItem().toString())
                                                                .child("conta_assegnazioni");

                                                        conta_assegnazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                count_assegnazioni = snapshot.getValue(Integer.class);
                                                                count_assegnazioni++;
                                                                snapshot.getRef().setValue(count_assegnazioni);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                        count_assegnazioni = 0;

                                                        //ASSEGNA ESERCIZIO 4
                                                        database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Pazienti")
                                                                .child(list.get(pos).getCf())
                                                                .child("Terapie")
                                                                .child(therapy_day_select)
                                                                .child("esercizio_4")
                                                                .child(select_exercise4_spinner.getSelectedItem().toString())
                                                                .child("id_esercizio")
                                                                .setValue(select_exercise4_spinner.getSelectedItem().toString())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });
                                                        conta_assegnazioni = database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Esercizi")
                                                                .child(select_exercise4_spinner.getSelectedItem().toString())
                                                                .child("conta_assegnazioni");

                                                        conta_assegnazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                count_assegnazioni = snapshot.getValue(Integer.class);
                                                                count_assegnazioni++;
                                                                snapshot.getRef().setValue(count_assegnazioni);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                        count_assegnazioni = 0;

                                                        //ASSEGNA ESERCIZIO 5
                                                        database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Pazienti")
                                                                .child(list.get(pos).getCf())
                                                                .child("Terapie")
                                                                .child(therapy_day_select)
                                                                .child("esercizio_5")
                                                                .child(select_exercise5_spinner.getSelectedItem().toString())
                                                                .child("id_esercizio")
                                                                .setValue(select_exercise5_spinner.getSelectedItem().toString())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });
                                                        conta_assegnazioni = database.getReference("Utenti")
                                                                .child("Logopedisti")
                                                                .child(sessionKey)
                                                                .child("Esercizi")
                                                                .child(select_exercise5_spinner.getSelectedItem().toString())
                                                                .child("conta_assegnazioni");

                                                        conta_assegnazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                count_assegnazioni = snapshot.getValue(Integer.class);
                                                                count_assegnazioni++;
                                                                snapshot.getRef().setValue(count_assegnazioni);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                        count_assegnazioni = 0;

                                                        Toast.makeText(v.getContext(), R.string.terapia_salvata, Toast.LENGTH_SHORT).show();
                                                        assegna_terapia_dialog.dismiss();
                                                        mDialog.dismiss();

                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                        fragmentTransaction.replace(container.getId() , ElencoPazienti.newInstance(sessionKey));
                                                        fragmentTransaction.commit();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }else{
                                            Toast.makeText(v.getContext(), R.string.giorno_non_consentito, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                                discard_therapy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        assegna_terapia_dialog.dismiss();
                                        mDialog.dismiss();
                                    }
                                });

                                assegna_terapia_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                assegna_terapia_dialog.show();
                            }
                        });

                        //VISUALIZZA TERAPIE
                        Dialog view_therapy_dialog = new Dialog(mDialog.getContext());
                        view_therapy_patient.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                view_therapy_dialog.setContentView(R.layout.view_therapy_patient);

                                RecyclerView  therapy_recycler_view = view_therapy_dialog.findViewById(R.id.therapy_recycler_view);
                                MyAdapter_Terapie myAdapter;
                                ArrayList<String> list_therapy;

                                therapy_recycler_view.setHasFixedSize(true);
                                therapy_recycler_view.setLayoutManager(new LinearLayoutManager(v.getContext()));

                                list_therapy = new ArrayList<>();
                                myAdapter = new MyAdapter_Terapie(view_therapy_dialog.getContext(),list_therapy,sessionKey,container
                                        , fragmentManager , view_therapy_dialog , list.get(pos).getCf(), list.get(pos).getCfGenitore());
                                therapy_recycler_view.setAdapter(myAdapter);

                                Query therapy_exist = database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(sessionKey)
                                        .child("Pazienti")
                                        .child(list.get(pos).getCf())
                                        .child("Terapie");
                                therapy_exist.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            String therapy_id = dataSnapshot.getRef().getKey();
                                            list_therapy.add(therapy_id);
                                        }
                                        myAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                view_therapy_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                view_therapy_dialog.show();
                            }
                        });

                        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        mDialog.show();

                    }
                }
            });

        }
    }

}