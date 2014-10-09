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

import org.klomp.cassowary.CL;
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

    private HashMap<Integer, ViewModel> viewModels = new HashMap<Integer, ViewModel>();

    private ClVariable containerWidth = new ClVariable();
    private ClVariable  containerHeight = new ClVariable();

    private ClLinearEquation containerWidthConstraint;
    private ClLinearEquation containerHeightConstraint;



    private ClSimplexSolver solver = new ClSimplexSolver();

    private ViewIdResolver viewIdResolver;

    private CassowaryVariableResolver cassowaryVariableResolver = new CassowaryVariableResolver() {
        @Override
        public ClVariable resolveVariable(String variableName) {
            ClVariable variable = null;

            if ("container.width".equals(variableName)) {
                return containerWidth;
            } else if ("container.height".equals(variableName)) {
                return containerHeight;
            } else {
                String[] stringArray = variableName.split("\\.");
                ViewModel viewModel = null;
                String viewName = stringArray[0];
                if (viewName != null) {
                    viewModel = getViewById(viewIdResolver.getViewId(viewName));
                }
                String propertyName = stringArray[1];
                if (viewModel != null) {
                    if ("left".equals(propertyName) || "x".equals(propertyName)) {
                        variable = viewModel.getX();
                    } else if ("top".equals(propertyName) || "y".equals(propertyName)) {
                        variable = viewModel.getY();
                    } else if ("height".equals(propertyName)) {
                        variable = viewModel.getHeight();
                    } else if ("width".equals(propertyName)) {
                        variable = viewModel.getWidth();
                    }
                }
            }
            if (variable == null) {
                throw new RuntimeException("unknown variable " + variableName);
            }
            return variable;
        }

        @Override
        public ClLinearExpression resolveConstant(String name) {
            Integer dimension = DimensionParser.getDimension(name, getContext());
            if (dimension != null) {
                return new ClLinearExpression(dimension);
            }
            return null;
        }
    };


    public ViewModel getViewById(int id) {
        ViewModel viewModel = viewModels.get(id);
        if (viewModel == null) {
            viewModel = new ViewModel();
            viewModels.put(id, viewModel);
        }
        return viewModel;
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

    private void createWrapContentConstraints() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                ViewModel viewModel = getViewById(child.getId());
                ClLinearEquation heightConstraint = getUnusedWeakEqualityConstraint();
                CassowaryUtil.updateConstraint(heightConstraint, viewModel.getHeight(), child.getMeasuredHeight());
                solver.addConstraint(heightConstraint);
                dynamicConstraints.add(heightConstraint);

                ClLinearEquation widthConstraint = getUnusedWeakEqualityConstraint();
                CassowaryUtil.updateConstraint(widthConstraint, viewModel.getWidth(), child.getMeasuredWidth());
                solver.addConstraint(widthConstraint);
                dynamicConstraints.add(widthConstraint);
            }

        }
    }

    private void createPaddingConstraints() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                ViewModel viewModel = getViewById(child.getId());
                int leftPadding = getPaddingLeft();
                int topPadding = getPaddingTop();
                Log.d(LOG_TAG, "child id " + child.getId() + " left padding " + leftPadding + " top padding " + topPadding);
                ClConstraint paddingLeft = CassowaryUtil.createWeakInequalityConstraint(viewModel.getX(), CL.GEQ, leftPadding);
                solver.addConstraint(paddingLeft);
                dynamicConstraints.add(paddingLeft);

                ClConstraint paddingRight = CassowaryUtil.createWeakInequalityConstraint(viewModel.getY(), CL.GEQ, topPadding);
                solver.addConstraint(paddingRight);
                dynamicConstraints.add(paddingRight);
            }


        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(LOG_TAG, "onMesaure");


        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;


        // Find out how big everyone wants to be
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        removeDynamicConstraints();
        createWrapContentConstraints();
        createPaddingConstraints();

        // Find rightmost and bottom-most child
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childRight;
                int childBottom;

                ViewModel viewModel = getViewById(child.getId());

                childRight = (int)viewModel.getX().getValue() + child.getMeasuredWidth();
                childBottom = (int)viewModel.getY().getValue() + child.getMeasuredHeight();

                maxWidth = Math.max(maxWidth, childRight);
                maxHeight = Math.max(maxHeight, childBottom);
            }
        }

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingRight();

        // Check against minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        int resolvedWidth = resolveSizeAndState(maxWidth, widthMeasureSpec, 0);
        int resolvedHeight = resolveSizeAndState(maxHeight, heightMeasureSpec, 0);
        setMeasuredDimension(resolvedWidth, resolvedHeight);


        CassowaryUtil.updateConstraint(containerWidthConstraint, containerWidth, resolvedWidth);
        solver.removeConstraint(containerWidthConstraint);
        solver.addConstraint(containerWidthConstraint);

        CassowaryUtil.updateConstraint(containerHeightConstraint, containerHeight, resolvedHeight);
        solver.removeConstraint(containerHeightConstraint);
        solver.addConstraint(containerHeightConstraint);

        Log.d(LOG_TAG, "onMesaure width " + resolvedWidth + " height " + resolvedHeight);
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

        solver.solve();

        Log.d(LOG_TAG, "onLayout - Resolve took " + TimerUtil.since(timeBeforeSolve));
        Log.d(LOG_TAG, "container height " + containerHeight.getValue() + " container width " + containerWidth.getValue() );
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                CassowaryLayout.LayoutParams lp =
                        (CassowaryLayout.LayoutParams) child.getLayoutParams();

                int childId = child.getId();
                ViewModel viewModel = getViewById(childId);

                int x = (int)viewModel.getX().getValue();
                int y = (int)viewModel.getY().getValue();

                int width = (int)viewModel.getWidth().getValue();
                int height = (int)viewModel.getHeight().getValue();
                Log.d(LOG_TAG, "child id " + childId + " x " + x + " y " + y + " width " + width + " height " + height);


                child.layout(x, y,
                        x + /*child.getMeasuredWidth()*/ width ,
                        y + /*child.getMeasuredHeight() */+ height);

            }
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
        containerWidthConstraint = new ClLinearEquation(containerWidth, new ClLinearExpression(0));
        solver.addConstraint(containerWidthConstraint);
        containerHeightConstraint =  new ClLinearEquation(containerHeight, new ClLinearExpression(0));
        solver.addConstraint(containerHeightConstraint);
    }

    public void addConstraint(ClConstraint constraint) {
            solver.addConstraint(constraint);
    }

    public void addConstraint(String constraint) {
        Log.d(LOG_TAG, "adding constraint " + constraint);
        addConstraint(CassowaryConstraintParser.parseConstraint(constraint, cassowaryVariableResolver));
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
            addConstraints(constraints);
        } finally {
            a.recycle();
        }

    }
}

