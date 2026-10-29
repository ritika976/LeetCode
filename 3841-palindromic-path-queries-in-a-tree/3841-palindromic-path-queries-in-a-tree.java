
import java.util.*;

class Solution {
    List<Integer>[] adj;
    int[][] up;
    int[] depth, tin, tout;
    int[] bit;
    char[] chars;
    int timer, LOG, n;

    void add(int i, int val) {
        for (i++; i < bit.length; i += i & -i) {
            bit[i] ^= val;
        }
    }

    int prefix(int i) {
        int res = 0;
        for (i++; i > 0; i -= i & -i) {
            res ^= bit[i];
        }
        return res;
    }

    int mask(char c) {
        return 1 << (c - 'a');
    }

    int lca(int a, int b) {
        if (depth[a] < depth[b]) {
            int temp = a;
            a = b;
            b = temp;
        }

        int diff = depth[a] - depth[b];

        for (int j = 0; j < LOG; j++) {
            if (((diff >> j) & 1) == 1) {
                a = up[j][a];
            }
        }

        if (a == b) return a;

        for (int j = LOG - 1; j >= 0; j--) {
            if (up[j][a] != up[j][b]) {
                a = up[j][a];
                b = up[j][b];
            }
        }

        return up[0][a];
    }

    public List<Boolean> palindromePath(
            int n, int[][] edges,
            String s, String[] queries) {

        this.n = n;
        chars = s.toCharArray();

        LOG = 1;
        while ((1L << LOG) <= n) LOG++;

        adj = new ArrayList[n];

        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }

        for (int[] e : edges) {
            adj[e[0]].add(e[1]);
            adj[e[1]].add(e[0]);
        }

        up = new int[LOG][n];
        depth = new int[n];
        tin = new int[n];
        tout = new int[n];

        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int[] stack = new int[2 * n + 1];
        int top = 0;

        stack[top++] = 0;
        timer = 0;

        while (top > 0) {
            int x = stack[--top];

            if (x >= 0) {
                int u = x;
                tin[u] = timer++;

                up[0][u] = parent[u] == -1
                        ? u : parent[u];

                for (int j = 1; j < LOG; j++) {
                    up[j][u] = up[j - 1][up[j - 1][u]];
                }

                stack[top++] = ~u;

                for (int v : adj[u]) {
                    if (v == parent[u]) continue;

                    parent[v] = u;
                    depth[v] = depth[u] + 1;
                    stack[top++] = v;
                }
            } else {
                int u = ~x;
                tout[u] = timer;
            }
        }

        bit = new int[n + 2];

        for (int i = 0; i < n; i++) {
            add(tin[i], mask(chars[i]));
            add(tout[i], mask(chars[i]));
        }

        List<Boolean> ans = new ArrayList<>();

        for (String q : queries) {
            String[] parts = q.split(" ");

            if (parts[0].equals("update")) {
                int u = Integer.parseInt(parts[1]);
                char c = parts[2].charAt(0);

                int change = mask(chars[u]) ^ mask(c);

                add(tin[u], change);
                add(tout[u], change);

                chars[u] = c;

            } else {
                int u = Integer.parseInt(parts[1]);
                int v = Integer.parseInt(parts[2]);

                int w = lca(u, v);

                int pathMask = prefix(tin[u])
                             ^ prefix(tin[v])
                             ^ mask(chars[w]);

                boolean possible =
                        (pathMask & (pathMask - 1)) == 0;

                ans.add(possible);
            }
        }

        return ans;
    }
}

