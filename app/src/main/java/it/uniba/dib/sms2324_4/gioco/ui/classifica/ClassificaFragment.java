package it.uniba.dib.sms2324_4.gioco.ui.classifica;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import it.uniba.dib.sms2324_4.R;

public class ClassificaFragment extends Fragment {

    public ClassificaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_classifica, container, false);

        // Ottieni il riferimento alla TableLayout all'interno del fragment
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayout);

        // Aggiungi del margine al TableLayout
        int tableLayoutMarginTop = 32; // Imposta il margine superiore a tuo piacimento
        ViewGroup.MarginLayoutParams tableLayoutParams = (ViewGroup.MarginLayoutParams) tableLayout.getLayoutParams();
        tableLayoutParams.topMargin = tableLayoutMarginTop;

        // Trova il TextView del secondo posto
        TextView scoreTextView2 = rootView.findViewById(R.id.secondo_posto);
        // Imposta il testo del secondo posto
        scoreTextView2.setText("vituzzz");

        // Trova il TextView del secondo posto
        TextView scoreTextView1 = rootView.findViewById(R.id.primo_posto);
        // Imposta il testo del secondo posto
        scoreTextView1.setText("tako");

        // Trova il TextView del secondo posto
        TextView scoreTextView3 = rootView.findViewById(R.id.terzo_posto);
        // Imposta il testo del secondo posto
        scoreTextView3.setText("sane");

        // Crea una nuova riga
        TableRow row;

        // Aggiungi la cella alla riga
        row = new TableRow(getActivity());


        // Aggiungi la riga al TableLayout
        tableLayout.addView(row);

        // Itera per aggiungere il resto delle celle come hai già fatto
        for (int i = 3; i < 20; i++) {
            // Crea una nuova cella utilizzando la funzione createCellView
            View cellView = createCellView("- " + (i + 1) + "  andrea leone " + "                               " + "1200");

            // Aggiungi la cella alla riga
            row = new TableRow(getActivity());
            row.addView(cellView);

            // Aggiungi la riga al TableLayout
            tableLayout.addView(row);
        }

        return rootView;
    }


    private View createCellView(String prefix) {
        // Inflate il layout della cella
        View cellView = LayoutInflater.from(getActivity()).inflate(R.layout.cella_layout, null);

        // Trova il TextView all'interno della cella
        TextView textView = cellView.findViewById(R.id.testoTextView);

        // Imposta il testo nel TextView con il prefisso e il punteggio
        textView.setText(prefix + " " );

        return cellView;
    }

}