package optimizarcargahornos;
/**
 * @author Natalia Palomares Melgarejo
 */
public class Memetico {
    //Parametros
    private GestorPiezas gPiezas;
    private boolean[][] mDimension;
    private SolucionMeme mejorSol;
    private Grasp graspRestaurar;
    
    //Parametros para Generar Nueva Poblacion
    static double T_RECOMBINACION;
    static double PROBABILIDAD_UC;//probabilidad usada en uniform crossover
    static double T_MUTACION; //tasa de mutacion
    final static int NPIEZAS_MUTAR_GENERAR=1;//cantidad de elementos a modificar en la mutacion de generarNuevaPoblacion
        
    //Parametros para la búsqueda local
    static int GEN_INTERVALO_LS;//indica cada cuantas generaciones se hará la busqueda local
    static double PORC_LS;//proporcion de la poblacion a la que se le aplica busqueda local
    static int VECINOS_LS;//numero de vecino visitados durante la busqueda local
    final static int NPIEZAS_MUTAR_LS=1;//cantidad de elementos afectados por la mutacion en la bus. local
    
    //Parametros Restaurar Poblacion    
    static double PORC_PRESERVAR; //porcentaje de la poblacion a preservar
    final static int NPIEZAS_RESTAURAR=1;//cantidad de elementos a modificar en la solucion
    //final static double ALF_RESTAURAR=0.4;//alfa para seleccionar el RCL
    
    //Condiciones de parada
    static int MAX_SIN_MEJORA;
    static int MAX_GENERACIONES;
    static int TIEMPO_MAXIMO;
    
    public Memetico(GestorPiezas gPiezas,boolean[][] mDimension, Grasp graspRestaurar){
        this.gPiezas=gPiezas;
        this.mDimension=mDimension;
        this.graspRestaurar=graspRestaurar;
        this.mejorSol=null;
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
        //Orrdenando poblacion padre e hijo
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
        
        PoblacionMeme nuevaPob=new PoblacionMeme();
        int tamanioPob=poblacion.size();
        int cPreservar=(int)Math.round(tamanioPob*PORC_PRESERVAR);
        int i=0;
        while(i<cPreservar){
            SolucionMeme mejor=poblacion.getInd(0);
            nuevaPob.add(mejor);
            poblacion.remove(mejor);
            i++;
        }
        //Luego, para completar la poblacion se generaran soluciones al azar.
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
    public void ejecutar(PoblacionMeme pob){
        //mejorSol=pob.buscarMejor();
        mejorSol=pob.getMejor();
        double fitnessGRASP=mejorSol.getFitness();
        int sinMejora=0;
        for(int generacion=0;generacion<MAX_GENERACIONES;generacion++){
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
                if(sinMejora==MAX_SIN_MEJORA){
                    pob=restaurarPoblacion(pob);
                    sinMejora=0;
                }
            }
        }
    }
    public SolucionMeme mejor(){
        return this.mejorSol;
    }
}
