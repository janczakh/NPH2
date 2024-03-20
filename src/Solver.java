import java.util.*;

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
        abstract void infer(Variable[] variables, Integer choice);
    }

    // Ascending constraint. Every next variable is greater than the previous one.
    static class AscendingConstraint extends Constraint {

        public AscendingConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice) {
            if (variables[choice].domain.isEmpty()) return;
            for (int i = choice; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                for (int j = nextVar.domain.size() - 1; j >= 0; j--) {
                    if (nextVar.domain.get(j) > currentVar.domain.get(0)) {
                        remaining.add(nextVar.domain.get(j));
                    } else {
                        Collections.reverse(remaining);
                        variables[i + 1].domain = remaining;
                        break;
                    }
                }
            }
            for (int i = choice; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                for (int j = 0; j < nextVar.domain.size() - 1; j++) {
                    if (nextVar.domain.get(j) < currentVar.domain.get(currentVar.domain.size() - 1)) {
                        remaining.add(nextVar.domain.get(j));
                    } else {
                        variables[i - 1].domain = remaining;
                        break;
                    }
                }
            }
        }
    }


    // Ascending constraint. Every next variable is greater than the previous one.
    static class AscendingWithEqualConstraint extends Constraint {

        public AscendingWithEqualConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice) {
            if (variables[choice].domain.isEmpty()) return;
            for (int i = choice; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                for (int j = nextVar.domain.size() - 1; j >= 0; j--) {
                    if (nextVar.domain.get(j) >= currentVar.domain.get(0)) {
                        remaining.add(nextVar.domain.get(j));
                    } else {
                        Collections.reverse(remaining);
                        variables[i + 1].domain = remaining;
                        break;
                    }
                }
            }
            for (int i = choice; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                for (int j = 0; j < nextVar.domain.size() - 1; j++) {
                    if (nextVar.domain.get(j) <= currentVar.domain.get(currentVar.domain.size() - 1)) {
                        remaining.add(nextVar.domain.get(j));
                    } else {
                        variables[i - 1].domain = remaining;
                        break;
                    }
                }
            }
        }
    }

    // Ascending except 0 constraint. Every next variable is greater than the previous one.
    static class AscendingExceptZeroConstraint extends Constraint {

        public AscendingExceptZeroConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice) {
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
        void infer(Variable[] variables, Integer choice) {
            for (int i = 0; i < variables.length - 1; i++) {
                if (i != choice) {
                    int other = variables[choice].domain.get(0);
                    variables[i].domain.removeIf(value -> value.equals(other));
                }
            }
        }
    }

    // Not collide constraint. Queens don't see each other vertically, horizontally and diagonally.
    static class NotCollideConstraint extends Constraint {
        HashMap<Integer, int[]> map;
        HashMap<List<Integer>, Boolean> collidesMap;
        public NotCollideConstraint(HashMap<Integer, int[]> map, HashMap<List<Integer>, Boolean> collidesMap) {
            this.map = map;
            this.collidesMap = collidesMap;
        }

        @Override
        void infer(Variable[] variables, Integer choice) {
            if (variables[choice].domain.size() == 0) return;
            for (int i = 0; i < variables.length; i++) {
                if (i != choice) {
                    variables[i].domain.removeIf(value -> collidesMap.get(List.of(variables[choice].domain.get(0), value)));
                }
            }
        }
    }

    // Not collide constraint. Sudoku doesn't collide vertically, horizontally and in 3x3 blocks.
    static class SudokuNotCollideConstraint extends Constraint {
        HashMap<Integer, List<Integer>> collisions;
        public SudokuNotCollideConstraint(HashMap<Integer, List<Integer>> collisions) {
            this.collisions = collisions;
        }

        @Override
        void infer(Variable[] variables, Integer choice) {
            if (variables[choice].domain.isEmpty()) return;
            Queue<Integer> queue = new LinkedList<>();
            queue.add(choice);
            ArrayList<Integer> checked = new ArrayList<>();
            while (!queue.isEmpty()) {
                int cur = queue.poll();
                for (int i : collisions.get(cur)) {
                    boolean wasSelected = variables[i].domain.size() == 1;
                    variables[i].domain.removeIf(value -> value.equals(variables[cur].domain.get(0)));
                    boolean isSelected = variables[i].domain.size() == 1;
                    if (variables[i].domain.isEmpty()) return;
                    if (!wasSelected && isSelected && !checked.contains(i)) {
                        queue.add(i);
                        checked.add(i);
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
                for (int i = 0; i < variables.length; i++) {
                    constraint.infer(nextVariables, i);
                }
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
                constraint.infer(nextVariables, curVarIndex);
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
}

