package engtelecom.std;

import java.net.ServerSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import engtelecom.std.multicast.ServidorMulticast;
import engtelecom.std.tcp.ThreadServidor;

public class Principal {
    // Variável para armazenar o endereço IP do servidor
    private static String proprioEnderecoIp;

    // Para exibir mensagens de log
    private static Logger logger = Logger.getLogger(Principal.class.getName());

    // Variável para controlar a execução da thread principal
    // volatile garante que a variável running será sempre lida diretamente da memória principal e não de uma cópia em cache do processador
    private static volatile boolean running = true;

    public void iniciaServidorMulticast(int porta, String enderecoMulticast, int portaTcp){
        try {
            ServidorMulticast servidorMulticast = new ServidorMulticast(enderecoMulticast, porta, portaTcp);

            // Inicia a thread do tcp de hora multicast
            new Thread(servidorMulticast).start();

        } catch (UnknownHostException | SocketException e) {
            logger.severe("Erro: " + e.getMessage());
        }
    }

    public void iniciaServidorTCP(int porta){
        // Adiciona um tratador para encerrar o processo quando o usuário pressionar CTRL+C
        Runtime.getRuntime().addShutdownHook( new Thread(() -> running = false));

        System.out.println("\u2591\u2592\u2592 Iniciando o tcp \u2592\u2592\u2591");
        
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            
            System.out.println("Servidor aguardando conexões em " + serverSocket.getInetAddress() +":" + porta);
            System.out.println("Pressione CTRL+C para encerrar o processo do tcp\n\n");

            while (running) {
                // Aceita a conexão de um cliente e cria uma nova thread para atendê-lo
                new Thread(new ThreadServidor(serverSocket.accept())).start();
            }

        } catch (Exception e) {
            logger.severe("Erro: " + e.getMessage());
        }
    }

    public List<InetAddress> obterInterfacesDeRede() {
        List<InetAddress> interfaces = new ArrayList<>();
        try {
            logger.info("Obtendo interfaces de rede...");
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                    // Não incluir loopback e IPv6ß
                    if (inetAddress.isLoopbackAddress() || inetAddress instanceof Inet6Address) {
                        continue;
                    }
                    interfaces.add(inetAddress);
                    String m = String.format("Nome: %s\tEndereço: %s\n", networkInterface.getDisplayName(), inetAddress.toString().substring(1));
                    logger.info(m);
                }
            }
        } catch (Exception e) {
            logger.severe("Erro: " + e.getMessage());
        }
        return interfaces;
    }

    public static void main(String[] args) {
        int porta = 8888;
        String enderecoMulticast = "231.0.0.0";
        int portaTcp = 51000;

        // Caso algum argumento seja informado, obtém as informações
        // de endereço multicast e porta
        if (args.length == 2) {
            enderecoMulticast = args[0];
            porta = Integer.parseInt(args[1]);
        }

        Principal principal = new Principal();
        List<InetAddress> enderecos =  principal.obterInterfacesDeRede();
        proprioEnderecoIp = enderecos.get(0).toString().substring(1);

        principal.iniciaServidorMulticast(porta, enderecoMulticast, portaTcp);

        principal.iniciaServidorTCP(portaTcp);
    }
}