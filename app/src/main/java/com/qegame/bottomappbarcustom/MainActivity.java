package com.qegame.bottomappbarcustom;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import com.qegame.bottomappbarqe.BottomAppBarQe;
import com.qegame.qeutil.QeUtil;
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
    }

    public BottomAppBarQe.Construction.FABCenter getFabCenter() {

        BottomAppBarQe.FABSettings fab = new BottomAppBarQe.FABSettings() {
            @Override
            public Drawable getImage() {
                return getResources().getDrawable(R.drawable.help);
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

                        bottomAppBarQe.snack().show("This is SnackBar!", 100000000);
                        /*bottomAppBarQe.snack().make("This is SnackBar!")
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
                                .radius(50)
                                .duration(10000)
                                .show();*/
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

                        bottomAppBarQe.setColorPanel(getRandomColor());
                        bottomAppBarQe.setFabColor(getRandomColor());
                        bottomAppBarQe.progress().setColor(getRandomColor());
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


    public static int getRandomColor() {
        return getRandomColor(255);
    }
    public static int getRandomColor(int alpha) {
        return Color.argb(alpha, QeUtil.getRandom().nextInt(256), QeUtil.getRandom().nextInt(256) , QeUtil.getRandom().nextInt(256));
    }
}
