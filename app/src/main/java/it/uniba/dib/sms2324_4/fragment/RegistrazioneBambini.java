package it.uniba.dib.sms2324_4.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import it.uniba.dib.sms2324_4.Figli;
import it.uniba.dib.sms2324_4.Paziente;
import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.Skin;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrazioneBambini#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrazioneBambini extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SESSION_KEY = "key";

    // TODO: Rename and change types of parameters
    String sessionKey;
    //Oggetti grafici
    EditText editTextChildCf , editTextChildNome , editTextChildCognome , editData, editTextParentCf;
    DatePickerDialog datePickerDialog;
    Button registerChild;

    public RegistrazioneBambini() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RegistrazioneBambini newInstance(String sessionKey) {
        RegistrazioneBambini fragment = new RegistrazioneBambini();
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

        View view = inflater.inflate(R.layout.fragment_registrazione_bambini, container, false);

        initDatePicker();

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(container.getId() , ElencoPazienti.newInstance(sessionKey))
                        .commit();
            }
        };

        editTextChildCf = view.findViewById(R.id.child_cf);
        editTextChildCognome = view.findViewById(R.id.child_surname);
        editTextChildNome = view.findViewById(R.id.child_name);
        editData = view.findViewById(R.id.child_birthdate);
        registerChild = view.findViewById(R.id.child_btn_register);
        editTextParentCf = view.findViewById(R.id.genitore_cf);

        //Definizione Metodo Ascoltatore del buttone Data
        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        registerChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editTextChildCf.getText()) || editTextChildCf.getText().toString().length() != 16){
                    Toast.makeText(requireContext(),
                            "Inserisci Codice Fiscale",
                            Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(editTextChildCognome.getText())){
                    Toast.makeText(requireContext(),
                            "Inserisci Cognome",
                            Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(editTextChildNome.getText())){
                    Toast.makeText(requireContext(),
                            "Inserisci Nome",
                            Toast.LENGTH_SHORT).show();
                }else if(editData.getText().toString().compareTo("Data di nascita") == 0){
                    Toast.makeText(requireContext(),
                            "Inserisci Data di nascita",
                            Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(editTextParentCf.getText())){
                    Toast.makeText(requireContext(),
                            "Inserisci Codice Fiscale Genitore",
                            Toast.LENGTH_SHORT).show();
                }else{
                    //Controllo se il genitore è registrato nel sistema
                    FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
                    Query parentRegistered = database.getReference("Utenti")
                            .child("Genitori")
                            .child(editTextParentCf.getText().toString());
                    parentRegistered.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                //SELECT * FROM Genitori  WHERE cf = ?
                                Query childExists = database.getReference("Utenti")
                                        .child("Genitori")
                                        .child(editTextParentCf.getText().toString())
                                        .child("Bambini")
                                        .child(editTextChildCf.getText().toString());
                                childExists.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            Toast.makeText(v.getContext(),
                                                    "Bambino già registrato",
                                                    Toast.LENGTH_SHORT).show();
                                        }else{
                                            String nome, cognome , cf , dataDiNascita;
                                            Skin skin = new Skin();

                                            nome = editTextChildNome.getText().toString();
                                            cognome = editTextChildCognome.getText().toString();
                                            cf = editTextChildCf.getText().toString();
                                            dataDiNascita = editData.getText().toString();

                                            //skin.setCosto();
                                            //skin.setIdSkin();

                                            Figli paziente = new Figli(nome , cognome , cf , dataDiNascita , sessionKey , 0 , 0);
                                            //Registrazione bambino
                                            database.getReference("Utenti")
                                                    .child("Genitori")
                                                    .child(editTextParentCf.getText().toString())
                                                    .child("Bambini")
                                                    .child(cf)
                                                    .setValue(paziente);

                                            /*
                                            database.getReference("Utenti")
                                                    .child("Genitori")
                                                    .child(editTextParentCf.getText().toString())
                                                    .child("Bambini")
                                                    .child(cf)
                                                    .child("Skin")
                                                    .child(skin.getIdSkin())
                                                    .setValue(skin);
                                             */

                                            //Registrazione paziente
                                            Paziente paziente1 = new Paziente(nome , cognome , cf , dataDiNascita , editTextParentCf.getText().toString());
                                            database.getReference("Utenti")
                                                    .child("Logopedisti")
                                                    .child(sessionKey)
                                                    .child("Pazienti")
                                                    .child(cf)
                                                    .setValue(paziente1);

                                            database.getReference("Utenti")
                                                    .child("Logopedisti")
                                                    .child(sessionKey)
                                                    .child("Pazienti")
                                                    .child(cf)
                                                    .child("esperienza")
                                                    .setValue(0);

                                            Toast.makeText(v.getContext(),
                                                    R.string.paziente_registrato_con_successo,
                                                    Toast.LENGTH_SHORT).show();

                                            FragmentManager fragmentManager = getParentFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(container.getId(),ElencoPazienti.newInstance(sessionKey));
                                            fragmentTransaction.commit();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else{
                                Toast.makeText(v.getContext(),
                                        "Genitore non registrato",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        // Inflate the layout for this fragment

        return view;

    }


    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                editData.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = R.style.MyDatePickerDialogTheme;

        datePickerDialog = new DatePickerDialog(requireContext(), style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
}