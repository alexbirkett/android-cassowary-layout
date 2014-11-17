package no.agens.cassowarylayout;

import android.content.Context;
import android.util.Log;

import org.pybee.cassowary.Constraint;
import org.pybee.cassowary.ConstraintNotFound;
import org.pybee.cassowary.Expression;
import org.pybee.cassowary.SimplexSolver;
import org.pybee.cassowary.Variable;

import java.util.HashMap;

import no.agens.cassowarylayout.util.DimensionParser;
import no.agens.cassowarylayout.util.TimerUtil;

/**
 * Created by alex on 01/11/14.
 */
public class CassowaryModel {

    private Context context;

    private static final String LOG_TAG = "CassowaryModel";

    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String CENTERX = "centerX";
    public static final String CENTERY = "centerY";
    public static final String INTRINSIC_WIDTH = "intrinsicWidth";
    public static final String INTRINSIC_HEIGHT = "intrinsicHeight";

    public CassowaryModel(Context context) {
        this.context = context;
        setupCassowary();
    }

    private HashMap<String, ChildNode> horizontalNodes = new HashMap<String, ChildNode>();
    private HashMap<String, ChildNode> verticalNodes = new HashMap<String, ChildNode>();

    private SimplexSolver solver = new SimplexSolver();

    private ContainerNode containerNode = new ContainerNode(solver);

    private CassowaryVariableResolver cassowaryVariableResolver = new CassowaryVariableResolver() {
        @Override
        public Variable resolveVariable(String variableName) {
            return CassowaryModel.this.resolveVariable(variableName);
        }

        @Override
        public Expression resolveConstant(String constantName) {

            Expression expression = null;
            Double value;

            try {
                value = new Double(Double.parseDouble(constantName));
            } catch (NumberFormatException e) {
                value = DimensionParser.getDimension(constantName, getContext());

            }

            if (value != null) {
                expression = new Expression(value);
            }
            return expression;
        }
    };


    private Variable resolveVariable(String variableName) {
        Variable variable = null;

        String[] stringArray = variableName.split("\\.");

        if (stringArray.length > 1) {
            String nodeName = stringArray[0];
            String propertyName = stringArray[1];

            if (nodeName != null) {
                if ("container".equals(nodeName) || "parent".equals(nodeName)) {
                    variable = containerNode.getVariable(propertyName);
                } else {
                    variable = resolveProperty(nodeName, getCanonicalName(propertyName));
                }
            }
        }
        if (variable == null) {
            throw new RuntimeException("unknown variable " + variableName);
        }
        return variable;
    }


    private Variable resolveProperty(String nodeName, String propertyName) {
        Variable variable = null;

        if (LEFT.equals(propertyName)) {
            variable = getHorizontalNodeByName(nodeName).getVariable(ChildNode.START);
        } else if (RIGHT.equals(propertyName)) {
            variable = getHorizontalNodeByName(nodeName).getVariable(ChildNode.END);
        } else if (TOP.equals(propertyName)) {
            variable = getVerticalNodeByName(nodeName).getVariable(ChildNode.START);
        } else if (BOTTOM.equals(propertyName)) {
            variable = getVerticalNodeByName(nodeName).getVariable(ChildNode.END);
        } else if (CENTERX.equals(propertyName)) {
            variable = getHorizontalNodeByName(nodeName).getVariable(ChildNode.CENTER);
        } else if (CENTERY.equals(propertyName)) {
            variable = getVerticalNodeByName(nodeName).getVariable(ChildNode.CENTER);
        } else if (INTRINSIC_HEIGHT.equals(propertyName)) {
            variable = getVerticalNodeByName(nodeName).getVariable(ChildNode.INTRINSIC_LENGTH);
        } else if (INTRINSIC_WIDTH.equals(propertyName)) {
            variable = getHorizontalNodeByName(nodeName).getVariable(ChildNode.INTRINSIC_LENGTH);
        } else if (HEIGHT.equals(propertyName)) {
            variable = getVerticalNodeByName(nodeName).getVariable(ChildNode.LENGTH);
        } else if (WIDTH.equals(propertyName)) {
            variable = getHorizontalNodeByName(nodeName).getVariable(ChildNode.LENGTH);
        } else  {
            variable = getHorizontalNodeByName(nodeName).getVariable(propertyName);
        }
        return  variable;

    }
    private String getCanonicalName(String name) {
        String canonicalName = name;
        if ("x".equals(name)) {
            canonicalName = LEFT;
        } else if ("y".equals(name)) {
            canonicalName = TOP;
        } else if ("x2".equals(name)) {
            canonicalName = RIGHT;
        } else if ("y2".equals(name)) {
            canonicalName = BOTTOM;
        }
        return canonicalName;
    }

    public ChildNode getHorizontalNodeByName(String name) {
        ChildNode node = horizontalNodes.get(name);
        if (node == null) {
            node = new ChildNode(solver);
            horizontalNodes.put(name, node);
        }
        return node;
    }

    public ChildNode getVerticalNodeByName(String name) {
        ChildNode node = verticalNodes.get(name);
        if (node == null) {
            node = new ChildNode(solver);
            verticalNodes.put(name, node);
        }
        return node;
    }

    private Context getContext() {
        return context;
    }

    private void setupCassowary() {
        Log.d(LOG_TAG, "setupCassowary");
        solver.setAutosolve(false);
    }

    public void addConstraint(Constraint constraint) {
        solver.addConstraint(constraint);
    }

    public Constraint addConstraint(String constraintString) {
        Log.d(LOG_TAG, "adding constraint " + constraintString);
        Constraint constraint = CassowaryConstraintParser.parseConstraint(constraintString, cassowaryVariableResolver);
        addConstraint(constraint);
        return constraint;
    }

    public void removeConstraint(Constraint constraint) {
        try {
            solver.removeConstraint(constraint);
        } catch (ConstraintNotFound constraintNotFound) {
            constraintNotFound.printStackTrace();
        }
    }

    public void addConstraints(CharSequence[] constraints) {
        for (CharSequence constraint : constraints) {
            addConstraint(constraint.toString());
        }
    }

    public void addConstraints(int id) {
        String[] constraints = context.getResources().getStringArray(id);
        addConstraints(constraints);
    }

    public ContainerNode getContainerNode() {
        return containerNode;
    }

    public void solve() {
        long timeBeforeSolve = System.currentTimeMillis();

        solver.solve();

        Log.d(LOG_TAG, "solve took " + TimerUtil.since(timeBeforeSolve));
    }

}
