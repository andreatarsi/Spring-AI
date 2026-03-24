package spring.ai.example.spring_ai_demo.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component // Lo rendiamo un Bean per il supporto AOT e riflessione
public class DateTimeTools {

    @Tool(description = "Get the current date and time in the user's timezone") //
    public String getCurrentDateTime() {
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

    @Tool(description = "Imposta una sveglia tra un numero specifico di minuti da ora")
    public String setAlarmInMinutes(
            @ToolParam(description = "Il numero di minuti da attendere (es. 15)") int minutes) {

        LocalDateTime alarmTime = LocalDateTime.now().plusMinutes(minutes);
        String formattedTime = alarmTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        String msg = "Sveglia impostata correttamente per le " + formattedTime;
        System.out.println(msg);
        return msg;
    }
}