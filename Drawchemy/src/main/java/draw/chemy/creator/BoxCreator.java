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

import android.graphics.Paint;
import android.graphics.Path;

import draw.chemy.DrawManager;

public class BoxCreator extends ACreator {

    private float fX;
    private float fY;
    private MultiPathOperation fCurrentOperation;

    private static final float COS_HALF_PI = 1.f;
    private static final float SIN_HALF_PI = 0.f;

    public BoxCreator(DrawManager aManager) {
        super(aManager);
    }

    @Override
    public IDrawingOperation startDrawingOperation(float x, float y) {
        fX = x;
        fY = y;

        Paint refPaint = fManager.getPaint();

        Paint paint = new Paint();
        paint.setColor(refPaint.getColor());
        paint.setStrokeWidth(refPaint.getStrokeWidth());
        paint.setStyle(refPaint.getStyle());
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        fCurrentOperation = new MultiPathOperation(paint);
        return fCurrentOperation;
    }

    @Override
    public void updateDrawingOperation(float x, float y) {
        float dx = x - fX;
        float dy = y - fY;

        float ax = COS_HALF_PI * dx - SIN_HALF_PI * dy;
        float ay = SIN_HALF_PI * dx + COS_HALF_PI * dy;

        fCurrentOperation.addPath();

        Path p = new Path();
        p.moveTo(fX - ax, fY - ay);
        p.lineTo(fX + ax, fY + ay);
        p.lineTo(x + ax, y + ay);
        p.lineTo(x - ax, y - ay);
        p.close();
        fCurrentOperation.setTop(p);

        fX = x;
        fY = y;
        fManager.redraw();
    }

    @Override
    public void endDrawingOperation() {
        fCurrentOperation = null;
    }

}
