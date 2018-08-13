package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Horno {
    double pesoMaximo; //peso máximo que puede soportar el horno
    double volumenMaximo; //volumen máximo que se puede introducir al horno sin afectar el proceso de coccion
    int numeroEspacios; //numero de espacios/compartimentos en los que se puede colocar piezas
    int turnos; //numero de turnos al dia para los cuales se prepara la carga
    Compartimento[] listaEspacios;//lista de compartimentos/espacios que tiene el horno;
}
