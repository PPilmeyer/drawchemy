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

import android.graphics.PointF;

import draw.chemy.DrawManager;

public class SketchCreator extends NearestPointLineCreator {

    public static final float alpha = 0.3f;
    public static final float beta = 1.f - alpha;

    public SketchCreator(DrawManager aManager) {
        super(aManager);
    }

    @Override
    void addLine(PointF a, PointF b) {
        PointF a2 = new PointF(a.x * beta + b.x * alpha, a.y * beta + b.y * alpha);
        PointF b2 = new PointF(a.x * alpha + b.x * beta, a.y * alpha + b.y * beta);
        super.addLine(a2, b2);
    }
}
