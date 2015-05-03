/*
 * This file is part of the Drawchemy project - https://github.com/PPilmeyer/drawchemy
 *
 * Copyright (c) 2015 Pilmeyer Patrick
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

import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import draw.chemy.UI.*;
import draw.chemy.color.ColorSwitchIconGenerator;
import draw.chemy.color.ColorUIFragment;
import draw.chemy.creator.*;
import draw.chemy.utils.PropertyChangeEvent;
import draw.chemy.utils.PropertyChangeEventListener;
import org.al.chemy.R;

public class DrawActivity extends Activity {

  // Drawing
  private DrawManager fManager;
  private PaintState fPaintState;
  private ZoomPanDrawingView fDrawingView;
  private Spinner fSpinner;

  private ColorSwitchIconGenerator fColorIconGenerator;
  private VerticalSeekBar fOpacitySeekBar;

  private int fClearID = R.id.clear_white;
  private int fClearColor = Color.WHITE;
  private ColorUIFragment fColorFragment;

  private boolean fFragmentTransition;
  private Fragment fSecondaryFragment;
  private View fSettingContainer;

  private static int sDurationAnimation = 600;
  private MenuItem fColorSwitch;
  private SettingFragment fSettingsFragment;
  private ASettingsGroupUI fEmptySettings = new EmptyBrushSettings();
  private Map<ACreator, ASettingsGroupUI> fBrushSettings = new HashMap<ACreator, ASettingsGroupUI>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
      @Override
      public void onBackStackChanged() {
        setLayout();
      }
    });

    fSettingContainer = findViewById(R.id.settingContainer);
    fSettingContainer.setVisibility(View.GONE);

    View.OnTouchListener defaultListener = new DefaultTouchListener();

    fSettingContainer.setOnTouchListener(defaultListener);
    findViewById(R.id.bottomBar).setOnTouchListener(defaultListener);
    findViewById(R.id.leftBar).setOnTouchListener(defaultListener);

    fDrawingView = (ZoomPanDrawingView) findViewById(R.id.drawingView);

    fManager = fDrawingView.getCanvasManager();
    fPaintState = fManager.getPaintState();

    PropertyChangeEventListener listener = new PropertyChangeEventListener() {
      @Override
      public void propertyChange(PropertyChangeEvent aEvent) {
        if (fColorSwitch != null) {
          fColorSwitch.setIcon(fColorIconGenerator.getIcon(fPaintState.getMainColor(), fPaintState.getSubColor()));
        }
      }
    };

    new FileUtils(fManager).loadTempImage(this);

    fPaintState.addPropertyEventListener("color", listener);
    fPaintState.addPropertyEventListener("subcolor", listener);

    fColorFragment = new ColorUIFragment();
    fColorFragment.setPaintState(fPaintState);
    fColorIconGenerator = new ColorSwitchIconGenerator(this);

    fManager.addTool(0, new LineCreator(fManager));
    ScrawlCreator scrawlCreator = new ScrawlCreator(fManager);

    fManager.addTool(1, scrawlCreator);
    fBrushSettings.put(scrawlCreator, new ScrawlUI(scrawlCreator));
    SplatterCreator splatterCreator = new SplatterCreator(fManager);

    fManager.addTool(2, splatterCreator);
    fBrushSettings.put(splatterCreator, new SplatterUI(splatterCreator));

    RibbonCreator ribbonCreator = new RibbonCreator(fManager);
    fManager.addTool(3, ribbonCreator);
    fBrushSettings.put(ribbonCreator, new RibbonUI(ribbonCreator));

    XShapeCreator xShapeCreator = new XShapeCreator(fManager);
    fManager.addTool(4, xShapeCreator);
    fBrushSettings.put(xShapeCreator, new XShapeUI(xShapeCreator));

    PaintBrushCreator paintBrushCreator = new PaintBrushCreator(fManager);
    fManager.addTool(5, paintBrushCreator);
    fBrushSettings.put(paintBrushCreator, new PaintBrushUI(paintBrushCreator));

    BallCreator ballCreator = new BallCreator(fManager);
    fManager.addTool(6, new BallCreator(fManager));
    fBrushSettings.put(ballCreator, new BallUI(ballCreator));

    BasicShapesCreator basicShapesCreator = new BasicShapesCreator(fManager);
    fManager.addTool(7, basicShapesCreator);
    fBrushSettings.put(basicShapesCreator, new BasicShapeUI(basicShapesCreator));

    StraightlineCreator straightlineCreator = new StraightlineCreator(fManager);
    fManager.addTool(8, straightlineCreator);
    fBrushSettings.put(straightlineCreator, new StraightLineUI(straightlineCreator));

    NearestPointLineCreator nearestPointLineCreator = new NearestPointLineCreator(fManager);
    fManager.addTool(9, nearestPointLineCreator);
    fBrushSettings.put(nearestPointLineCreator, new NearestPointUI(nearestPointLineCreator));

    NearestPointLineCreator sketchCreator = new SketchCreator(fManager);
    fManager.addTool(10, sketchCreator);
    fBrushSettings.put(sketchCreator, new NearestPointUI(sketchCreator));

    XShapeV2Creator xShapeV2Creator = new XShapeV2Creator(fManager);
    fManager.addTool(11, xShapeV2Creator);
    fBrushSettings.put(xShapeV2Creator, new XShapeV2UI(xShapeV2Creator));

    fSettingsFragment = new SettingFragment();
    fSettingsFragment.setGeneralSetting(new ExtraSettingsUI(fPaintState));

    try {
      ComplexShapeCreator.ShapeGroup bones = JSONLoader.getInstance().loadJSON(this, R.raw.bones);
      ComplexShapeCreator.ShapeGroup parts = JSONLoader.getInstance().loadJSON(this, R.raw.parts);
      ComplexShapeCreator.ShapeGroup ozwalled = JSONLoader.getInstance().loadJSON(this, R.raw.ozwalled);

      ComplexShapeCreator complexShapeCreator = new ComplexShapeCreator(fManager, bones, parts, ozwalled);
      fManager.addTool(12, complexShapeCreator);
      fBrushSettings.put(complexShapeCreator, new ComplexeShapeUI(complexShapeCreator));
    } catch (Exception e) {
      Log.e("exceptions", e.getMessage());
    }
    fManager.addTool(13, new MultiLineCreator(fManager));
    fManager.addTool(14, new TextCreator(fManager));

    fManager.setCurrentTool(0);
    fSpinner = (Spinner) findViewById(R.id.mirroring);
    fSpinner.setAdapter(new MirrorSpinnerAdapter(this, R.id.mirroring));
    fSpinner.setOnItemSelectedListener(new MirrorSpinnerAdapter.MirrorListener(fPaintState));

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

    fOpacitySeekBar = (VerticalSeekBar) findViewById(R.id.opacity);
    fOpacitySeekBar.setMax(255);
    fOpacitySeekBar.setProgress(255);

    fOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        int alpha = seekBar.getProgress();
        int color = fPaintState.getMainColor();
        fPaintState.setMainColor(Color.argb(alpha, Color.red(color),
                                            Color.green(color),
                                            Color.blue(color)));
      }

    });

    fPaintState.addPropertyEventListener("color", new PropertyChangeEventListener() {
      @Override
      public void propertyChange(PropertyChangeEvent aEvent) {
        int alpha = Color.alpha(fPaintState.getMainColor());
        if (alpha != fOpacitySeekBar.getProgress()) {
          fOpacitySeekBar.setProgressAndThumb(alpha);
        }
      }
    });
  }

  private void setLayout() {
    if (fFragmentTransition) {
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

  private void addFragment(Fragment aFragment) {

    fSecondaryFragment = aFragment;
    if (!aFragment.isAdded()) {
      fFragmentTransition = true;
      FragmentTransaction transaction = getFragmentManager().beginTransaction();
      Fragment f = getFragmentManager().findFragmentById(R.id.settingContainer);
      if (f != null) {
        transaction.replace(R.id.settingContainer, aFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        getFragmentManager().popBackStack();
        transaction.addToBackStack(null);
      } else {
        transaction.add(R.id.settingContainer, aFragment);
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
      }
      transaction.commit();
      getFragmentManager().executePendingTransactions();
      fFragmentTransition = false;
      setLayout();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu2, menu);
    MenuItem item = menu.findItem(R.id.i_overflow);
    SubMenu subMenu;
    if (item != null) {
      subMenu = item.getSubMenu();
      getMenuInflater().inflate(R.menu.sub_menu, subMenu);
    }

    item = menu.findItem(R.id.i_color_switch);
    if (item != null) {
      item.setIcon(fColorIconGenerator.getIcon(fPaintState.getMainColor(), fPaintState.getSubColor()));
      fColorSwitch = item;
    }

    item = menu.findItem(R.id.i_creators);

    if (item != null) {
      subMenu = item.getSubMenu();
      getMenuInflater().inflate(R.menu.creators_menu, subMenu);
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {

    case R.id.i_reset_view: {
      fDrawingView.resetZoomPan();
      break;
    }
    case R.id.i_line: {
      fManager.setCurrentTool(0);
      break;
    }
    case R.id.i_scraw: {
      fManager.setCurrentTool(1);
      break;
    }
    case R.id.i_splatter: {
      fManager.setCurrentTool(2);
      break;
    }
    case R.id.i_ribbon: {
      fManager.setCurrentTool(3);
      break;
    }
    case R.id.i_xshape: {
      fManager.setCurrentTool(4);
      break;
    }
    case R.id.i_paintbrush: {
      fManager.setCurrentTool(5);
      break;
    }
    case R.id.i_ball: {
      fManager.setCurrentTool(6);
      break;
    }
    case R.id.i_basic_shapes: {
      fManager.setCurrentTool(7);
      break;
    }
    case R.id.i_straigtline: {
      fManager.setCurrentTool(8);
      break;
    }
    case R.id.i_web_brush: {
      fManager.setCurrentTool(9);
      break;
    }
    case R.id.i_sketch_brush: {
      fManager.setCurrentTool(10);
      break;
    }
    case R.id.i_xshape2: {
      fManager.setCurrentTool(11);
      break;
    }
    case R.id.i_complex: {
      fManager.setCurrentTool(12);
      break;
    }
    case R.id.i_multiline: {
      fManager.setCurrentTool(13);
      break;
    }
    case R.id.i_textcreator: {
      fManager.setCurrentTool(14);
      break;
    }
    case R.id.i_clear: {
      createClearDialog();
      break;
    }
    case R.id.i_settings: {
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
    case R.id.i_color_switch: {
      fPaintState.switchColor();
      break;
    }
    case R.id.i_colors_wheel: {
      int color = fPaintState.getMainColor();
      addFragment(fColorFragment);
      fColorFragment.setColor(color, true);
      break;
    }
    case R.id.i_about: {
      createInfoDialog();
      break;
    }
    case R.id.i_load: {
      Intent intent = new Intent(Intent.ACTION_PICK,
                                 android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      startActivityForResult(intent, 0);
      break;
    }
    }

    fSettingsFragment.setBrushSetting(getBrushSettings());
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      Uri targetUri = data.getData();
      new FileUtils(fManager).load(this, targetUri);
    }
  }

  public void undo(View aView) {
    fManager.undo();
  }

  public void redo(View aView) {
    fManager.redo();
  }

  public void gradient(View aView) {
    fPaintState.setGradientActive(!fPaintState.isGradientActive());
    int drawable = fPaintState.isGradientActive() ? R.drawable.gradient : R.drawable.unicolor;
    ImageButton button = (ImageButton) aView;
    button.setImageResource(drawable);
  }

  public void style(View aView) {
    fPaintState.setStyle(fPaintState.getStyle() == Paint.Style.FILL ? Paint.Style.STROKE : Paint.Style.FILL);
    int drawable = fPaintState.getStyle() == Paint.Style.FILL ? R.drawable.fill : R.drawable.stroke;
    ImageButton button = (ImageButton) aView;
    button.setImageResource(drawable);
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
      startWebIntent("https://github.com/PPilmeyer/drawchemy");
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    fManager.removeListeners();
    fDrawingView.close();
    fDrawingView = null;
    fManager = null;
    fSecondaryFragment = null;
    fColorFragment.removeListeners();
    fColorFragment = null;
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

  public ASettingsGroupUI getBrushSettings() {
    if (fBrushSettings.containsKey(fManager.getCurrentCreator())) {
      return fBrushSettings.get(fManager.getCurrentCreator());
    }
    return fEmptySettings;
  }

  private static class DefaultTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View aView, MotionEvent aMotionEvent) {
      return true;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    new FileUtils(fManager).saveOnPause(this);
  }
}
