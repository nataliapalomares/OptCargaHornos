package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Memetico {
    //Parametros
    int maxGeneraciones;
    int maxSinMejora;
    Solucion mejorSol;
    //Parametros Grasp (poblacion inicial)
    final static int TAM_INICIAL=50;
    final static double ALF_INICIAL=0.5;
    //Parametros Restaurar Poblacion
    final static double PORC_PRESERVAR=0.2; //porcentaje de la poblacion a preservar
    final static double ALF_RESTAURAR=0.3;//alfa para seleccionar el RCL
    final static int NPIEZAS_RESTAURAR=4;//cantidad de elementos a modificar en la solucion
    
    public Memetico(int maxG,int maxSM){
        this.maxGeneraciones=maxG;
        this.maxSinMejora=maxSM;
    }
    public Solucion buscarMejor(List<Solucion> poblacion){
        Solucion mejorActual=null;
        for(Solucion solActual:poblacion){
            if(mejorActual==null){
                solActual=mejorActual;
            }
            else if(mejorActual.getFitness()<solActual.getFitness()){
                mejorActual=solActual;
            }
        }
        return mejorActual;
    }
    public List<Solucion> restaurarPoblacion(List<Solucion> poblacion){
        //Cuando se concluye que la poblacion se ha degenerado se
        //conservaran solo las mejores soluciones y el resto se desechará
        //Luego, para completar la poblacion se generaran soluciones al azar.
        List<Solucion> nuevaPob=new ArrayList();
        int tamanioPob=poblacion.size();
        int cPreservar=(int)Math.round(tamanioPob*PORC_PRESERVAR);
        int i=0;
        while(i<cPreservar){
            Solucion mejor=buscarMejor(poblacion);
            nuevaPob.add(mejor);
            poblacion.remove(mejor);
            i++;
        }
        while(i<tamanioPob){
            Solucion sol=new Solucion();
            sol.mutar(NPIEZAS_RESTAURAR);
            nuevaPob.add(sol);
            i++;
        }
        return nuevaPob;
    }
    public Solucion ejecutar(){
        //FALTA CONSIDERAR EL TEMPORIZADOR
        int sinMejora=0;
        List<Solucion> poblacion=new ArrayList();
        //Generacion de la poblacion inicial
        Grasp graspMemetico=new Grasp(TAM_INICIAL,ALF_INICIAL);
        poblacion=graspMemetico.runGrasp();
        mejorSol=buscarMejor(poblacion);
        graspMemetico.setAlpha(ALF_RESTAURAR);
        for(int generacion=0;generacion<this.maxGeneraciones;generacion++){
            List<Solucion> nuevaPop=generarNuevaPoblacion(poblacion);
            poblacion=actualizarPoblacion(nuevaPop,poblacion);
            Solucion mejorActual=buscarMejor(poblacion);
            if(mejorSol.getFitness()<mejorActual.getFitness()){
                mejorSol=mejorActual;
                sinMejora=0;
            }
            else{
                sinMejora++;
                if(sinMejora==this.maxSinMejora){
                    poblacion=restaurarPoblacion(poblacion);
                }
            }
        }
        return mejorSol;
    }
}
