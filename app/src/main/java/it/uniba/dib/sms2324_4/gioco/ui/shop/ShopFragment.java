package it.uniba.dib.sms2324_4.gioco.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import it.uniba.dib.sms2324_4.R;
import it.uniba.dib.sms2324_4.gioco.ui.gioco.GiocoFragment;

public class ShopFragment extends Fragment {

    private int monete;
    private ItemAdapter itemAdapter;
    private TextView textViewMonete;

    private static final String BAMBINO_ID = "BAMBINO_ID";
    private static  final String SESSION_KEY = "SESSION_KEY";
    private static final String ID_LOGOPEDISTA = "ID_LOGOPEDISTA";
    private String id_bambino;
    private String sessionKey;
    private String id_logopedista;

    public static ShopFragment newInstance(String id_bambino,String sessionKey,String id_logopedista) {
        ShopFragment fragment = new ShopFragment();
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Creazione della lista di personaggi
        ArrayList<Item> personaggi = creaListaPersonaggi();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp-register-default-rtdb.europe-west1.firebasedatabase.app/");

        for (Iterator<Item> it = personaggi.iterator(); it.hasNext(); ) {
            Item item = it.next();
            Query item_acquistato = database.getReference("Utenti")
                    .child("Genitori")
                    .child(sessionKey)
                    .child("Bambini")
                    .child(id_bambino)
                    .child("Skin")
                    .child(String.valueOf(item.getId()));
            item_acquistato.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        item.setAcquistato(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // Configurazione della GridView con un adattatore personalizzato
        GridView gridViewPersonaggi = view.findViewById(R.id.gridViewPersonaggi);
        itemAdapter = new ItemAdapter(getContext(), personaggi, this ,id_bambino,sessionKey,id_logopedista,container,getParentFragmentManager());
        gridViewPersonaggi.setAdapter(itemAdapter);

        // Inizializza il TextView per visualizzare il contatore delle monete
        textViewMonete = view.findViewById(R.id.textViewMonete);
        updateMoneteText();

        return view;
    }

    private ArrayList<Item> creaListaPersonaggi() {
        ArrayList<Item> item = new ArrayList<>();

        item.add(new Item(0,"Tony ", R.drawable.bimbo, 0, descrizione0,Item.ItemType.PERSONAGGIO));
        item.get(0).setAcquistato(true);
        item.add(new Item(1,"Torvaldo", R.drawable.skin_troll, 500, descrizione1,Item.ItemType.PERSONAGGIO));
        item.add(new Item(2,"Rex Fulminatus", R.drawable.skin_dinosauro, 600, descrizione2,Item.ItemType.PERSONAGGIO));
        item.add(new Item(3,"Gelatiello", R.drawable.skin_gelato, 700, descrizione3,Item.ItemType.PERSONAGGIO));
        item.add(new Item(4,"Trombone", R.drawable.skin_stregone, 800, descrizione4,Item.ItemType.PERSONAGGIO));
        item.add(new Item(5,"Oculopodio ", R.drawable.skin_polpo, 900, descrizione5,Item.ItemType.PERSONAGGIO));
        item.add(new Item(6,"Robotelmo ", R.drawable.skin_robot, 1000, descrizione6,Item.ItemType.PERSONAGGIO));

        item.add(new Item(7,"Sentiero", R.drawable.map_sentiero, 0, descrizione7,Item.ItemType.SCENARIO));
        item.get(7).setAcquistato(true);
        item.add(new Item(8,"Flying island", R.drawable.map_flying_island, 600, descrizione8,Item.ItemType.SCENARIO));
        item.add(new Item(9,"Marte", R.drawable.map_mars, 700, descrizione9,Item.ItemType.SCENARIO));

        item.add(new Item(10,"Cielo", R.drawable.sfondo_cielo, 0, descrizione12,Item.ItemType.SFONDO));
        item.get(10).setAcquistato(true);
        item.add(new Item(11,"Spazio viola", R.drawable.sfondo_spazioviola, 600, descrizione11,Item.ItemType.SFONDO));
        item.add(new Item(12,"Onde di colore", R.drawable.sfondo_onde_di_colore, 500, descrizione10,Item.ItemType.SFONDO));
        item.add(new Item(13,"Natura", R.drawable.sfondo_natura, 800, descrizione13,Item.ItemType.SFONDO));

        return item;
    }

    public void updateMoneteText() {
        textViewMonete.setText("Monete: " + monete);
    }

    public int getMonete() {
        return monete;
    }

    public void decrementaMonete(int prezzo) {
        monete -= prezzo;
        updateMoneteText();
    }

    // metodo per impostare il valore delle monete
    public void setMonete(int monete) {
        this.monete = monete;
    }

    //metodo per cambiare lo sfondo
    public void cambiaSfondo(Item item){

    }




    String descrizione0 = "Ladri in casa";
    String descrizione1 = "Torvaldo è un imponente troll con una lunga barba intrecciata e uno spesso mantello di pelli. Indossa un elmo con corna imponenti e brandisce un'enorme ascia da guerra. Nonostante l'aspetto feroce, ha un cuore leale e un senso dell'umorismo unico tra i troll.";
    String descrizione2 = "Rex Fulminatus è un T-Rex maestoso e potente, con scaglie lucenti che riflettono la luce del sole. La sua coda è lunga e muscolosa, e gli occhi esprimono saggezza antica. Porta con sé un'aria regale, come se fosse il monarca indiscusso del mondo dei dinosauri.";
    String descrizione3 = "Gelatiello Radiale è un cono gelato vivace e colorato con occhiali da sole alla moda, sempre pronto a diffondere gioia. Si muove agilmente per la città su uno skateboard, portando un sorriso a tutti. I suoi gusti variano, e gli occhiali riflettono il sole del caldo giorno estivo mentre sfreccia sulla strada.";
    String descrizione4 = "Trombone l'Incantatore è un musicista straordinario che suona il trombone, vestito con un mantello scuro e ornamenti magici. Le sue note possono incantare chiunque le ascolti, trasportandoli in mondi fantastici. Ha un cappello a punta decorato con stelle e lune.";
    String descrizione5 = "Oculopodio è un polpo straordinario con un unico occhio centrale. Le sue braccia sono adornate da simpatiche ventose e possiede una straordinaria intelligenza. Si muove elegantemente nell'acqua, sempre alla ricerca di avventure e scoperte sottomarine.";
    String descrizione6 = "Roboelmo è un robot futuristico con un elmetto decorato. Il suo corpo metallico riflette la luce, e le sue antenne emettono segnali luminosi. Dotato di un'intelligenza artificiale avanzata, Roboelmo è programmato per svolgere compiti vari, ma ha anche una personalità affabile e curiosa.";


    String descrizione7 = "Il Sentiero Celestiale è una via incantata che si snoda attraverso un bosco antico, illuminato da luci fatate e circondato da alberi maestosi. Il terreno morbido e liscio rende il cammino un'esperienza piacevole, mentre creature magiche danzano tra gli alberi, rendendo questo sentiero un'incantevole avventura.";
    String descrizione8 = "L'Isola Volante è un paradiso sospeso tra le nuvole, avvolto da una luce eterea e circondato da scintillanti cascate d'aria. Su questa massa di terra fluttuante, alberi fantastici e giardini colorati prosperano, mentre creature volanti e libellule giganti sfrecciano nell'aria. L'atmosfera qui è pervasa da un senso di magia e meraviglia, trasformando ogni visita in un viaggio straordinario.";
    String descrizione9 = "Marte, il quarto pianeta dal Sole, cattura l'immaginazione con la sua paesaggistica aliena. La Mappa di Marte offre uno sguardo dettagliato su questa terra rossa e misteriosa, evidenziando le caratteristiche uniche che la rendono così affascinante per gli astronomi e gli appassionati di esplorazione spaziale. Dalla sua atmosfera sottile ai suoi poli ghiacciati, Marte è un mondo ricco di enigmi e promesse di scoperte future.";


    String descrizione10 = "L'atmosfera vivace di questo sfondo è composta da onde di colore vibranti che si mescolano in armonia. I toni accesi e la transizione fluida tra sfumature creano un'atmosfera dinamica e accattivante. Ogni onda porta con sé un'energia positiva, rendendo questo sfondo perfetto per ispirare creatività e vitalità.";
    String descrizione11 = "Un affascinante viaggio nello spazio profondo, questo sfondo viola offre uno sguardo surreale alle meraviglie dell'universo. Tra stelle scintillanti e galassie lontane, il colore viola avvolge lo spazio cosmico, creando un'atmosfera misteriosa e magica. È un invito a esplorare l'ignoto e a contemplare l'infinito.";
    String descrizione12 = "Il cielo sereno domina questo sfondo, dipingendo un'immagine di tranquillità e bellezza. Le tonalità di azzurro si sfumano delicatamente da un orizzonte all'altro, mentre nuvole soffici decorano il panorama. Questo sfondo evoca una sensazione di pace e serenità, ideale per momenti di riflessione e relax.";
    String descrizione13 = "Una rappresentazione ricca e variegata della bellezza naturale, questo sfondo cattura la diversità degli ambienti naturali. Da boschi lussureggianti a prati fioriti, da montagne maestose a fiumi sinuosi, la natura si manifesta in tutta la sua grandezza e varietà. Questo sfondo celebra la meraviglia della vita selvaggia e invita a connettersi con la bellezza della Terra.";


}

