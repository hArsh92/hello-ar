/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.hellosceneform;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable renderableCouch;
    private ModelRenderable renderableTelevision;
    private ModelRenderable renderableTable;
    private BottomSheetDialog bottomSheetDialog;

    private Anchor currentAnchor;
    private Group televisionGroup;
    private Group tableGroup;
    private Group couchGroup;

    @Override
    @SuppressWarnings({
            "AndroidApiChecker",
            "FutureReturnValueIgnored"
    })
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        loadRenderables();
        setUpBottomSheet();

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (renderableCouch == null) {
                        return;
                    }

                    boolean isVerticalPlane = plane.getType() == Plane.Type.VERTICAL;
                    televisionGroup.setVisibility(isVerticalPlane ? View.VISIBLE : View.GONE);
                    tableGroup.setVisibility(isVerticalPlane ? View.GONE : View.VISIBLE);
                    couchGroup.setVisibility(isVerticalPlane ? View.GONE : View.VISIBLE);

                    // Create the Anchor.
                    currentAnchor = hitResult.createAnchor();
                    bottomSheetDialog.show();
                });
    }

    private void setUpBottomSheet() {
        View bottomSheet = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null);
        bottomSheet.findViewById(R.id.image_view_item_couch).setOnClickListener(view -> onCouchRenderingClicked());
        bottomSheet.findViewById(R.id.image_view_item_television).setOnClickListener(view -> onTelevisionRenderingClicked());
        bottomSheet.findViewById(R.id.image_view_item_table).setOnClickListener(view -> onTableRenderingClicked());
        televisionGroup = bottomSheet.findViewById(R.id.group_television);
        tableGroup = bottomSheet.findViewById(R.id.group_table);
        couchGroup = bottomSheet.findViewById(R.id.group_couch);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheet);

        bottomSheetDialog.setOnDismissListener(dialogInterface -> currentAnchor = null);
    }

    private void onCouchRenderingClicked() {
        if (currentAnchor == null) {
            bottomSheetDialog.dismiss();
            return;
        }

        if (renderableCouch == null) {
            bottomSheetDialog.dismiss();
            return;
        }

        AnchorNode anchorNode = new AnchorNode(currentAnchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create the transformable andy and add it to the anchor.
        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
        andy.setParent(anchorNode);
        andy.setRenderable(renderableCouch);
        andy.select();
        bottomSheetDialog.dismiss();
    }

    private void onTelevisionRenderingClicked() {
        if (currentAnchor == null) {
            bottomSheetDialog.dismiss();
            return;
        }

        if (renderableTelevision == null) {
            bottomSheetDialog.dismiss();
            return;
        }

        AnchorNode anchorNode = new AnchorNode(currentAnchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create the transformable andy and add it to the anchor.
        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
        andy.setParent(anchorNode);
        andy.setRenderable(renderableTelevision);
        andy.select();
        bottomSheetDialog.dismiss();
    }


    private void onTableRenderingClicked() {
        if (currentAnchor == null) {
            bottomSheetDialog.dismiss();
            return;
        }

        if (renderableTable == null) {
            bottomSheetDialog.dismiss();
            return;
        }

        AnchorNode anchorNode = new AnchorNode(currentAnchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create the transformable andy and add it to the anchor.
        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
        andy.setParent(anchorNode);
        andy.setRenderable(renderableTable);
        andy.select();
        bottomSheetDialog.dismiss();
    }

    /**
     * When you build a Renderable, Sceneform loads its resources in the background while returning
     * a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
     */
    private void loadRenderables() {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("couch.sfb"))
                .build()
                .thenAccept(renderable -> renderableCouch = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load couch renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("television.sfb"))
                .build()
                .thenAccept(renderable -> renderableTelevision = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load tv renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });


        ModelRenderable.builder()
                .setSource(this, Uri.parse("table.sfb"))
                .build()
                .thenAccept(renderable -> renderableTable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load tv renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
