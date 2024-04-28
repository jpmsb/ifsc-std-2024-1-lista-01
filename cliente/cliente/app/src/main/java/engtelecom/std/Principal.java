package engtelecom.std;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

/**
 * Aplicação Cliente
 *
 */
public class Principal {
    /**
     * Cores para uso com as mensagens do console
     */
    public static final String VERDE = "\033[1;32m";
    public static final String MAGENTA = "\033[1;35m";
    public static final String VERMELHO = "\033[1;31m";
    public static final String AMARELO = "\033[1;33m";
    public static final String CIANO = "\033[1;36m";
    public static final String PRETO = "\033[1;30]";
    public static final String FUNDO_AZUL = "\033[1;44m";
    public static final String FUNDO_CIANO = "\033[1;46m";
    public static final String FUNDO_CINZA = "\033[1;47m";
    public static final String FUNDO_VERMELHO = "\033[1;41m";
    public static final String FUNDO_VERDE = "\033[2;30;1;42m";
    public static final String NORMAL = "\033[0m";

    /**
     * Valores padrão 
     */
    private static final String ENDERECO_MULTICAST_PADRAO = "231.0.0.0";
    private static final int PORTA_MULTICAST_PADRAO = 8888;
    private static final int TEMPO_LIMITE_PADRAO_PARA_DESCOBERTA = 3;

    /**
     * Valores de configuração do cliente
     */
    private static int tempoLimiteDescobrir;
    private static int portaMulticast;
    private static String enderecoMulticast;
    private static String pilotoAutomatico;
    private static Scanner teclado = new Scanner(System.in);

    /**
     * Sistema de log geral da aplicação
     */
    private static final Logger logger = Logger.getLogger(Principal.class.getName());  

    /**
     * Objeto que vai cuidar da exibição do log na saída padrão do console
     */ 
    private static ConsoleHandler consoleHandler = new ConsoleHandler();

    /**
     * Exibe os elementos de um vetor de string no formato de lista numerada.
     * Ex.: N) Elemento
     * @param arranjo arranjo de strings
     * @param posicaoDestacada Posição que será destacada com uma cor diferente
     */
    private static void listaNumerada(String elementos[], int posicaoDestacada){
        int indice = 1;
        for (String elemento : elementos) {
            if (indice == posicaoDestacada) System.out.println(FUNDO_VERDE + indice + ") " + elemento + NORMAL + "\t<= Opção escolhida");
            else System.out.println(indice + ") " + elemento);
            indice++;
        }
    }

    /**
     * Exibe os elementos de uma ArrayList no formato de lista numerada.
     * Ex.: N) Elemento
     * @param lista ArrayList de strings
     * @param posicaoDestacada Posição que será destacada com uma cor diferente
     */
    private static void listaNumerada(ArrayList<String> lista, int posicaoDestacada){
        int indice = 1;
        for (String elemento : lista) {
            if (indice == posicaoDestacada) System.out.println(FUNDO_VERDE + indice + ") " + elemento + NORMAL + "\t<= Opção escolhida");
            else System.out.println(indice + ") " + elemento);
            indice++;
        }
    }

    /**
     * Exibe um menu de opções retornando o número de uma opção válida.
     * @param titulo texto no topo do menu
     * @param pedido texto no rodapé do menu, precedido pela numeração de opções
     * @param opcoes Lista com todas as opções que serão exibidas
     * 
     * @return número válido da opção escolhida
     */
    private static int menuOpcoes(String titulo, String pedido, ArrayList<String> opcoes){
        boolean sair = false;
        int quantidadeOpcoes = opcoes.size();
        int opcao = 0;
        
        while (! sair){
            System.out.println(titulo + "\n");

            // Exibe as opções. Como não se deseja destacar nenhuma opção,
            // é informado o valor zero para posicaoDestacada, que nunca
            // ocorrerá no método listaNumerada.
            listaNumerada(opcoes, 0);

            System.out.print("\n" + pedido + " (1 .. " + quantidadeOpcoes + ", 0 para sair): ");
            opcao = teclado.nextInt();

            if (opcao == 0) {
                System.out.println("\nEncerrando a aplicação.\n");
                sair = true;
            } else if (opcao < 1 || opcao > quantidadeOpcoes) {
                System.out.println(VERMELHO + "\nOpção inválida!\n" + NORMAL);
            } else {
                sair = true;
            }
        }

        return opcao;
    }

    public static void main(String[] args) {
        // Redefine o sistema log para evitar saídas indesejadas
        LogManager.getLogManager().reset();

        // Definição do formatador de log. Como o gestor de log
        // foi redefinido anteriormente, este será o único
        // formatador a ser utilizado.
        consoleHandler.setFormatter(new FormatadorDeLog());
        logger.addHandler(consoleHandler);

        
        // Obter o endereço do grupo multicast
        // a partir da variável de ambiente 
        // ENDERECO_MULTICAST.
        // Caso a variável de ambiente esteja vazia, é atribuído o valor padrão
        // ENDERECO_MULTICAST_PADRAO.
        //  
        enderecoMulticast = System.getenv("ENDERECO_MULTICAST");
        if (enderecoMulticast == null) enderecoMulticast = ENDERECO_MULTICAST_PADRAO;

        // Obter a porta do grupo multicast
        // a partir da variável de ambiente 
        // PORTA_MULTICAST.
        // Caso a variável de ambiente esteja vazia, é atribuído o valor padrão
        // PORTA_MULTICAST_PADRAO
        // 
        try {
            portaMulticast = Integer.parseInt(System.getenv("PORTA_MULTICAST"));
        } catch (final NumberFormatException e){
            portaMulticast = PORTA_MULTICAST_PADRAO;
        }

        // Obter o tempo limite para descobrir servidores
        // a partir da variável de ambiente 
        // TEMPO_LIMITE_PARA_DESCOBERTA.
        // Caso a variável de ambiente esteja vazia, é atribuído o valor padrão
        // TEMPO_LIMITE_PADRAO_PARA_DESCOBERTA
        // 
        try {
            tempoLimiteDescobrir = Integer.parseInt(System.getenv("TEMPO_LIMITE_PARA_DESCOBERTA"));
        } catch (final NumberFormatException e){
            tempoLimiteDescobrir = TEMPO_LIMITE_PADRAO_PARA_DESCOBERTA;
        }

        // Obter uma string com uma ou mais frases, separadas por ponto e 
        // vírgula (;), a partir da variável de ambiente PILOTO_AUTOMATICO.
        // Somente uma frase será selecionada.
        pilotoAutomatico = System.getenv("PILOTO_AUTOMATICO");

        // Se a variável de ambiente PILOTO_AUTOMATICO estiver declarada,
        // exibe de forma clara para o usuário que o modo piloto automático
        // está ativo.
        if (pilotoAutomatico != null) {
            System.out.println("╔═════════════════════════════════╗");
            System.out.println("║ " + AMARELO + "Modo piloto automático ativado." + NORMAL + " ║");
            System.out.println("╚═════════════════════════════════╝\n");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Caso sejam informados 4 argumentos de linha de comando,
        // obtém o endereço multicast, a porta e o tempo limite para
        // a descoberta de servidores a partir destes argumentos.
        if (args.length == 4) {
            enderecoMulticast = args[1];
            portaMulticast = Integer.parseInt(args[2]);
            tempoLimiteDescobrir = Integer.parseInt(args[3]);
        }

        // Instancia o cliente e faz a descoberta de servidores na rede local
        Cliente cliente = new Cliente(logger, enderecoMulticast, portaMulticast, tempoLimiteDescobrir);

        // Faz a descoberta dos servidors via multicast
        cliente.descobreServidores();

        String titulo = "Servidores descobertos";
        String pedido = "Escolha um dos servidores abaixo para enviar a mensagem";
        int indiceDigitado = 0;
        int quantidadeServidoresEncontrados = cliente.getServidoresDescobertos().size();
        String mensagem = "";

        if (pilotoAutomatico != null) {
            // Seleciona um servidor aleatório para enviar a mensagem.
            Random random = new Random();

            // Caso haja somente um servidor, este será o utilizado
            if (quantidadeServidoresEncontrados == 1) indiceDigitado = 1;
            else indiceDigitado = 1 + random.nextInt(cliente.getServidoresDescobertos().size()-1);

            String mensagens[] = pilotoAutomatico.split(";");
            int posicaoMensagem = 0;

            if (mensagens.length == 1) mensagem = mensagens[0];
            else {
                posicaoMensagem = random.nextInt(mensagens.length - 1);
                mensagem = mensagens[posicaoMensagem];
            }

            System.out.println("Servidores descobertos\n");
            listaNumerada(cliente.getServidoresDescobertos(), indiceDigitado);

            System.out.println("\nMensagens disponíveis\n");
            listaNumerada(mensagens, posicaoMensagem + 1);
            
        } else {
            // Exibição do menu de servidores a serem escolhidos, tendo como resultado
            // uma opção válida
            indiceDigitado = menuOpcoes(titulo, pedido, cliente.getServidoresDescobertos());

            // Caso o usuário tenha escolhido a opção 0, a aplicação é encerrada
            if (indiceDigitado == 0) {
                System.exit(0);
            }

            teclado.nextLine(); // Consome a quebra de linha

            // A mensagem que será enviada para o servidor escolhido
            System.out.print("Digite a mensagem a ser enviada: ");
            mensagem = teclado.nextLine();
        }

        // Separa a informações do servidor em um arranjo de strings (ip e porta)
        String infoServidor[] = cliente.getServidoresDescobertos().get(indiceDigitado - 1).split(":");
        String ipServidor = infoServidor[0];
        int portaServidor = Integer.parseInt(infoServidor[1]);

        // Mostra a mensagem que será enviada e para qual servidor será enviada
        System.out.println(VERDE + "\nEnviando a mensagem \"" + CIANO + mensagem + VERDE + "\" para o servidor " + ipServidor + ":" + portaServidor + "\n" + NORMAL);

        // Envia a mensagem para o servidor escolhido e recebe de retorno a resposta do servidor
        String resposta = cliente.comunicacao(ipServidor, portaServidor, mensagem);

        System.out.println("Resposta do servidor: " + resposta);
        teclado.close();
    }
}