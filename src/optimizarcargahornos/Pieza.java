package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Pieza {
    int id;
    String descripcion;
    int ancho;//ancho de la pieza en mm
    int alto;//alto de la pieza en mm
    int largo;//largo de la pieza en mm
    double volumen;//volumen de la pieza en cm3
    double peso; //peso de la pieza en Kg
    
    public Pieza(int id,String descripcion,int h,int w,int l,double peso,double volumen){
        this.id=id;
        this.descripcion=descripcion;
        this.ancho=w;
        this.alto=h;
        this.largo=l;
        this.volumen=volumen;
        this.peso=peso;
    }
}
