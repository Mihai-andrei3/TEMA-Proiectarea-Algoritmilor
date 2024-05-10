import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Teleportare {

	static class Task {

		public static final String INPUT_FILE = "teleportare.in";
		public static final String OUTPUT_FILE = "teleportare.out";

		public static final int NMAX = 50005;

		public static final int INF = (int) 1e9;

		// n = numar de noduri, m = numar de muchii
		int n, m;

		int k;

		// structura folosita pentru a stoca distanta, cat si vectorul de parinti
		// folosind algoritmul Dijkstra
		public class DijkstraResult {
			List<Integer> d;
			List<Integer> p;

			DijkstraResult(List<Integer> _d, List<Integer> _p) {
				d = _d;
				p = _p;
			}
		};

		public class Pair implements Comparable<Pair> {
			public int destination;
			public int cost;

			Pair(int _destination, int _cost) {
				destination = _destination;
				cost = _cost;
			}

			public int compareTo(Pair o) {
				return Integer.compare(cost, o.cost);
			}
		}

		@SuppressWarnings("unchecked")
		ArrayList<Pair>[] adj = new ArrayList[NMAX];

		@SuppressWarnings("unchecked")
		ArrayList<Pair>[] tel_adj = new ArrayList[NMAX];

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
				k = sc.nextInt();

				for (int i = 1; i <= n; i++) {
					adj[i] = new ArrayList<>();
				}

				for (int i = 1; i <= n; i++) {
					tel_adj[i] = new ArrayList<>();
				}

				for (int i = 1; i <= m; i++) {
					int x, y, w;

					x = sc.nextInt();
					y = sc.nextInt();
					w = sc.nextInt();

					adj[x].add(new Pair(y, w));
					adj[y].add(new Pair(x, w));
				}

				for (int i = 1; i <= k; i++) {
					int x, y, w;
					
					x = sc.nextInt();
					y = sc.nextInt();
					w = sc.nextInt();

					tel_adj[x].add(new Pair(y, w));
					tel_adj[y].add(new Pair(x, w));
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void writeOutput(DijkstraResult result) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));
				StringBuilder sb = new StringBuilder();

				sb.append(result.d.get(n));
				bw.write(sb.toString());
				bw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private DijkstraResult dijkstra(int source, int nodes, ArrayList<Pair>[] adj) {
			List<Integer> d = new ArrayList<>();
			List<Integer> p = new ArrayList<>();

			int time = 0;

			// Initializam distantele la infinit
			for (int node = 0; node <= nodes; node++) {
				d.add(INF);
				p.add(0);
			}

			// Folosim un priority queue de Pair, desi elementele noastre nu sunt tocmai
			// muchii.
			// Vom folosi field-ul de cost ca si distanta pana la nodul respectiv.
			// Observati ca am modificat clasa Pair ca sa implementeze interfata Comparable.
			PriorityQueue<Pair> pq = new PriorityQueue<>();

			// Inseram nodul de plecare in pq si ii setam distanta la 0.
			d.set(source, 0);
			pq.add(new Pair(source, 0));

			// Cat timp inca avem noduri de procesat
			while (!pq.isEmpty()) {
				// Scoatem head-ul cozii
				int cost = pq.peek().cost;
				int node = pq.poll().destination;

				// In cazul in care un nod e introdus in coada cu mai multe distante (pentru ca
				// distanta pana la el se imbunatateste in timp), vrem sa procesam doar
				// versiunea sa cu distanta minima. Astfel, dam discard la intrarile din coada
				// care nu au distanta optima.
				
				if (cost > d.get(node)) {
					continue;
				}

				for (var e : tel_adj[node]) {
					int neigh = e.destination;
					int w = e.cost;

					if (time == 0) {
						if (d.get(node) + w < d.get(neigh)) {
							// Actualizam distanta si parintele.
							d.set(neigh, d.get(node) + w);
							p.set(neigh, node);
							pq.add(new Pair(neigh, d.get(neigh)));
							time += w;
						}
					} else if (time  % e.cost == 0) {
						
						if (d.get(node) + w < d.get(neigh)) {
							// Actualizam distanta si parintele.
							d.set(neigh, d.get(node) + w);
							p.set(neigh, node);
							pq.add(new Pair(neigh, d.get(neigh)));
							time += w;
						}
					}
				}

				// Ii parcurgem toti vecinii.
				for (var e : adj[node]) {
					int neigh = e.destination;
					int w = e.cost;

					// Se imbunatateste distanta?
					if (d.get(node) + w < d.get(neigh)) {
						// Actualizam distanta si parintele.
						d.set(neigh, d.get(node) + w);
						p.set(neigh, node);
						pq.add(new Pair(neigh, d.get(neigh)));
						time += w;
					}
				}
			}

			// Toate nodurile ce au distanta INF nu pot fi atinse din sursa, asa ca setam
			// distantele pe -1.

			for (int i = 1; i <= n; i++) {
				if (d.get(i) == INF) {
					d.set(i, -1);
				}
			}
			
			return new DijkstraResult(d, p);
		}

		private DijkstraResult getResult() {     
			return dijkstra(1, n, adj);
		}
	}
	public static void main(String[] args) {
		new Task().solve();
	}
}
