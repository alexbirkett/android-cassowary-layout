package no.agens.cassowarylayout;

import android.content.Context;
import android.util.Log;

import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClSimplexSolver;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;

import java.util.ArrayList;
import java.util.HashMap;

import no.agens.cassowarylayout.util.DimensionParser;
import no.agens.cassowarylayout.util.TimerUtil;

/**
 * Created by alex on 01/11/14.
 */
public class CassowaryModel {

    private Context context;

    private static final String LOG_TAG = "CassowaryModel";
    public CassowaryModel(Context context, ViewIdResolver viewIdResolver) {
        this.viewIdResolver = viewIdResolver;
        this.context = context;
        setupCassowary();
    }

    private ArrayList<ClConstraint> dynamicConstraints = new ArrayList<ClConstraint>();

    private HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

    private ClSimplexSolver solver = new ClSimplexSolver();

    private ContainerNode containerNode = new ContainerNode(solver);

    private ViewIdResolver viewIdResolver;

    private CassowaryVariableResolver cassowaryVariableResolver = new CassowaryVariableResolver() {
        @Override
        public ClVariable resolveVariable(String variableName) {
            return CassowaryModel.this.resolveVariable(variableName);
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

    private void removeDynamicConstraints() {
        for (ClConstraint constraint : dynamicConstraints) {
            solver.removeConstraint(constraint);
        }
        dynamicConstraints.clear();
    }

    private Context getContext() {
        return context;
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
