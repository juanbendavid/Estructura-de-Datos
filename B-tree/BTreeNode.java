public class BTreeNode {
    int M; // orden del B-arbol o la cantidad de ramificaciones máxima
    int[] keys; // arreglo de claves
    int keysC; // cantidad actual de claves
    BTreeNode[] hijos;
    int hijosC; // cantidad actual de hijos

    // Constructor
    public BTreeNode(int M){
        this.M = M;
        this.keys = new int[2 * M - 1]; 
        this.hijos = new BTreeNode[2 * M];
        this.keysC = 0;
    }

    

}
