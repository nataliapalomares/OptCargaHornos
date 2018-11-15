package optimizarcargahornos;

import java.util.Random;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Genetico {
    //Estructuras auxiliares
    GestorPiezas gPiezas;
    boolean[][] mDimension;
    SolucionG mejor;
    
    //Casamiento
    static double TASA_CASAMIENTO;
    static double PROBABILIDAD_UC;
    
    //Mutacion
    static double TASA_MUTACION;
    final static int NPIEZAS_MUTAR=1;
    
    //Depuracion de la poblacion
    static double PORC_PRESERVAR;

    //Condiciones de parada
    static int MAX_ITERACIONES;
    static int MAX_SIN_MEJORA;
    
    public Genetico(GestorPiezas gPiezas, boolean[][] mDimension){     
        this.gPiezas=gPiezas;
        this.mDimension=mDimension;
    }
    public PoblacionGen convertirPoblacion(PoblacionMeme pob){
        PoblacionGen pobGen=new PoblacionGen();
        for(int i=0;i<pob.size();i++){
            SolucionG sol=new SolucionG(pob.getInd(i));
            pobGen.add(sol);
        }
        return pobGen;
    }
    public SolucionG[] uniformCrossover(SolucionG padre1,SolucionG padre2){
        SolucionG[] hijos=new SolucionG[2];
        hijos[0]=new SolucionG();
        hijos[1]=new SolucionG();
        for(int w=0;w<Horno.nVagonetas;w++){
            for(int c=0;c<Vagoneta.nCompartimentos;c++){
                if(Math.random()<PROBABILIDAD_UC){
                    hijos[0].agregarElemento(w,c,padre1.getIndPieza(w,c),gPiezas);
                    hijos[1].agregarElemento(w,c,padre2.getIndPieza(w,c),gPiezas);                
                }
                else{
                    hijos[0].agregarElemento(w,c,padre2.getIndPieza(w,c),gPiezas);
                    hijos[1].agregarElemento(w,c,padre1.getIndPieza(w,c),gPiezas);
                }
            }
        }
        hijos[0].actualizarFitness();
        hijos[1].actualizarFitness();
        return hijos;
    }
    public void casamiento(PoblacionGen pobGen){
        int numAplicaciones=(int)Math.round(pobGen.size()*TASA_CASAMIENTO);
        double[] rangos=pobGen.preparacionRuleta();
        for(int i=0;i<numAplicaciones;i++){
            SolucionG padre1=pobGen.ruleta(rangos);
            SolucionG padre2=pobGen.ruleta(rangos);
            SolucionG[] hijos=uniformCrossover(padre1,padre2);
            for(SolucionG sol:hijos){
                if(sol.valida(gPiezas)){
                    pobGen.add(sol);
                }
            }
        }
    }
    public void mutacion(PoblacionGen pobGen){
        int numAplicaciones=(int)Math.round(pobGen.size()*TASA_MUTACION);
        Random aleatorio = new Random(System.currentTimeMillis());
        for(int i=0;i<numAplicaciones;i++){
            SolucionG sol=pobGen.getInd(aleatorio.nextInt(pobGen.size()));
            SolucionG nuevaSol=sol.mutarG(NPIEZAS_MUTAR,gPiezas,mDimension);
            if(nuevaSol.valida(gPiezas)){
                pobGen.add(nuevaSol);
            }
            
        }
    }
    public PoblacionGen depurarPobGen(PoblacionGen pobGen,int nIndividuos){
        PoblacionGen nuevaPob=new PoblacionGen();
        int cantConservar=(int)Math.round(nIndividuos*PORC_PRESERVAR);
        pobGen.ordenar();
        for(int i=0;i<cantConservar;i++){
            SolucionG mejorPob=pobGen.getInd(0);
            nuevaPob.add(mejorPob);
            pobGen.remove(mejorPob);
        }
        for(int i=cantConservar;i<nIndividuos;i++){
            //Se selecciona una solucion de manera aleatoria
            SolucionG escogido=pobGen.ruleta();
            nuevaPob.add(escogido);
            pobGen.remove(escogido);
        }
        return nuevaPob;
    }
    public void ejecutar(PoblacionMeme pob,long finalContador){
        PoblacionGen pobGen=convertirPoblacion(pob);
        this.mejor=pobGen.getMejor();
        int tOriginal=pobGen.size();
        int sinMejora=0;
        for(int i=0;(System.currentTimeMillis()<finalContador) && (i<MAX_ITERACIONES) && sinMejora<MAX_SIN_MEJORA;i++){
            casamiento(pobGen);
            mutacion(pobGen);
            pobGen= depurarPobGen(pobGen,tOriginal);
            SolucionG mejorActual= pobGen.getMejor();
            if(mejor.getFitness()<mejorActual.getFitness()){
                mejor=mejorActual;
                sinMejora=0;
            }
            else sinMejora++;
        }
    }
    public SolucionG mejor(){
        return this.mejor;
    }
}
