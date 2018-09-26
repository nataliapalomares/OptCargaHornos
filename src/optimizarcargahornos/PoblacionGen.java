package optimizarcargahornos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class PoblacionGen extends Poblacion{
    List<SolucionG> pob;
    SolucionG mejor;
    
    public PoblacionGen(){
        super();
        this.pob=new ArrayList<>();
        this.mejor=null;
    }
    public SolucionG getMejor(){
        return this.mejor;
    }
    public void setMejor(SolucionG sol){
        this.mejor=sol;
    }
    public SolucionG getInd(int ind){
        return this.pob.get(ind);
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
    public SolucionG ruleta(double[] rangosRuleta){
        double r=Math.random();
        int izq=0;
        int der=rangosRuleta.length-1;
        int indice=super.ruleta(rangosRuleta,izq,der,r);
        return this.pob.get(indice);
    }
    public void add(SolucionG sol){
        this.pob.add(sol);
        tamanio++;
        if(tamanio==1 || this.mejor.getFitness()<sol.getFitness()) this.setMejor(sol);
    }
    public void remove(SolucionG sol){
        this.pob.remove(sol);
        tamanio--;
    }
    public void ordenar(){
        Collections.sort(pob);
    }
}
