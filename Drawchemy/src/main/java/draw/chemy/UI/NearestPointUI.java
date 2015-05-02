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

package draw.chemy.UI;

import org.al.chemy.R;

import draw.chemy.creator.NearestPointLineCreator;

public class NearestPointUI extends ASettingsGroupUIWithSeekBar {

    public NearestPointUI(NearestPointLineCreator aNearestPointLineCreator) {
        super(createSettings(aNearestPointLineCreator));
    }

    private static SeekBarSettings[] createSettings(final NearestPointLineCreator aNearestPointLineCreator) {

        SeekBarSettings dist = new SeekBarSettings() {

            @Override
            public boolean isPercent() {
                return true;
            }

            @Override
            public float getMax() {
                return NearestPointLineCreator.MAX_DIST_LIM;
            }

            @Override
            public float getMin() {
                return NearestPointLineCreator.MIN_DIST_LIM;
            }

            @Override
            public float getCurrent() {
                return aNearestPointLineCreator.getDistLim();
            }

            @Override
            public void setCurrent(float aValue) {
                aNearestPointLineCreator.setDistLim(aValue);
            }

            @Override
            public int getTextId() {
                return R.string.maxDist;
            }
        };


        return new SeekBarSettings[]{dist};
    }

}
