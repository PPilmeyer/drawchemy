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

package draw.chemy.color;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import draw.chemy.DrawManager;
import draw.chemy.PaintState;
import draw.chemy.utils.PropertyChangeEvent;
import draw.chemy.utils.PropertyChangeEventListener;
import org.al.chemy.R;

public class ColorUIFragment extends Fragment {

  private SeekBar fHueBar;
  private SeekBar fSaturationBar;
  private SeekBar fBrightnessBar;
  private SeekBar fAlphaSeekBar;
  private SeekBar fHueSwitchSeekbar;

  private int fColor;
  private List<ColorChangeListener> fColorListeners;
  private List<HueSwitchListener> fHueSwitchListeners;

  private int fHue;
  private int fSaturation;
  private int fBrightness;
  private int fAlpha;

  private int fHueAmp = 0;
  private float fTemp[];

  private ShapeDrawable fHueDrawable;
  private ShapeDrawable fSaturationDrawable;
  private ShapeDrawable fBrightnessDrawable;

  private Button[] fPaletteButton;

  private List<Integer> fColors;
  private boolean fPipetteActive = false;
  private PaintState fPaintState;
  private MyOnSeekBarChangeListener fListener;

  public ColorUIFragment() {
    fColorListeners = new ArrayList<ColorChangeListener>();
    fHueSwitchListeners = new ArrayList<HueSwitchListener>();
    fTemp = new float[3];
    fHueDrawable = new ShapeDrawable(new RectShape());
    fSaturationDrawable = new ShapeDrawable(new RectShape());
    fBrightnessDrawable = new ShapeDrawable(new RectShape());
  }

  public void setHistoryColor(List<Integer> aColors) {
    fColors = aColors;
    if (fPaletteButton == null) {
      return;
    }
    for (int i = 0; i < fPaletteButton.length; i++) {
      GradientDrawable drawable = (GradientDrawable) fPaletteButton[i].getBackground();
      drawable.setColor(fColors.get(i) | 0xFF000000);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.colorlayout,
                                 container, false);
    fColor = fPaintState.getMainColor();
    setColor(fColor);
    fHueBar = (SeekBar) view.findViewById(R.id.seekHue);

    fSaturationBar = (SeekBar) view.findViewById(R.id.seekSaturation);
    fBrightnessBar = (SeekBar) view.findViewById(R.id.seekValue);
    fAlphaSeekBar = (SeekBar) view.findViewById(R.id.seekAlpha);

    fHueBar.setProgress(fHue);
    fSaturationBar.setProgress(fSaturation);
    fBrightnessBar.setProgress(fBrightness);
    fAlphaSeekBar.setProgress(fAlpha);

    fSaturationBar.setProgressDrawable(fSaturationDrawable);
    fBrightnessBar.setProgressDrawable(fBrightnessDrawable);
    fHueBar.setProgressDrawable(fHueDrawable);

    setBrightnessDrawable();
    setSaturationDrawable();
    setHueDrawable();

    fHueBar.setOnSeekBarChangeListener(fListener);
    fSaturationBar.setOnSeekBarChangeListener(fListener);
    fBrightnessBar.setOnSeekBarChangeListener(fListener);
    fAlphaSeekBar.setOnSeekBarChangeListener(fListener);

    fHueSwitchSeekbar = (SeekBar) view.findViewById(R.id.seekColorRandomization);

    fHueSwitchSeekbar.setProgress(fHueAmp);

    fHueSwitchSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        fHueAmp = fHueSwitchSeekbar.getProgress();
        if (fPaintState != null) {
          fPaintState.setColorVariation(fHueSwitchSeekbar.getProgress() / 100.f);
        }
        for (HueSwitchListener listener : fHueSwitchListeners) {
          listener.amplitudeChanged(fHueSwitchSeekbar.getProgress() / 100.f);
        }
      }
    });

    /*Button pipette = (Button) view.findViewById(R.id.pipette_button);
    pipette.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fPipetteActive = true;
      }
    });*/

    View fFinishButton = view.findViewById(R.id.backButton);
    fFinishButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getFragmentManager().popBackStack();
      }
    });

    fPaletteButton = new Button[8];
    fPaletteButton[0] = (Button) view.findViewById(R.id.color_a);
    fPaletteButton[1] = (Button) view.findViewById(R.id.color_b);
    fPaletteButton[2] = (Button) view.findViewById(R.id.color_c);
    fPaletteButton[3] = (Button) view.findViewById(R.id.color_d);
    fPaletteButton[4] = (Button) view.findViewById(R.id.color_e);
    fPaletteButton[5] = (Button) view.findViewById(R.id.color_f);
    fPaletteButton[6] = (Button) view.findViewById(R.id.color_g);
    fPaletteButton[7] = (Button) view.findViewById(R.id.color_h);

    setColorPalette();
    return view;
  }

  private void setColorPalette() {

    View.OnClickListener onClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        for (int i = 0; i < fPaletteButton.length; i++) {
          if (fPaletteButton[i].getId() == view.getId()) {
            setColor(fColors.get(i), true);
          }
        }
      }
    };

    for (int i = 0; i < fPaletteButton.length; i++) {
      GradientDrawable drawable = (GradientDrawable) fPaletteButton[i].getBackground();
      drawable.setColor(fColors.get(i));
      fPaletteButton[i].setOnClickListener(onClickListener);
    }
  }

  private void changeColor() {
    fHue = fHueBar.getProgress();
    fSaturation = fSaturationBar.getProgress();
    fBrightness = fBrightnessBar.getProgress();
    fAlpha = fAlphaSeekBar.getProgress();

    fTemp[0] = fHue;
    fTemp[1] = fSaturation / 100.f;
    fTemp[2] = fBrightness / 100.f;

    fColor = Color.HSVToColor(fAlpha, fTemp);

    setBrightnessDrawable();
    setSaturationDrawable();
    if (fPaintState != null) {
      fPaintState.setMainColor(fColor);
    }
    for (ColorChangeListener listener : fColorListeners) {
      listener.colorChange(fColor);
    }
  }

  public void setColor(int aColor, boolean sendEvent) {

    setColor(aColor);
    if (fHueBar == null) {
      return;
    }

    int hue = fHue;
    int saturation = fSaturation;
    int brightness = fBrightness;
    int alpha = fAlpha;
    fHueBar.setProgress(hue);
    fSaturationBar.setProgress(saturation);
    fBrightnessBar.setProgress(brightness);
    fAlphaSeekBar.setProgress(alpha);

    setBrightnessDrawable();
    setSaturationDrawable();

    if (sendEvent) {
      for (ColorChangeListener listener : fColorListeners) {
        listener.colorChange(fColor);
      }
    }

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    fHueBar = null;
    fSaturationBar = null;
    fBrightnessBar = null;
    fAlphaSeekBar = null;
    fHueSwitchSeekbar = null;
  }

  private boolean isPipetteActive() {
    return fPipetteActive;
  }

  public void setPaintState(PaintState aPaintState) {
    fPaintState = aPaintState;
    fListener = new MyOnSeekBarChangeListener();
    setHistoryColor(fPaintState.getHistoryColors());
    fPaintState.setColorHistoryListener(new PaintState.ColorHistoryListener() {
      @Override
      public void historyChanged() {
        setHistoryColor(fPaintState.getHistoryColors());
      }
    });
    fPaintState.addPropertyEventListener("color", new PropertyChangeEventListener() {
      @Override
      public void propertyChange(PropertyChangeEvent aEvent) {
        if (fColor != fPaintState.getMainColor()) {
          setColor(fPaintState.getMainColor(), false);
        }
      }
    });
  }

  private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
      changeColor();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      changeColor();
    }
  }

  public void addColorListener(ColorChangeListener aListener) {
    fColorListeners.add(aListener);
  }

  public void addHueSwitchListener(HueSwitchListener aListener) {
    fHueSwitchListeners.add(aListener);
  }

  public static interface ColorChangeListener {
    public void colorChange(int aColor);
  }

  public static interface HueSwitchListener {

    public void amplitudeChanged(float aAmplitude);

  }

  private void setColor(int aColor) {
    Color.colorToHSV(aColor, fTemp);
    fHue = (int) fTemp[0];
    fSaturation = (int) (fTemp[1] * 100);
    fBrightness = (int) (fTemp[2] * 100);
    fAlpha = Color.alpha(aColor);
    fColor = aColor;
  }

  private void setBrightnessDrawable() {
    Paint p = fBrightnessDrawable.getPaint();
    final int colorA = Color.HSVToColor(new float[]{fHue, fSaturation / 100.f, 0.f});
    final int colorB = Color.HSVToColor(new float[]{fHue, fSaturation / 100.f, 1.f});
    int width = fBrightnessBar.getWidth();
    p.setShader(new LinearGradient(0, 0, width, 0, colorA, colorB, Shader.TileMode.CLAMP));
    fBrightnessDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
      @Override
      public Shader resize(int i, int i2) {
        return new LinearGradient(0, 0, i, 0, colorA, colorB, Shader.TileMode.CLAMP);
      }
    });
    fBrightnessBar.invalidate();
  }

  private void setSaturationDrawable() {
    Paint p = fSaturationDrawable.getPaint();
    final int colorA = Color.HSVToColor(new float[]{fHue, 0.f, fBrightness / 100.f});
    final int colorB = Color.HSVToColor(new float[]{fHue, 1.f, fBrightness / 100.f});
    int width = fSaturationBar.getWidth();
    p.setShader(new LinearGradient(0, 0, width, 0, colorA, colorB, Shader.TileMode.CLAMP));
    fSaturationDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
      @Override
      public Shader resize(int i, int i2) {
        return new LinearGradient(0, 0, i, 0, colorA, colorB, Shader.TileMode.CLAMP);
      }
    });
    fSaturationBar.invalidate();
  }

  private void setHueDrawable() {
    Paint p = fHueDrawable.getPaint();
    int size = 6;
    final int colors[] = new int[size + 1];

    float temp[] = new float[3];
    temp[1] = 1.f;
    temp[2] = 1.f;
    for (int i = 0; i < size; i++) {
      temp[0] = i * (360.f / size);
      colors[i] = Color.HSVToColor(temp);
    }
    temp[0] = 0.f;
    colors[size] = Color.HSVToColor(temp);

    int width = fHueBar.getWidth();
    p.setShader(new LinearGradient(0, 0, width, 0, colors, null, Shader.TileMode.MIRROR));
    fHueDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
      @Override
      public Shader resize(int i, int i2) {
        return new LinearGradient(0, 0, i, 0, colors, null, Shader.TileMode.MIRROR);
      }
    });
    fHueBar.invalidate();
  }

  public void removeListeners() {
    fColorListeners.clear();
    fHueSwitchListeners.clear();
  }

  public static class Pipette implements DrawManager.TouchListener {

    private final ColorUIFragment fFragment;
    private final DrawManager fDrawManager;

    public Pipette(ColorUIFragment aFragment, DrawManager aDrawManager) {
      super();
      fFragment = aFragment;
      fDrawManager = aDrawManager;
    }

    @Override
    public void touch(int aMotionEventType, float[] aPoint, float aX, float aY) {
      switch (aMotionEventType) {
      case MotionEvent.ACTION_MOVE:
      case MotionEvent.ACTION_DOWN:
        getColor(aPoint);
        break;
      default:
        fFragment.fPipetteActive = false;
      }
    }

    @Override
    public boolean isActive() {
      return fFragment.isPipetteActive();
    }

    public void getColor(float[] aPoint) {
      int x = (int) aPoint[0];
      int y = (int) aPoint[1];
      if (x < 0 || x > fDrawManager.getWidth() || y < 0 || y > fDrawManager.getHeight()) {
        return;
      }
      fDrawManager.redraw();
      int pixel = fDrawManager.getBitmap().getPixel(x, y);
      fFragment.setColor(pixel, true);
    }
  }
}
