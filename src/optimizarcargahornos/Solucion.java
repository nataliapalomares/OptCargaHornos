/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimizarcargahornos;

/**
 *
 * @author Natalia
 */
public class Solucion {
    int[][] arregloPiezas;
    double fitness;
    public Solucion(Horno oven){
        this.arregloPiezas=new int[oven.numeroEspacios][oven.turnos];
        this.fitness=0;
    }
}
