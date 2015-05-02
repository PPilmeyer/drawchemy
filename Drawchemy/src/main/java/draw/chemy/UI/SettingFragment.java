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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.al.chemy.R;

public class SettingFragment extends Fragment {

  private RelativeLayout fBrushLayout;
  private RelativeLayout fGeneralLayout;
  private LayoutInflater fLayoutInflater = null;
  private ASettingsGroupUI fBrushSettingUI;
  private ASettingsGroupUI fGeneralSettingUI;

  public SettingFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.settinglayout, container, false);
    fBrushLayout = (RelativeLayout) view.findViewById(R.id.brushSettingLayout);
    fGeneralLayout = (RelativeLayout) view.findViewById(R.id.generalSettingLayout);

    View b = view.findViewById(R.id.backButton);
    b.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getFragmentManager().popBackStack();
      }
    });

    fLayoutInflater = inflater;

    fBrushSettingUI.fillView(fLayoutInflater, fBrushLayout, getActivity());
    fGeneralSettingUI.fillView(fLayoutInflater, fGeneralLayout, getActivity());
    view.invalidate();

    return view;
  }

  public void setBrushSetting(ASettingsGroupUI aSettingsGroupUI) {
    fBrushSettingUI = aSettingsGroupUI;
    if (getView() != null) {
      fBrushLayout.removeAllViewsInLayout();
      fBrushSettingUI.fillView(fLayoutInflater, fBrushLayout, getActivity());
      getView().invalidate();
    }
  }

  public void setGeneralSetting(ASettingsGroupUI aSettingsGroupUI) {
    fGeneralSettingUI = aSettingsGroupUI;
    if (getView() != null) {
      fGeneralLayout.removeAllViewsInLayout();
      fGeneralSettingUI.fillView(fLayoutInflater, fGeneralLayout, getActivity());
      getView().invalidate();
    }
  }

  @Override
  public void onDestroyView() {
    if (getView() != null) {
      fGeneralLayout.removeAllViewsInLayout();
      fBrushLayout.removeAllViewsInLayout();
    }
    super.onDestroyView();
  }

  public void close() {
    fGeneralSettingUI = null;
    fBrushSettingUI = null;
  }
}
