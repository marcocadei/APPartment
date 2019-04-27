package com.unison.appartment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.unison.appartment.model.AudioPost;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInsertPostFragmentListener} interface
 * to handle interaction events.
 * Use the {@link InsertPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertPostFragment extends Fragment {

    // Edittext contenente l'input del messaggio
    private EditText inputText;

    // Request code per aprire l'activity usata per caricare un'immagine
    private static int RESULT_LOAD_IMAGE = 1;
    // Listener usato per la gestione degli eventi interni al fragment
    private OnInsertPostFragmentListener mListener;
    // Oggetto usato per la registrazione di audio
    private MediaRecorder recorder;
    // Flag usato per monitorare se è in corso una registrazione
    private boolean isRecording = false;
    // Name of the file in which i save audio (just 1 for now)
    private String fileName;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View myView =  inflater.inflate(R.layout.fragment_insert_post, container, false);

        final ImageButton btnSendText = myView.findViewById(R.id.fragment_insert_post_btn_send_text);
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

        final ImageButton btnSendAudio = myView.findViewById(R.id.fragment_insert_post_btn_send_audio);
        btnSendAudio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    // Controllo di avere il permesso di registrare
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Non ho il permesso di registrare, quindi lo richiedo
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                AudioPost.PERMISSION_REQUEST_RECORDER);
                        Log.d("audio_prova", "no Permesso di registrare");
                    } else {
                        // Ho il permesso di registrare
                        Log.d("audio_prova", "Permesso di registrare");
                        isRecording = true;
                        startRecording();
                    }
                }
                return true;
            }
        });
        btnSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    Log.d("audio_prova", "registrazione terminata");
                    stopRecording();
                    // Una volta terminata la registrazione dell'audio aggiungo il post
                    mListener.onInsertPostFragmentSendAudio(fileName);
                }
                isRecording = !isRecording;
            }
        });
        // Cambio il colore del bottone di invio del testo in base al fatto che il campo di input
        // sia riempito o meno
        inputText = myView.findViewById(R.id.fragment_insert_post_input_text);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    btnSendText.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gray));
                    btnSendText.setClickable(false);
                } else {
                    btnSendText.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    btnSendText.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Il permesso è stato fornito, posso effettuare la registrazione
                    // Non faccio nulla perché per iniziare la registrazione deve tenere premuto
                } else {
                    // Il permesso è stato negato, non effettuo la registrazione
                    // TODO mettere un qualche effetto grafico, es microfono barrato
                }
                return;
            }
            // Altri CASE se l'applicazione richiede anche altri permessi
        }
    }

    /**
     * Inizio la registrazione
     */
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        fileName = getActivity().getExternalCacheDir().getAbsolutePath() + "/audiotest.3gp";
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            // Qualcosa è andato storto con la registrazione
        }
        Log.d("audio_prova", "iniziata registrazione");
        inputText.setText("Registrazione in corso");
        recorder.start();
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;

            inputText.getText().clear();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnInsertPostFragmentListener) {
            mListener = (OnInsertPostFragmentListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInsertPostFragmentListener errore in insert");
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
        void onInsertPostFragmentSendAudio(String fileName);
    }
}
