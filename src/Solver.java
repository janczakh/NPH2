import java.util.*;

class Solver {
    static class Variable {
        List<Integer> domain;
        Integer choice;

        /**
         * Constructs a new variable.
         * @param domain A list of values that the variable can take
         */
        public Variable(List<Integer> domain) {
            this.domain = domain;
            this.choice = null;
        }

        public Variable(List<Integer> domain, Integer choice) {
            this.domain = domain;
            this.choice = choice;
        }
    }

    static abstract class Constraint {
        /**
         * Tries to reduce the domain of the variables associated to this constraint, using inference
         */
        abstract void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<int[]> stack);
        abstract void infer(Variable[] variables, Integer choice);
    }

    // Ascending constraint. Every next variable is greater than the previous one.
    static class AscendingConstraint extends Constraint {

        public AscendingConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice) {}

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<int[]> stack) {
            if (variables[choice].domain.isEmpty()) return;
            for (int i = choice; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                List<Integer> removed = new ArrayList<>();
                ArrayList<Integer> remaining = new ArrayList<>();
                int min = Collections.min(currentVar.domain);
//                for (int j = nextVar.domain.size() - 1; j >= 0; j--) {
                //THIS IS DEPENDENT ON SORTING SOMEHOW? BUT NO BREAK STATEMENT
                for (int j = 0; j < nextVar.domain.size(); j++) {
                    if (currentVar.choice != null && nextVar.domain.get(j) > currentVar.choice
                            || currentVar.choice == null && nextVar.domain.get(j) > min) {
                        remaining.add(nextVar.domain.get(j));
                    } else {
                        removed.add(nextVar.domain.get(j));
                    }
                }
                variables[i + 1].domain = remaining;
                for (int val : removed) {
                    stack.push(new int[]{val, i+1, recursionLevel});
                }

            }

            //DOING TWO APPROACHES. ON TOP SORTING, WHICH IS BAD. ON THE BOTTOM THE SAME BUT SORTING-AMBIVALENT. FINISH.
            //ALSO, IF SORTING-AMBIVALENT -> LINE 50 AND LINE 74 DON'T USE MAX BUT INDEX, ALSO SOLVE.
            for (int i = choice; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                List<Integer> removed = new ArrayList<>();
//                if ((nextVar.domain.size() - 1) == 0) return;
                int max = Collections.max(currentVar.domain);
                for (int j = 0; j < nextVar.domain.size(); j++) {
                    if (currentVar.choice != null && nextVar.domain.get(j) < currentVar.choice
                            || currentVar.choice == null && nextVar.domain.get(j) < max) {
                        remaining.add(nextVar.domain.get(j));
                    }
                    else {
                        removed.add(nextVar.domain.get(j));
                    }
                }
                variables[i - 1].domain = remaining;
                for (int val : removed) {
                    stack.push(new int[]{val, i - 1, recursionLevel});
                }

            }
        }
    }


    // Ascending constraint. Every next variable is greater than the previous one.
    static class AscendingWithEqualConstraint extends Constraint {

        public AscendingWithEqualConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<int[]> stack) {}

        @Override
        void infer(Variable[] variables, Integer choice) {
            if (variables[choice].domain.isEmpty()) return;
            for (int i = choice; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                for (int j = nextVar.domain.size() - 1; j >= 0; j--) {
                    if (currentVar.choice != null && nextVar.domain.get(j) >= currentVar.choice || currentVar.choice == null && nextVar.domain.get(j) >= currentVar.domain.get(0)) {
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
                    if (currentVar.choice != null && nextVar.domain.get(j) <= currentVar.choice || currentVar.choice == null && nextVar.domain.get(j) <= currentVar.domain.get(currentVar.domain.size() - 1)) {
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
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<int[]> stack) {}

        @Override
        void infer(Variable[] variables, Integer choice) {
            for (int i = 0; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                int minPreviousValue = currentVar.choice == null ? Collections.min(currentVar.domain) : currentVar.choice;
                nextVar.domain.removeIf(value -> (value <= minPreviousValue && value != 0));
            }
            for (int i = variables.length - 1; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                int maxNextValue = currentVar.choice == null ? Collections.max(currentVar.domain) : currentVar.choice;
                nextVar.domain.removeIf(value -> (value >= maxNextValue && value != 0));
            }
        }
    }


    // Not equal to other constraint. Every next variable is greater than the previous one.
    static class NotOtherConstraint extends Constraint {

        public NotOtherConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<int[]> stack) {}

        @Override
        void infer(Variable[] variables, Integer choice) {
            for (int i = 0; i < variables.length; i++) {
                if (i != choice) {
//                    int other = variables[choice].domain.get(0);
                    int other = variables[choice].choice;
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
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<int[]> stack) {}

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
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<int[]> stack) {}

        @Override
        void infer(Variable[] variables, Integer choice) {
            for (int i = 0; i < variables.length; i++) {
//                if (variables[i].domain.size() == 1) { //TODO
                if (variables[i].choice != null) { //TODO
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
    int recursionLevel;
    ArrayDeque<int[]> stack; //slightly faster than stack
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
        recursionLevel = 0;
        stack = new ArrayDeque<>();
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
//        variables = new Variable[]{new Variable(List.of(0,1,2), 0), new Variable(List.of(0,1,2)), new Variable(List.of(0,1,2), 0)};
        //solution found
        if (curVarIndex == -1) {
            for (Constraint constraint : constraints) {
                for (int i = 0; i < variables.length; i++) {
                    constraint.infer(variables, i, recursionLevel, stack);
                }
            }

            //If conflict -> a variable has empty domain -> return
            for (Variable v : variables) {
                if (v.domain.isEmpty()) {
                    return;
                };
            }
            if (!verifyChoiceInDomain(variables)) return;
            solutions.add(collapseSolution(variables));
            return;
        }

        Variable cur = variables[curVarIndex];
        for (Integer choice : new ArrayList<>(cur.domain)) {
            variables[curVarIndex].choice = choice;
            //Collapse the domain of the variable to choice and infer from there
            for (Constraint constraint : constraints) {
                constraint.infer(variables, curVarIndex, recursionLevel, stack);
            }

            //If conflict -> a variable has empty domain -> return
            boolean skip = false;
            for (Variable v : variables) {
                if (v.domain.isEmpty()) {
                    skip = true;
                    break;
                };
            }
            if (!skip && verifyChoiceInDomain(variables)) {
                recursionLevel++;
                solve(findAllSolutions, variables, constraints);
                recursionLevel--;
            }

            //Restore from the stack up to the current recursion layer
            while (!stack.isEmpty()) {
                int[] nxt = stack.pop();
                if (nxt[2] >= recursionLevel) {
                    variables[nxt[1]].domain.add(nxt[0]);
                } else {
                    stack.push(nxt);
                    break;
                }
            }
        }
        cur.choice = null;
    }


    //Assuming all variables are of length 1, return an array of solutions
    int[] collapseSolution(Variable[] variables) {
//        return Arrays.stream(variables).mapToInt(x -> x.domain.get(0)).toArray();
        return Arrays.stream(variables).mapToInt(x -> x.choice).toArray();
    }

    boolean verifyChoiceInDomain(Variable[] variables) {
        for (Variable v : variables) {
            if (v.choice != null && !v.domain.contains(v.choice)) return false;
        }
        return true;
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
            if (v.choice == null && v.domain.size() < bestNumber) {
                best = i;
                bestNumber = v.domain.size();
            }
        }
        return best;
    }

    Variable[] copy(Variable[] variables) {
        Variable[] ret = new Variable[variables.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new Variable(new ArrayList<>(variables[i].domain), variables[i].choice);
        }
        return ret;
    }
}

