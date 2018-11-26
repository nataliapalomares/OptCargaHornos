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
    public void agregarCompartimento(int ind,int id,double w,double l,double h){
        lCompartimentos[ind]=new Compartimento(id,w,l,h);
        volumenTotalComp+=(w*h*l);//en m3
    }
    public double getPesoLimite(int compartimento){
        return lCompartimentos[compartimento].getPesoLimite();
    }
    public double getVolLimite(int compartimento){
        return lCompartimentos[compartimento].getVolumenLimite();
    }
    public void setPorcentVolumen(){
        //Calcula el porcentaje de volumen (aprox) que se deberia ocupar del 
        //compartimento
        for(int i=0;i<Vagoneta.nCompartimentos;i++){
            lCompartimentos[i].setPorcentVolumen();
        }
    }
}
