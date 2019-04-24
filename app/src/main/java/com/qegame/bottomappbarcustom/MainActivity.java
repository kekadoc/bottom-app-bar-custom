package com.qegame.bottomappbarcustom;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.qegame.animsimple.Anim;
import com.qegame.bottomappbarqe.BottomAppBarQe;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity-ИНФ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final BottomAppBarQe bar = findViewById(R.id.bar);

        //bar.setElevation(200);

        bar.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.showSnackBar("This is SnackBar", Snackbar.LENGTH_LONG);
            }
        });

    }

}
