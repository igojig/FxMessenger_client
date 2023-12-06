package ru.igojig.fxmessenger.exchanger.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.igojig.fxmessenger.exchanger.ChatExchanger;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryExchanger implements ChatExchanger, Serializable {
    @Serial
    private static final long serialVersionUID = 5601676629572006971L;
    private List<String> historyList;


}