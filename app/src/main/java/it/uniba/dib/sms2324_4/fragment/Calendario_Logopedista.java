package it.uniba.dib.sms2324_4.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.uniba.dib.sms2324_4.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Calendario_Logopedista#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendario_Logopedista extends Fragment {

    private static final String SESSION_KEY = "SESSION_KEY";
    private String sessionKey;

    private CalendarView calendarView ;
    private Calendar calendar = Calendar.getInstance();


    private TextView timePicker_ora_inizio;
    private TextView timePicker_ora_fine;

    private String stringDateSelected;


    public Calendario_Logopedista() {
        // Required empty public constructor
    }

    public static Calendario_Logopedista newInstance(String sessionKey) {
        Calendario_Logopedista fragment = new Calendario_Logopedista();
        Bundle args = new Bundle();
        args.putString(SESSION_KEY, sessionKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sessionKey = getArguments().getString(SESSION_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_calendario_logopedista, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance(view.getContext().getString(R.string.db_url));
        calendarView = view.findViewById(R.id.calendarView);

        timePicker_ora_inizio = view.findViewById(R.id.timePicker_ora_inizio);
        timePicker_ora_fine = view.findViewById(R.id.timePicker_ora_fine);

        Button buttonpreno = view.findViewById(R.id.buttonpreno);

        Spinner elencoPazienti_spinner = view.findViewById(R.id.elencoPazienti_spinner);

        ArrayAdapter<String> elencoPazienti = new ArrayAdapter<>(view.getContext() , android.R.layout.simple_list_item_1);

        Query fetch_patients = database.getReference("Utenti")
                .child("Logopedisti")
                .child(sessionKey)
                .child("Pazienti");
        fetch_patients.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String cf = dataSnapshot.child("cf").getValue(String.class);
                        String nomeCognome = dataSnapshot.child("nome").getValue(String.class)
                                 + " " +
                                dataSnapshot.child("cognome").getValue(String.class);
                        elencoPazienti.add(cf + " ("+ nomeCognome + ")");
                    }

                    elencoPazienti_spinner.setAdapter(elencoPazienti);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        timePicker_ora_inizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Memorizza l'ora scelta
                                timePicker_ora_inizio.setText(String.format("%02d:%02d",hourOfDay, minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        timePicker_ora_fine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Memorizza l'ora scelta
                                timePicker_ora_fine.setText(String.format("%02d:%02d",hourOfDay, minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        stringDateSelected = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                stringDateSelected = Integer.toString(i) + "-" +
                        Integer.toString(i1+1) + "-"  + Integer.toString(i2);
            }
        });

        buttonpreno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CONTROLLO SULL'INPUT DELL'ORA
                DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    Date oraInizio = dateFormat.parse(timePicker_ora_inizio.getText().toString());
                    Date oraFine = dateFormat.parse(timePicker_ora_fine.getText().toString());

                    if(oraFine.before(oraInizio)){
                        Toast.makeText(view.getContext(),
                                R.string.orario_non_valido,
                                Toast.LENGTH_SHORT).show();
                    }else{
                        //CONTROLLO SE ESISTE UNA PRENOTAZIONE A QUELL'ORA
                        Query prenotazioneEsistente = database.getReference("Utenti")
                                .child("Logopedisti")
                                .child(sessionKey)
                                .child("Prenotazioni")
                                .child(stringDateSelected)
                                .child(timePicker_ora_inizio.getText().toString());
                        prenotazioneEsistente.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Toast.makeText(view.getContext(),
                                           R.string.hai_gi_una_prenotazione_per_quest_ora,
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    //CONTROLLO SE ESISTE UNA PRENOTAZIONE PER UN DETERMINATO PAZIENTE
                                    Query prenotazionePazienteEsistente = database.getReference("Utenti")
                                            .child("Logopedisti")
                                            .child(sessionKey)
                                            .child("Prenotazioni")
                                            .child(stringDateSelected)
                                            .orderByChild("cfPaziente")
                                            .equalTo(elencoPazienti_spinner.getSelectedItem().toString().substring(0,16));
                                    prenotazionePazienteEsistente.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                Toast.makeText(view.getContext(),
                                                        R.string.hai_gi_una_prenotazione_per_questo_paziente,
                                                        Toast.LENGTH_SHORT).show();
                                            }else{
                                                //CONTROLLO SE LA PRENOTAZIONE SI SOVRAPPONE AD UN ALTRA
                                                Query sovrapPren = database.getReference("Utenti")
                                                        .child("Logopedisti")
                                                        .child(sessionKey)
                                                        .child("Prenotazioni")
                                                        .child(stringDateSelected);
                                                sovrapPren.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        boolean flag = false;
                                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                            LocalTime time = LocalTime.parse(timePicker_ora_inizio.getText().toString());
                                                            LocalTime time2 = LocalTime.parse(dataSnapshot.child("ora_fine").getValue(String.class));

                                                            if(time.isBefore(time2)){
                                                                flag = true;
                                                            }
                                                        }
                                                        if(flag == true){
                                                            Toast.makeText(view.getContext(),
                                                                    R.string.sovrapposizione_prenotazioni,
                                                                    Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            database.getReference("Utenti")
                                                                    .child("Logopedisti")
                                                                    .child(sessionKey)
                                                                    .child("Prenotazioni")
                                                                    .child(stringDateSelected)
                                                                    .child(timePicker_ora_inizio.getText().toString())
                                                                    .child("cfPaziente")
                                                                    .setValue(elencoPazienti_spinner.getSelectedItem().toString().substring(0,16))
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {

                                                                        }
                                                                    });

                                                            database.getReference("Utenti")
                                                                    .child("Logopedisti")
                                                                    .child(sessionKey)
                                                                    .child("Prenotazioni")
                                                                    .child(stringDateSelected)
                                                                    .child(timePicker_ora_inizio.getText().toString())
                                                                    .child("ora_fine")
                                                                    .setValue(timePicker_ora_fine.getText().toString())
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {

                                                                        }
                                                                    });

                                                            Toast.makeText(view.getContext(),
                                                                    R.string.prenotazione_effettuata_con_successo,
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Button view_reservation = view.findViewById(R.id.view_reservation);
                        view_reservation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {



                            }
                        });
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return view;
    }
}