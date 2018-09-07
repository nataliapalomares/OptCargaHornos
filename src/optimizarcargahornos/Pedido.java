package optimizarcargahornos;

import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Date;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Pedido {
    int idPedido; //código que identifica el pedido
    int idSet;//set pedido
    int cantidad;
    LocalDate fEntrega; //fecha de entrega
    int pCliente; //prioridad del cliente 

    public Pedido(int idP, int idS, int cant, LocalDate entrega,int priorCliente){
        this.idPedido=idP;
        this.idSet=idS;
        this.cantidad=cant;
        this.fEntrega=entrega;
        this.pCliente=priorCliente;
    }
    public int calcularPrioridad(){
        long dias = DAYS.between(fEntrega, LocalDate.now());
        int nDias;
        if(dias<0)
            nDias=5;
        else if(dias<7)
            nDias=4;
        else if(dias<16)
            nDias=3;
        else if(dias<31)
            nDias=2;
        else nDias=1;
        return cantidad*pCliente*nDias;
    }
}
