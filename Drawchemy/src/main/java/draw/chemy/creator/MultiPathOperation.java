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

import java.util.LinkedList;

class MultiPathOperation implements IDrawingOperation {

    private Paint fPaint;
    private LinkedList<Path> fPaths;
    private RectF fBounds;
    private RectF fBoundsTemp;

    public MultiPathOperation(Paint aPaint) {
        fPaint = aPaint;
        fPaths = new LinkedList<Path>();
        fBounds = new RectF();
        fBoundsTemp = new RectF();
    }

    @Override
    public synchronized void draw(Canvas aCanvas) {
        for (Path p : fPaths) {
            aCanvas.drawPath(p, fPaint);
        }
    }

    public synchronized void addPath() {
        if (fPaths.size() == 1) {
            fPaths.getLast().computeBounds(fBounds, true);
        } else if (fPaths.size() > 1) {
            fPaths.getLast().computeBounds(fBoundsTemp, true);
            fBounds.union(fBoundsTemp);
        }
        fPaths.addLast(new Path());
    }

    public synchronized void setTop(Path aPath) {
        fPaths.getLast().set(aPath);
    }

    @Override
    public Paint getPaint() {
        return fPaint;
    }

    @Override
    public synchronized void computeBounds(RectF aBoundSFCT) {
        fPaths.getLast().computeBounds(aBoundSFCT, true);
        if (fPaths.size() > 1) {
            aBoundSFCT.union(fBounds);
        }
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
