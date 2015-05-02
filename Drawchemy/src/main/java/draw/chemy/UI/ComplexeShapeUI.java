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
import android.widget.RadioButton;
import draw.chemy.creator.BasicShapesCreator;
import draw.chemy.creator.ComplexShapeCreator;
import org.al.chemy.R;

public class ComplexeShapeUI extends ASettingsGroupUI {

  private ComplexShapeCreator fCreator;
  private View fView;

  public ComplexeShapeUI(ComplexShapeCreator aCreator) {
    fCreator = aCreator;
  }

  @Override
  public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
    fView = aInflater.inflate(R.layout.complexe_shape_ui, aViewGroup);

    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int id = view.getId();
        switch (id) {
        case R.id.bones:
          fCreator.setShapeGroup(0);
          break;
        case R.id.parts:
          fCreator.setShapeGroup(1);
          break;
        case R.id.fluid:
          fCreator.setShapeGroup(2);
          break;
        case R.id.complexAll:
          fCreator.setShapeGroup(-1);
          break;
        }
      }
    };

    RadioButton button = (RadioButton) fView.findViewById(R.id.bones);
    button.setChecked(fCreator.getShapeGroupId() == 0);
    button.setOnClickListener(listener);

    button = (RadioButton) fView.findViewById(R.id.parts);
    button.setChecked(fCreator.getShapeGroupId() == 1);
    button.setOnClickListener(listener);

    button = (RadioButton) fView.findViewById(R.id.fluid);
    button.setChecked(fCreator.getShapeGroupId() == 2);
    button.setOnClickListener(listener);

    button = (RadioButton) fView.findViewById(R.id.complexAll);
    button.setChecked(fCreator.getShapeGroupId() == -1);
    button.setOnClickListener(listener);
  }
}

