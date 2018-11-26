package optimizarcargahornos;

import java.util.Random;

/**
 * @author Natalia Palomares Melgarejo
 */
public class SolucionG extends Solucion{
    private int[] arregloPiezas;
    
    public SolucionG(){
        super();
        arregloPiezas=new int[Horno.nVagonetas*Vagoneta.nCompartimentos];
    }
    public SolucionG(SolucionMeme sol){
        //Creo una solucion con la estructura del genetico a partir de una solucion
        //con la estructura del memetico
        this();
        int nComp=Vagoneta.nCompartimentos;
        for(int w=0;w<Horno.nVagonetas;w++){
            for(int c=0;c<nComp;c++){
                arregloPiezas[w*nComp+c]=sol.getIdPieza(w,c);
            }
            super.setPrioridadV(w, sol.getPrioridadV(w));
            super.setPesoV(w,sol.getPesoV(w));
            super.setVolV(w,sol.getVolV(w));
        }
        System.arraycopy(sol.piezasCol, 0, piezasCol, 0, piezasCol.length);
        fitness=sol.getFitness();
    }
    
    public void agregarElemento(int rV,int rC,int ind,GestorPiezas gPiezas){
        //Si la pieza es un marcador de un compartimento vacio, solo coloco en
        //el compartimento rC el valor 0
        if(ind==-1){
            this.arregloPiezas[rV*Vagoneta.nCompartimentos+rC]=0;
            return;
        }
        //Si no, busco la pieza de indice "ind"
        Pieza nuevaPieza=gPiezas.getPieza(ind);
        agregarElemento(rV, rC, nuevaPieza, gPiezas);
    }
    public void agregarElemento(int rV,int rC,Pieza nuevaPieza,GestorPiezas gPiezas){
        //Agrega el elemento "nuevaPieza" a la solucion
        
        //Actualiza los contadores de piezas, volumen, peso y prioridad cargada
        agregarElemento(rV, nuevaPieza, gPiezas);
        //Ingresa el id de la pieza colocada
        this.arregloPiezas[rV*Vagoneta.nCompartimentos+rC]=nuevaPieza.id();
    }
    public int getIndPieza(int rV,int rC){
        //devuelve el indice de la pieza en el vagon rV y el compartimento rC
        return this.arregloPiezas[rV*Vagoneta.nCompartimentos+rC]-1;
    }
    private void quitarElemento(int rV,int rC,GestorPiezas gPiezas){
        //Quita la pieza en el vagon rV y el compartimento rC del arreglo de piezas,
        //antes de eso llama a quitarElemento para eliminar el efecto en el peso
        //volumen y prioridad del vagon.
        
        int ind=getIndPieza(rV,rC);
        if(ind==-1) return;
        Pieza piezaActual=gPiezas.getPieza(ind);
        super.quitarElemento(rV, ind, piezaActual, gPiezas);
        //Se señala que en el compartimento no hay ninguna pieza que no hay ningun elemento
        this.arregloPiezas[rV*Vagoneta.nCompartimentos+rC]=0;
    }
    private SolucionG mutar(int numMutar,GestorPiezas gPiezas,boolean[][] mDimensiones){
        //Se encarga de mutar la solucion, cambia numMutar elementos de la solucion
        Random aleatorio = new Random(System.currentTimeMillis());
        int i=0;
        while(i<numMutar){
            int rV=aleatorio.nextInt(Horno.nVagonetas);
            int rC=aleatorio.nextInt(Vagoneta.nCompartimentos);
            int ind=this.getIndPieza(rV,rC);
                        
            if(ind!=-1){
                //Si el compartimento no esta vacio quito la pieza junto con la 
                //prioridad, peso y volumen cargado de la pieza
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
    public void copiar(SolucionG original){
        //Copia a la solucion que llama la funcion, los valores contenidos en la
        //solucion parametro (original)
        setFitness(original.getFitness());
        for(int w=0;w<Horno.nVagonetas;w++){
            setPrioridadV(w, original.getPrioridadV(w));
            setPesoV(w,original.getPesoV(w));
            setVolV(w,original.getVolV(w));
        }
        System.arraycopy(original.piezasCol, 0, this.piezasCol, 0, this.piezasCol.length);
        System.arraycopy(original.arregloPiezas, 0, this.arregloPiezas, 0, this.arregloPiezas.length);
    }
    public SolucionG mutarG(int numMutar,GestorPiezas gPiezas,boolean[][] mDimensiones){
        //Funcion que copia a una nueva solucion la solucion origen y luego
        //muta la solucion nueva
        
        SolucionG nueva=new SolucionG();
        nueva.copiar(this);
        return nueva.mutar(numMutar, gPiezas, mDimensiones);
    }
    public int getIdPieza(int vagon,int comp){
        //Funcion que devuelve el id de la pieza cargada en el vagon "vagon"  
        //en el compartimento "comp"
        return this.arregloPiezas[vagon*Vagoneta.nCompartimentos+comp];
    }
    
    public Integer[] getPiezasVagon(int indVagon){
        //Funcion que devuelve un arreglo con los indices de todas las piezas
        //que se cargaron en el vagon indVagon
        
        Integer[] piezasVagon=new Integer[Vagoneta.nCompartimentos];
        for(int i=0;i<Vagoneta.nCompartimentos;i++){
            piezasVagon[i]=this.getIdPieza(indVagon, i);
        }
        return piezasVagon;
    }
}
