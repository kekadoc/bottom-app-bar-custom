package com.qegame.bottomappbarqe;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.qegame.animsimple.Anim;
import com.qegame.qeshaper.QeShaper;
import com.qegame.qeutil.QeUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BottomAppBarQe extends LinearLayout {
    private static final String TAG = "BottomAppBarQe-TAG";

    public enum Corner {
        CUT,
        ROUND
    }

    /** Максимальное значение прогресса у ProgressBar */
    private final int MAX_PB = 360;
    private final long SNACK_BAR_ANIM_DURATION = 300;
    private final int DURATION_SNACK_DEFAULT = 2500;

    public interface FABSettings {
        Drawable getImage();
        OnClickListener getClickListener();
        Anim getAnimation(Anim animDefault);
    }
    public interface IconSettings {
        Drawable getImage();
        OnClickListener getClickListener();
    }

    private FloatingActionButton fab;
    private BottomAppBar bottomAppBar;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    private LinearLayout icons_all_left;
    private AppCompatImageView[] images_all_left;
    private LinearLayout icons_left;
    private AppCompatImageView[] images_left;
    private LinearLayout icons_right;
    private AppCompatImageView[] images_right;

    private FABSettings fabSettings;

    private int colorPrimary;
    private int colorAccent;

    private Construction construction;
    private Integer defProgress;
    private boolean progressBarShown;
    private Corner corners;
    private int radius;

    public BottomAppBarQe(Context context) {
        super(context);
        init(context, null);
    }
    public BottomAppBarQe(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public BottomAppBarQe(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    public BottomAppBarQe(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.view_bottom_app_bar_custom,this);

        fab = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bab);
        coordinatorLayout = findViewById(R.id.coordinator);
        icons_all_left = findViewById(R.id.icons_all_left);
        icons_left = findViewById(R.id.icons_left);
        icons_right = findViewById(R.id.icons_right);

        fab.setElevation(getResources().getDimension(R.dimen.elevation_fab));
        bottomAppBar.setElevation(getResources().getDimension(R.dimen.elevation_bar));
        setElevation(getResources().getDimension(R.dimen.elevation_bar));

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFabSettings().getClickListener() == null) {
                    fab.setEnabled(false);
                    fab.setClickable(false);
                } else {
                    fab.setEnabled(true);
                    fab.setClickable(true);
                    getFabSettings().getClickListener().onClick(v);
                }
            }
        });

        images_all_left = new AppCompatImageView[icons_all_left.getChildCount()];
        for (int i = 0; i < icons_all_left.getChildCount(); i++) {
            images_all_left[i] = (AppCompatImageView) icons_all_left.getChildAt(i);
        }
        images_left = new AppCompatImageView[icons_left.getChildCount()];
        for (int i = 0; i < icons_left.getChildCount(); i++) {
            images_left[i] = (AppCompatImageView) icons_left.getChildAt(i);
        }
        images_right = new AppCompatImageView[icons_right.getChildCount()];
        for (int i = 0; i < icons_right.getChildCount(); i++) {
            images_right[i] = (AppCompatImageView) icons_right.getChildAt(i);
        }

        FABSettings fab = new FABSettings() {
            @Override
            public Drawable getImage() {
                return null;
            }

            @Override
            public OnClickListener getClickListener() {
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                };
            }

            @Override
            public Anim getAnimation(Anim animDefault) {
                return animDefault;
            }
        };

        setConstruction(new Construction.FABCenter(fab, null, null));


        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary, R.attr.colorAccent });

        this.colorPrimary = a.getColor(0, 0);
        this.colorAccent = a.getColor(1, 0);

        setColorPanel(colorPrimary);
        getFab().setBackgroundTintList(ColorStateList.valueOf(colorAccent));

        setSnackBarCorners(Corner.ROUND, (int) QeUtil.dp(context, 3));
    }

    //region Getters/Setters

    public FABSettings getFabSettings() {
        return fabSettings;
    }
    public void setFabSettings(FABSettings fabSettings) {
        setFabSettings(fabSettings, true);
    }
    private void setFabSettings(FABSettings fabSettings, boolean animate) {
        this.fabSettings = fabSettings;
        if (fabSettings != null) {

            getFab().setImageDrawable(fabSettings.getImage());

            if (animate) {
                Anim anim_default = Anim.animate(fab).scale(0f, 1f, Anim.DURATION_NORMAL, new OvershootInterpolator());
                Anim anim = fabSettings.getAnimation(anim_default);
                if (anim != null) anim.start();
            }

            if (fabSettings.getClickListener() == null) {
                getFab().setEnabled(false);
                getFab().setClickable(false);
            } else {
                getFab().setEnabled(true);
                getFab().setClickable(true);
            }
        }
        removeProgressBar();
    }

    public boolean isProgressBarShown() {
        return progressBarShown;
    }

    //endregion

    public FloatingActionButton getFab() {
        return fab;
    }
    public BottomAppBar getBottomAppBar() {
        return bottomAppBar;
    }

    public void setConstruction(Construction construction) {
        this.construction = construction;

        if (construction instanceof Construction.FABEnd) {
            Construction.FABEnd construct = (Construction.FABEnd) construction;
            construct(construct.fabSettings, BottomAppBar.FAB_ALIGNMENT_MODE_END);

            for (int i = 0; i < construct.iconSettings.length; i++) {
                images_all_left[i].setVisibility(VISIBLE);
                images_all_left[i].setImageDrawable(construct.iconSettings[i].getImage());
                final IconSettings iconSettings = construct.iconSettings[i];
                images_all_left[i].setOnClickListener(iconSettings.getClickListener());
            }
            Anim anim = new Anim.MoveLeft(icons_all_left, Anim.DURATION_VERY_VERY_LONG, new OvershootInterpolator());
            anim.start();
        }

        if (construction instanceof Construction.FABCenter) {
            Construction.FABCenter construct = (Construction.FABCenter) construction;
            construct(construct.fabSettings, BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);

            if (construct.iconSettings_left != null) {
                for (int i = 0; i < construct.iconSettings_left.length; i++) {
                    images_left[i].setVisibility(VISIBLE);
                    images_left[i].setImageDrawable(construct.iconSettings_left[i].getImage());
                    final IconSettings iconSettings = construct.iconSettings_left[i];
                    images_left[i].setOnClickListener(iconSettings.getClickListener());
                }
            }
            if (construct.iconSettings_right != null) {
                for (int i = 0; i < construct.iconSettings_right.length; i++) {
                    images_right[i].setVisibility(VISIBLE);
                    images_right[i].setImageDrawable(construct.iconSettings_right[i].getImage());
                    final IconSettings iconSettings = construct.iconSettings_right[i];
                    images_right[i].setOnClickListener(iconSettings.getClickListener());
                }
            }

            Anim anim_left = new Anim.MoveLeft(icons_left, Anim.DURATION_VERY_VERY_LONG, new OvershootInterpolator());
            anim_left.start();

            Anim anim_right = new Anim.MoveRight(icons_right, Anim.DURATION_VERY_VERY_LONG, new OvershootInterpolator());
            anim_right.start();
        }
    }

    public Snackbar showSnackBar(String text, int duration) {

        final Snackbar snackbar = Snackbar.make(this, text, Snackbar.LENGTH_INDEFINITE);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ((Activity) getContext()).runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        animateSnackBarFade(snackbar);
                    }
                });
            }
        };
        timer.schedule(timerTask, duration);

        snackBarViewBuilder(snackbar, snackbar.getView());

        snackbar.show();

        QeUtil.doOnMeasureView(snackbar.getView(), new QeUtil.Do.WithIt<View>() {
            @Override
            public void doWithIt(View it) {
                animateSnackBarStart(snackbar);
            }
        });

        return snackbar;
    }
    public Snackbar showSnackBar(String text) {
        return showSnackBar(text, DURATION_SNACK_DEFAULT);
    }
    public View snackBarViewBuilder(final Snackbar snackbar, View view) {
        QeUtil.Density d = new QeUtil.Density(getContext());

        int color = getResources().getColor(com.google.android.material.R.color.design_snackbar_background_color);
        int marginSide = (int) getResources().getDimension(R.dimen.margin_side_snackbar);
        view.setElevation(getResources().getDimension(R.dimen.elevation_snack));
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        params.setAnchorId(R.id.coordinator);
        params.anchorGravity = Gravity.TOP;
        params.gravity = Gravity.TOP;
        params.setMargins(marginSide, 0, marginSide, 0);
        view.setLayoutParams(params);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) view;

        SnackbarContentLayout view_snack = (SnackbarContentLayout) snackbarLayout.getChildAt(0);
        MaterialButton button = (MaterialButton) view_snack.getChildAt(1);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateSnackBarFade(snackbar);
            }
        });
        button.setText("Ok");
        button.setTextColor(colorAccent);
        button.setVisibility(VISIBLE);

        Drawable background = QeShaper.buildDrawable(new CutCornerTreatment(d.dp(5)), new QeShaper.Corner[]{}, color, 0, colorPrimary);
        button.setBackground(background);

        button.setPadding(0, 0, 0, 0);

        LinearLayout.LayoutParams lp = (LayoutParams) button.getLayoutParams();
        lp.height = d.dp(36);
        lp.setMargins(d.dp(8),d.dp(6), d.dp(8), d.dp(6));

        if (this.corners == Corner.CUT) {
            view.setBackground(QeShaper.getCutCorners(d.dp(radius), color, colorAccent));
            button.setBackground(QeShaper.getCutCorners(d.dp(radius), color, colorPrimary));
        } else {
            view.setBackground(QeShaper.getRoundCorners(d.dp(radius), color, colorAccent));
            button.setBackground(QeShaper.getRoundCorners(d.dp(radius), color, colorPrimary));
        }

        return view;
    }
    public Anim snackBarAnimShowBuilder(View viewSnack, Anim animDefault) {
        return animDefault;
    }
    public Anim snackBarAnimFadeBuilder(View viewSnack, Anim animDefault) {
        return animDefault;
    }
    public void setSnackBarCorners(Corner corners, int radius) {
        this.corners = corners;
        this.radius = radius;
    }
    public void setSnackBarCorners(Corner corners) {
        setSnackBarCorners(corners, this.radius);
        this.corners = corners;
    }

    public void showProgressBar() {
        if (this.progressBar != null) {
            refreshProgressBar(this.progressBar.getProgress());
        } else {
            if (fab.getWidth() == 0) {
                QeUtil.doOnMeasureView(this.fab, new QeUtil.Do.WithIt<View>() {
                    @Override
                    public void doWithIt(View it) {
                        buildProgressBar();
                    }
                });
            } else {
                buildProgressBar();
            }
        }
        this.progressBarShown = true;
    }
    public void removeProgressBar() {
        Log.e(TAG, "removeProgressBar: ");
        if (this.progressBar != null) {
            Anim anim = new Anim(this.progressBar);
            anim.setEndListener(new QeUtil.Do.WithIt<Anim>() {
                @Override
                public void doWithIt(Anim it) {
                    if (progressBar != null) {
                        progressBar.setProgress(0);
                        coordinatorLayout.removeView(progressBar);
                        progressBar = null;
                        defProgress = null;
                    }
                }
            });
            anim.scale(1f, 1.5f).alpha(0f).setDuration(200L).start();
        }
        this.progressBarShown = false;
    }
    public void refreshProgressBar(int progress) {
        if (progressBar != null) {
            progressBar.setX(fab.getX());
            progressBar.setY(fab.getY());
            progressBar.getLayoutParams().height = fab.getHeight();
            progressBar.getLayoutParams().width = fab.getWidth();
            progressBar.setMax(MAX_PB);
            progressBar.setProgress(progress);
        }
    }
    public void setProgress(@IntRange(from = 0, to = MAX_PB) int value) {
        if (progressBar != null) {
            Anim.ViewAnimation.ProgressBarAnim.progressAnimation(progressBar, value);
        } else {
            this.defProgress = value;
        }
    }
    public void addProgress(int value) {
        if (this.progressBar != null) {
            setProgress(this.progressBar.getProgress() + value);
        }
    }
    public void addProgressPercent(int percent) {
        if (this.progressBar != null) {
            int step = (int) ((MAX_PB / 100.0) * percent);
            setProgress(this.progressBar.getProgress() + step);
        }
    }
    public void setProgressPercent(@FloatRange(from = 0.0, to = 100.0) float percent) {
        int step = (int) ((MAX_PB / 100.0) * percent);
        setProgress(step);
    }
    public int getProgress() {
        if (this.progressBar != null) return this.progressBar.getProgress();
        return 0;
    }
    public int getProgressPercent() {
        if (this.progressBar != null) return this.progressBar.getProgress() / this.progressBar.getMax() * 100;
        return 0;
    }


    public void setColorPanel(int color) {
        this.colorPrimary = color;
        this.bottomAppBar.setBackgroundTint(ColorStateList.valueOf(color));
        icons_all_left.setBackgroundColor(color);
        icons_left.setBackgroundColor(color);
        icons_right.setBackgroundColor(color);
    }
    public void setFabColor(int color) {
        this.colorAccent = color;
        getFab().setBackgroundTintList(ColorStateList.valueOf(colorAccent));
    }

    public void performClickIcon(int position) {
        if (this.construction instanceof Construction.FABEnd) {
            if (icons_all_left.getChildCount() > position) {
                if (icons_all_left.getChildAt(position).getVisibility() == VISIBLE) {
                    icons_all_left.getChildAt(position).performClick();
                }
            }
        }
        if (this.construction instanceof Construction.FABCenter) {
            if (icons_left.getChildCount() + icons_right.getChildCount() > position) {
                ArrayList<View> icons = new ArrayList<>();
                for (int i = 0; i < icons_left.getChildCount(); i++) {
                    icons.add(icons_left.getChildAt(i));
                }
                for (int i = 0; i < icons_right.getChildCount(); i++) {
                    icons.add(icons_right.getChildAt(i));
                }
                if (icons.get(position).getVisibility() == VISIBLE) {
                    icons.get(position).performClick();
                }
            }
        }
    }

    private void buildProgressBar() {
        inflate(getContext(), R.layout.progress_bar_fab, (ViewGroup) findViewById(R.id.coordinator));
        progressBar = (ProgressBar) coordinatorLayout.getChildAt(coordinatorLayout.getChildCount() - 1);
        progressBar.setX(fab.getX());
        progressBar.setY(fab.getY());

        progressBar.getLayoutParams().height = fab.getHeight();
        progressBar.getLayoutParams().width = fab.getWidth();
        progressBar.setMax(MAX_PB);
        progressBar.setProgressTintList(ColorStateList.valueOf(colorPrimary));

        if (this.defProgress != null) {
            setProgress(this.defProgress);
            this.defProgress = null;
        }
    }
    private void construct(final FABSettings fabSettings, int fabAlignment) {

        icons_all_left.setVisibility(GONE);
        icons_left.setVisibility(GONE);
        icons_right.setVisibility(GONE);

        setFabSettings(fabSettings, false);
        bottomAppBar.setFabAlignmentMode(fabAlignment);
        if (bottomAppBar.getFabAlignmentMode() != fabAlignment) {
            bottomAppBar.setFabAlignmentMode(fabAlignment);
        }
        for (AppCompatImageView anImages : images_all_left) {
            anImages.setVisibility(GONE);
        }
        for (AppCompatImageView anImages : images_left) {
            anImages.setVisibility(GONE);
        }
        for (AppCompatImageView anImages : images_right) {
            anImages.setVisibility(GONE);
        }
    }
    private void animateSnackBarFade(final Snackbar snackbar) {
        if (snackbar.isShown()) {
            Anim animDefault = Anim.animate(snackbar.getView()).translationY(0, Anim.TRANSLATION_END, SNACK_BAR_ANIM_DURATION, new AnticipateInterpolator(1f));
            Anim anim = snackBarAnimFadeBuilder(snackbar.getView(), animDefault);
            if (anim != null) {
                anim.setEndListener(new QeUtil.Do.WithIt<Anim>() {
                    @Override
                    public void doWithIt(Anim it) {
                        snackbar.dismiss();
                    }
                });
                anim.start();
            }
        }
    }
    private void animateSnackBarStart(final Snackbar snackbar) {
        Anim animDefault = Anim.animate(snackbar.getView()).translationY(Anim.TRANSLATION_END, 0, SNACK_BAR_ANIM_DURATION, new OvershootInterpolator(0.8f));
        Anim anim = snackBarAnimShowBuilder(snackbar.getView(), animDefault);
        if (anim != null) {
            anim.start();
        }
    }

    public static abstract class Construction {

        private Construction() {

        }

        public static final class FABEnd extends Construction {

            private FABSettings fabSettings;
            private IconSettings[] iconSettings;

            /** Конструкция BottomAppBar при котрой FloatingActionButton находится справа.
             * Слева находятся View.
             * @param fabSettings Изменения в FloatingActionButton
             * @param iconSettings Массив View (от 0 да 4) которые будут расположены слева.
             * У View будут установленны нужные размеры и отступы.*/
            public FABEnd(FABSettings fabSettings, @Size(min = 0, max = 4) IconSettings...iconSettings)
            {
                this.fabSettings = fabSettings;
                this.iconSettings = iconSettings;
            }
        }
        public static final class FABCenter extends Construction {

            private FABSettings fabSettings;
            private IconSettings[] iconSettings_left;
            private IconSettings[] iconSettings_right;

            /** Конструкция BottomAppBar при котрой FloatingActionButton находится в центре.
             * Слева и Справа нахлдсят View.
             * @param fabSettings Изменения в FloatingActionButton
             * @param iconSettings_left Массив View (от 0 да 3) которые будут расположены слева.
             * @param iconSettings_right Массив View (от 0 да 3) которые будут расположены справа.
             * У View будут установленны нужные размеры и отступы.*/
            public FABCenter(FABSettings fabSettings,
                             @Size(min = 0, max = 2) IconSettings[] iconSettings_left,
                             @Size(min = 0, max = 2) IconSettings[] iconSettings_right)
            {
                this.fabSettings = fabSettings;
                this.iconSettings_left = iconSettings_left;
                this.iconSettings_right = iconSettings_right;
            }
        }
    }
}
