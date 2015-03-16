package draw.chemy;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static draw.chemy.creator.ComplexShapeCreator.Shape;
import static draw.chemy.creator.ComplexShapeCreator.ShapeGroup;

public class JSONLoader {

  private final Map<String, SVGPathCommand> fSVGCommands = new HashMap<String, SVGPathCommand>();

  private static JSONLoader sInstance;

  private JSONLoader() {
    SVGPathCommand op = new RelativeMove();
    fSVGCommands.put(op.getKey(), op);
    op = new Close();
    fSVGCommands.put(op.getKey(), op);
    op = new RelLineTo();
    fSVGCommands.put(op.getKey(), op);
    op = new RelBezier();
    fSVGCommands.put(op.getKey(), op);
    op = new Move();
    fSVGCommands.put(op.getKey(), op);
    op = new LineTo();
    fSVGCommands.put(op.getKey(), op);
    op = new Bezier();
    fSVGCommands.put(op.getKey(), op);
  }

  public static JSONLoader getInstance() {
    if (sInstance == null) {
      sInstance = new JSONLoader();
    }
    return sInstance;
  }

  public ShapeGroup loadJSON(Context aContext, int aID) throws JSONException, IOException {
    InputStream in = aContext.getResources().openRawResource(aID);
    ByteArrayOutputStream byteArrayOutputStream;

    int ctr = in.read();
    byteArrayOutputStream = new ByteArrayOutputStream();
    while (ctr != -1) {
      byteArrayOutputStream.write(ctr);
      ctr = in.read();
    }
    in.close();
    JSONObject jObject = new JSONObject(
        byteArrayOutputStream.toString());
    Iterator<String> keys = jObject.keys();
    ShapeGroup result = new ShapeGroup();
    while (keys.hasNext()) {

      String next = keys.next();
      List<Path> pathList = createPath(jObject.getString(next));
      result.addShape(new Shape(pathList));
    }
    return result;
  }

  private List<Path> createPath(String aPath) {
    StringTokenizer tokenizer = new StringTokenizer(aPath);
    List<Path> result = new ArrayList<Path>();
    List<PointF> tempPointFs = new ArrayList<PointF>();
    SVGPathCommand currentOp = null;
    PointF lastPointF = new PointF(0f, 0f);
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (token.contains(",")) {
        String[] split = token.split(",");
        PointF PointF = new PointF(Float.parseFloat(split[0])
            , Float.parseFloat(split[1]));
        tempPointFs.add(PointF);
      } else if (fSVGCommands.containsKey(token)) {
        if (currentOp != null) {
          currentOp.operation(tempPointFs, result, lastPointF);
        }
        currentOp = fSVGCommands.get(token);
        tempPointFs.clear();
      }
    }
    if (currentOp != null) {
      currentOp.operation(tempPointFs, result, lastPointF);
    }
    return result;
  }

  abstract class SVGPathCommand {

    private final String fKey;

    protected SVGPathCommand(String aKey) {
      fKey = aKey;
    }

    public final String getKey() {
      return fKey;
    }

    public abstract void operation(List<PointF> aPointFList, List<Path> aPathListSFCT, PointF lastPointFSFCT);
  }

  private class RelativeMove extends SVGPathCommand {
    protected RelativeMove() {
      super("m");
    }

    @Override
    public void operation(List<PointF> aPointFList, List<Path> aPathListSFCT, PointF lastPointFSFCT) {
      aPathListSFCT.add(new Path());
      Path path = aPathListSFCT.get(aPathListSFCT.size() - 1);
      PointF p = aPointFList.get(0);
      float x = p.x + lastPointFSFCT.x;
      float y = p.y + lastPointFSFCT.y;
      lastPointFSFCT.set(x, y);
      path.moveTo(x, y);
    }
  }

  private class Move extends SVGPathCommand {
    protected Move() {
      super("M");
    }

    @Override
    public void operation(List<PointF> aPointFList, List<Path> aPathListSFCT, PointF lastPointFSFCT) {
      Path path = new Path();
      aPathListSFCT.add(path);
      PointF p = aPointFList.get(0);
      lastPointFSFCT.set(p);
      path.moveTo(p.x, p.y);
    }
  }

  private class Close extends SVGPathCommand {
    protected Close() {
      super("z");
    }

    @Override
    public void operation(List<PointF> aPointFList, List<Path> aPathListSFCT, PointF lastPointFSFCT) {
      aPathListSFCT.get(aPathListSFCT.size() - 1).close();
    }
  }

  private class RelLineTo extends SVGPathCommand {
    protected RelLineTo() {
      super("l");
    }

    @Override
    public void operation(List<PointF> aPointFList, List<Path> aPathListSFCT, PointF lastPointFSFCT) {
      Path path = aPathListSFCT.get(aPathListSFCT.size() - 1);
      for (PointF PointF : aPointFList) {
        path.rLineTo(PointF.x, PointF.y);
      }
    }
  }

  private class LineTo extends SVGPathCommand {
    protected LineTo() {
      super("L");
    }

    @Override
    public void operation(List<PointF> aPointFList, List<Path> aPathListSFCT, PointF lastPointFSFCT) {
      Path path = aPathListSFCT.get(aPathListSFCT.size() - 1);
      for (PointF PointF : aPointFList) {
        path.lineTo(PointF.x, PointF.y);
      }
    }
  }

  private class RelBezier extends SVGPathCommand {
    protected RelBezier() {
      super("c");
    }

    @Override
    public void operation(List<PointF> aPointFList, List<Path> aPathListSFCT, PointF lastPointFSFCT) {
      Path path = aPathListSFCT.get(aPathListSFCT.size() - 1);
      for (int i = 0; i < aPointFList.size(); i += 3) {
        PointF p1 = aPointFList.get(i);
        PointF p2 = aPointFList.get(i + 1);
        PointF p3 = aPointFList.get(i + 2);
        path.rCubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
      }
    }
  }

  private class Bezier extends SVGPathCommand {
    protected Bezier() {
      super("C");
    }

    @Override
    public void operation(List<PointF> aPointFList, List<Path> aPathListSFCT, PointF lastPointFSFCT) {
      Path path = aPathListSFCT.get(aPathListSFCT.size() - 1);
      for (int i = 0; i < aPointFList.size(); i += 3) {
        PointF p1 = aPointFList.get(i);
        PointF p2 = aPointFList.get(i + 1);
        PointF p3 = aPointFList.get(i + 2);
        path.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
      }
    }
  }
}
