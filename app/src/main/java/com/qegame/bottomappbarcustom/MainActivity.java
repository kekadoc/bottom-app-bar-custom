package com.qegame.bottomappbarcustom;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.qegame.animsimple.Anim;
import com.qegame.bottomappbarqe.BottomAppBarQe;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity-ИНФ";

    private BottomAppBarQe bottomAppBarQe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomAppBarQe = findViewById(R.id.bar);
        bottomAppBarQe.setConstruction(getFabCenter());
    }

    public BottomAppBarQe.Construction.FABCenter getFabCenter() {
        BottomAppBarQe.FABSettings fab = new BottomAppBarQe.FABSettings() {
            @Override
            public Drawable getImage() {
                return null;
            }

            @Override
            public View.OnClickListener getClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomAppBarQe.addProgressPercent(20);
                    }
                };
            }

            @Override
            public Anim getAnimation(Anim animDefault) {
                return animDefault;
            }
        };
        BottomAppBarQe.IconSettings icon_0 = new BottomAppBarQe.IconSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomAppBarQe.showProgressBar();
                        Log.e(TAG, "onClick: 0");
                    }
                };
            }
        };
        BottomAppBarQe.IconSettings icon_1 = new BottomAppBarQe.IconSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomAppBarQe.removeProgressBar();
                        Log.e(TAG, "onClick: 1");
                    }
                };
            }
        };

        BottomAppBarQe.IconSettings icon_2 = new BottomAppBarQe.IconSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "onClick: 2");
                    }
                };
            }
        };
        BottomAppBarQe.IconSettings icon_3 = new BottomAppBarQe.IconSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "onClick: 3");
                    }
                };
            }
        };
        return new BottomAppBarQe.Construction.FABCenter(fab, new BottomAppBarQe.IconSettings[] {icon_0, icon_1}, new BottomAppBarQe.IconSettings[] {icon_2, icon_3});
    }
    public BottomAppBarQe.Construction.FABEnd getFabEnd() {
        BottomAppBarQe.FABSettings fab = new BottomAppBarQe.FABSettings() {
            @Override
            public Drawable getImage() {
                return null;
            }

            @Override
            public View.OnClickListener getClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomAppBarQe.setConstruction(getFabCenter());
                    }
                };
            }

            @Override
            public Anim getAnimation(Anim animDefault) {
                return animDefault;
            }
        };
        BottomAppBarQe.IconSettings icon_0 = new BottomAppBarQe.IconSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return null;
            }
        };
        BottomAppBarQe.IconSettings icon_1 = new BottomAppBarQe.IconSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return null;
            }
        };
        BottomAppBarQe.IconSettings icon_2 = new BottomAppBarQe.IconSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return null;
            }
        };
        BottomAppBarQe.IconSettings icon_3 = new BottomAppBarQe.IconSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return null;
            }
        };
        return new BottomAppBarQe.Construction.FABEnd(fab, icon_0, icon_1, icon_2, icon_3);
    }
}
