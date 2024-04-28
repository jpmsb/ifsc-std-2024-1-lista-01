package engtelecom.std;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class FormatadorDeLog extends Formatter {
    /**
     * Formato da data e hora que será exibido no log.
     * Esse formato garante que seja adicionado um zero
     * à esquerda caso o valor seja menor que 10, garantindo
     * que os campos sempre possuam a quantidade de dígitos
     * desejada, sendo 4 para o ano e 2 para o restante.
     */
    private final DateTimeFormatter DATA_E_HORA_FORMATADA = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime dataEHora = LocalDateTime.now();

    /**
     * Formata a mensagem para que fique no formato abaixo:
     * ANO/MÊS/DIA HORA:MINUTO:SEGUNDO - NÍVEL - MENSAGEM
     * @param record Objeto que conterá a mensagem e o nível do log
     */
    @Override
    public String format(LogRecord record) {
        String dataEHoraFormatada = dataEHora.format(DATA_E_HORA_FORMATADA);

        StringBuilder saida = new StringBuilder();
        saida.append(dataEHoraFormatada);
        saida.append(" - ");
        saida.append(record.getLevel());
        saida.append(" - ");
        saida.append(record.getMessage());
        saida.append(System.lineSeparator());
        return saida.toString();
    }
}
