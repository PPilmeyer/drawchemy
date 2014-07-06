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

import draw.chemy.creator.PaintBrushCreator;

import static draw.chemy.creator.PaintBrushCreator.MAX_BRISLTES_NUM;
import static draw.chemy.creator.PaintBrushCreator.MIN_BRISLTES_NUM;

public class PaintBrushUI extends AbstractCreatorUI {

    private PaintBrushCreator fCreator;
    private View fView;
    private SeekBar fBristlesSeekBar;
    private TextView fBristleLabel;
    private String fBristlesTxt;

    public PaintBrushUI(PaintBrushCreator aCreator) {
        fCreator = aCreator;
    }

    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.paintbrush_ui, aViewGroup);
        fBristlesSeekBar = (SeekBar) fView.findViewById(R.id.bristle_seekbar);
        fBristleLabel = (TextView) fView.findViewById(R.id.bristle_text);

        fBristlesSeekBar.setMax(MAX_BRISLTES_NUM - MIN_BRISLTES_NUM);
        fBristlesSeekBar.setProgress(fCreator.getBristlesNumber() - MIN_BRISLTES_NUM);

        fBristlesTxt = aContext.getResources().getString(R.string.bristles);

        setLabel(fBristleLabel, fBristlesTxt, fCreator.getBristlesNumber());

        fBristlesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int bristlesNumber = fCreator.getBristlesNumber();
                fCreator.setBristlesNumber(MIN_BRISLTES_NUM + bristlesNumber);
                setLabel(fBristleLabel, fBristlesTxt, fCreator.getBristlesNumber());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
