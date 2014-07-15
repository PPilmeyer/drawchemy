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

import draw.chemy.creator.BallCreator;

import static draw.chemy.creator.BallCreator.MAX_FLOW;
import static draw.chemy.creator.BallCreator.MAX_RADIUS;
import static draw.chemy.creator.BallCreator.MAX_SIZE;
import static draw.chemy.creator.BallCreator.MIN_FLOW;
import static draw.chemy.creator.BallCreator.MIN_RADIUS;
import static draw.chemy.creator.BallCreator.MIN_SIZE;


public class BallUI extends ASettingsGroupUI {

    private BallCreator fBallCreator;
    private View fView;

    private SeekBar fSizeBar;
    private SeekBar fFlowBar;
    private SeekBar fRadiusBar;

    private TextView fSizeLabel;
    private TextView fFlowLabel;
    private TextView fRadiusLabel;

    private String fSizeTxt;
    private String fFlowTxt;
    private String fRadiusTxt;

    public BallUI(BallCreator aBallCreator) {
        fBallCreator = aBallCreator;
    }


    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.ball_ui, aViewGroup);
        fSizeBar = (SeekBar) fView.findViewById(R.id.ball_size_seekbar);
        fFlowBar = (SeekBar) fView.findViewById(R.id.ball_flow_seekbar);
        fRadiusBar = (SeekBar) fView.findViewById(R.id.ball_radius_seekbar);

        fSizeBar.setMax(100);
        fFlowBar.setMax(MAX_FLOW - MIN_FLOW);
        fRadiusBar.setMax(MAX_RADIUS - MIN_RADIUS);

        float size = (fBallCreator.getSize() - MIN_SIZE) * (100.f) / (MAX_SIZE - MIN_SIZE);
        fSizeBar.setProgress((int) size);

        fFlowBar.setProgress(fBallCreator.getFlow() - MIN_FLOW);

        fRadiusBar.setProgress(fBallCreator.getRadius() - MIN_RADIUS);

        fSizeLabel = (TextView) fView.findViewById(R.id.ball_size_text);
        fFlowLabel = (TextView) fView.findViewById(R.id.ball_flow_text);
        fRadiusLabel = (TextView) fView.findViewById(R.id.ball_radius_text);

        fSizeTxt = aContext.getResources().getString(R.string.size);
        fFlowTxt = aContext.getResources().getString(R.string.flow);
        fRadiusTxt = aContext.getResources().getString(R.string.radius);

        setLabel(fSizeLabel, fSizeTxt, fBallCreator.getSize());
        setLabel(fFlowLabel, fFlowTxt, fBallCreator.getFlow());
        setLabel(fRadiusLabel, fRadiusTxt, fBallCreator.getRadius());

        Listener listener = new Listener();
        fSizeBar.setOnSeekBarChangeListener(listener);
        fFlowBar.setOnSeekBarChangeListener(listener);
        fRadiusBar.setOnSeekBarChangeListener(listener);

    }

    private class Listener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int size = fSizeBar.getProgress();
            fBallCreator.setSize(MIN_SIZE + (MAX_SIZE - MIN_SIZE) * (((float) size) / 100.f));
            setLabel(fSizeLabel, fSizeTxt, fBallCreator.getSize());

            int flow = fFlowBar.getProgress();
            fBallCreator.setFlow(MIN_FLOW + flow);
            setLabel(fFlowLabel, fFlowTxt, fBallCreator.getFlow());

            int radius = fRadiusBar.getProgress();
            fBallCreator.setRadius((MIN_RADIUS + radius));
            setLabel(fRadiusLabel, fRadiusTxt, fBallCreator.getRadius());

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
