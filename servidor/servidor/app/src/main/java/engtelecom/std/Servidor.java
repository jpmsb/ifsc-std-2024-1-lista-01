package engtelecom.std;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import engtelecom.std.multicast.ServidorMulticast;
import engtelecom.std.tcp.ServidorTcp;

public class Servidor {
    private Logger logger;
    

    public Servidor(Logger logger){
        this.logger = logger;
    }

    /**
     * Obtém as interfaces de rede disponíveis no sistema.
     * @return Lista de endereços IP das interfaces de rede, exceto loopback e IPv6.
     */
    public List<InetAddress> obterInterfacesDeRede() {
        List<InetAddress> interfaces = new ArrayList<>();
        try {
            logger.info("Identificando as interfaces de rede...");
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                    // Não incluir loopback e IPv6
                    if (inetAddress.isLoopbackAddress() || inetAddress instanceof Inet6Address) {
                        continue;
                    }

                    interfaces.add(inetAddress);
                    String informe = String.format("Nome: %s\t\tEndereço: %s\n", networkInterface.getDisplayName(), inetAddress.toString().substring(1));
                    logger.info(informe);
                }
            }
        } catch (Exception e) {
            logger.severe("Erro: " + e.getMessage());
        }
        return interfaces;
    }

    /**
     * Variável para controlar a execução da thread principal.
     * 'volatile' garante que a variável running será sempre lida 
     * diretamente da memória principal e não de uma cópia em 
     * cache do processador.
     */
    private static volatile boolean running = true;

    /**
     * Inicia a parte multicast do servidor
     * @param porta porta do grupo multicast
     * @param enderecoMulticast endereço do grupo multicast
     * @param portaServidorTcp porta do servidor TCP que será a mensagem enviada
     */
    public void iniciaParteMulticast(int porta, String enderecoMulticast, int portaServidorTcp){
        try {
            ServidorMulticast servidorMulticast = new ServidorMulticast(logger,enderecoMulticast, porta, portaServidorTcp);

            // Inicia a thread do servidor multicast
            new Thread(servidorMulticast).start();

        } catch (UnknownHostException | SocketException e) {
            logger.severe("Erro: " + e.getMessage());
        }
    }

    /**
     * Inicia a parte TCP do servidor
     * @param porta porta cujo servidor TCP irá escutar
     */
    public void iniciaParteTcp(int porta){
        logger.info("\u2591\u2592\u2592 Iniciando o servidor TCP \u2592\u2592\u2591");

        // Adiciona um tratador para encerrar o processo quando o usuário pressionar CTRL+C
        Runtime.getRuntime().addShutdownHook( new Thread(() -> running = false));

        // Estabelece um servidor TCP na porta especificada, escutando em todas as interfaces
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            logger.info("Servidor aguardando conexões em " + serverSocket.getInetAddress() +":" + porta + "\n");
            logger.info("Pressione CTRL+C para encerrar o servidor.\n\n");
            
            while (running) {
                // Aceita a conexão de um cliente e cria uma nova thread para atendê-lo
                new Thread(new ServidorTcp(logger, serverSocket.accept())).start();
            }

        } catch (Exception e) {
            logger.severe("Erro: " + e.getMessage());
        }
    }   
}