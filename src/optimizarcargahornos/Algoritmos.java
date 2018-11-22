package optimizarcargahornos;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Algoritmos {
    Horno oven;
    GestorSets gSets;
    GestorProducto gProd; //conjunto de productos
    GestorPiezas gPiezas; //conjunto de piezas
    List<Pedido> lPedidos;
    
    SolucionMeme mejorMeme;
    SolucionG mejorGen;
    //ESTRUCTURAS AUXILIARES
    boolean[][] mDimension; //indica las piezas que caben en cada compartimento
    
    //Parametros del grasp para generar la poblacion inicial
    static int TAM_INICIAL;//tamaño poblacion inicial
    static double ALF_INICIAL;
    
    //Condicion de parada
    static int TIEMPO_MAXIMO;//cantidad de minutos de ejecucion del algoritmo
    
    public Algoritmos() {
        this.mejorMeme=null;
        this.mejorGen=null;
        this.mDimension=null;
    }
    public SwingWorker createWorker(JProgressBar executionProgressBar) {
        return new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                long inicioContador=System.currentTimeMillis();
                long finalContador=TIEMPO_MAXIMO*60*1000;
                if(mDimension==null) crearEstructuraAuxiliares();
                //GRASP-CREACION DE LA POBLACION INICIAL----------------------------------
                Grasp graspPobInicial=new Grasp(TAM_INICIAL,ALF_INICIAL,gPiezas,mDimension);                
                PoblacionMeme pobInicial=graspPobInicial.ejecutar();
                int porcentaje=Math.round(((System.currentTimeMillis()-inicioContador)*100/finalContador));
                if(porcentaje>100){
                    mejorMeme=pobInicial.getMejor();                    
                    mejorGen=new SolucionG(mejorMeme);
                    return null;
                }
                
                Genetico algGenetico=new Genetico(gPiezas,mDimension);
                PoblacionGen pobGen=algGenetico.convertirPoblacion(pobInicial);                
                porcentaje=Math.round(((System.currentTimeMillis()-inicioContador)*100/finalContador));
                if(porcentaje>100){
                    mejorGen=pobGen.getMejor();
                    mejorMeme=pobInicial.getMejor();
                    return null;
                }
                else publish(porcentaje);
                //ALGORITMO GENÉTICO----------------------------------------------------        
                Thread threadGen = new Thread(){
                    public void run(){                        
                        algGenetico.ejecutar(pobGen);
                    }
                };
                threadGen.start();
                //ALGORITMO MEMETICO------------------------------------------------------ 
                Memetico algMemetico=new Memetico(gPiezas,mDimension,graspPobInicial);
                Thread threadMem = new Thread(){
                    public void run(){
                       algMemetico.ejecutar(pobInicial);
                    }
                };
                threadMem.start();
                
                long intervalo=((finalContador+inicioContador)-System.currentTimeMillis())/5;
                for(int i=0;i<5;i++){
                    Thread.sleep(intervalo);
                    publish(Math.round(((System.currentTimeMillis()-inicioContador)*100/finalContador)));
                }
                threadGen.interrupt();
                threadMem.interrupt();
                mejorGen=algGenetico.mejor();
                mejorMeme=algMemetico.mejor();
                return null;                
            }
            @Override
            protected void process(List<Integer> avances) {
                executionProgressBar.setValue(avances.get(0));
            }
            @Override
            protected void done() {
                executionProgressBar.setValue(100);
            }
        };    
    }
    private void inicializarGestoresProd(){
        gSets=new GestorSets();
        gProd=new GestorProducto();
        gPiezas = new GestorPiezas();
    }
    public int datosHorno(String csvFile) {
        
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            line = br.readLine();
            String[] linea = line.split(",");
            double volMaximo = Double.parseDouble(linea[0]);
            double pMaximo = Double.parseDouble(linea[1]);
            int nVagonetas = Integer.parseInt(linea[2]);
            int cantC = Integer.parseInt(linea[3]); //cantidad de compartimentos
            oven = new Horno(volMaximo, pMaximo, nVagonetas, cantC);
            Vagoneta wagon = new Vagoneta();
            while ((line = br.readLine()) != null) {
                //COMPARTIMENTOS
                linea = line.split(",");
                int id = Integer.parseInt(linea[0]);
                double alto = Double.parseDouble(linea[1]);
                double ancho = Double.parseDouble(linea[2]);
                double largo = Double.parseDouble(linea[3]);
                wagon.agregarCompartimento(id-1, id, ancho, largo, alto);
            }
            wagon.setPorcentVolumen();
            return 1;
        } catch (IOException e) {
            return -1;
        } catch(Exception e){
            return -2;
        }
    }
    public int cargarDatos(String csvFile) {
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            inicializarGestoresProd();
            int cant = 0, tipo = 0,i=0;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split(",");
                if (cant == 0) {
                    cant = Integer.parseInt(linea[0]);
                    i=0;
                    tipo++;
                    switch(tipo){
                        case 1: this.gSets.completarIni(cant);
                                break;
                        case 2: this.gProd.completarIni(cant);
                                break;
                        case 3: this.gPiezas.completarIni(cant);
                                break;
        }
                    continue;
                } else if (tipo == 1) {//SETS
                    Set setActual = new Set(Integer.parseInt(linea[0]), linea[1], linea[2], Integer.parseInt(linea[3]), linea[4]);
                    gSets.addRSet(i,'A',Integer.parseInt(linea[5]));
                    gSets.add(setActual,i++);
                } else if (tipo == 2) {//PRODUCTO
                    String[] detallesDescripcion=linea[1].split("/");
                    if(detallesDescripcion.length!=3) throw new Exception("Archivo con formato incorrecto");
                    Producto prodActual = new Producto(Integer.parseInt(linea[0]), linea[1], Integer.parseInt(linea[2]), linea[3]);
                    gProd.addRProd(i,'A',Integer.parseInt(linea[4]));
                    gProd.add(prodActual,i++);
                } else {//PIEZA
                    String descripcion = linea[1] + "/" + linea[2] + "/" + linea[3];
                    double alto = Double.parseDouble(linea[4]);
                    double ancho = Double.parseDouble(linea[5]);
                    double largo = Double.parseDouble(linea[6]);
                    Double peso = Double.parseDouble(linea[7]);
                    Pieza pActual = new Pieza(Integer.parseInt(linea[0]), descripcion, alto, ancho, largo, peso);
                    gPiezas.addRPiezas(i,'A',Integer.parseInt(linea[8]));//cantidad de piezas terminadas en el almacen
                    gPiezas.addRPiezas(i, 'Q',Integer.parseInt(linea[9]));//cantidad pendiente a hornear 
                    gPiezas.add(pActual,i++);
    }
                cant--;
            }
            return 1; //CARGA EXITOSA
        }  catch (IOException e) {
            return -1; //ERROR AL ABRIR EL ARCHIVO
        } catch(Exception e){
            return -2; //ERROR: EL ARCHIVO NO TIENE FORMATO ADECUADO
        }
    }

    public int cargarPedidos(String csvFile){
        if(lPedidos!=null && lPedidos.size()>0) gSets.reiniciarPedidos();
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            lPedidos=new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            while ((line = br.readLine()) != null) {
                String[] linea = line.split(",");
                int idP=Integer.parseInt(linea[0]);
                int idS=Integer.parseInt(linea[1]);
                int cant=Integer.parseInt(linea[2]);
                LocalDate entrega=LocalDate.parse(linea[3],formatter);
                int priorCliente=Integer.parseInt(linea[4]);
                Pedido pActual=new Pedido(idP,idS,cant,entrega,priorCliente);
                gSets.addRSet(idS-1, 'P', cant);
                gSets.addRSet(idS-1, 'R', pActual.calcularPrioridad());
                lPedidos.add(pActual);
            }
            return 1;
        } catch(ArrayIndexOutOfBoundsException  e){
            return -3; //ARCHIVO DE PEDIDOS NO CORRESPONDE AL DE SETS
        } catch (IOException e) {
            return -1; //ERROR AL ABRIR EL ARCHIVO
        } catch(Exception e){
            return -2; //ERROR: EL ARCHIVO NO TIENE FORMATO ADECUADO
        }       
    }
    private void crearEstructuraAuxiliares(){
        //MATRIZ DE DIMENSIONES
        int cantPiezas=this.gPiezas.size();
        mDimension=new boolean[cantPiezas][Vagoneta.nCompartimentos];
        for(int j=0;j<Vagoneta.nCompartimentos;j++){
            double maximoDC=Vagoneta.lCompartimentos[j].maximo();
            double minimoDC=Vagoneta.lCompartimentos[j].minimo();
            double medioDC=Vagoneta.lCompartimentos[j].medio();
            for(int i=0;i<cantPiezas;i++){
                mDimension[i][j] = this.gPiezas.cabeEnCompartimento(i,maximoDC,minimoDC,medioDC);
            }
        }
        //ESTRUCTURAS RESUMEN
        for(int i=0;i<this.gSets.size();i++){
            //Completando RESUMEN SETS
            int cantFS=this.gSets.calcularFaltante(i);
            double pPromS=this.gSets.pPromedio(i);
            int[] listaProd=this.gSets.productos(i);
            for(int j=0;j<listaProd.length;j++){
                //Completando RESUMEN PRODUCTOS
                //La cantidad pedida del producto es la cantidad faltante de sets
                this.gProd.addRProd(listaProd[j]-1, 'P', cantFS);
                //Se calcula la cantidad faltante de productos
                int cantFP=this.gProd.calcularFaltante(listaProd[j]-1);
                double pPromP=this.gProd.pPromedio(listaProd[j]-1,pPromS);
                int[] listaPieza=this.gProd.piezas(listaProd[j]-1);
                for(int k=0;k<listaPieza.length;k++){
                    //Completando RESUMEN PIEZAS
                    this.gPiezas.addRPiezas(listaPieza[k]-1, 'P', cantFP);
                    this.gPiezas.calcularFaltante(listaPieza[k]-1);
                    this.gPiezas.setpPromedio(listaPieza[k]-1,pPromP);
                }
            }
        }
    }
    
    private void ejecutar(){
        
        long inicioContador=System.currentTimeMillis();
        long finalContador=(TIEMPO_MAXIMO*60*1000)+inicioContador;
        /*CARGA DE DATOS: horno, productos, pedidos------------------------------
        datosHorno("C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\ArchivosDatos\\hornoPequenio.csv");
        cargarDatos("C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-1\\Algoritmo optimizacion\\ArchivosExpNumerica\\150sets_piezas.csv");
        cargarPedidos("C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\ArchivosDatos\\504pedidos.csv");*/
        
        //ESTRUCTURAS AUXILIARES: se crea y completa la matriz de dimensiones y resumen
        crearEstructuraAuxiliares();
       
        //GRASP-CREACION DE LA POBLACION INICIAL----------------------------------
        Grasp graspPobInicial=new Grasp(TAM_INICIAL,ALF_INICIAL,gPiezas,mDimension);
        //Instant first = Instant.now();
        PoblacionMeme pobInicial=graspPobInicial.ejecutar();
        //Instant second= Instant.now();
        //Duration duration = Duration.between(first, second);

        //ALGORITMO GENÉTICO----------------------------------------------------        
        /*Thread threadGen = new Thread(){
            public void run(){*/
                Genetico algGenetico=new Genetico(gPiezas,mDimension);
//                algGenetico.ejecutar(pobInicial,finalContador);
//                this.mejorGen=algGenetico.mejor();
        /*    }
        };
        threadGen.start();*/
        //ALGORITMO MEMETICO------------------------------------------------------        
        /*Thread threadMem = new Thread(){
            public void run(){*/
               Memetico algMemetico=new Memetico(gPiezas,mDimension,graspPobInicial);
//               algMemetico.ejecutar(pobInicial,finalContador);
               this.mejorMeme=algMemetico.mejor();
        /*    }
        };
        threadMem.start();  */
    }
}
