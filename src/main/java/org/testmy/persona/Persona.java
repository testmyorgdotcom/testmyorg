package org.testmy.persona;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Persona {
    private String name;
    private String username;

    public Persona(final String personaName) {
        this.name = personaName;
    }
}
