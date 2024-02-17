package it.uniba.dib.sms2324_4.logopedista.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio1;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio2;
import it.uniba.dib.sms2324_4.creazione.esercizi.Esercizio3;
import it.uniba.dib.sms2324_4.logopedista.menu.ElencoEsercizi;

public class MyAdapter_Esercizi extends RecyclerView.Adapter<MyAdapter_Esercizi.MyViewHolder> {

    Context context;

    String sessionKey;
    ViewGroup container;
    FragmentManager fragmentManager;

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public ViewGroup getContainer() {
        return container;
    }

    public void setContainer(ViewGroup container) {
        this.container = container;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    ArrayList<?> list = new ArrayList<>();


    public MyAdapter_Esercizi(Context context, ArrayList<?> list , String sessionKey, ViewGroup container,
                              FragmentManager fragmentManager) {
        this.context = context;
        this.list = list;
        this.sessionKey = sessionKey;
        this.container = container;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.esercizi,parent,false);
        if(list.getClass().equals(Esercizio1.class)){
            return  new MyViewHolder(v , (ArrayList<Esercizio1>) list, sessionKey , fragmentManager , container);
        }else if(list.getClass().equals(Esercizio2.class)){
            return  new MyViewHolder(v , (ArrayList<Esercizio2>) list , sessionKey , fragmentManager , container);
        }else{
            return  new MyViewHolder(v , (ArrayList<Esercizio3>) list , sessionKey , fragmentManager , container);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(list.get(position).getClass().equals(Esercizio1.class)){
            Esercizio1 exercise = (Esercizio1) list.get(position);
            holder.exercise_name.setText(exercise.getId_esercizio());
        }else if(list.get(position).getClass().equals(Esercizio2.class)){
            Esercizio2 exercise = (Esercizio2) list.get(position);
            holder.exercise_name.setText(exercise.getId_esercizio());
        }else if(list.get(position).getClass().equals(Esercizio3.class)){
            Esercizio3 exercise = (Esercizio3) list.get(position);
            holder.exercise_name.setText(exercise.getId_esercizio());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView exercise_name;
        private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

        public MyViewHolder(@NonNull View itemView , ArrayList<?> list , String sessionKey , FragmentManager fragmentManager , ViewGroup container) {
            super(itemView);

            exercise_name = itemView.findViewById(R.id.tvexerciseName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        //ESERCIZIO 1
                        if(list.get(pos).getClass().equals(Esercizio1.class)){
                            Dialog mDialog , delete_item;
                            TextView txtClose , id_esercizio , aiuto_1 , aiuto_2 , aiuto_3;
                            ImageView imageView;

                            Button delete_exercise;

                            Esercizio1 esercizio1 = (Esercizio1) list.get(pos);

                            mDialog = new Dialog(v.getContext());
                            delete_item = new Dialog(mDialog.getContext());

                            mDialog.setContentView(R.layout.exercise1_info_popup);
                            delete_exercise = mDialog.findViewById(R.id.delete_exercise_1);

                            Query conta_assegnazioni = database.getReference("Utenti")
                                    .child("Logopedisti")
                                    .child(sessionKey)
                                    .child("Esercizi")
                                    .child(esercizio1.getId_esercizio())
                                    .child("conta_assegnazioni");
                            conta_assegnazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue(Integer.class) != 0){
                                        delete_exercise.setVisibility(View.GONE);
                                    }else{
                                        delete_exercise.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //ELIMINA ESERCIZIO 1
                            delete_exercise.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    delete_item.setContentView(R.layout.confirm_delete_exercise);
                                    Button confirm , discard;

                                    confirm = delete_item.findViewById(R.id.confirm_delete);
                                    discard = delete_item.findViewById(R.id.discard_delete);

                                    confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            database.getReference("Utenti")
                                                    .child("Logopedisti")
                                                    .child(sessionKey)
                                                    .child("Esercizi")
                                                    .child(esercizio1.getId_esercizio())
                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(v.getContext(), R.string.esercizio_eliminato, Toast.LENGTH_SHORT).show();
                                                            delete_item.dismiss();
                                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                            fragmentTransaction.replace(container.getId() , ElencoEsercizi.newInstance(sessionKey));
                                                            fragmentTransaction.commit();
                                                        }
                                                    });

                                            FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                            StorageReference storageReference = storage.getReference(esercizio1.getUriImage().substring(1));
                                            storageReference.delete();
                                        }
                                    });

                                    discard.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            delete_item.dismiss();
                                            mDialog.dismiss();
                                        }
                                    });

                                    delete_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    delete_item.show();
                                }
                            });

                            txtClose = (TextView) mDialog.findViewById(R.id.exercise_text_close);
                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio1_popup);
                            aiuto_1 = (TextView) mDialog.findViewById(R.id.aiuto1_tv);
                            aiuto_2 = (TextView) mDialog.findViewById(R.id.aiuto2_tv);
                            aiuto_3 = (TextView) mDialog.findViewById(R.id.aiuto3_tv);
                            imageView = (ImageView) mDialog.findViewById(R.id.ex1_image_viewer);


                            id_esercizio.setText(esercizio1.getId_esercizio());
                            aiuto_1.setText(v.getResources().getText(R.string.aiuto_1_colon) + esercizio1.getAiuto_1());
                            aiuto_2.setText(v.getResources().getText(R.string.aiuto_2_colon) + esercizio1.getAiuto_2());
                            aiuto_3.setText(v.getResources().getText(R.string.aiuto_3_colon) + esercizio1.getAiuto_3());


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
                        //ESERCIZIO 2
                        }else if(list.get(pos).getClass().equals(Esercizio2.class)){
                            Dialog mDialog;
                            TextView txtClose , id_esercizio ,  parola_1  , parola_2 , parola_3 , cfPaziente;

                            Esercizio2 esercizio2 = (Esercizio2) list.get(pos);


                            mDialog = new Dialog(v.getContext());
                            mDialog.setContentView(R.layout.exercise2_info_popup);

                            Dialog delete_item = new Dialog(mDialog.getContext());

                            txtClose = (TextView) mDialog.findViewById(R.id.exercise2_text_close);
                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio2_popup);
                            parola_1 = (TextView) mDialog.findViewById(R.id.parola1_tv);
                            parola_2 = (TextView) mDialog.findViewById(R.id.parola2_tv);
                            parola_3 = (TextView) mDialog.findViewById(R.id.parola3_tv);

                            id_esercizio.setText(esercizio2.getId_esercizio());
                            parola_1.setText(v.getResources().getText(R.string.parola_1_colon) + esercizio2.getParola_1());
                            parola_2.setText(v.getResources().getText(R.string.parola_2_colon) + esercizio2.getParola_2());
                            parola_3.setText(v.getResources().getText(R.string.parola_3_colon) + esercizio2.getParola_3());

                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

                            Button delete_exercise = mDialog.findViewById(R.id.delete_exercise_2);

                            Query conta_assegnazioni = database.getReference("Utenti")
                                    .child("Logopedisti")
                                    .child(sessionKey)
                                    .child("Esercizi")
                                    .child(esercizio2.getId_esercizio())
                                    .child("conta_assegnazioni");
                            conta_assegnazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue(Integer.class) != 0){
                                        delete_exercise.setVisibility(View.GONE);
                                    }else{
                                        delete_exercise.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //ELIMINA ESERCIZIO 2
                            delete_exercise.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    delete_item.setContentView(R.layout.confirm_delete_exercise);
                                    Button confirm , discard;

                                    confirm = delete_item.findViewById(R.id.confirm_delete);
                                    discard = delete_item.findViewById(R.id.discard_delete);

                                    confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

                                            database.getReference("Utenti")
                                                    .child("Logopedisti")
                                                    .child(sessionKey)
                                                    .child("Esercizi")
                                                    .child(esercizio2.getId_esercizio())
                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(v.getContext(), R.string.esercizio_eliminato, Toast.LENGTH_SHORT).show();
                                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                            fragmentTransaction.replace(container.getId() , ElencoEsercizi.newInstance(sessionKey));
                                                            fragmentTransaction.commit();
                                                            delete_item.dismiss();
                                                        }
                                                    });
                                        }
                                    });

                                    discard.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            delete_item.dismiss();
                                            mDialog.dismiss();
                                        }
                                    });

                                    delete_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    delete_item.show();
                                }
                            });

                            txtClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                }
                            });

                            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            mDialog.show();
                        //ESERCIZIO 3
                        }else if(list.get(pos).getClass().equals(Esercizio3.class)){
                            Dialog mDialog;
                            TextView txtClose , id_esercizio , parola_immagine;
                            ImageView imageView_wrong , imageView_right ;

                            Esercizio3 esercizio3 = (Esercizio3) list.get(pos);

                            mDialog = new Dialog(v.getContext());
                            mDialog.setContentView(R.layout.exercise3_info_popup);

                            parola_immagine = mDialog.findViewById(R.id.ex3_parola_da_ascoltare);
                            parola_immagine.setText(v.getResources().getText(R.string.parola_da_ascoltare) + esercizio3.getParola_immagine());

                            Dialog delete_item = new Dialog(mDialog.getContext());

                            Button delete_exercise = mDialog.findViewById(R.id.delete_exercise_3);

                            Query conta_assegnazioni = database.getReference("Utenti")
                                    .child("Logopedisti")
                                    .child(sessionKey)
                                    .child("Esercizi")
                                    .child(esercizio3.getId_esercizio())
                                    .child("conta_assegnazioni");
                            conta_assegnazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue(Integer.class) != 0){
                                        delete_exercise.setVisibility(View.GONE);
                                    }else{
                                        delete_exercise.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //ELIMINA ESERCIZIO 3
                            delete_exercise.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    delete_item.setContentView(R.layout.confirm_delete_exercise);
                                    Button confirm , discard;

                                    confirm = delete_item.findViewById(R.id.confirm_delete);
                                    discard = delete_item.findViewById(R.id.discard_delete);

                                    confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

                                            database.getReference("Utenti")
                                                    .child("Logopedisti")
                                                    .child(sessionKey)
                                                    .child("Esercizi")
                                                    .child(esercizio3.getId_esercizio())
                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(v.getContext(), R.string.esercizio_eliminato, Toast.LENGTH_SHORT).show();
                                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                            fragmentTransaction.replace(container.getId() , ElencoEsercizi.newInstance(sessionKey));
                                                            fragmentTransaction.commit();
                                                            delete_item.dismiss();
                                                        }
                                                    });
                                        }
                                    });

                                    discard.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            delete_item.dismiss();
                                            mDialog.dismiss();
                                        }
                                    });

                                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                                    StorageReference storageReference = storage.getReference(esercizio3.getUriImage_sbagliata().substring(1));
                                    storageReference.delete();
                                    storageReference = storage.getReference(esercizio3.getUriImage_corretta().substring(1));
                                    storageReference.delete();

                                    delete_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    delete_item.show();
                                }
                            });

                            txtClose = (TextView) mDialog.findViewById(R.id.exercise3_text_close);
                            id_esercizio = (TextView) mDialog.findViewById(R.id.id_esercizio3_popup);
                            imageView_wrong = (ImageView) mDialog.findViewById(R.id.image_viewer_wrong_popup);
                            imageView_right = (ImageView) mDialog.findViewById(R.id.image_viewer_right_popup);


                            id_esercizio.setText(esercizio3.getId_esercizio());

                            FirebaseStorage storage = FirebaseStorage.getInstance("gs://pronuntiapp-register.appspot.com");
                            StorageReference storageReference = storage.getReference(esercizio3.getUriImage_sbagliata().substring(1));
                            Log.d("Image_Path", "Image Path: " + storageReference.toString());

                            try {
                                File file = File.createTempFile("tempfile" , ".jpg");

                                storageReference.getFile(file)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                                imageView_wrong.setImageBitmap(bitmap);
                                            }
                                        });

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            storageReference = storage.getReference(esercizio3.getUriImage_corretta().substring(1));
                            Log.d("Image_Path", "Image Path: " + storageReference.toString());

                            try {
                                File file = File.createTempFile("tempfile" , ".jpg");

                                storageReference.getFile(file)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                                imageView_right.setImageBitmap(bitmap);
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

                            Dialog dialog_assEx = new Dialog(mDialog.getContext());

                            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            mDialog.show();
                        }

                    }

                }
            });

        }
    }

}