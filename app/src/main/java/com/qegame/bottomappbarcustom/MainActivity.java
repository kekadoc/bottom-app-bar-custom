package com.qegame.bottomappbarcustom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity-ИНФ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final BottomAppBarCustom babc = findViewById(R.id.babc);

        babc.setElevation(200);

        babc.showSnackBar("gggg");
        babc.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Snackbar snackbar = Snackbar.make(findViewById(R.id.cont), "", 2000);
                View view = snackbar.getView();


                snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                params.setAnchorId(R.id.coordinator);
                params.anchorGravity = Gravity.TOP;
                params.gravity = Gravity.TOP;
                params.setMargins(111, 0, 111, 0);
                view.setElevation(0);


                view.setLayoutParams(params);
                snackbar.show();*/
                babc.showSnackBar("sh", 2000);
            }
        });
    }

}
