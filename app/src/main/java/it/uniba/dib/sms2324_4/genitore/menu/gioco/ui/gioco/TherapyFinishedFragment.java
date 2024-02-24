package it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.gioco;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.dib.sms2324_4.R;


public class TherapyFinishedFragment extends Fragment {

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static  final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOGOPEDISTA";
    private String id_bambino;
    private String sessionKey_genitore;
    private String id_logopedista;

    public TherapyFinishedFragment() {
        // Required empty public constructor
    }


    public static TherapyFinishedFragment newInstance(String id_bambino, String sessionKey_genitore,
                                                      String id_logopedista) {
        TherapyFinishedFragment fragment = new TherapyFinishedFragment();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY,sessionKey_genitore);
        args.putString(ID_LOGOPEDISTA,id_logopedista);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_bambino = getArguments().getString(BAMBINO_ID);
            sessionKey_genitore = getArguments().getString(SESSION_KEY);
            id_logopedista = getArguments().getString(ID_LOGOPEDISTA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // GESTIONE PULSANTE BACK
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Chiudi l'app
                getActivity().finish();
            }
        };

        // Aggiungi il callback al gestore dei pressioni del pulsante "back"
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_therapy_finished, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Mostra la navbar
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomBar);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }


}