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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZoomPanDrawingView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawManager fCanvasManager;
    private SurfaceHolder fHolder;
    private ViewThread fThread;

    private ViewDrawListener fDrawListener;
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

    private enum STATE {
        INIT,
        WAIT,
        DRAW,
        ZOOM_PAN
    }

    List<OverlayPainter> fOverlayPainters = new ArrayList<OverlayPainter>();

    @SuppressWarnings("all")
    public ZoomPanDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fHolder = getHolder();
        fHolder.addCallback(this);

        int width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        int height = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        fCanvasManager = new DrawManager(width, height);

        fDrawListener = new ViewDrawListener();
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

    @SuppressWarnings("all")
    public ZoomPanDrawingView(Context context, DrawManager aCanvasManager) {
        super(context);
        fHolder = getHolder();
        fHolder.addCallback(this);
        fCanvasManager = aCanvasManager;
        fDrawListener = new ViewDrawListener();
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
        if (fThread != null) {
            fThread.stopThread();
            fThread.fInstance = null;
        }
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

    public interface OverlayPainter {

        public void draw(Canvas aCanvas, DrawManager aDrawManager);

    }

    private static class ViewThread extends Thread {

        private boolean fRun = true;
        private ZoomPanDrawingView fInstance;

        public ViewThread(ZoomPanDrawingView aInstance) {
            fInstance = aInstance;
        }

        public void stopThread() {
            fRun = false;
            if (fInstance != null && fInstance.fDrawListener != null) {
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
                            canvas.save();
                            canvas.setMatrix(fInstance.fViewMatrix);
                            fInstance.fCanvasManager.draw(canvas);
                            fInstance.fDrawListener.hadRedraw();
                            canvas.restore();
                            fInstance.paintOver(canvas);
                            fInstance.fHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                } catch (InterruptedException e) {

                } finally {
                    fInstance.fDrawLock.unlock();
                }
            }
            fInstance = null;
        }


    }

    public void addOverlayPainter(OverlayPainter aOverlayPainter) {
        synchronized (fOverlayPainters) {
            fOverlayPainters.add(aOverlayPainter);
        }
    }

    public void removeOverlayPainter(OverlayPainter aOverlayPainter) {
        synchronized (fOverlayPainters) {
            fOverlayPainters.remove(aOverlayPainter);
        }
    }

    private void paintOver(Canvas aCanvas) {
        synchronized (fOverlayPainters) {
            for(OverlayPainter painter : fOverlayPainters) {
                painter.draw(aCanvas, fCanvasManager);
            }
        }
    }

    public void close() {
        fCanvasManager = null;
        fZoomManager.fDelegate = null;
        fZoomManager = null;
        if (fThread != null) {
            fThread.stopThread();
            fThread.fInstance = null;
        }
    }

    public void resetZoomPan() {
        fZoomManager.resetZoomPan();
    }

    private class ViewDrawListener implements DrawManager.DrawListener {

        public ViewDrawListener() {
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

        private DrawManager fDelegate;

        private float fScale = 1.0f;
        private float fPreviousScale = 1.0f;

        private float fTranslateX = 0;
        private float fTranslateY = 0;

        private float fInitialCenterZoomScreen[];
        private float fInitialCenterZoomConcrete[];

        private float fCenterZoomScreen[];
        private float fInitialSpan;

        private STATE fState = STATE.INIT;

        private List<DrawManager.MyMotionEvent> fPreviousEvents = new ArrayList<DrawManager.MyMotionEvent>();

        public void setViewMatrix(Matrix viewMatrix) {
            viewMatrix.setScale(fScale, fScale);
            viewMatrix.postTranslate(fTranslateX, fTranslateY);
        }

        public void setInputMatrix(Matrix inputMatrix) {
            inputMatrix.setTranslate(-fTranslateX, -fTranslateY);
            inputMatrix.postScale(1.f / fScale, 1.f / fScale);
        }

        public ZoomPanTouchListener(DrawManager aDelegate) {
            fDelegate = aDelegate;
        }


        private void change() {
            setInputMatrix(fInputMatrix);
            fCanvasManager.setInputMatrix(fInputMatrix);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    if (fState == STATE.INIT && event.getPointerCount() == 1) {
                        fState = STATE.WAIT;
                        fPreviousEvents.add(new DrawManager.MyMotionEvent(event.getAction(), event.getX(), event.getY()));
                    }
                    if (fState == STATE.INIT && event.getPointerCount() == 2) {
                        fState = STATE.ZOOM_PAN;
                        initZoom(event);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (fState == STATE.WAIT && event.getPointerCount() == 1) {
                        if (trueMove(event)) {
                            fState = STATE.DRAW;
                            for (DrawManager.MyMotionEvent previousEvent : fPreviousEvents)
                                fDelegate.onTouch(view, previousEvent);
                            fDelegate.onTouch(view, event);
                            fPreviousEvents.clear();
                        } else {
                            fPreviousEvents.add(new DrawManager.MyMotionEvent(event.getAction(), event.getX(), event.getY()));
                        }

                    } else if (fState == STATE.WAIT && event.getPointerCount() == 2) {
                        fState = STATE.ZOOM_PAN;
                        //init zoom pan
                        initZoom(event);
                    } else if (fState == STATE.ZOOM_PAN) {
                        //update zoom//pan
                        updateZoomAndPan(event);
                    } else if (fState == STATE.DRAW) {
                        fDelegate.onTouch(view, event);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (fState == STATE.ZOOM_PAN) {
                        //finish zoom
                        closeAction();
                    } else if (fState == STATE.WAIT) {
                        for (DrawManager.MyMotionEvent previousEvent : fPreviousEvents)
                            fDelegate.onTouch(view, previousEvent);
                        fPreviousEvents.clear();
                        fDelegate.onTouch(view, event);
                    } else if (fState == STATE.DRAW && event.getPointerCount() == 1) {
                        fDelegate.onTouch(view, event);
                    }
                    fState = STATE.INIT;
                    fPreviousEvents.clear();
                    break;
            }
            return true;
        }

        private boolean trueMove(MotionEvent aEvent) {
            if (fPreviousEvents.size() > 5) {
                return true;
            }
            DrawManager.MyMotionEvent myMotionEvent = fPreviousEvents.get(0);
            double distance = Math.hypot(aEvent.getX() - myMotionEvent.getX(), aEvent.getY() - myMotionEvent.getY());
            return distance > 3.;
        }

        private void closeAction() {
            change();
            fPreviousScale = fScale;
        }

        private void updateZoomAndPan(MotionEvent aEvent) {
            if (aEvent.getPointerCount() != 2) {
                return;
            }
            updateZoom(aEvent);
            updateTranslation(aEvent);
            redraw();
        }

        private void updateZoom(MotionEvent aEvent) {
            float dSpan = getSpan(aEvent) - fInitialSpan;
            fScale = fPreviousScale + (dSpan * 0.0025f);
            limitScale();
        }

        private void updateTranslation(MotionEvent event) {
            fCenterZoomScreen = getCenter(event, fCenterZoomScreen);

            fTranslateX = (-fInitialCenterZoomConcrete[0]*fScale + fCenterZoomScreen[0]);
            fTranslateY = (-fInitialCenterZoomConcrete[1]*fScale + fCenterZoomScreen[1]);
            limitDrag();
        }

        private void initZoom(MotionEvent event) {
            fInitialSpan = getSpan(event);
            initDrag(event);
        }

        private void initDrag(MotionEvent event) {
            fInitialCenterZoomScreen = getCenter(event, fInitialCenterZoomScreen);
            fInitialCenterZoomConcrete = getConcreteCenter(fInitialCenterZoomConcrete, fInitialCenterZoomScreen);
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
            fPreviousScale = 1.f;
            change();
            redraw();
        }

        public void limitScale() {
            if (fScale > 4.f) {
                fScale = 4.f;
            } else if (fScale < 0.25f) {
                fScale = 0.25f;
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

    float getSpan(MotionEvent aEvent) {
        final float x0 = aEvent.getX(0);
        final float x1 = aEvent.getX(1);
        final float y0 = aEvent.getY(0);
        final float y1 = aEvent.getY(1);
        return (float) Math.hypot(x1 - x0, y1 - y0);
    }

    float[] getCenter(MotionEvent aEvent, float[] pt) {
        int P = aEvent.getPointerCount();
        pt = ((pt == null) ? new float[2] : pt);
        pt[0] = aEvent.getX(0);
        pt[1] = aEvent.getY(0);

        for (int j = 1; j < P; j++) {
            pt[0] += aEvent.getX(j);
            pt[1] += aEvent.getY(j);
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


