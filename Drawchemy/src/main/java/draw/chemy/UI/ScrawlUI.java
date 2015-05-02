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

import draw.chemy.creator.ScrawlCreator;

public class ScrawlUI extends ASettingsGroupUIWithSeekBar {


    public ScrawlUI(ScrawlCreator aScrawlCreator) {
        super(createSettings(aScrawlCreator));
    }

    private static SeekBarSettings[] createSettings(final ScrawlCreator aScrawlCreator) {
        SeekBarSettings flow = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return ScrawlCreator.MAX_FLOW;
            }

            @Override
            public float getMin() {
                return ScrawlCreator.MIN_FLOW;
            }

            @Override
            public float getCurrent() {
                return aScrawlCreator.getFlow();
            }

            @Override
            public void setCurrent(float aValue) {
                aScrawlCreator.setFlow((int) aValue);
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
                return ScrawlCreator.MAX_NOISE;
            }

            @Override
            public float getMin() {
                return ScrawlCreator.MIN_NOISE;
            }

            @Override
            public float getCurrent() {
                return aScrawlCreator.getNoise();
            }

            @Override
            public void setCurrent(float aValue) {
                aScrawlCreator.setNoise(aValue);
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
                return ScrawlCreator.MAX_DETAIL;
            }

            @Override
            public float getMin() {
                return ScrawlCreator.MIN_DETAIL;
            }

            @Override
            public float getCurrent() {
                return aScrawlCreator.getDetail();
            }

            @Override
            public void setCurrent(float aValue) {
                aScrawlCreator.setDetail((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.detail;
            }
        };


        return new SeekBarSettings[]{noise, details, flow};
    }
}
