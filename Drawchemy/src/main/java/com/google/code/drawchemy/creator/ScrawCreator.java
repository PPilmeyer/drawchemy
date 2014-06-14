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

import android.graphics.PointF;

import com.google.code.drawchemy.DrawManager;

import static com.google.code.drawchemy.DrawUtils.getProbability;

public class ScrawCreator extends ACreator {

    private SimpleLineOperation fCurrentOperation = null;

    // Parameters
    private float fNoise = 5.0f;
    private float fDetail = 7.f;
    private int fFlow = 5;


    public static final float MIN_NOISE = 1.f;
    public static final float MAX_NOISE = 20.f;

    public static final float MIN_DETAIL = 1.f;
    public static final float MAX_DETAIL = 20.f;

    public static final int MIN_FLOW = 1;
    public static final int MAX_FLOW = 10;

    private PointF fPreviousPoint;
    private int fCount = 0;

    public ScrawCreator(DrawManager aManager) {
        super(aManager);
        fPreviousPoint = new PointF();
    }

    public float getNoise() {
        return fNoise;
    }

    public void setNoise(float fNoise) {
        this.fNoise = fNoise;
    }

    public float getDetail() {
        return fDetail;
    }

    public void setDetail(float fDetail) {
        this.fDetail = fDetail;
    }

    public int getFlow() {
        return fFlow;
    }

    public void setFlow(int fFlow) {
        this.fFlow = fFlow;
    }

    @Override
    public IDrawingOperation startDrawingOperation(float x, float y) {

        fCurrentOperation = new SimpleLineOperation(x,y,fManager.getPaint());

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
        float dirX = (current.x - fPreviousPoint.x) / fDetail;
        float dirY = (current.y - fPreviousPoint.y) / fDetail;

        float x_s = fPreviousPoint.x;
        float y_s = fPreviousPoint.y;

        for (int i = 0; i < fDetail; i++) {
            x_s += (dirX + getProbability(fNoise));
            y_s += (dirY + getProbability(fNoise));
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
