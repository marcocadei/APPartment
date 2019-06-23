package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.unison.appartment.R;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        final WebView webView = findViewById(R.id.activity_web_webview);

        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);

        /*
        Codice javascript invocato al termine del caricamento della pagina. Rimuove tutti gli
        href dai link della pagina e toglie la possibilità di premere il bottone per tornare alla
        pagina di generazione di un nuovo numero.
         */
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:(function() { " +
                        "var anchors = document.getElementsByTagName(\"a\");" +
                        "for (var i = 0; i < anchors.length; i++) {" +
                        "anchors[i].removeAttribute(\"href\");" +
                        "}" +
                        "var btnAltroNumero = document.getElementById(\"ctl00_cphContenuto_btnGeneraAltroNumero\");" +
                        "if (btnAltroNumero != null) {" +
                        "btnAltroNumero.setAttribute(\"style\", \"pointer-events: none\");" +
                        "}" +
                        "})()");
            }
        });

        webView.loadUrl("file:///android_asset/er_redirect.html");

        /*
        Listener che permette di ignorare i long-press (inserito così che l'utente non possa
        selezionare testo e visualizzare così opzioni non desiderate).
         */
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        /*
        Listener che permette di ignorare ogni tipo di touch event
        (disabilitato così da permettere all'utente di scrollare e zoomare).
         */
//        webView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                finish();
//                return true;
//            }
//        });
    }
}
