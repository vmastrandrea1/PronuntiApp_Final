package it.uniba.dib.sms2324_4;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.util.Calendar;

public class Register_Logopedista extends AppCompatActivity {

    TextInputEditText editTextEmail , editTextPassword, editTextConfirmPassword ,editTextName, editTextSurname, editTextCF;
    DatePickerDialog datePickerDialog;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView  loginNow;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent =  new Intent(getApplicationContext() , User.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_logopedista);


        //initDatePicker();
        //Istanza della variabile FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.name_logopedista);
        editTextSurname = findViewById(R.id.surname_logopedista);
        editTextCF = findViewById(R.id.cf_logopedista);

        editTextEmail = findViewById(R.id.email_logopedista);
        editTextPassword = findViewById(R.id.password_logopedista);
        editTextConfirmPassword = findViewById(R.id.conferma_password_logopedista);
        buttonReg = findViewById(R.id.btn_register_logopedista);
        progressBar = findViewById(R.id.progressBar_logopedista);
        loginNow = findViewById(R.id.loginNow_logopedista);


        //Definizione Metodo Ascoltatore della TextView LoginNow
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext() , Login_Logopedista.class);
                startActivity(intent);
                finish();
            }
        });

        //Definizione Metodo Ascoltatore del bottone Registrati
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String nome,cognome,cf,email, password , dataDiNascita;

                nome = String.valueOf(editTextName.getText());
                cognome = String.valueOf(editTextSurname.getText());
                cf = String.valueOf(editTextCF.getText());
                cf = cf.toUpperCase();
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                //Controllo sull'input - Se Email e Password NON sono VUOTI
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register_Logopedista.this ,
                            "Inserisci Email" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register_Logopedista.this ,
                            "Inserisci Password" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(TextUtils.isEmpty(nome)){
                    Toast.makeText(Register_Logopedista.this ,
                            "Inserisci Nome" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(TextUtils.isEmpty(cognome)){
                    Toast.makeText(Register_Logopedista.this ,
                            "Inserisci Cognome" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(TextUtils.isEmpty(cf) || cf.length() != 16){
                    Toast.makeText(Register_Logopedista.this ,
                            "Inserisci Codice Fiscale" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(!editTextConfirmPassword.getText().toString().equals(editTextPassword.getText().toString())){
                    Toast.makeText(Register_Logopedista.this ,
                            "Le Password non coincidono" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else{
                    String passwordCrypted ;
                    //Registrazione tramite RealtimeDatabase
                    progressBar.setVisibility(View.GONE);
                    FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
                    DatabaseReference reference = database.getReference();

                    try {
                        passwordCrypted = hashPasswordSHA3(password);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }



                    Logopedisti logopedista = new Logopedisti(nome, cognome , cf ,  email , passwordCrypted);

                    Query fetchData = FirebaseDatabase.getInstance(getString(R.string.db_url)).getReference("Utenti").child("Logopedisti")
                            .orderByChild("email")
                            .equalTo(email);
                    String finalCf = cf;
                    fetchData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(Register_Logopedista.this, "Account Già registrato",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }else{
                                progressBar.setVisibility(View.GONE);
                                reference.child("Utenti").child("Logopedisti").child(finalCf).setValue(logopedista);
                                SessionManagement sessionManagement = new SessionManagement(Register_Logopedista.this);
                                sessionManagement.saveSession(logopedista,"lgoopedista",nome);
                                Intent intent = new Intent(getApplicationContext() , User_Logopedista.class);
                                startActivity(intent);
                                finish();

                                Toast.makeText(Register_Logopedista.this,
                                        R.string.registrazione_effettuata_con_successo,
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
                //editData.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    private static String hashPasswordSHA3(String password) throws Exception {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] hashBytes = digestSHA3.digest(password.getBytes());

        // Convert the byte array to a hexadecimal string
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte hashByte : hashBytes) {
            hexStringBuilder.append(String.format("%02x", hashByte));
        }

        return hexStringBuilder.toString();
    }
}
