package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;

/**
 * * @author Natalia Palomares Melgarejo
 */
public class Grasp {
    int tamPoblacion;//tamano que deberá alcanzar la poblacion para terminar GRASP
    double alpha;
    public Grasp(int tamPoblacion,double alpha){
        this.tamPoblacion=tamPoblacion;
        this.alpha=alpha;
    }
    public void setAlpha(double alpha){
        this.alpha=alpha;
    }
    public Solucion construirSol(){
        Solucion nuevaSol=new Solucion();
        int k=0,w=0;
        while(k<Vagoneta.nCompartimentos){
            prioridades = actualizarPrioridad(k);
            if (prioridades.size()!=0){
                rcl=seleccionarCandidatos(prioridades)
                pieza=seleccionarPieza(rcl);
                nuevaSol.arregloPiezas[k][w]=pieza;
                //ACTUALIZAR PIEZAS FALTANTES
                w++;
            }
            if(w==Horno.nVagonetas || prioridades.size()<1){
                k++;
                w=0;
            }
        }
        return nuevaSol;
    }
    public List<Solucion> runGrasp(){
        List<Solucion> poblacion=new ArrayList();
        int cant=0;
        while(cant!=tamPoblacion){
            Solucion sol=construirSol();
            if(sol.valida()){
                //Se descartaran las soluciones no validas
                poblacion.add(sol);
            }
        }
        return poblacion;
    }
}
