package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Solucion {
    final static int MAXPRIORIDAD=15;//5(Fecha de entrega)*3(Importancia del cliente)
    //Coeficientes de importancia de los factores de la solucion
    final static double COEF_DEMANDA=1;
    final static double COEF_VOLUMEN=0;
    final static double COEF_PESO=0;
    //variables de la solucion particular
    int[][] arregloPiezas;
    double fitness;
    double[] prioridadV;//suma de las prioridades cargadas en la vagoneta
    double[] volV;//volumen cargado a cada vagoneta
    double[] pesoV;//peso cargado a cada vagoneta
    public Solucion(){
        prioridadV=new double[Horno.nVagonetas];
        volV=new double[Horno.nVagonetas];
        pesoV=new double[Horno.nVagonetas];
        arregloPiezas=new int[Horno.nVagonetas][Vagoneta.nCompartimentos];
    }
    public double getFitness(){
        return fitness;
    }
    public int getPieza(int v,int c){
        return this.arregloPiezas[v][c];
    }
    public void actualizarFitness(){
        //actualizarFitness
        int maxPrioridadW=MAXPRIORIDAD*Horno.nVagonetas;
        double fitnessActual=0;
        for(int w=0;w<Horno.nVagonetas;w++){
            fitnessActual+=(COEF_DEMANDA*this.prioridadV[w]/maxPrioridadW);
            fitnessActual+=(COEF_VOLUMEN*Horno.nVagonetas*this.volV[w]/Horno.volMaximo);
            fitnessActual+=(COEF_PESO*this.pesoV[w]/Vagoneta.pesoMaximo);
        }
        this.fitness=fitnessActual;
    }
    public void quitarElemento(int rV,int rC,GestorPiezas gPiezas){
        Pieza piezaActual=gPiezas.getPieza(this.arregloPiezas[rV][rC]);
        int id=piezaActual.getId()-1;
            
        this.prioridadV[rV]-=(gPiezas.getpPromedio(id)*10*gPiezas.faltantes(id)/gPiezas.maxFaltantes);
        this.pesoV[rV]-=piezaActual.peso;
        this.volV[rV]-=piezaActual.volumen;
        //Se señala que en el compartimento no hay ninguna pieza que no hay ningun elemento
        this.arregloPiezas[rV][rC]=-1;
    }
    public void agregarElemento(int rV,int rC,Pieza nuevaPieza,GestorPiezas gPiezas){
        int id=nuevaPieza.getId()-1;
        this.prioridadV[rV]-=(gPiezas.getpPromedio(id)*10*gPiezas.faltantes(id)/gPiezas.maxFaltantes);
        this.pesoV[rV]+=nuevaPieza.peso;
        this.volV[rV]+=nuevaPieza.volumen;
        this.arregloPiezas[rV][rC]=id;
    }
    public void agregarElemento(int rV,int rC,int id,GestorPiezas gPiezas){
        Pieza nuevaPieza=gPiezas.getPieza(id);
        this.agregarElemento(rV, rC, nuevaPieza, gPiezas);
    }
    public Pieza buscarReemplazo(int id,int rC,boolean[][]mDimensiones,GestorPiezas gPiezas){
        List<Integer> indicesReemplazo=new ArrayList();
        for(int i=0;i<gPiezas.size();i++){
            if(mDimensiones[i][rC] && i!=id)
                indicesReemplazo.add(i);
        }
        if(indicesReemplazo.isEmpty()){
            return gPiezas.getPieza(id);
        }
        Random aleatorio = new Random(System.currentTimeMillis());
        int reemplazo=aleatorio.nextInt(indicesReemplazo.size());
        return gPiezas.getPieza(reemplazo);
    }
    public Solucion mutar(int numMutar,GestorPiezas gPiezas,boolean[][] mDimensiones){
        Random aleatorio = new Random(System.currentTimeMillis());
        for(int i=0;i<numMutar;i++){
            int rV=aleatorio.nextInt(Horno.nVagonetas);
            int rC=aleatorio.nextInt(Vagoneta.nCompartimentos);
            int id=this.getPieza(rV, rC);
            //Quito la pieza junto con la prioridad, peso y volumen cargado de la pieza
            quitarElemento(rV,rC,gPiezas);
            Pieza nuevaPieza=buscarReemplazo(id,rC,mDimensiones,gPiezas);
            agregarElemento(rV,rC,nuevaPieza,gPiezas);
        }
        actualizarFitness();
        return this;
    }
    public boolean valida(GestorPiezas gPieza){
        double volTotal=0;
        int[]piezasColocadas=new int[gPieza.size()];
        for(int i=0;i<Horno.nVagonetas;i++){
            //RE1: no exceder el peso máximo de la vagoneta
            if(pesoV[i]>Vagoneta.pesoMaximo){
                return false;
            }
            for(int j=0;j<Vagoneta.nCompartimentos;j++){
                piezasColocadas[this.arregloPiezas[i][j]]+=1;
            }
            volTotal+=volV[i];
        }
        //RE2: no exceder el volumen máximo del horno
        if(volTotal>Horno.volMaximo)
            return false;
        for(int i=0;i<gPieza.size();i++){
            if(gPieza.pendientes(i)-piezasColocadas[i]<0) return false;
        }
        //RE3: la pieza debe caber en el compartimento asignado
        //      Esto se asegura en la asignación
        //RE4: solo se puede colocar hasta 1 pieza por compartimento
        //      Esto lo asegura la estructura
        //RE5: no exceder la cantidad de piezas pendientes
        
        return true;
    }
    public void copiar(Solucion original){
        this.fitness=original.fitness;
        for(int v=0;v<Horno.nVagonetas;v++){
            this.pesoV[v]=original.pesoV[v];
            this.volV[v]=original.volV[v];
            for(int c=0;c<Vagoneta.nCompartimentos;c++){
                this.arregloPiezas[v][c]=original.arregloPiezas[v][c];
            }
        }
    }
    public Solucion mutarLS(int nMutar,GestorPiezas gPiezas,boolean[][] mDimensiones){
        Solucion nueva=new Solucion();
        nueva.copiar(this);
        return nueva.mutar(nMutar,gPiezas,mDimensiones);
    }
}
