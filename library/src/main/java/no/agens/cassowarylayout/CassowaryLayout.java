/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2014 Agens AS
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

package no.agens.cassowarylayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.agens.cassowarylayout.util.MeasureSpecUtils;
import no.agens.cassowarylayout.util.TimerUtil;

public class CassowaryLayout extends ViewGroup  {

    private String logTag;
    private volatile CassowaryModel cassowaryModel;
    private ViewIdResolver viewIdResolver;

    private boolean asyncSetup = true;

    private boolean aspectRatioFixed = false;
    private float aspectRatioWidthFactor = 1;
    private float aspectRatioHeightFactor = 1;

    private enum State {
        UNINITIALIZED,
        PARSING_CONSTRAINTS,
        PARSING_COMPLETE
    }

    private State state = State.UNINITIALIZED;

    private Integer widthMeasureSpec;
    private Integer heightMeasureSpec;

    private ArrayList<CassowaryLayoutSetupCallback> setupObservers;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public interface CassowaryLayoutSetupCallback {
        void onCassowaryLayoutSetupComplete(CassowaryLayout layout);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public LayoutParams() {
            super(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public String debug(String output) {
            return "";
        }
    }

    public CassowaryLayout(Context context, ViewIdResolver viewIdResolver) {
        super(context);
        this.viewIdResolver = viewIdResolver;
        this.cassowaryModel = new CassowaryModel(context);
    }

    public CassowaryLayout(Context context) {
        this(context, new DefaultViewIdResolver(context));
    }

    public CassowaryLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CassowaryLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.viewIdResolver = new DefaultViewIdResolver(getContext());
        cassowaryModel = new CassowaryModel(getContext().getApplicationContext());
        readConstraintsFromXml(attrs);
    }

    public CassowaryModel getCassowaryModel() {
        return cassowaryModel;
    }

    public void addSetupCallback(final CassowaryLayoutSetupCallback setupObserver) {
        if (state == State.PARSING_COMPLETE) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setupObserver.onCassowaryLayoutSetupComplete(CassowaryLayout.this);
                }
            });
        } else {
            if (setupObservers == null) {
                setupObservers = new ArrayList<CassowaryLayoutSetupCallback>();
            }
            setupObservers.add(setupObserver);
        }
    }

    public void setupSolverAsync(final CharSequence[] constraints) {
        state = State.PARSING_CONSTRAINTS;

        log("setupSolverAsync - submitting task");

        parseConstraintsOnBackgroundsThread(constraints, new Runnable() {
            @Override
            public void run() {
                state = State.PARSING_COMPLETE;
                if (isMeasureSpecSet()) {
                    log("measureSpecSet requesting layout");
                    callbackAfterSetup();
                    requestLayout();
                }
            }
        });
    }

    public float getAspectRatioWidthFactor() {
        return aspectRatioWidthFactor;
    }

    public void setAspectRatioWidthFactor(float aspectRatioWidthFactor) {
        this.aspectRatioWidthFactor = aspectRatioWidthFactor;
    }

    public float getAspectRatioHeightFactor() {
        return aspectRatioHeightFactor;
    }

    public void setAspectRatioHeightFactor(float aspectRatioHeightFactor) {
        this.aspectRatioHeightFactor = aspectRatioHeightFactor;
    }

    public void setChildPositionsFromCassowaryModel() {
        long timeBeforeSolve = System.nanoTime();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                int childId = child.getId();

                Node node = getNodeById(childId);

                int x = (int) node.getLeft().value() + getPaddingLeft();
                int y = (int) node.getTop().value() + getPaddingTop();

                child.setX(x);
                child.setY(y);

            }
        }
        log("setChildPositionsFromCassowaryModel - took " + TimerUtil.since(timeBeforeSolve));
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        long before = System.nanoTime();

        log("onMeasure width " +
                MeasureSpecUtils.getModeAsString(widthMeasureSpec) + " " +
                MeasureSpec.getSize(widthMeasureSpec) + " height " +
                MeasureSpecUtils.getModeAsString(heightMeasureSpec) + " " +
                MeasureSpec.getSize(heightMeasureSpec) + " in state " + state);

        switch(state) {
            case UNINITIALIZED:
            case PARSING_CONSTRAINTS:
                saveMeasureSpec(widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimensionsFromAspectRatio(widthMeasureSpec, heightMeasureSpec);
                // wait for parsing to complete
                break;
            case PARSING_COMPLETE:
                if (aspectRatioFixed) {
                    setMeasuredDimensionsFromAspectRatio(widthMeasureSpec, heightMeasureSpec);
                    // make new measure spec based on aspect ratio set above
                    cassowaryMeasure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                                    MeasureSpec.makeMeasureSpec(getMeasuredHeight(),  MeasureSpec.EXACTLY));
                } else {
                    cassowaryMeasure(widthMeasureSpec, heightMeasureSpec);
                    setMeasuredDimensionsFromCassowaryModel(widthMeasureSpec, heightMeasureSpec);
                }
                break;
        }

        log("onMeasure took " + TimerUtil.since(before) + " state now " + state);

    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * and with the coordinates (0, 0).
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t,
                            int r, int b) {
        if (state == State.PARSING_COMPLETE) {
            layoutChildren(changed, l, t, r, b);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CassowaryLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of CassowaryLayout.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof CassowaryLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    private void measureChildrenUsingNodes(int widthMeasureSpec, int heightMeasureSpec) {
        long timeBeforeSolve = System.nanoTime();

        int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentWidthMode = MeasureSpec.getMode(widthMeasureSpec);

        final int size = getChildCount();

        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                Node node = getNodeById(child.getId());

                int nodeHeight = (int) node.getVariableValue(Node.HEIGHT);
                int nodeWidth = (int) node.getVariableValue(Node.WIDTH);

                int widthMode = parentWidthMode;
                if (node.hasIntrinsicWidth()) {
                    widthMode = MeasureSpec.UNSPECIFIED;
                    nodeWidth = 0;
                }

                int heightMode = parentHeightMode;
                if (node.hasIntrinsicHeight()) {
                    heightMode = MeasureSpec.UNSPECIFIED;
                    nodeHeight = 0;
                }

                // If the parent's width is unspecified, infer it from the container node
                if (parentWidthMode == MeasureSpec.UNSPECIFIED) {
                    widthMode = MeasureSpec.AT_MOST;
                    nodeWidth = (int) cassowaryModel.getContainerNode().getWidth().value();
                }


                int childHeightSpec = MeasureSpec.makeMeasureSpec(nodeHeight, heightMode);
                int childWidthSpec = MeasureSpec.makeMeasureSpec(nodeWidth, widthMode);

                log("child " + viewIdResolver.getViewNameById(child.getId()) + " width " + MeasureSpecUtils.getModeAsString(childWidthSpec) + " " + nodeWidth + " height " + MeasureSpecUtils.getModeAsString(childHeightSpec) + " " + nodeHeight);
                measureChild(child, childWidthSpec, childHeightSpec);
            }
        }
        log("measureChildrenUsingCassowaryModel took " + TimerUtil.since(timeBeforeSolve));
    }

    private boolean saveMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {
        boolean changed = false;
        if (this.widthMeasureSpec == null || this.widthMeasureSpec != widthMeasureSpec) {
            this.widthMeasureSpec = widthMeasureSpec;
            changed = true;
        }
        if (this.heightMeasureSpec == null || this.heightMeasureSpec != heightMeasureSpec) {
            this.heightMeasureSpec = heightMeasureSpec;
            changed = true;
        }
        return changed;
    }

    private void callbackAfterSetup() {
        if (setupObservers != null) {
            for (CassowaryLayoutSetupCallback observer : setupObservers) {
                observer.onCassowaryLayoutSetupComplete(this);
            }
            setupObservers.clear();
        }
    }

    private HashMap<String, Integer> getIntrinsicHeights() {
        HashMap<String, Integer> intrinsicHeights = new HashMap<String, Integer>();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                String viewName = viewIdResolver.getViewNameById(child.getId());
                Node node = cassowaryModel.getNodeByName(viewName);
                if (node.hasIntrinsicHeight()) {
                    int childHeight = child.getMeasuredHeight();
                    intrinsicHeights.put(viewName, childHeight);
                }
            }
        }
        return intrinsicHeights;
    }

    private HashMap<String, Integer> getIntrinsicWidths() {
        HashMap<String, Integer> intrinsicWidths = new HashMap<String, Integer>();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                String viewName = viewIdResolver.getViewNameById(child.getId());
                Node node = cassowaryModel.getNodeByName(viewName);
                if (node.hasIntrinsicWidth()) {
                    int childWidth = child.getMeasuredWidth();
                    intrinsicWidths.put(viewName, childWidth);
                }
            }
        }
        return intrinsicWidths;
    }

    private void setVariableToValue(String variableName, HashMap<String, Integer> nodesMap) {
        Iterator<Map.Entry<String, Integer>> iterator = nodesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            Node node = cassowaryModel.getNodeByName(entry.getKey());
            node.setVariableToValue(variableName, entry.getValue());
        }
    }

    private void cassowaryMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        long timeBeforeSolve = System.nanoTime();

        setMeasureSpecOnCassowaryModel(widthMeasureSpec, heightMeasureSpec);

        log("cassowaryMeasure width " +
                MeasureSpecUtils.getModeAsString(widthMeasureSpec) + " " +
                MeasureSpec.getSize(widthMeasureSpec) + " height " +
                MeasureSpecUtils.getModeAsString(heightMeasureSpec) + " " +
                MeasureSpec.getSize(heightMeasureSpec));


        cassowaryModel.solve();
        measureChildrenUsingNodes(widthMeasureSpec, heightMeasureSpec);
        setVariableToValue(ChildNode.INTRINSIC_HEIGHT, getIntrinsicHeights());
        setVariableToValue(ChildNode.INTRINSIC_WIDTH, getIntrinsicWidths());
        cassowaryModel.solve();

        log("cassowaryMeasure took " + TimerUtil.since(timeBeforeSolve));
    }

    private void setMeasureSpecOnCassowaryModel(int widthMeasureSpec, int heightMeasureSpec) {
        int widthWithoutPadding =  MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding =  MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        setMeasureSpecOnCassowaryModel(heightMode, widthMode, heightWithoutPadding, widthWithoutPadding);
    }

    private void setMeasureSpecOnCassowaryModel(int heightMode, int widthMode, int heightWithoutPadding, int widthWithoutPadding) {
        if (heightMode == MeasureSpec.AT_MOST) {
            cassowaryModel.getContainerNode().setVariableToAtMost(Node.HEIGHT, heightWithoutPadding);
        } else if (heightMode == MeasureSpec.EXACTLY) {
            cassowaryModel.getContainerNode().setVariableToValue(Node.HEIGHT, heightWithoutPadding);
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            cassowaryModel.getContainerNode().setVariableToAtMost(Node.WIDTH, widthWithoutPadding);
        } else {
            cassowaryModel.getContainerNode().setVariableToValue(Node.WIDTH, widthWithoutPadding);
        }
    }

    private void parseConstraintsOnBackgroundsThread(final CharSequence[] constraints, final Runnable callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                cassowaryModel.addConstraints(constraints);
                cassowaryModel.solve();
                handler.postAtFrontOfQueue(callback);
            }
        });
    }

    private void setMeasuredDimensionsFromCassowaryModel(int widthMeasureSpec, int heightMeasureSpec) {
        cassowaryModel.solve();

        int resolvedWidth = -1;
        int resolvedHeight = -1;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            resolvedWidth = (int) cassowaryModel.getContainerNode().getWidth().value() + getPaddingLeft() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            resolvedHeight = (int) cassowaryModel.getContainerNode().getHeight().value() + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    private double getAspectRatio() {
        return getAspectRatioWidthFactor() / getAspectRatioHeightFactor();
    }

    private void setMeasuredDimensionsFromAspectRatio(int widthMeasureSpec, int heightMeasureSpec) {
        int width =  MeasureSpec.getSize(widthMeasureSpec);
        int height =  MeasureSpec.getSize(heightMeasureSpec);

        log("setMeasuredDimensionsFromAspectRatio width " +
                MeasureSpecUtils.getModeAsString(widthMeasureSpec) + " " +
                width + " height " +
                MeasureSpecUtils.getModeAsString(heightMeasureSpec) + " " +
                height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        double aspectRatio = getAspectRatio();

        int widthBasedOnRatio = (int)(height * aspectRatio);

        int heightBasedOnRatio = (int)(width / aspectRatio);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                if (widthBasedOnRatio < width) {
                    width = widthBasedOnRatio;
                }
                break;
            case MeasureSpec.EXACTLY:
                // do nothing
                break;
            case MeasureSpec.UNSPECIFIED:
                width = widthBasedOnRatio;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                if (heightBasedOnRatio < height) {
                    height = heightBasedOnRatio;
                }
                break;
            case MeasureSpec.EXACTLY:
                // do nothing
                break;
            case MeasureSpec.UNSPECIFIED:
                height = heightBasedOnRatio;
                break;
        }

        setMeasuredDimension(width, height);
    }

    private void layoutChildren(boolean changed, int l, int t,
                                  int r, int b) {

        long timeBeforeSolve = System.nanoTime();

        cassowaryModel.solve();

        log(
                       " container height " + cassowaryModel.getContainerNode().getHeight().value() +
                       " container width " + cassowaryModel.getContainerNode().getWidth().value() +
                       " container center x " + cassowaryModel.getContainerNode().getCenterX().value() +
                       " container center y " + cassowaryModel.getContainerNode().getCenterY().value()
                );
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childId = child.getId();
                Node node = getNodeById(childId);

                int x = (int) node.getLeft().value() + getPaddingLeft();
                int y = (int) node.getTop().value() + getPaddingTop();

                int width = (int) node.getWidth().value();
                int height = (int) node.getHeight().value();
                
                String childName = viewIdResolver.getViewNameById(child.getId());
                log("child " + childName  + " x " + x + " y " + y + " width " + width + " height " + height);

                if (node.hasIntrinsicHeight()) {
                    log("child " + childName  + " intrinsic height " + node.getIntrinsicHeight().value());
                }

                if (node.hasVariable(Node.CENTERX)) {
                    log("child " + childName  + " centerX " + node.getVariable(Node.CENTERX).value());
                }

                if (node.hasVariable(Node.CENTERY)) {
                    log("child " + childName + " centerY " + node.getVariable(Node.CENTERY).value());
                }

                child.layout(x, y, x + width ,y + height);
            }
        }
        log("onLayout - took " + TimerUtil.since(timeBeforeSolve));
    }

    private void readConstraintsFromXml(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CassowaryLayout,
                0, 0);

        try {
            final CharSequence[] constraints = a.getTextArray(R.styleable.CassowaryLayout_constraints);

            asyncSetup = a.getBoolean(R.styleable.CassowaryLayout_asyncSetup, asyncSetup);
            aspectRatioFixed = a.getBoolean(R.styleable.CassowaryLayout_aspectRatioFixed, aspectRatioFixed);
            aspectRatioWidthFactor = a.getFloat(R.styleable.CassowaryLayout_aspectRatioWidthFactor, aspectRatioWidthFactor);
            aspectRatioHeightFactor = a.getFloat(R.styleable.CassowaryLayout_aspectRatioHeightFactor, aspectRatioHeightFactor);

            log("readConstraintsFromXml asyncSetup " + asyncSetup );
            if (asyncSetup) {
                setupSolverAsync(constraints);
            } else {
                cassowaryModel.addConstraints(constraints);
                state = State.PARSING_COMPLETE;
            }

            if (constraints == null) {
                throw new RuntimeException("missing cassowary:constraints attribute in XML");
            }

        } finally {
            a.recycle();
        }

    }

    public Node getNodeById(int id) {
        Node node = cassowaryModel.getNodeByName(viewIdResolver.getViewNameById(id));
        return node;
    }

    private void log(String message) {
        try {
            logTag = "CassowaryLayout " + viewIdResolver.getViewNameById(getId()) + " " + hashCode();
        } catch (RuntimeException e) {
            logTag = "CassowaryLayout noid " + hashCode();
        }
        Log.d(logTag, message);
    }

    private boolean isMeasureSpecSet() {
        return widthMeasureSpec != null && heightMeasureSpec != null;
    }

    public boolean isAspectRatioFixed() {
        return aspectRatioFixed;
    }

    public void setAspectRatioFixed(boolean aspectRatioFixed) {
        this.aspectRatioFixed = aspectRatioFixed;
    }
}

