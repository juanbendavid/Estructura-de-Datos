public class Adyacente {
    // La clase adyacente representa un vertice vecino
    int id;
    double peso;
    public Adyacente(int id, double peso){
        this.id = id;
        this.peso = peso;
    }
    @Override
    public String toString() {
        return id + " " + peso;
    }
}