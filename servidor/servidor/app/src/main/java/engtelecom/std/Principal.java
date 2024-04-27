package engtelecom.std;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.LogManager;

public class Principal {
    // Porta padrão para o servidor de descoberta
    private static final int PORTA_MULTICAST_PADRAO = 8888;
    
    // Porta padrão para o servidor TCP
    private static final int PORTA_TCP_PADRAO = 51000;

    // Logger geral para exibir mensagens de log
    private static Logger logger = Logger.getLogger(Servidor.class.getName());
    private static ConsoleHandler consoleHandler = new ConsoleHandler();

    public static void main(String[] args) {
        LogManager.getLogManager().reset();

        consoleHandler.setFormatter(new FormatadorDeLog());
        logger.addHandler(consoleHandler);

        int portaMulticast = PORTA_MULTICAST_PADRAO;
        String enderecoMulticast = "231.0.0.0";
        int portaServidorTcp = PORTA_TCP_PADRAO;

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
        servidor.iniciaParteTcp(portaServidorTcp);
    }
}