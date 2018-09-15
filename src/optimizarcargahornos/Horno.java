package optimizarcargahornos;
/**
 * @author Natalia Palomares Melgarejo
 */
public class Horno {
    static double volMaximo; //volumen máximo (m3)
    static int nVagonetas; //numero de vagonetas que entran en el horno
    
    public Horno(double volMax, double pesoMaximo,int nVagones,int nCompartimentos){
        volMaximo=volMax;//m3
        nVagonetas=nVagones;
        Vagoneta.pesoMaximo=pesoMaximo;
        Vagoneta.nCompartimentos=nCompartimentos;
        Vagoneta.lCompartimentos=new Compartimento[nCompartimentos];
    }
}
