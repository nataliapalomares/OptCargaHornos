package optimizarcargahornos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class PoblacionMeme {
    List<SolucionMeme> pob;
    int tamanio;
    SolucionMeme mejor;
    
    public PoblacionMeme(){
        this.pob=new ArrayList<>();
        this.tamanio=0;
        this.mejor=null;
    }
    public SolucionMeme getMejor(){
        return this.mejor;
    }
    public SolucionMeme getInd(int ind){
        return this.pob.get(ind);
    }
    private void setMejor(SolucionMeme mejor){
        this.mejor=mejor;
    }
    public SolucionMeme buscarMejor(){
        SolucionMeme mejorActual=null;
        for(SolucionMeme solActual:pob){
            if((mejorActual==null) ||(mejorActual.getFitness()<solActual.getFitness())){
                mejorActual=solActual;
            }
        }
        setMejor(mejorActual);
        return mejorActual;
    }
    public int size(){
        return this.tamanio;
    }
    public void add(SolucionMeme sol){
        this.pob.add(sol);
        this.tamanio++;
        if(tamanio==1 || this.mejor.getFitness()<sol.getFitness()) this.setMejor(sol);
    }
    public void remove(SolucionMeme sol){
        this.pob.remove(sol);
        this.tamanio--;
    }
    public void remove(int ind){
        remove(this.pob.get(ind));
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
                if(mitad==izq || rangosRuleta[mitad-1]<r){
                    return mitad;
                }
                else der= mitad-1;
            }
            else return mitad;
            mitad = (izq + der)/2;
        }
        return -1;  
    }
    public SolucionMeme ruleta(double[] rangosRuleta){
        double r=Math.random();
        int izq=0;
        int der=rangosRuleta.length-1;
        int indice=ruleta(rangosRuleta,izq,der,r);
        return this.pob.get(indice);
    }
    public void ordenar(){
        Collections.sort(pob);
    }
}
