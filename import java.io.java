import java.io.*;
import java.util.*;



public class Solution {
static int n,m;


static int time; 

@SuppressWarnings("unchecked")
static ArrayList<Integer>[] adj = new ArrayList[100000];

static int[] parent; 

static int[] found; 

static int[] low; 

static Stack<Integer> s = new Stack<>(); 

static int[] in_stack; 
    
    public static void dfs(int node, ArrayList<ArrayList<Integer>> sccs ){
        found[node] = ++time;
        low[node] = found[node]; 
        s.push(node); 
        in_stack[node] = 1;
        
        for (Integer u : adj[node]) {
                if (parent[u] != -1) {
                    
                    if (in_stack[u] == 1) {
                        low[node] = Math.min(low[node], found[u]);
                    }
                    continue;
                }

                parent[u] = node;

                dfs(u, sccs);


                low[node] = Math.min(low[node], low[u]);
            }
        
        if (low[node] == found[node]) {
                ArrayList<Integer> scc = new ArrayList<>();
                do {
                    Integer x = s.peek();
                    s.pop();
                    in_stack[x] = 0;

                    scc.add(x);

                 
                } while (scc.get(scc.size() - 1) != node); 

                sccs.add(scc);
            }
    }
    
    public static ArrayList<ArrayList<Integer>> tarjan_scc(){
            parent = new int[n];
            found = new int[n];
            low = new int[n];
            in_stack = new int[n];

            Arrays.fill(parent, -1);
            Arrays.fill(found, -1);
            Arrays.fill(low, -1);
            Arrays.fill(in_stack, 0);
        
            ArrayList<ArrayList<Integer>> sccs = new ArrayList<>();

            for (int i = 0; i < n; ++i) {
                if (parent[i] == -1) { 
                    parent[i] = i; 

                    dfs(i, sccs);
                }
            }

            return sccs;
    }

    public static void main(String[] args) {
        
        
                Scanner sc =new Scanner(System.in);

                n = sc.nextInt();
                m = sc.nextInt();

                for (int i = 0; i < n; i++) {
                    adj[i] = new ArrayList<>();
                }
                int u,v;
                for (int i = 0; i < m; ++i) {
                    u = sc.nextInt();
                    v = sc.nextInt();
                    adj[u].add(v); 
                    adj[v].add(u);
                }

                time = 0; 
                
             ArrayList<ArrayList<Integer>> componente_conexe = new ArrayList<ArrayList<Integer>>();
             componente_conexe = tarjan_scc();
            int k = componente_conexe.size();
            int nr = 0;

            for(int i = 0; i < k; i++) {
                if(componente_conexe.get(i).size() >= 5) nr++;
            }
            
        
        System.out.println(nr);
}
}