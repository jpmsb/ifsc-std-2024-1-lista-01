package engtelecom.std.multicast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Servidor de descoberta Multicast
 * 
 * Envia o próprio endereço IP em intervalos regulares para um grupo multicast.
 */
public class ServidorMulticast implements Runnable {
    private Logger logger;
    private final int INTERVALO = 1000;
    private String menssagem;
    private int porta;
    private InetAddress enderecoMulticast;
    private String stringEnderecoMulticast;

    public ServidorMulticast(java.util.logging.Logger logger, String enderecoMulticast, int porta, int portaServidorTcp) throws UnknownHostException, SocketException {
        this.logger = logger;
        this.enderecoMulticast = InetAddress.getByName(enderecoMulticast);
        this.stringEnderecoMulticast = enderecoMulticast;
        this.porta = porta;
        this.menssagem = portaServidorTcp + "";
    }

    /**
     * Reimplementação do método que é chamado quando a thread é iniciada.
     * É iniciado um servidor multicast que envia a porta do servidor TCP
     * como a mensagem para o grupo.
     */
    @Override
    public void run() {
        logger.info("\u2591\u2592\u2592 Servidor de descoberta Multicast iniciado! \u2592\u2592\u2591");
        logger.info("Endereço do grupo multicast: " + stringEnderecoMulticast + "\tPorta: " + porta + "\n");
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            while (true) {
                byte[] buffer =  menssagem.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, enderecoMulticast, porta);
                datagramSocket.send(datagramPacket);
                Thread.sleep(INTERVALO);
            }
        } catch (Exception e) {
            logger.severe("Erro: " + e.getMessage());
        }
    }
}
