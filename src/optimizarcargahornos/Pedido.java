package optimizarcargahornos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Pedido {
    private int idPedido; //código que identifica el pedido
    private int idSet;//set pedido
    private int cantidad;
    private LocalDate fEntrega; //fecha de entrega
    private int pCliente; //prioridad del cliente 

    public Pedido(int idP, int idS, int cant, LocalDate entrega,int priorCliente){
        this.idPedido=idP;
        this.idSet=idS;
        this.cantidad=cant;
        this.fEntrega=entrega;
        this.pCliente=priorCliente;
    }
    public int calcularPrioridad(){
        long dias = DAYS.between(LocalDate.now(),fEntrega);
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
    public int idPedido(){
        return idPedido;
    }
    public int idSetPedido(){
        return idSet;
    }
    public int cantidad(){
        return cantidad;
    }
    public int priorCliente(){
        return pCliente;
    }
    public String fechaEntrega(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fEntrega.format(formatter);
    }
}
