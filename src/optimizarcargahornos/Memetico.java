package optimizarcargahornos;
/**
 * @author Natalia Palomares Melgarejo
 */
public class Memetico {
    //Parametros
    int maxGeneraciones;
    int maxSinMejora;
    GestorPiezas gPiezas;
    boolean[][] mDimension;
    Solucion mejorSol;
    //Parametros Grasp (poblacion inicial)
    Grasp graspMemetico;
    final static int TAM_INICIAL=2;
    final static double ALF_INICIAL=0.5;
    //Parametros para Generar Nueva Poblacion
    final static double T_RECOMBINACION=0.6;
    final static double T_MUTACION=0.3; //tasa de mutacion
    final static int NPIEZAS_MUTAR_GENERAR=2;//cantidad de elementos a modificar en la mutacion de generarNuevaPoblacion
    final static double PROBABILIDAD_UC=0.5;//probabilidad usada en uniform crossover
    //Parametros para la búsqueda local
    final static int GEN_INTERVALO_LS=5;//indica cada cuantas generaciones se hará la busqueda local
    final static double PORC_LS=0.2;//proporcion de la poblacion a la que se le aplica busqueda local
    final static int VECINOS_LS=5;//numero de vecino visitados durante la busqueda local
    final static int NPIEZAS_MUTAR_LS=4;//cantidad de elementos afectados por la mutacion en la bus. local
    //Parametros Restaurar Poblacion
    final static double PORC_PRESERVAR=0.2; //porcentaje de la poblacion a preservar
    final static double ALF_RESTAURAR=0.3;//alfa para seleccionar el RCL
    final static int NPIEZAS_RESTAURAR=4;//cantidad de elementos a modificar en la solucion
    
    public Memetico(int maxG,int maxSM,GestorPiezas gPiezas,boolean[][] mDimension){
        this.maxGeneraciones=maxG;
        this.maxSinMejora=maxSM;
        this.gPiezas=gPiezas;
        this.mDimension=mDimension;
    }
    public Solucion[] uniform_crossover(Solucion p1,Solucion p2){
        Solucion[] hijos=new Solucion[2];
        hijos[0]=new Solucion();
        hijos[1]=new Solucion();
        for(int v=0;v<Horno.nVagonetas;v++){
            for(int c=0;c<Vagoneta.nCompartimentos;c++){
                if(Math.random()<PROBABILIDAD_UC){
                    hijos[0].agregarElemento(v,c,p1.getIndPieza(v,c),gPiezas);
                    hijos[1].agregarElemento(v,c,p2.getIndPieza(v,c),gPiezas);
                }
                else{
                    hijos[0].agregarElemento(v,c,p2.getIndPieza(v,c),gPiezas);
                    hijos[1].agregarElemento(v,c,p1.getIndPieza(v,c),gPiezas);
                }
            }
        }
        hijos[0].actualizarFitness();
        hijos[1].actualizarFitness();
        return hijos;
    }
    public Poblacion generarNuevaPoblacion(Poblacion pobPadre,int generacion){
        Poblacion nuevaPob=new Poblacion();
        double[] rangosRuleta=pobPadre.preparacionRuleta();
        int cant=pobPadre.size();
        while(cant>0){
            Solucion padre1=pobPadre.ruleta(rangosRuleta);
            Solucion padre2=pobPadre.ruleta(rangosRuleta);
            Solucion[] hijos=uniform_crossover(padre1,padre2);
            for (Solucion hijo : hijos) {
                if(Math.random()<T_MUTACION){
                    hijo.mutar(NPIEZAS_MUTAR_GENERAR,gPiezas,mDimension);
                }
                if(hijo.valida(this.gPiezas)){
                    //PENDIENTE: ¿se debe reparar las aberraciones?
                    nuevaPob.add(hijo);
                    cant--;
                }
            }
        }
        //BUSQUEDA LOCAL
        if(generacion%GEN_INTERVALO_LS==0){
            int i=0;
            double[] rangosRuletaNuevaPob=nuevaPob.preparacionRuleta();
            while(i<PORC_LS*pobPadre.size()){
                Solucion actual=nuevaPob.ruleta(rangosRuletaNuevaPob);
                Solucion mejor=actual;
                boolean cambio=false;
                for(int j=0;j<VECINOS_LS;j++){
                    actual=actual.mutarLS(NPIEZAS_MUTAR_LS,this.gPiezas,mDimension);
                    if(actual.valida(gPiezas) && (mejor.getFitness()<actual.getFitness())){
                        mejor=actual;
                        cambio=true;
                    }
                }
                if(cambio){
                    nuevaPob.add(mejor);
                }
                i++;
            }
        }
        return nuevaPob;
    }
    public Poblacion actualizarPoblacion(Poblacion pobHijos,Poblacion pobPadre){
        //Funcion que reune las mejores soluciones de las dos poblaciones para formar una nueva poblacion
        int cantInd=pobPadre.size();
        Solucion mejorHijoPob=pobHijos.buscarMejor();
        Solucion mejorPadrePob=pobPadre.buscarMejor();
        Poblacion poblacion=new Poblacion();
        for(int i=0;i<cantInd;i++){
            if(mejorHijoPob.getFitness()>=mejorPadrePob.getFitness()){
                poblacion.add(mejorHijoPob);
                pobHijos.remove(mejorHijoPob);
                mejorHijoPob=pobHijos.buscarMejor();
            }
            else{
                poblacion.add(mejorPadrePob);
                pobPadre.remove(mejorPadrePob);
                mejorPadrePob=pobPadre.buscarMejor();
            }
        }
        return poblacion;
    }
    public Poblacion restaurarPoblacion(Poblacion poblacion){
        //Cuando se concluye que la poblacion se ha degenerado se
        //conservaran solo las mejores soluciones y el resto se desechará
        //Luego, para completar la poblacion se generaran soluciones al azar.
        Poblacion nuevaPob=new Poblacion();
        int tamanioPob=poblacion.size();
        int cPreservar=(int)Math.round(tamanioPob*PORC_PRESERVAR);
        int i=0;
        while(i<cPreservar){
            Solucion mejor=poblacion.buscarMejor();
            nuevaPob.add(mejor);
            poblacion.remove(mejor);
            i++;
        }
        while(i<tamanioPob){
            Solucion sol=graspMemetico.construirSol();
            sol.mutar(NPIEZAS_RESTAURAR,this.gPiezas,mDimension);
            nuevaPob.add(sol);
            i++;
        }
        return nuevaPob;
    }
    public Solucion ejecutar(){
        //FALTA CONSIDERAR EL TEMPORIZADOR
        int sinMejora=0;
        //Generacion de la poblacion inicial
        graspMemetico=new Grasp(TAM_INICIAL,ALF_INICIAL,gPiezas,mDimension);
        Poblacion pob=graspMemetico.ejecutar();
        mejorSol=pob.buscarMejor();
        graspMemetico.setAlpha(ALF_RESTAURAR);
        for(int generacion=0;generacion<this.maxGeneraciones;generacion++){
            Poblacion nuevaPop=generarNuevaPoblacion(pob,generacion);
            pob=actualizarPoblacion(nuevaPop,pob);
            Solucion mejorActual=pob.buscarMejor();
            if(mejorSol.getFitness()<mejorActual.getFitness()){
                mejorSol=mejorActual;
                sinMejora=0;
            }
            else{
                sinMejora++;
                if(sinMejora==this.maxSinMejora){
                    pob=restaurarPoblacion(pob);
                }
            }
        }
        System.out.println("IMPRIMIENDO MEJOR");
        mejorSol.imprimir();
        return mejorSol;
    }
}
