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
import android.graphics.Path;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

import draw.chemy.DrawManager;
import draw.chemy.DrawUtils;


public class MultiLineCreator extends ACreator {


    private MultiLineOperation fCurrentOperation = null;

    public MultiLineCreator(DrawManager aManager) {
        super(aManager);
    }

    @Override
    public IDrawingOperation startDrawingOperation(float x, float y) {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeWidth(1.1f);
        fCurrentOperation = new MultiLineOperation(x, y, paint, 5);
        return fCurrentOperation;
    }

    @Override
    public void updateDrawingOperation(float x, float y) {

        fCurrentOperation.addPoint(x, y);
        redraw();
    }

    @Override
    public void endDrawingOperation() {
        fCurrentOperation = null;
    }

    private class MultiLineOperation implements IDrawingOperation {

        private List<Path> fPaths;
        private List<Float> fWidths;
        private Paint fPaint;
        private RectF fBounds;
        private int step = 5;

        public MultiLineOperation(float x, float y, Paint aPaint, int aLineNumber) {
            fPaths = new ArrayList<Path>(aLineNumber);
            fWidths = new ArrayList<Float>(aLineNumber);
            fBounds = new RectF(x, y, x, y);
            for (int i = 0; i < aLineNumber; i++) {
                int offset = step * (i - (aLineNumber / step));
                Path p = new Path();
                p.moveTo(x + offset, y + offset);
                fBounds.union(x + offset, y + offset);
                fPaths.add(p);
                fWidths.add(DrawUtils.RANDOM.nextFloat() * 7 + 1.f);
            }
            fPaint = aPaint;

        }

        @Override
        public synchronized void draw(Canvas aCanvas) {
            float width = 1;
            for (int i = 0; i < fPaths.size(); i++) {
                fPaint.setStrokeWidth(fWidths.get(i));
                aCanvas.drawPath(fPaths.get(i), fPaint);
            }
        }

        public synchronized void addPoint(float x, float y) {
            for (int i = 0; i < fPaths.size(); i++) {
                int offset = step * (i - (fPaths.size() / step));
                Path p = fPaths.get(i);
                p.lineTo(x + offset, y + offset);
                fBounds.union(x + offset, y + offset);
            }
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
        }

        @Override
        public void redo() {
        }

        @Override
        public void complete() {
        }
    }
}
