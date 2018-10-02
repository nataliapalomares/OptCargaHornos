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
    SolucionMeme mejorSol;
    Grasp graspRestaurar;
    
    //Parametros para Generar Nueva Poblacion
    final static double T_RECOMBINACION=0.25;
    final static double T_MUTACION=0.04; //tasa de mutacion
    final static int NPIEZAS_MUTAR_GENERAR=1;//cantidad de elementos a modificar en la mutacion de generarNuevaPoblacion
    final static double PROBABILIDAD_UC=0.5;//probabilidad usada en uniform crossover
    //Parametros para la búsqueda local
    final static int GEN_INTERVALO_LS=1;//indica cada cuantas generaciones se hará la busqueda local
    final static double PORC_LS=0.05;//proporcion de la poblacion a la que se le aplica busqueda local
    final static int VECINOS_LS=100;//numero de vecino visitados durante la busqueda local
    final static int NPIEZAS_MUTAR_LS=1;//cantidad de elementos afectados por la mutacion en la bus. local
    //Parametros Restaurar Poblacion
    final static double PORC_PRESERVAR=0.05; //porcentaje de la poblacion a preservar
    final static double ALF_RESTAURAR=0.4;//alfa para seleccionar el RCL
    final static int NPIEZAS_RESTAURAR=1;//cantidad de elementos a modificar en la solucion
    
    public Memetico(int maxG,int maxSM,GestorPiezas gPiezas,boolean[][] mDimension, Grasp graspRestaurar){
        this.maxGeneraciones=maxG;
        this.maxSinMejora=maxSM;
        this.gPiezas=gPiezas;
        this.mDimension=mDimension;
        this.graspRestaurar=graspRestaurar;
        this.graspRestaurar.setAlpha(ALF_RESTAURAR);
    }
    public SolucionMeme[] uniform_crossover(SolucionMeme p1,SolucionMeme p2){
        SolucionMeme[] hijos=new SolucionMeme[2];
        hijos[0]=new SolucionMeme();
        hijos[1]=new SolucionMeme();
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
    public PoblacionMeme generarNuevaPoblacion(PoblacionMeme pobPadre,int generacion){
        PoblacionMeme nuevaPob=new PoblacionMeme();
        double[] rangosRuleta=pobPadre.preparacionRuleta();
        int cant=(int)Math.round(pobPadre.size()*T_RECOMBINACION);
        for(int i=0;i<cant;i++){
            SolucionMeme padre1=pobPadre.ruleta(rangosRuleta);
            SolucionMeme padre2=pobPadre.ruleta(rangosRuleta);
            SolucionMeme[] hijos=uniform_crossover(padre1,padre2);
            for (SolucionMeme hijo : hijos) {
                if(Math.random()<T_MUTACION){
                    hijo.mutar(NPIEZAS_MUTAR_GENERAR,gPiezas,mDimension);
                }
                if(hijo.valida(this.gPiezas)){
                    nuevaPob.add(hijo);
                }
            }
        }
        //BUSQUEDA LOCAL
        if(generacion%GEN_INTERVALO_LS==0){
            double[] rangosRuletaNuevaPob=nuevaPob.preparacionRuleta();
            int aplicacionestLS=(int)Math.round(pobPadre.size()*PORC_LS);
            for(int i=0;i<aplicacionestLS;i++){
                SolucionMeme actual=nuevaPob.ruleta(rangosRuletaNuevaPob);
                SolucionMeme mejor=actual;
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
            }
        }
        return nuevaPob;
    }
    public PoblacionMeme actualizarPoblacion(PoblacionMeme pobHijos,PoblacionMeme pobPadre){
        //Funcion que reune las mejores soluciones de las dos poblaciones para formar una nueva poblacion
        int cantInd=pobPadre.size();
        //ORrdenando poblacion padre e hijo
        pobHijos.ordenar();
        pobPadre.ordenar();
        SolucionMeme mejorHijoPob=pobHijos.getMejor();
        SolucionMeme mejorPadrePob=pobPadre.getMejor();
        PoblacionMeme poblacion=new PoblacionMeme();
        for(int i=0;i<cantInd;i++){
            if(mejorHijoPob!=null && (mejorHijoPob.getFitness()>mejorPadrePob.getFitness())){
                poblacion.add(mejorHijoPob);
                pobHijos.remove(mejorHijoPob);
                if(pobHijos.size()>0) mejorHijoPob=pobHijos.getInd(0);
                else mejorHijoPob=null;
                //mejorHijoPob=pobHijos.buscarMejor();
            }
            else{
                poblacion.add(mejorPadrePob);
                pobPadre.remove(mejorPadrePob);
                if(pobPadre.size()>0)mejorPadrePob=pobPadre.getInd(0);
                //mejorPadrePob=pobPadre.buscarMejor();
            }
        }
        //retorna poblacion ordenada
        return poblacion;
    }
    public PoblacionMeme restaurarPoblacion(PoblacionMeme poblacion){
        //Cuando se concluye que la poblacion se ha degenerado se
        //conservaran solo las mejores soluciones y el resto se desechará
        //Luego, para completar la poblacion se generaran soluciones al azar.
        PoblacionMeme nuevaPob=new PoblacionMeme();
        int tamanioPob=poblacion.size();
        int cPreservar=(int)Math.round(tamanioPob*PORC_PRESERVAR);
        int i=0;
        while(i<cPreservar){
            SolucionMeme mejor=poblacion.getInd(0);
            //Solucion mejor=poblacion.buscarMejor();
            nuevaPob.add(mejor);
            poblacion.remove(mejor);
            i++;
        }
        
        while(i<tamanioPob){
            SolucionMeme sol=graspRestaurar.construirSol();
            sol.mutar(NPIEZAS_RESTAURAR,this.gPiezas,mDimension);
            if(sol.valida(gPiezas)){
                nuevaPob.add(sol);
                i++;
            }
        }
        return nuevaPob;
    }
    public SolucionMeme ejecutar(PoblacionMeme pob){
        //FALTA CONSIDERAR EL TEMPORIZADOR
        int sinMejora=0;
        //mejorSol=pob.buscarMejor();
        mejorSol=pob.getMejor();
        double fitnessGRASP=mejorSol.getFitness();
        for(int generacion=0;generacion<this.maxGeneraciones;generacion++){
            PoblacionMeme nuevaPop=generarNuevaPoblacion(pob,generacion);
            pob=actualizarPoblacion(nuevaPop,pob);
            //Solucion mejorActual=pob.buscarMejor();
            SolucionMeme mejorActual=pob.getMejor();
            if(mejorSol.getFitness()<mejorActual.getFitness()){
                mejorSol=mejorActual;
                sinMejora=0;
            }
            else{
                sinMejora++;
                if(sinMejora==this.maxSinMejora){
                    pob=restaurarPoblacion(pob);
                    sinMejora=0;
                }
            }
            if(generacion%100==0) System.out.println(generacion);
        }
        mejorSol.imprimir(fitnessGRASP);
        return mejorSol;
    }
}
