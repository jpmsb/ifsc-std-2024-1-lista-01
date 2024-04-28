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
public class ServidorTcp implements Runnable {
    private Logger logger;
    private Socket socket;

    public ServidorTcp(Logger logger, Socket socket) {
        this.socket = socket;
        this.logger = logger;
    }

    /**
     * Reimplementação do método que é chamado quando a thread é iniciada.
     */
    @Override
    public void run() {
        if (socket != null) {
            String enderecoDoCliente = socket.getInetAddress().toString().substring(1);
            int portaDoCliente = socket.getPort();

            logger.info("Cliente conectado: " + enderecoDoCliente + ":" + portaDoCliente);

            try {
                // Estabelecendo os fluxos de entrada e saída.
                // Acordo entre o cliente e o tcp, garante que
                // serão trocadas apenas cadeias de caracteres 
                // codificadas em UTF-8.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                // Lendo a mensagem do cliente
                String mensagemDoCliente = bufferedReader.readLine();
                logger.info(enderecoDoCliente + ":" + portaDoCliente + " >>> " + mensagemDoCliente);

                // Enviando a resposta para o cliente contendo
                // a mensagem com todos os caracteres em caixa alta.
                String respostaCliente = mensagemDoCliente.toUpperCase();
                logger.info(enderecoDoCliente + ":" + portaDoCliente + " <<< " + respostaCliente);
                dataOutputStream.writeBytes(respostaCliente + "\n");

            } catch (Exception e) {
                logger.severe("Erro: " + e.getMessage());
            }
        } else {
            logger.info("Erro: cliente não conectado");
        }
    }
}