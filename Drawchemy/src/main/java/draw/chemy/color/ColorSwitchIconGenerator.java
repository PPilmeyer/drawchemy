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

package draw.chemy.color;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ColorSwitchIconGenerator {

  private static final int BACKGROUND_COLOR = Color.BLACK;
  private static final int STROKE_COLOR = Color.LTGRAY;

  private static final int SIZE = 32;
  private final Paint fBorder;

  private Bitmap fBitmap;
  private Canvas fCanvas;
  private Context fContext;

  public ColorSwitchIconGenerator(Context aContext) {
    fContext = aContext;
    fBitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
    fCanvas = new Canvas(fBitmap);
    fBorder = new Paint();
    fBorder.setColor(STROKE_COLOR);
    fBorder.setStrokeWidth(2.2f);
    fBorder.setAntiAlias(true);
    fBorder.setStyle(Paint.Style.STROKE);
  }

  private void draw(int aMainColor, int aSubColor) {
    fCanvas.drawColor(BACKGROUND_COLOR, PorterDuff.Mode.CLEAR);

    Paint main = new Paint();
    main.setColor(aMainColor);
    main.setStyle(Paint.Style.FILL);

    Paint sub = new Paint();
    sub.setColor(aSubColor);
    sub.setStyle(Paint.Style.FILL);

    int width = (SIZE * 2) / 3;
    int height = SIZE / 2;
    int margin = SIZE / 8;

    fCanvas.drawRect(SIZE - margin - width, SIZE - margin - height, SIZE - margin, SIZE - margin, sub);
    fCanvas.drawRect(SIZE - margin - width, SIZE - margin - height, SIZE - margin, SIZE - margin, fBorder);

    fCanvas.drawRect(margin, margin, margin + width, margin + height, main);
    fCanvas.drawRect(margin, margin, margin + width, margin + height, fBorder);
  }

  public synchronized Drawable getIcon(int aMainColor, int aSubColor) {
    aMainColor = aMainColor | 0xff000000;
    aSubColor = aSubColor | 0xff000000;
    draw(aMainColor, aSubColor);
    return new BitmapDrawable(fContext.getResources(), fBitmap);
  }
}
