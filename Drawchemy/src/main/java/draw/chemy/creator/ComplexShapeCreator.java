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

package draw.chemy.creator;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import draw.chemy.DrawManager;
import draw.chemy.DrawUtils;

public class ComplexShapeCreator extends ACreator {

  private ShapeGroup[] fShapeGroups;
  private MultiPathOperation fCurrentOperation = null;

  public int getFlow() {
    return fFlow;
  }

  public void setFlow(int aFlow) {
    this.fFlow = aFlow;
  }

  private int fFlow = 10;
  private int fCount = 0;
  private int fShapeGroupID = -1;

  public ComplexShapeCreator(DrawManager aManager, ShapeGroup... aShapeGroups) {
    super(aManager);
    this.fShapeGroups = aShapeGroups;
  }

  public void setShapeGroup(int aShapeGroupID) {
    fShapeGroupID = aShapeGroupID;
  }

  public int getShapeGroupId() {
    return fShapeGroupID;
  }

  @Override
  public IDrawingOperation startDrawingOperation(float x, float y) {

    fCurrentOperation = new MultiPathOperation(getPaint());
    fCount = 0;
    Shape shape;
    if (fShapeGroupID == -1) {
      shape = ShapeGroup.getShape(fShapeGroups);
    } else {
      shape = fShapeGroups[fShapeGroupID].getShape();
    }
    transformPath(shape.getCopyPath(), x, y);
    return fCurrentOperation;
  }

  private void transformPath(List<Path> aPathList, float x, float y) {
    Matrix m = new Matrix();
    float scale = DrawUtils.getProbability(2.f);
    m.setRotate(DrawUtils.getProbability(180));
    float value[] = new float[9];
    m.getValues(value);
    value[8] = 1 / scale;
    m.setValues(value);
    m.postTranslate(x, y);
    for (Path p : aPathList) {
      fCurrentOperation.addPath();
      fCurrentOperation.setTop(transformPath(p, m));
    }
  }

  private Path transformPath(Path aModifiablePath, Matrix aMatrix) {
    aModifiablePath.transform(aMatrix);
    return aModifiablePath;
  }

  @Override
  public void updateDrawingOperation(float x, float y) {
    if (fCount++ % fFlow == 0) {
      Shape shape;
      if (fShapeGroupID == -1) {
        shape = ShapeGroup.getShape(fShapeGroups);
      } else {
        shape = fShapeGroups[fShapeGroupID].getShape();
      }
      transformPath(shape.getCopyPath(), x, y);
      redraw();
    }
  }

  @Override
  public void endDrawingOperation() {
    fCurrentOperation = null;
  }

  public static class ShapeGroup {

    private final List<Shape> fShapes;

    public ShapeGroup() {
      fShapes = new ArrayList<Shape>();
    }

    public void addShape(Shape aShape) {
      fShapes.add(aShape);
    }

    public Shape getShape() {
      int id = DrawUtils.RANDOM.nextInt(fShapes.size());
      return fShapes.get(id);
    }

    public static Shape getShape(ShapeGroup... aShapeGroups) {
      int id = DrawUtils.RANDOM.nextInt(aShapeGroups.length);
      return aShapeGroups[id].getShape();
    }
  }

  public static class Shape {

    private final List<Path> fPaths;

    public Shape(List<Path> fPaths) {

      this.fPaths = fPaths;
    }

    public List<Path> getCopyPath() {
      List<Path> result = new ArrayList<Path>();
      Path newPath = new Path();
      for (Path p : fPaths) {
        result.add(new Path(p));
        newPath.addPath(p);
      }
      return Collections.singletonList(newPath);
    }
  }
}
