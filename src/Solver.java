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

    //When multiple items are removed from a variable, this object allows us to avoid adding and popping a lot of times
    static class StackItem {
        List<Integer> items;
        int recursionLevel;
        int varIndex;

        public StackItem(List<Integer> items, int recursionLevel, int varIndex) {
            this.items = items;
            this.recursionLevel = recursionLevel;
            this.varIndex = varIndex;
        }
    }

    static abstract class Constraint {
        /**
         * Tries to reduce the domain of the variables associated to this constraint, using inference
         */
        abstract void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<StackItem> stack);
        abstract void infer(Variable[] variables, Integer choice);
    }

    // Ascending constraint. Every next variable is greater than the previous one.
    static class AscendingConstraint extends Constraint {
        int counter = 0;
        public AscendingConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice) {}

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<StackItem> stack) {
            counter++;
            if (variables[choice].domain.isEmpty()) return;
            for (int i = choice; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                if (nextVar.choice != null) continue;
                List<Integer> removed = new ArrayList<>();
                ArrayList<Integer> remaining = new ArrayList<>();
                int min;
                if (currentVar.choice != null) {
                    min = currentVar.choice;
                } else {
                    min = Collections.min(currentVar.domain);
                }
//                int min = Math.min(currentVar.choice, Collections.min(currentVar.domain));
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
                stack.push(new StackItem(removed, recursionLevel, i+1));

            }

            //DOING TWO APPROACHES. ON TOP SORTING, WHICH IS BAD. ON THE BOTTOM THE SAME BUT SORTING-AMBIVALENT. FINISH.
            //ALSO, IF SORTING-AMBIVALENT -> LINE 50 AND LINE 74 DON'T USE MAX BUT INDEX, ALSO SOLVE.
            for (int i = choice; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                List<Integer> removed = new ArrayList<>();
                if (nextVar.choice != null) continue;
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
//                for (int val : removed) {
//                    stack.push(new int[]{val, i - 1, recursionLevel});
//                }
                stack.push(new StackItem(removed, recursionLevel, i-1));

            }
        }
    }


    // Ascending constraint. Every next variable is greater than the previous one.
    static class AscendingWithEqualConstraint extends Constraint {

        public AscendingWithEqualConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice) {}

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<StackItem> stack) {
            if (variables[choice].domain.isEmpty()) return;
            for (int i = choice; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                if (nextVar.choice != null) continue;
                List<Integer> removed = new ArrayList<>();
                ArrayList<Integer> remaining = new ArrayList<>();
                int min;
                if (currentVar.choice != null) {
                    min = currentVar.choice;
                } else {
                    min = Collections.min(currentVar.domain);
                }
//                int min = Math.min(currentVar.choice, Collections.min(currentVar.domain));
                //THIS IS DEPENDENT ON SORTING SOMEHOW? BUT NO BREAK STATEMENT
                for (int j = 0; j < nextVar.domain.size(); j++) {
                    if (currentVar.choice != null && nextVar.domain.get(j) >= currentVar.choice
                            || currentVar.choice == null && nextVar.domain.get(j) >= min) {
                        remaining.add(nextVar.domain.get(j));
                    } else {
                        removed.add(nextVar.domain.get(j));
                    }
                }
                variables[i + 1].domain = remaining;
                stack.push(new StackItem(removed, recursionLevel, i+1));

            }

            //DOING TWO APPROACHES. ON TOP SORTING, WHICH IS BAD. ON THE BOTTOM THE SAME BUT SORTING-AMBIVALENT. FINISH.
            //ALSO, IF SORTING-AMBIVALENT -> LINE 50 AND LINE 74 DON'T USE MAX BUT INDEX, ALSO SOLVE.
            for (int i = choice; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                List<Integer> removed = new ArrayList<>();
                if (nextVar.choice != null) continue;
                int max = Collections.max(currentVar.domain);
                for (int j = 0; j < nextVar.domain.size(); j++) {
                    if (currentVar.choice != null && (nextVar.domain.get(j) <= currentVar.choice
                            || nextVar.domain.get(j) <= max)) {
                        remaining.add(nextVar.domain.get(j));
                    }
                    else {
                        removed.add(nextVar.domain.get(j));
                    }
                }
                variables[i - 1].domain = remaining;
//                for (int val : removed) {
//                    stack.push(new int[]{val, i - 1, recursionLevel});
//                }
                stack.push(new StackItem(removed, recursionLevel, i-1));

            }
        }

    }

    // Ascending except 0 constraint. Every next variable is greater than the previous one.
    static class AscendingExceptZeroConstraint extends Constraint {

        public AscendingExceptZeroConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice) {}

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<StackItem> stack) {
            if (variables[choice].domain.isEmpty()) return;
            for (int i = choice; i < variables.length - 1; i++) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i + 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                if (nextVar.choice != null) continue;
                List<Integer> removed = new ArrayList<>();
                ArrayList<Integer> remaining = new ArrayList<>();
                int min;
                if (currentVar.choice != null) {
                    min = currentVar.choice;
                } else {
                    min = Collections.min(currentVar.domain);
                }
//                int min = Math.min(currentVar.choice, Collections.min(currentVar.domain));
                //THIS IS DEPENDENT ON SORTING SOMEHOW? BUT NO BREAK STATEMENT
                for (int j = 0; j < nextVar.domain.size(); j++) {
                    if (currentVar.choice != null && (currentVar.choice == 0 && nextVar.domain.get(j) == 0 || nextVar.domain.get(j) > currentVar.choice)
                            || currentVar.choice == null && (nextVar.domain.get(j) == 0 || nextVar.domain.get(j) > min)) {
                        remaining.add(nextVar.domain.get(j));
                    } else {
                        removed.add(nextVar.domain.get(j));
                    }
                }
                variables[i + 1].domain = remaining;
                stack.push(new StackItem(removed, recursionLevel, i+1));

            }

            //DOING TWO APPROACHES. ON TOP SORTING, WHICH IS BAD. ON THE BOTTOM THE SAME BUT SORTING-AMBIVALENT. FINISH.
            //ALSO, IF SORTING-AMBIVALENT -> LINE 50 AND LINE 74 DON'T USE MAX BUT INDEX, ALSO SOLVE.
            for (int i = choice; i > 0; i--) {
                Variable currentVar = variables[i];
                Variable nextVar = variables[i - 1];
                if (nextVar.domain.isEmpty() || currentVar.domain.isEmpty()) return;
                ArrayList<Integer> remaining = new ArrayList<>();
                List<Integer> removed = new ArrayList<>();
                if (nextVar.choice != null) continue;
                int max = Collections.max(currentVar.domain);
                for (int j = 0; j < nextVar.domain.size(); j++) {
                    if (currentVar.choice != null && (currentVar.choice == 0 && nextVar.domain.get(j) == 0 || nextVar.domain.get(j) < currentVar.choice)
                            || currentVar.choice == null && (nextVar.domain.get(j) == 0 || nextVar.domain.get(j) < max)) {
                        remaining.add(nextVar.domain.get(j));
                    }
                    else {
                        removed.add(nextVar.domain.get(j));
                    }
                }
                variables[i - 1].domain = remaining;

                stack.push(new StackItem(removed, recursionLevel, i-1));

            }
        }
    }


    // Not equal to other constraint. Every next variable is greater than the previous one.
    static class NotOtherConstraint extends Constraint {

        public NotOtherConstraint() {}

        @Override
        void infer(Variable[] variables, Integer choice) {}

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<StackItem> stack) {
            Variable currentVar = variables[choice];
            if (currentVar.domain.isEmpty()) return;
            int other = variables[choice].choice;
            for (int i = 0; i < variables.length; i++) {
                if (i != choice) {
//                    int other = variables[choice].domain.get(0);
                    Variable nextVar = variables[i];
                    if (nextVar.domain.isEmpty()) return;
                    ArrayList<Integer> remaining = new ArrayList<>();
                    List<Integer> removed = new ArrayList<>();
                    for (int j = 0; j < nextVar.domain.size(); j++) {
                        if (other != nextVar.domain.get(j)) {
                            remaining.add(nextVar.domain.get(j));
                        }
                        else {
                            removed.add(nextVar.domain.get(j));
                        }
                    }
                    nextVar.domain = remaining;

                    stack.push(new StackItem(removed, recursionLevel, i));
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
        void infer(Variable[] variables, Integer choice) {}

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<StackItem> stack) {
            if (variables[choice].domain.size() == 0) return;
            for (int i = 0; i < variables.length; i++) {
                if (i != choice) {
                    if (variables[i].choice != null) continue; //This line 3x's the solution xd
                    List<Integer> removed = new ArrayList<>();
                    List<Integer> remaining = new ArrayList<>();
                    for (int val : variables[i].domain) {
                        if (collidesMap.get(List.of(variables[choice].choice, val))) {
                            removed.add(val);
                        } else {
                            remaining.add(val);
                        }
                    }
                    variables[i].domain = remaining;
                    stack.push(new StackItem(removed, recursionLevel, i));
//                    variables[i].domain.removeIf(value -> collidesMap.get(List.of(variables[choice].domain.get(0), value)));
                }
            }
        }
    }

    static class SudokuNotCollideConstraint extends Constraint {
        HashMap<Integer, List<Integer>> collisions;

        public SudokuNotCollideConstraint(HashMap<Integer, List<Integer>> collisions) {
            this.collisions = collisions;
        }

        @Override
        void infer(Variable[] variables, Integer choice) {
        }

        @Override
        void infer(Variable[] variables, Integer choice, int recursionLevel, ArrayDeque<StackItem> stack) {
            if (variables[choice].domain.isEmpty()) return;
            Queue<Integer> queue = new LinkedList<>();
            queue.add(choice);
            ArrayList<Integer> checked = new ArrayList<>();
            while (!queue.isEmpty()) {
                int cur = queue.poll();
                for (int i : collisions.get(cur)) {
                    if (variables[i].choice != null) continue; //This line 3x's the solution xd
                    boolean wasSelected = variables[i].choice != null;
                    List<Integer> removed = new ArrayList<>();
                    List<Integer> remaining = new ArrayList<>();
                    for (int val : variables[i].domain) {
                        if (variables[cur].choice != null && variables[cur].choice == val) {
                            removed.add(val);
                        } else {
                            remaining.add(val);
                        }
                    }
                    variables[i].domain = remaining;
                    stack.push(new StackItem(removed, recursionLevel, i));
                    boolean isSelected = variables[i].choice != null;
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
    int recursionLevel;
    ArrayDeque<StackItem> stack; //slightly faster than stack
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
                StackItem nxt = stack.pop();
                if (nxt.recursionLevel >= recursionLevel) {
                    variables[nxt.varIndex].domain.addAll(nxt.items);
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

