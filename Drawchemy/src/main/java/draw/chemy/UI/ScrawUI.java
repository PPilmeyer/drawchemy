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

import draw.chemy.creator.ScrawCreator;

public class ScrawUI extends ASettingsGroupUIWithSeekBar {


    public ScrawUI(ScrawCreator aScrawCreator) {
        super(createSettings(aScrawCreator));
    }

    public static SeekBarSettings[] createSettings(final ScrawCreator aScrawCreator) {
        SeekBarSettings flow = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return ScrawCreator.MAX_FLOW;
            }

            @Override
            public float getMin() {
                return ScrawCreator.MIN_FLOW;
            }

            @Override
            public float getCurrent() {
                return aScrawCreator.getFlow();
            }

            @Override
            public void setCurrent(float aValue) {
                aScrawCreator.setFlow((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.flow;
            }
        };

        SeekBarSettings noise = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return true;
            }

            @Override
            public float getMax() {
                return ScrawCreator.MAX_NOISE;
            }

            @Override
            public float getMin() {
                return ScrawCreator.MIN_NOISE;
            }

            @Override
            public float getCurrent() {
                return aScrawCreator.getNoise();
            }

            @Override
            public void setCurrent(float aValue) {
                aScrawCreator.setNoise(aValue);
            }

            @Override
            public int getTextId() {
                return R.string.noise;
            }
        };

        SeekBarSettings details = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return ScrawCreator.MAX_DETAIL;
            }

            @Override
            public float getMin() {
                return ScrawCreator.MIN_DETAIL;
            }

            @Override
            public float getCurrent() {
                return aScrawCreator.getDetail();
            }

            @Override
            public void setCurrent(float aValue) {
                aScrawCreator.setDetail((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.detail;
            }
        };


        return new SeekBarSettings[]{noise, details, flow};
    }
}
