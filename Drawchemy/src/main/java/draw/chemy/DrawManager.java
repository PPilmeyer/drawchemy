/*
 * This file is part of the Drawchemy project - https://code.google.com/p/drawchemy/
 *
 * Copyright (c) 2014 Pilmeyer Patrick
 *
 * Drawchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Drawchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Drawchemy.  If not, see <http://www.gnu.org/licenses/>.
 */

package draw.chemy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import draw.chemy.creator.ACreator;
import draw.chemy.creator.IDrawingOperation;

public class DrawManager implements View.OnTouchListener {


    LinkedList<IDrawingOperation> fOperations;
    LinkedList<IDrawingOperation> fUndo;
    IDrawingOperation fCurrentOperation = null;

    Map<Integer, ACreator> fCreators;
    private ACreator fCurrentCreator;

    private DrawListener fDrawListener;
    private Matrix fInputMatrix = new Matrix();
    private TouchListener fTouchListener;

    private final Matrix fMirrorHorizontal;
    private final Matrix fMirrorVertical;
    private final Matrix fMirrorHAndV;

    private final Paint fDitherPaint = new Paint();

    private final Bitmap fBackgroundImage;

    private final Canvas fBackgroundCanvas;

    private final Bitmap fBackgroundImageBackUP;

    private final Canvas fBackgroundCanvasBackUP;


    private static final int MAX_UNDO = 10;


    public PaintState getPaintState() {
        return fPaintState;
    }

    private final PaintState fPaintState = new PaintState();


    public DrawManager(int aWidth, int aHeight) {
        fBackgroundImage = Bitmap.createBitmap(aWidth, aHeight, Bitmap.Config.RGB_565);

        fBackgroundCanvas = new Canvas(fBackgroundImage);

        fBackgroundImageBackUP = Bitmap.createBitmap(aWidth, aHeight, Bitmap.Config.RGB_565);

        fBackgroundCanvasBackUP = new Canvas(fBackgroundImageBackUP);

        fBackgroundCanvas.drawColor(fPaintState.getSubColor() | 0xff000000);
        fBackgroundCanvasBackUP.drawColor(fPaintState.getSubColor() | 0xff000000);

        fOperations = new LinkedList<IDrawingOperation>();
        fUndo = new LinkedList<IDrawingOperation>();

        fCreators = new HashMap<Integer, ACreator>();
        fCurrentCreator = null;

        fDitherPaint.setDither(true);

        fMirrorHorizontal = new Matrix();
        fMirrorHorizontal.setScale(1, -1, 0, 0);
        fMirrorHorizontal.postTranslate(0, aHeight);

        fMirrorVertical = new Matrix();
        fMirrorVertical.setScale(-1, 1, 0, 0);
        fMirrorVertical.postTranslate(aWidth, 0);

        fMirrorHAndV = new Matrix();
        fMirrorHAndV.setScale(-1, -1, 0, 0);
        fMirrorHAndV.postTranslate(aWidth, aHeight);

    }

    public void clear() {
        clear(fPaintState.getSubColor());
    }

    public void clear(int aColor) {
        synchronized (fBackgroundCanvas) {
            fBackgroundCanvas.drawColor(aColor | 0xff000000);
            fBackgroundCanvasBackUP.drawColor( aColor | 0xff000000);
            fOperations.clear();
            fUndo.clear();
            for (Map.Entry<Integer, ACreator> creator : fCreators.entrySet()) {
                creator.getValue().clear();
            }
        }
        redraw();
    }



    public void undo() {
        synchronized (fBackgroundCanvas) {
            if (fOperations.size() != 0) {
                IDrawingOperation op = fOperations.removeLast();
                op.undo();
                fUndo.addFirst(op);
                backup();
            }
        }
        redraw();
    }

    public void redo() {
        synchronized (fBackgroundCanvas) {
            if (fUndo.size() != 0) {
                IDrawingOperation op = fUndo.removeFirst();
                op.redo();
                fOperations.addLast(op);
                backup();
            }
        }
        redraw();
    }

    private void backup() {
        fBackgroundCanvas.drawBitmap(fBackgroundImageBackUP, 0, 0, fDitherPaint);
        for (IDrawingOperation op : fOperations) {
            op.draw(fBackgroundCanvas);
        }
    }


    public void setInputMatrix(Matrix aInputMatrix) {
        fInputMatrix = aInputMatrix;
    }

    public void setDrawListener(DrawListener aDrawListener) {
        fDrawListener = aDrawListener;
    }

    @SuppressWarnings("all")
    public DrawListener getDrawListener() {
        return fDrawListener;
    }


    public void addTool(int aKey, ACreator aDrawingTool) {
        fCreators.put(aKey, aDrawingTool);
    }

    public void setCurrentTool(int aKey) {
        if (fCreators.containsKey(aKey)) {
            fCurrentCreator = fCreators.get(aKey);
        }
    }

    public ACreator getCurrentCreator() {
        return fCurrentCreator;
    }

    static class MyMotionEvent {

        int action;
        float x;
        float y;

        public MyMotionEvent(int aAction, float aX, float aY){
            action = aAction;
            x = aX;
            y = aY;
        }

        public int getAction() {
            return action;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return onTouch(view, new MyMotionEvent(motionEvent.getAction(), motionEvent.getX(), motionEvent.getY()));
    }

    public boolean onTouch(View view, MyMotionEvent motionEvent) {
        int action = motionEvent.getAction();

        float point[] = new float[]{motionEvent.getX(), motionEvent.getY()};
        fInputMatrix.mapPoints(point);


        if(fTouchListener.isActive()) {
            fTouchListener.touch(action, point, motionEvent.getX(), motionEvent.getY());
            return true;
        }

        if (fCurrentCreator != null) {
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    addOperation(fCurrentCreator.startDrawingOperation(point[0], point[1]));
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    fCurrentCreator.updateDrawingOperation(point[0], point[1]);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    fCurrentCreator.endDrawingOperation();
                    finishOp();
                    break;
                }
                default:
                    return false;
            }
        }
        return true;
    }

    public void redraw() {
        fDrawListener.redraw();
    }

    public void addOperation(IDrawingOperation op) {
        fUndo.clear();
        if (fPaintState.isGradientActive()) {
            op = new GradientOp(op, fPaintState.getModifiedMainColor(), fPaintState.getModifiedSubColor());
        }

        if (fPaintState.getMirrorState() != PaintState.MIRROR.None) {
            op = new MirrorOp(op, fPaintState.getMirrorState());
        }

        if (fPaintState.isKaleidoscopeActive()) {
            op = new KaleidoscopeOp(op);
        }

        synchronized (fBackgroundCanvas) {
            fCurrentOperation = op;
            fOperations.addLast(fCurrentOperation);
            while (fOperations.size() > MAX_UNDO) {
                op = fOperations.removeFirst();
                op.complete();
                op.draw(fBackgroundCanvasBackUP);
            }
        }
    }

    private void finishOp() {
        synchronized (fBackgroundCanvas) {
            fCurrentOperation.draw(fBackgroundCanvas);
            fCurrentOperation = null;
        }
        redraw();
    }

    public void draw(Canvas aCanvas) {
        synchronized (fBackgroundCanvas) {
            aCanvas.drawBitmap(fBackgroundImage, 0.f, 0.f, fDitherPaint);
            if (fCurrentOperation != null) {
                fCurrentOperation.draw(aCanvas);
            }
        }
    }

    public int getWidth() {
        return fBackgroundImage.getWidth();
    }

    public int getHeight() {
        return fBackgroundImage.getHeight();
    }

    public Bitmap copyBitmap() {
        Bitmap result = Bitmap.createBitmap(fBackgroundImage.getWidth(), fBackgroundImage.getHeight(), Bitmap.Config.RGB_565);
        draw(new Canvas(result));
        return result;
    }

    public Bitmap getBitmap() {
        return fBackgroundImage;
    }

    public void putBitmapAsBackground(Bitmap aBitmap) {

        float width = getWidth();
        float height = getHeight();

        float bitmapWidth = aBitmap.getWidth();
        float bitmapHeight = aBitmap.getHeight();

        Matrix matrix = new Matrix();
        float dx, dy, scale;

        if (width > height) {
            // canvas is in on paysage mode

            if (bitmapHeight > bitmapWidth) {
                // bitmap is in on portrait mode
                matrix.setRotate(-90);
                matrix.postTranslate(0, bitmapWidth);

                float temp = bitmapWidth;
                bitmapWidth = bitmapHeight;
                bitmapHeight = temp;
            }

            scale = width / bitmapWidth;
            if (scale * bitmapHeight > height) {
                scale = height / bitmapHeight;
                dx = (width - scale * bitmapWidth) / 2.f;
                dy = 0.f;
            } else {
                dx = 0;
                dy = (height - scale * bitmapHeight) / 2.f;
            }
        } else {
            // canvas is in on portrait mode
            if (bitmapWidth > bitmapHeight) {
                // bitmap is in on paysage mode
                matrix.setRotate(90);
                matrix.postTranslate(bitmapHeight, 0);

                float temp = bitmapWidth;
                bitmapWidth = bitmapHeight;
                bitmapHeight = temp;
            }

            scale = height / bitmapHeight;
            if (scale * bitmapWidth > bitmapWidth) {
                scale = width / bitmapWidth;
                dx = 0;
                dy = (height - scale * bitmapHeight) / 2.f;
            } else {
                dx = (width - scale * bitmapWidth) / 2.f;
                dy = 0.f;
            }
        }
        matrix.postScale(scale, scale);
        matrix.postTranslate(dx, dy);

        synchronized (fBackgroundCanvas) {
            fBackgroundCanvas.drawColor(fPaintState.getSubColor() | 0xff000000);
            fOperations.clear();
            fUndo.clear();
            fBackgroundCanvas.drawBitmap(aBitmap, matrix, null);
            fBackgroundCanvasBackUP.drawBitmap(aBitmap, matrix, null);
            for (Map.Entry<Integer, ACreator> creator : fCreators.entrySet()) {
                creator.getValue().clear();
            }
        }
        redraw();
    }

    //Effect Operations;

    private class MirrorOp implements IDrawingOperation {

        private final IDrawingOperation fDelegate;
        private final PaintState.MIRROR fMirrorState;

        public MirrorOp(IDrawingOperation aDelegate, PaintState.MIRROR aMirrorState) {
            fDelegate = aDelegate;
            fMirrorState = aMirrorState;
        }

        @Override
        public void draw(Canvas aCanvas) {
            switch (fMirrorState) {
                case Horizontal: {
                    fDelegate.draw(aCanvas);
                    aCanvas.save();
                    aCanvas.concat(fMirrorHorizontal);
                    fDelegate.draw(aCanvas);
                    aCanvas.restore();
                    break;
                }
                case Vertical: {
                    fDelegate.draw(aCanvas);
                    aCanvas.save();
                    aCanvas.concat(fMirrorVertical);
                    fDelegate.draw(aCanvas);
                    aCanvas.restore();
                    break;
                }
                case Both: {
                    fDelegate.draw(aCanvas);

                    aCanvas.save();
                    aCanvas.concat(fMirrorVertical);
                    fDelegate.draw(aCanvas);
                    aCanvas.restore();

                    aCanvas.save();
                    aCanvas.concat(fMirrorHorizontal);
                    fDelegate.draw(aCanvas);
                    aCanvas.restore();

                    aCanvas.save();
                    aCanvas.concat(fMirrorHAndV);
                    fDelegate.draw(aCanvas);
                    aCanvas.restore();
                    break;
                }

            }
        }

        @Override
        public Paint getPaint() {
            return fDelegate.getPaint();
        }

        @Override
        public void computeBounds(RectF aBoundSFCT) {
            fDelegate.computeBounds(aBoundSFCT);
        }

        @Override
        public void undo() {
            fDelegate.undo();
        }

        @Override
        public void redo() {
            fDelegate.redo();
        }

        @Override
        public void complete() {
            fDelegate.complete();
        }
    }

    private class GradientOp implements IDrawingOperation {

        private final int fMainColor;
        private final int fSubColor;
        private final IDrawingOperation fDelegate;
        private final RectF fBounds;

        public GradientOp(IDrawingOperation aDelegate, int aMainColor, int aSubColor) {
            fDelegate = aDelegate;
            fMainColor = aMainColor;
            fSubColor = aSubColor;
            fBounds = new RectF();
        }

        @Override
        public synchronized void draw(Canvas aCanvas) {
            getPaint().setShader(createShader());
            fDelegate.draw(aCanvas);
        }

        @Override
        public Paint getPaint() {
            return fDelegate.getPaint();
        }

        @Override
        public void computeBounds(RectF aBoundSFCT) {
            fDelegate.computeBounds(aBoundSFCT);
        }

        private Shader createShader() {
            computeBounds(fBounds);
            return new LinearGradient(fBounds.left, fBounds.top, fBounds.right, fBounds.bottom,
                    this.fMainColor,
                    this.fSubColor, Shader.TileMode.REPEAT);
        }

        @Override
        public void undo() {
            fDelegate.undo();
        }

        @Override
        public void redo() {
            fDelegate.redo();
        }

        @Override
        public void complete() {
            fDelegate.complete();
        }
    }

    private class KaleidoscopeOp implements IDrawingOperation {

        IDrawingOperation fDelegate;
        private Matrix[] KaleidoscopeMatrix;


        @Override
        public synchronized void draw(Canvas aCanvas) {
            fDelegate.draw(aCanvas);
            for (Matrix m : KaleidoscopeMatrix) {
                aCanvas.save();
                aCanvas.concat(m);
                fDelegate.draw(aCanvas);
                aCanvas.restore();
            }
        }

        @Override
        public Paint getPaint() {
            return fDelegate.getPaint();
        }

        @Override
        public void computeBounds(RectF aBoundSFCT) {
            fDelegate.computeBounds(aBoundSFCT);
        }

        @Override
        public void undo() {
            fDelegate.undo();
        }

        @Override
        public void redo() {
            fDelegate.redo();
        }

        @Override
        public void complete() {
            fDelegate.complete();
        }

        public KaleidoscopeOp(IDrawingOperation op) {
            fDelegate = op;
            KaleidoscopeMatrix = new Matrix[fPaintState.getKaleidoscopeSec() - 1];
            float angle = 360.f / (1.f + KaleidoscopeMatrix.length);
            for (int i = 0; i < KaleidoscopeMatrix.length; i++) {
                Matrix m = new Matrix();
                m.setTranslate(-getWidth() / 2, -getHeight() / 2);
                m.postRotate((i + 1) * angle);
                m.postTranslate(getWidth() / 2, getHeight() / 2);
                KaleidoscopeMatrix[i] = m;
            }
        }
    }

    //Listener Interface
    public interface DrawListener {
        public void redraw();
    }


    public interface TouchListener {
        void touch(int aMotionEventType, float[] aPoint, float X, float Y);

        boolean isActive();
    }

    public void setTouchListener(TouchListener aTouchListener) {
        fTouchListener = aTouchListener;
    }

    public void removeListeners() {
        fDrawListener = null;
        fTouchListener = null;
    }
}