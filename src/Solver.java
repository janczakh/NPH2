import java.util.*;

import static java.util.Collections.max;

class Solver {
    static class Variable {
        List<Integer> domain;

        /**
         * Constructs a new variable.
         * @param domain A list of values that the variable can take
         */
        public Variable(List<Integer> domain) {
            this.domain = domain;
        }
    }

    static abstract class Constraint {
        /**
         * Tries to reduce the domain of the variables associated to this constraint, using inference
         */
        abstract void infer(Variable[] variables);
    }

    // Example implementation of the Constraint interface.
    static abstract class BetweenFiveAndTenConstraint {
        Variable var;

        public BetweenFiveAndTenConstraint(Variable var) {
            this.var = var;
        }

        void infer() {
            List<Integer> newDomain = new LinkedList<>();

            for (Integer x : this.var.domain) {
                if (5 < x && x < 10)
                    newDomain.add(x);
            }

            this.var.domain = newDomain;
        }
    }

    // Ascending constraint. Every next variable is greater than the previous one.
    static class AscendingConstraint extends Constraint {

        public AscendingConstraint() {}

        @Override
        void infer(Variable[] variables) {
            for (int i = 0; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                int minPreviousValue = Collections.min(currentVar.domain);
                nextVar.domain.removeIf(value -> value <= minPreviousValue);
            }
            for (int i = variables.length - 1; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                int maxNextValue = Collections.max(currentVar.domain);
                nextVar.domain.removeIf(value -> value >= maxNextValue);
            }
        }
    }


    // Ascending constraint. Every next variable is greater than the previous one.
    static class AscendingWithEqualConstraint extends Constraint {

        public AscendingWithEqualConstraint() {}

        @Override
        void infer(Variable[] variables) {
            for (int i = 0; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                int minPreviousValue = Collections.min(currentVar.domain);
                nextVar.domain.removeIf(value -> value < minPreviousValue);
            }
            for (int i = variables.length - 1; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                int maxNextValue = Collections.max(currentVar.domain);
                nextVar.domain.removeIf(value -> value > maxNextValue);
            }
        }
    }

    // Ascending except 0 constraint. Every next variable is greater than the previous one.
    static class AscendingExceptZeroConstraint extends Constraint {

        public AscendingExceptZeroConstraint() {}

        @Override
        void infer(Variable[] variables) {
            for (int i = 0; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                int minPreviousValue = Collections.min(currentVar.domain);
                nextVar.domain.removeIf(value -> (value <= minPreviousValue && value != 0));
            }
            for (int i = variables.length - 1; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                int maxNextValue = Collections.max(currentVar.domain);
                nextVar.domain.removeIf(value -> (value >= maxNextValue && value != 0));
            }
        }
    }

    // Not equal to other constraint. Every next variable is greater than the previous one.
    static class NotOtherConstraint extends Constraint {

        public NotOtherConstraint() {}

        @Override
        void infer(Variable[] variables) {
            for (int i = 0; i < variables.length - 1; i++) {
                if (variables[i].domain.size() == 1) {
                    int index = i;
                    for (int j = 0; j < variables.length; j++) {
                        if (j != i) {
                            variables[j].domain.removeIf(value -> value.equals(variables[index].domain.get(0)));
                        }
                    }
                }
            }
        }
    }

    // Not collide constraint. Queens don't see each other vertically, horizontally and diagonally.
    static class NotCollideConstraint extends Constraint {
        HashMap<Integer, int[]> map;
        public NotCollideConstraint(HashMap<Integer, int[]> map) {
            this.map = map;
        }

        static boolean collides(int[] a, int[] b) {
            return a[0] == b[0] || a[1] == b[1] || Math.abs(a[0] - b[0]) == Math.abs(a[1] - b[1]);
        }

        @Override
        void infer(Variable[] variables) {
            for (int i = 0; i < variables.length; i++) {
                if (variables[i].domain.size() == 1) {
                    int index = i;
                    for (int j = 0; j < variables.length; j++) {
                        if (j != i) {
                            variables[j].domain.removeIf(value -> collides(map.get(variables[index].domain.get(0)), map.get(value)));
                        }
                    }
                }
            }
        }
    }

    // Not collide constraint. Sudoku doesn't collide vertically, horizontally and in 3x3 blocks.
    static class SudokuNotCollideConstraint extends Constraint {
        HashMap<Integer, int[]> map;
        HashMap<Integer, Integer> subnetMap;
        public SudokuNotCollideConstraint(HashMap<Integer, int[]> map, HashMap<Integer, Integer> subnetMap) {
            this.subnetMap = subnetMap;
            this.map = map;
        }

        boolean sudokuCollides(int a, int b) {
            return map.get(a)[0] == map.get(b)[0] || map.get(a)[1] == map.get(b)[1] || subnetMap.get(a).equals(subnetMap.get(b));
        }

        @Override
        void infer(Variable[] variables) {
            for (int i = 0; i < variables.length; i++) {
                if (variables[i].domain.size() == 1) {
                    int idx = i;
                    for (int j = 0; j < variables.length; j++) {
                        if (j != i && sudokuCollides(i, j)) {
                            variables[j].domain.removeIf(value -> value.equals(variables[idx].domain.get(0)));
                        }
                    }
                }
            }
        }
    }

    Variable[] variables;
    Constraint[] constraints;
    List<int[]> solutions;
    // you can add more attributes

    /**
     * Constructs a solver.
     * @param variables The variables in the problem
     * @param constraints The constraints applied to the variables
     */
    public Solver(Variable[] variables, Constraint[] constraints) {
        this.variables = variables;
        this.constraints = constraints;

        solutions = new LinkedList<>();
    }

    /**
     * Searches for one solution that satisfies the constraints.
     * @return The solution if it exists, else null
     */
    int[] findOneSolution() {
        solve(false, variables, constraints);

        return !solutions.isEmpty() ? solutions.get(0) : null;
    }

    List<int[]> findAllSolutions() {
        solve(true, variables, constraints);

        return solutions;
    }

    void solve(boolean findAllSolutions, Variable[] variables, Constraint[] constraints) {
        //when collapsing a variable, restrict its domain to a specific number but keep track of the domain
        //when backtracking, remove Var from domain somehow?
        int curVarIndex = findNextBestUndecided(variables);

        if (!findAllSolutions && !solutions.isEmpty()) return;

        //solution found
        if (curVarIndex == -1) {
            Variable[] nextVariables = copy(variables);
            for (Constraint constraint : constraints) {
                constraint.infer(nextVariables);
            }

            //If conflict -> a variable has empty domain -> return
            for (Variable v : nextVariables) {
                if (v.domain.isEmpty()) {
                    return;
                };
            }
            solutions.add(collapseSolution(variables));
            return;
        }

        Variable cur = variables[curVarIndex];
        for (Integer choice : cur.domain) {
            Variable[] nextVariables = copy(variables);

            //Collapse the domain of the variable to choice and infer from there
            nextVariables[curVarIndex].domain = new ArrayList<>(Collections.singletonList(choice));
            for (Constraint constraint : constraints) {
                constraint.infer(nextVariables);
            }

            //If conflict -> a variable has empty domain -> return
            boolean skip = false;
            for (Variable v : nextVariables) {
                if (v.domain.isEmpty()) {
                    skip = true;
                    break;
                };
            }
            if (skip) continue;
            solve(findAllSolutions, nextVariables, constraints);
        }
    }


    //Assuming all variables are of length 1, return an array of solutions
    int[] collapseSolution(Variable[] variables) {
        return Arrays.stream(variables).mapToInt(x -> x.domain.get(0)).toArray();
    }

    /**
     * Returns index of the variable with the smallest domain, but the domain has length >= 2
     * If a solution is already defined (i.e. all variables have a domain of 1) returns null
     * @return Best variable to compute with or null if all variables chosen
     */
    int findNextBestUndecided(Variable[] variables) {
        int best = -1;
        int bestNumber = Integer.MAX_VALUE;
        for (int i = 0; i < variables.length; i++) {
            Variable v = variables[i];
            if (v.domain.size() < bestNumber && v.domain.size() > 1) {
                best = i;
                bestNumber = v.domain.size();
            }
        }
        return best;
    }

    Variable[] copy(Variable[] variables) {
        Variable[] ret = new Variable[variables.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new Variable(new ArrayList<>(variables[i].domain));
        }
        return ret;
    }


    static class Node {
        Variable[] variables;
        int depth; //Represents which variable index should be considered now

        public Node(Variable[] variables, int depth) {
            this.variables = variables;
            this.depth = depth;
        }
    }
}

