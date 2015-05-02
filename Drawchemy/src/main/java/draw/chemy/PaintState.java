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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import draw.chemy.utils.PropertyChangeEvent;
import draw.chemy.utils.PropertyChangeEventListener;
import draw.chemy.utils.PropertyChangeEventSource;
import draw.chemy.utils.PropertyChangeEventSupport;

public class PaintState implements PropertyChangeEventSource {

  //Default
  private int fMainColor = Color.BLACK;
  private int fSubColor = Color.WHITE;

  public static final int MaxKaleidoscopeSec = 8;
  public static final int MinKaleidoscopeSec = 2;

  private float fColorVariation;

  private Paint.Style fStyle = Paint.Style.STROKE;
  private float fStrokeWeight = 1.5f;

  private ColorHistory fColorHistory = new ColorHistory();
  private boolean fNewColorUsage = false;

  private int fKaleidoscopeSec = 6;

  private boolean fGradientActive = false;

  private PaintState.MIRROR fMirrorState = PaintState.MIRROR.None;

  private PropertyChangeEventSupport fPropertyChangeEventSupport = new PropertyChangeEventSupport();

  @Override
  public void addPropertyEventListener(PropertyChangeEventListener aListener) {
    fPropertyChangeEventSupport.addPropertyEventListener(aListener);
  }

  @Override
  public void addPropertyEventListener(String aProperty, PropertyChangeEventListener aListener) {
    fPropertyChangeEventSupport.addPropertyEventListener(aProperty, aListener);
  }

  @Override
  public void removePropertyEventListener(PropertyChangeEventListener aListener) {
    fPropertyChangeEventSupport.removePropertyEventListener(aListener);
  }

  @Override
  public void firePropertyChange(PropertyChangeEvent aEvent) {
    fPropertyChangeEventSupport.firePropertyChange(aEvent);
  }

  public enum MIRROR {
    None,
    Horizontal,
    Vertical,
    Both,
    Kaleidoscope
  }

  public int getMainColor() {
    return fMainColor;
  }

  public int getSubColor() {
    return fSubColor;
  }

  public void setMainColor(int aColor) {
    if(aColor != fMainColor) {
      PropertyChangeEvent event = new PropertyChangeEvent("color", this, fMainColor, aColor);
      fMainColor = aColor;
      fNewColorUsage = true;
      firePropertyChange(event);
    }
  }

  public int getModifiedMainColor() {
    if (fNewColorUsage) {
      fNewColorUsage = false;
      fColorHistory.updateColor(getMainColor());
    }
    return getColor(getMainColor());
  }

  public boolean getMirrorHorizontal() {
    return fMirrorState == PaintState.MIRROR.Horizontal || fMirrorState == PaintState.MIRROR.Both;
  }

  public boolean getMirrorVertical() {
    return fMirrorState == PaintState.MIRROR.Vertical || fMirrorState == PaintState.MIRROR.Both;
  }

  public int getKaleidoscopeSec() {
    return fKaleidoscopeSec;
  }

  public void setKaleidoscopeSec(int aKaleidoscopeSec) {
    fKaleidoscopeSec = aKaleidoscopeSec;
  }

  public void setMirrorVertical(boolean checked) {
    switch (fMirrorState) {
    case None: {
      fMirrorState = checked ? PaintState.MIRROR.Vertical : PaintState.MIRROR.None;
      break;
    }
    case Horizontal: {
      fMirrorState = checked ? PaintState.MIRROR.Both : PaintState.MIRROR.Horizontal;
      break;
    }
    case Vertical: {
      fMirrorState = checked ? PaintState.MIRROR.Vertical : PaintState.MIRROR.None;
      break;
    }
    case Both: {
      fMirrorState = checked ? PaintState.MIRROR.Both : PaintState.MIRROR.Horizontal;
      break;
    }
    case Kaleidoscope: {
      fMirrorState = checked ? PaintState.MIRROR.Both : PaintState.MIRROR.Horizontal;
      break;
    }
    }
  }

  public void setMirrorState(MIRROR aMirrorState) {
    fMirrorState = aMirrorState;
  }

  public void setMirrorHorizontal(boolean checked) {
    switch (fMirrorState) {
    case None: {
      fMirrorState = checked ? PaintState.MIRROR.Horizontal : PaintState.MIRROR.None;
      break;
    }
    case Horizontal: {
      fMirrorState = checked ? PaintState.MIRROR.Horizontal : PaintState.MIRROR.None;
      break;
    }
    case Vertical: {
      fMirrorState = checked ? PaintState.MIRROR.Both : PaintState.MIRROR.Vertical;
      break;
    }
    case Both: {
      fMirrorState = checked ? PaintState.MIRROR.Both : PaintState.MIRROR.Vertical;
      break;
    }

    }
  }

  public int getModifiedSubColor() {
    return getColor(getSubColor());
  }

  public MIRROR getMirrorState() {
    return fMirrorState;
  }

  public boolean isGradientActive() {
    return fGradientActive;
  }

  public void setGradientActive(boolean fGradientActive) {
    this.fGradientActive = fGradientActive;
  }

  @SuppressWarnings("all")
  public void setSubColor(int aColor) {
    if(fSubColor != aColor) {
      PropertyChangeEvent event = new PropertyChangeEvent("subcolor", this, fSubColor, aColor);
      fSubColor = aColor;
      firePropertyChange(event);
    }
  }

  public void switchColor() {
    int tmp = fMainColor;
    setMainColor(fSubColor);
    setSubColor(tmp);
  }

  public void setColorVariation(float aColorVariation) {
    fColorVariation = aColorVariation;
  }

  @SuppressWarnings("all")
  public float getColorVariation() {
    return fColorVariation;
  }

  public Paint getPaint() {
    Paint p = new Paint();
    p.setColor(getModifiedMainColor());
    p.setStrokeWidth(fStrokeWeight);
    p.setStyle(fStyle);
    p.setStrokeJoin(Paint.Join.ROUND);
    p.setStrokeCap(Paint.Cap.ROUND);
    p.setAntiAlias(true);
    p.setPathEffect(new CornerPathEffect(7.f));
    return p;
  }

  private int getColor(int aColor) {
    if (fColorVariation != 0) {
      float hsv[] = new float[3];
      Color.colorToHSV(aColor, hsv);
      hsv[0] += DrawUtils.getProbability(90.f * fColorVariation);
      if (hsv[0] < 0.f) {
        hsv[0] += 360.f;
      } else if (hsv[0] > 360.f) {
        hsv[0] -= 360.f;
      }
      return Color.HSVToColor(Color.alpha(aColor), hsv);
    } else {
      return aColor;
    }
  }

  public Paint.Style getStyle() {
    return fStyle;
  }

  public void setStyle(Paint.Style fStyle) {
    this.fStyle = fStyle;
  }

  public void setStrokeWeight(float aStrokeWeigth) {
    fStrokeWeight = aStrokeWeigth;
  }

  @SuppressWarnings("all")
  public float getStrokeWeight() {
    return fStrokeWeight;
  }

  public List<Integer> getHistoryColors() {
    return Collections.unmodifiableList(fColorHistory.getHistoryColors());
  }

  public void setColorHistoryListener(ColorHistoryListener aColorHistoryListener) {
    fColorHistory.fListener = aColorHistoryListener;
  }

  public static interface ColorHistoryListener {

    public void historyChanged();
  }

  private static class ColorHistory {

    private ColorElem fColors[];

    private ColorHistoryListener fListener;

    public ColorHistory() {
      fColors = new ColorElem[8];
      fColors[0] = new ColorElem(Color.BLACK, 0);
      fColors[1] = new ColorElem(Color.WHITE, 1);
      fColors[2] = new ColorElem(Color.RED, 2);
      fColors[3] = new ColorElem(Color.GREEN, 3);
      fColors[4] = new ColorElem(Color.BLUE, 4);
      fColors[5] = new ColorElem(Color.YELLOW, 5);
      fColors[6] = new ColorElem(Color.MAGENTA, 6);
      fColors[7] = new ColorElem(Color.CYAN, 7);
    }

    private void updateColor(int aNewColor) {
      for (ColorElem fColor : fColors) {
        fColor.priority++;
      }
      int idx = 0;
      int priority = -1;
      for (int i = 0; i < fColors.length; i++) {
        if (fColors[i].color == aNewColor) {
          fColors[i].priority = 0;
          return;
        } else if (priority < fColors[i].priority) {
          priority = fColors[i].priority;
          idx = i;
        }
      }
      fColors[idx].priority = 0;
      fColors[idx].color = aNewColor;

      if (fListener != null) {
        fListener.historyChanged();
      }
    }

    private List<Integer> getHistoryColors() {
      ArrayList<Integer> result = new ArrayList<Integer>();
      for (ColorElem fColor : fColors) {
        result.add(fColor.color);
      }
      return result;
    }
  }

  private static class ColorElem implements Comparable {

    int color;
    int priority;

    public ColorElem(int aColor, int aPriority) {
      color = aColor;
      priority = aPriority;
    }

    @Override
    public int compareTo(Object o) {
      if (!(o instanceof ColorElem)) {
        throw new RuntimeException();
      }
      return priority - ((ColorElem) o).priority;
    }
  }

}
