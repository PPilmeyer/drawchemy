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

import static draw.chemy.DrawUtils.RANDOM;

public class BasicShapesCreator extends ACreator {

  public enum SHAPE {
    Box,
    Circle,
    Triangle,
    Random
  }

  private static final float COS_HALF_PI = 0.f;
  private static final float SIN_HALF_PI = 1.f;

  private SHAPE fCurrentShape = SHAPE.Random;
  private float fX;
  private float fY;
  private MultiPathOperation fCurrentOperation;

  public BasicShapesCreator(DrawManager aManager) {
    super(aManager);
  }

  @Override
  public IDrawingOperation startDrawingOperation(float x, float y) {
    fX = x;
    fY = y;

    Paint refPaint = getPaint();
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

    fCurrentOperation.addPath();
    switch (fCurrentShape) {
    case Box:
      fCurrentOperation.setTop(createBox(x, y));
      break;
    case Circle:
      fCurrentOperation.setTop(createCircle(x, y));
      break;
    case Triangle:
      fCurrentOperation.setTop(createTriangle(x, y));
      break;
    case Random:
      fCurrentOperation.setTop(createRandomShape(x, y));
      break;
    }

    fX = x;
    fY = y;

    redraw();
  }

  public void setShape(SHAPE aShape) {
    fCurrentShape = aShape;
  }

  public SHAPE getShape() {
    return fCurrentShape;
  }

  @Override
  public void endDrawingOperation() {
    fCurrentOperation = null;
  }

  private Path createBox(float x, float y) {
    float dx = x - fX;
    float dy = y - fY;

    // rotate by 90°
    float ax = COS_HALF_PI * dx - SIN_HALF_PI * dy;
    float ay = SIN_HALF_PI * dx + COS_HALF_PI * dy;

    Path p = new Path();
    p.moveTo(fX - ax, fY - ay);
    p.lineTo(fX + ax, fY + ay);
    p.lineTo(x + ax, y + ay);
    p.lineTo(x - ax, y - ay);
    p.close();
    return p;
  }

  private Path createCircle(float x, float y) {
    Path p = new Path();
    float cX = (x + fX) / 2.f;
    float cY = (y + fY) / 2.f;
    float radius = (float) Math.hypot(x - fX, y - fY) / 2.f;
    p.moveTo(x, y);
    p.addCircle(cX, cY, radius, Path.Direction.CCW);
    return p;
  }

  private Path createTriangle(float x, float y) {
    float dx = x - fX;
    float dy = y - fY;

    // rotate by 90°
    float ax = (COS_HALF_PI * dx - SIN_HALF_PI * dy) * 0.33f;
    float ay = (SIN_HALF_PI * dx + COS_HALF_PI * dy) * 0.33f;

    Path p = new Path();
    p.moveTo(fX - ax, fY - ay);
    p.lineTo(fX + ax, fY + ay);
    p.lineTo(x, y);
    p.close();
    return p;
  }

  private Path createRandomShape(float x, float y) {
    int i = RANDOM.nextInt(3);
    if (i == 0) {
      return createBox(x, y);
    }
    if (i == 1) {
      return createCircle(x, y);
    }
    return createTriangle(x, y);
  }
}
