package it.uniba.dib.sms2324_4;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;

import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio1;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio2;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio3;
import it.uniba.dib.sms2324_4.fragment.ElencoEsercizi;
import it.uniba.dib.sms2324_4.fragment.ElencoPazienti;

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


    public MyAdapter_Terapie(Context context, ArrayList<String> list, String sessionKey,
                             ViewGroup container, FragmentManager fragmentManager , Dialog backDialog , String cfPaziente) {
        this.context = context;
        this.list = list;
        this.sessionKey = sessionKey;
        this.container = container;
        this.fragmentManager = fragmentManager;
        this.backDialog = backDialog;
        this.cfPaziente = cfPaziente;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.therapy,parent,false);
        return  new MyViewHolder(v , list , sessionKey , fragmentManager , container , backDialog , cfPaziente);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String terapie = list.get(position);
        holder.tvTherapyName.setText(terapie);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTherapyName;

        int conta_assegnazioni = 0;
        private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

        public MyViewHolder(@NonNull View itemView , ArrayList<String> list ,String  sessionKey ,
                            FragmentManager fragmentManager ,ViewGroup container , Dialog backDialog , String cfPaziente) {
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
                                                    esercizio1.setAudio_soluzione(snapshot1.child("audio_soluzione").getValue().toString());
                                                    esercizio1.setCorretto((Boolean) snapshot1.child("corretto").getValue());
                                                    esercizio1.setEseguito((Boolean) snapshot1.child("eseguito").getValue());
                                                    esercizio1.setMonete(snapshot1.child("monete").getValue(Integer.class));
                                                    esercizio1.setEsperienza(snapshot1.child("esperienza").getValue(Integer.class));
                                                    esercizio1.setEsito((Boolean) snapshot1.child("esito").getValue());
                                                    esercizio1.setConta_assegnazioni(snapshot1.child("conta_assegnazioni").getValue(Integer.class));
                                                    esercizio1.setCorretto((Boolean) snapshot1.child("corretto").getValue());

                                                    list_es.add(esercizio1);
                                                }else if(snapshot1.child("id_esercizio").getValue().toString().startsWith("2_")){
                                                    Esercizio2 esercizio2 = new Esercizio2();

                                                    esercizio2.setId_esercizio(snapshot1.child("id_esercizio").getValue().toString());
                                                    esercizio2.setFrase_1(snapshot1.child("frase_1").getValue().toString());
                                                    esercizio2.setFrase_2(snapshot1.child("frase_2").getValue().toString());
                                                    esercizio2.setFrase_3(snapshot1.child("frase_3").getValue().toString());
                                                    esercizio2.setCorretto((Boolean) snapshot1.child("corretto").getValue());
                                                    esercizio2.setEseguito((Boolean) snapshot1.child("eseguito").getValue());
                                                    esercizio2.setMonete(snapshot1.child("monete").getValue(Integer.class));
                                                    esercizio2.setEsperienza(snapshot1.child("esperienza").getValue(Integer.class));
                                                    esercizio2.setEsito((Boolean) snapshot1.child("esito").getValue());
                                                    esercizio2.setConta_assegnazioni(snapshot1.child("conta_assegnazioni").getValue(Integer.class));
                                                    esercizio2.setCorretto((Boolean) snapshot1.child("corretto").getValue());

                                                    list_es.add(esercizio2);
                                                }else if(snapshot1.child("id_esercizio").getValue().toString().startsWith("3_")) {
                                                    Esercizio3 esercizio3 = new Esercizio3();

                                                    esercizio3.setId_esercizio(snapshot1.child("id_esercizio").getValue().toString());
                                                    esercizio3.setUriImage_sbagliata(snapshot1.child("uriImage_sbagliata").getValue().toString());
                                                    esercizio3.setUriImage_corretta(snapshot1.child("uriImage_corretta").getValue().toString());
                                                    esercizio3.setCorretto((Boolean) snapshot1.child("corretto").getValue());
                                                    esercizio3.setEseguito((Boolean) snapshot1.child("eseguito").getValue());
                                                    esercizio3.setMonete(snapshot1.child("monete").getValue(Integer.class));
                                                    esercizio3.setEsperienza(snapshot1.child("esperienza").getValue(Integer.class));
                                                    esercizio3.setEsito((Boolean) snapshot1.child("esito").getValue());
                                                    esercizio3.setConta_assegnazioni(snapshot1.child("conta_assegnazioni").getValue(Integer.class));
                                                    esercizio3.setCorretto((Boolean) snapshot1.child("corretto").getValue());

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
                                delete_item.setContentView(R.layout.confirm_delete);
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

                                        Toast.makeText(v.getContext(), "Terapia eliminata", Toast.LENGTH_SHORT).show();
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
                                        .child(list.get(pos))
                                        .orderByChild("eseguito")
                                        .equalTo(true);
                        correzione_terapia.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    verify_therapy.setVisibility(View.VISIBLE);
                                    delete_therapy.setVisibility(View.GONE);
                                }else{
                                    verify_therapy.setVisibility(View.GONE);
                                    delete_therapy.setVisibility(View.VISIBLE);
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