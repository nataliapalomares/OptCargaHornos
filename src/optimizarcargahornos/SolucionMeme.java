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
public class SolucionMeme implements Comparable<SolucionMeme>{
    final static int N_DEMANDA=10;//rango en el que se calificara la demanda de una pieza
    final static int MAXPRIORIDAD=150;//5(F. de entrega)*3(Importancia del cliente)*10(Rango demanda)
    //Coeficientes de importancia de los factores de la solucion
    static double COEF_DEMANDA;
    static double COEF_VOLUMEN;
    static double COEF_PESO;
    //variables de la solucion particular
    private int[][] arregloPiezas;
    int[] piezasCol;
    private double[] prioridadV;//suma de las prioridades cargadas en la vagoneta
    private double[] volV;//volumen cargado a cada vagoneta
    private double[] pesoV;//peso cargado a cada vagoneta
    private double fitness;
    
    
    public SolucionMeme(){
        prioridadV=new double[Horno.nVagonetas];
        volV=new double[Horno.nVagonetas];
        pesoV=new double[Horno.nVagonetas];
        arregloPiezas=new int[Horno.nVagonetas][Vagoneta.nCompartimentos];
        piezasCol=new int[GestorPiezas.cantidadPiezas];
        
    }
    public double getPrioridadV(int w){
        //Devuelve la prioridad cargada en el vagon w
        return this.prioridadV[w];
    }
    public double getPesoV(int w){
        //Devuelve el peso cargado en el vagon w
        return this.pesoV[w];
    }
    public double getVolV(int w){
        //Devuelve el volumen cargado en el vagon w
        return this.volV[w];
    }
    public double getFitness(){
        //Devuelve el fitness de la solucion
        return fitness;
    }
    public int getCantColocada(int ind){
        //Devuelve la cantidad de piezas de indice "ind" que se han colocado en
        //la solucion
        return this.piezasCol[ind];
    }
    public int getIdPieza(int rV,int rC){
        //Devuelve el id de la pieza que esta en el vagon v, compartimento c
        return this.arregloPiezas[rV][rC];
    }
    public int getIndPieza(int rV,int rC){
        //Devuelve el indice de la pieza que esta en el vagon v, compartimento c
        return this.arregloPiezas[rV][rC]-1;
    }
    public int[] getPiezasVagon(int vagon){
        //Devuelve las piezas colocadas en el vagon especificado
        return this.arregloPiezas[vagon];
    }
    public void actualizarFitness(){
        //Actualiza el valor fitness: recalcula el fitness considerando los coeficientes
        //de demanda, volumen y peso
        
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
        //Quita un elemento y reduce la prioridad, peso y volumen total cargados
        //Marca el compartimento rC como vacio
        
        int ind=getIndPieza(rV,rC);
        if(ind==-1) return;
        Pieza piezaActual=gPiezas.getPieza(ind);
        piezasCol[ind]-=1;
        int faltantesActual=Math.max(gPiezas.faltantes(ind)-piezasCol[ind],0);
        this.prioridadV[rV]-=(gPiezas.getpPromedio(ind)*Math.ceil((double)(10*faltantesActual)/gPiezas.maxFaltantes));
        this.pesoV[rV]-=piezaActual.peso();
        this.volV[rV]-=piezaActual.volumen();
        //Se señala que en el compartimento no hay ninguna pieza que no hay ningun elemento
        this.arregloPiezas[rV][rC]=0;
    }
    public void agregarElemento(int rV,int rC,Pieza nuevaPieza,GestorPiezas gPiezas){
        int ind=nuevaPieza.id()-1;
        int faltantesActual=Math.max(gPiezas.faltantes(ind)-piezasCol[ind],0);
        piezasCol[ind]+=1;
        this.prioridadV[rV]+=(gPiezas.getpPromedio(ind)*Math.ceil((double)(10*faltantesActual)/gPiezas.maxFaltantes));
        this.pesoV[rV]+=nuevaPieza.peso();
        this.volV[rV]+=nuevaPieza.volumen();
        this.arregloPiezas[rV][rC]=nuevaPieza.id();
    }
    public void agregarElemento(int rV,int rC,int ind,GestorPiezas gPiezas){
        if(ind==-1){
            this.arregloPiezas[rV][rC]=0;
            return;
        }
        Pieza nuevaPieza=gPiezas.getPieza(ind);
        this.agregarElemento(rV, rC, nuevaPieza, gPiezas);
    }
    public Pieza buscarReemplazo(int ind,int rC,boolean[][]mDimensiones,GestorPiezas gPiezas){
        List<Integer> indicesReemplazo=new ArrayList<>();
        for(int i=0;i<gPiezas.size();i++){
            if(mDimensiones[i][rC] && (i!=ind)){
                boolean hayPorHornear= piezasCol[i]<gPiezas.pendientes(i);
                //boolean pedidosPorCompletar=piezasCol[i]<gPiezas.faltantes(i);
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
    public SolucionMeme mutar(int numMutar,GestorPiezas gPiezas,boolean[][] mDimensiones){
        Random aleatorio = new Random(System.currentTimeMillis());
        int i=0;
        while(i<numMutar){
            int rV=aleatorio.nextInt(Horno.nVagonetas);
            int rC=aleatorio.nextInt(Vagoneta.nCompartimentos);
            int ind=this.getIndPieza(rV,rC);
            //Quito la pieza junto con la prioridad, peso y volumen cargado de la pieza
            if(ind!=-1){
                quitarElemento(rV,rC,gPiezas);
            }
            Pieza nuevaPieza=buscarReemplazo(ind,rC,mDimensiones,gPiezas);
            if(nuevaPieza==null) continue;
            agregarElemento(rV,rC,nuevaPieza,gPiezas);
            i++;
        }
        actualizarFitness();
        return this;
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
    public void copiar(SolucionMeme original){
        this.fitness=original.fitness;
        for(int v=0;v<Horno.nVagonetas;v++){
            this.prioridadV[v]=original.prioridadV[v];
            this.pesoV[v]=original.pesoV[v];
            this.volV[v]=original.volV[v];
            /*for(int c=0;c<Vagoneta.nCompartimentos;c++){
            this.arregloPiezas[v][c]=original.arregloPiezas[v][c];
            }*/
            System.arraycopy(original.arregloPiezas[v], 0, this.arregloPiezas[v], 0, Vagoneta.nCompartimentos);            
        }
        System.arraycopy(original.piezasCol, 0, this.piezasCol, 0, this.piezasCol.length);
    }
    public SolucionMeme mutarLS(int nMutar,GestorPiezas gPiezas,boolean[][] mDimensiones){
        SolucionMeme nueva=new SolucionMeme();
        nueva.copiar(this);
        //nueva.imprimir();
        return nueva.mutar(nMutar,gPiezas,mDimensiones);
    }
    public void imprimir(Duration tiempo){
        for(int j=0;j<Horno.nVagonetas;j++){
            if(j==0) System.out.print("\t");
            System.out.print(String.format("[%3d]",j+1));
        }
        System.out.print("\n");
        for(int i=0;i<Vagoneta.nCompartimentos;i++){
            System.out.print("["+(i+1)+"]\t");
            for(int j=0;j<Horno.nVagonetas;j++){
                System.out.print(String.format("%5d",getIdPieza(j, i)));
            }
            System.out.print("\n");
        }
        System.out.println("FITNESS: "+fitness+" \tDURACIÓN: "+tiempo.toMillis());
        System.out.println("W\tVOLUMEN\t\tPESO\tPRIORIDAD");
        for(int i=0;i<Horno.nVagonetas;i++){
            System.out.println(String.format( "[%d]\t%.3f\t\t%.2f\t%.2f", i+1,volV[i],pesoV[i],prioridadV[i] ));
        }
        try(FileWriter fw = new FileWriter("myfile.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(fitness+","+tiempo.toMillis());
        } catch (IOException ex) {
            Logger.getLogger(SolucionMeme.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void imprimir(long startCont,double fitnessGRASP){
        /*for(int j=0;j<Horno.nVagonetas;j++){
            if(j==0) System.out.print("\t");
            System.out.print(String.format("[%3d]",j+1));
        }
        System.out.print("\n");
        for(int i=0;i<Vagoneta.nCompartimentos;i++){
            System.out.print("["+(i+1)+"]\t");
            for(int j=0;j<Horno.nVagonetas;j++){
                System.out.print(String.format("%5d",getIdPieza(j, i)));
            }
            System.out.print("\n");
        }
        System.out.println("FITNESS: "+fitness+" \tDURACIÓN: ");
        System.out.println("W\tVOLUMEN\t\tPESO\tPRIORIDAD");
        for(int i=0;i<Horno.nVagonetas;i++){
            System.out.println(String.format( "[%d]\t%.3f\t\t%.2f\t%.2f", i+1,volV[i],pesoV[i],prioridadV[i] ));
        }*/
        try(FileWriter fw = new FileWriter("memeticoThread.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println("T: "+startCont+", "+fitnessGRASP+","+fitness);
        } catch (IOException ex) {
            Logger.getLogger(SolucionMeme.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public int compareTo(SolucionMeme solComparar) {
       //  int compareage=((Student)comparestu).getStudentage();
       double fitnessComparar=solComparar.getFitness();
       return Double.compare(fitnessComparar, this.fitness);
    }
}
