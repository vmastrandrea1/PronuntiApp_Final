package it.uniba.dib.sms2324_4.creazione.esercizi;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.logopedista.menu.ElencoEsercizi;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreaEsercizi#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreaEsercizi extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SESSION_KEY = "key";

    private String userID;
    private CardView bt1;
    private CardView bt2;
    private CardView bt3;

    private ViewGroup container;

    // TODO: Rename and change types of parameters

    public CreaEsercizi() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreaEsercizi.
     */
    // TODO: Rename and change types and number of parameters
    public static CreaEsercizi newInstance(String userkey) {
        CreaEsercizi fragment = new CreaEsercizi();
        Bundle args = new Bundle();
        args.putString(SESSION_KEY, userkey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(SESSION_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // GESTIONE PULSANTE BACK
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(container.getId() , ElencoEsercizi.newInstance(userID))
                        .commit();
            }
        };

        // Aggiungi il callback al gestore dei pressioni del pulsante "back"
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(container.getId() , creaEsercizio1.newInstance(userID))
                .commit();
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(container.getId() , creaEsercizio2.newInstance(userID))
                .commit();
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(container.getId() , creaEsercizio3.newInstance(userID))
                .commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crea_esercizi, container, false);

        //String ok=getIntent().getExtras().getString("nome");
        //toolbar.setTitle(ok);
        bt1=view.findViewById(R.id.tipoEs1);
        bt2=view.findViewById(R.id.tipoEs2);
        bt3=view.findViewById(R.id.tipoEs3);

        this.container = container;

        return view;
    }
}