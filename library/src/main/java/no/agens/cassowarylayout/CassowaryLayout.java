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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import org.pybee.cassowary.Variable;

import no.agens.cassowarylayout.util.MeasureSpecUtils;
import no.agens.cassowarylayout.util.TimerUtil;

public class CassowaryLayout extends ViewGroup  {

    private String logTag;

    private CassowaryModel cassowaryModel;

    private ViewIdResolver viewIdResolver;

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
        cassowaryModel = new CassowaryModel(context);
        readConstraintsFromXml(attrs);
    }

    public CassowaryLayout(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        this.viewIdResolver = new DefaultViewIdResolver(getContext());
        this.cassowaryModel = new CassowaryModel(context);
        readConstraintsFromXml(attrs);
    }


    public CassowaryModel getCassowaryModel() {
        return cassowaryModel;
    }


    private void updateIntrinsicWidthConstraints() {
        long timeBeforeSolve = System.currentTimeMillis();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                Node node = getHorizontalNode(child);
                if (node.hasVariable(ChildNode.INTRINSIC_LENGTH)) {
                    int childWidth = child.getMeasuredWidth();
                    log("child " + viewIdResolver.getViewNameById(child.getId()) + " intrinsic width " + childWidth);
                    if ((int)node.getVariableValue(ChildNode.INTRINSIC_LENGTH) != childWidth) {
                        node.setVariableToValue(ChildNode.INTRINSIC_LENGTH, childWidth);
                    }

                }
            }
        }
        log("updateIntrinsicWidthConstraints took " + TimerUtil.since(timeBeforeSolve));
    }

    private void updateIntrinsicHeightConstraints() {

       long timeBeforeSolve = System.currentTimeMillis();

       int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                Node node = getVerticalNode(child);

                if (node.hasVariable(ChildNode.INTRINSIC_LENGTH)) {
                    int childHeight = child.getMeasuredHeight();
                    Variable intrinsicHeight = node.getVariable(ChildNode.INTRINSIC_LENGTH);
                    log("child " + viewIdResolver.getViewNameById(child.getId()) + " intrinsic height (measured)" + childHeight);
                    if ((int)intrinsicHeight.value() != childHeight) {
                        long timeBeforeGetMeasuredHeight = System.currentTimeMillis();

                        node.setVariableToValue(ChildNode.INTRINSIC_LENGTH, childHeight);
                        log("node.setIntrinsicHeight took " + TimerUtil.since(timeBeforeGetMeasuredHeight));
                    }
                }

            }

        }
        log("updateIntrinsicHeightConstraints took " + TimerUtil.since(timeBeforeSolve));
    }

    protected void measureChildrenUsingNodes(int parentWidthMode, int parentHeightMode) {
        long timeBeforeSolve = System.currentTimeMillis();

        final int size = getChildCount();

        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                Node verticalNode = getVerticalNode(child);
                Node horizontalNode  = getHorizontalNode(child);

                int nodeHeight = (int) verticalNode.getVariableValue(ChildNode.LENGTH);
                int nodeWidth = (int) horizontalNode.getVariableValue(ChildNode.LENGTH);

                int widthMode = parentWidthMode;
                if (horizontalNode.hasVariable(ChildNode.INTRINSIC_LENGTH)) {
                    widthMode = MeasureSpec.UNSPECIFIED;
                    nodeWidth = 0;
                }

                int heightMode = parentHeightMode;
                if (verticalNode.hasVariable(ChildNode.INTRINSIC_LENGTH)) {
                    heightMode = MeasureSpec.UNSPECIFIED;
                    nodeHeight = 0;
                }


                int childHeightSpec = MeasureSpec.makeMeasureSpec(nodeHeight, heightMode);
                int childWidthSpec = MeasureSpec.makeMeasureSpec(nodeWidth, widthMode);

                measureChild(child, childWidthSpec, childHeightSpec);
            }
            log("measureChildrenUsingNodes took " + TimerUtil.since(timeBeforeSolve));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        long timeBeforeSolve = System.currentTimeMillis();

        int width =  MeasureSpec.getSize(widthMeasureSpec);
        int height =  MeasureSpec.getSize(heightMeasureSpec);

        log("onMeasure width " +
           MeasureSpecUtils.getModeAsString(widthMeasureSpec) + " " +
                width + " height " +
           MeasureSpecUtils.getModeAsString(heightMeasureSpec) + " " +
                height);


        int resolvedWidth = -1;
        int resolvedHeight = -1;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            cassowaryModel.getContainerNode().setVariableToAtMost(ContainerNode.HEIGHT, height - getPaddingTop() - getPaddingBottom());
        } else if (heightMode == MeasureSpec.EXACTLY) {
            cassowaryModel.getContainerNode().setVariableToValue(ContainerNode.HEIGHT, height - getPaddingTop() - getPaddingBottom());
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            cassowaryModel.getContainerNode().setVariableToAtMost(ContainerNode.WIDTH, width - getPaddingLeft() - getPaddingRight());
        } else {
            cassowaryModel.getContainerNode().setVariableToValue(ContainerNode.WIDTH, width - getPaddingLeft() - getPaddingRight());
        }

        cassowaryModel.solve();

        measureChildrenUsingNodes(widthMode, heightMode);

        updateIntrinsicWidthConstraints();
        updateIntrinsicHeightConstraints();

        cassowaryModel.solve();

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            resolvedWidth = (int) cassowaryModel.getContainerNode().getVariableValue(ContainerNode.WIDTH) + getPaddingLeft() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            resolvedHeight = (int) cassowaryModel.getContainerNode().getVariableValue(ContainerNode.HEIGHT) + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(resolvedWidth, resolvedHeight);

        log("onMeasure took " + TimerUtil.since(timeBeforeSolve));
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

        long timeBeforeSolve = System.currentTimeMillis();

        cassowaryModel.solve();

        log(
                " container height " + cassowaryModel.getContainerNode().getVariableValue(ContainerNode.HEIGHT) +
                        " container width " + cassowaryModel.getContainerNode().getVariableValue(ContainerNode.WIDTH) +
                        " container center x " + cassowaryModel.getContainerNode().getVariableValue(ContainerNode.CENTERX) +
                        " container center y " + cassowaryModel.getContainerNode().getVariableValue(ContainerNode.CENTERY)
        );
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                Node verticalNode = getVerticalNode(child);
                Node horizontalNode  = getHorizontalNode(child);

                int x = (int) horizontalNode.getVariableValue(ChildNode.START) + getPaddingLeft();
                int y = (int) verticalNode.getVariableValue(ChildNode.START) + getPaddingTop();

                int width = (int) horizontalNode.getVariableValue(ChildNode.LENGTH);
                int height = (int) verticalNode.getVariableValue(ChildNode.LENGTH);
                log("child " + viewIdResolver.getViewNameById(child.getId()) + " x " + x + " y " + y + " width " + width + " height " + height);

                if (verticalNode.hasVariable(ChildNode.INTRINSIC_LENGTH)) {
                    log("child " + viewIdResolver.getViewNameById(child.getId()) + " intrinsic height " + verticalNode.getVariableValue(ChildNode.INTRINSIC_LENGTH));
                }

                if (horizontalNode.hasVariable(ChildNode.CENTER)) {
                    log("child " + viewIdResolver.getViewNameById(child.getId()) + " centerX " + horizontalNode.getVariableValue(ChildNode.CENTER));
                }

                if (verticalNode.hasVariable(ChildNode.CENTER)) {
                    log("child " + viewIdResolver.getViewNameById(child.getId()) + " centerY " + verticalNode.getVariable(ChildNode.CENTER));
                }

                child.layout(x, y,
                        x + /*child.getMeasuredWidth()*/ width ,
                        y + /*child.getMeasuredHeight() */+ height);

            }
        }
        log("onLayout - took " + TimerUtil.since(timeBeforeSolve));
    }

    public void setChildPositionsFromCassowaryModel() {
        long timeBeforeSolve = System.currentTimeMillis();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                Node verticalNode = getVerticalNode(child);
                Node horizontalNode = getHorizontalNode(child);

                int x = (int) horizontalNode.getVariableValue(ChildNode.START) + getPaddingLeft();
                int y = (int) verticalNode.getVariableValue(ChildNode.START) + getPaddingTop();

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

        private String horizontalNode;
        private String verticalNode;

        public LayoutParams() {
            super(0, 0);
        }


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.CassowaryLayout_Layout);
            horizontalNode = a.getString(R.styleable.CassowaryLayout_Layout_horizontalNode);
            verticalNode = a.getString(R.styleable.CassowaryLayout_Layout_verticalNode);
            a.recycle();
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public String debug(String output) {
           /* return output + "Absolute.LayoutParams={width="
                    + width + ", height=" + height
                    + " x=" + x + " y=" + y + "}";*/
            return "";
        }

        public String getHorizontalNode() {
            return horizontalNode;
        }

        public void setHorizontalNode(String horizontalNode) {
            this.horizontalNode = horizontalNode;
        }

        public String getVerticalNode() {
            return verticalNode;
        }

        public void setVerticalNode(String verticalNode) {
            this.verticalNode = verticalNode;
        }
    }


    private void readConstraintsFromXml(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CassowaryLayout,
                0, 0);

        try {
            CharSequence[] constraints = a.getTextArray(R.styleable.CassowaryLayout_constraints);

            if (constraints == null) {
                throw new RuntimeException("missing cassowary:constraints attribute in XML");
            }

            long timebefore = System.currentTimeMillis();
            cassowaryModel.addConstraints(constraints);
            log("addConstraints took " + TimerUtil.since(timebefore));
        } finally {
            a.recycle();
        }

    }

    public Node getHorizontalNode(View view) {
        return cassowaryModel.getHorizontalNodeByName(getHorizontalNodeName(view));
    }

    public Node getVerticalNode(View view) {
        return cassowaryModel.getVerticalNodeByName(getVerticalNodeName(view));
    }

    private String getVerticalNodeName(View view) {
        String name = null;
        CassowaryLayout.LayoutParams layoutParams = (CassowaryLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            name = layoutParams.getVerticalNode();
        }

        if (name == null) {
            name = viewIdResolver.getViewNameById(view.getId());
        }
        return name;
    }

    private String getHorizontalNodeName(View view) {
        String name = null;
        CassowaryLayout.LayoutParams layoutParams = (CassowaryLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            name = layoutParams.getHorizontalNode();
        }

        if (name == null) {
            name = viewIdResolver.getViewNameById(view.getId());
        }
        return name;
    }

    private void log(String message) {
        if (logTag == null) {
            try {
                logTag = "CassowaryLayout " + viewIdResolver.getViewNameById(getId());
            } catch (RuntimeException e) {
                logTag = "CassowaryLayout noid";
            }
        }
        Log.d(logTag, message);
    }
}

