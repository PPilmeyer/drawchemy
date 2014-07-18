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
import android.graphics.PointF;

import draw.chemy.DrawManager;

import static draw.chemy.DrawUtils.getProbability;

public class XShapeCreator extends ACreator {

    public static final float MIN_NOISE = 5.f;
    public static final float MAX_NOISE = 30.f;

    public static final int MIN_DETAIL = 2;
    public static final int MAX_DETAIL = 15;

    public static final int MIN_FLOW = 1;
    public static final int MAX_FLOW = 10;

    // Parameters
    private float fNoise = 17;
    private int fDetail = 8;
    private int fFlow = 3;

    private PointF fPreviousPoint;
    private int fCount = 0;
    private SimpleLineOperation fCurrentOperation = null;

    public XShapeCreator(DrawManager aManager) {
        super(aManager);
        fPreviousPoint = new PointF();
    }

    public float getNoise() {
        return fNoise;
    }

    public void setNoise(float aNoise) {
        fNoise = aNoise;
    }

    public int getDetail() {
        return fDetail;
    }

    public void setDetail(int aDetail) {
        fDetail = aDetail;
    }

    public int getFlow() {
        return fFlow;
    }

    public void setFlow(int fFlow) {
        this.fFlow = fFlow;
    }

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
        fPreviousPoint.x = x;
        fPreviousPoint.y = y;

        return fCurrentOperation;
    }

    @Override
    public void updateDrawingOperation(float x, float y) {
        if (fCount++ % fFlow == 0) {
            scraw(x, y);
            fManager.redraw();
        }
    }

    private void scraw(float x, float y) {
        PointF current = new PointF(x, y);
        float dirX = (current.x - fPreviousPoint.x) / (fDetail);
        float dirY = (current.y - fPreviousPoint.y) / (fDetail);

        float d = Math.abs(dirX) + Math.abs(dirY);

        float pDirX = dirY / d;
        float pDirY = dirX / (-d);

        float x_s = x;
        float y_s = y;

        for (int i = 1; i < fDetail; i++) {
            x_s = fPreviousPoint.x + i * dirX + pDirX * getProbability(fNoise);
            y_s = fPreviousPoint.y + i * dirY + pDirY * getProbability(fNoise);

            fCurrentOperation.addPoint(x_s, y_s);
        }

        fPreviousPoint.x = x_s;
        fPreviousPoint.y = y_s;
    }

    @Override
    public void endDrawingOperation() {
        fCurrentOperation = null;
    }
}