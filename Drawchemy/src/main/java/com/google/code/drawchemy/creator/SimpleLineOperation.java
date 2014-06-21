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
import android.graphics.RectF;

class SimpleLineOperation implements IDrawingOperation {

    private Path fPath;
    private Paint fPaint;
    private RectF fBounds;

    public SimpleLineOperation(float x, float y, Paint aPaint) {
        fPath = new Path();
        fPath.moveTo(x, y);
        fPaint = aPaint;
        fBounds = new RectF(x,y,x,y);
    }

    @Override
    public synchronized void draw(Canvas aCanvas) {
        aCanvas.drawPath(fPath, fPaint);
    }

    public synchronized void addPoint(float x, float y) {
        fPath.lineTo(x, y);
        fBounds.union(x,y);
    }

    @Override
    public Paint getPaint() {
        return fPaint;
    }

    @Override
    public synchronized void computeBounds(RectF aBoundSFCT) {
        aBoundSFCT.set(fBounds);
    }
}
