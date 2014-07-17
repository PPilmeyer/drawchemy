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

import draw.chemy.DrawManager;

public class ExtraSettingsUI extends ASettingsGroupUIWithSeekBar {

    public ExtraSettingsUI(DrawManager aDrawManager) {
        super(createSeekSetting(aDrawManager));
    }

    private static SeekBarSettings[] createSeekSetting(final DrawManager aDrawManager) {

        SeekBarSettings kaleidoscopeSettings = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return DrawManager.MaxKaleidoscopeSec;
            }

            @Override
            public float getMin() {
                return DrawManager.MinKaleidoscopeSec;
            }

            @Override
            public float getCurrent() {
                return aDrawManager.getKaleidoscopeSec();
            }

            @Override
            public void setCurrent(float aValue) {
                aDrawManager.setKaleidoscopeSec((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.kaleidoscope;
            }
        };

        SeekBarSettings undos = new SeekBarSettings() {
            @Override
            public boolean isPercent() {
                return false;
            }

            @Override
            public float getMax() {
                return DrawManager.MaxAllowedUndos;
            }

            @Override
            public float getMin() {
                return DrawManager.MinAllowedUndos;
            }

            @Override
            public float getCurrent() {
                return aDrawManager.getAllowedUndos();
            }

            @Override
            public void setCurrent(float aValue) {
                aDrawManager.setAllowedUndos((int) aValue);
            }

            @Override
            public int getTextId() {
                return R.string.allowed_undos;
            }
        };


        return new SeekBarSettings[]{kaleidoscopeSettings, undos};
    }
}
