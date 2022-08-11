package ru.igojig.fxmessenger.exchanger.impl;

import lombok.*;
import ru.igojig.fxmessenger.exchanger.ChatObject;
import ru.igojig.fxmessenger.model.User;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExchanger implements ChatObject, Serializable {
    @Serial
    private static final long serialVersionUID= -4945206969976763143L;
    private User user;


}
