package com.qegame.bottomappbarcustom;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qegame.bottomappbarqe.BottomAppBarQe;
import com.qegame.qeutil.QeUtil;
import com.qegame.qeutil.androids.QeAndroid;
import com.qegame.qeutil.doing.Do;
import com.qegame.qeutil.graph.QeColor;
import com.qegame.qeutil.listening.subscriber.Subscriber;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity-TAG";

    private BottomAppBarQe bottomAppBarQe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomAppBarQe = findViewById(R.id.bar);
        bottomAppBarQe.setConstruction(getFabCenter());
        bottomAppBarQe.snack().setCorners(BottomAppBarQe.Snack.Corner.CUT, 10);
        bottomAppBarQe.progress().onCompletely().subscribe(new Subscriber() {
            @Override
            public void onCall() {
                bottomAppBarQe.snack().show("Complete!");
            }
        });

        //bottomAppBarQe.sheet().setHeight((int) QeAndroid.dp(this, 50));
    }

    public BottomAppBarQe.Construction.FABCenter getFabCenter() {

        BottomAppBarQe.FABSettings fab = new BottomAppBarQe.FABSettings() {
            @Override
            public Drawable getImage() {
                return getDrawable(R.drawable.help);
            }

            @Override
            public View.OnClickListener getClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomAppBarQe.setConstruction(getFabEnd());
                    }
                };
            }

            @Override
            public void createAnimation(FloatingActionButton fab, Drawable image) {

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
                        if (bottomAppBarQe.progress().isShown()) bottomAppBarQe.progress().remove(true);
                        else bottomAppBarQe.progress().show();
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
                        bottomAppBarQe.progress().add(10);
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
                        bottomAppBarQe.sheet().getView().addView(new MaterialButton(MainActivity.this));
                        //bottomAppBarQe.snack().show("This is SnackBar!", 100000000);
                        bottomAppBarQe.snack().make("This is SnackBar!")
                                .buttonColorBody(Color.RED)
                                .buttonColorRipple(Color.BLUE)
                                .buttonText("GG")
                                .onButtonClick(new Do() {
                                    @Override
                                    public void work() {
                                        bottomAppBarQe.snack().show("Snack Close!");
                                    }
                                })
                                .colorBody(Color.MAGENTA)
                                .colorText(Color.LTGRAY)
                                .radius(10)
                                .duration(10000)
                                .show();
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

                        bottomAppBarQe.setColorPanel(QeColor.getRandomColor());
                        bottomAppBarQe.setFabColor(QeColor.getRandomColor());
                        bottomAppBarQe.progress().setColor(QeColor.getRandomColor());
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
                Log.e(TAG, "getImage: ");
                return getDrawable(R.drawable.help);
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
                        bottomAppBarQe.sheet().swich();
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
                        bottomAppBarQe.sheet().setHeight(bottomAppBarQe.sheet().getHeight() + 50);
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
                        bottomAppBarQe.sheet().setColor(Color.MAGENTA);
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
                return null;
            }
        };
        return new BottomAppBarQe.Construction.FABEnd(fab, icon_0, icon_1, icon_2, icon_3);
    }



}
