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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.dib.sms2324_4.fragment.Calendario_Logopedista;
import it.uniba.dib.sms2324_4.fragment.ElencoPazienti;

public class MyAdapter_Orari extends RecyclerView.Adapter<MyAdapter_Orari.MyViewHolder> {

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
    String date;


    public MyAdapter_Orari(Context context,String date,ArrayList<String> list, String sessionKey,
                           FragmentManager fragmentManager,ViewGroup container,Dialog backDialog) {
        this.context = context;
        this.list = list;
        this.sessionKey = sessionKey;
        this.date = date;
        this.fragmentManager = fragmentManager;
        this.container = container;
        this.backDialog = backDialog;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.time,parent,false);
        return  new MyViewHolder(v , date , list , sessionKey , fragmentManager , container , backDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String ora = list.get(position);
        holder.tvchildOrario.setText(ora);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvchildOrario;

        private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

        public MyViewHolder(@NonNull View itemView ,String date , ArrayList<String> list ,String  sessionKey ,
                            FragmentManager fragmentManager , ViewGroup container ,Dialog backDialog) {
            super(itemView);

            tvchildOrario = itemView.findViewById(R.id.tvchildOrario);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        backDialog.dismiss();
                        Dialog reservationDialog = new Dialog(v.getContext());
                        reservationDialog.setContentView(R.layout.reservation_info_popup);

                        Query reservationExistant = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(sessionKey)
                                .child("Prenotazioni")
                                .child(date)
                                .child(list.get(pos));
                        reservationExistant.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                TextView cfPaziente_prenotazione = reservationDialog.findViewById(R.id.cfPaziente_prenotazione);
                                TextView giorno_prenotazione = reservationDialog.findViewById(R.id.giorno_prenotazione);
                                TextView ora_fine_prenotazione = reservationDialog.findViewById(R.id.ora_fine_prenotazione);

                                cfPaziente_prenotazione.setText(snapshot.child("cfPaziente").getValue(String.class));
                                giorno_prenotazione.setText(date);
                                ora_fine_prenotazione.setText(snapshot.child("ora_fine").getValue(String.class));

                                //ELIMINA PRENOTAZIONE
                                Button delete_reservation = reservationDialog.findViewById(R.id.delete_reservation);
                                delete_reservation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Dialog delete_item = new Dialog(reservationDialog.getContext());

                                        reservationDialog.dismiss();
                                        delete_item.setContentView(R.layout.confirm_delete_reservation);
                                        Button confirm , discard;

                                        confirm = delete_item.findViewById(R.id.confirm_delete);
                                        discard = delete_item.findViewById(R.id.discard_delete);

                                        confirm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                database.getReference("Utenti")
                                                        .child("Logopedisti")
                                                        .child(sessionKey)
                                                        .child("Prenotazioni")
                                                        .child(date)
                                                        .child(list.get(pos))
                                                        .removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        });

                                                Toast.makeText(v.getContext(), R.string.prenotazione_eliminata, Toast.LENGTH_SHORT).show();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                fragmentTransaction.replace(container.getId() , Calendario_Logopedista.newInstance(sessionKey));
                                                fragmentTransaction.commit();
                                                reservationDialog.dismiss();
                                                delete_item.dismiss();
                                            }
                                        });

                                        discard.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                delete_item.dismiss();
                                                reservationDialog.dismiss();
                                            }
                                        });

                                        delete_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        delete_item.show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        reservationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        reservationDialog.show();
                    }
                }
            });

        }
    }

}