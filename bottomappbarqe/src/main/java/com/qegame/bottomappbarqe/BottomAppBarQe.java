package com.qegame.bottomappbarqe;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
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
import com.qegame.animsimple.Durations;
import com.qegame.animsimple.anim.Anim;
import com.qegame.animsimple.anim.AnimView;
import com.qegame.animsimple.anim.MoveLeft;
import com.qegame.animsimple.anim.MoveRight;
import com.qegame.animsimple.path.Alpha;
import com.qegame.animsimple.path.ScaleX;
import com.qegame.animsimple.path.ScaleY;
import com.qegame.animsimple.path.TranslationY;
import com.qegame.animsimple.path.params.AnimParams;
import com.qegame.animsimple.path.params.OtherParams;
import com.qegame.animsimple.path.params.SimpleParams;
import com.qegame.animsimple.viewsanimations.ProgressBarAnimation;
import com.qegame.qeshaper.QeShaper;
import com.qegame.qeutil.androids.QeAndroid;
import com.qegame.qeutil.androids.QeViews;
import com.qegame.qeutil.doing.Do;
import com.qegame.qeutil.listening.listener.Listener;
import com.qegame.qeutil.listening.subscriber.Subscriber;

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
    private static final int MAX_PB = 360;
    private static final long SNACK_BAR_ANIM_DURATION = 500;
    private static final long DURATION_SNACK_DEFAULT = 2500;
    private static final long DURATION_ICONS = 800;

    public interface FABSettings {
        Drawable getImage();
        OnClickListener getClickListener();
        default Anim<FloatingActionButton> getAnimation(Anim<FloatingActionButton> animDefault) {
            return animDefault;
        }
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

    @NonNull
    private Construction construction;
    private Integer defProgress;
    private boolean progressBarShown;
    private Corner corners;
    private int radius;

    private Listener onProgressCompletely;

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

        setSnackBarCorners(Corner.ROUND, (int) QeAndroid.dp(context, 3));

        this.onProgressCompletely = new Listener();
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
                Anim<FloatingActionButton> anim_default = Anim.animate(fab);
                anim_default
                        .play(new ScaleX<>(new AnimParams.OfFloat<>(0f, 1f, 1000)))
                        .with(new ScaleY<>(new AnimParams.OfFloat<>(0f, 1f, 1000)));

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

    public Listener getOnProgressCompletely() {
        return onProgressCompletely;
    }

    //endregion

    @NonNull
    public FloatingActionButton getFab() {
        return fab;
    }
    @NonNull
    public BottomAppBar getBottomAppBar() {
        return bottomAppBar;
    }
    /** Сменить конструкцию */
    public void setConstruction(@NonNull Construction construction) {
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

            MoveLeft.animate(icons_all_left, new OtherParams.Smart(DURATION_ICONS, new OvershootInterpolator())).start();
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

            MoveLeft.animate(icons_left, new OtherParams.Smart(DURATION_ICONS, new OvershootInterpolator())).start();
            MoveRight.animate(icons_right, new OtherParams.Smart(DURATION_ICONS, new OvershootInterpolator())).start();
        }
    }
    /** Показать SnackBar */
    @NonNull
    public Snackbar showSnackBar(String text, long duration) {

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

        QeViews.doOnMeasureView(snackbar.getView(), new Do.With<View>() {
            @Override
            public void work(View with) {
                animateSnackBarStart(snackbar);
            }
        });

        return snackbar;
    }
    /** Показать SnackBar */
    @NonNull
    public Snackbar showSnackBar(String text) {
        return showSnackBar(text, DURATION_SNACK_DEFAULT);
    }
    /** Поменять тип углов у SnackBar */
    public void setSnackBarCorners(@NonNull Corner corners, int radius) {
        this.corners = corners;
        this.radius = radius;
    }
    /** Поменять тип углов у SnackBar */
    public void setSnackBarCorners(@NonNull Corner corners) {
        setSnackBarCorners(corners, this.radius);
        this.corners = corners;
    }
    /** Показать прогресс */
    public void showProgressBar() {
        if (this.progressBar != null) {
            refreshProgressBar(this.progressBar.getProgress());
        } else {
            if (fab.getWidth() == 0) {
                QeViews.doOnMeasureView(this.fab, new Do.With<FloatingActionButton>() {
                    @Override
                    public void work(FloatingActionButton with) {
                        buildProgressBar();
                    }
                });
            } else {
                buildProgressBar();
            }
        }
        this.progressBarShown = true;
    }
    /** Убрать прогресс */
    public void removeProgressBar() {
        if (this.progressBar != null) {
            Anim<ProgressBar> anim = new Anim<>(progressBar);
            anim
                    .play(new Alpha<>(new AnimParams.OfFloat<>(1f, 0f, Durations.DURATION_VERY_VERY_SHORT)))
                    .with(new ScaleX<>(new AnimParams.OfFloat<>(1f, 1.5f, Durations.DURATION_VERY_VERY_SHORT)))
                    .with(new ScaleX<>(new AnimParams.OfFloat<>(1f, 1.5f, Durations.DURATION_VERY_VERY_SHORT)))
                    .build();
            anim.getOnEnd().subscribe(new Subscriber.Twins<Anim<ProgressBar>, Animator>() {
                @Override
                public void onCall(Anim<ProgressBar> first, Animator second) {
                    if (progressBar != null) {
                        progressBar.setProgress(0);
                        coordinatorLayout.removeView(progressBar);
                        progressBar = null;
                        defProgress = null;
                    }
                }
            });
            anim.start();
        }
        this.progressBarShown = false;
    }
    /** Обновить прогресс */
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
    /** Изменить уровень прогресса */
    public void setProgress(@IntRange(from = 0, to = MAX_PB) int value) {
        if (progressBar != null) {
            ProgressBarAnimation anim = ProgressBarAnimation.animateProgress(progressBar, progressBar.getProgress(), value, 1000L);
            anim.start();

            if (value >= MAX_PB)
                anim.getOnEnd().subscribe(new Subscriber.Twins<Anim<ProgressBar>, Animator>() {
                    @Override
                    public void onCall(Anim<ProgressBar> first, Animator second) {
                        getOnProgressCompletely().call();
                    }
                });
        } else
            this.defProgress = value;

    }
    /** Добавить уровень прогресса */
    public void addProgress(int value) {
        if (this.progressBar != null) {
            setProgress(this.progressBar.getProgress() + value);
        }
    }
    /** Добавить процент прогресса */
    public void addProgressPercent(int percent) {
        if (this.progressBar != null) {
            int step = (int) ((MAX_PB / 100.0) * percent);
            setProgress(this.progressBar.getProgress() + step);
        }
    }
    /** Изменить процент прогресса */
    public void setProgressPercent(@FloatRange(from = 0.0, to = 100.0) float percent) {
        int step = (int) ((MAX_PB / 100.0) * percent);
        setProgress(step);
    }
    /** Получить текущий прогресс */
    public int getProgress() {
        if (this.progressBar != null) return this.progressBar.getProgress();
        return 0;
    }
    /** Получить текущий прогресс в процентах */
    public int getProgressPercent() {
        if (this.progressBar != null) return this.progressBar.getProgress() / this.progressBar.getMax() * 100;
        return 0;
    }
    /** Изменить цвет панели */
    public void setColorPanel(@ColorInt int color) {
        this.colorPrimary = color;
        this.bottomAppBar.setBackgroundTint(ColorStateList.valueOf(color));
        icons_all_left.setBackgroundColor(color);
        icons_left.setBackgroundColor(color);
        icons_right.setBackgroundColor(color);
    }
    /** Изменить цвет FAB */
    public void setFabColor(@ColorInt int color) {
        this.colorAccent = color;
        getFab().setBackgroundTintList(ColorStateList.valueOf(colorAccent));
    }
    /** Программный клик по иконке на панели
     * @param position Позиция иконки относительно левого края */
    public void performClickIcon(int position) {
        if (this.construction instanceof Construction.FABEnd) {
            if (icons_all_left.getChildCount() > position) 
                if (icons_all_left.getChildAt(position).getVisibility() == VISIBLE) 
                    icons_all_left.getChildAt(position).performClick();
                
        }
        if (this.construction instanceof Construction.FABCenter) {
            if (icons_left.getChildCount() + icons_right.getChildCount() > position) {
                ArrayList<View> icons = new ArrayList<>();
                for (int i = 0; i < icons_left.getChildCount(); i++) 
                    icons.add(icons_left.getChildAt(i));
                
                for (int i = 0; i < icons_right.getChildCount(); i++) 
                    icons.add(icons_right.getChildAt(i));
                
                if (icons.get(position).getVisibility() == VISIBLE) 
                    icons.get(position).performClick();
            }
        }
    }

    /** Построенеи View у SnackBar */
    protected View snackBarViewBuilder(@NonNull final Snackbar snackbar, @NonNull View view) {
        @ColorInt int color = 0xFF323232;
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
        button.setText("Ok"); // TODO: 11.08.2019 StringRes
        button.setTextColor(colorAccent);
        button.setVisibility(VISIBLE);

        Drawable background = QeShaper.buildDrawable(new CutCornerTreatment(dp(5)), new QeShaper.Corner[]{}, color, 0, colorPrimary);
        button.setBackground(background);

        button.setPadding(0, 0, 0, 0);

        LinearLayout.LayoutParams lp = (LayoutParams) button.getLayoutParams();
        lp.height = dp(36);
        lp.setMargins(dp(8), dp(6), dp(8), dp(6));

        if (this.corners == Corner.CUT) {
            view.setBackground(QeShaper.getCutCorners(dp(radius), color, colorAccent));
            button.setBackground(QeShaper.getCutCorners(dp(radius), color, colorPrimary));
        } else {
            view.setBackground(QeShaper.getRoundCorners(dp(radius), color, colorAccent));
            button.setBackground(QeShaper.getRoundCorners(dp(radius), color, colorPrimary));
        }

        return view;
    }
    /** Построенеи анимации появления SnackBar */
    protected AnimView<View> snackBarAnimShowBuilder(@NonNull View viewSnack, @NonNull AnimView<View> animDefault) {
        return animDefault;
    }
    /** Построенеи анимации ухода SnackBar */
    protected AnimView<View> snackBarAnimFadeBuilder(@NonNull View viewSnack, @NonNull AnimView<View> animDefault) {
        return animDefault;
    }

    /** Построение ProgressBar */
    private void buildProgressBar() {
        inflate(getContext(), R.layout.progress_bar_fab, findViewById(R.id.coordinator));
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
        if (bottomAppBar.getFabAlignmentMode() != fabAlignment)
            bottomAppBar.setFabAlignmentMode(fabAlignment);

        for (AppCompatImageView anImages : images_all_left)
            anImages.setVisibility(GONE);

        for (AppCompatImageView anImages : images_left)
            anImages.setVisibility(GONE);

        for (AppCompatImageView anImages : images_right)
            anImages.setVisibility(GONE);

    }
    private void animateSnackBarFade(final Snackbar snackbar) {
        if (snackbar.isShown()) {
            AnimView<View> animDefault = AnimView.animate(snackbar.getView());
            animDefault.playTogether(
                    TranslationY.animate(new AnimParams.OfFloat<>(0f, snackbar.getView().getHeight() * 2f, new OtherParams() {
                        @Override
                        public long getDuration() {
                            return SNACK_BAR_ANIM_DURATION;
                        }

                        @Override
                        public Interpolator getInterpolator() {
                            return new OvershootInterpolator(0.8f);
                        }
                    })),
                    Alpha.animate(new AnimParams.OfFloat<>(1f, 0f, SNACK_BAR_ANIM_DURATION))
            );

            animDefault = snackBarAnimFadeBuilder(snackbar.getView(), animDefault);
            if (animDefault != null) {
                animDefault.getOnEnd().subscribe(new Subscriber.Twins<Anim<View>, Animator>() {
                    @Override
                    public void onCall(Anim<View> first, Animator second) {
                        snackbar.dismiss();
                        snackbar.getView().setVisibility(GONE);
                    }
                });
                animDefault.start();
            }
        }
    }
    private void animateSnackBarStart(final Snackbar snackbar) {
        AnimView<View> animDefault = AnimView.animate(snackbar.getView());
        animDefault.playTogether(
                TranslationY.animate(new AnimParams.OfFloat<>(snackbar.getView().getHeight() * 2f, 0f, new OtherParams() {
                    @Override
                    public long getDuration() {
                        return SNACK_BAR_ANIM_DURATION;
                    }

                    @Override
                    public Interpolator getInterpolator() {
                        return new OvershootInterpolator(0.8f);
                    }
                })),
                Alpha.animate(new AnimParams.OfFloat<>(0f, 1f, SNACK_BAR_ANIM_DURATION))
        );

        animDefault = snackBarAnimShowBuilder(snackbar.getView(), animDefault);
        if (animDefault != null) animDefault.start();
    }

    private int dp(int dp) {
        return (int) QeAndroid.dp(getContext(), dp);
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
