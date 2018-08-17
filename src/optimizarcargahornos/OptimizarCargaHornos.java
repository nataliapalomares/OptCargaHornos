
//import optimizarcargahornos.Horno;

package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class OptimizarCargaHornos {

    /**
     * @param args the command line arguments
     */
    private Solucion GreedyRandomizedAlgorithm(){
        return null;
    }
    private boolean valido(Solucion solucion){
        return true;
    }
    public void main(String[] args) {
        //INICIO DE GRASP
        int numIteraciones = 150; //CALIBRAR VALOR
        Horno oven=new Horno(1000,2000,10, 3); //Inicialización del horno
        List<Solucion> poblacionInicial=new ArrayList<Solucion>();
        for(int k=0;k<numIteraciones;k++){
            Solucion solIteracion=new Solucion(oven);
            solIteracion=GreedyRandomizedAlgorithm();
            if(valido(solIteracion)){
                poblacionInicial.add(solIteracion);
            }else{
                //no es una solucion valida
                //OPCION 1: reparar
                //OPCION 2: desechar
            }
        }
    }
    
}
