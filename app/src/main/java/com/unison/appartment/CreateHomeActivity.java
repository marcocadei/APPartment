package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_home);

        final EditText inputName = findViewById(R.id.activity_create_home_input_name_value);
        final EditText inputPassword = findViewById(R.id.activity_create_home_input_password_value);

        FloatingActionButton floatNext = findViewById(R.id.activity_create_home_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateHomeActivity.this, CreateMemberActivity.class);
                // Passo i parametri della casa all'activity successiva
                i.putExtra("homeName", inputName.getText().toString());
                i.putExtra("homePassword", inputPassword.getText().toString());
                i.putExtra("origin", "fromEnter");
                startActivity(i);
            }
        });
    }
}
