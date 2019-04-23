package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private int selectedBottomNavigationMenuItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Supporto per la toolbar
        final Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigation = findViewById(R.id.activity_main_bottom_navigation);
        selectedBottomNavigationMenuItemId = bottomNavigation.getSelectedItemId();
        final MenuItem selectedBottomNavigationMenuItem = bottomNavigation.getMenu().findItem(selectedBottomNavigationMenuItemId);
        toolbar.setTitle(selectedBottomNavigationMenuItem.getTitle());
        toolbar.setLogo(selectedBottomNavigationMenuItem.getIcon());

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                /*
                In questo modo quando si seleziona la stessa sezione in cui si è già non viene
                ricaricato il fragment (semplicemente non viene fatto nulla).
                 */
                if (menuItem.getItemId() != selectedBottomNavigationMenuItemId) {
                    // TODO Switch Fragment
                    toolbar.setTitle(menuItem.getTitle());
                    toolbar.setLogo(menuItem.getIcon());
                }
                selectedBottomNavigationMenuItemId = menuItem.getItemId();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // FIXME Da sistemare
            case R.id.activity_main_toolbar_settings:
//                Log.d(this.getLocalClassName(), "Premuto ingraggio");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
