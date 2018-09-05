package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Compartimento {
    int id; //código del compartimento
    double ancho; //medida del ancho del compartimento en centimetros
    double largo; //medida del largo del compartimento en centimetros
    double alto; //medida del largo del compartimento en centimetros
    public Compartimento(int id,double w, double l, double h){
        this.id=id;
        this.ancho=w;
        this.largo=l;
        this.alto=h;
    }
}
