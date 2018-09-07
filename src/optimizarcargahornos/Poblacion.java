package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Poblacion {
    List<Solucion> pob;
    int tamanio;
    
    public Poblacion(){
        this.pob=new ArrayList();
        this.tamanio=0;
    }
    public Solucion buscarMejor(){
        Solucion mejorActual=null;
        for(Solucion solActual:pob){
            if(mejorActual==null){
                solActual=mejorActual;
            }
            else if(mejorActual.getFitness()<solActual.getFitness()){
                mejorActual=solActual;
            }
        }
        return mejorActual;
    }
    public int size(){
        return this.tamanio;
    }
    public void add(Solucion sol){
        this.pob.add(sol);
        this.tamanio++;
    }
    public void remove(Solucion sol){
        this.pob.remove(sol);
        this.tamanio--;
    }
    public double[] preparacionRuleta(){
        double[] rangosRuleta=new double[this.tamanio];
        double sumaFitness=0;
        for(int i=0;i<this.tamanio;i++){
            sumaFitness+=this.pob.get(i).getFitness();
        }
        double anterior=0;
        for(int i=0;i<this.tamanio;i++){
            rangosRuleta[i]=anterior+(this.pob.get(i).getFitness()/sumaFitness);
            anterior=rangosRuleta[i];
        }
        return rangosRuleta;
    }
    private int ruleta(double[] rangosRuleta,int izq,int der,double r){
        //Ruleta búsqueda binaria
        int mitad=(izq+der)/2;
        while (izq <= der) {
            if (rangosRuleta[mitad] < r)
                izq = mitad + 1;    
            else if (rangosRuleta[mitad] > r) {
                der= mitad-1;
            }
            else return mitad;
            mitad = (izq + der)/2;
        }
        return -1;  
    }
    public Solucion ruleta(double[] rangosRuleta){
        double r=Math.random();
        int izq=0;
        int der=rangosRuleta.length-1;
        int indice=ruleta(rangosRuleta,izq,der,r);
        return this.pob.get(indice);
    }
}
