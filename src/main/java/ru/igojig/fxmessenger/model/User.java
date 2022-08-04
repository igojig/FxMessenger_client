package ru.igojig.fxmessenger.model;


import lombok.*;

import java.io.Serial;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -1584943797365343078L;

    Long id;

    @EqualsAndHashCode.Exclude
    String username;
    @EqualsAndHashCode.Exclude
    String login;
    @EqualsAndHashCode.Exclude
    String password;
}
