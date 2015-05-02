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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.al.chemy.R;

import draw.chemy.creator.StraightlineCreator;

public class StraightLineUI extends ASettingsGroupUI {

    private StraightlineCreator fCreator;

    public StraightLineUI(StraightlineCreator aStraightLineCreator) {
        fCreator = aStraightLineCreator;
    }

    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        View view = aInflater.inflate(R.layout.straight_line_ui, aViewGroup);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.snapping);
        checkBox.setChecked(fCreator.isSpanningFlag());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                fCreator.setSpanningFlag(isChecked);
            }
        });
    }
}
