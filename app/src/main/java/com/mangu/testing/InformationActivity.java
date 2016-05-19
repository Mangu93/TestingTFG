package com.mangu.testing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
    }

    /**
     * Abre cliente de correo al pulsar en el correo.
     *
     */
    public void onClickCorreo(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{this.getString(R.string.correo)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sobre la aplicación para el TFG");
        startActivity(Intent.createChooser(intent, ""));
    }
}
