package optimizarcargahornos;
/**
 * @author Natalia Palomares Melgarejo
 */
public class Horno {
    static double volMaximo; //volumen máximo que se puede introducir al horno sin afectar el proceso de coccion
    //double pesoMaximo; //peso máximo que pueden soportar las vagonetas
    static int nVagonetas; //numero de vagonetas que entran en el horno
    //int nCompartimentos; //numero de compartimentos que tiene una vagoneta
    Vagoneta[] lVagonetas;
    
    public Horno(double volMaximo, double pesoMaximo,int nVagonetas,int nCompartimentos){
        this.volMaximo=volMaximo;
        this.nVagonetas=nVagonetas;
        //this.pesoMaximo=pesoMaximo;        
        //this.nCompartimentos=nCompartimentos;
        Vagoneta.pesoMaximo=pesoMaximo;
        Vagoneta.nCompartimentos=nCompartimentos;
        this.lVagonetas=new Vagoneta[nVagonetas];
        Vagoneta.lCompartimentos=new Compartimento[nCompartimentos];
    }
    public void agregarVagoneta(int i,Vagoneta wagon){
        this.lVagonetas[i]=wagon;
    }
}
