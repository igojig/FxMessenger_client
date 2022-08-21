package ru.igojig.fxmessenger.exchanger;

import lombok.*;
import ru.igojig.fxmessenger.prefix.Prefix;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exchanger implements Serializable {
    @Serial
    private static final long serialVersionUID= -3079205955494740913L;

    private Prefix command;

    private String message;

    private ChatExchanger chatExchanger;

    public <T extends ChatExchanger> T getChatExchanger(Class<T> tClass) {
        return tClass.cast(chatExchanger);
    }


}
