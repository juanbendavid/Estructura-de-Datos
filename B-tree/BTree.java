public class BTree{
    


    /*
     * Funcion para buscar una clave dentro del arbol
     * Devuelve un  nodo
     * 
    */
    public BTreeNode buscarNodo(BTreeNode nodo, int k){
        int i =0; // indice para recorrer los hijos y claves
        // buscamos el nodo entre las claves que este tiene
        while (i<nodo.hijosC && nodo.keys[i]<k) {
            i++;
        }
        // salimos si el nodo > k o nodo ==k o si salimos del limite
        // del array

        if (i<nodo.hijosC && nodo.keys[i]==k) {
            return nodo;
        }

        // Caso en el que no se ha encontrado
        //entre las claves del nodo actual
        
        // Si el nodo es una hoja, el valor buscado no existe
        if (nodo.hijosC == 0) {
            return null;
        }
        // recooremos recursivamente por el hijo izquierdo o derecho
        // dependiendo donde haya quedado convenientemente
        // el indice i
        return buscarNodo(nodo, k);
        
    }
}