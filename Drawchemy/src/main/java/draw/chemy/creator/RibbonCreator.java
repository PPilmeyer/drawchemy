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

package draw.chemy.creator;

import android.graphics.Path;

import draw.chemy.DrawManager;

import static draw.chemy.DrawUtils.getProbability;

public class RibbonCreator extends ACreator {

    private MultiPathOperation fCurrentOperation;
    private Path fCurrentPath;

    private static final float PI = (float) Math.PI * 0.5f;
    private static final float PI3 = (float) Math.PI * 1.5f;

    //Parameters
    private int fSpacing;
    private float fGravity;
    private float fFriction;

    public static final float MIN_GRAVITY = 0.f;
    public static final float MAX_GRAVITY = 1.f;

    public static final float MIN_FRICTION = 1.f;
    public static final float MAX_FRICTION = 2.f;

    public static final int MIN_SPACING = 5;
    public static final int MAX_SPACING = 20;


    private Ribbon fRibbon;
    private long fTimestamp;

    public RibbonCreator(DrawManager aManager) {
        super(aManager);
        fCurrentPath = new Path();
        fRibbon = new Ribbon(20, 0.1f);
        fSpacing = 9;
        fFriction = 1.1f;
        fGravity = 0.15f;
    }

    @Override
    public IDrawingOperation startDrawingOperation(float x, float y) {
        fCurrentOperation = new MultiPathOperation(fManager.getPaint());
        fCurrentOperation.addPath();
        fRibbon.init();
        fRibbon.update(x, y);
        fTimestamp = System.currentTimeMillis();
        return fCurrentOperation;
    }

    @Override
    public void updateDrawingOperation(float x, float y) {
        if (System.currentTimeMillis() - fTimestamp >= fSpacing * 10) {
            fCurrentOperation.addPath();
            fRibbon.update(x, y);
            fTimestamp = System.currentTimeMillis();

        } else {
            fRibbon.update(x, y);
        }
        fManager.redraw();
    }

    @Override
    public void endDrawingOperation() {
        fCurrentOperation = null;
    }

    private class Ribbon {
        private int fNbOfParticles;
        private float fRandomness;
        private Particle fParticles[];
        private int first;
        private int last;

        float radiusMax = 10.f;
        float radiusDivide = 10.f;
        public float maxDistance = 40;

        public float drag = 2.f;
        public float dragFlare = 0.015f;


        public Ribbon(int aNbOfParticles, float aRandomness) {
            fNbOfParticles = aNbOfParticles;
            fRandomness = aRandomness;
            init();
        }

        public void init() {
            fParticles = new Particle[fNbOfParticles];
            first = 0;
            last = 0;
        }

        private void addParticle(float x, float y) {
            if (last - first == fNbOfParticles) {
                first++;
            }
            fParticles[last % fNbOfParticles] = new Particle(x, y, this, fRandomness);
            last++;
        }

        private void drawCurve() {

            int particlesAssigned = last - first;
            for (int i = 1; i < particlesAssigned - 1; i++) {
                Particle p = fParticles[(first + i) % fNbOfParticles];
                Particle previous = fParticles[(first + i - 1) % fNbOfParticles];
                Particle next = fParticles[(first + i + 1) % fNbOfParticles];
                p.updateControl(previous, next, particlesAssigned, i);
            }
            Particle p0 = fParticles[(first) % fNbOfParticles];
            p0.updateExtremity();
            Particle last = fParticles[(first + particlesAssigned - 1) % fNbOfParticles];
            last.updateExtremity();
            for (int i = 1; i < particlesAssigned - 1; i++) {
                Particle p = fParticles[(first + i) % fNbOfParticles];
                Particle previous = fParticles[(first + i - 1) % fNbOfParticles];
                Particle next = fParticles[(first + i + 1) % fNbOfParticles];
                p.updatePoint(previous, next);
            }

            if (particlesAssigned > 2) {
                fCurrentPath.rewind();
                Particle p1 = fParticles[(first + 1) % fNbOfParticles];
                fCurrentPath.moveTo(p1.leftFirstX, p1.leftFirstY);
                for (int i = 1; i < particlesAssigned - 1; i++) {
                    Particle p = fParticles[(first + i) % fNbOfParticles];
                    fCurrentPath.quadTo(p.controlLeftX, p.controlLeftY, p.leftSecondX, p.leftSecondY);
                }
                Particle lastp = fParticles[(first + particlesAssigned - 2) % fNbOfParticles];
                fCurrentPath.quadTo(last.fX,last.fY,lastp.rightFirstX,lastp.rightFirstY);

                for (int i = particlesAssigned - 2; i >= 1; i--) {
                    Particle p = fParticles[(first + i) % fNbOfParticles];
                    fCurrentPath.quadTo(p.controlRightX, p.controlRightY, p.rightSecondX, p.rightSecondY);
                }
                fCurrentPath.quadTo(p0.fX,p0.fY,p1.leftFirstX,p1.leftFirstY);
                fCurrentOperation.setTop(fCurrentPath);
            }

        }

        public void update(float x, float y) {
            addParticle(x, y);
            drawCurve();
        }
    }


    private class Particle {

        private float fX;
        private float fY;
        private Ribbon fRibbonContainer;
        private float fRandomness;

        float xSpeed, ySpeed = 0;

        float controlLeftX, controlLeftY, controlRightX, controlRightY;
        float radius;

        float leftFirstX, leftFirstY, leftSecondX, leftSecondY;
        float rightFirstX, rightFirstY, rightSecondX, rightSecondY;

        public Particle(float x, float y, Ribbon aRibbonContainer, float aRandomness) {
            fX = x;
            fY = y;
            fRibbonContainer = aRibbonContainer;
            fRandomness = aRandomness;
        }

        public void updateControl(Particle aPrevious, Particle aNext, int particulesNB, int index) {
            float div = 2;
            float cx1 = (aPrevious.fX + fX) / div;
            float cy1 = (aPrevious.fY + fY) / div;
            float cx2 = (aNext.fX + fX) / div;
            float cy2 = (aNext.fY + fY) / div;

            // calculate radians (direction of next point)
            float dx = cx2 - cx1;
            float dy = cy2 - cy1;

            float pRadians = (float) Math.atan2(dy, dx);

            float distance = (float) Math.sqrt(dx * dx + dy * dy);


            if (distance > fRibbonContainer.maxDistance) {
                float oldX = fX;
                float oldY = fY;
                fX = (float) (fX + ((fRibbonContainer.maxDistance / fRibbonContainer.drag) * Math.cos(pRadians)));
                fY = (float) (fY + ((fRibbonContainer.maxDistance / fRibbonContainer.drag) * Math.sin(pRadians)));
                xSpeed += (fX - oldX) * fRibbonContainer.dragFlare;
                ySpeed += (fY - oldY) * fRibbonContainer.dragFlare;
            }

            ySpeed += fGravity;
            xSpeed *= fFriction;
            ySpeed *= fFriction;
            fX += xSpeed + getProbability(0.3f);
            fY += ySpeed + getProbability(0.3f);

            float randX = getProbability(fRandomness) * distance;
            float randY = getProbability(fRandomness) * distance;
            fX += randX;
            fY += randY;


            radius = distance / fRibbonContainer.radiusDivide;


            if (radius > fRibbonContainer.radiusMax) {
                radius = fRibbonContainer.radiusMax;
            }

            // extremity
            if (index == particulesNB - 2 || index == 1) {
                if (radius > 1) {
                    radius = 1;
                }
            }

            controlLeftX = (float) (fX + Math.cos(pRadians + PI3) * radius);
            controlLeftY = (float) (fY + Math.sin(pRadians + PI3) * radius);
            controlRightX = (float) (fX + Math.cos(pRadians + PI) * radius);
            controlRightY = (float) (fY + Math.sin(pRadians + PI) * radius);

        }


        public void updatePoint(Particle previous, Particle next) {
            leftFirstX = (previous.controlLeftX+controlLeftX)/2.f;
            leftFirstY = (previous.controlLeftY+controlLeftY)/2.f;
            leftSecondX = (next.controlLeftX+controlLeftX)/2.f;
            leftSecondY = (next.controlLeftY+controlLeftY)/2.f;

            rightFirstX = (next.controlRightX+controlRightX)/2.f;
            rightFirstY = (next.controlRightY+controlRightY)/2.f;
            rightSecondX = (previous.controlRightX+controlRightX)/2.f;
            rightSecondY = (previous.controlRightY+controlRightY)/2.f;
        }

        public void updateExtremity() {
            controlLeftX = controlRightX = fX;
            controlLeftY = controlRightY = fY;
        }
    }

    public float getFriction() {
        return fFriction;
    }

    public float getGravity() {
        return fGravity;
    }

    public int getSpacing() {
        return fSpacing;
    }

    public void setSpacing(int fSpacing) {
        this.fSpacing = fSpacing;
    }

    public void setGravity(float fGravity) {
        this.fGravity = fGravity;
    }

    public void setFriction(float fFriction) {
        this.fFriction = fFriction;
    }

}
