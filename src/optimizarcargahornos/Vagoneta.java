package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Vagoneta {
    static double pesoMaximo;
    static int nCompartimentos; //numero de compartimentos que tiene la vagoneta
    static Compartimento[] lCompartimentos;
    static double volumenTotalComp=0;
    
    public Vagoneta(){
        //this.lCompartimentos=new Compartimento[nCompartimentos];
    }
    public void agregarCompartimento(int i,int id,int w,int h,int l){
        this.lCompartimentos[i]=new Compartimento(id,w,l,h);
        volumenTotalComp+=(w*h*l);
    }
    public double getPesoLimite(int compartimento){
        return lCompartimentos[compartimento].getPesoLimite();
    }
    public double getVolLimite(int compartimento){
        return lCompartimentos[compartimento].getVolumenLimite();
    }
}
