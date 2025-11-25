package br.gov.sp.fatec.fatecroom.models;

import java.util.List;
import java.util.Map;

public class User {
    private User() {}

    public static final String ID_FIELD = "id_usuario";
    public static final String NAME_FIELD = "nome_usuario";
    public static final String USER_TYPE_FIELD = "tipo_usuario";
    public static final String EMAIL_FIELD = "email";
    public static final String PASSWORD_FIELD = "senha";
    public static final String ACTIVE_FIELD = "ativo";

    public static final String ADMIN_TYPE = "ADMIN";
    public static final String PROFESSOR_TYPE = "PROFESSOR";
    public static final String STUDENT_TYPE = "STUDENT";
    // This don't ensure the list is unmodifiable, but it's enough for our use case
    public static final List<String> VALID_USER_TYPES = List.of(
        ADMIN_TYPE,
        PROFESSOR_TYPE,
        STUDENT_TYPE
    );

    private static final String FATEC_DOMAIN = "@fatec.sp.gov.br";
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_PASSWORD_LENGTH = 12;
    private static final int MAX_PASSWORD_LENGTH = 256; 

    /**
     * Validates user data from a map.
     * @param userData Map containing user data to validate.
     * @throws IllegalArgumentException if any validation rule is violated.
     */
    public static void validate(Map<String, String> userData) throws IllegalArgumentException {
        if (userData.containsKey(ID_FIELD)) {
            if (userData.get(ID_FIELD).isEmpty())
                throw new IllegalArgumentException("ID do usuário não pode estar vazio.");
            else if (!userData.get(ID_FIELD).matches("\\d+"))
                throw new IllegalArgumentException("ID do usuário deve ser um número inteiro positivo.");
        }

        if (!userData.containsKey(NAME_FIELD))
            throw new IllegalArgumentException("Nome do usuário é obrigatório.");
        else if (userData.get(NAME_FIELD).length() < MIN_NAME_LENGTH || userData.get(NAME_FIELD).length() > MAX_NAME_LENGTH)
            throw new IllegalArgumentException("Nome do usuário deve ter entre %d e %d caracteres.".formatted(MIN_NAME_LENGTH, MAX_NAME_LENGTH));

        if (!userData.containsKey(USER_TYPE_FIELD))
            throw new IllegalArgumentException("Tipo do usuário é obrigatório.");
        else if (!VALID_USER_TYPES.contains(userData.get(USER_TYPE_FIELD)))
            throw new IllegalArgumentException("Tipo do usuário inválido. Tipos válidos: %s.".formatted(String.join(", ", VALID_USER_TYPES)));

        if (!userData.containsKey(EMAIL_FIELD))
            throw new IllegalArgumentException("Email do usuário é obrigatório.");
        else if (!userData.get(EMAIL_FIELD).endsWith(FATEC_DOMAIN))
            throw new IllegalArgumentException("Email do usuário deve pertencer ao domínio %s.".formatted(FATEC_DOMAIN));
        else if (userData.get(EMAIL_FIELD).equals(FATEC_DOMAIN))
            throw new IllegalArgumentException("Parte local do email do usuário não pode estar vazia.");

        if (!userData.containsKey(PASSWORD_FIELD))
            throw new IllegalArgumentException("Senha do usuário é obrigatória.");
        else if (userData.get(PASSWORD_FIELD).length() < MIN_PASSWORD_LENGTH || userData.get(PASSWORD_FIELD).length() > MAX_PASSWORD_LENGTH)
            throw new IllegalArgumentException("Senha do usuário deve ter entre %d e %d caracteres.".formatted(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));

        if (!userData.containsKey(ACTIVE_FIELD))
            throw new IllegalArgumentException("Campo de ativo do usuário é obrigatório.");
        else if (!userData.get(ACTIVE_FIELD).equals("true") && !userData.get(ACTIVE_FIELD).equals("false"))
            throw new IllegalArgumentException("Campo de ativo do usuário deve ser 'true' ou 'false'.");
    }
}
