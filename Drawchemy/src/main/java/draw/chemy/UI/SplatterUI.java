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

import draw.chemy.creator.SplatterCreator;

public class SplatterUI extends ASettingsGroupUIWithSeekBar {

    public SplatterUI(SplatterCreator aSplatterCreator) {
        super(createSettings(aSplatterCreator));
    }


    private static SeekBarSettings[] createSettings(final SplatterCreator aSplatterCreator) {

        SeekBarSettings size = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return true;
            }

            @Override
            public float getMax() {
                return SplatterCreator.MAX_SIZE;
            }

            @Override
            public float getMin() {
                return SplatterCreator.MIN_SIZE;
            }

            @Override
            public float getCurrent() {
                return aSplatterCreator.getSize();
            }

            @Override
            public void setCurrent(float aValue) {
                aSplatterCreator.setSize(aValue);
            }

            @Override
            public int getTextId() {
                return R.string.size;
            }
        };
        SeekBarSettings drips = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return SplatterCreator.MAX_DRIPS;
            }

            @Override
            public float getMin() {
                return SplatterCreator.MIN_DRIPS;
            }

            @Override
            public float getCurrent() {
                return aSplatterCreator.getDrips();
            }

            @Override
            public void setCurrent(float aValue) {
                aSplatterCreator.setDrips((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.drips;
            }
        };

        return new SeekBarSettings[]{size, drips};
    }
}