package it.uniba.dib.sms2324_4.accesso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import it.uniba.dib.sms2324_4.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Opzionale: Puoi impostare una transizione di ingresso qui se lo desideri
        setContentView(R.layout.activity_splash);

        // Opzionale: Avvia un thread per simulare il caricamento dell'applicazione
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Passa all'attività principale o successiva
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);

                // Imposta l'animazione personalizzata per la transizione tra attività
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                finish();
            }
        }, 1000); // Tempo di attesa in millisecondi prima di passare all'attività successiva
    }
}
