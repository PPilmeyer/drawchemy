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

import draw.chemy.creator.XShapeCreator;

import static draw.chemy.creator.XShapeCreator.MAX_DETAIL;
import static draw.chemy.creator.XShapeCreator.MAX_FLOW;
import static draw.chemy.creator.XShapeCreator.MAX_NOISE;
import static draw.chemy.creator.XShapeCreator.MIN_DETAIL;
import static draw.chemy.creator.XShapeCreator.MIN_FLOW;
import static draw.chemy.creator.XShapeCreator.MIN_NOISE;

public class XShapeUI extends ASettingsGroupUI {

    private final XShapeCreator fXShapeCreator;
    private View fView;
    private SeekBar fNoiseBar;
    private SeekBar fDetailBar;
    private SeekBar fFlowBar;

    private TextView fNoiseLabel;
    private TextView fDetailLabel;
    private TextView fFlowLabel;

    private String fNoiseTxt;
    private String fDetailTxt;
    private String fFlowTxt;

    public XShapeUI(XShapeCreator aXShapeCreator) {
        fXShapeCreator = aXShapeCreator;
    }

    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.xshape_ui, aViewGroup);
        fNoiseBar = (SeekBar) fView.findViewById(R.id.xshape_noise_seekbar);
        fDetailBar = (SeekBar) fView.findViewById(R.id.xshape_detail_seekbar);
        fFlowBar = (SeekBar) fView.findViewById(R.id.xshape_flow_seekbar);

        fNoiseBar.setMax(100);
        fDetailBar.setMax(MAX_DETAIL - MIN_DETAIL);
        fFlowBar.setMax(MAX_FLOW - MIN_FLOW);

        fFlowBar.setProgress(fXShapeCreator.getFlow() - MIN_FLOW);

        float noise = (fXShapeCreator.getNoise() - MIN_NOISE) * (100.f) / (MAX_NOISE - MIN_NOISE);
        fNoiseBar.setProgress((int) noise);

        fDetailBar.setProgress(fXShapeCreator.getDetail() - MIN_DETAIL);

        fNoiseLabel = (TextView) fView.findViewById(R.id.xshape_noise_text);
        fDetailLabel = (TextView) fView.findViewById(R.id.xshape_detail_text);
        fFlowLabel = (TextView) fView.findViewById(R.id.xshape_flow_text);

        fNoiseTxt = aContext.getResources().getString(R.string.noise);
        fDetailTxt = aContext.getResources().getString(R.string.detail);
        fFlowTxt = aContext.getResources().getString(R.string.flow);

        setLabel(fNoiseLabel, fNoiseTxt, fXShapeCreator.getNoise());
        setLabel(fDetailLabel, fDetailTxt, fXShapeCreator.getDetail());
        setLabel(fFlowLabel, fFlowTxt, fXShapeCreator.getFlow());

        Listener listener = new Listener();
        fNoiseBar.setOnSeekBarChangeListener(listener);
        fDetailBar.setOnSeekBarChangeListener(listener);
        fFlowBar.setOnSeekBarChangeListener(listener);
    }

    private class Listener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int noise = fNoiseBar.getProgress();
            fXShapeCreator.setNoise(MIN_NOISE + (MAX_NOISE - MIN_NOISE) * (((float) noise) / 100.f));
            setLabel(fNoiseLabel, fNoiseTxt, fXShapeCreator.getNoise());

            int detail = fDetailBar.getProgress();
            fXShapeCreator.setDetail(MIN_DETAIL + detail);
            setLabel(fDetailLabel, fDetailTxt, fXShapeCreator.getDetail());

            int flow = fFlowBar.getProgress();
            fXShapeCreator.setFlow(MIN_FLOW + flow);
            setLabel(fFlowLabel, fFlowTxt, fXShapeCreator.getFlow());

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
