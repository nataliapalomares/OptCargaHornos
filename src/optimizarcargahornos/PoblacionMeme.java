package optimizarcargahornos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class PoblacionMeme extends Poblacion{
    List<SolucionMeme> pob;
    SolucionMeme mejor;
    
    public PoblacionMeme(){
        super();
        this.pob=new ArrayList<>();
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
    public void add(SolucionMeme sol){
        this.pob.add(sol);
        tamanio++;
        sumaFitness+=sol.getFitness();
        if(tamanio==1 || this.mejor.getFitness()<sol.getFitness()) this.setMejor(sol);
    }
    public void remove(SolucionMeme sol){
        sumaFitness-=sol.getFitness();
        this.pob.remove(sol);
        tamanio--;
    }
    public void remove(int ind){
        remove(this.pob.get(ind));
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
