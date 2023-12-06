package ru.igojig.fxmessenger.model;


import lombok.*;

import java.io.Serial;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -1584943797365343078L;

    private Long id;
    private String username;
    private String login;
    private String password;
}
