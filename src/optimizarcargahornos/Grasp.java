package optimizarcargahornos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * * @author Natalia Palomares Melgarejo
 */
public class Grasp {

    //parametros
    int tamPoblacion;//tamano de la poblacion al terminar GRASP
    double alpha;
    //datos
    GestorPiezas gPiezas;
    boolean[][] mDimension;

    public Grasp(int tamPoblacion, double alpha, GestorPiezas gPiezas, boolean[][] mDimension) {
        this.tamPoblacion = tamPoblacion;
        this.alpha = alpha;
        this.gPiezas = gPiezas;
        this.mDimension = mDimension;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void obtenerRCL(List<Double> prioridades, List<Pieza> candidatos, double limiteInf) {
        Iterator itr = prioridades.iterator();
        Iterator<Pieza> itrPiezas = candidatos.iterator();
        while (itr.hasNext()) {
            double x = (double) itr.next();
            Pieza p = itrPiezas.next();
            if (x < limiteInf) {
                itr.remove();
                itrPiezas.remove();
            }
        }
    }

    public double actualizarPrioridad(int compartimento, List<Double> prioridades, List<Pieza> candidatos,
            Solucion nuevaSol) {
        Vagoneta wagon = new Vagoneta();
        double maximo = 0;//MAX
        double minimo = Double.MAX_VALUE;//MIN
        for (int i = 0; i < this.gPiezas.size(); i++) {
            if (mDimension[i][compartimento]) {
                boolean hayPorHornear = nuevaSol.getCantColocada(i) < gPiezas.pendientes(i);
                int pedidosPorCompletar=gPiezas.faltantes(i)-nuevaSol.getCantColocada(i);
                if (hayPorHornear && pedidosPorCompletar>0) {
                    //Si cabe en el compartimento, se requiere para completar pedidos
                    //y hay piezas pendientes por hornear, es un candidato
                    double fDemanda = (gPiezas.getpPromedio(i) * 10 * pedidosPorCompletar/ gPiezas.maxFaltantes()) / Solucion.MAXPRIORIDAD;
                    double fVolumen = gPiezas.getVolumen(i) / wagon.getVolLimite(compartimento);
                    double fPeso = gPiezas.getPeso(i) / wagon.getPesoLimite(compartimento);
                    double valor = 100 * (fDemanda * Solucion.COEF_DEMANDA + fVolumen * Solucion.COEF_VOLUMEN + fPeso * Solucion.COEF_PESO);
                    if (fVolumen < 1 && fPeso < 1) {
                        //Para asegurarnos de que la solucion no excede el volumen o el peso
                        prioridades.add(valor);
                        candidatos.add(gPiezas.getPieza(i));
                        if (valor > maximo) {
                            maximo = valor;
                        }
                        if (valor < minimo) {
                            minimo = valor;
                        }
                    }
                }
            }
        }
        return maximo - this.alpha * (maximo - minimo);
    }

    public Solucion construirSol() {
        Solucion nuevaSol = new Solucion();
        int k = 0, w = 0;
        List<Double> prioridades = new ArrayList<>();
        List<Pieza> candidatos = new ArrayList<>();
        while (k < Vagoneta.nCompartimentos) {
            //obtiene piezas que caben en el compartimento
            double limiteInf = actualizarPrioridad(k, prioridades, candidatos, nuevaSol);
            if (!prioridades.isEmpty()) {
                obtenerRCL(prioridades, candidatos, limiteInf);
                Random aleatorio = new Random(System.currentTimeMillis());
                Pieza pieza = candidatos.get(aleatorio.nextInt(candidatos.size()));
                nuevaSol.agregarElemento(w, k, pieza, gPiezas);
                w++;
            }
            if (w == Horno.nVagonetas || prioridades.isEmpty()) {
                k++;
                w = 0;
                continue;
            }
            prioridades.clear();
            candidatos.clear();
        }
        nuevaSol.actualizarFitness();
        return nuevaSol;
    }

    public Poblacion ejecutar() {
        Poblacion pob = new Poblacion();
        int cant = 0;
        while (cant != tamPoblacion) {
            Solucion sol = construirSol();
            if (sol.valida(gPiezas)) {
                //Se descartaran las soluciones no validas
                pob.add(sol);
                cant++;
            }
        }
        return pob;
    }
}
