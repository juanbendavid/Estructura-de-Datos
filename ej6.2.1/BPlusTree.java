/**
 * G28
 *
 *
 * Carlos Ariel Vallejos Caballero 5670236 
 * Juan Emanuel David Zaracho 5611898
 */


public class BPlusTree<Key extends Comparable<Key>, Value> {
    // max children per B-tree node = M-1
    // (must be even and greater than 2)
    private static final int M = 4;

    private Node root; // root of the B-tree
    private int height; // height of the B-tree
    private int n; // number of key-value pairs in the B-tree

    // helper B-tree node data type
    private static final class Node { // like a leafNode
        private int m; // number of children
        private Entry[] children = new Entry[M]; // the array of children
        private Node nextLeaf; // pointer to next leaf
        // create a node with k children

        private Node(int k) {
            m = k;
            nextLeaf = null;
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
     */
    public BPlusTree() {
        root = new Node(0);
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
     * Verifica si un key existe dentro del B+tree
     * 
     * @param key la clave que queremos saber si existe
     * @param h nodo raiz del btree
     * @param ht altura del btree
     * @return true o false si existe o el @param key
     */ 
    private boolean existe(Comparable key, Node h, int ht) {
        if (ht == 0) {
            while (h != null) {
                for (int j = 0; j < h.m; j++) {
                    if (eq(h.children[j].key, key)) {
                        return true;
                    }
                }
                h = h.nextLeaf;
            }
            return false;

        }

        return existe(key, h.children[0].next, ht - 1);

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
            return; // si no se superó el limite de hijos, retorna null

        // need to split root
        Node t = new Node(2);

        root.nextLeaf = u; // hacemos que el nodo de la izquierda apunte al de la derecha
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
                    Node u = insert(h.children[j++].next, key, val, ht - 1);

                    if (u == null)
                        return null; // si no se realizó el split
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

        // ******** paso para conectar los nodos hojas de subarboles
        // aquí validamos que el el next del hijo[j-1] ya apunte a un nodo hoja
        // entonces buscamos que ese nodo tenga como nextLeaf al nodo h.children[j]
        if (j - 1 > 0 && h.children[j - 1].next != null) {
            h.children[j - 1].next.nextLeaf = h.children[j].next;
        }

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
        Node t = new Node(M / 2); // nuevo nodo desde la media para arriba/derecha
        h.m = M / 2;
        // despues de crear un nodo nuevo, cargamos la mitad para arriba de indice en él
        for (int j = 0; j < M / 2; j++)
            t.children[j] = h.children[M / 2 + j]; // se cargaron los dos ultimos
        return t;
    }

    
    /**
     * Imprime los valores dentro de un rango de key dado
     * De acuerdo a un B+Tree
     * 
     * @param value1
     * @param value2
     */
    @SuppressWarnings("unchecked")
    public void rangePrint(Comparable value1, Comparable value2) {

        if (less(value1, value2) && existe(value1, root, height) && existe(value2, root, height)) {
            System.out.println("Rango entre " + value1 + " y " + value2 + " es: " );
            print(root, height, value1, value2);
            System.out.println("");
        }else{
            throw new IllegalArgumentException("Datos introducidos inválidos");
        }
    }

    private void print(Node h, int ht, Comparable value1, Comparable value2) {
        Entry[] children = h.children;
        
        if (ht == 0) {
            // estamos en la hoja, entonces imprimimos todos sus values
            while (h != null) {
                for (int j = 0; j < h.m; j++) {
                    if (less(h.children[j].key, value2) && less(value1, h.children[j].key)) {
                        System.out.print(h.children[j].val + " ");
                    }
                    
                }
                h = h.nextLeaf;  // iteramos a la siguiente hoja
            }
            return;

        } else {
            print(children[0].next, ht - 1, value1, value2);
        }
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
        BPlusTree<Integer, Integer> st = new BPlusTree<>();

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
        

        System.out.println("size: " + st.size());
        System.out.println("height:  " + st.height());
        System.out.println("");
        
        //Ejemplo de rangos
        st.rangePrint(30,80);
        //st.rangePrint(60,40); // Lanza una excepcion
        //st.rangePrint(60,200); // Lanza una excepcion
        st.rangePrint(80, 100); // no hay valores entre en el rango indicado
        st.rangePrint(10, 110);
        
        System.out.println();
    }

}

