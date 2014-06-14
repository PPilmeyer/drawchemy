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

package com.google.code.drawchemy;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.al.chemy.R;

import com.google.code.drawchemy.UI.AbstractCreatorUI;
import com.google.code.drawchemy.UI.RibbonUI;
import com.google.code.drawchemy.UI.ScrawUI;
import com.google.code.drawchemy.UI.XShapeUI;
import com.google.code.drawchemy.UI.CreatorSettingsFragment;
import com.google.code.drawchemy.UI.CreatorWithoutUI;
import com.google.code.drawchemy.UI.SplatterUI;
import com.google.code.drawchemy.color.ColorUIFragment;
import com.google.code.drawchemy.color.RoundIconGenerator;
import com.google.code.drawchemy.creator.LineCreator;
import com.google.code.drawchemy.creator.RibbonCreator;
import com.google.code.drawchemy.creator.ScrawCreator;
import com.google.code.drawchemy.creator.SplatterCreator;
import com.google.code.drawchemy.creator.XShapeCreator;

public class DrawActivity extends Activity {

    private DrawManager fManager;

    private ZoomPanDrawingView fDrawingView;
    private FragmentManager fFragmentManager;
    private LinearLayout fSettingContainer;

    CreatorSettingsFragment fCreatorSettings;
    ColorUIFragment fColorSettings;

    private AbstractCreatorUI fEmptySettings;
    private ActionBar fActionBar;
    private Fragment fSecondaryFragment;

    private RoundIconGenerator fFirstIconGenerator;
    private RoundIconGenerator fSecondIconGenerator;

    private ActionBar.Tab fFirstColor;
    private ActionBar.Tab fSecondColor;

    private ImageButton fStyleButton;
    private ImageButton fGradientButton;
    private ImageButton fHorizontalButton;
    private ImageButton fVerticalButton;

    private MenuItem fZoomItem;

    private View fBottomBar;
    private ImageButton fShowUI;
    private boolean fTransactionFlag = false;

    private Context fContext;

    private static float sAlphaHide = 0.3f;
    private static int sDurationAnimation = 600;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fContext = this;

        setContentView(R.layout.main);

        fShowUI = (ImageButton) findViewById(R.id.i_show_ui);
        fShowUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getClick(view.getId());
            }
        });

        fShowUI.setVisibility(View.GONE);

        fDrawingView = (ZoomPanDrawingView) findViewById(R.id.drawingView);
        fManager = fDrawingView.getCanvasManager();

        fManager.addTool(0, new LineCreator(fManager));
        fManager.addTool(1, new ScrawCreator(fManager));
        fManager.addTool(2, new SplatterCreator(fManager));
        fManager.addTool(3, new RibbonCreator(fManager));
        fManager.addTool(4, new XShapeCreator(fManager));

        fManager.setCurrentTool(0);

        ViewGroup drawingContainer = (ViewGroup) findViewById(R.id.drawingContainer);
        drawingContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                fManager.redraw();
            }
        });

        View.OnTouchListener dummyTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        };

        fSettingContainer = (LinearLayout) findViewById(R.id.settingContainer);
        fSettingContainer.setOnTouchListener(dummyTouchListener);
        fSettingContainer.setVisibility(View.GONE);

        SeekBar seekBar = (SeekBar) findViewById(R.id.i_stroke_weight);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fManager.setStrokeWeight(1.f + ((float) seekBar.getProgress()) / 10.f);
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getClick(view.getId());
            }
        };

        findViewById(R.id.i_flip_v).setOnClickListener(clickListener);
        findViewById(R.id.i_flip_h).setOnClickListener(clickListener);
        findViewById(R.id.i_flip_gradient).setOnClickListener(clickListener);
        findViewById(R.id.i_flip_style).setOnClickListener(clickListener);

        fFragmentManager = getFragmentManager();
        fFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setLayout();
            }
        });

        fEmptySettings = new CreatorWithoutUI();

        fBottomBar = findViewById(R.id.bottom_bar);
        fBottomBar.setOnTouchListener(dummyTouchListener);

        fCreatorSettings = new CreatorSettingsFragment();
        fCreatorSettings.getNewCreator(fEmptySettings);

        fColorSettings = new ColorUIFragment();
        fColorSettings.setColor(fManager.getMainColor(), false);

        fColorSettings.addHueSwitchListener(new ColorUIFragment.HueSwitchListener() {
            @Override
            public void stateChanged(boolean isEnabled) {
                fManager.setColorSwitchFlag(isEnabled);
            }

            @Override
            public void amplitudeChanged(float aAmplitude) {
                fManager.setColorVariation(aAmplitude);
            }
        });

        fFirstIconGenerator = new RoundIconGenerator(this);
        fSecondIconGenerator = new RoundIconGenerator(this);

        fStyleButton = (ImageButton) findViewById(R.id.i_flip_style);
        fGradientButton = (ImageButton) findViewById(R.id.i_flip_gradient);

        fHorizontalButton = (ImageButton) findViewById(R.id.i_flip_h);
        fVerticalButton = (ImageButton) findViewById(R.id.i_flip_v);

        View.OnLongClickListener descriptionClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (view.getContentDescription() != null) {
                    CharSequence txt = view.getContentDescription();
                    Toast.makeText(fContext, txt, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        };

        fStyleButton.setOnLongClickListener(descriptionClick);
        fGradientButton.setOnLongClickListener(descriptionClick);
        fHorizontalButton.setOnLongClickListener(descriptionClick);
        fVerticalButton.setOnLongClickListener(descriptionClick);

        fHorizontalButton.setAlpha(sAlphaHide);
        fVerticalButton.setAlpha(sAlphaHide);

        fActionBar = getActionBar();
        if (fActionBar != null) {
            fActionBar.setDisplayShowTitleEnabled(false);
            fActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            fFirstColor = fActionBar.newTab();
            fSecondColor = fActionBar.newTab();
            fFirstColor.setIcon(fFirstIconGenerator.getIcon(fManager.getMainColor(), true));
            ColorOnTabListener listener = new ColorOnTabListener();

            fFirstColor.setTabListener(listener);

            fActionBar.addTab(fFirstColor);

            fSecondColor.setIcon(fSecondIconGenerator.getIcon(fManager.getSubColor(), false));
            fSecondColor.setTabListener(listener);

            fActionBar.addTab(fSecondColor);

            fActionBar.setDisplayUseLogoEnabled(false);
            fActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void addFragment(Fragment aFragment) {

        fSecondaryFragment = aFragment;
        if (!aFragment.isAdded()) {
            fTransactionFlag = true;
            FragmentTransaction transaction = fFragmentManager.beginTransaction();
            Fragment f = fFragmentManager.findFragmentById(R.id.settingContainer);
            if (f != null) {
                transaction.replace(R.id.settingContainer, aFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fFragmentManager.popBackStack();
                transaction.addToBackStack(null);
            } else {
                transaction.add(R.id.settingContainer, aFragment);
                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            }
            transaction.commit();
            fFragmentManager.executePendingTransactions();
            fTransactionFlag = false;
            setLayout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.i_overflow);
        SubMenu subMenu;
        if (item != null) {
            subMenu = item.getSubMenu();
            getMenuInflater().inflate(R.menu.sub_menu, subMenu);
        }

        item = menu.findItem(R.id.i_creators);

        if (item != null) {
            subMenu = item.getSubMenu();
            getMenuInflater().inflate(R.menu.creators_menu, subMenu);
        }

        fZoomItem = menu.findItem(R.id.i_zoom);
        if (fZoomItem != null && fZoomItem.getIcon() != null) {
            fZoomItem.getIcon().setAlpha(127);
        }
        return true;
    }

    private void setLayout() {
        if (fTransactionFlag) {
            return;
        }
        if (fSecondaryFragment == null || !fSecondaryFragment.isAdded()) {
            ObjectAnimator translation = ObjectAnimator.ofFloat(fSettingContainer,
                    "translationX",
                    fSettingContainer.getWidth());
            translation.setDuration(sDurationAnimation);
            translation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    fSettingContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            translation.start();
        } else {
            fSettingContainer.setTranslationX(0.f);
            fSettingContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getClick(item.getItemId());
        return true;
    }

    private void getClick(int id) {
        switch (id) {
            case R.id.i_undo: {
                fManager.undo();
                break;
            }
            case R.id.i_redo: {
                fManager.redo();
                break;
            }
            case R.id.i_reset_view: {
                fDrawingView.resetZoomPan();
                fDrawingView.setEnabled(false);
                if (fZoomItem.getIcon() != null) {
                    fZoomItem.getIcon().setAlpha(127);
                }
                break;
            }
            case R.id.i_zoom: {
                fDrawingView.switchEnabled();
                if (fZoomItem.getIcon() != null) {
                    if (fDrawingView.isEnable()) {
                        fZoomItem.getIcon().setAlpha(255);
                    } else {
                        fZoomItem.getIcon().setAlpha(127);
                    }
                }
                break;
            }
            case R.id.i_line: {
                fManager.setCurrentTool(0);
                fCreatorSettings.getNewCreator(fEmptySettings);
                break;
            }
            case R.id.i_scraw: {
                fManager.setCurrentTool(1);
                fCreatorSettings.getNewCreator(new ScrawUI((ScrawCreator) fManager.getCurrentCreator()));
                break;
            }
            case R.id.i_splatter: {
                fManager.setCurrentTool(2);
                fCreatorSettings.getNewCreator(new SplatterUI((SplatterCreator) fManager.getCurrentCreator()));
                break;
            }
            case R.id.i_ribbon: {
                fManager.setCurrentTool(3);
                fCreatorSettings.getNewCreator(new RibbonUI((RibbonCreator) fManager.getCurrentCreator()));
                break;
            }
            case R.id.i_xshape: {
                fManager.setCurrentTool(4);
                fCreatorSettings.getNewCreator(new XShapeUI((XShapeCreator) fManager.getCurrentCreator()));
                break;
            }
            case R.id.i_flip_h: {
                fManager.setMirrorHorizontal(!fManager.getMirrorHorizontal());
                if (fManager.getMirrorHorizontal()) {
                    fHorizontalButton.setAlpha(1.f);
                } else {
                    fHorizontalButton.setAlpha(sAlphaHide);
                }
                break;
            }
            case R.id.i_flip_v: {
                fManager.setMirrorVertical(!fManager.getMirrorVertical());
                if (fManager.getMirrorVertical()) {
                    fVerticalButton.setAlpha(1.f);
                } else {
                    fVerticalButton.setAlpha(sAlphaHide);
                }
                break;
            }
            case R.id.i_flip_gradient: {
                if (fManager.isGradientActive()) {
                    fManager.setGradientActive(false);
                    fGradientButton.setImageDrawable(getResources().getDrawable(R.drawable.unicolor));
                } else {
                    fManager.setGradientActive(true);
                    fGradientButton.setImageDrawable(getResources().getDrawable(R.drawable.gradient));
                }
                break;
            }
            case R.id.i_flip_style: {
                if (fManager.getStyle() == Paint.Style.FILL) {
                    fManager.setStyle(Paint.Style.STROKE);
                    fStyleButton.setImageDrawable(getResources().getDrawable(R.drawable.stroke));
                } else {
                    fManager.setStyle(Paint.Style.FILL);
                    fStyleButton.setImageDrawable(getResources().getDrawable(R.drawable.fill));
                }
                break;
            }
            case R.id.i_clear: {
                createClearDialog();
                break;
            }
            case R.id.i_settings: {
                addFragment(fCreatorSettings);
                break;
            }
            case R.id.i_save: {
                new FileUtils(fManager).save(this);
                break;
            }
            case R.id.i_share: {
                new FileUtils(fManager).share(this);
                break;
            }
            case R.id.i_about: {
                createInfoDialog();
                break;
            }
            case R.id.i_help: {
                createHelpDialog();
                break;
            }
            case android.R.id.home: {
                fActionBar.hide();

                ObjectAnimator translation = ObjectAnimator.ofFloat(fBottomBar,
                        "translationY",
                        fBottomBar.getHeight());
                translation.setDuration(sDurationAnimation);

                translation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        fBottomBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });

                translation.start();

                fShowUI.setVisibility(View.VISIBLE);
                if (fColorSettings.isAdded() || fCreatorSettings.isAdded()) {
                    fFragmentManager.popBackStack();
                }
                break;
            }
            case R.id.i_show_ui: {

                fShowUI.setVisibility(View.GONE);
                fBottomBar.setVisibility(View.VISIBLE);
                fBottomBar.setTranslationY(0);
                fActionBar.show();
                break;
            }
        }
    }

    private void createClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.clear_dialog));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.agree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fManager.clear();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createInfoDialog() {
        createSimpleDialog(getResources().getString(R.string.about_text));
    }

    private void createSimpleDialog(String aMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(aMessage);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createHelpDialog() {
        createSimpleDialog("Help");
    }

    private class ColorOnTabListener implements ActionBar.TabListener, ColorUIFragment.ColorChangeListener {

        private boolean isFirst = true;
        private boolean isInit = false;

        public ColorOnTabListener() {
            fColorSettings.addColorListener(this);
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if (isInit) {
                fManager.switchColor();
            } else {
                isInit = true;
            }
            if (tab.getPosition() == 0) {
                isFirst = true;
                fFirstColor.setIcon(fFirstIconGenerator.getIcon(fManager.getMainColor(), true));
                fSecondColor.setIcon(fSecondIconGenerator.getIcon(fManager.getSubColor(), false));
                fColorSettings.setColor(fManager.getMainColor(), false);
            } else if (tab.getPosition() == 1) {
                isFirst = false;
                fFirstColor.setIcon(fFirstIconGenerator.getIcon(fManager.getSubColor(), false));
                fSecondColor.setIcon(fSecondIconGenerator.getIcon(fManager.getMainColor(), true));
                fColorSettings.setColor(fManager.getMainColor(), false);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            int tmp_color = fManager.getMainColor();
            addFragment(fColorSettings);
            fColorSettings.setColor(tmp_color, true);
        }

        @Override
        public void colorChange(int aColor) {
            fManager.setMainColor(aColor);
            if (isFirst) {
                fFirstColor.setIcon(fFirstIconGenerator.getIcon(fManager.getMainColor(), true));
            } else {
                fSecondColor.setIcon(fSecondIconGenerator.getIcon(fManager.getMainColor(), true));
            }
        }
    }
}