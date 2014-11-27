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
import android.view.ViewParent;


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

    private final Handler handler = new Handler(Looper.getMainLooper());

    private float preSetupWidthHeightRatio = 1;

    private boolean nextMeasureAsync;

    private int outstandingChildMeasureCalls = 0;

    private enum State {
        UNINITIALIZED,
        PARSING_CONSTRAINTS,
        PARSING_COMPLETE,
        ASYNC_MEASURE,
        AWAITING_LAYOUT,
        SETUP_COMPLETE
    }

    private volatile State state = State.UNINITIALIZED;

    private Integer widthMeasureSpec;
    private Integer heightMeasureSpec;

    public interface CassowaryLayoutSetupCallback {
        void onCassowaryLayoutSetupComplete(CassowaryLayout layout);
    }

    private ArrayList<CassowaryLayoutSetupCallback> setupObservers;

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public CassowaryLayout(Context context, ViewIdResolver viewIdResolver) {
        super(context);
        this.viewIdResolver = viewIdResolver;
        this.cassowaryModel = new CassowaryModel(context);
    }

    public CassowaryLayout(Context context, ViewIdResolver viewIdResolver, CassowaryModel cassowaryModel) {
        this(context, viewIdResolver);
        this.cassowaryModel = cassowaryModel;
    }

    public CassowaryLayout(Context context) {
        super(context);
        this.viewIdResolver = new DefaultViewIdResolver(getContext());
        this.cassowaryModel = new CassowaryModel(context);

    }

    public CassowaryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.viewIdResolver = new DefaultViewIdResolver(getContext());
        cassowaryModel = new CassowaryModel(getContext().getApplicationContext());
        readConstraintsFromXml(attrs);

    }

    public CassowaryLayout(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        this.viewIdResolver = new DefaultViewIdResolver(getContext());
        readConstraintsFromXml(attrs);
        cassowaryModel = new CassowaryModel(getContext().getApplicationContext());
    }

    public CassowaryModel getCassowaryModel() {
        return cassowaryModel;
    }


    public void addSetupCallback(final CassowaryLayoutSetupCallback setupObserver) {

        if (isSetupComplete()) {
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

    protected void measureChildrenUsingNodes(int widthMeasureSpec, int heightMeasureSpec) {
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

    public void setupSolverAsync(final CharSequence[] constraints) {

        state = State.PARSING_CONSTRAINTS;

        log("setupSolverAsync - submitting task");

        parseConstraintsOnBackgroundsThread(constraints, new Runnable() {
            @Override
            public void run() {
                state = State.PARSING_COMPLETE;
                if (isMeasureSpecSet()) {
                    log("measureSpecSet calling asyncMeasure()");
                    asyncMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        });
    }

    private void setVariableToValue(String variableName, HashMap<String, Integer> nodesMap) {
        Iterator<Map.Entry<String, Integer>> iterator = nodesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            Node node = cassowaryModel.getNodeByName(entry.getKey());
            node.setVariableToValue(variableName, entry.getValue());
        }
    }

    CassowaryLayout getRootCassowaryLayout() {
        CassowaryLayout rootLayout = this;
        ViewParent parent = this;
        while ((parent = parent.getParent()) != null) {
            if (parent instanceof CassowaryLayout) {
                rootLayout = (CassowaryLayout) parent;
            }
        }
        return rootLayout;
    }

    CassowaryLayout getParentCassowaryLayout() {
        CassowaryLayout cassowaryParent = null;
        ViewParent parent = this;
        while ((parent = parent.getParent()) != null) {
            if (parent instanceof CassowaryLayout) {
                cassowaryParent = (CassowaryLayout) parent;
                break;
            }
        }
        return cassowaryParent;
    }

    private void asyncMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {

        state = State.ASYNC_MEASURE;

        // on main thread
        setMeasureSpecOnBackgroundThread(widthMeasureSpec, heightMeasureSpec, new Runnable() {
            @Override
            public void run() {
                // back on main thread

                measureChildrenUsingNodes(widthMeasureSpec, heightMeasureSpec);
                if (outstandingChildMeasureCalls == 0) {

                    setIntrinsicConstraints();
                }

            }
        });

    }

    private void notifyParentComplete() {
        state = State.AWAITING_LAYOUT;
        CassowaryLayout parent = getParentCassowaryLayout();
        if (parent == null) {
            // this object is the
            requestLayout();
        } else {
            parent.handleChildComplete();
        }
    }

    private void handleChildComplete() {
        outstandingChildMeasureCalls--;
        if (outstandingChildMeasureCalls == 0) {
            measureChildrenUsingNodes(widthMeasureSpec, heightMeasureSpec);
            setIntrinsicConstraints();
        }
    }

    private void incrementOutstandingCallsOnParent() {
        CassowaryLayout parent = getParentCassowaryLayout();
        if (parent != null) {
            parent.outstandingChildMeasureCalls++;
        }
    }

    private void setIntrinsicConstraints() {
        setIntrinsicConstraintsOnBackgroundThread(getIntrinsicHeights(),
                getIntrinsicWidths(), new Runnable() {

                    @Override
                    public void run() {
                        // outstandingChildMeasureCalls--;
                        log("async measure complete outstanding calls " + outstandingChildMeasureCalls);

                        notifyParentComplete();
                        callbackAfterSetup();
                    }
                });
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {


        long before = System.nanoTime();

        log("onMeasure width " +
                MeasureSpecUtils.getModeAsString(widthMeasureSpec) + " " +
                MeasureSpec.getSize(widthMeasureSpec) + " height " +
                MeasureSpecUtils.getModeAsString(heightMeasureSpec) + " " +
                MeasureSpec.getSize(heightMeasureSpec) + " in state " + state + " nextMeasureAsync " + nextMeasureAsync);

       boolean measureSpecChanged = saveMeasureSpec(widthMeasureSpec, heightMeasureSpec);

        switch(state) {
            case UNINITIALIZED:
            case PARSING_CONSTRAINTS:
                placeHolderMeasure(widthMeasureSpec, heightMeasureSpec);
                if (measureSpecChanged) {
                    incrementOutstandingCallsOnParent();
                }
                // wait for parsing to complete
                break;
            case ASYNC_MEASURE:
            case PARSING_COMPLETE:
                // kick off new asyncMeasure - (even if one is already in progress)
                if (measureSpecChanged) {
                    incrementOutstandingCallsOnParent();
                    asyncMeasure(widthMeasureSpec, heightMeasureSpec);
                }
                placeHolderMeasure(widthMeasureSpec, heightMeasureSpec);
                break;
            case AWAITING_LAYOUT:
                setMeasuredDimensionsFromCassowaryModel(widthMeasureSpec, heightMeasureSpec);
                state = State.SETUP_COMPLETE;
                break;
            case SETUP_COMPLETE:
                if (nextMeasureAsync) {
                    state = State.PARSING_COMPLETE;
                    asyncMeasure(widthMeasureSpec, heightMeasureSpec);
                    placeHolderMeasure(widthMeasureSpec, heightMeasureSpec);
                    nextMeasureAsync = false;
                } else {
                    onMeasureSync(widthMeasureSpec, heightMeasureSpec);
                }
                break;
            }

        log("onMeasure took " + TimerUtil.since(before) + " state now " + state);

    }


    private void onMeasureSync(int widthMeasureSpec, int heightMeasureSpec) {
        long timeBeforeSolve = System.nanoTime();

        log("postSetupOnMeasure width " +
                MeasureSpecUtils.getModeAsString(widthMeasureSpec) + " " +
                MeasureSpec.getSize(widthMeasureSpec) + " height " +
                MeasureSpecUtils.getModeAsString(heightMeasureSpec) + " " +
                MeasureSpec.getSize(heightMeasureSpec));

        setMeasureSpecOnCassowaryModel(widthMeasureSpec, heightMeasureSpec);
        cassowaryModel.solve();
        measureChildrenUsingNodes(widthMeasureSpec, heightMeasureSpec);
        setVariableToValue(ChildNode.INTRINSIC_HEIGHT, getIntrinsicHeights());
        setVariableToValue(ChildNode.INTRINSIC_HEIGHT, getIntrinsicWidths());
        setMeasuredDimensionsFromCassowaryModel(widthMeasureSpec, heightMeasureSpec);

        log("onMeasure took " + TimerUtil.since(timeBeforeSolve));
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

   private void setMeasureSpecOnBackgroundThread(final int widthMeasureSpec, final int heightMeasureSpec, final Runnable callback) {
       final int widthWithoutPadding =  MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
       final int heightWithoutPadding =  MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
       final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
       final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                setMeasureSpecOnCassowaryModel(heightMode, widthMode, heightWithoutPadding, widthWithoutPadding);
                cassowaryModel.solve();
                handler.postAtFrontOfQueue(callback);
            }
        });
    }

    private void setIntrinsicConstraintsOnBackgroundThread(final HashMap<String, Integer> intrinsicHeights,
                                                           final HashMap<String, Integer> intrinsicWidths,
                                                           final Runnable callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                setVariableToValue(ChildNode.INTRINSIC_HEIGHT, intrinsicHeights);
                setVariableToValue(ChildNode.INTRINSIC_WIDTH, intrinsicWidths);
                cassowaryModel.solve();
                handler.postAtFrontOfQueue(callback);
            }
        });
    }

    private void parseConstraintsOnBackgroundsThread(final CharSequence[] constraints, final Runnable callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                cassowaryModel.addConstraints(constraints);
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


    protected void placeHolderMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width =  MeasureSpec.getSize(widthMeasureSpec);
        int height =  MeasureSpec.getSize(heightMeasureSpec);

        log("placeHolderMeasure width " +
                MeasureSpecUtils.getModeAsString(widthMeasureSpec) + " " +
                width + " height " +
                MeasureSpecUtils.getModeAsString(heightMeasureSpec) + " " +
                height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthBasedOnRatio = (int)(height * getPreSetupWidthHeightRatio());

        int heightBasedOnRatio = (int)(width / getPreSetupWidthHeightRatio());

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                if (heightMode == MeasureSpec.EXACTLY) {
                    if (widthBasedOnRatio < width) {
                        width = widthBasedOnRatio;
                    }
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
                if (widthMode == MeasureSpec.EXACTLY) {
                    if (heightBasedOnRatio < height) {
                        height = heightBasedOnRatio;
                    }
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

    private void setAllChildViewsTo(int visibility) {
        final int size = getChildCount();

        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            child.setVisibility(visibility);
        }

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
        if (isSetupComplete()) {
            setAllChildViewsTo(View.VISIBLE);
            layoutChildren(changed, l, t, r, b);
        } else {
            setAllChildViewsTo(View.INVISIBLE);
        }

    }

    protected void layoutChildren(boolean changed, int l, int t,
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

                CassowaryLayout.LayoutParams lp =
                        (CassowaryLayout.LayoutParams) child.getLayoutParams();

                int childId = child.getId();
                Node node = getNodeById(childId);

                int x = (int) node.getLeft().value() + getPaddingLeft();
                int y = (int) node.getTop().value() + getPaddingTop();

                int width = (int) node.getWidth().value();
                int height = (int) node.getHeight().value();
                log("child " + viewIdResolver.getViewNameById(child.getId())  + " x " + x + " y " + y + " width " + width + " height " + height);

                if (node.hasIntrinsicHeight()) {
                    log("child " + viewIdResolver.getViewNameById(child.getId())  + " intrinsic height " + node.getIntrinsicHeight().value());
                }

                if (node.hasVariable(Node.CENTERX)) {
                    log("child " + viewIdResolver.getViewNameById(child.getId())  + " centerX " + node.getVariable(Node.CENTERX).value());
                }

                if (node.hasVariable(Node.CENTERY)) {
                    log("child " + viewIdResolver.getViewNameById(child.getId())  + " centerY " + node.getVariable(Node.CENTERY).value());
                }

                child.layout(x, y,
                        x + /*child.getMeasuredWidth()*/ width ,
                        y + /*child.getMeasuredHeight() */+ height);

            }
        }
        log("onLayout - took " + TimerUtil.since(timeBeforeSolve));
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


    public static class LayoutParams extends ViewGroup.LayoutParams {

        public LayoutParams() {
            super(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.AbsoluteLayout_Layout);
            a.recycle();
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

    private void readConstraintsFromXml(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CassowaryLayout,
                0, 0);

        try {
            final CharSequence[] constraints = a.getTextArray(R.styleable.CassowaryLayout_constraints);

            boolean asyncSetup = a.getBoolean(R.styleable.CassowaryLayout_asyncSetup, true);

            preSetupWidthHeightRatio = a.getFloat(R.styleable.CassowaryLayout_preSetupWidthHeightRatio, preSetupWidthHeightRatio);

            log("readConstraintsFromXml asyncSetup " + asyncSetup );
            if (asyncSetup) {
                setupSolverAsync(constraints);
            } else {
                cassowaryModel.addConstraints(constraints);
                state = State.SETUP_COMPLETE;
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

    private boolean isSetupComplete() {
        return state == State.SETUP_COMPLETE;
    }

    public float getPreSetupWidthHeightRatio() {
        return preSetupWidthHeightRatio;
    }

    public void setPreSetupWidthHeightRatio(float preSetupWidthHeightRatio) {
        this.preSetupWidthHeightRatio = preSetupWidthHeightRatio;
    }

    public boolean isNextMeasureAsync() {
        return nextMeasureAsync;
    }

    public void setNextMeasureAsync(boolean nextMeasureAsync) {
        this.nextMeasureAsync = nextMeasureAsync;
    }

    private boolean isMeasureSpecSet() {
        return widthMeasureSpec != null && heightMeasureSpec != null;
    }

}

