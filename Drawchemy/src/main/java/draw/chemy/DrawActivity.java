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

package draw.chemy;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import org.al.chemy.R;

import draw.chemy.UI.ASettingsGroupUI;
import draw.chemy.UI.BallUI;
import draw.chemy.UI.BasicShapeUI;
import draw.chemy.UI.CreatorWithoutUI;
import draw.chemy.UI.ExtraSettingsUI;
import draw.chemy.UI.NearestPointUI;
import draw.chemy.UI.PaintBrushUI;
import draw.chemy.UI.RibbonUI;
import draw.chemy.UI.ScrawlUI;
import draw.chemy.UI.SettingFragment;
import draw.chemy.UI.SplatterUI;
import draw.chemy.UI.StraightLineUI;
import draw.chemy.UI.XShapeUI;
import draw.chemy.UI.XShapeV2UI;
import draw.chemy.color.ColorUIFragment;
import draw.chemy.color.RoundIconGenerator;
import draw.chemy.creator.BallCreator;
import draw.chemy.creator.BasicShapesCreator;
import draw.chemy.creator.ComplexShapeCreator;
import draw.chemy.creator.LineCreator;
import draw.chemy.creator.MultiLineCreator;
import draw.chemy.creator.NearestPointLineCreator;
import draw.chemy.creator.PaintBrushCreator;
import draw.chemy.creator.RibbonCreator;
import draw.chemy.creator.ScrawlCreator;
import draw.chemy.creator.SketchCreator;
import draw.chemy.creator.SplatterCreator;
import draw.chemy.creator.StraightlineCreator;
import draw.chemy.creator.TextCreator;
import draw.chemy.creator.XShapeCreator;
import draw.chemy.creator.XShapeV2Creator;

public class DrawActivity extends Activity {

  private DrawManager fManager;
  private PaintState fPaintState;

  private ZoomPanDrawingView fDrawingView;
  private FragmentManager fFragmentManager;
  private LinearLayout fSettingContainer;

  SettingFragment fSettingsFragment;
  ColorUIFragment fColorSettings;

  private ASettingsGroupUI fEmptySettings;
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
  private ImageButton fKaleidoscopeButton;

  private View fBottomBar;
  private ImageButton fShowUI;
  private boolean fTransactionFlag = false;

  private Context fContext;

  private static float sAlphaHide = 0.3f;
  private static int sDurationAnimation = 600;

  private int fClearColor = Color.WHITE;
  private int fClearID = R.id.clear_white;

  private int fCurrentCreatorID;

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
    fPaintState = fManager.getPaintState();

    new FileUtils(fManager).loadTempImage(this);

    fManager.addTool(0, new LineCreator(fManager));
    fManager.addTool(1, new ScrawlCreator(fManager));
    fManager.addTool(2, new SplatterCreator(fManager));
    fManager.addTool(3, new RibbonCreator(fManager));
    fManager.addTool(4, new XShapeCreator(fManager));
    fManager.addTool(5, new PaintBrushCreator(fManager));
    fManager.addTool(6, new BallCreator(fManager));
    fManager.addTool(7, new BasicShapesCreator(fManager));
    fManager.addTool(8, new StraightlineCreator(fManager));
    fManager.addTool(9, new NearestPointLineCreator(fManager));
    fManager.addTool(10, new SketchCreator(fManager));
    fManager.addTool(11, new XShapeV2Creator(fManager));
    Log.e("JSON", "START");
    try {
      fManager.addTool(12, new ComplexShapeCreator(fManager, JSONLoader.getInstance().loadJSON(this, R.raw.parts)));
    } catch (Exception e) {
      Log.e("JSON", e.getMessage(), e);
    }
    fManager.addTool(13, new MultiLineCreator(fManager));
    fManager.addTool(14, new TextCreator(fManager));

    fManager.setCurrentTool(0);
    fCurrentCreatorID = R.id.i_line;

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
        fPaintState.setStrokeWeight(1.f + ((float) seekBar.getProgress()) / 10.f);
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
    findViewById(R.id.i_kaleidoscope).setOnClickListener(clickListener);

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

    fSettingsFragment = new SettingFragment();
    fSettingsFragment.getNewCreator(fEmptySettings);

    fColorSettings = new ColorUIFragment();
    fColorSettings.setHistoryColor(fManager.getPaintState().getHistoryColors());

    fManager.getPaintState().setColorHistoryListener(new PaintState.ColorHistoryListener() {
      @Override
      public void historyChanged() {
        if (fColorSettings != null) {
          fColorSettings.setHistoryColor(fManager.getPaintState().getHistoryColors());
          Log.i("INFO", "Color history event");
        }
      }
    });

    fColorSettings.setColor(fPaintState.getMainColor(), false);
    ColorUIFragment.Pipette pipette = new ColorUIFragment.Pipette(fColorSettings, fManager);
    fManager.setTouchListener(pipette);

    fColorSettings.addHueSwitchListener(new ColorUIFragment.HueSwitchListener() {

      @Override
      public void amplitudeChanged(float aAmplitude) {
        fPaintState.setColorVariation(aAmplitude);
      }
    });

    fFirstIconGenerator = new RoundIconGenerator(this);
    fSecondIconGenerator = new RoundIconGenerator(this);

    fStyleButton = (ImageButton) findViewById(R.id.i_flip_style);
    fGradientButton = (ImageButton) findViewById(R.id.i_flip_gradient);

    fHorizontalButton = (ImageButton) findViewById(R.id.i_flip_h);
    fVerticalButton = (ImageButton) findViewById(R.id.i_flip_v);

    fKaleidoscopeButton = (ImageButton) findViewById(R.id.i_kaleidoscope);

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
    fKaleidoscopeButton.setOnLongClickListener(descriptionClick);

    fHorizontalButton.setAlpha(sAlphaHide);
    fVerticalButton.setAlpha(sAlphaHide);
    fKaleidoscopeButton.setAlpha(sAlphaHide);

    fActionBar = getActionBar();
    if (fActionBar != null) {
      fActionBar.setDisplayShowTitleEnabled(false);
      fActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
      fFirstColor = fActionBar.newTab();
      fSecondColor = fActionBar.newTab();
      fFirstColor.setIcon(fFirstIconGenerator.getIcon(fPaintState.getMainColor(), true));
      ColorOnTabListener listener = new ColorOnTabListener();

      fFirstColor.setTabListener(listener);

      fActionBar.addTab(fFirstColor);

      fSecondColor.setIcon(fSecondIconGenerator.getIcon(fPaintState.getSubColor(), false));
      fSecondColor.setTabListener(listener);

      fActionBar.addTab(fSecondColor);

      fActionBar.setDisplayUseLogoEnabled(false);
      fActionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    fManager.removeListeners();
    fDrawingView.close();
    fDrawingView = null;
    fManager = null;
    fColorSettings.removeListeners();
    fColorSettings = null;
    fSecondaryFragment = null;
    fSettingsFragment.close();
    fSettingsFragment = null;
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
      break;
    }
    case R.id.i_line: {
      fManager.setCurrentTool(0);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(fEmptySettings);
      break;
    }
    case R.id.i_scraw: {
      fManager.setCurrentTool(1);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new ScrawlUI((ScrawlCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_splatter: {
      fManager.setCurrentTool(2);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new SplatterUI((SplatterCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_ribbon: {
      fManager.setCurrentTool(3);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new RibbonUI((RibbonCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_xshape: {
      fManager.setCurrentTool(4);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new XShapeUI((XShapeCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_paintbrush: {
      fManager.setCurrentTool(5);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new PaintBrushUI((PaintBrushCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_ball: {
      fManager.setCurrentTool(6);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new BallUI((BallCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_basic_shapes: {
      fManager.setCurrentTool(7);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new BasicShapeUI((BasicShapesCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_straigtline: {
      fManager.setCurrentTool(8);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new StraightLineUI((StraightlineCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_web_brush: {
      fManager.setCurrentTool(9);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new NearestPointUI((NearestPointLineCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_sketch_brush: {
      fManager.setCurrentTool(10);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new NearestPointUI((NearestPointLineCreator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_xshape2: {
      fManager.setCurrentTool(11);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(new XShapeV2UI((XShapeV2Creator) fManager.getCurrentCreator()));
      break;
    }
    case R.id.i_complex: {
      fManager.setCurrentTool(12);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(fEmptySettings);
      break;
    }
    case R.id.i_multiline: {
      fManager.setCurrentTool(13);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(fEmptySettings);
      break;
    }
    case R.id.i_textcreator: {
      fManager.setCurrentTool(14);
      fCurrentCreatorID = id;
      fSettingsFragment.getNewCreator(fEmptySettings);
      break;
    }
    case R.id.i_flip_h: {
      fPaintState.setMirrorHorizontal(!fPaintState.getMirrorHorizontal());
      if (fPaintState.getMirrorHorizontal()) {
        fHorizontalButton.setAlpha(1.f);
        fKaleidoscopeButton.setAlpha(sAlphaHide);
        fPaintState.setKaleidoscopeActive(false);
      } else {
        fHorizontalButton.setAlpha(sAlphaHide);
      }
      break;
    }
    case R.id.i_flip_v: {
      fPaintState.setMirrorVertical(!fPaintState.getMirrorVertical());
      if (fPaintState.getMirrorVertical()) {
        fVerticalButton.setAlpha(1.f);
        fKaleidoscopeButton.setAlpha(sAlphaHide);
        fPaintState.setKaleidoscopeActive(false);
      } else {
        fVerticalButton.setAlpha(sAlphaHide);
      }
      break;
    }
    case R.id.i_kaleidoscope: {
      fPaintState.setKaleidoscopeActive(!fPaintState.isKaleidoscopeActive());
      if (fPaintState.isKaleidoscopeActive()) {
        fKaleidoscopeButton.setAlpha(1.f);
        fPaintState.setMirrorVertical(false);
        fPaintState.setMirrorHorizontal(false);
        fVerticalButton.setAlpha(sAlphaHide);
        fHorizontalButton.setAlpha(sAlphaHide);

      } else {
        fKaleidoscopeButton.setAlpha(sAlphaHide);
      }
      break;
    }

    case R.id.i_flip_gradient: {
      if (fPaintState.isGradientActive()) {
        fPaintState.setGradientActive(false);
        fGradientButton.setImageDrawable(getResources().getDrawable(R.drawable.unicolor));
      } else {
        fPaintState.setGradientActive(true);
        fGradientButton.setImageDrawable(getResources().getDrawable(R.drawable.gradient));
      }
      break;
    }
    case R.id.i_flip_style: {
      if (fPaintState.getStyle() == Paint.Style.FILL) {
        fPaintState.setStyle(Paint.Style.STROKE);
        fStyleButton.setImageDrawable(getResources().getDrawable(R.drawable.stroke));
      } else {
        fPaintState.setStyle(Paint.Style.FILL);
        fStyleButton.setImageDrawable(getResources().getDrawable(R.drawable.fill));
      }
      break;
    }
    case R.id.i_clear: {
      createClearDialog();
      break;
    }
    case R.id.i_settings: {
      addFragment(fSettingsFragment);
      getClick(fCurrentCreatorID);
      break;
    }
    case R.id.i_extra_setting: {
      fSettingsFragment.getNewCreator(new ExtraSettingsUI(fPaintState));
      addFragment(fSettingsFragment);
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
      Intent intent = new Intent(this, HelpActivity.class);

      startActivity(intent);
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
      if (fColorSettings.isAdded() || fSettingsFragment.isAdded()) {
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
    case R.id.i_colors: {
      int tmp_color = fPaintState.getMainColor();
      addFragment(fColorSettings);
      fColorSettings.setColor(tmp_color, true);
      break;
    }
    case R.id.i_load: {
      Intent intent = new Intent(Intent.ACTION_PICK,
                                 android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      startActivityForResult(intent, 0);
      break;
    }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      Uri targetUri = data.getData();
      new FileUtils(fManager).load(this, targetUri);
    }
  }

  private void createClearDialog() {

    final Dialog clearDialog = new Dialog(this);
    clearDialog.setTitle("Clear");
    View clearView = getLayoutInflater().inflate(R.layout.clear, null);
    RadioGroup clearGroup = (RadioGroup) clearView.findViewById(R.id.clear_group);
    clearGroup.check(fClearID);
    if (fClearID == R.id.clear_sub) {
      fClearColor = fManager.getPaintState().getSubColor();
    }
    if (fClearID == R.id.clear_main) {
      fClearColor = fManager.getPaintState().getMainColor();
    }
    clearGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int radioID) {
        clear(radioID);
        fClearID = radioID;
      }
    });

    Button okButton = (Button) clearView.findViewById(R.id.clear_ok);
    okButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fManager.clear(fClearColor);
        clearDialog.cancel();
      }
    });

    Button cancelButton = (Button) clearView.findViewById(R.id.clear_cancel);
    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        clearDialog.cancel();
      }
    });

    clearDialog.setContentView(clearView);
    clearDialog.show();
  }

  private void clear(int aRadioID) {
    switch (aRadioID) {
    case R.id.clear_black:
      fClearColor = Color.BLACK;
      break;
    case R.id.clear_white:
      fClearColor = Color.WHITE;
      break;
    case R.id.clear_main:
      fClearColor = fManager.getPaintState().getMainColor();
      break;
    case R.id.clear_sub:
      fClearColor = fManager.getPaintState().getSubColor();
      break;
    }
  }

  private void startWebIntent(String aUrl) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(aUrl));
    startActivity(intent);
  }

  public void aboutClick(View aView) {
    int id = aView.getId();
    switch (id) {

    case R.id.about_drawchemy: {
      startWebIntent("https://code.google.com/p/drawchemy/");
      break;
    }
    case R.id.about_alchemy: {
      startWebIntent("http://al.chemy.org/");
      break;
    }
    case R.id.about_tumblr: {
      startWebIntent("http://drawchemy.tumblr.com/");
      break;
    }
    case R.id.about_mrdoob: {
      startWebIntent("http://mrdoob.com/projects/harmony");
      break;
    }

    }
  }

  private void createInfoDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    LayoutInflater inflater = getLayoutInflater();
    builder.setTitle(getResources().getString(R.string.about));

    builder.setView(inflater.inflate(R.layout.about, null));
    builder.setCancelable(false);
    builder.setPositiveButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialogInterface, int i) {

      }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
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
        fPaintState.switchColor();
      } else {
        isInit = true;
      }
      if (tab.getPosition() == 0) {
        isFirst = true;
        fFirstColor.setIcon(fFirstIconGenerator.getIcon(fPaintState.getMainColor(), true));
        fSecondColor.setIcon(fSecondIconGenerator.getIcon(fPaintState.getSubColor(), false));
        fColorSettings.setColor(fPaintState.getMainColor(), false);
      } else if (tab.getPosition() == 1) {
        isFirst = false;
        fFirstColor.setIcon(fFirstIconGenerator.getIcon(fPaintState.getSubColor(), false));
        fSecondColor.setIcon(fSecondIconGenerator.getIcon(fPaintState.getMainColor(), true));
        fColorSettings.setColor(fPaintState.getMainColor(), false);
      }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
      int tmp_color = fPaintState.getMainColor();
      addFragment(fColorSettings);
      fColorSettings.setColor(tmp_color, true);
    }

    @Override
    public void colorChange(int aColor) {
      fPaintState.setMainColor(aColor);
      if (isFirst) {
        fFirstColor.setIcon(fFirstIconGenerator.getIcon(fPaintState.getMainColor(), true));
      } else {
        fSecondColor.setIcon(fSecondIconGenerator.getIcon(fPaintState.getMainColor(), true));
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    new FileUtils(fManager).saveOnPause(this);
  }
}