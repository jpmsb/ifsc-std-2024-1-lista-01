package engtelecom.std;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.net.Inet6Address;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Cliente {
    // Cores
    public static final String VERDE = "\033[1;32m";
    public static final String MAGENTA = "\033[1;35m";
    public static final String VERMELHO = "\033[1;31m";
    public static final String AMARELO = "\033[1;33m";
    public static final String CIANO = "\033[1;36m";
    public static final String FUNDO_AZUL = "\033[1;44m";
    public static final String FUNDO_CIANO = "\033[1;46m";
    public static final String FUNDO_CINZA = "\033[1;47m";
    public static final String FUNDO_VERMELHO = "\033[1;41m";
    public static final String NORMAL = "\033[0m";

    private final int BUFFER_SIZE;
    private String endereco;
    private int porta;
    private int tempoLimiteDescobrir;
    private ArrayList<String> servidoresDescobertos = new ArrayList<>();
    
    /**
     * Logger para a exibição de avisos
     */
    private Logger logger;
    
    public Cliente(Logger logger, String enderecoMulticast, int porta, int tempoLimiteDescobrir){
        this.logger = logger;
        this.endereco = enderecoMulticast;
        this.porta = porta;
        this.tempoLimiteDescobrir = tempoLimiteDescobrir;
        this.BUFFER_SIZE = 256;
    }

    /**
     * Obtém as interfaces de rede disponíveis. Irá excluir a interface de loopback
     * e as interfaces IPv6.
     * 
     * @return Lista de interfaces de rede encontradas.
     */
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

    /**
     * Faz a descoberta de servidores via multicast
     */
    public void descobreServidores() {
        logger.info("Cliente Multicast iniciado.");
        List<InetAddress> interfacesDeRede = obterInterfacesDeRede();

        try (MulticastSocket multicastSocket = new MulticastSocket(this.porta)) {
            // Cria o endereço multicast
            InetAddress enderecoMulticast = InetAddress.getByName(this.endereco);
            
            // Cria o grupo multicast
            InetSocketAddress grupo = new InetSocketAddress(enderecoMulticast, this.porta);

            // Entra no grupo multicast para todas as interfaces
            for (InetAddress inetAddress : interfacesDeRede) {
                multicastSocket.joinGroup(grupo, NetworkInterface.getByInetAddress(inetAddress));
            }

            byte[] buffer = new byte[this.BUFFER_SIZE];
            
            // Entra no grupo multicast para todas as interfaces
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);

            // Receberá, durante 'tempoLimiteDescobrir' segundos, mensagens dos servidores multicast
            logger.info(MAGENTA + "Descobrindo servidores na rede local..." + NORMAL + "\n");
            long tempoInicio = System.currentTimeMillis();
            long tempoFim = tempoInicio + tempoLimiteDescobrir * 1000;

            while (System.currentTimeMillis() < tempoFim) {
                multicastSocket.receive(datagramPacket);
                String portaDoServidor = new String(datagramPacket.getData());
                String servidorDeOrigem = datagramPacket.getAddress().toString().substring(1);
                String ipPortaServidor = servidorDeOrigem + ":" + portaDoServidor.trim();
                
                if (! servidoresDescobertos.contains(ipPortaServidor)){
                    servidoresDescobertos.add(ipPortaServidor);
                }
            }

            // Deixa o grupo multicast para todas as interfaces
            for (InetAddress inetAddress : interfacesDeRede) {
                multicastSocket.leaveGroup(grupo, NetworkInterface.getByInetAddress(inetAddress));
            }
        } catch (Exception e) {
            logger.severe("Erro: " + e.getMessage());
        }
    }

    /**
     * Estabelece a comunicação com o servidor TCP.
     * @param enderecoServidor Endereço do servidor TCP.
     * @param porta Porta do servidor TCP.
     * @param mensagem Mensagem a ser enviada para o servidor TCP.
     * @return Resposta do servidor TCP.
     */
    public String comunicacao(String enderecoServidor, int porta, String mensagem){
        String respostaDoServidor = "";
        // Estabelecendo a conexão com o tcp
        try (Socket socket = new Socket(enderecoServidor, porta)) {
            logger.info("Conectado ao servidor " + enderecoServidor + ":" + porta);

            // Estabelecendo os fluxos de entrada e saída
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Somente bytes podem ser enviados e aqui estamos enviando uma string, por isso usamos um OutputStreamWriter com UTF-8
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);

            // Enviando a mensagem para o servidor TCP
            outputStreamWriter.write(mensagem+ "\n");
            outputStreamWriter.flush();

            // Lendo a resposta do servidor TCP
            respostaDoServidor = bufferedReader.readLine();

        } catch (Exception e) {
            logger.severe("Erro: " + e.getMessage());
        }
        return respostaDoServidor;
    }
  
    /**
     * Retorna a lista de servidores descobertos
     * @return ArrayList com os servidores descobertos no formato "ip:porta"
     */
    public ArrayList<String> getServidoresDescobertos() {
        return servidoresDescobertos;
    }
}
