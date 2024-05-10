import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;


public class Ferate {

	public static class Task {

		public static final String INPUT_FILE = "ferate.in";
		public static final String OUTPUT_FILE = "ferate.out";


		public static final int NMAX = (int) 1e5 + 5; // 10^5 + 5 = 100.005

		//n = number of train stations
		//m = number of train lines
		static int n, m;

		int src; //the main deposit

		int time; //the timestamp used in Tarjan's algorithm

		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] adj = new ArrayList[NMAX];

		int[] parent; // parent array of each node in the DFS traversal

		int[] disc; //timestamp for the time when each node was found

		int[] low; //the minimum accessible timestamp that node can see

		Stack<Integer> s = new Stack<>(); //stack for visiting order

		int[] in_stack; //array that marks if a node was pushed in stack

		public void solve() {
			readInput(); 
			writeOutput(getResult());
		}

		private static class MyScanner { 
			private BufferedReader br; 
			private StringTokenizer st; 

			public MyScanner(Reader reader) { 
				br = new BufferedReader(reader); 
			} 

			public String next() { 
				while (st == null || !st.hasMoreElements()) { 
					try { 
						st = new StringTokenizer(br.readLine()); 
					} catch (IOException e) { 
						e.printStackTrace(); 
					} 
				} 
				return st.nextToken(); 
			} 

			public int nextInt() { 
				return Integer.parseInt(next()); 
			} 

		}

		private void readInput() {
			try {
				MyScanner sc = new MyScanner(new BufferedReader(new FileReader(INPUT_FILE)));

				n = sc.nextInt();
				m = sc.nextInt();
				src = sc.nextInt();

				for (int i = 1; i <= n; i++) {
					adj[i] = new ArrayList<>();
				}

				for (int i = 1; i <= m; i++) {
					int x, y;
					x = sc.nextInt();
					y = sc.nextInt();
					adj[x].add(y); // train line (x -> y)
				}

				time = 0; //initially time is 0

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}


		private void writeOutput(int r) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));
				bw.write(Integer.toString(r));
				bw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		private ArrayList<ArrayList<Integer>> tarjan_scc() {
			// STEP 1: initialize results
			parent = new int[n + 1];
			disc = new int[n + 1];
			low = new int[n + 1];
			in_stack = new int[n + 1];

			Arrays.fill(parent, -1);
			Arrays.fill(disc, -1);
			Arrays.fill(low, -1);
			Arrays.fill(in_stack, 0);

			// STEP 2: visit all nodes
			ArrayList<ArrayList<Integer>> all_sccs = new ArrayList<>();

			for (int i = 1; i <= n; ++i) {
				if (parent[i] == -1) { // node not visited
					parent[i] = i; // convention: the parent of the root is actually the root

					// STEP 3: start a new DFS traversal this subtree
					dfs(i, all_sccs);
				}
			}

			return all_sccs;
		}

		private void dfs(int node, ArrayList<ArrayList<Integer>> all_sccs) {
			// STEP 1: a new node is visited - increment the timestamp
			disc[node] = ++time; // the timestamp when node was found
			low[node] = disc[node]; // node only knows its timestamp
			s.push(node); // add node to the visiting stack
			in_stack[node] = 1;

			// STEP 2: visit each neighbour
			for (Integer u : adj[node]) {
				// STEP 3: check if neigh is already visited
				if (parent[u] != -1) {
					
					if (in_stack[u] == 1) {
						low[node] = Math.min(low[node], disc[u]);
					}
					continue;
				}

				// STEP 4: save parent
				parent[u] = node;

				// STEP 5: recursively visit the child subree
				dfs(u, all_sccs);

				// STEP 6: update low_link[node] with information gained through neigh
				low[node] = Math.min(low[node], low[u]);
			}

			// STEP 7: node is root in a SCC if low_link[node] == found[node]
			// (there is no edge from a descendant to an ancestor)
			if (low[node] == disc[node]) {
				// STEP 7.1: pop all elements above node from stack 
				//=> extract the SCC where node is root
				ArrayList<Integer> scc = new ArrayList<>();
				do {
					Integer x = s.peek();
					s.pop();
					in_stack[x] = 0;

					scc.add(x);

					// stop when node was popped from the stack
				} while (scc.get(scc.size() - 1) != node); 

				// STEP 7.2: save SCC
				all_sccs.add(scc);
			}
		}

		int getResult() {

			ArrayList<ArrayList<Integer>> all_sccs = new ArrayList<>();
			//all_sccs stores all the sccs found 
			all_sccs = tarjan_scc();

			int add = 0; //the number of train lines that must be added

			//iterate through each scc			
			for (ArrayList<Integer> scc : all_sccs) {
				//the flag is used to check if a scc is isolated and needs a train line

				int flag = 1; //1 => it needs a train line

				//if the scc already is conected to the main deposit
				//we don t need to add a train line and we skip to the next scc
				if (scc.contains(src)) {
					continue;
				}
				//if the scc only has a train station and it s not connected to another
				//station, we add a train line and skip to the next scc
				if (scc.size() == 1 && parent[scc.get(0)] == -1) {
					add++;
					continue;
				}

				//iterate through each train station from a scc to see if it s connected
				//to another train line
				for (int node : scc) {

					for (int i = 1; i <= n ; i++) {
						ArrayList<Integer> temp  = adj[i];
							
						//if there is a train station that is connected to the curent station
						//and it s not from the same scc and it s not the main deposit => flag = 0
						if (temp.contains(node) 
							&& !scc.contains(i) && i != src) {
							//the train station is already connected to the rest of the stations
							flag = 0; 
							break; 
						} else if (temp.contains(node) && i == src) {
							flag = 0;
							break;
						}

					}

					if (flag == 0) {
						break; //we skip to the next scc if the curent one is connected
					}

				}
				//if the flag remains 1 => the station needs a train line
				if (flag == 1) {
					add++;
				}

			}

			return add; //return the result

		}

	}

	public static void main(String[] args) {
		new Task().solve();
	}
}