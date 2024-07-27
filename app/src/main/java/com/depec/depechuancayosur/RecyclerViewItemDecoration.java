package com.depec.depechuancayosur;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing;
    private int borderWidth;
    private int borderColor;

    public RecyclerViewItemDecoration(int spacing, int borderWidth, int borderColor) {
        this.spacing = spacing;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(spacing, spacing, spacing, spacing);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.STROKE);


        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            // Dibujar el fondo rojo con borde redondeado dentro del marco
            RectF backgroundRect = new RectF(
                    child.getLeft() + borderWidth,
                    child.getTop() + borderWidth,
                    child.getRight() - borderWidth,
                    child.getBottom() - borderWidth);
            float cornerRadius = 20f; // Radio de los bordes redondeados
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GRAY);
            c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, paint);

            // Dibujar el marco con borde redondeado
            paint.setStyle(Paint.Style.STROKE);
            c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, paint);
        }
    }
}
