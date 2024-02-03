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

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail , editTextPassword , editTextConfirmPassword ,editTextName, editTextSurname, editTextCF;
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
        setContentView(R.layout.activity_register);

        //Istanza della variabile FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.name);
        editTextSurname = findViewById(R.id.surname);
        editTextCF = findViewById(R.id.cf);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmPassword);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        loginNow = findViewById(R.id.loginNow);


        //Definizione Metodo Ascoltatore della TextView LoginNow
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext() , Login.class);
                startActivity(intent);
                finish();
            }
        });

        //Definizione Metodo Ascoltatore del bottone Registrati
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String nome,cognome,cf,email, password, confirmPassword , dataDiNascita;

                nome = String.valueOf(editTextName.getText());
                cognome = String.valueOf(editTextSurname.getText());
                cf = String.valueOf(editTextCF.getText()).toUpperCase();
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());

                //Controllo sull'input - Se Email e Password NON sono VUOTI
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this ,
                            "Inserisci Email" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this ,
                            "Inserisci Password" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(TextUtils.isEmpty(nome)){
                    Toast.makeText(Register.this ,
                            "Inserisci Nome" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(TextUtils.isEmpty(cognome)){
                    Toast.makeText(Register.this ,
                            "Inserisci Cognome" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(TextUtils.isEmpty(cf) || cf.length() != 16){
                    Toast.makeText(Register.this ,
                            "Inserisci Codice Fiscale" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(!TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(Register.this ,
                            "Conferma Password" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if(password.compareTo(confirmPassword)!=0) {
                    Toast.makeText(Register.this ,
                            "Le password non coincidono" ,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else {
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

                    Genitori genitore = new Genitori(nome, cognome , cf , email , passwordCrypted );

                        Query fetchData = FirebaseDatabase.getInstance(getString(R.string.db_url)).getReference("Utenti").child("Genitori")
                                .orderByChild("cf")
                                .equalTo(cf);
                        fetchData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Toast.makeText(Register.this, "Account Già registrato",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Query fetchData2 = FirebaseDatabase.getInstance(getString(R.string.db_url)).getReference("Utenti").child("Genitori")
                                            .orderByChild("email")
                                            .equalTo(email);
                                    fetchData2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange (@NonNull DataSnapshot snapshot){
                                            if (snapshot.exists()) {
                                                Toast.makeText(Register.this, "Account Già registrato",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                reference.child("Utenti").child("Genitori").child(cf).setValue(genitore);
                                                SessionManagement sessionManagement = new SessionManagement(Register.this);
                                                sessionManagement.saveSession(genitore,"genitore",nome);
                                                Intent intent = new Intent(getApplicationContext(), User.class);
                                                startActivity(intent);
                                                finish();

                                                Toast.makeText(Register.this,
                                                        "Registrazione Effettuata con SUCCESSO",
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
        });
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
