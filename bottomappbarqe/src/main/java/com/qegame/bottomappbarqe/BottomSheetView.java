package com.qegame.bottomappbarqe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qegame.qeutil.androids.QeAndroid;
import com.qegame.qeutil.androids.views.QeViews;

public class BottomSheetView extends FrameLayout {
    private static final String TAG = "BottomSheetView-TAG";

    public static final int DEFAULT_SHEET_HEIGHT_DP = 200;

    private int height = DEFAULT_SHEET_HEIGHT_DP;

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
        this.height = QeAndroid.dpToPx(context, DEFAULT_SHEET_HEIGHT_DP);
    }

    public void setSheet(BottomAppBarQe.Sheet sheet) {
        this.sheet = sheet;
    }

    public int getSheetHeight() {
        return height;
    }
    public void setSheetHeight(int height) {
        if (height < -2) height = 0;
        this.height = height;
        float backHeight = height * 0.5f;
        float tY = backHeight * 0.5f;
        QeViews.setSize(sheet.getBackSpace(), LayoutParams.MATCH_PARENT, (int) backHeight);
        sheet.getBackSpace().setTranslationY(tY);
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = resolveSize(getSheetHeight(), widthMeasureSpec);
        int height = resolveSize(getSheetHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

}
