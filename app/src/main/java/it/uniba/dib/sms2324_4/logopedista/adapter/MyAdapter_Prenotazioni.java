package it.uniba.dib.sms2324_4.logopedista.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.dib.sms2324_4.R;

public class MyAdapter_Prenotazioni extends RecyclerView.Adapter<MyAdapter_Prenotazioni.MyViewHolder> {

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
    Dialog backDialog;
    FragmentManager fragmentManager;


    public MyAdapter_Prenotazioni(Context context, ArrayList<String> list, String sessionKey,
                                  FragmentManager fragmentManager , ViewGroup container, Dialog backDialog) {
        this.context = context;
        this.list = list;
        this.sessionKey = sessionKey;
        this.fragmentManager = fragmentManager;
        this.container = container;
        this.backDialog = backDialog;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reservation,parent,false);
        return  new MyViewHolder(v , list , sessionKey , fragmentManager , container, backDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String prenotazione = list.get(position);
        holder.tvchildReservation.setText(prenotazione);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvchildReservation;

        private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

        public MyViewHolder(@NonNull View itemView , ArrayList<String> list ,String  sessionKey,
                            FragmentManager fragmentManager , ViewGroup container , Dialog backDialog) {
            super(itemView);

            tvchildReservation = itemView.findViewById(R.id.tvchildReservation);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        backDialog.dismiss();

                        RecyclerView recyclerView;
                        MyAdapter_Orari myAdapter;
                        ArrayList<String> list_orari;

                        Dialog dialog = new Dialog(v.getContext());
                        dialog.setContentView(R.layout.view_reservation_time);

                        recyclerView = dialog.findViewById(R.id.reservation_time_recycler_view);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

                        list_orari = new ArrayList<>();
                        myAdapter = new MyAdapter_Orari(dialog.getContext(),list.get(pos),list_orari,sessionKey,fragmentManager,container,dialog);
                        recyclerView.setAdapter(myAdapter);

                        Query reservationExistant = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(sessionKey)
                                .child("Prenotazioni")
                                .child(list.get(pos));
                        reservationExistant.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String id_esercizio = null;
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    list_orari.add(dataSnapshot.getKey());
                                }
                                myAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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