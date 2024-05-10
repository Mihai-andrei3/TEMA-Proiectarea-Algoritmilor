import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;


public class Magazin {
	public static class Task {

		public static final String INPUT_FILE = "magazin.in";
		public static final String OUTPUT_FILE = "magazin.out";

		public static final int NMAX = (int) 1e5 + 5; // 10^5 + 5 = 100.005
		//n = number of warehouses(nodes)
		//q = number of questions
		static int n, q;

		int[] D; //array that stores the warehouses for each question
		int[] E; //the number of consecutive shppings

		Vector<Integer> result; //vector that stores the answer to the questions

		int[] in_time; //array that stores the arrival time in each warehouse
		int[] out_time; //array that stores the departure time from each warehouse
		int time; //the timestamp used in DFS

		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] adj = new ArrayList[NMAX];

		public void solve() {
			readInput(); 

			result = new Vector<>();

			in_time = new int[n + 1];
			out_time = new int[n + 1];

			time = 1; //we start the counting with 1

			get_Result(result);
			writeOutput(result);

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
				q = sc.nextInt();

				for (int i = 1; i <= n; i++) {
					adj[i] = new ArrayList<>();
				}

				for (int i = 2; i <= n; i++) {
					int x;
					x = sc.nextInt();
					adj[x].add(i); // (i recieve a package from x)
				}

				D = new int[q];
				E = new int[q];

				for (var i = 0; i < q; i += 1) {
					D[i] = sc.nextInt();
					E[i] = sc.nextInt();
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void writeOutput(Vector<Integer> result) {
			try {
				BufferedWriter pw = new BufferedWriter(new FileWriter(OUTPUT_FILE));

				for (int i = 0; i < result.size(); i++) {
					pw.write(Integer.toString(result.get(i)));  
					pw.newLine();                 
				}

				pw.close();

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}


		void simple_dfs(int src, Vector<Integer> order, int[] positions) {
			
			int[] v = new int[n + 1]; //keep track of visited warehouses
			int p = 1; //position of warehouse in the travelling order

			Stack<Integer> s = new Stack<>();

			s.push(src); //push the source warehouse in stack

			while (!s.isEmpty()) {

				int node = s.pop(); //take the warehouse from stack

				//if it s not already visited
				if (v[node] == 0) {
					v[node] = 1; //mark as visited
					order.add(node); //add it in the order vector
					//add the position that the warehouse have inside the order vector
					positions[node] = p; 
					p++; //increase the position
				}

				for (int u : adj[node]) {
					//travel to each warehouse that is adjacent to the warehouse
					//and it s not already visited
					if (v[u] != 1) {
						s.push(u);   
					}
				}
			}
		}

		void dfs(int[] visited, int node) {

			in_time[node] = time; //add the arrivak time
			visited[node] = 1; //mark the warehouse as visited

			time = time + 1; //incrementing the time

			for (int u : adj[node]) {
				//travel to each warehouse that is adjacent to the warehouse
				//and it s not already visited
				if (visited[u] == 0) {
					dfs(visited, u);
				}
			}

			out_time[node] = time; //add the departure time
			time++; //increase the time
		}
		
		void get_Result(Vector<Integer> result) {

			int[] visited = new int[n + 1]; //array that keep track of visited warehouses
			Vector<Integer> order = new Vector<>(n); //stores the travelling order of packages
			int[] pos = new int[n + 1]; //stores the position of each warehouse in the order vector

			for (int i = 1; i <= n; i++) {
				//sorting the adjacency list for each warehouse 
				//so the dfs will go from left to right
				Collections.sort(adj[i], Collections.reverseOrder());
			}

			//a dfs starting from the source that will initialize the order and pos 
			simple_dfs(1, order, pos);
			//a dfs that will initialize the in_time and out_time arrays
			dfs(visited, 1);

			//iterate through each question
			for (int i = 0; i < q; i++) {
				int dep = D[i]; //warehouse
				int steps = E[i]; //number of shipping/steps that we have to do

				//compute the maximum number of steps that we can go from the deposit dep
				int max_steps = (out_time[dep] - in_time[dep] - 1) / 2;

				//if this number is less than the number of steps we have to do
				//it means that we can reach other warehouse
				if (max_steps < steps) {
					result.add(-1);
				} else {
					//we get the index of the destination warehouse by adding 
					// to the index of dep in the order vector the number of steps
					int index = pos[dep] + steps - 1;
					//we add in the result vector the warehouse that has that index 
					result.add(order.get(index));
				}
			}
		}
	}

	public static void main(String[] args) {
		new Task().solve();
	}
}
