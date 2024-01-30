package it.uniba.dib.sms2324_4;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.dib.sms2324_4.gioco.ui.Gioco_Home_Page;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;

    ArrayList<Figli> list;

    ViewGroup container;
    FragmentManager fragmentManager;

    private String sessionKey;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<Figli> getList() {
        return list;
    }

    public void setList(ArrayList<Figli> list) {
        this.list = list;
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

    public MyAdapter(Context context, ArrayList<Figli> list, ViewGroup container, FragmentManager fragmentManager,String sessionKey) {
        this.context = context;
        this.list = list;
        this.container = container;
        this.fragmentManager = fragmentManager;
        this.sessionKey = sessionKey;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return  new MyViewHolder(v , list , container , fragmentManager,sessionKey);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Figli figli = list.get(position);
        holder.firstName.setText(figli.getNome());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView firstName, lastName, dataDiNascita , cfDottore;

        public MyViewHolder(@NonNull View itemView , ArrayList<Figli> list , ViewGroup container ,
                            FragmentManager fragmentManager,String sessionKey) {
            super(itemView);

            firstName = itemView.findViewById(R.id.tvchildName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Dialog mDialog;
                        TextView txtClose , child_name_tv , child_birthdate_tv , child_therapist;

                        mDialog = new Dialog(v.getContext());
                        mDialog.setContentView(R.layout.child_info_popup);
                        txtClose = (TextView) mDialog.findViewById(R.id.txtClose);
                        child_name_tv = (TextView) mDialog.findViewById(R.id.child_name_tv);
                        child_birthdate_tv = (TextView) mDialog.findViewById(R.id.child_birthdate_tv);
                        child_therapist = (TextView) mDialog.findViewById(R.id.child_therapist);

                        String nomeCognome = list.get(pos).getNome() + " "  + list.get(pos).getCognome();
                        String dataDiNascita = list.get(pos).getDataDiNascita();
                        String cfLogopedista = list.get(pos).getCfLogopedista();

                        child_name_tv.setText(nomeCognome);
                        child_birthdate_tv.setText(dataDiNascita);

                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");
                        Query info_logo = database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(cfLogopedista);
                        info_logo.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String info_logo_string = "Dott. "
                                            + snapshot.child("cognome").getValue().toString()
                                            + " " + snapshot.child("nome").getValue().toString();
                                    child_therapist.setText(info_logo_string);
                                }else{
                                    child_therapist.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        txtClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });

                        //CONTROLLO PRESENZA TERAPIE
                        Button view_therapy_child = mDialog.findViewById(R.id.view_therapy_child);
                        Button game_child = mDialog.findViewById(R.id.game_child);

                        //SWITCH VISUALIZZA TERAPIA
                        Query therapy_exists = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(list.get(pos).cfLogopedista)
                                .child("Pazienti")
                                .child(list.get(pos).getCf())
                                .child("Terapie");
                        therapy_exists.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    view_therapy_child.setVisibility(View.VISIBLE);
                                    game_child.setVisibility(View.VISIBLE);

                                }else{
                                    view_therapy_child.setVisibility(View.GONE);
                                    game_child.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //APRI GIOCO
                        game_child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(container.getId() , Gioco_Home_Page.newInstance(list.get(pos).getCf(),sessionKey,list.get(pos).getCfLogopedista()));
                                fragmentTransaction.commit();
                            }
                        });


                        //VISUALIZZA TERAPIE
                        Dialog view_therapy_dialog = new Dialog(mDialog.getContext());
                        view_therapy_child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                view_therapy_dialog.setContentView(R.layout.view_therapy_patient);

                                RecyclerView  therapy_recycler_view = view_therapy_dialog.findViewById(R.id.therapy_recycler_view);
                                MyAdapter_Terapie_Bambino myAdapter;
                                ArrayList<String> list_therapy;

                                therapy_recycler_view.setHasFixedSize(true);
                                therapy_recycler_view.setLayoutManager(new LinearLayoutManager(v.getContext()));

                                list_therapy = new ArrayList<>();
                                myAdapter = new MyAdapter_Terapie_Bambino(view_therapy_dialog.getContext(),list_therapy,list.get(pos).cfLogopedista,container
                                        , fragmentManager , view_therapy_dialog , list.get(pos).getCf());
                                therapy_recycler_view.setAdapter(myAdapter);

                                Query therapy_exist = database.getReference("Utenti")
                                        .child("Logopedisti")
                                        .child(list.get(pos).cfLogopedista)
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