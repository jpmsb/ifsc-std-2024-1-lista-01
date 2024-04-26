package engtelecom.std.multicast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servidor de descoberta Multicast
 * 
 * Envia o próprio endereço IP em intervalos regulares para um grupo multicast.
 */
public class ServidorMulticast implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ServidorMulticast.class);
    private final int INTERVALO = 1000;
    private String menssagem;
    private int porta;
    private InetAddress enderecoMulticast;

    public ServidorMulticast(String enderecoMulticast, int porta, int portaTcp) throws UnknownHostException, SocketException {
        this.enderecoMulticast = InetAddress.getByName(enderecoMulticast);
        this.porta = porta;
        this.menssagem = portaTcp + "";
    }

    @Override
    public void run() {
        logger.info("Servidor de descoberta Multicast iniciado.");
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            while (true) {
                byte[] buffer =  menssagem.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, enderecoMulticast, porta);
                datagramSocket.send(datagramPacket);
                Thread.sleep(INTERVALO);
            }
        } catch (Exception e) {
            logger.error("Erro: " + e.getMessage());
        }
    }
}
