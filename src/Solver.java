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
        abstract void infer(/* you can add params */);
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
            solutions.add(collapseSolution(variables));
            return;
        }

        Variable cur = variables[curVarIndex];
        for (Integer choice : cur.domain) {
            Variable[] nextVariables = copy(variables);

            //Collapse the domain of the variable to choice and infer from there
            nextVariables[curVarIndex].domain = List.of(choice);
            for (Constraint constraint : constraints) {
                constraint.infer();
            }

            //If conflict -> a variable has empty domain -> return
            for (Variable v : nextVariables) {
                if (v.domain.isEmpty()) return;
            }

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

