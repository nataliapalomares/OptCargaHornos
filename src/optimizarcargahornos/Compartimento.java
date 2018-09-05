package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Compartimento {
    int id; //código del compartimento
    int ancho; //medida del ancho del compartimento en mm
    int largo; //medida del largo del compartimento en mm
    int alto; //medida del largo del compartimento en mm
    public Compartimento(int id,int w, int l, int h){
        this.id=id;
        this.ancho=w;
        this.largo=l;
        this.alto=h;
    }
}
