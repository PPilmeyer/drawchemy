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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import draw.chemy.DrawManager;
import draw.chemy.DrawUtils;

public class PaintBrushCreator extends ACreator {

  public static int MIN_BRISLTES_NUM = 7;
  public static int MAX_BRISLTES_NUM = 25;
  private static float DIV = 0.85f;

  private MultiLineOperation fCurrentOperation = null;
  Painter fPainters[];

  private int fBristlesNumber = 10;
  private float fX;
  private float fY;

  public PaintBrushCreator(DrawManager aManager) {
    super(aManager);
    fPainters = new Painter[MAX_BRISLTES_NUM];
    for (int i = 0; i < MAX_BRISLTES_NUM; i++) {
      fPainters[i] = new Painter();
    }
  }

  public void setBristlesNumber(int aBristlesNumber) {
    fBristlesNumber = aBristlesNumber;
  }

  public int getBristlesNumber() {
    return fBristlesNumber;
  }

  @Override
  public IDrawingOperation startDrawingOperation(float x, float y) {
    for (int i = 0; i < fBristlesNumber; i++) {
      fPainters[i].start(x, y);
    }
    Paint p = getPaint();
    p.setStyle(Paint.Style.STROKE);
    fCurrentOperation = new MultiLineOperation(x, y, p, fBristlesNumber);
    fX = x;
    fY = y;
    return fCurrentOperation;
  }

  @Override
  public void updateDrawingOperation(float x, float y) {
    fX = x;
    fY = y;
    for (int i = 0; i < fBristlesNumber; i++) {
      fPainters[i].update(x, y);
      fCurrentOperation.addPoint(fPainters[i].X, fPainters[i].Y, i);
    }
    redraw();
  }

  @Override
  public void endDrawingOperation() {
    for (int i = 0; i < fBristlesNumber; i++) {
      fPainters[i].update(fX, fY);
      fCurrentOperation.addPoint(fPainters[i].X, fPainters[i].Y, i);
    }

    for (int i = 0; i < fBristlesNumber; i++) {
      fCurrentOperation.addCurve(
          fPainters[i].X, fPainters[i].Y, fX, fY, i);
    }

    for (int i = 0; i < MAX_BRISLTES_NUM; i++) {
      fPainters[i].reset();
    }

    fCurrentOperation = null;
  }

  private class Painter {
    float X, Y;
    float dX, dY;
    float fEase;

    public Painter() {
      reset();
    }

    public void reset() {
      dX = 0;
      dY = 0;
      fEase = DrawUtils.RANDOM.nextFloat() * .2f + .2f;
    }

    public void start(float x, float y) {
      X = x;
      Y = y;
    }

    public void update(float x, float y) {
      dX = (dX + (X - x) * DIV) * fEase;
      X = X - (dX);
      dY = (dY + (Y - y) * DIV) * fEase;
      Y = Y - (dY);
    }
  }

  class MultiLineOperation implements IDrawingOperation {

    private Path fPaths[];
    private Paint fPaint;
    private RectF fBounds;

    public MultiLineOperation(float x, float y, Paint aPaint, int size) {
      fPaths = new Path[size];
      for (int i = 0; i < size; i++) {
        fPaths[i] = new Path();
        fPaths[i].moveTo(x, y);
      }
      fPaint = aPaint;
      fBounds = new RectF(x, y, x, y);
    }

    @Override
    public synchronized void draw(Canvas aCanvas) {
      for (Path path : fPaths) {
        aCanvas.drawPath(path, fPaint);
      }
    }

    public synchronized void addPoint(float x, float y, int id) {
      fPaths[id].lineTo(x, y);
      fBounds.union(x, y);
    }

    @Override
    public Paint getPaint() {
      return fPaint;
    }

    @Override
    public synchronized void computeBounds(RectF aBoundSFCT) {
      aBoundSFCT.set(fBounds);
    }

    public synchronized void addCurve(float x1, float y1, float x2, float y2, int id) {
      fPaths[id].quadTo(x1, y1, x2, y2);
      fBounds.union(x2, y2);
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
