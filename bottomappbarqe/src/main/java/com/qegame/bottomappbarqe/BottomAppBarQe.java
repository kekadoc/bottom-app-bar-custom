package com.qegame.bottomappbarqe;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.qegame.animsimple.anim.Anim;
import com.qegame.animsimple.anim.AnimView;
import com.qegame.animsimple.anim.MoveLeft;
import com.qegame.animsimple.anim.MoveRight;
import com.qegame.animsimple.anim.Scale;
import com.qegame.animsimple.interpolator.BounceInterpolator;
import com.qegame.animsimple.path.Alpha;
import com.qegame.animsimple.path.Path;
import com.qegame.animsimple.path.ScaleX;
import com.qegame.animsimple.path.ScaleY;
import com.qegame.animsimple.path.TranslationY;
import com.qegame.animsimple.path.params.AnimParams;
import com.qegame.animsimple.path.params.OtherParams;
import com.qegame.animsimple.viewsanimations.ProgressBarAnimation;
import com.qegame.qeshaper.QeShaper;
import com.qegame.qeutil.androids.QeAndroid;
import com.qegame.qeutil.androids.views.QeViews;
import com.qegame.qeutil.androids.views.listeners.OnSwipeListener;
import com.qegame.qeutil.doing.Do;
import com.qegame.qeutil.listening.listener.Listener;
import com.qegame.qeutil.listening.subscriber.Subscriber;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BottomAppBarQe extends FrameLayout {
    private static final String TAG = "BottomAppBarQe-TAG";

    /** Скорость анимации смены иконок */
    private static final long DURATION_ICONS = 800L;

    /** Найстройка FAB */
    public interface FABSettings {
        
        FABSettings EMPTY_SETTINGS = new FABSettings() {
            @Override
            public Drawable getImage() {
                return null;
            }

            @Override
            public OnClickListener getClickListener() {
                return null;
            }
        };
        
        static void runDefaultAnimation(FloatingActionButton fab) {
            Scale.animate(fab, new AnimParams.OfFloat<>(0f, 1f, 300L, new OvershootInterpolator())).start();
        }
        
        /** Изображение */
        Drawable getImage();
        /** Слушатель на нажатие */
        OnClickListener getClickListener();
        /** Анимация появления / изменения */
        default void createAnimation(FloatingActionButton fab) {
            runDefaultAnimation(fab);
        }

    }
    /** Настройка иконки. */
    public interface IconSettings {
        
        IconSettings EMPTY_ICON = new IconSettings() {
            @Override
            public Drawable getImage() {
                return null;
            }

            @Override
            public OnClickListener getClickListener() {
                return null;
            }
        };
        
        /** Изображение */
        Drawable getImage();
        /** Слушатель на нажатие */
        OnClickListener getClickListener();
    }

    private FloatingActionButton fab;
    private BottomAppBar bottomAppBar;

    private CoordinatorLayout coordinatorLayout;

    private LinearLayout icons_all_left;
    private AppCompatImageView[] images_all_left;
    private LinearLayout icons_left;
    private AppCompatImageView[] images_left;
    private LinearLayout icons_right;
    private AppCompatImageView[] images_right;


    /** Текщие настройки FAB */
    private FABSettings fabSettings;
    /** Primary цвет темы */
    @ColorInt private int colorPrimary;
    /** PrimaryDark цвет темы */
    @ColorInt private int colorPrimaryDark;
    /** Accent цвет темы */
    @ColorInt private int colorAccent;

    /** Цвет FAB */
    @ColorInt private int colorFAB;
    /** Цвет всей панели */
    @ColorInt private int colorPanel;

    /** Текущая конструкция */
    private Construction construction;
    /** Управление ProgressBar */
    private Progress progress;
    /** Управление SnackBar */
    private Snack snack;
    /** Нижняя панель */
    private Sheet sheet;

    private boolean lockZeroPosition = true;

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

        int[] colors = QeAndroid.getThemeColors(context, QeAndroid.ThemeColor.PRIMARY, QeAndroid.ThemeColor.PRIMART_DARK, QeAndroid.ThemeColor.ACCENT);

        this.colorPrimary = colors[0];
        this.colorPrimaryDark = colors[1];
        this.colorAccent = colors[2];

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BottomAppBarQe, 0, 0);
        float sheetHeight = ta.getDimension(R.styleable.BottomAppBarQe_sheetMaxHeight, -777);
        ta.recycle();

        this.fab = findViewById(R.id.fab);
        this.bottomAppBar = findViewById(R.id.bab);
        this.coordinatorLayout = findViewById(R.id.coordinator);
        this.icons_all_left = findViewById(R.id.icons_all_left);
        this.icons_left = findViewById(R.id.icons_left);
        this.icons_right = findViewById(R.id.icons_right);

        this.fab.setElevation(getResources().getDimension(R.dimen.elevation_fab));
        this.fab.setOnClickListener(new OnClickListener() {
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

        this.bottomAppBar.setElevation(getResources().getDimension(R.dimen.elevation_bar));


        this.colorFAB = this.colorAccent;
        this.colorPanel = this.colorPrimary;

        this.progress = new Progress(this);
        this.snack = new Snack(this);
        this.sheet = new Sheet(this);

        if (sheetHeight != -777) sheet().getView().setCustomHeight((int) sheetHeight);


        this.images_all_left = new AppCompatImageView[this.icons_all_left.getChildCount()];
        for (int i = 0; i < this.icons_all_left.getChildCount(); i++)
            this.images_all_left[i] = (AppCompatImageView) this.icons_all_left.getChildAt(i);

        this.images_left = new AppCompatImageView[this.icons_left.getChildCount()];
        for (int i = 0; i < this.icons_left.getChildCount(); i++)
            this.images_left[i] = (AppCompatImageView) this.icons_left.getChildAt(i);

        this.images_right = new AppCompatImageView[this.icons_right.getChildCount()];
        for (int i = 0; i < this.icons_right.getChildCount(); i++)
            this.images_right[i] = (AppCompatImageView) this.icons_right.getChildAt(i);


        setConstruction(new Construction.FABCenter(FABSettings.EMPTY_SETTINGS, null, null));

        setColorPanel(this.colorPanel);
        this.sheet.setColor(this.colorPrimaryDark);
        refreshFabColor();

        setElevation(getResources().getDimension(R.dimen.elevation_bar));
        setClipChildren(false);
        setClipToPadding(false);

    }

    //region Getters/Setters

    public final FABSettings getFabSettings() {
        return fabSettings;
    }
    public final void setFabSettings(@NonNull FABSettings fabSettings) {
        if (fabSettings == null) throw new RuntimeException("FabSettings is null!");
        setFabSettings(fabSettings, true);
    }

    @NonNull
    public final FloatingActionButton getFab() {
        return fab;
    }
    @NonNull
    public final BottomAppBar getBottomAppBar() {
        return bottomAppBar;
    }

    public final void setColorFAB(int colorFAB) {
        this.colorFAB = colorFAB;
    }

    public final void setProgress(@NonNull Progress progress) {
        this.progress = progress;
    }

    public final void setSnack(@NonNull Snack snack) {
        this.snack = snack;
    }

    public final void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    //endregion

    /** Управление SnackBar */
    @NonNull
    public final Snack snack() {
        return snack;
    }
    /** Управление ProgressView */
    @NonNull
    public final Progress progress() {
        return progress;
    }
    /**  */
    @NonNull
    public final Sheet sheet() {
        return sheet;
    }

    /** Сменить конструкцию */
    public final void setConstruction(@NonNull Construction construction) {
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

            MoveLeft.animate(icons_all_left, new OtherParams.Smart(getDurationIconsShow(), new OvershootInterpolator())).start();
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

            MoveLeft.animate(icons_left, new OtherParams.Smart(getDurationIconsShow(), new OvershootInterpolator())).start();
            MoveRight.animate(icons_right, new OtherParams.Smart(getDurationIconsShow(), new OvershootInterpolator())).start();
        }
    }

    /** Изменить цвет панели */
    public final void setColorPanel(@ColorInt int color) {
        this.colorPanel = color;
        this.bottomAppBar.setBackgroundTint(ColorStateList.valueOf(color));
        this.icons_all_left.setBackgroundColor(color);
        this.icons_left.setBackgroundColor(color);
        this.icons_right.setBackgroundColor(color);
    }
    /** Изменить цвет панели */
    public final void setColorPanel(@ColorInt int color, @ColorInt int sheet) {
        setColorPanel(color);
        this.sheet.setColor(sheet);
    }
    /** Изменить цвет FAB */
    public final void setFabColor(@ColorInt int color) {
        this.colorFAB = color;
        refreshFabColor();
    }

    /**
     *
     * Программный клик по иконке на панели
     * @param position Позиция иконки относительно левого края */
    public final void performClickIcon(int position) {
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


    /** Длительность анимации появления иконок */
    protected long getDurationIconsShow() {
        return DURATION_ICONS;
    }
    /**  */
    protected int dp(int dp) {
        return (int) QeAndroid.dp(getContext(), dp);
    }

    /**  */
    private void setFabSettings(@NonNull FABSettings fabSettings, boolean animate) {
        this.fabSettings = fabSettings;

        getFab().setImageDrawable(fabSettings.getImage());

        if (animate) fabSettings.createAnimation(getFab());
        

        if (fabSettings.getClickListener() == null) {
            getFab().setEnabled(false);
            getFab().setClickable(false);
        } else {
            getFab().setEnabled(true);
            getFab().setClickable(true);
        }

        this.progress.remove();
    }
    /**  */
    private void construct(final FABSettings fabSettings, int fabAlignment) {
        icons_all_left.setVisibility(GONE);
        icons_left.setVisibility(GONE);
        icons_right.setVisibility(GONE);

        setFabSettings(fabSettings, false);

        if (bottomAppBar.getFabAlignmentMode() != fabAlignment)
            bottomAppBar.setFabAlignmentMode(fabAlignment);

        for (AppCompatImageView anImages : images_all_left)
            anImages.setVisibility(GONE);

        for (AppCompatImageView anImages : images_left)
            anImages.setVisibility(GONE);

        for (AppCompatImageView anImages : images_right)
            anImages.setVisibility(GONE);

    }
    /** Обновить цвет FAB */
    private void refreshFabColor() {
        getFab().setBackgroundTintList(ColorStateList.valueOf(colorFAB));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        QeViews.doOnMeasureView(sheet().getView(), new Do.With<BottomSheetView>() {
            @Override
            public void work(BottomSheetView with) {
                if (!sheet().isExpanded()) {
                    float h = sheet().getHeight();
                    setTranslationY(h);
                }
            }
        });
    }

    @Override
    public void setTranslationY(float translationY) {
        if (translationY == 0 && lockZeroPosition) return;
        super.setTranslationY(translationY);
        sheet.onSetTranslationY(translationY);
    }

    /** Управление нижней панелью */
    public static class Sheet implements OnSwipeListener {

        enum Mode {
            EXPANDED,
            COLLAPSE,
            UNCERTAINTY;
        }

        private Mode activeMode = Mode.COLLAPSE;
        private Mode previewMove = Mode.COLLAPSE;

        private boolean touched;

        private View backSpace;
        private FrameLayout bottomContainer;
        private BottomSheetView bottomSheetView;

        private BottomAppBarQe bottomAppBarQe;

        public Sheet(BottomAppBarQe bottomAppBarQe) {
            this.bottomAppBarQe = bottomAppBarQe;
            this.backSpace = bottomAppBarQe.findViewById(R.id.backSpace);
            this.bottomContainer = bottomAppBarQe.findViewById(R.id.bottomContainer);
            this.bottomSheetView = bottomAppBarQe.findViewById(R.id.sheet);
            this.bottomSheetView.setSheet(this);
        }

        public void setActiveMode(Mode activeMode, boolean animate) {
            this.previewMove = this.activeMode;
            this.activeMode = activeMode;

            if (isExpanded()) {
                onExpand();
                if (animate) animateExpand();
            }
            if (isCollapse()) {
                onCollapse();
                if (animate) animateCollapse();
            }
        }

        public void show() {
            setPosition(Mode.EXPANDED, true);
        }
        public void hide() {
            setPosition(Mode.COLLAPSE, true);
        }
        public void swich() {
            if (isExpanded()) hide();
            else show();
        }

        public final boolean isExpanded() {
            return activeMode == Mode.EXPANDED;
        }
        public final boolean isCollapse() {
            return activeMode == Mode.COLLAPSE;
        }
        public final boolean isUncertainty() {
            return activeMode == Mode.UNCERTAINTY;
        }

        @NonNull
        public final BottomSheetView getView() {
            return bottomSheetView;
        }

        public final void collapse(boolean animate) {
            if (animate) animateCollapse(); else instantCollapse();
        }
        public final void expand(boolean animate) {
            if (animate) animateExpand(); else instantExpand();
        }

        public final void autoCorrectPosition(boolean animate) {
            float y = bottomAppBarQe.getTranslationY();
            float h = getView().getHeight();

            if (activeMode == Mode.EXPANDED || previewMove == Mode.EXPANDED) {
                if (y > h * 0.2) collapse(animate);
                else expand(animate);
            }
            if (activeMode == Mode.COLLAPSE || previewMove == Mode.COLLAPSE) {
                if (y < h * 0.9) expand(animate);
                else collapse(animate);
            }


        }

        public final void setPosition(Mode mode, boolean animate) {
            if (mode == activeMode) return;

            setActiveMode(mode, animate);

        }

        public final void setHeight(@Nullable Integer height) {
            getView().setCustomHeight(height);
        }
        public final int getHeight() {
            return getView().getHeight();
        }

        @CallSuper
        public void setColor(@ColorInt int color) {
            bottomSheetView.setBackgroundColor(color);
            backSpace.setBackgroundColor(color);
            bottomContainer.setBackgroundColor(color);
        }

        public View getBackSpace() {
            return backSpace;
        }

        @CallSuper
        protected void onSetTranslationY(float translationY) {
            if (translationY > getMaxUp() && translationY < getMaxDown())
                setPosition(Mode.UNCERTAINTY, false);

            if (translationY <= getMaxUp() && !isCollapse()) {
                setPosition(Mode.EXPANDED, false);
            }
            if (translationY >= getMaxDown() && !isExpanded()) {
                setPosition(Mode.COLLAPSE, false);
            }

            //correctPosWithMod();
        }

        protected void onExpand() {

        }
        protected void onCollapse() {

        }

        private void animateCollapse() {
            TranslationY.builder(bottomAppBarQe)
                    .duration(600L)
                    .interpolator(new BounceInterpolator(1))
                    .from(bottomAppBarQe.getTranslationY())
                    .to(getMaxDown())
                    .onStart(new Subscriber.Twins<Path<BottomAppBarQe, Float>, Animator>() {
                        @Override
                        public void onCall(Path<BottomAppBarQe, Float> first, Animator second) {
                            bottomAppBarQe.lockZeroPosition = false;
                        }
                    })
                    .onEnd(new Subscriber.Twins<Path<BottomAppBarQe, Float>, Animator>() {
                        @Override
                        public void onCall(Path<BottomAppBarQe, Float> first, Animator second) {
                            bottomAppBarQe.lockZeroPosition = true;
                        }
                    })
                    .build().start();
        }
        private void instantCollapse() {
            bottomAppBarQe.lockZeroPosition = false;
            bottomAppBarQe.setTranslationY(getMaxDown());
            bottomAppBarQe.lockZeroPosition = true;
        }
        private void animateExpand() {
            TranslationY.builder(bottomAppBarQe)
                    .duration(600L)
                    .interpolator(new OvershootInterpolator())
                    .from(bottomAppBarQe.getTranslationY())
                    .to( - getMaxUp())
                    .onStart(new Subscriber.Twins<Path<BottomAppBarQe, Float>, Animator>() {
                        @Override
                        public void onCall(Path<BottomAppBarQe, Float> first, Animator second) {
                            bottomAppBarQe.lockZeroPosition = false;
                        }
                    })
                    .onEnd(new Subscriber.Twins<Path<BottomAppBarQe, Float>, Animator>() {
                        @Override
                        public void onCall(Path<BottomAppBarQe, Float> first, Animator second) {
                            bottomAppBarQe.lockZeroPosition = true;
                        }
                    })
                    .build().start();
        }
        private void instantExpand() {
            bottomAppBarQe.lockZeroPosition = false;
            bottomAppBarQe.setTranslationY(getMaxUp());
            bottomAppBarQe.lockZeroPosition = true;
        }

        private float getMaxUp() {
            return 0;
        }
        private float getMaxDown() {
            return bottomSheetView.getHeight();
        }

        private void changePosition(float change) {
            float trans = bottomAppBarQe.getTranslationY() + change;

            if (trans > getMaxDown())  {
                animateCollapse();
                return;
            }
            if (trans < 0) {
                animateExpand();
                return;
            }
            bottomAppBarQe.setTranslationY(trans);
        }

        @Override
        public void onUp(float x, float y, float dxStart, float dyStart) {
            this.touched = false;
            autoCorrectPosition(true);
            bottomAppBarQe.lockZeroPosition = true;
        }
        @Override
        public void onDown(float x, float y) {
            this.touched = true;
            bottomAppBarQe.lockZeroPosition = false;
        }
        @Override
        public void onMove(float dxStart, float dyStart, float dx, float dy) {

        }
        @Override
        public void onSwipeLeft(float startDistance, float distance) {

        }
        @Override
        public void onSwipeTop(float startDistance, float distance) {
            float ch = (float) (Math.pow(distance, 2) * -0.009f);
            changePosition(ch);
        }
        @Override
        public void onSwipeRight(float startDistance, float distance) {

        }
        @Override
        public void onSwipeBottom(float startDistance, float distance) {
            changePosition(distance);
        }

    }
    /**  */
    public static class Progress {

        /** Максимальное значение прогресса у ProgressBar */
        private static final int MAX_PB = 360;
        /** Продолжительность анимации изменения прогресса */
        private static final long DURATION_PROGRESS_ANIMATION = 1000L;
        /** Продолжительность анимации изменения прогресса */
        private static final long DURATION_PROGRESS_SHOW = 300L;
        /** Продолжительность анимации изменения прогресса */
        private static final long DURATION_PROGRESS_LEAVE = 300L;

        /** Цвет прогресса */
        @ColorInt
        private int colorProgressBar;
        /** Прогрес создан */
        private boolean shown;
        /** Слушатель на выполнение прогресса */
        @NonNull
        private Listener onProgressCompletely;
        /** Экземпляр View */
        @Nullable
        private ProgressBar progressBar;

        @NonNull
        BottomAppBarQe bottomAppBarQe;

        public Progress(@NonNull BottomAppBarQe bottomAppBarQe) {
            this.bottomAppBarQe = bottomAppBarQe;
            this.colorProgressBar = bottomAppBarQe.colorPrimary;
            this.onProgressCompletely = new Listener();
        }

        public final boolean isShown() {
            return shown;
        }
        @NonNull
        public final Listener onCompletely() {
            return onProgressCompletely;
        }

        /** Показать прогресс */
        public final void show() {
            show(0, false);
        }
        /** Показать прогресс */
        public final void show(int progressPercent) {
            show(progressPercent, false);
        }
        /** Показать прогресс */
        public final void show(boolean animation) {
            show(0, animation);
        }
        /** Показать прогресс */
        public final void show(int progressPercent, boolean animation) {
            //Если он уже существует то просто обновляется
            if (this.progressBar != null) {
                refresh(this.progressBar.getProgress());
            } else {
                //Если ещё не отрисован
                if (bottomAppBarQe.fab.getWidth() == 0) {
                    QeViews.doOnMeasureView(bottomAppBarQe.fab, new Do.With<FloatingActionButton>() {
                        @Override
                        public void work(FloatingActionButton with) {
                            buildProgressBar((int) (progressPercent * getOnePercentValue()), animation);
                        }
                    });
                } else {
                    buildProgressBar((int) (progressPercent * getOnePercentValue()), animation);
                }
                
                this.shown = true;
            }
        }
        
        /** Убрать прогресс */
        public final void remove() {
            remove(true);
        }
        /**  */
        public final void remove(boolean animation) {
            if (this.progressBar != null) {
                if (animation) runLeaveAnimation();
            }
            this.shown = false;
        }
        
        /** Обновить прогресс */
        public final void refresh(int progress) {
            if (progressBar != null) {
                progressBar.setX(bottomAppBarQe.fab.getX());
                progressBar.setY(bottomAppBarQe.fab.getY());
                progressBar.getLayoutParams().height = bottomAppBarQe.fab.getHeight();
                progressBar.getLayoutParams().width = bottomAppBarQe.fab.getWidth();
                progressBar.setMax(getProgressMaxValue());
                progressBar.setProgress(progress);
            }
        }

        /** Добавить процент прогресса */
        public final void add(@FloatRange(from = 0.0, to = 100.0) float percent) {
            if (this.progressBar != null) {
                int step = (int) ((getProgressMaxValue() / 100.0) * percent);
                set(this.progressBar.getProgress() + step);
            }
        }
        /** Изменить процент прогресса */
        public final void set(@FloatRange(from = 0.0, to = 100.0) float percent) {
            int step = (int) ((getProgressMaxValue() / 100.0) * percent);
            set(step);
        }
        /** Получить текущий прогресс */
        @FloatRange(from = 0.0, to = 100.0)
        public final float getValue() {
            if (this.progressBar != null) return this.progressBar.getProgress() / getOnePercentValue();
            return 0f;
        }
        /** Получить текущий прогресс */
        @IntRange(from = 0, to = MAX_PB)
        public final int getRealValue() {
            if (this.progressBar != null) return this.progressBar.getProgress();
            return 0;
        }
        
        /** Изменить цвет прогресса */
        public final void setColor(int color) {
            this.colorProgressBar = color;
            refreshColor();
        }

        /** Максимальное значение ProgressBar */
        protected int getProgressMaxValue() {
            return MAX_PB;
        }
        /** 1 процент от максимального значения */
        protected float getOnePercentValue() {
            return (getProgressMaxValue() / 100.0f);
        }

        @Nullable
        protected void runShowAnimation() {
            Anim.animate(progressBar)
                    .play(new Alpha<>(new AnimParams.OfFloat<>(0f, 1f, getDurationProgressShow())))
                    .with(new ScaleX<>(new AnimParams.OfFloat<>(1.5f, 1f, getDurationProgressShow())))
                    .with(new ScaleY<>(new AnimParams.OfFloat<>(1.5f, 1f, getDurationProgressShow())))
                    .build().start();
        }
        @Nullable
        protected void runLeaveAnimation() {
            Anim.animate(progressBar)
                    .play(new Alpha<>(new AnimParams.OfFloat<>(1f, 0f, getDurationProgressLeave())))
                    .with(new ScaleX<>(new AnimParams.OfFloat<>(1f, 1.5f, getDurationProgressLeave())))
                    .with(new ScaleY<>(new AnimParams.OfFloat<>(1f, 1.5f, getDurationProgressLeave())))
                    .build()
                    .addEndSub(new Subscriber.Twins<Anim<ProgressBar>, Animator>() {
                        @Override
                        public void onCall(Anim<ProgressBar> first, Animator second) {
                            finallyRemoveProgressBar();
                        }
                    })
                    .start();
        }

        /** Длительность анимации прогресса */
        protected long getDurationProgressAnimation() {
            return DURATION_PROGRESS_ANIMATION;
        }
        /** Длительность анимации ухода ProgressBar */
        protected long getDurationProgressLeave() {
            return DURATION_PROGRESS_LEAVE;
        }
        /** Длительность анимации появления ProgressBar */
        protected long getDurationProgressShow() {
            return DURATION_PROGRESS_SHOW;
        }

        /** Изменить уровень прогресса */
        private void set(@IntRange(from = 0, to = MAX_PB) int value) {
            if (progressBar != null) {
                ProgressBarAnimation anim = ProgressBarAnimation.animateProgress(progressBar, progressBar.getProgress(), value, getDurationProgressAnimation());
                anim.start();

                if (value >= getProgressMaxValue())
                    anim.getOnEnd().subscribe(new Subscriber.Twins<Anim<ProgressBar>, Animator>() {
                        @Override
                        public void onCall(Anim<ProgressBar> first, Animator second) {
                            onProgressCompletely.call();
                        }
                    });
            }
        }
        /** Построение ProgressBar */
        private void buildProgressBar(@IntRange(from = 0, to = MAX_PB) int progress, boolean animation) {
            inflate(bottomAppBarQe.getContext(), R.layout.progress_bar_fab, bottomAppBarQe.findViewById(R.id.coordinator));
            progressBar = (ProgressBar) bottomAppBarQe.coordinatorLayout.getChildAt(bottomAppBarQe.coordinatorLayout.getChildCount() - 1);
            progressBar.setX(bottomAppBarQe.fab.getX());
            progressBar.setY(bottomAppBarQe.fab.getY());

            progressBar.getLayoutParams().height = bottomAppBarQe.fab.getHeight();
            progressBar.getLayoutParams().width = bottomAppBarQe.fab.getWidth();
            progressBar.setMax(getProgressMaxValue());
            progressBar.setProgress(progress);

            refreshColor();
            
            if (animation) runShowAnimation();


        }
        /**  */
        private void finallyRemoveProgressBar() {
            if (progressBar != null) {
                progressBar.setProgress(0);
                bottomAppBarQe.coordinatorLayout.removeView(progressBar);
                progressBar = null;
            }
        }
        /** Обновление цвета. */
        private void refreshColor() {
            if (progressBar != null)
                progressBar.getProgressDrawable().setColorFilter(colorProgressBar, PorterDuff.Mode.SRC_IN);
        }

    }
    /**  */
    public static class Snack {

        /** Типы углов */
        public enum Corner {
            /** Обрезанные */
            CUT,
            /** Закругленные */
            ROUND
        }

        private interface Settings {
            /** Углы */
            Corner getCorners();
            /** Радиус углов */
            int getRadius();

            @ColorInt
            int getColorBody();
            @ColorInt
            int getColorText();
            @ColorInt
            int getColorButtonText();
            @ColorInt
            int getColorButtonBody();
            @ColorInt
            int getColorButtonRipple();

            String getText();
            String getButtonText();
            @Nullable
            Do getOnButtonClick();

            long getDuration();
        }

        /** Время анимации появления/ ухода SnackBar */
        private static final long DURATION_SNACK_SHOW = 400L;
        /** Время анимации появления/ ухода SnackBar */
        private static final long DURATION_SNACK_LEAVE = 600L;
        /** Продолжительность видимости */
        private static final long DURATION_SNACK_DEFAULT = 2500L;

        @NonNull
        private BottomAppBarQe bottomAppBarQe;
        /** Углы */
        private Corner corners;
        /** Радиус углов */
        private int radius;

        @ColorInt
        private int colorBody = 0xFF323232;
        @ColorInt
        private int colorText = 0xFFFFFFFF;
        @ColorInt
        private int colorButtonText;
        @ColorInt
        private int colorButtonBody;
        @ColorInt
        private int colorButtonRipple;

        private String buttonText;

        public Snack(@NonNull BottomAppBarQe bottomAppBarQe) {
            this.bottomAppBarQe = bottomAppBarQe;

            this.colorButtonText = bottomAppBarQe.colorAccent;
            this.colorButtonBody = colorBody;
            this.colorButtonRipple = bottomAppBarQe.colorPrimary;

            this.buttonText = bottomAppBarQe.getResources().getString(R.string.snack_ok);

            setCorners(Corner.ROUND, dp(3));
        }

        //region Getters/Setters

        public final void setColorBody(int colorBody) {
            this.colorBody = colorBody;
        }
        public final void setColorText(int colorText) {
            this.colorText = colorText;
        }
        public final void setColorButtonText(int colorButtonText) {
            this.colorButtonText = colorButtonText;
        }
        public final void setColorButtonBody(int colorButtonBody) {
            this.colorButtonBody = colorButtonBody;
        }
        public final void setColorButtonRipple(int colorButtonRipple) {
            this.colorButtonRipple = colorButtonRipple;
        }
        public final void setButtonText(String buttonText) {
            this.buttonText = buttonText;
        }

        //endregion

        @NonNull
        public final Builder make(String text) {
            return new Builder(text);
        }

        /** Показать SnackBar */
        @NonNull
        public final Snackbar show(String text) {
            return show(text, DURATION_SNACK_DEFAULT);
        }
        /** Показать SnackBar */
        @NonNull
        public final Snackbar show(String text, long duration) {
            return show(text, duration, null);
        }

        /** Поменять тип углов у SnackBar */
        public final void setCorners(@NonNull Corner corners, int radius) {
            this.corners = corners;
            this.radius = radius;
        }
        /** Поменять тип углов у SnackBar */
        public final void setCorners(@NonNull Corner corners) {
            setCorners(corners, this.radius);
        }

        /** Скорость стандартной анимации появления */
        protected long getDurationSnackShow() {
            return DURATION_SNACK_SHOW;
        }
        /** Скорость стандартной анимации ухода */
        protected long getDurationSnackLeave() {
            return DURATION_SNACK_LEAVE;
        }

        /** Построенеи View у SnackBar */
        private View snackBarViewBuilder(@NonNull final Snackbar snackbar, @NonNull View view, @Nullable Settings settings) {
            int marginSide = (int) bottomAppBarQe.getResources().getDimension(R.dimen.margin_side_snackbar);
            view.setElevation(bottomAppBarQe.getResources().getDimension(R.dimen.elevation_snack));
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            params.setAnchorId(R.id.coordinator);
            params.anchorGravity = Gravity.TOP;
            params.gravity = Gravity.TOP;
            params.setMargins(marginSide, 0, marginSide, 0);
            view.setLayoutParams(params);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) view;

            SnackbarContentLayout view_snack = (SnackbarContentLayout) snackbarLayout.getChildAt(0);
            view_snack.setPadding(0, 0, dp(6), 0);

            Button button = (Button) view_snack.getChildAt(1);
            button.setVisibility(VISIBLE);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (settings != null && settings.getOnButtonClick() != null) settings.getOnButtonClick().work();
                    animateSnackBarLeave(snackbar);
                }
            });
            button.setPadding(0, 0, 0, 0);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) button.getLayoutParams();
            lp.height = dp(36);
            lp.setMargins(dp(8), dp(6), dp(8), dp(6));
            button.setLayoutParams(lp);

            TextView textView = (TextView) view_snack.getChildAt(0);
            if (settings == null) {
                button.setText(buttonText);
                button.setTextColor(bottomAppBarQe.colorAccent);
                textView.setTextColor(colorText);

                if (this.corners == Corner.CUT) {
                    view.setBackground(QeShaper.builder().allCorner(new CutCornerTreatment(dp(radius))).color(colorBody).build());
                    button.setBackground(QeShaper.builder().allCorner(new CutCornerTreatment(dp(radius) * 0.66f)).color(colorButtonBody).rippleColor(colorButtonRipple).build());
                } else {
                    view.setBackground(QeShaper.builder().allCorner(new RoundedCornerTreatment(dp(radius))).color(colorBody).build());
                    button.setBackground(QeShaper.builder().allCorner(new RoundedCornerTreatment(dp(radius) * 0.66f)).color(colorButtonBody).rippleColor(colorButtonRipple).build());
                }
            } else {
                button.setText(settings.getButtonText());
                button.setTextColor(settings.getColorButtonText());
                textView.setTextColor(settings.getColorText());

                if (settings.getCorners() == Corner.CUT) {
                    view.setBackground(QeShaper.builder().allCorner(new CutCornerTreatment(dp(settings.getRadius()))).color(settings.getColorBody()).build());
                    button.setBackground(QeShaper.builder().allCorner(new CutCornerTreatment(dp(settings.getRadius()) * 0.66f)).color(settings.getColorButtonBody()).rippleColor(settings.getColorButtonRipple()).build());
                } else {
                    view.setBackground(QeShaper.builder().allCorner(new RoundedCornerTreatment(dp(settings.getRadius()))).color(settings.getColorBody()).build());
                    button.setBackground(QeShaper.builder().allCorner(new RoundedCornerTreatment(dp(settings.getRadius()) * 0.66f)).color(settings.getColorButtonBody()).rippleColor(settings.getColorButtonRipple()).build());
                }
            }

            return view;
        }
        /** Показать SnackBar */
        @NonNull
        private Snackbar show(String text, long duration, @Nullable Settings settings) {

            final Snackbar snackbar = Snackbar.make(bottomAppBarQe, settings != null ? settings.getText() : text, Snackbar.LENGTH_INDEFINITE);
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    ((Activity) bottomAppBarQe.getContext()).runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            animateSnackBarLeave(snackbar);

                        }
                    });
                }
            };
            timer.schedule(timerTask, settings == null ? duration : settings.getDuration());
            snackBarViewBuilder(snackbar, snackbar.getView(), settings);
            snackbar.show();
            QeViews.doOnMeasureView(snackbar.getView(), new Do.With<View>() {
                @Override
                public void work(View with) {
                    animateSnackBarShow(snackbar);
                }
            });

            return snackbar;
        }

        /** Вызов анимации ухода */
        private void animateSnackBarLeave(final Snackbar snackbar) {
            if (snackbar.isShown()) {
                AnimView<View> anim = AnimView.animate(snackbar.getView());
                anim.playTogether(
                        TranslationY.animate(new AnimParams.OfFloat<>(0f, snackbar.getView().getHeight() * 3f, new OtherParams() {
                            @Override
                            public long getDuration() {
                                return getDurationSnackLeave();
                            }

                            @Override
                            public Interpolator getInterpolator() {
                                return new OvershootInterpolator(0.8f);
                            }
                        })),
                        Alpha.animate(new AnimParams.OfFloat<>(1f, 0f, getDurationSnackLeave()))
                );
                anim.addEndSub(new Subscriber.Twins<Anim<View>, Animator>() {
                    @Override
                    public void onCall(Anim<View> first, Animator second) {
                        snackbar.dismiss();
                        snackbar.getView().setVisibility(GONE);
                    }
                });
                anim.start();
            }
        }
        /** Вызов анимации появления */
        private void animateSnackBarShow(final Snackbar snackbar) {
            AnimView<View> animDefault = AnimView.animate(snackbar.getView());
            animDefault.playTogether(
                    TranslationY.animate(new AnimParams.OfFloat<>(snackbar.getView().getHeight() * 3f, 0f, new OtherParams() {
                        @Override
                        public long getDuration() {
                            return getDurationSnackShow();
                        }

                        @Override
                        public Interpolator getInterpolator() {
                            return new OvershootInterpolator(1f);
                        }
                    })),
                    Alpha.animate(new AnimParams.OfFloat<>(0f, 1f, getDurationSnackShow() / 2))
            );
            animDefault.start();
        }
        /**  */
        private int dp(int val) {
            return bottomAppBarQe.dp(val);
        }


        public final class Builder {

            private Corner corners;
            private int radius;
            @ColorInt
            private int colorBody;
            @ColorInt
            private int colorText;
            @ColorInt
            private int colorButtonText;
            @ColorInt
            private int colorButtonBody;
            @ColorInt
            private int colorButtonRipple;
            private String text;
            private String buttonText;
            @Nullable
            private Do onButtonClick;

            private long duration;

            public Builder(String text) {
                this.corners = Corner.ROUND;
                this.radius = Snack.this.radius;

                this.text = text;

                this.colorBody = Snack.this.colorBody;
                this.colorText = Snack.this.colorText;
                this.colorButtonText = Snack.this.colorButtonText;
                this.colorButtonBody = Snack.this.colorButtonBody;
                this.colorButtonRipple = Snack.this.colorButtonRipple;

                this.buttonText = Snack.this.buttonText;

                this.onButtonClick = null;

                this.duration = Snack.DURATION_SNACK_DEFAULT;
            }
            public Builder() {
                this(null);
            }

            public Snackbar show() {
                return Snack.this.show(text, duration, new Settings() {
                    @Override
                    public Corner getCorners() {
                        return corners;
                    }

                    @Override
                    public int getRadius() {
                        return radius;
                    }

                    @Override
                    public int getColorBody() {
                        return colorBody;
                    }

                    @Override
                    public int getColorText() {
                        return colorText;
                    }

                    @Override
                    public int getColorButtonText() {
                        return colorButtonText;
                    }

                    @Override
                    public int getColorButtonBody() {
                        return colorButtonBody;
                    }

                    @Override
                    public int getColorButtonRipple() {
                        return colorButtonRipple;
                    }

                    @Override
                    public String getText() {
                        return text;
                    }

                    @Override
                    public String getButtonText() {
                        return buttonText;
                    }

                    @Nullable
                    @Override
                    public Do getOnButtonClick() {
                        return onButtonClick;
                    }

                    @Override
                    public long getDuration() {
                        return duration;
                    }
                });
            }

            public Builder corners(@NonNull Corner corners) {
                this.corners = corners;
                return this;
            }
            public Builder radius(int radius) {
                this.radius = radius;
                return this;
            }

            public Builder colorBody(@ColorInt int colorBody) {
                this.colorBody = colorBody;
                return this;
            }
            public Builder colorText(@ColorInt int colorText) {
                this.colorText = colorText;
                return this;
            }
            public Builder buttonColorText(@ColorInt int colorButtonText) {
                this.colorButtonText = colorButtonText;
                return this;
            }
            public Builder buttonColorBody(@ColorInt int colorButtonBody) {
                this.colorButtonBody = colorButtonBody;
                return this;
            }
            public Builder buttonColorRipple(@ColorInt int colorButtonRipple) {
                this.colorButtonRipple = colorButtonRipple;
                return this;
            }

            public Builder text(String text) {
                this.text = text;
                return this;
            }
            public Builder buttonText(String text) {
                this.buttonText = text;
                return this;
            }

            public Builder onButtonClick(Do onButtonClick) {
                this.onButtonClick = onButtonClick;
                return this;
            }

            public Builder duration(long duration) {
                this.duration = duration;
                return this;
            }

        }
    }
    /** Конструкция */
    public static abstract class Construction {

        private Construction() {

        }

        /** Fab End */
        public static final class FABEnd extends Construction {

            private FABSettings fabSettings;
            private IconSettings[] iconSettings;

            /** Конструкция BottomAppBar при котрой FloatingActionButton находится справа.
             * Слева находятся View.
             * @param fabSettings Изменения в FloatingActionButton
             * @param iconSettings Массив View (от 0 да 4) которые будут расположены слева.
             * У View будут установленны нужные размеры и отступы.*/
            public FABEnd(FABSettings fabSettings, @Size(min = 0, max = 4) IconSettings...iconSettings) {
                this.fabSettings = fabSettings;
                this.iconSettings = iconSettings;
            }
        }

        /** Fab Center */
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
