package optimizarcargahornos;

import java.util.Date;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Pedido {
    int idPedido; //código que identifica el pedido
    int idSet;//set pedido
    int cantidad;
    Date fEntrega; //fecha de entrega
    int pCliente; //prioridad del cliente 

    public Pedido(int idP, int idS, int cant, Date entrega,int priorCliente){
        this.idPedido=idP;
        this.idSet=idS;
        this.cantidad=cant;
        this.fEntrega=entrega;
        this.pCliente=priorCliente;
    }
}
