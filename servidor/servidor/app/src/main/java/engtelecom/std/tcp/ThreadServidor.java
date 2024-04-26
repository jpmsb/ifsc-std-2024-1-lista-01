package engtelecom.std.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Thread do Servidor TCP. É criada uma nova thread para atender cada cliente.
 */
public class ThreadServidor implements Runnable{

    private static final Logger logger = Logger.getLogger(ThreadServidor.class.getName());

    private Socket socket;

    public ThreadServidor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            logger.info("Cliente conectado: " + socket.getInetAddress() + ":" + socket.getPort());
            try {
                // Estabelecendo os fluxos de entrada e saída
                // Acordo entre o cliente e o tcp, garante que serão trocadas apenas cadeias de caracteres codificadas em UTF-8
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                // Lendo a mensagem do cliente
                String mensagemDoCliente = bufferedReader.readLine();
                logger.info("<<< " + mensagemDoCliente);

                // Enviando a resposta para o cliente
                dataOutputStream.writeBytes(mensagemDoCliente.toUpperCase() + "\n");

            } catch (Exception e) {
                logger.severe("Erro: " + e.getMessage());
            }
        } else {
            logger.info("Erro: cliente não conectado");
        }
    }
}