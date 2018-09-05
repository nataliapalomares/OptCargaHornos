package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Vagoneta {
    static double pesoMaximo;
    static int nCompartimentos; //numero de compartimentos que tiene la vagoneta
    Compartimento[] lCompartimentos;
    public Vagoneta(){
        this.lCompartimentos=new Compartimento[nCompartimentos];
    }
    public void agregarCompartimento(int i,int id,double w,double h,double l){
        this.lCompartimentos[i]=new Compartimento(id,w,l,h);
    }
}
