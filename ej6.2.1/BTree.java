
/**
 * G28
 *
 *
 * Carlos Ariel Vallejos Caballero 5670236
 * Juan Emanuel David Zaracho 5611898
 */

public class BTree<Key extends Comparable<Key>, Value> {
    // max children per B-tree node = M-1
    // (must be even and greater than 2)
    private int M;

    private Node root; // root of the B-tree
    private int height; // height of the B-tree
    private int n; // number of key-value pairs in the B-tree

    // helper B-tree node data type
    private static final class Node { // like a leafNode
        private int m; // number of children
        private Entry[] children;

        // create a node with k children
        private Node(int k, int orden) {
            m = k;
            children = new Entry[orden]; // the array of children
        }
    }

    // internal nodes: only use key and next
    // external nodes: only use key and value

    private static class Entry {

        private Comparable key;
        private Object val;
        private Node next; // helper field to iterate over array entries // right child

        public Entry(Comparable key, Object val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    /**
     * Initializes an empty B-tree.
     * 
     * @param M orden del arbol
     */
    public BTree(int orden) {
        root = new Node(0, orden);
        M = orden; // inicializamos el orden del arbol
    }

    /**
     * Returns true if this symbol table is empty.
     * 
     * @return {@code true} if this symbol table is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * 
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return n;
    }

    /**
     * Returns the height of this B-tree (for debugging).
     *
     * @return the height of this B-tree
     */
    public int height() {
        return height;
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param key the key
     * @return the value associated with the given key if the key is in the symbol
     *         table
     *         and {@code null} if the key is not in the symbol table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public Value get(Key key) {
        if (key == null)
            throw new IllegalArgumentException("argument to get() is null");
        return search(root, key, height);
    }

    @SuppressWarnings("unchecked")
    private Value search(Node x, Key key, int ht) {
        Entry[] children = x.children;

        // external node, cuasndo es un nodo hoja
        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {

                if (eq(key, children[j].key))
                    return (Value) children[j].val;
            }
        }

        // internal node, need recursion
        else {
            for (int j = 0; j < x.m; j++) {

                if (j + 1 == x.m || less(key, children[j + 1].key))
                    return search(children[j].next, key, ht - 1);
            }
        }
        return null;
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is {@code null}, this effectively deletes the key from the
     * symbol table.
     *
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(Key key, Value val) {
        if (key == null)
            throw new IllegalArgumentException("argument key to put() is null");
        Node u = insert(root, key, val, height);
        n++;
        if (u == null)
            return; // si no se super?? el limite de hijos, retorna null

        // need to split root
        Node t = new Node(2, M);

        t.children[0] = new Entry(root.children[0].key, null, root);
        t.children[1] = new Entry(u.children[0].key, null, u);

        root = t;
        height++;
    }

    private Node insert(Node h, Key key, Value val, int ht) {

        int j; // global en la funcion
        Entry t = new Entry(key, val, null);

        // external node
        if (ht == 0) {
            for (j = 0; j < h.m; j++) {
                if (less(key, h.children[j].key))
                    break;
            }
        }

        // internal node
        else {
            for (j = 0; j < h.m; j++) {
                if ((j + 1 == h.m) || less(key, h.children[j + 1].key)) {
                    System.out.println("interno");
                    Node u = insert(h.children[j++].next, key, val, ht - 1);

                    if (u == null)
                        return null; // si no se realiz?? el split
                    t.key = u.children[0].key;
                    t.val = null; // como es nodo interno, no almacenamos el dato, solo el key
                    t.next = u;

                    break;
                }
            }
        }
        // hacemos un corrimiento para insertar un numero menor a los que ya existen en
        // el nodo h
        for (int i = h.m; i > j; i--)
            h.children[i] = h.children[i - 1];
        h.children[j] = t; // el nuevo Entry se coloca en la posicion j
        h.m++; // aumenta el numero de keys
        // si no rompemos la regla, retorna null, en caso contrario, debemos dividir el
        // nodo

        if (h.m < M)
            return null;
        else
            return split(h, ht);
    }

    // split node in half
    private Node split(Node h, int ht) {
        Node t = new Node(M / 2, M); // nuevo nodo desde la media para arriba/derecha
        h.m = M / 2;
        // despues de crear un nodo nuevo, cargamos la mitad para arriba de indice en ??l
        for (int j = 0; j < M / 2; j++)
            t.children[j] = h.children[M / 2 + j]; // se cargaron los dos ultimos
        return t;
    }

    /**
     * Returns a string representation of this B-tree (for debugging).
     *
     * @return a string representation of this B-tree.
     */
    @Override
    public String toString() {
        return toString(root, height, "") + "\n";
    }

    private String toString(Node h, int ht, String indent) {
        StringBuilder s = new StringBuilder();
        Entry[] children = h.children;

        if (ht == 0) {
            for (int j = 0; j < h.m; j++) {
                s.append(indent + children[j].key + " " + children[j].val + "\n");
            }
        } else {
            for (int j = 0; j < h.m; j++) {
                if (j > 0)
                    s.append(indent + "(" + children[j].key + ")\n");
                s.append(toString(children[j].next, ht - 1, indent + "   --  "));
            }
        }
        return s.toString();
    }

    @SuppressWarnings("unchecked")
    // comparison functions - make Comparable instead of Key to avoid casts
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    @SuppressWarnings("unchecked")
    private boolean eq(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }

    /**
     * Unit tests the {@code BTree} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        // se recibe el entero desde la linea de comando de argumentos
        if (args.length != 0) {
            int orden = Integer.parseInt(args[0]);
            // validacion requerida
            if (!(orden > 2 && orden % 2 == 0)) {
                throw new RuntimeException("Numero de orden inv??lido");
            }

            BTree<Integer, Integer> st = new BTree<>(orden);

            st.put(10, 10);
            st.put(20, 20);
            st.put(30, 30);
            st.put(40, 40);
            st.put(50, 50);
            st.put(60, 60);
            st.put(70, 70);
            st.put(15, 15);
            st.put(80, 80);
            st.put(100, 100);
            st.put(65, 65);
            st.put(110, 110);

            System.out.println("size:    " + st.size());
            System.out.println("height:  " + st.height());

            System.out.println(st);
            System.out.println();
        }

    }

}
