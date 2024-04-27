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

    public ServidorMulticast(java.util.logging.Logger logger, String enderecoMulticast, int porta, int portaServidorTcp) throws UnknownHostException, SocketException {
        this.logger = logger;
        this.enderecoMulticast = InetAddress.getByName(enderecoMulticast);
        this.porta = porta;
        this.menssagem = portaServidorTcp + "";
    }

    @Override
    public void run() {
        logger.info("Servidor de descoberta Multicast iniciado!");
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
