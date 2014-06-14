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

package com.google.code.drawchemy.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.al.chemy.R;

import com.google.code.drawchemy.creator.RibbonCreator;

import static com.google.code.drawchemy.creator.RibbonCreator.*;

public class RibbonUI extends AbstractCreatorUI {

    private RibbonCreator fRibbonCreator;
    private View fView;

    private SeekBar fGravityBar;
    private SeekBar fFrictionBar;
    private SeekBar fSpacingBar;

    private TextView fGravityLabel;
    private TextView fFrictionLabel;
    private TextView fSpacingLabel;

    private String fGravityTxt;
    private String fFrictionTxt;
    private String fSpacingTxt;

    public RibbonUI(RibbonCreator aRibbonCreator) {
        fRibbonCreator = aRibbonCreator;
    }


    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.ribbon_ui, aViewGroup);
        fGravityBar = (SeekBar) fView.findViewById(R.id.ribbon_gravity_seekbar);
        fFrictionBar = (SeekBar) fView.findViewById(R.id.ribbon_friction_seekbar);
        fSpacingBar = (SeekBar) fView.findViewById(R.id.ribbon_spacing_seekbar);

        fGravityBar.setMax(100);
        fFrictionBar.setMax(100);
        fSpacingBar.setMax(MAX_SPACING - MIN_SPACING);

        float gravity = (fRibbonCreator.getGravity()-MIN_GRAVITY)*(100.f)/(MAX_GRAVITY-MIN_GRAVITY);
        fGravityBar.setProgress((int) gravity);

        float friction = (fRibbonCreator.getFriction()-MIN_FRICTION)*(100.f)/(MAX_FRICTION-MIN_FRICTION);
        fFrictionBar.setProgress((int) friction);

        fSpacingBar.setProgress(fRibbonCreator.getSpacing()-MIN_SPACING);

        fGravityLabel = (TextView) fView.findViewById(R.id.ribbon_gravity__text);
        fFrictionLabel = (TextView) fView.findViewById(R.id.ribbon_friction_text);
        fSpacingLabel = (TextView) fView.findViewById(R.id.ribbon_spacing__text);

        fGravityTxt = aContext.getResources().getString(R.string.gravity);
        fFrictionTxt = aContext.getResources().getString(R.string.friction);
        fSpacingTxt = aContext.getResources().getString(R.string.spacing);

        setLabel(fGravityLabel, fGravityTxt, fRibbonCreator.getGravity());
        setLabel(fFrictionLabel, fFrictionTxt, fRibbonCreator.getFriction());
        setLabel(fSpacingLabel, fSpacingTxt, fRibbonCreator.getSpacing());

        Listener listener = new Listener();
        fGravityBar.setOnSeekBarChangeListener(listener);
        fFrictionBar.setOnSeekBarChangeListener(listener);
        fSpacingBar.setOnSeekBarChangeListener(listener);

    }

    private class Listener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int gravity = fGravityBar.getProgress();
            fRibbonCreator.setGravity(MIN_GRAVITY + (MAX_GRAVITY - MIN_GRAVITY) * (((float) gravity) / 100.f));
            setLabel(fGravityLabel, fGravityTxt, fRibbonCreator.getGravity());

            int friction = fFrictionBar.getProgress();
            fRibbonCreator.setFriction(MIN_FRICTION + (MAX_FRICTION - MIN_FRICTION) * (((float) friction) / 100.f));
            setLabel(fFrictionLabel, fFrictionTxt, fRibbonCreator.getFriction());

            int Spacing = fSpacingBar.getProgress();
            fRibbonCreator.setSpacing((MIN_SPACING + Spacing));
            setLabel(fSpacingLabel, fSpacingTxt, fRibbonCreator.getSpacing());

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
