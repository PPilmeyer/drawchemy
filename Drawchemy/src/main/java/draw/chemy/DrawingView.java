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

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Surface View with a unique DrawManager
 */
public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {

  private DrawManager fCanvasManager;
  private SurfaceHolder fHolder;
  private ViewThread fThread;

  private MyDrawListener fDrawListener;

  public DrawingView(Context context, DrawManager aCanvasManager) {
    super(context);
    fHolder = getHolder();
    fHolder.addCallback(this);
    fCanvasManager = aCanvasManager;
    fDrawListener = new MyDrawListener();
    fCanvasManager.setDrawListener(fDrawListener);
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    setOnTouchListener(fCanvasManager);
    fThread = new ViewThread();
    fThread.start();
    fCanvasManager.redraw();
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    fThread.stopThread();
  }

  private class ViewThread extends Thread {

    private boolean fRun = true;

    public void stopThread() {
      fRun = false;
      interrupt();
    }

    @Override
    public void run() {
      while (fRun) {
        try {

          fDrawListener.getDrawLock().lock();
          while (!fDrawListener.needToRedraw()) {
            fDrawListener.getDrawCondition().await();
          }
          Canvas canvas = fHolder.lockCanvas();
          fCanvasManager.draw(canvas);
          fDrawListener.hadRedraw();
          fHolder.unlockCanvasAndPost(canvas);
        } catch (InterruptedException e) {

        } finally {
          fDrawListener.getDrawLock().unlock();
        }
      }
    }
  }

  private class MyDrawListener implements DrawManager.DrawListener {

    private final Lock fDrawLock;
    private final Condition fDrawCondition;
    private boolean fDrawFlag;

    public MyDrawListener() {
      fDrawLock = new ReentrantLock();
      fDrawCondition = fDrawLock.newCondition();
      fDrawFlag = true;
    }

    @Override
    public void redraw() {
      fDrawLock.lock();
      fDrawFlag = true;
      fDrawCondition.signalAll();
      fDrawLock.unlock();
    }

    public Condition getDrawCondition() {
      return fDrawCondition;
    }

    public Lock getDrawLock() {
      return fDrawLock;
    }

    public boolean needToRedraw() {
      return fDrawFlag;
    }

    public void hadRedraw() {
      fDrawLock.lock();
      fDrawFlag = false;
      fDrawLock.unlock();
    }
  }
}
