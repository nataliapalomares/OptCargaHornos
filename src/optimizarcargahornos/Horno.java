package optimizarcargahornos;
/**
 * @author Natalia Palomares Melgarejo
 */
public class Horno {
    static double volMaximo; //volumen máximo (cm^3)
    //double pesoMaximo; //peso máximo que pueden soportar las vagonetas
    static int nVagonetas; //numero de vagonetas que entran en el horno
    //int nCompartimentos; //numero de compartimentos que tiene una vagoneta
    
    public Horno(double volMax, double pesoMaximo,int nVagones,int nCompartimentos){
        volMaximo=volMax*Math.pow(100,3);//cm3
        nVagonetas=nVagones;
        //this.pesoMaximo=pesoMaximo;        
        //this.nCompartimentos=nCompartimentos;
        Vagoneta.pesoMaximo=pesoMaximo;
        Vagoneta.nCompartimentos=nCompartimentos;
        Vagoneta.lCompartimentos=new Compartimento[nCompartimentos];
    }
}
