package optimizarcargahornos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class PoblacionGen extends Poblacion{
    private List<SolucionG> pob;
    private SolucionG mejor;
    
    public PoblacionGen(){
        super();
        this.pob=new ArrayList<>();
        mejor=null;
    }
    
    public SolucionG getMejor(){
        return mejor;
    }
    public void setMejor(SolucionG sol){
        mejor=sol;
    }
    public SolucionG getInd(int ind){
        return this.pob.get(ind);
    }
    public double[] preparacionRuleta(){
        double[] rangosRuleta=new double[tamanio];
        double anterior=0;
        for(int i=0;i<tamanio;i++){
            rangosRuleta[i]=anterior+(this.pob.get(i).getFitness()/sumaFitness);
            anterior=rangosRuleta[i];
        }
        return rangosRuleta;
    }
    public SolucionG ruleta(double[] rangosRuleta){
        //Ruleta binaria usada durante el casamiento
        double r=Math.random();
        int izq=0;//primer indice
        int der=rangosRuleta.length-1;//ultimo indice
        int indice=ruleta(rangosRuleta,izq,der,r);
        return this.pob.get(indice);
    }
    public SolucionG ruleta(){
        double r=Math.random();
        double suma=0.0;
        for(SolucionG sol:this.pob){
            suma+=(sol.fitness/sumaFitness);
            if(suma>=r) return sol;
        }
        return null;
    }
    public void add(SolucionG sol){
        this.pob.add(sol);
        tamanio++;
        sumaFitness+=sol.getFitness();
        if(tamanio==1 || mejor.getFitness()<sol.getFitness()) this.setMejor(sol);
    }
    public void remove(SolucionG sol){
        sumaFitness-=sol.getFitness();
        this.pob.remove(sol);
        tamanio--;
    }
    public void ordenar(){
        Collections.sort(pob);
    }
}
