/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gearvrf.pickandmove;

import android.util.Log;
import android.view.MotionEvent;

import org.gearvrf.FutureWrapper;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTransform;
import org.gearvrf.IPickEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class PickandmoveMain extends GVRMain {

    public class PickHandler implements IPickEvents
    {
        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo)
        {
            sceneObj.getRenderData().getMaterial().setColor(LOOKAT_COLOR_MASK_R, LOOKAT_COLOR_MASK_G, LOOKAT_COLOR_MASK_B);
            mPickedObject = sceneObj;
        }
        public void onExit(GVRSceneObject sceneObj)
        {
            sceneObj.getRenderData().getMaterial().setColor(1.0f, 1.0f, 1.0f);
        }
        public void onNoPick(GVRPicker picker)
        {
        	mPickedObject = null;
        }
        public void onPick(GVRPicker picker) { }
        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo) { }      
    }
    
    private static final float CUBE_WIDTH = 200.0f;
    private static final float OBJECT_POSITION = 5.0f;
    private static final float SCALE_FACTOR = 2.0f;
    private GVRContext mGVRContext = null;
    private GVRScene mScene = null;
    private List<GVRSceneObject> mObjects = new ArrayList<GVRSceneObject>();
    private IPickEvents mPickHandler;
    private GVRSceneObject mPickedObject = null;
    private GVRPicker   mPicker;
    
    @Override
    public void onInit(GVRContext gvrContext) {
        mGVRContext = gvrContext;

        mScene = mGVRContext.getMainScene();

        // head-tracking pointer
        GVRSceneObject headTracker = new GVRSceneObject(gvrContext,
                new FutureWrapper<GVRMesh>(gvrContext.createQuad(0.1f, 0.1f)),
                gvrContext.loadFutureTexture(new GVRAndroidResource(
                        mGVRContext, R.drawable.headtrackingpointer)));
        headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);
        headTracker.getRenderData().setDepthTest(false);
        headTracker.getRenderData().setRenderingOrder(100000);
        mScene.getMainCameraRig().addChildObject(headTracker);
        mPickHandler = new PickHandler();
        mScene.getEventReceiver().addListener(mPickHandler);

        FutureWrapper<GVRMesh> futureQuadMesh = new FutureWrapper<GVRMesh>(
                gvrContext.createQuad(CUBE_WIDTH, CUBE_WIDTH));

        Future<GVRTexture> futureCubemapTexture = gvrContext
                .loadFutureCubemapTexture(new GVRAndroidResource(mGVRContext,
                        R.raw.beach));

        GVRMaterial cubemapMaterial = new GVRMaterial(gvrContext,
                GVRMaterial.GVRShaderType.Cubemap.ID);
        cubemapMaterial.setMainTexture(futureCubemapTexture);

        // surrounding cube
        GVRSceneObject frontFace = new GVRSceneObject(gvrContext,
                futureQuadMesh, futureCubemapTexture);
        frontFace.getRenderData().setMaterial(cubemapMaterial);
        frontFace.setName("front");
        mScene.addSceneObject(frontFace);
        frontFace.getTransform().setPosition(0.0f, 0.0f, -CUBE_WIDTH * 0.5f);

        GVRSceneObject backFace = new GVRSceneObject(gvrContext,
                futureQuadMesh, futureCubemapTexture);
        backFace.getRenderData().setMaterial(cubemapMaterial);
        backFace.setName("back");
        mScene.addSceneObject(backFace);
        backFace.getTransform().setPosition(0.0f, 0.0f, CUBE_WIDTH * 0.5f);
        backFace.getTransform().rotateByAxis(180.0f, 0.0f, 1.0f, 0.0f);

        GVRSceneObject leftFace = new GVRSceneObject(gvrContext,
                futureQuadMesh, futureCubemapTexture);
        leftFace.getRenderData().setMaterial(cubemapMaterial);
        leftFace.setName("left");
        mScene.addSceneObject(leftFace);
        leftFace.getTransform().setPosition(-CUBE_WIDTH * 0.5f, 0.0f, 0.0f);
        leftFace.getTransform().rotateByAxis(90.0f, 0.0f, 1.0f, 0.0f);

        GVRSceneObject rightFace = new GVRSceneObject(gvrContext,
                futureQuadMesh, futureCubemapTexture);
        rightFace.getRenderData().setMaterial(cubemapMaterial);
        rightFace.setName("right");
        mScene.addSceneObject(rightFace);
        rightFace.getTransform().setPosition(CUBE_WIDTH * 0.5f, 0.0f, 0.0f);
        rightFace.getTransform().rotateByAxis(-90.0f, 0.0f, 1.0f, 0.0f);

        GVRSceneObject topFace = new GVRSceneObject(gvrContext, futureQuadMesh,
                futureCubemapTexture);
        topFace.getRenderData().setMaterial(cubemapMaterial);
        topFace.setName("top");
        mScene.addSceneObject(topFace);
        topFace.getTransform().setPosition(0.0f, CUBE_WIDTH * 0.5f, 0.0f);
        topFace.getTransform().rotateByAxis(90.0f, 1.0f, 0.0f, 0.0f);

        GVRSceneObject bottomFace = new GVRSceneObject(gvrContext,
                futureQuadMesh, futureCubemapTexture);
        bottomFace.getRenderData().setMaterial(cubemapMaterial);
        bottomFace.setName("bottom");
        mScene.addSceneObject(bottomFace);
        bottomFace.getTransform().setPosition(0.0f, -CUBE_WIDTH * 0.5f, 0.0f);
        bottomFace.getTransform().rotateByAxis(-90.0f, 1.0f, 0.0f, 0.0f);

        // reflective object
        // Future<GVRMesh> futureSphereMesh = gvrContext
        // .loadFutureMesh(new GVRAndroidResource(mGVRContext,
        // R.raw.sphere));
        GVRMesh sphereMesh = gvrContext.loadMesh(new GVRAndroidResource(
                mGVRContext, R.raw.sphere));
        GVRMaterial cubemapReflectionMaterial = new GVRMaterial(gvrContext,
                GVRMaterial.GVRShaderType.CubemapReflection.ID);
        cubemapReflectionMaterial.setMainTexture(futureCubemapTexture);

        GVRSceneObject sphere = new GVRSceneObject(gvrContext, sphereMesh);
        sphere.getRenderData().setMaterial(cubemapReflectionMaterial);
        sphere.setName("sphere");
        mScene.addSceneObject(sphere);
        mObjects.add(sphere);
        sphere.getTransform().setPosition(0.0f, 0.0f, -OBJECT_POSITION);
        sphere.getTransform().setScale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);
        GVRSphereCollider collider = new GVRSphereCollider(gvrContext);
        collider.setRadius(1.0f);
        sphere.attachComponent(collider);

        for (GVRSceneObject so : mScene.getWholeSceneObjects()) {
            Log.v("", "scene object name : " + so.getName());
        }
        mPicker = new GVRPicker(gvrContext, mScene);
    }

    private static float LOOKAT_COLOR_MASK_R = 1.0f;
    private static float LOOKAT_COLOR_MASK_G = 0.8f;
    private static float LOOKAT_COLOR_MASK_B = 0.8f;
    private static float PICKED_COLOR_MASK_R = 1.0f;
    private static float PICKED_COLOR_MASK_G = 0.5f;
    private static float PICKED_COLOR_MASK_B = 0.5f;

    @Override
    public void onStep() {
        FPSCounter.tick();
    }

    private GVRSceneObject attachedObject = null;
    private float lastX = 0, lastY = 0;
    private boolean isOnClick = false;
    private static final float MOVE_SCALE_FACTOR = 0.01f;
    private static final float MOVE_THRESHOLD = 80f;
    private static final float MIN_POSSIBLE_Z = -50.0f;
    private static final float MAX_POSSIBLE_Z = -3.0f;

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            lastX = event.getX();
            lastY = event.getY();
            isOnClick = true;
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (isOnClick) {
            	if (attachedObject != null) {
                	mScene.getMainCameraRig().removeChildObject(attachedObject);
                    mScene.addSceneObject(attachedObject);

                    mScene.getEventReceiver().addListener(mPickHandler);
                    attachedObject.getRenderData().getMaterial().setColor(
        					LOOKAT_COLOR_MASK_R,
        					LOOKAT_COLOR_MASK_G,
        					LOOKAT_COLOR_MASK_B);
                    attachedObject = null;            		
            	}
            	else if (mPickedObject != null) {
                    mScene.getEventReceiver().removeListener(mPickHandler);
                    mScene.removeSceneObject(mPickedObject);

                    attachedObject = mPickedObject;
                    mScene.getMainCameraRig().addChildObject(attachedObject);
                    attachedObject.getRenderData().getMaterial().setColor(
                    					PICKED_COLOR_MASK_R,
                                        PICKED_COLOR_MASK_G,
                                        PICKED_COLOR_MASK_B);
                }
            }
         break;
        case MotionEvent.ACTION_MOVE:
            float currentX = event.getX();
            float currentY = event.getY();
            float dx = currentX - lastX;
            float dy = currentY - lastY;
            float distance = dx * dx + dy * dy;
            if (Math.abs(distance) > MOVE_THRESHOLD) {
                if (attachedObject != null) {
                    lastX = currentX;
                    lastY = currentY;
                    distance *= MOVE_SCALE_FACTOR;
                    if (dy < 0) {
                        distance = -distance;
                    }
                    GVRTransform transform = attachedObject.getTransform();
                    transform.translate(0.0f, 0.0f, distance);
                    if (transform.getPositionZ() < MIN_POSSIBLE_Z) {
                        transform.setPositionZ(MIN_POSSIBLE_Z);
                    }
                    if (transform.getPositionZ() > MAX_POSSIBLE_Z) {
                        transform.setPositionZ(MAX_POSSIBLE_Z);
                    }
                }
                isOnClick = false;
            }
            break;
        default:
            break;
        }
    }

}

