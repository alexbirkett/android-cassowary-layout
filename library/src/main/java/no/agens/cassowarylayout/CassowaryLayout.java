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

import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClSimplexSolver;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;
import org.klomp.cassowary.clconstraint.ClLinearEquation;

import java.util.ArrayList;
import java.util.HashMap;

import no.agens.cassowarylayout.util.CassowaryUtil;
import no.agens.cassowarylayout.util.DimensionParser;
import no.agens.cassowarylayout.util.TimerUtil;

public class CassowaryLayout extends ViewGroup  {

    private static final String LOG_TAG = "CassowaryLayout";

    private HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

    private ClSimplexSolver solver = new ClSimplexSolver();

    private ContainerNode containerNode = new ContainerNode(solver);

    private ViewIdResolver viewIdResolver;

    private CassowaryVariableResolver cassowaryVariableResolver = new CassowaryVariableResolver() {
        @Override
        public ClVariable resolveVariable(String variableName) {
            return CassowaryLayout.this.resolveVariable(variableName);
        }

        @Override
        public ClLinearExpression resolveConstant(String constantName) {

            ClLinearExpression expression = null;
            Double value;

            try {
                value = new Double(Double.parseDouble(constantName));
            } catch (NumberFormatException e) {
                value = DimensionParser.getDimension(constantName, getContext());

            }

            if (value != null) {
                expression = new ClLinearExpression(value);
            }

            return expression;
        }


    };


    private ClVariable resolveVariable(String variableName) {
        ClVariable variable = null;

        String[] stringArray = variableName.split("\\.");

        if (stringArray.length > 1) {
            String viewName = stringArray[0];
            String propertyName = stringArray[1];

            if (viewName != null) {
                if ("container".equals(viewName) || "parent".equals(viewName)) {
                    if ("height".equals(propertyName)) {
                        variable = containerNode.getHeight();
                    } else if ("width".equals(propertyName)) {
                        variable = containerNode.getWidth();
                    } else if ("centerX".equals(propertyName)) {
                        variable = containerNode.getCenterX();
                    } else if ("centerY".equals(propertyName)) {
                        variable = containerNode.getCenterY();
                    }
                } else {
                    Node node = getNodeById(viewIdResolver.getViewIdByName(viewName));
                    if (node != null) {
                        variable = node.getVariableByName(propertyName);
                    }

                }
            }

        }
        if (variable == null) {
            throw new RuntimeException("unknown variable " + variableName);
        }
        return variable;
    }

    public Node getNodeById(int id) {
        Node node = nodes.get(id);
        if (node == null) {
            node = new Node(solver, containerNode);
            nodes.put(id, node);
        }
        return node;
    }

    public CassowaryLayout(Context context, ViewIdResolver viewIdResolver) {
        super(context);
        setupCassowary();
        this.viewIdResolver = viewIdResolver;
    }

    public CassowaryLayout(Context context) {
        super(context);
        setupCassowary();
        this.viewIdResolver = new DefaultViewIdResolver(getContext());
    }

    public CassowaryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupCassowary();
        this.viewIdResolver = new DefaultViewIdResolver(getContext());
        readConstraintsFromXml(attrs);
    }

    public CassowaryLayout(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        setupCassowary();
        this.viewIdResolver = new DefaultViewIdResolver(getContext());
        readConstraintsFromXml(attrs);
    }

    private ArrayList<ClConstraint> dynamicConstraints = new ArrayList<ClConstraint>();

    private ClLinearEquation getUnusedWeakEqualityConstraint() {
        return CassowaryUtil.createWeakEqualityConstraint();
    }

    private void removeDynamicConstraints() {
        for (ClConstraint constraint : dynamicConstraints) {
            solver.removeConstraint(constraint);
        }
        dynamicConstraints.clear();
    }

    private void updateIntrinsicWidthConstraints() {
        long timeBeforeSolve = System.currentTimeMillis();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                Node node = getNodeById(child.getId());
                ClVariable intrinsicWidth = node.getIntrinsicWidth();
                if (intrinsicWidth != null) {
                    int childWidth = child.getMeasuredWidth();
                    Log.d(LOG_TAG, "child " + viewIdResolver.getViewNameById(child.getId()) + " intrinsic width " + childWidth);
                    if ((int)intrinsicWidth.getValue() != childWidth) {
                        node.setIntrinsicWidth(childWidth);
                    }

                }
            }
        }
        Log.d(LOG_TAG, "updateIntrinsicWidthConstraints took " +  TimerUtil.since(timeBeforeSolve));
    }

    private void updateIntrinsicHeightConstraints() {

       long timeBeforeSolve = System.currentTimeMillis();

       int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                Node node = getNodeById(child.getId());
                ClVariable intrinsicHeight = node.getIntrinsicHeight();
                if (intrinsicHeight != null) {
                    int childHeight = child.getMeasuredHeight();


                    Log.d(LOG_TAG, "child " + viewIdResolver.getViewNameById(child.getId()) + " intrinsic height (measured)" + childHeight);
                    if ((int)intrinsicHeight.getValue() != childHeight) {
                        long timeBeforeGetMeasuredHeight = System.currentTimeMillis();

                        node.setIntrinsicHeight(childHeight);
                        Log.d(LOG_TAG, "node.setIntrinsicHeight took " +  TimerUtil.since(timeBeforeGetMeasuredHeight));
                    }
                }

            }

        }
        Log.d(LOG_TAG, "updateIntrinsicHeightConstraints took " +  TimerUtil.since(timeBeforeSolve));
    }

    protected void measureHeightBasedOnWidth(int widthMeasureSpec, int heightMeasureSpec) {
        long timeBeforeSolve = System.currentTimeMillis();

        final int size = getChildCount();

        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {


                Node node = getNodeById(child.getId());

                if (node.getIntrinsicHeight() != null) {

                    int width = (int) node.getWidth().getValue();
                    Log.d(LOG_TAG, "measureHeightBasedOnWidth child " + viewIdResolver.getViewNameById(child.getId()) + " width " + width);

                    int childHeightSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.getMode(heightMeasureSpec));

                    int childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);

                    Log.d(LOG_TAG, "child " + viewIdResolver.getViewNameById(child.getId()) + " parent measured width " +  MeasureSpec.getSize(childWidthSpec) + " mode " + MeasureSpec.getMode(childWidthSpec) );

                    measureChild(child, childWidthSpec, childHeightSpec);



                }
             }
        }
        Log.d(LOG_TAG, "measureHeightBasedOnWidth took " +  TimerUtil.since(timeBeforeSolve));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(LOG_TAG, "onMesaure");

        long timeBeforeSolve = System.currentTimeMillis();
        removeDynamicConstraints();


        int resolvedWidth = -1;
        int resolvedHeight = -1;

        int heightSpec = MeasureSpec.getMode(heightMeasureSpec);
        if (heightSpec == MeasureSpec.EXACTLY) {
            resolvedHeight = resolveSizeAndState(0, heightMeasureSpec, 0);
            containerNode.setContainerHeight(resolvedHeight - getPaddingTop() - getPaddingBottom());
        } else if (heightSpec == MeasureSpec.AT_MOST) {
            int maxHeight =  MeasureSpec.getSize(heightMeasureSpec);
            containerNode.setContainerHeightToAtMost(maxHeight - getPaddingTop() - getPaddingBottom());
        }

        int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
        if (widthSpec == MeasureSpec.EXACTLY) {
            resolvedWidth = resolveSizeAndState(0, widthMeasureSpec, 0);
            containerNode.setContainerWidth(resolvedWidth - getPaddingLeft() - getPaddingRight());
        } else if (widthSpec == MeasureSpec.AT_MOST) {
            int maxWidth =  MeasureSpec.getSize(widthMeasureSpec);
            containerNode.setContainerWidthToAtMost(maxWidth - getPaddingLeft() - getPaddingRight());
        }


        measureChildren(widthMeasureSpec, heightMeasureSpec);

        updateIntrinsicWidthConstraints();

        solve();

        measureHeightBasedOnWidth(widthMeasureSpec, heightMeasureSpec);

        updateIntrinsicHeightConstraints();

        solve();

        if (widthSpec == MeasureSpec.AT_MOST || widthSpec == MeasureSpec.UNSPECIFIED) {
            resolvedWidth = (int) containerNode.getWidth().getValue() + getPaddingLeft() + getPaddingRight();
        }

        if (heightSpec == MeasureSpec.AT_MOST || heightSpec == MeasureSpec.UNSPECIFIED) {
            resolvedHeight = (int) containerNode.getHeight().getValue() + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(resolvedWidth, resolvedHeight);
        Log.d(LOG_TAG, "onMeasure took " + TimerUtil.since(timeBeforeSolve));
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * and with the coordinates (0, 0).
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t,
                            int r, int b) {

        long timeBeforeSolve = System.currentTimeMillis();

        solve();

        Log.d(LOG_TAG,
                       " container height " + containerNode.getHeight().getValue() +
                       " container width " + containerNode.getWidth().getValue() +
                       " container center x " + containerNode.getCenterX().getValue() +
                       " container center y " + containerNode.getCenterY().getValue()
                );
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                CassowaryLayout.LayoutParams lp =
                        (CassowaryLayout.LayoutParams) child.getLayoutParams();

                int childId = child.getId();
                Node node = getNodeById(childId);

                int x = (int) node.getLeft().getValue() + getPaddingLeft();
                int y = (int) node.getTop().getValue() + getPaddingTop();

                int width = (int) node.getWidth().getValue();
                int height = (int) node.getHeight().getValue();
                Log.d(LOG_TAG, "child " + viewIdResolver.getViewNameById(child.getId())  + " x " + x + " y " + y + " width " + width + " height " + height);

                if (node.getIntrinsicHeight() != null) {
                    Log.d(LOG_TAG, "child " + viewIdResolver.getViewNameById(child.getId())  + " intrinsic height " + node.getIntrinsicHeight().getValue());
                }

                child.layout(x, y,
                        x + /*child.getMeasuredWidth()*/ width ,
                        y + /*child.getMeasuredHeight() */+ height);

            }
        }
        Log.d(LOG_TAG, "onLayout - took " + TimerUtil.since(timeBeforeSolve));
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

    /**
     * Per-child layout information associated with AbsoluteLayout.
     * See
     * {@link android.R.styleable#AbsoluteLayout_Layout Absolute Layout Attributes}
     * for a list of all child view attributes that this class supports.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {

        /**
         * The horizontal, or X, location of the child within the view group.
         */
        //public int x;
        /**
         * The vertical, or Y, location of the child within the view group.
         */
        //public int y;

        /**
         * Creates a new set of layout parameters with the specified width,
         * height and location.
         *
         * @param width the width, either {@link #MATCH_PARENT},
        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height the height, either {@link #MATCH_PARENT},
        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param x the X location of the child
         * @param y the Y location of the child
         */
        public LayoutParams(int width, int height) {
            super(width, height);
            //this.x = x;
            //this.y = y;
        }

        /**
         * Creates a new set of layout parameters. The values are extracted from
         * the supplied attributes set and context. The XML attributes mapped
         * to this set of layout parameters are:
         *
         * <ul>
         *   <li><code>layout_x</code>: the X location of the child</li>
         *   <li><code>layout_y</code>: the Y location of the child</li>
         *   <li>All the XML attributes from
         *   {@link android.view.ViewGroup.LayoutParams}</li>
         * </ul>
         *
         * @param c the application environment
         * @param attrs the set of attributes from which to extract the layout
         *              parameters values
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.AbsoluteLayout_Layout);
           /* x = a.getDimensionPixelOffset(
                    R.styleable.AbsoluteLayout_Layout_layout_x, 0);
            y = a.getDimensionPixelOffset(
                    R.styleable.AbsoluteLayout_Layout_layout_y, 0);*/
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
    }

    private void setupCassowary() {
        Log.d(LOG_TAG, "setupCassowary");
        solver.setAutosolve(false);
    }

    public void addConstraint(ClConstraint constraint) {
            solver.addConstraint(constraint);
    }

    public ClConstraint addConstraint(String constraintString) {
        Log.d(LOG_TAG, "adding constraint " + constraintString);
        ClConstraint constraint = CassowaryConstraintParser.parseConstraint(constraintString, cassowaryVariableResolver);
        addConstraint(constraint);
        return constraint;
    }

    public void removeConstraint(ClConstraint constraint) {
        solver.removeConstraint(constraint);
    }

    public void addConstraints(CharSequence[] constraints) {
        for (CharSequence constraint : constraints) {
            addConstraint(constraint.toString());
        }
    }

    public void addConstraints(int id) {
        String[] constraints = getResources().getStringArray(id);
        addConstraints(constraints);
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

            addConstraints(constraints);
        } finally {
            a.recycle();
        }

    }

    private void solve() {
        long timeBeforeSolve = System.currentTimeMillis();

        solver.solve();

        Log.d(LOG_TAG, "solve took " + TimerUtil.since(timeBeforeSolve));
    }
}

