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

import draw.chemy.DrawManager;
import draw.chemy.DrawUtils;

public class XShapeV2Creator extends ACreator {

    private SimpleLineOperation fCurrentOperation;

    private float fPreviousX, fPreviousY;

    public XShapeV2Creator(DrawManager aManager) {
        super(aManager);
    }

    public static final float MAX_NOISE = 1.0f;
    public static final float MIN_NOISE = 0.1f;

    public float getNoise() {
        return fNoise;
    }

    public void setNoise(float aNoise) {
        fNoise = aNoise;
    }

    private float fNoise = 0.5f;

    @Override
    public IDrawingOperation startDrawingOperation(float x, float y) {

        Paint p = fManager.getPaint();

        Paint paint = new Paint();
        paint.setColor(p.getColor());
        paint.setStrokeWidth(p.getStrokeWidth());
        paint.setStyle(p.getStyle());
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeCap(Paint.Cap.SQUARE);

        fCurrentOperation = new SimpleLineOperation(x, y, paint);
        fPreviousX = x;
        fPreviousY = y;
        return fCurrentOperation;
    }

    @Override
    public void updateDrawingOperation(float x, float y) {

        float vX = x - fPreviousX;
        float vY = y - fPreviousY;


        float aX = x - vX * (1 + DrawUtils.getProbability(fNoise));
        float aY = y - vY * (1 + DrawUtils.getProbability(fNoise));

        float bX = fPreviousX + vX * (1 + DrawUtils.getProbability(fNoise));
        float bY = fPreviousY + vY * (1 + DrawUtils.getProbability(fNoise));

        fCurrentOperation.addPoint(aX, aY);
        fCurrentOperation.addPoint(bX, bY);

        fPreviousX = x;
        fPreviousY = y;
        fManager.redraw();
    }

    @Override
    public void endDrawingOperation() {
        fCurrentOperation = null;
    }
}
