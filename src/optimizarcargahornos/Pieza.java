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
    
    public Pieza(int id,String descripcion,int h,int w,int l,double peso){
        this.id=id;
        this.descripcion=descripcion;
        this.ancho=w;
        this.alto=h;
        this.largo=l;
        this.volumen=h*w*l/Math.pow(10,3);
        this.peso=peso;
    }
    public boolean cabeEnCompartimento(int maxDC,int minDC,int medDC){
        //Hallando que lado es el más grande, más pequeño y mediano de la pieza
        int maximo=Math.max(this.ancho,Math.max(this.largo,this.alto));
        int minimo=Math.min(this.ancho,Math.min(this.largo,this.alto));
        int medio= this.ancho+this.largo+this.alto-maximo-minimo;
        //Comparando cada lado con el compartimento
        return (maximo<=maxDC && minimo<=minDC && medio<=medDC);
    }
    public double getVolumen(){
        return this.volumen;
    }
    public double getPeso(){
        return this.peso;
    }
    public int getId(){
        return this.id;
    }
}
