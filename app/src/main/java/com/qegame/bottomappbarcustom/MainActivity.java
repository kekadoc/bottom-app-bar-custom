package com.qegame.bottomappbarcustom;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.qegame.animsimple.Anim;
import com.qegame.bottomappbarqe.BottomAppBarQe;
import com.qegame.qeutil.listener.Subscriber;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity-TAG";

    private BottomAppBarQe bottomAppBarQe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomAppBarQe = findViewById(R.id.bar);
        bottomAppBarQe.setConstruction(getFabCenter());
        bottomAppBarQe.setSnackBarCorners(BottomAppBarQe.Corner.CUT, 10);
        bottomAppBarQe.getOnProgressCompletely().addSub(new Subscriber.Simple() {
            @Override
            public void doIt() {
                bottomAppBarQe.showSnackBar("Complete!");
            }
        });
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
                        bottomAppBarQe.showSnackBar("This is SnackBar!", 100000000);
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
                        Random random = new Random();
                        int r = random.nextInt(256);
                        int g = random.nextInt(256);
                        int b = random.nextInt(256);
                        int color = Color.argb(255, r, g ,b);
                        bottomAppBarQe.setColorPanel(color);

                        int r1 = random.nextInt(256);
                        int g1 = random.nextInt(256);
                        int b1 = random.nextInt(256);
                        int color1 = Color.argb(255, r1, g1 ,b1);

                        bottomAppBarQe.setFabColor(color1);
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
