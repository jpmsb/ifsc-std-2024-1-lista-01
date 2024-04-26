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
import java.util.Scanner;

/**
 * Aplicação Cliente multicast
 *
 */
public class Principal {

    // Para exibir mensagens de log
    private static final Logger logger = Logger.getLogger(Principal.class.getName());

    private final int BUFFER_SIZE;
    private String endereco;
    private int porta;
    private List<InetAddress> interfacesDeRede;
    private ArrayList<String> servidoresDescobertos = new ArrayList<>();
    private int tempoLimiteDescobrir = 3;

    public Principal(String endereco, int porta) {
        this.BUFFER_SIZE = 256;
        this.endereco = endereco;
        this.porta = porta;
        this.interfacesDeRede = new ArrayList<>();
    }

    /**
     * Obtém as interfaces de rede disponíveis. Irá excluir a interface de loopback
     * e as interfaces IPv6.
     * 
     * @return
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
        System.out.println("Cliente Multicast iniciado.");
        this.interfacesDeRede = obterInterfacesDeRede();

        try (MulticastSocket multicastSocket = new MulticastSocket(this.porta)) {

            // Cria o endereço multicast
            InetAddress enderecoMulticast = InetAddress.getByName(this.endereco);
            
            // Cria o grupo multicast
            InetSocketAddress grupo = new InetSocketAddress(enderecoMulticast, this.porta);

            // Entra no grupo multicast para todas as interfaces
            for (InetAddress inetAddress : this.interfacesDeRede) {
                multicastSocket.joinGroup(grupo, NetworkInterface.getByInetAddress(inetAddress));
            }

            byte[] buffer = new byte[this.BUFFER_SIZE];
            
            // Cria o pacote para receber a mensagem
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);

            // Receberá, durante 3 segundos, mensagens dos servidores multicast
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
                // System.out.printf("Servidor: %s enviou a mensagem: %s\n", datagramPacket.getAddress(), mensagemRecebida.trim());
            }

            // Deixa o grupo multicast para todas as interfaces
            for (InetAddress inetAddress : this.interfacesDeRede) {
                multicastSocket.leaveGroup(grupo, NetworkInterface.getByInetAddress(inetAddress));
            }
        } catch (Exception e) {
            logger.severe("Erro: " + e.getMessage());
        }
    }

    /**
     * Estabelece a comunicação com o servidor TCP.
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
  
    public ArrayList<String> getServidoresDescobertos() {
        return servidoresDescobertos;
    }

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        String enderecoMulticast = "231.0.0.0";
        int porta = 8888;

        // Caso sejam informados argumentos de linha de comando,
        // 2, obtém o endereço multicast e a porta
        if (args.length == 2) {
            enderecoMulticast = args[0];
            porta = Integer.parseInt(args[1]);
        }

        Principal clienteMulticast = new Principal(enderecoMulticast, porta);
        clienteMulticast.descobreServidores();

        System.out.println("Escolha um dos servidores abaixo para enviar a mensagem: ");
        int indice = 1;
        for (String servidor : clienteMulticast.getServidoresDescobertos()) { 
            System.out.println(indice + ") " + servidor);
            indice++;
        }

        int indiceDigitado = teclado.nextInt();
        teclado.nextLine();

        System.out.print("Digite a mensagem a ser enviada: ");
        String mensagem = teclado.nextLine();

        String infoServidor[] = clienteMulticast.getServidoresDescobertos().get(indiceDigitado - 1).split(":");
        String ipServidor = infoServidor[0];
        int portaServidor = Integer.parseInt(infoServidor[1]);

        System.out.println("Enviando a mensagem: " + mensagem + " para o servidor " + ipServidor + ":" + portaServidor);
        String resposta = clienteMulticast.comunicacao(ipServidor, portaServidor, mensagem);

        System.out.println("Resposta do servidor: " + resposta);
    }
}