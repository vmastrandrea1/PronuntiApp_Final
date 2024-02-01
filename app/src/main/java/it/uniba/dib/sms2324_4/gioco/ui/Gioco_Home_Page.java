package it.uniba.dib.sms2324_4.gioco.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.gioco.ui.classifica.ClassificaFragment;
import it.uniba.dib.sms2324_4.gioco.ui.gioco.GiocoFragment;
import it.uniba.dib.sms2324_4.gioco.ui.shop.ShopFragment;

public class Gioco_Home_Page extends Fragment {

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOPOPEDISTA";
    private String id_bambino;
    private String sessionKey;
    private String id_logopedista;

    public Gioco_Home_Page() {
        // Required empty public constructor
    }

    public static Gioco_Home_Page newInstance(String id_bambino, String sessionKey, String id_logopedista) {
        Gioco_Home_Page fragment = new Gioco_Home_Page();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY, sessionKey);
        args.putString(ID_LOGOPEDISTA, id_logopedista);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_bambino = getArguments().getString(BAMBINO_ID);
            sessionKey = getArguments().getString(SESSION_KEY);
            id_logopedista = getArguments().getString(ID_LOGOPEDISTA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gioco__home__page, container, false);
        FragmentManager fragmentManager_gioco = requireActivity().getSupportFragmentManager();

        GiocoFragment fragmentGioco = GiocoFragment.newInstance(id_bambino, sessionKey, id_logopedista);
        fragmentManager_gioco.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentGioco)
                .addToBackStack(null)
                .commit();

        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottomBar);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        return v;
    }

     BottomNavigationView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.shop) {
            ShopFragment fragmentShop = ShopFragment.newInstance(id_bambino, sessionKey, id_logopedista);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragmentShop)
                    .addToBackStack(null)
                    .commit();
        } else if (itemId == R.id.gioco) {
            GiocoFragment fragmentGioco = GiocoFragment.newInstance(id_bambino, sessionKey, id_logopedista);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragmentGioco)
                    .addToBackStack(null)
                    .commit();
        } else if (itemId == R.id.classifica) {
            ClassificaFragment fragmentClassifica = new ClassificaFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragmentClassifica)
                    .addToBackStack(null)
                    .commit();
        }
        return true;
    };
}
