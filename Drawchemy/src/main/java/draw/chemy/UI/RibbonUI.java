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

import draw.chemy.creator.RibbonCreator;

public class RibbonUI extends ASettingsGroupUIWithSeekBar {
    public RibbonUI(RibbonCreator aRibbonCreator) {
        super(createSettings(aRibbonCreator));
    }

    private static SeekBarSettings[] createSettings(final RibbonCreator aRibbonCreator) {
        SeekBarSettings gravity = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return true;
            }

            @Override
            public float getMax() {
                return RibbonCreator.MAX_GRAVITY;
            }

            @Override
            public float getMin() {
                return RibbonCreator.MIN_GRAVITY;
            }

            @Override
            public float getCurrent() {
                return aRibbonCreator.getGravity();
            }

            @Override
            public void setCurrent(float aValue) {
                aRibbonCreator.setGravity(aValue);
            }

            @Override
            public int getTextId() {
                return R.string.gravity;
            }
        };

        SeekBarSettings friction = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return true;
            }

            @Override
            public float getMax() {
                return RibbonCreator.MAX_FRICTION;
            }

            @Override
            public float getMin() {
                return RibbonCreator.MIN_FRICTION;
            }

            @Override
            public float getCurrent() {
                return aRibbonCreator.getFriction();
            }

            @Override
            public void setCurrent(float aValue) {
                aRibbonCreator.setFriction(aValue);
            }

            @Override
            public int getTextId() {
                return R.string.friction;
            }
        };

        SeekBarSettings spacing = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return RibbonCreator.MAX_SPACING;
            }

            @Override
            public float getMin() {
                return RibbonCreator.MIN_SPACING;
            }

            @Override
            public float getCurrent() {
                return aRibbonCreator.getSpacing();
            }

            @Override
            public void setCurrent(float aValue) {
                aRibbonCreator.setSpacing((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.spacing;
            }
        };


        return new SeekBarSettings[]{gravity, friction, spacing};
    }
}