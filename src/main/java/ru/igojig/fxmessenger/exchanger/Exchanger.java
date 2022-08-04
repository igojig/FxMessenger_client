package ru.igojig.fxmessenger.exchanger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ru.igojig.fxmessenger.model.User;

import java.io.Serializable;

@ToString
@AllArgsConstructor
@Getter
public class Exchanger implements Serializable {
    String command;
    String message;
    User user;

}
