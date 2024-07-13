package com.kprandro.whiteboard;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private boolean isEraserActive = false;
    private String currentShape = "freehand"; // Default shape is freehand
    private float startX, startY, endX, endY;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK); // Default brush color
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10); // Default brush size
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        // Draw current shape
        if (currentShape.equals("rectangle")) {
            canvas.drawRect(startX, startY, endX, endY, drawPaint);
        } else if (currentShape.equals("circle")) {
            float radius = Math.min(Math.abs(endX - startX), Math.abs(endY - startY)) / 2;
            canvas.drawCircle(startX + (endX - startX) / 2, startY + (endY - startY) / 2, radius, drawPaint);
        } else if (currentShape.equals("line")) {
            canvas.drawLine(startX, startY, endX, endY, drawPaint);
        } else if (currentShape.equals("freehand")) {
            canvas.drawPath(drawPath, drawPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = touchX;
                startY = touchY;
                if (currentShape.equals("freehand")) {
                    drawPath.moveTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                endX = touchX;
                endY = touchY;
                if (currentShape.equals("freehand")) {
                    drawPath.lineTo(touchX, touchY);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                endX = touchX;
                endY = touchY;

                // Draw shape onto the canvas
                if (currentShape.equals("rectangle")) {
                    drawCanvas.drawRect(startX, startY, endX, endY, drawPaint);
                } else if (currentShape.equals("circle")) {
                    float radius = Math.min(Math.abs(endX - startX), Math.abs(endY - startY)) / 2;
                    drawCanvas.drawCircle(startX + (endX - startX) / 2, startY + (endY - startY) / 2, radius, drawPaint);
                } else if (currentShape.equals("line")) {
                    drawCanvas.drawLine(startX, startY, endX, endY, drawPaint);
                } else if (currentShape.equals("freehand")) {
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                }

                invalidate();
                break;
            default:
                return false;
        }

        return true;
    }

    // Set the shape type
    public void setShape(String shape) {
        currentShape = shape;
    }

    // Set brush size
    public void setBrushSize(float newSize) {
        drawPaint.setStrokeWidth(newSize);
    }

    // Set brush color
    public void setColor(int newColor) {
        if (!isEraserActive) {
            drawPaint.setColor(newColor);
        }
    }

    // Enable/Disable eraser
    public void setEraser(boolean isEnabled) {
        isEraserActive = isEnabled;
        if (isEraserActive) {
            drawPaint.setColor(Color.WHITE); // Eraser color
        }
    }

    // Clear the board
    public void clearBoard() {
        drawCanvas.drawColor(Color.WHITE);
        invalidate();
    }
}
