package optimizarcargahornos;

import java.util.Random;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Solucion {
    int[][] arregloPiezas;
    double fitness;
    double[] pesoV;//peso cargado a cada vagoneta
    double[] volV;//volumen cargado a cada vagoneta
    public Solucion(){
        pesoV=new double[Horno.nVagonetas];
        volV=new double[Horno.nVagonetas];
        int[][]arregloPiezas=new int[Horno.nVagonetas][Vagoneta.nCompartimentos];
    }
    public double getFitness(){
        return fitness;
    }
    public void mutar(int numMutar){
        Random aleatorio = new Random(System.currentTimeMillis());
        for(int i=0;i<numMutar;i++){
            int rV=aleatorio.nextInt(Horno.nVagonetas);
            int rC=aleatorio.nextInt(Vagoneta.nCompartimentos);
            int idNuevaPieza;//PENDIENTE: seleccionar una pieza que entre en el compartimento
            this.arregloPiezas[rV][rC]=idNuevaPieza;
        }
    }
    public boolean valida(){
        double volTotal=0;
        for(int i=0;i<Horno.nVagonetas;i++){
            //RE1: no exceder el peso máximo de la vagoneta
            if(pesoV[i]>Vagoneta.pesoMaximo)
                return false;
            volTotal+=volV[i];
        }
        //RE2: no exceder el volumen máximo del horno
        if(volTotal>Horno.volMaximo)
            return false;
        
        //RE3: la pieza debe caber en el compartimento asignado
        //      Esto se asegura en la asignación
        //RE4: solo se puede colocar hasta 1 pieza por compartimento
        //      Esto lo asegura la estructura
        //PENDIENTE RE5: no exceder la cantidad de piezas pendientes
        return true;
    }
}
