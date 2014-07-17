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

package draw.chemy.creator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Pair;

import java.util.LinkedList;

import draw.chemy.DrawManager;

public class NearestPointLineCreator extends ACreator {

    public static final float MAX_DIST_LIM = 55.f;
    public static final float MIN_DIST_LIM = 15.f;

    private NearestPointLineOperation fCurrentOperation;
    private LinkedList<NearestPointLineOperation> fPreviousOps;
    private PointF fPrevious;

    private float fDistLim = 35.f;

    public NearestPointLineCreator(DrawManager aManager) {
        super(aManager);
        fPreviousOps = new LinkedList<NearestPointLineOperation>();
    }

    @Override
    public IDrawingOperation startDrawingOperation(float x, float y) {
        fPrevious = new PointF(x, y);
        fCurrentOperation = new NearestPointLineOperation(fManager.getPaint(), x, y);
        return fCurrentOperation;
    }

    @Override
    public void updateDrawingOperation(float x, float y) {
        PointF next = new PointF(x, y);
        fCurrentOperation.addLine(fPrevious, next);
        // find removeListeners points from previous operations
        for (NearestPointLineOperation previousOp : fPreviousOps) {
            for (PointF p : previousOp.fPreviousPoints) {
                if (Math.hypot(p.x - x, p.y - y) < fDistLim) {
                    addLine(p, next);
                }
            }
        }

        // find removeListeners points from current operations
        synchronized (fCurrentOperation) {
            for (PointF p : fCurrentOperation.fPreviousPoints) {
                if (Math.hypot(p.x - x, p.y - y) < fDistLim) {
                    addLine(p, next);
                }
            }
        }

        fCurrentOperation.addPoint(next);
        fPrevious = next;
        fManager.redraw();
    }

    void addLine(PointF a, PointF b) {
        fCurrentOperation.addLine(a, b);
    }

    @Override
    public void endDrawingOperation() {
        fPreviousOps.addLast(fCurrentOperation);
        fCurrentOperation = null;
    }

    @Override
    public void clear() {
        super.clear();
        fPreviousOps.clear();
    }

    public float getDistLim() {
        return fDistLim;
    }

    public void setDistLim(float aDistLim) {
        fDistLim = aDistLim;
    }

    private class NearestPointLineOperation implements IDrawingOperation {

        RectF fBounds;
        Paint fPaint;
        LinkedList<Pair<PointF, PointF>> fLines;
        LinkedList<PointF> fPreviousPoints;

        public NearestPointLineOperation(Paint aPaint, float x, float y) {
            fBounds = new RectF(x, y, x, y);
            fPaint = new Paint();
            fPaint.setAntiAlias(true);
            fPaint.setStyle(Paint.Style.STROKE);
            fPaint.setStrokeWidth(aPaint.getStrokeWidth());
            fPaint.setStrokeCap(Paint.Cap.ROUND);
            fPaint.setColor(aPaint.getColor());
            fLines = new LinkedList<Pair<PointF, PointF>>();
            fPreviousPoints = new LinkedList<PointF>();
            fPreviousPoints.add(new PointF(x, y));
        }

        @Override
        public synchronized void draw(Canvas aCanvas) {
            for (Pair<PointF, PointF> line : fLines) {
                aCanvas.drawLine(line.first.x, line.first.y, line.second.x, line.second.y, fPaint);
            }
        }

        public synchronized void addLine(PointF first, PointF second) {
            fLines.addLast(new Pair<PointF, PointF>(first, second));
        }

        public synchronized void addPoint(PointF aPoint) {
            fPreviousPoints.addLast(aPoint);
            fBounds.union(aPoint.x, aPoint.y);
        }

        @Override
        public Paint getPaint() {
            return fPaint;
        }

        @Override
        public synchronized void computeBounds(RectF aBoundSFCT) {
            aBoundSFCT.set(fBounds);
        }

        @Override
        public void undo() {
            fPreviousOps.removeLast();
        }

        @Override
        public void redo() {
            fPreviousOps.addLast(this);
        }

        @Override
        public void complete() {
            fPreviousOps.removeFirst();
        }
    }
}
