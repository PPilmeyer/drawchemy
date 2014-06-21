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

package draw.chemy.color;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class RoundIconGenerator {

    private Bitmap fBitmap;
    private Canvas fCanvas;

    private final static int BACKGROUND_COLOR = Color.BLACK;
    private final static int STROKE_COLOR = Color.LTGRAY;

    private static final int SIZE = 24;
    private static final float MARGIN = 1.5f;
    private Context fContext;

    public RoundIconGenerator(Context aContext) {
        fContext = aContext;

        fBitmap = Bitmap.createBitmap(SIZE,SIZE, Bitmap.Config.ARGB_8888);
        fCanvas = new Canvas(fBitmap);
    }

    private void draw(int aColor, boolean isMain) {

        Paint main = new Paint();
        main.setColor(aColor);
        main.setStyle(Paint.Style.FILL);

        Paint round = new Paint();
        round.setColor(STROKE_COLOR);
        round.setStrokeWidth(2.2f);
        round.setAntiAlias(true);
        round.setStyle(Paint.Style.STROKE);

        fCanvas.drawColor(BACKGROUND_COLOR, PorterDuff.Mode.CLEAR);

        float size;
        if(isMain) {
            size = (SIZE-MARGIN)/2.f;
        } else {
            size = (SIZE-MARGIN)/4.f;
        }

        fCanvas.drawCircle(SIZE/2,SIZE/2,size,main);
        fCanvas.drawCircle(SIZE/2,SIZE/2,size,round);
    }

    public synchronized Drawable getIcon(int aColor, boolean isMain) {
        aColor = aColor | 0xff000000;
        draw(aColor,isMain);
        return new BitmapDrawable(fContext.getResources(),fBitmap);
    }

}
