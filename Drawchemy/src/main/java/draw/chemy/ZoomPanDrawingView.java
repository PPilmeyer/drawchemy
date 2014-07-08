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

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ZoomPanDrawingView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawManager fCanvasManager;
    private SurfaceHolder fHolder;
    private ViewThread fThread;

    private MyDrawListener fDrawListener;
    public ZoomPanTouchListener fZoomManager;

    private Matrix fViewMatrix;
    private Matrix fInputMatrix;

    private final Lock fDrawLock;
    private final Condition fDrawCondition;
    private boolean fDrawFlag;
    private boolean fZoomFlag;

    private int fWidth;
    private int fHeight;

    private float fCorners[];

    private enum MODE {
        DRAG, ZOOM, NONE
    }


    @SuppressWarnings("deprecation")
    public ZoomPanDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fHolder = getHolder();
        fHolder.addCallback(this);

        int width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        int height = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        fCanvasManager = new DrawManager(width, height);

        fDrawListener = new MyDrawListener();
        fCanvasManager.setDrawListener(fDrawListener);
        fZoomManager = new ZoomPanTouchListener(fCanvasManager);

        fViewMatrix = new Matrix();
        fZoomManager.setViewMatrix(fViewMatrix);

        fInputMatrix = new Matrix();
        fZoomManager.setInputMatrix(fInputMatrix);

        fCanvasManager.setInputMatrix(fInputMatrix);

        fDrawLock = new ReentrantLock();
        fDrawCondition = fDrawLock.newCondition();
        fDrawFlag = true;
        fZoomFlag = false;

        fWidth = fCanvasManager.getWidth();
        fHeight = fCanvasManager.getHeight();

        fCorners = new float[4];
    }

    public DrawManager getCanvasManager() {
        return fCanvasManager;
    }

    public ZoomPanDrawingView(Context context, DrawManager aCanvasManager) {
        super(context);
        fHolder = getHolder();
        fHolder.addCallback(this);
        fCanvasManager = aCanvasManager;
        fDrawListener = new MyDrawListener();
        fCanvasManager.setDrawListener(fDrawListener);
        fZoomManager = new ZoomPanTouchListener(fCanvasManager);

        fViewMatrix = new Matrix();
        fZoomManager.setViewMatrix(fViewMatrix);

        fInputMatrix = new Matrix();
        fZoomManager.setInputMatrix(fInputMatrix);

        fCanvasManager.setInputMatrix(fInputMatrix);

        fDrawLock = new ReentrantLock();
        fDrawCondition = fDrawLock.newCondition();
        fDrawFlag = true;
        fZoomFlag = false;

        fWidth = fCanvasManager.getWidth();
        fHeight = fCanvasManager.getHeight();

        fCorners = new float[4];
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setOnTouchListener(fZoomManager);
        fThread = new ViewThread(this);
        fThread.start();

        fCanvasManager.redraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        fThread.stopThread();
    }

    public void stopThread() {
        if (fThread != null) {
            fThread.stopThread();
        }
    }

    private static class ViewThread extends Thread {

        private boolean fRun = true;
        private ZoomPanDrawingView fInstance;

        public ViewThread(ZoomPanDrawingView aInstance) {
            fInstance = aInstance;
        }

        public void stopThread() {
            fRun = false;
            if (fInstance.fDrawListener != null) {
                fInstance.fDrawListener.redraw();
            }
        }

        @Override
        public void run() {
            while (fRun) {
                try {
                    fInstance.fDrawLock.lock();
                    if (fRun) {
                        while (!fInstance.fDrawFlag && !fInstance.fZoomFlag) {
                            fInstance.fDrawCondition.await();
                        }
                        if (fInstance.fZoomFlag) {
                            fInstance.fZoomManager.setViewMatrix(fInstance.fViewMatrix);
                            fInstance.fZoomManager.hadZoomed();
                        }
                        Canvas canvas = fInstance.fHolder.lockCanvas(fInstance.fHolder.getSurfaceFrame());

                        if (canvas != null) {
                            fInstance.fCorners[0] = 0;
                            fInstance.fCorners[1] = 0;
                            fInstance.fCorners[2] = fInstance.fWidth;
                            fInstance.fCorners[3] = fInstance.fHeight;
                            fInstance.fViewMatrix.mapPoints(fInstance.fCorners);
                            canvas.drawColor(Color.GRAY);
                            canvas.clipRect(fInstance.fCorners[2], fInstance.fCorners[3], fInstance.fCorners[0], fInstance.fCorners[1]);

                            canvas.setMatrix(fInstance.fViewMatrix);
                            fInstance.fCanvasManager.draw(canvas);
                            fInstance.fDrawListener.hadRedraw();
                            fInstance.fHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                } catch (InterruptedException e) {

                } finally {
                    fInstance.fDrawLock.unlock();
                }
            }
            fInstance.fCanvasManager = null;
            fInstance.fZoomManager.fDelegate = null;
            fInstance.fZoomManager = null;
            fInstance.fDrawListener = null;
            fInstance = null;
        }
    }

    public void setEnabled(boolean check) {
        fZoomManager.setEnabled(check);
    }

    public boolean isEnable() {
        return fZoomManager.isEnable();
    }

    public void switchEnabled() {
        fZoomManager.switchEnabled();
    }

    public void resetZoomPan() {
        fZoomManager.resetZoomPan();
    }

    private class MyDrawListener implements DrawManager.DrawListener {

        public MyDrawListener() {
        }

        @Override
        public void redraw() {
            fDrawLock.lock();
            fDrawFlag = true;
            fDrawCondition.signalAll();
            fDrawLock.unlock();
        }

        public void hadRedraw() {
            fDrawLock.lock();
            fDrawFlag = false;
            fDrawLock.unlock();
        }
    }

    public class ZoomPanTouchListener implements OnTouchListener {

        private OnTouchListener fDelegate;
        public boolean fEnabled = false;

        private float fScale = 1.0f;
        private float fPreviousScale = 1.0f;

        private float fTranslateX = 0;
        private float fTranslateY = 0;

        private float fPreviousX = 0;
        private float fPreviousY = 0;

        private MODE fMode;

        private float fCenterZoomRelative[];
        private float fCenterZoomConcrete[];

        private float fInitialTouch[];
        private float fInitialSpan;

        public void setViewMatrix(Matrix viewMatrix) {
            viewMatrix.setScale(fScale, fScale);
            viewMatrix.postTranslate(fTranslateX, fTranslateY);
        }

        public void setInputMatrix(Matrix inputMatrix) {
            inputMatrix.setTranslate(-fTranslateX, -fTranslateY);
            inputMatrix.postScale(1.f / fScale, 1.f / fScale);
        }


        public ZoomPanTouchListener(OnTouchListener aDelegate) {
            fDelegate = aDelegate;
        }

        public void setEnabled(boolean check) {
            fEnabled = check;
            change();
        }

        public boolean isEnable() {
            return fEnabled;
        }

        public void switchEnabled() {
            fEnabled = !fEnabled;
            change();
        }

        private void change() {
            if (!fEnabled) {
                setInputMatrix(fInputMatrix);
                fCanvasManager.setInputMatrix(fInputMatrix);
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (fEnabled) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        setActionMode(MODE.DRAG);
                        initDrag(event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (getActionMode() == MODE.DRAG) {
                            updateDrag(event.getX(), event.getY());
                        } else if (getActionMode() == MODE.ZOOM) {
                            updateZoom(event);
                        }
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        setActionMode(MODE.ZOOM);
                        initZoom(event);
                        break;

                    case MotionEvent.ACTION_UP:
                        setActionMode(MODE.NONE);
                        closeAction();
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        setActionMode(MODE.NONE);
                        closeAction();
                        break;
                }
                return true;
            } else
                return fDelegate.onTouch(view, event);
        }

        private void closeAction() {
            fPreviousX = fTranslateX;
            fPreviousY = fTranslateY;
            fPreviousScale = fScale;
        }

        private void updateZoom(MotionEvent event) {
            float dSpan = getSpan(event) - fInitialSpan;
            fScale = fPreviousScale + (dSpan * 0.003f);
            limitScale();

            fTranslateX = fCenterZoomRelative[0] - fScale * fCenterZoomConcrete[0];
            fTranslateY = fCenterZoomRelative[1] - fScale * fCenterZoomConcrete[1];

            redraw();
        }

        private void updateDrag(float x, float y) {
            float dx = (x - fInitialTouch[0]) * .5f;
            float dy = (y - fInitialTouch[1]) * .5f;

            fTranslateX = fPreviousX + dx;
            fTranslateY = fPreviousY + dy;
            limitDrag();
            redraw();
        }

        private void initZoom(MotionEvent event) {
            fInitialSpan = getSpan(event);
            // position relative
            fCenterZoomRelative = getCenter(event, fCenterZoomRelative);
            fCenterZoomConcrete = getConcreteCenter(fCenterZoomConcrete, fCenterZoomRelative);
            //
        }

        private void initDrag(float x, float y) {
            if (fInitialTouch == null) {
                fInitialTouch = new float[2];
            }
            fInitialTouch[0] = x;
            fInitialTouch[1] = y;
        }

        private MODE getActionMode() {
            return fMode;
        }

        private void setActionMode(MODE actionMode) {
            fMode = actionMode;
        }

        public void redraw() {
            fDrawLock.lock();
            fZoomFlag = true;
            fDrawCondition.signalAll();
            fDrawLock.unlock();
        }

        public void hadZoomed() {
            fDrawLock.lock();
            fZoomFlag = false;
            fDrawLock.unlock();
        }

        public void resetZoomPan() {
            fScale = 1.f;
            fTranslateX = 0.f;
            fTranslateY = 0.f;
            fPreviousX = 0.f;
            fPreviousY = 0.f;
            change();
            redraw();
        }

        public void limitScale() {
            if (fScale > 6.f) {
                fScale = 6.f;
            } else if (fScale < 0.2f) {
                fScale = 0.2f;
            }
        }

        public void limitDrag() {

            float w = fWidth * fScale;
            float h = fHeight * fScale;

            if (fTranslateX < (-0.8f) * w) {
                fTranslateX = (-0.8f) * w;
            } else if (fTranslateX > fWidth - (w * 0.2f)) {
                fTranslateX = fWidth - (w * 0.2f);
            }

            if (fTranslateY < (-0.8f) * h) {
                fTranslateY = (-0.8f) * h;
            } else if (fTranslateY > fHeight - (h * 0.4f)) {
                fTranslateY = fHeight - (h * 0.4f);
            }
        }
    }

    float getSpan(MotionEvent event) {
        int P = event.getPointerCount();
        if (P != 2) return 0;

        final float x0 = event.getX(0);
        final float x1 = event.getX(1);
        final float y0 = event.getY(0);
        final float y1 = event.getY(1);
        return (float) Math.hypot(x1 - x0, y1 - y0);
    }

    float[] getCenter(MotionEvent event, float[] pt) {
        int P = event.getPointerCount();
        pt = ((pt == null) ? new float[2] : pt);
        pt[0] = event.getX(0);
        pt[1] = event.getY(0);

        for (int j = 1; j < P; j++) {
            pt[0] += event.getX(j);
            pt[1] += event.getY(j);
        }
        pt[0] /= P;
        pt[1] /= P;
        return pt;
    }

    float[] getConcreteCenter(float concreteCenter[], float relativeCenter[]) {
        concreteCenter = ((concreteCenter == null) ? new float[2] : concreteCenter);
        concreteCenter[0] = relativeCenter[0];
        concreteCenter[1] = relativeCenter[1];
        fInputMatrix.mapPoints(concreteCenter);
        return concreteCenter;
    }
}


