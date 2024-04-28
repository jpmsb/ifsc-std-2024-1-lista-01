package engtelecom.std;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class FormatadorDeLog extends Formatter {
    private final DateTimeFormatter DATA_E_HORA_FORMATADA = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime dataEHora = LocalDateTime.now();

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
