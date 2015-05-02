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

import draw.chemy.creator.BallCreator;


public class BallUI extends ASettingsGroupUIWithSeekBar {

    public BallUI(BallCreator aBallCreator) {
        super(createSettings(aBallCreator));
    }

    private static SeekBarSettings[] createSettings(final BallCreator aBallCreator) {

        SeekBarSettings size = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return true;
            }

            @Override
            public float getMax() {
                return BallCreator.MAX_SIZE;
            }

            @Override
            public float getMin() {
                return BallCreator.MIN_SIZE;
            }

            @Override
            public float getCurrent() {
                return aBallCreator.getSize();
            }

            @Override
            public void setCurrent(float aValue) {
                aBallCreator.setSize(aValue);
            }

            @Override
            public int getTextId() {
                return R.string.size;
            }
        };
        SeekBarSettings radius = new SeekBarSettings() {

            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return BallCreator.MAX_RADIUS;
            }

            @Override
            public float getMin() {
                return BallCreator.MIN_RADIUS;
            }

            @Override
            public float getCurrent() {
                return aBallCreator.getRadius();
            }

            @Override
            public void setCurrent(float aValue) {
                aBallCreator.setRadius((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.radius;
            }
        };

        SeekBarSettings flow = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return BallCreator.MAX_FLOW;
            }

            @Override
            public float getMin() {
                return BallCreator.MIN_FLOW;
            }

            @Override
            public float getCurrent() {
                return aBallCreator.getFlow();
            }

            @Override
            public void setCurrent(float aValue) {
                aBallCreator.setFlow((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.flow;
            }
        };

        return new SeekBarSettings[]{size, radius, flow};
    }
}