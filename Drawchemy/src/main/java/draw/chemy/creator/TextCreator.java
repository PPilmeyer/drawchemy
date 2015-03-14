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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import draw.chemy.DrawManager;
import draw.chemy.DrawUtils;

public class TextCreator extends ACreator {

    TextOperation fOperation = null;
    private static final char possibilities[] = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z','A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '0'};
    private int count;
    private int fFlow = 10;

    public TextCreator(DrawManager aManager) {
        super(aManager);
    }

    @Override
    public IDrawingOperation startDrawingOperation(float x, float y) {
        Paint tempPaint = getPaint();
        Paint paint = new Paint();
        paint.setColor(tempPaint.getColor());
        paint.setStyle(tempPaint.getStyle());
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create("default", Typeface.BOLD_ITALIC));
        fOperation = new TextOperation(paint);
        count = 0;
        addCharacter(x, y);
        return fOperation;
    }

    public int getFlow() {
        return fFlow;
    }

    public void setFlow(int aFlow) {
        this.fFlow = aFlow;
    }

    @Override
    public void updateDrawingOperation(float x, float y) {
        if (count++ % fFlow == 0) {
            addCharacter(x, y);
            redraw();
        }
    }

    private void addCharacter(float x, float y) {
        Matrix m = new Matrix();
        Random random = DrawUtils.RANDOM;
        float scale = random.nextFloat() * 8 + 8;
        m.setScale(scale, scale);
        m.preRotate(360.f * random.nextFloat());
        m.postTranslate(x, y);
        Rect bounds = new Rect();
        char character = possibilities[random.nextInt(possibilities.length)];
        fOperation.getPaint().getTextBounds(new char[] {character},0,1,bounds );
        fOperation.addCharacter(new Character(character, m, bounds));
    }

    @Override
    public void endDrawingOperation() {
        fOperation = null;
    }

    private class Character {
        char fChar;
        private Matrix fMatrix;
        private RectF fBounds;

        public Character(char aChar, Matrix aMatrix, Rect aBounds) {
            fChar = aChar;
            fMatrix = aMatrix;
            fBounds = new RectF(aBounds);
            fMatrix.mapRect(fBounds);
        }
    }

    private static class TextOperation implements IDrawingOperation {

        private Paint fPaint;
        private List<Character> fCharacters = new ArrayList<Character>();

        private TextOperation(Paint fPaint) {
            this.fPaint = fPaint;
            this.fPaint.setTypeface(Typeface.DEFAULT_BOLD);
            this.fPaint.setStrokeJoin(Paint.Join.MITER);
            this.fPaint.setStrokeCap(Paint.Cap.BUTT);
        }

        public void addCharacter(Character aCharacter) {
            fCharacters.add(aCharacter);
        }

        @Override
        public void draw(Canvas aCanvas) {
            Matrix m = new Matrix();
            for (Character c : fCharacters) {
                aCanvas.save();
                aCanvas.concat(c.fMatrix);
                if(fPaint.getShader() != null) {
                    c.fMatrix.invert(m);
                    fPaint.getShader().setLocalMatrix(m);
                }
                aCanvas.drawText(new char[]{c.fChar}, 0, 1, 0, 0, fPaint);
                aCanvas.restore();
            }
            if(fPaint.getShader() != null) {
                fPaint.getShader().setLocalMatrix(null);
            }
        }

        @Override
        public Paint getPaint() {
            return fPaint;
        }

        @Override
        public void computeBounds(RectF aBoundSFCT) {
            if (fCharacters.size() > 0) {
                Character first = fCharacters.get(0);
                aBoundSFCT.set(first.fBounds);
                for (Character c : fCharacters) {
                    aBoundSFCT.union(c.fBounds);
                }
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
}
