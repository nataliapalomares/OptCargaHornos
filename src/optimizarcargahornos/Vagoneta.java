package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Vagoneta {
    static double pesoMaximo;
    static int nCompartimentos; //numero de compartimentos que tiene la vagoneta
    static Compartimento[] lCompartimentos;
    public Vagoneta(){
        //this.lCompartimentos=new Compartimento[nCompartimentos];
    }
    public void agregarCompartimento(int i,int id,int w,int h,int l){
        this.lCompartimentos[i]=new Compartimento(id,w,l,h);
    }
}
