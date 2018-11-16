package optimizarcargahornos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Solucion implements Comparable<Solucion>{
    //Constantes
    final static int N_DEMANDA=10;//rango en el que se calificara la demanda de una pieza
    final static int MAXPRIORIDAD=150;//5(F. de entrega)*3(Importancia del cliente)*10(Rango demanda)
    //Coeficientes de importancia de los factores de la solucion
    static double COEF_DEMANDA;
    static double COEF_VOLUMEN;
    static double COEF_PESO;
    
    double fitness;
    
    //Estructuras auxiliares
    int[] piezasCol;
    double[] prioridadV;
    double[] pesoV;
    double[] volV;
    
    public Solucion(){
        this.fitness=0.0;
        this.prioridadV=new double[Horno.nVagonetas];
        this.volV=new double[Horno.nVagonetas];
        this.pesoV=new double[Horno.nVagonetas];
        this.piezasCol=new int[GestorPiezas.cantidadPiezas];
    }
    public double getPrioridadV(int w){
        return this.prioridadV[w];
    }
    public double getPesoV(int w){
        return this.pesoV[w];
    }
    public double getVolV(int w){
        return this.volV[w];
    }
    public double getFitness(){
        return this.fitness;
    }
    public void setPrioridadV(int w,double prioridad){
        this.prioridadV[w]=prioridad;
    }
    public void setPesoV(int w, double peso){
        this.pesoV[w]=peso;
    }
    public void setVolV(int w, double volumen){
        this.volV[w]=volumen;
    }
    public void setFitness(double fitness){
        this.fitness=fitness;
    }
    public void agregarElemento(int rV,Pieza nuevaPieza,GestorPiezas gPiezas){
        int ind=nuevaPieza.getId()-1;
        int faltantesActual=Math.max(gPiezas.faltantes(ind)-piezasCol[ind],0);
        piezasCol[ind]+=1;
        prioridadV[rV]+=(gPiezas.getpPromedio(ind)*Math.ceil((double)(10*faltantesActual)/gPiezas.maxFaltantes));
        pesoV[rV]+=nuevaPieza.peso;
        volV[rV]+=nuevaPieza.volumen;
    }
    public void quitarElemento(int rV,int ind,Pieza piezaPorQuitar,GestorPiezas gPiezas){
        piezasCol[ind]-=1;
        int faltantesActual=Math.max(gPiezas.faltantes(ind)-piezasCol[ind],0);
        prioridadV[rV]-=(gPiezas.getpPromedio(ind)*Math.ceil((double)(10*faltantesActual)/gPiezas.maxFaltantes));
        pesoV[rV]-=piezaPorQuitar.peso;
        volV[rV]-=piezaPorQuitar.volumen;
    }
    public void actualizarFitness(){
        //Maxima prioridad que se puede cargar en una vagoneta
        int maxPrioridadW=MAXPRIORIDAD*Vagoneta.nCompartimentos;
        double fitnessActual=0;
        for(int w=0;w<Horno.nVagonetas;w++){
            fitnessActual+=(COEF_DEMANDA*prioridadV[w]/maxPrioridadW);
            fitnessActual+=(COEF_VOLUMEN*Horno.nVagonetas*volV[w]/Horno.volMaximo);
            fitnessActual+=(COEF_PESO*pesoV[w]/Vagoneta.pesoMaximo);
        }
        fitness=fitnessActual;
    }
    public Pieza buscarReemplazo(int ind,int rC,boolean[][]mDimensiones,GestorPiezas gPiezas){
        List<Integer> indicesReemplazo=new ArrayList<>();
        for(int i=0;i<gPiezas.size();i++){
            if(mDimensiones[i][rC] && (i!=ind)){
                boolean hayPorHornear= piezasCol[i]<gPiezas.pendientes(i);
                //Si aun hay piezas que se pueden asignar
                if(hayPorHornear) indicesReemplazo.add(i);
            }
        }
        if(indicesReemplazo.isEmpty()){
            //Si no hay reemplazos posibles, la mutacion falló
            if(ind==-1) return null;
            return gPiezas.getPieza(ind);
        }
        Random aleatorio = new Random(System.currentTimeMillis());
        int reemplazo=aleatorio.nextInt(indicesReemplazo.size());
        return gPiezas.getPieza(indicesReemplazo.get(reemplazo));
    }
    public boolean valida(GestorPiezas gPieza){
        double volTotal=0;
        for(int i=0;i<Horno.nVagonetas;i++){
            //RE1: no exceder el peso máximo de la vagoneta
            if(pesoV[i]>Vagoneta.pesoMaximo){
                return false;
            }
            volTotal+=volV[i];
        }
        //RE2: no exceder el volumen máximo del horno
        if(volTotal>Horno.volMaximo)
            return false;
        //RE3: la pieza debe caber en el compartimento asignado
        //      Esto se asegura en la asignación
        //RE4: solo se puede colocar hasta 1 pieza por compartimento
        //      Esto lo asegura la estructura
        //RE5: no exceder la cantidad de piezas pendientes
        for(int i=0;i<GestorPiezas.cantidadPiezas;i++){
            //Como máximo solo puedo asignar la cantidad de piezass que estan pendientes por hornear
            if(gPieza.pendientes(i)<piezasCol[i]) return false;
        }
        return true;
    }
    
    public int getCantColocada(int ind){
        return this.piezasCol[ind];
    }
    
    @Override
    public int compareTo(Solucion solComparar) {
       //  int compareage=((Student)comparestu).getStudentage();
       double fitnessComparar=solComparar.getFitness();
       return Double.compare(fitnessComparar, this.fitness);
    }
}
