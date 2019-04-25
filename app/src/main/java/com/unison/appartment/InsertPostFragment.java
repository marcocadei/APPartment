package com.unison.appartment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.unison.appartment.model.AudioPost;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInsertPostFragmentListener} interface
 * to handle interaction events.
 * Use the {@link InsertPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertPostFragment extends Fragment {

    // Request code per aprire l'activity usata per caricare un'immagine
    private static int RESULT_LOAD_IMAGE = 1;

    private OnInsertPostFragmentListener mListener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public InsertPostFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InsertPostFragment.
     */
    public static InsertPostFragment newInstance() {
        InsertPostFragment fragment = new InsertPostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View myView =  inflater.inflate(R.layout.fragment_insert_post, container, false);

        ImageButton btnSendText = myView.findViewById(R.id.fragment_insert_post_btn_send_text);
        btnSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    EditText inputText = myView.findViewById(R.id.fragment_insert_post_input_text);
                    mListener.onInsertPostFragmentSendText(inputText.getText().toString());
                    // Ripulisco l'edit text dopo che il messaggio è stato inviato
                    inputText.getText().clear();
                }
            }
        });

        ImageButton btnSendImg = myView.findViewById(R.id.fragment_insert_post_btn_send_img);
        btnSendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // I risultati di questa chiamata vengono recuperati in #onActivityResult
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });

        ImageButton btnSendAudio = myView.findViewById(R.id.fragment_insert_post_btn_send_audio);
        btnSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Controllo di avere il permesso di registrare
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Non ho il permesso di registrare, quindi lo richiedo
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                AudioPost.PERMISSION_REQUEST_RECORDER);

                    } else {
                        // Ho il permesso di registrare


                    }
                }
            }
        });

        return myView;
    }

    /**
     * Recupero dell'immagine selezionata dall'utente
     * @param requestCode il codice della richiesta
     * @param resultCode il codice del risultato
     * @param data i dati restituiti dall'activity chiamata
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            mListener.onInsertPostFragmentSendImage(selectedImage);
        }
    }

    /**
     * Recupero i permessi per la registrazione di audio
     * @param requestCode il codice della richiesta
     * @param permissions i permessi richiesti
     * @param grantResults i risultati ottenuti
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AudioPost.PERMISSION_REQUEST_RECORDER: {
                // Se la richiesta è cancellata l'array dei risultati è vuoto
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Il permesso è stato fornito, posso effettuare la registrazione
                } else {
                    // Il permesso è stato negato, non effettuo la registrazione
                }
                return;
            }
            // Altri CASE se l'applicazione richiede anche altri permessi
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInsertPostFragmentListener) {
            mListener = (OnInsertPostFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInsertPostFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Questa interfaccia deve essere implementata dalle activity che contengono questo
     * fragment, per consentire al fragment di comunicare eventuali interazioni all'activity
     * che a sua volta può comunicare con altri fragment
     */
    public interface OnInsertPostFragmentListener {
        void onInsertPostFragmentSendText(String message);
        void onInsertPostFragmentSendImage(Uri selectedImage);
    }
}
