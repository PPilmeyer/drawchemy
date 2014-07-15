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
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.al.chemy.R;

import draw.chemy.DrawManager;

import static draw.chemy.DrawManager.MaxAllowedUndos;
import static draw.chemy.DrawManager.MaxKaleidoscopeSec;
import static draw.chemy.DrawManager.MinAllowedUndos;
import static draw.chemy.DrawManager.MinKaleidoscopeSec;

public class ExtraSettingsUI extends ASettingsGroupUI {

    private DrawManager fDrawingManager;

    private View fView;

    private SeekBar fKaleidoscopeBar;
    private SeekBar fUndosBar;


    private TextView fKaleidoscopeLabel;
    private TextView fUndosLabel;


    private String fKaleidoscopeTxt;
    private String fUndosTxt;


    public ExtraSettingsUI(DrawManager aDrawingManager) {
        fDrawingManager = aDrawingManager;
    }


    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.extra_settings, aViewGroup);

        fKaleidoscopeBar = (SeekBar) fView.findViewById(R.id.kaleidoscope_seekbar);
        fUndosBar = (SeekBar) fView.findViewById(R.id.undo_seekbar);

        fKaleidoscopeBar.setMax(MaxKaleidoscopeSec - MinKaleidoscopeSec);
        fKaleidoscopeBar.setProgress(fDrawingManager.getKaleidoscopeSec() - MinKaleidoscopeSec);

        fUndosBar.setMax(MaxAllowedUndos - MinAllowedUndos);
        fUndosBar.setProgress(fDrawingManager.getAllowedUndos() - MinAllowedUndos);


        fKaleidoscopeLabel = (TextView) fView.findViewById(R.id.kaleidoscope_txt);
        fUndosLabel = (TextView) fView.findViewById(R.id.undo_txt);

        fKaleidoscopeTxt = aContext.getResources().getString(R.string.kaleidoscope_option);
        fUndosTxt = aContext.getResources().getString(R.string.allowed_undos);

        setLabel(fKaleidoscopeLabel, fKaleidoscopeTxt, fDrawingManager.getKaleidoscopeSec());
        setLabel(fUndosLabel, fUndosTxt, fDrawingManager.getAllowedUndos());

        Listener listener = new Listener();

        fKaleidoscopeBar.setOnSeekBarChangeListener(listener);
        fUndosBar.setOnSeekBarChangeListener(listener);
    }

    private class Listener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            int allowedUndos = fUndosBar.getProgress();
            fDrawingManager.setAllowedUndos(allowedUndos + MinAllowedUndos);
            setLabel(fUndosLabel, fUndosTxt, fDrawingManager.getAllowedUndos());

            int kaleidoscope = fKaleidoscopeBar.getProgress();
            fDrawingManager.setKaleidoscopeSec(kaleidoscope + MinKaleidoscopeSec);
            setLabel(fKaleidoscopeLabel, fKaleidoscopeTxt, fDrawingManager.getKaleidoscopeSec());
            fView.invalidate();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    public void close() {
        super.close();
        fDrawingManager = null;
    }
}
