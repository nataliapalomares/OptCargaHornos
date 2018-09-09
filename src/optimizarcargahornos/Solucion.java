package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Solucion {
    final static int N_DEMANDA=10;//rango en el que se calificara la demanda de una pieza
    final static int MAXPRIORIDAD=150;//5(F. de entrega)*3(Importancia del cliente)*10(Rango demanda)
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
    public int getIdPieza(int v,int c){
        return this.arregloPiezas[v][c];
    }
    public int getIndPieza(int rV,int rC){
        return this.arregloPiezas[rV][rC]-1;
    }
    public void actualizarFitness(){
        //Maxima prioridad que se puede cargar en una vagoneta
        int maxPrioridadW=MAXPRIORIDAD*Vagoneta.nCompartimentos;
        double fitnessActual=0;
        for(int w=0;w<Horno.nVagonetas;w++){
            fitnessActual+=(COEF_DEMANDA*this.prioridadV[w]/maxPrioridadW);
            fitnessActual+=(COEF_VOLUMEN*Horno.nVagonetas*this.volV[w]/Horno.volMaximo);
            fitnessActual+=(COEF_PESO*this.pesoV[w]/Vagoneta.pesoMaximo);
        }
        this.fitness=fitnessActual;
    }
    public void quitarElemento(int rV,int rC,GestorPiezas gPiezas){
        int ind=getIndPieza(rV,rC);
        Pieza piezaActual=gPiezas.getPieza(ind);
        this.prioridadV[rV]-=(gPiezas.getpPromedio(ind)*10*gPiezas.faltantes(ind)/gPiezas.maxFaltantes);
        this.pesoV[rV]-=piezaActual.peso;
        this.volV[rV]-=piezaActual.volumen;
        //Se señala que en el compartimento no hay ninguna pieza que no hay ningun elemento
        this.arregloPiezas[rV][rC]=0;//PENDIENTE: -1 o 0 (Determinar)
    }
    public void agregarElemento(int rV,int rC,Pieza nuevaPieza,GestorPiezas gPiezas){
        int ind=nuevaPieza.getId()-1;
        this.prioridadV[rV]+=(gPiezas.getpPromedio(ind)*10*gPiezas.faltantes(ind)/gPiezas.maxFaltantes);
        this.pesoV[rV]+=nuevaPieza.peso;
        this.volV[rV]+=nuevaPieza.volumen;
        this.arregloPiezas[rV][rC]=nuevaPieza.getId();
    }
    public void agregarElemento(int rV,int rC,int ind,GestorPiezas gPiezas){
        Pieza nuevaPieza=gPiezas.getPieza(ind);
        this.agregarElemento(rV, rC, nuevaPieza, gPiezas);
    }
    public Pieza buscarReemplazo(int ind,int rC,boolean[][]mDimensiones,GestorPiezas gPiezas){
        List<Integer> indicesReemplazo=new ArrayList<>();
        for(int i=0;i<gPiezas.size();i++){
            if(mDimensiones[i][rC] && i!=ind)
                indicesReemplazo.add(i);
        }
        if(indicesReemplazo.isEmpty()){
            return gPiezas.getPieza(ind);
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
            int ind=this.getIndPieza(rV,rC);
            //Quito la pieza junto con la prioridad, peso y volumen cargado de la pieza
            quitarElemento(rV,rC,gPiezas);
            Pieza nuevaPieza=buscarReemplazo(ind,rC,mDimensiones,gPiezas);
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
                int ind=getIndPieza(i,j);
                if(ind>=0) piezasColocadas[ind]+=1;
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
        for(int i=0;i<gPieza.size();i++){
            if(gPieza.pendientes(i)-piezasColocadas[i]<0) return false;
        }
        return true;
    }
    public void copiar(Solucion original){
        this.fitness=original.fitness;
        for(int v=0;v<Horno.nVagonetas;v++){
            this.prioridadV[v]=original.prioridadV[v];
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
        //nueva.imprimir();
        return nueva.mutar(nMutar,gPiezas,mDimensiones);
    }
    public void imprimir(){
        for(int i=0;i<Vagoneta.nCompartimentos;i++){
            System.out.print("["+(i+1)+"]\t");
            for(int j=0;j<Horno.nVagonetas;j++){
                System.out.print(getIdPieza(j, i)+"  ");
            }
            System.out.print("\n");
        }
        System.out.println("FITNESS: "+fitness);
        System.out.println("W\tVOLUMEN\t\tPESO\tPRIORIDAD");
        for(int i=0;i<Horno.nVagonetas;i++){
            System.out.println(String.format( "[%d]\t%.2f\t%.2f\t%.2f", i+1,volV[i],pesoV[i],prioridadV[i] ));
        }
        //System.out.println("\nPESO: ");
        /*for(int i=0;i<Horno.nVagonetas;i++){
            System.out.print(String.format( "[%d] %.2f  ", i+1,pesoV[i] ));
        }
        System.out.println("\nPRIORIDAD: ");
        for(int i=0;i<Horno.nVagonetas;i++){
            System.out.print(String.format( "[%d] %.2f  ", i+1,prioridadV[i] ));
        }
        System.out.println("\n");*/
        
    }
}
