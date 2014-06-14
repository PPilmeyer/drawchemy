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

package com.google.code.drawchemy.creator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Pair;

import com.google.code.drawchemy.DrawManager;

import java.util.LinkedList;

import static com.google.code.drawchemy.DrawUtils.RANDOM;
import static com.google.code.drawchemy.DrawUtils.getProbability;

public class SplatterCreator extends ACreator {

    private static final float push = 0.5f;

    private static final float newSizeInfluence = 0.2f;

    // Parameters;
    private float fMaxLineWidth = 200.f;
    private int fDrips = 12;

    public static final float MIN_SIZE = 5.f;
    public static final float MAX_SIZE = 30.f;

    public static final int MIN_DRIPS = 1;
    public static final int MAX_DRIPS = 15;

    private float fSize;
    private float startX, startY;
    private float endX, endY;

    private SplatterDrawOp fCurrentOperation;

    public SplatterCreator(DrawManager aManager) {
        super(aManager);
    }

    @Override
    public IDrawingOperation startDrawingOperation(float x, float y) {


        startX = x;
        startY = y;

        endX = x;
        endY = y;

        fSize = 1;
        fCurrentOperation = new SplatterDrawOp(fManager.getPaint());
        return fCurrentOperation;
    }

    @Override
    public void updateDrawingOperation(float x, float y) {
        float mX = startX + (endX - startX) * (1.f + push);
        float mY = startY + (endY - startY) * (1.f + push);

        startX = endX;
        startY = endY;

        endX = x;
        endY = y;

        float dst = dist(startX, startY, endX, endY);

        if (dst < 1.f) {
            dst = 1.f;
        }

        float newSize = fMaxLineWidth / dst;
        fSize = newSize * (1.f - newSizeInfluence) + fSize * newSizeInfluence;

        splat(new PointF(startX, startY), new PointF(endX, endY), new PointF(mX, mY), fSize);
        fManager.redraw();
    }

    @Override
    public void endDrawingOperation() {

    }

    public int getDrips() {
        return fDrips;
    }

    public void setDrips(int fDrips) {
        this.fDrips = fDrips;
    }

    public float getSize() {
        return fMaxLineWidth / 10.f;
    }

    public void setSize(float aSize) {
        this.fMaxLineWidth = aSize * 10;
    }


    private static class SplatterDrawOp implements IDrawingOperation {

        LinkedList<Pair<Path, Float>> fPaths;
        private Paint fPaint;

        public SplatterDrawOp(Paint aPaint) {
            fPaths = new LinkedList<Pair<Path, Float>>();
            fPaint = aPaint;
            fPaint.setStrokeCap(Paint.Cap.ROUND);
            fPaint.setStyle(Paint.Style.STROKE);
            fPaint.setShader(null);
        }

        @Override
        public synchronized void draw(Canvas aCanvas) {
            for (Pair<Path, Float> path : fPaths) {
                fPaint.setStrokeWidth(path.second);
                aCanvas.drawPath(path.first, fPaint);
            }
        }

        public synchronized void addPath(Path aPath, float aStrokeWidth) {
            fPaths.add(new Pair<Path, Float>(aPath, aStrokeWidth));
        }

        @Override
        public Paint getPaint() {
            return fPaint;
        }

        @Override
        public void computeBounds(RectF aBoundSFCT) {
            RectF temp = new RectF();
            boolean init = false;
            for (Pair<Path, Float> p : fPaths) {
                if (!init) {
                    p.first.computeBounds(aBoundSFCT, true);
                    init = true;
                } else {
                    p.first.computeBounds(temp, true);
                    aBoundSFCT.union(temp);
                }
            }
        }
    }

    private void splat(PointF start, PointF end, PointF mid, float d) {

        Path firstPath = new Path();
        if (d < 0) {
            d = 0;
        }

        firstPath.moveTo(start.x, start.y);
        firstPath.quadTo(mid.x, mid.y, end.x, end.y);
        fCurrentOperation.addPath(firstPath, d);


        float dst = dist(start.x, start.y, end.x, end.y);
        int quarterDrips = fDrips / 4;
        int nbDrips = quarterDrips + RANDOM.nextInt(fDrips);

        for (int i = 0; i < nbDrips; i++) {
            // positioning of splotch varies between ±4dd, tending towards 0

            float x4 = dst * getProbability(0.5f);
            float y4 = dst * getProbability(0.5f);
            // direction of splotch varies between ±0.5
            float x5 = getProbability(0.5f);
            float y5 = getProbability(0.5f);

            float dd = d * (RANDOM.nextFloat() + .5f);
            Path subPath = new Path();
            subPath.moveTo(start.x + x4, start.y + y4);
            subPath.lineTo(start.x + x4 + x5, start.y + y4 + y5);
            fCurrentOperation.addPath(subPath, dd);

        }
    }

    private float dist(float ax, float ay, float bx, float by) {
        return (float) Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
    }
}
