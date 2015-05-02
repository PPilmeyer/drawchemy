/*
 * This file is part of the Drawchemy project - https://github.com/PPilmeyer/drawchemy
 *
 * Copyright (c) 2015 Pilmeyer Patrick
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

import android.graphics.PointF;

import draw.chemy.DrawManager;

import static draw.chemy.DrawUtils.getProbability;

public class ScrawlCreator extends ACreator {

  public static final float MIN_NOISE = 1.f;
  public static final float MAX_NOISE = 20.f;

  public static final int MIN_DETAIL = 2;
  public static final int MAX_DETAIL = 20;

  public static final int MIN_FLOW = 1;
  public static final int MAX_FLOW = 10;

  // Parameters
  private float fNoise = 10.0f;
  private int fDetail = 8;
  private int fFlow = 3;

  private PointF fPreviousPoint;
  private int fCount = 0;
  private SimpleLineOperation fCurrentOperation = null;

  public ScrawlCreator(DrawManager aManager) {
    super(aManager);
    fPreviousPoint = new PointF();
  }

  public float getNoise() {
    return fNoise;
  }

  public void setNoise(float fNoise) {
    this.fNoise = fNoise;
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

    fCurrentOperation = new SimpleLineOperation(x, y, getPaint());

    fPreviousPoint.x = x;
    fPreviousPoint.y = y;

    return fCurrentOperation;
  }

  @Override
  public void updateDrawingOperation(float x, float y) {
    if (fCount++ % fFlow == 0) {
      scraw(x, y);
      redraw();
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
