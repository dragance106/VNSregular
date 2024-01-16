/**
 * Implementation of a Variable Neighborhood Search (VNS) for the complete bipartite graph partitioning problem.
 *
 * @author Dragan Stevanovic
 * @version January 11, 2024
 */
public class VNS
{
    private RRGCreator rrg;

    private Solution current;
    private Solution bestSoFar;

    private static int MAXSHAKE = 100;       // how much shaking at most before steepest descent
    private static long MAXTIME = 100000;    // in milliseconds
    
    /**
     * Constructor for objects of class VNS
     */
    public VNS() {
        rrg = new RRGCreator();
    }
    
    public void randomInitialSolution(int n, int d) throws IllegalArgumentException {
        System.out.println("Generating a random regular graph as an initial solution...");
        int[][] A = rrg.randomRegularGraph(n, d);
        current = new Solution(n, d, A);
        current.report();
    }
    
    /** 
     * The entry point for the variable neighborhood search
     */
    public double startVNS(int n, int d) {
        // memorize the initial time 
        long initialTime = System.currentTimeMillis();
        long currentTime;
        
        // Initial solution is a random regular graph...
        try {
            randomInitialSolution(n, d);
        } catch (IllegalArgumentException e) {
            System.out.println("No regular graph with n="+n+" and d="+d+" exists. Stopping execution...");
            return -1;
        }

        System.out.println("The first steepest descent...");
        current.performSteepestDescent();
        
        // copy this solution to the best one so far
        bestSoFar = new Solution();
        bestSoFar.copySolution(current);

        currentTime = System.currentTimeMillis();
        System.out.print("time="+(currentTime-initialTime)+"ms, ");
        bestSoFar.report();
        
        // the main loop of the VNS
        int howMuchShaking=1;
        while (true) {
            if (howMuchShaking==1)
                System.out.print("shaking...");
            System.out.print(howMuchShaking+" ");
            
            if (current.shake(howMuchShaking)<0) {          // we came across a graph having vertices with equal neighborhoods
                try {
                    randomInitialSolution(n, d);                // just restart again with a new random regular graph...
                } catch (IllegalArgumentException e) {
                    // just ignore - we would have already caught this exception above...
                }
                howMuchShaking=1;
            }
            current.performSteepestDescent();
            
            if (current.neqtd < bestSoFar.neqtd) {
                bestSoFar.copySolution(current);
                
                System.out.println();
                System.out.println("better solution found:");
                currentTime = System.currentTimeMillis();
                System.out.print("time="+(currentTime-initialTime)+"ms, ");
                bestSoFar.report();
                
                howMuchShaking=1;
                
                if (bestSoFar.neqtd==0)
                    // we found what we wanted!
                    break;
            }
            else
                howMuchShaking++;
            
            if (howMuchShaking>MAXSHAKE)
                break;
            
            currentTime = System.currentTimeMillis();
            if (currentTime-initialTime>MAXTIME)
                break;
        }
        
        if (howMuchShaking>MAXSHAKE) {
            System.out.println();
            System.out.println("Stopping: maximum amount of shaking reached without improvement...");
        }
            
        if (currentTime-initialTime>MAXTIME) {
            System.out.println();
            System.out.println("Stopping: maximum amount of time ("+(currentTime-initialTime)+"ms) passed without improvement...");
        }

        System.out.println("Best solution found: ");
        bestSoFar.reportFull();
        return bestSoFar.value;
    }
    
    /**
     * The entry point to the program from the command line.
     */
    public static void main(String[] args) {
        VNS w = new VNS();
        if (args[0].equals("?")||args[0].equals("-?")||args[0].equals("-h")||args[0].equals("-help")) {
            // report how the program should be started
            System.out.println("The program should be started by calling");
            System.out.println("  java -jar tdvns.jar n d [-s<maxshaking>] [-t<maxseconds>]");
            System.out.println("Default values are maxshaking=100 and maxseconds=100.");
            System.out.println();        
        }
        else {
            // generate instance data randomly from the values of |V1|, |V2| and n
            int n = Integer.parseInt(args[0]);
            int d = Integer.parseInt(args[1]);
            
            for (int i=2; i<args.length; i++) {
                if (args[i].substring(0,2).equals("-s")) {
                    MAXSHAKE = Integer.parseInt(args[i].substring(2));
                    System.out.println("Maximum amount of shaking set to " + MAXSHAKE + " edge switchings.");
                }
                if (args[i].substring(0,2).equals("-t")) {
                    MAXTIME = 1000*Integer.parseInt(args[i].substring(2));
                    System.out.println("Maximum runtime set to " + MAXTIME + " milliseconds.");
                }
            }
            
            double result=n*n;
            while (result>n-0.0001)
                result = w.startVNS(n,d);
        }
    }
}