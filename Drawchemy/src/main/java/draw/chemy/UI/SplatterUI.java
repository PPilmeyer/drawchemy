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

import draw.chemy.creator.SplatterCreator;

import static draw.chemy.creator.SplatterCreator.*;

public class SplatterUI extends AbstractCreatorUI {

    private SplatterCreator fSplatterCreator;
    private View fView;

    private SeekBar fsizeBar;
    private SeekBar fdripsBar;

    private TextView fsizeLabel;
    private TextView fdripsLabel;

    private String fsizeTxt;
    private String fdripsTxt;

    public SplatterUI(SplatterCreator aSplatterCreator) {
        fSplatterCreator = aSplatterCreator;
    }

    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.splatter_ui, aViewGroup);
        fsizeBar = (SeekBar) fView.findViewById(R.id.splatter_size_seekbar);
        fdripsBar = (SeekBar) fView.findViewById(R.id.splatter_drips_seekbar);

        fsizeBar.setMax(100);
        fdripsBar.setMax(MAX_DRIPS - MIN_DRIPS);

        fdripsBar.setProgress(fSplatterCreator.getDrips()-MIN_DRIPS);

        float size = (fSplatterCreator.getSize()-MIN_SIZE)*(100.f)/(MAX_SIZE-MIN_SIZE);
        fsizeBar.setProgress((int) size);

        fsizeLabel = (TextView) fView.findViewById(R.id.splatter_size_text);
        fdripsLabel = (TextView) fView.findViewById(R.id.splatter_drips_text);

        fsizeTxt = aContext.getResources().getString(R.string.size);
        fdripsTxt = aContext.getResources().getString(R.string.drips);

        setLabel(fsizeLabel, fsizeTxt, fSplatterCreator.getSize());
        setLabel(fdripsLabel, fdripsTxt, fSplatterCreator.getDrips());

        Listener listener = new Listener();
        fsizeBar.setOnSeekBarChangeListener(listener);
        fdripsBar.setOnSeekBarChangeListener(listener);

    }

    private class Listener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int size = fsizeBar.getProgress();
            fSplatterCreator.setSize(MIN_SIZE + (MAX_SIZE - MIN_SIZE) * (((float) size) / 100.f));
            setLabel(fsizeLabel, fsizeTxt, fSplatterCreator.getSize());

            int drips = fdripsBar.getProgress();
            fSplatterCreator.setDrips((MIN_DRIPS + drips));
            setLabel(fdripsLabel, fdripsTxt, fSplatterCreator.getDrips());

            fView.invalidate();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
