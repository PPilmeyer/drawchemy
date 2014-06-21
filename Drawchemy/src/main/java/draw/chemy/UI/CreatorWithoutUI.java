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


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.al.chemy.R;

public class CreatorWithoutUI extends AbstractCreatorUI {
    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        aInflater.inflate(R.layout.empty_settings,aViewGroup);
    }
}
