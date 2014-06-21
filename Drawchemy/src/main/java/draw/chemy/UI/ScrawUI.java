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
import draw.chemy.creator.ScrawCreator;

import static draw.chemy.creator.ScrawCreator.*;

public class ScrawUI extends AbstractCreatorUI {

    private ScrawCreator fScrawCreator;
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

    public ScrawUI(ScrawCreator aScrawCreator) {
        fScrawCreator = aScrawCreator;
    }

    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.scraw_ui,aViewGroup);
        fNoiseBar = (SeekBar) fView.findViewById(R.id.scraw_noise_seekbar);
        fDetailBar = (SeekBar) fView.findViewById(R.id.scraw_detail_seekbar);
        fFlowBar = (SeekBar) fView.findViewById(R.id.scraw_flow_seekbar);

        fNoiseBar.setMax(100);
        fDetailBar.setMax(MAX_DETAIL-MIN_DETAIL);
        fFlowBar.setMax(MAX_FLOW-MIN_FLOW);

        fFlowBar.setProgress(fScrawCreator.getFlow()-MIN_FLOW);

        float noise = (fScrawCreator.getNoise()-MIN_NOISE)*(100.f)/(MAX_NOISE-MIN_NOISE);
        fNoiseBar.setProgress((int) noise);

        fDetailBar.setProgress(fScrawCreator.getDetail()-MIN_DETAIL);


        fNoiseLabel = (TextView) fView.findViewById(R.id.scraw_noise_text);
        fDetailLabel = (TextView) fView.findViewById(R.id.scraw_detail_text);
        fFlowLabel = (TextView) fView.findViewById(R.id.scraw_flow_text);

        fNoiseTxt = aContext.getResources().getString(R.string.noise);
        fDetailTxt = aContext.getResources().getString(R.string.detail);
        fFlowTxt = aContext.getResources().getString(R.string.flow);

        setLabel(fNoiseLabel,fNoiseTxt,fScrawCreator.getNoise());
        setLabel(fDetailLabel,fDetailTxt,fScrawCreator.getDetail());
        setLabel(fFlowLabel,fFlowTxt,fScrawCreator.getFlow());

        Listener listener = new Listener();
        fNoiseBar.setOnSeekBarChangeListener(listener);
        fDetailBar.setOnSeekBarChangeListener(listener);
        fFlowBar.setOnSeekBarChangeListener(listener);
    }

    private class Listener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int noise = fNoiseBar.getProgress();
            fScrawCreator.setNoise(MIN_NOISE + (MAX_NOISE-MIN_NOISE)*(((float)noise)/100.f));
            setLabel(fNoiseLabel,fNoiseTxt,fScrawCreator.getNoise());

            int detail = fDetailBar.getProgress();
            fScrawCreator.setDetail(MIN_DETAIL + detail);
            setLabel(fDetailLabel,fDetailTxt,fScrawCreator.getDetail());

            int flow = fFlowBar.getProgress();
            fScrawCreator.setFlow(MIN_FLOW + flow);
            setLabel(fFlowLabel,fFlowTxt,fScrawCreator.getFlow());

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
