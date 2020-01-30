package com.qegame.bottomappbarqe;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qegame.qeutil.androids.views.QeViews;
import com.qegame.qeutil.doing.Do;

public class BottomSheetView extends FrameLayout {
    private static final String TAG = "BottomSheetView-TAG";

    @Nullable
    private Integer customHeight;

    private BottomAppBarQe.Sheet sheet;

    public BottomSheetView(@NonNull Context context) {
        super(context);
        init(context, null);
    }
    public BottomSheetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public BottomSheetView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    public BottomSheetView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

    }

    public void setSheet(BottomAppBarQe.Sheet sheet) {
        this.sheet = sheet;
    }

    @Nullable
    public Integer getCustomHeight() {
        return customHeight;
    }
    public void setCustomHeight(@Nullable Integer height) {
        if (height != null && height < -2) height = 0;
        this.customHeight = height;

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getCustomHeight() != null) {
            int width = resolveSize(getCustomHeight(), widthMeasureSpec);
            int height = resolveSize(getCustomHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);
        }
        QeViews.doOnMeasureView(this, new Do.With<BottomSheetView>() {
            @Override
            public void work(BottomSheetView with) {
                float backHeight = getHeight() * 0.5f;
                float tY = backHeight * 0.5f;

                QeViews.setSize(sheet.getBackSpace(), LayoutParams.MATCH_PARENT, (int) backHeight);
                sheet.getBackSpace().setTranslationY(tY);
            }
        });

    }

}
