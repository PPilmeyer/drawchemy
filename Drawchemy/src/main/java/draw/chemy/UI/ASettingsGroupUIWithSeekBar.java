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

public abstract class ASettingsGroupUIWithSeekBar extends ASettingsGroupUI {

    private SeekBar[] fSeekBars;
    private TextView[] fLabels;
    private String[] fTexts;

    private SeekBarSettings[] fSeekBarsSettings;
    protected View fView;

    private int[] SeekBarIDs = {R.id.first_seekbar, R.id.second_seekbar, R.id.third_seekbar};
    private int[] LabelIDs = {R.id.first_label, R.id.second_label, R.id.third_label};


    private int fSize;
    private static int MAX_SIZE = 3;


    public ASettingsGroupUIWithSeekBar(SeekBarSettings... aSeekBarSettings) {
        fSeekBarsSettings = aSeekBarSettings;
        fSize = fSeekBarsSettings.length;
        fSeekBars = new SeekBar[fSize];
        fLabels = new TextView[fSize];
        fTexts = new String[fSize];
    }

    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.seekbars, aViewGroup);
        Listener listener = new Listener();
        for (int i = 0; i < fSize; i++) {
            SeekBar seekBar = (SeekBar) fView.findViewById(SeekBarIDs[i]);
            TextView textView = (TextView) fView.findViewById(LabelIDs[i]);

            SeekBarSettings currentSetting = fSeekBarsSettings[i];
            fTexts[i] = aContext.getResources().getString(currentSetting.getTextId());

            if (currentSetting.isPercent()) {
                seekBar.setMax(100);
                float currentValue = 100.f * (currentSetting.getCurrent() - currentSetting.getMin()) /
                        (currentSetting.getMax() - currentSetting.getMin());
                seekBar.setProgress((int) currentValue);
                setLabel(textView, fTexts[i], currentSetting.getCurrent());
            } else {
                float span = currentSetting.getMax() - currentSetting.getMin();
                seekBar.setMax((int) span);
                float currentValue = currentSetting.getCurrent() - currentSetting.getMin();
                seekBar.setProgress((int) currentValue);
                setLabel(textView, fTexts[i], (int) currentSetting.getCurrent());
            }
            seekBar.setOnSeekBarChangeListener(listener);
            fSeekBars[i] = seekBar;
            fLabels[i] = textView;
        }

        for (int i = fSize; i < MAX_SIZE; i++) {
            SeekBar seekBar = (SeekBar) fView.findViewById(SeekBarIDs[i]);
            TextView textView = (TextView) fView.findViewById(LabelIDs[i]);
            seekBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }

    public interface SeekBarSettings {

        public boolean isPercent();

        public float getMax();

        public float getMin();

        public float getCurrent();

        public void setCurrent(float aValue);

        public int getTextId();
    }

    private class Listener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar aSeekBar, int aProgress, boolean b) {
            for (int i = 0; i < fSize; i++) {
                SeekBar seekBar = fSeekBars[i];
                TextView textView = fLabels[i];

                SeekBarSettings currentSetting = fSeekBarsSettings[i];
                if (currentSetting.isPercent()) {
                    float span = currentSetting.getMax() - currentSetting.getMin();
                    float currentValue = span * ((float) seekBar.getProgress());
                    currentValue /= 100.f;
                    currentValue += currentSetting.getMin();
                    currentSetting.setCurrent(currentValue);
                    setLabel(textView, fTexts[i], currentSetting.getCurrent());
                } else {

                    float currentValue = seekBar.getProgress() + currentSetting.getMin();
                    currentSetting.setCurrent(currentValue);
                    setLabel(textView, fTexts[i], (int) currentSetting.getCurrent());
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
