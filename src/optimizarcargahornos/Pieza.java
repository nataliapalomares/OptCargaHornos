package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Pieza {
    private int id;
    private String descripcion;
    private double ancho;//ancho de la pieza en m
    private double alto;//alto de la pieza en m
    private double largo;//largo de la pieza en m
    private double volumen;//volumen de la pieza en m3
    private double peso; //peso de la pieza en Kg
    
    public Pieza(int id,String descripcion,double h,double w,double l,double peso){
        this.id=id;
        this.descripcion=descripcion;
        this.ancho=w;
        this.alto=h;
        this.largo=l;
        this.volumen=h*w*l;
        this.peso=peso;
    }
    public boolean cabeEnCompartimento(double maxDC,double minDC,double medDC){
        //Hallando que lado es el más grande, más pequeño y mediano de la pieza
        double maximo=Math.max(this.ancho,Math.max(this.largo,this.alto));
        double minimo=Math.min(this.ancho,Math.min(this.largo,this.alto));
        double medio= this.ancho+this.largo+this.alto-maximo-minimo;
        //Comparando cada lado con el compartimento
        return (maximo<=maxDC && minimo<=minDC && medio<=medDC);
    }
    public double volumen(){
        return this.volumen;
    }
    public double peso(){
        return this.peso;
    }
    public int id(){
        return this.id;
    }
    public String[] descripcion(){
        String[] datosDescrip=descripcion.split("/");
        return datosDescrip;
    }
    public double alto(){
        return alto;
    }
    public double ancho(){
        return ancho;
    }
    public double largo(){
        return largo;
    }
}
