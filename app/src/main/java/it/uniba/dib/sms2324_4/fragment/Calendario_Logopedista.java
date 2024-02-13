package it.uniba.dib.sms2324_4.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

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


    private TextView oraTimePicker;

    private String stringDateSelected;
    private String oraSelected;


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

        calendarView = view.findViewById(R.id.calendarView);
        oraTimePicker = view.findViewById(R.id.editText);

        Button buttonpreno = view.findViewById(R.id.buttonpreno);

        oraTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Memorizza l'ora scelta
                                oraTimePicker.setText(String.format("%02d:%02d",hourOfDay, minute));
                                oraSelected = String.format("%02d:%02d",hourOfDay, minute);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                stringDateSelected = Integer.toString(i) + Integer.toString(i1+1) + Integer.toString(2);
            }
        });

        buttonpreno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}