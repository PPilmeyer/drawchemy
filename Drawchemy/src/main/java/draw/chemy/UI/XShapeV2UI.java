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

package draw.chemy.UI;

import org.al.chemy.R;

import draw.chemy.creator.XShapeV2Creator;

public class XShapeV2UI extends ASettingsGroupUIWithSeekBar {


    public XShapeV2UI(XShapeV2Creator aXShapeV2Creator) {
        super(createSettings(aXShapeV2Creator));
    }

    private static SeekBarSettings[] createSettings(final XShapeV2Creator aXShapeV2Creator) {
        SeekBarSettings noise = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return true;
            }

            @Override
            public float getMax() {
                return XShapeV2Creator.MAX_NOISE;
            }

            @Override
            public float getMin() {
                return XShapeV2Creator.MIN_NOISE;
            }

            @Override
            public float getCurrent() {
                return aXShapeV2Creator.getNoise();
            }

            @Override
            public void setCurrent(float aValue) {
                aXShapeV2Creator.setNoise(aValue);
            }

            @Override
            public int getTextId() {
                return R.string.noise;
            }
        };
        return new SeekBarSettings[]{noise};

    }
}