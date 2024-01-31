package it.uniba.dib.sms2324_4.gioco.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.gioco.ui.classifica.ClassificaFragment;
import it.uniba.dib.sms2324_4.gioco.ui.gioco.GiocoFragment;
import it.uniba.dib.sms2324_4.gioco.ui.shop.ShopFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Gioco_Home_Page#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Gioco_Home_Page extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOPOPEDISTA";
    private String id_bambino;
    private String sessionKey;
    private String id_logopedista;


    public Gioco_Home_Page() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Gioco_Home_Page.
     */
    // TODO: Rename and change types and number of parameters
    public static Gioco_Home_Page newInstance(String id_bambino,String sessionKey,String id_logopedista) {
        Gioco_Home_Page fragment = new Gioco_Home_Page();
        Bundle args = new Bundle();
        args.putString(BAMBINO_ID, id_bambino);
        args.putString(SESSION_KEY,sessionKey);
        args.putString(ID_LOGOPEDISTA,id_logopedista);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_gioco__home__page, container, false);

        FragmentManager fragmentManager_gioco = requireActivity().getSupportFragmentManager();

        GiocoFragment fragmentGioco = GiocoFragment.newInstance(id_bambino,sessionKey,id_logopedista);
        fragmentManager_gioco.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentGioco)
                .addToBackStack(null)
                .commit();

        ImageButton imageButton1 = v.findViewById(R.id.shop);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea e mostra il fragment quando lo shop viene cliccato
                ShopFragment fragmentShop = ShopFragment.newInstance(id_bambino,sessionKey,id_logopedista);
                fragmentManager_gioco.beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentShop)
                        .addToBackStack(null)
                        .commit();
            }
        });


        ImageButton imageButton2 = v.findViewById(R.id.gioco);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea e mostra il fragment quando il gioco viene cliccato
                GiocoFragment fragmentGioco = GiocoFragment.newInstance(id_bambino,sessionKey,id_logopedista);
                fragmentManager_gioco.beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentGioco)
                        .addToBackStack(null)
                        .commit();
            }
        });


        ImageButton imageButton3 = v.findViewById(R.id.classifica);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea e mostra il fragment quando la classifica viene cliccata
                ClassificaFragment fragmentClassifica = new ClassificaFragment();
                fragmentManager_gioco.beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentClassifica)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }
}