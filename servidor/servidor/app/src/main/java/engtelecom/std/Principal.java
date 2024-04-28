package engtelecom.std;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.LogManager;

public class Principal {
    /**
     * Valores padrão
     */
    private static final String ENDERECO_MULTICAST_PADRAO = "231.0.0.0";
    private static final int PORTA_MULTICAST_PADRAO = 8888;
    private static final int PORTA_TCP_PADRAO = 51000;

    /**
     * Valores de configuração do servidor
     */
    private static String enderecoMulticast;
    private static int portaMulticast;
    private static int portaServidorTcp;

    /**
     * Logger geral para exibir mensagens de log
     */
    private static Logger logger = Logger.getLogger(Servidor.class.getName());
    private static ConsoleHandler consoleHandler = new ConsoleHandler();

    public static void main(String[] args) {
        LogManager.getLogManager().reset();

        consoleHandler.setFormatter(new FormatadorDeLog());
        logger.addHandler(consoleHandler);

        // Obter o endereço do grupo multicast
        // a partir da variável de ambiente 
        // ENDERECO_MULTICAST.
        // Caso a variável de ambiente esteja vazia, é atribuído o valor padrão
        // ENDERECO_MULTICAST_PADRAO.
        //  
        enderecoMulticast = System.getenv("ENDERECO_MULTICAST");
        if (enderecoMulticast == null) enderecoMulticast = ENDERECO_MULTICAST_PADRAO;

        // Obter a porta do grupo multicast
        // a partir da variável de ambiente 
        // PORTA_MULTICAST.
        // Caso a variável de ambiente esteja vazia, é atribuído o valor padrão
        // PORTA_MULTICAST_PADRAO
        // 
        try {
            portaMulticast = Integer.parseInt(System.getenv("PORTA_MULTICAST"));
        } catch (final NumberFormatException e){
            portaMulticast = PORTA_MULTICAST_PADRAO;
        }

        // Obter o tempo limite para descobrir servidores
        // a partir da variável de ambiente 
        // TEMPO_LIMITE_PARA_DESCOBERTA.
        // Caso a variável de ambiente esteja vazia, é atribuído o valor padrão
        // TEMPO_LIMITE_PADRAO_PARA_DESCOBERTA
        // 
        try {
            portaServidorTcp = Integer.parseInt(System.getenv("PORTA_TCP"));
        } catch (final NumberFormatException e){
            portaServidorTcp = PORTA_TCP_PADRAO;
        }


        // Caso algum argumento seja informado, obtém as informações
        // de endereço multicast e porta
        if (args.length == 4) {
            enderecoMulticast = args[1];
            portaMulticast = Integer.parseInt(args[2]);
            portaServidorTcp = Integer.parseInt(args[3]);
        }

        Servidor servidor = new Servidor(logger);
        servidor.obterInterfacesDeRede();
        servidor.iniciaParteMulticast(portaMulticast, enderecoMulticast, portaServidorTcp);

        // Espera para que as mensagens de log do servidor multicast
        // não sejam confundidas com a do servido TCP.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        servidor.iniciaParteTcp(portaServidorTcp);
    }
}