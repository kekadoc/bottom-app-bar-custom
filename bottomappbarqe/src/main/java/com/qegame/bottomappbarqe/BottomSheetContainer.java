package com.qegame.bottomappbarqe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qegame.qeutil.androids.views.listeners.SwipeDetector;

/** Контейнер для реализации {@link BottomAppBarQe.Sheet} свайпами */
public class BottomSheetContainer extends FrameLayout {

    private SwipeDetector swipeDetector;

    {
        swipeDetector = new SwipeDetector();
        setClipChildren(false);
        setClipToPadding(false);
    }

    public BottomSheetContainer(@NonNull Context context) {
        super(context);
    }
    public BottomSheetContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public BottomSheetContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public BottomSheetContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof BottomAppBarQe) {
            this.swipeDetector.setOnSwipeListener(((BottomAppBarQe) child).sheet());
            setElevation(child.getElevation());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event) || swipeDetector.onTouch(this, event);
    }

}
