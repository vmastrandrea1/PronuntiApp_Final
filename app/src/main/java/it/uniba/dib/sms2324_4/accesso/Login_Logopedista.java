package it.uniba.dib.sms2324_4.accesso;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import it.uniba.dib.sms2324_4.logopedista.Logopedisti;
import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.logopedista.User_Logopedista;

public class Login_Logopedista extends AppCompatActivity {

    TextInputEditText editTextEmail , editTextPassword;
    Button buttonLog;
    Button directAccess;
    TextView switchToLogin;

    //Variabili Firebase
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView registerNow;

    @Override
    protected void onResume() {
        super.onResume();

        directAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = "Default";
                String cognome = "Logopedista";
                String cf = "DLDLDLDLDLDLDLDL";
                Toast.makeText(Login_Logopedista.this,
                        getString(R.string.benvenuto) + nome,
                        Toast.LENGTH_SHORT).show();
                //Salvataggio Sessione

                Logopedisti user = new Logopedisti(nome, cognome, cf, "defaultlogopedista@gmail.com",
                        "1d6442ddcfd9db1ff81df77cbefcd5afcc8c7ca952ab3101ede17a84b866d3f3");
                SessionManagement sessionManagement = new SessionManagement(Login_Logopedista.this);
                sessionManagement.saveSession(user, "logopedista", nome);

                Intent intent = new Intent(getApplicationContext(), User_Logopedista.class);
                startActivity(intent);
                finish();

            }
        });

        //googleLogin_btn = findViewById(R.id.btn_googleLogin);

        /*
        //Definizione Metodo Ascoltatore del Bottone Google Login
        googleLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(Login.this, options);
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

         */

        //Definizione Metodo Ascoltatore della TextView LoginNow
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register_Logopedista.class);
                startActivity(intent);
                finish();
            }
        });

        //Switch to Login
        //Switch su Logopedista
        switchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        //Definizione Metodo Ascoltatore del bottone Login
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                //Controllo sull'input - Se Email e Password non sono VUOTI
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login_Logopedista.this,
                            R.string.inserisci_l_email,
                            Toast.LENGTH_SHORT).show();
                    ;
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login_Logopedista.this,
                            R.string.inserisci_la_password,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    //Tentativo di Login
                    //Autenticazione Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.db_url));
                    DatabaseReference reference = database.getReference();

                    String passwordCrypted;

                    try {
                        passwordCrypted = hashPasswordSHA3(password);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    Query login_email = database.getReference("Utenti").child("Logopedisti");

                    login_email.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean logged = false;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.child("email").getValue().toString().compareToIgnoreCase(email) == 0
                                        && dataSnapshot.child("password").getValue().toString().compareTo(passwordCrypted) == 0) {
                                    Toast.makeText(Login_Logopedista.this,
                                            getString(R.string.benvenuto) + dataSnapshot.child("nome").getValue().toString(),
                                            Toast.LENGTH_SHORT).show();
                                    //Salvataggio Sessione
                                    String nome = dataSnapshot.child("nome").getValue().toString();
                                    String cognome = dataSnapshot.child("cognome").getValue().toString();
                                    String cf = dataSnapshot.child("cf").getValue().toString();

                                    Logopedisti user = new Logopedisti(nome, cognome, cf, email, passwordCrypted);
                                    SessionManagement sessionManagement = new SessionManagement(Login_Logopedista.this);
                                    sessionManagement.saveSession(user, "logopedista", nome);

                                    Intent intent = new Intent(getApplicationContext(), User_Logopedista.class);
                                    startActivity(intent);
                                    finish();

                                    logged = true;
                                }
                            }
                            if (!logged) {
                                Toast.makeText(Login_Logopedista.this,
                                        R.string.accesso_negato,
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
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


    /*

        private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try{
                                GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                                mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                      if(task.isSuccessful()){

                                          mAuth = FirebaseAuth.getInstance();
                                          Glide.with(Login.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(imageView);


                                          Toast.makeText(Login.this,
                                                  "Login Effettuato con SUCCESSO",
                                                  Toast.LENGTH_SHORT).show();
                                          Intent intent = new Intent(getApplicationContext() , User.class);
                                          startActivity(intent);
                                          finish();
                                      }else{
                                          Toast.makeText(Login.this,
                                                  "Login NEGATO",
                                                  Toast.LENGTH_SHORT).show();
                                      }
                                    }
                                });
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

         */
    @Override
    public void onStart() {
        super.onStart();

        //Recupero Sessione
        SessionManagement sessionManagement = new SessionManagement(Login_Logopedista.this);
        String userID = sessionManagement.getSession();

        if(userID.compareTo("NULL")!=0) {
            Toast.makeText(this, getString(R.string.bentornato) + " " + sessionManagement.getNome(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), User_Logopedista.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_logopedista);

        //Istanza della variabile FirebaseAuth
        //mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email_logopedista);
        editTextPassword = findViewById(R.id.password_logopedista);
        buttonLog = findViewById(R.id.btn_login_logopedista);
        progressBar = findViewById(R.id.progressBar_logopedista);
        registerNow = findViewById(R.id.registerNow_logopedista);
        switchToLogin = findViewById(R.id.logopedista_to_genitore);

        directAccess = findViewById(R.id.direct_access_logo);


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