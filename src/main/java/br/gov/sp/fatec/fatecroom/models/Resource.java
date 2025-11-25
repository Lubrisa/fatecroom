package br.gov.sp.fatec.fatecroom.models;

import java.util.Map;

public class Resource {
    private Resource() {}

    private static final String ID_FIELD = "id_recurso";
    private static final String NAME_FIELD = "nome_recurso";
    private static final String RESOURCE_TYPE_FIELD = "tipo_recurso";
    private static final String PATRIMONY_FIELD = "patrimonio";
    private static final String DEFAULT_LOCATION_FIELD = "local_padrao";
    private static final String OBSERVATION_FIELD = "observacao";
    

    public static final String PROJECTOR_TYPE = "PROJETOR";
    public static final String MICROPHONE_TYPE = "MICROFONE";
    public static final String SPEAKER_TYPE = "ALTO_FALANTE";
    public static final String WHITEBOARD_TYPE = "QUADRO_BRANCO";
    public static final String COMPUTER_TYPE = "COMPUTADOR";
    public static final java.util.List<String> VALID_RESOURCE_TYPES = java.util.List.of(
        PROJECTOR_TYPE,
        MICROPHONE_TYPE,
        SPEAKER_TYPE,
        WHITEBOARD_TYPE,
        COMPUTER_TYPE
    );

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 100;

    /**
     * Validates resource data from a map.
     *
     * @param resourceData Map containing resource data to validate.
     * @throws IllegalArgumentException if any validation rule is violated.
     */
    public static void validate(Map<String, String> resourceData) throws IllegalArgumentException {
        if (resourceData.containsKey(ID_FIELD)) {
            if (resourceData.get(ID_FIELD).isEmpty())
                throw new IllegalArgumentException("ID do recurso não pode estar vazio.");
            else if (!resourceData.get(ID_FIELD).matches("\\d+"))
                throw new IllegalArgumentException("ID do recurso deve ser um número inteiro positivo.");
        }

        if (!resourceData.containsKey(NAME_FIELD))
            throw new IllegalArgumentException("Nome do recurso é obrigatório.");
        else if (resourceData.get(NAME_FIELD).length() < MIN_NAME_LENGTH || resourceData.get(NAME_FIELD).length() > MAX_NAME_LENGTH)
            throw new IllegalArgumentException("Nome do recurso deve ter entre %d e %d caracteres.".formatted(MIN_NAME_LENGTH, MAX_NAME_LENGTH));

        if (!resourceData.containsKey(RESOURCE_TYPE_FIELD))
            throw new IllegalArgumentException("Tipo do recurso é obrigatório.");
        else if (!VALID_RESOURCE_TYPES.contains(resourceData.get(RESOURCE_TYPE_FIELD)))
            throw new IllegalArgumentException("Tipo do recurso inválido. Tipos válidos: %s.".formatted(String.join(", ", VALID_RESOURCE_TYPES)));

        if (!resourceData.containsKey(PATRIMONY_FIELD))
            throw new IllegalArgumentException("Patrimônio do recurso é obrigatório.");
        else if (resourceData.get(PATRIMONY_FIELD).isEmpty())
            throw new IllegalArgumentException("Patrimônio do recurso não pode estar vazio.");
        else if (!resourceData.get(PATRIMONY_FIELD).matches("\\d+"))
            throw new IllegalArgumentException("Patrimônio do recurso deve ser um número inteiro positivo.");

        if (!resourceData.containsKey(DEFAULT_LOCATION_FIELD))
            throw new IllegalArgumentException("Local padrão do recurso é obrigatório.");
        else if (resourceData.get(DEFAULT_LOCATION_FIELD).isEmpty())
            throw new IllegalArgumentException("Local padrão do recurso não pode estar vazio.");
        // Optional observation length check
        if (resourceData.containsKey(OBSERVATION_FIELD)) {
            var obs = resourceData.get(OBSERVATION_FIELD);
            if (obs.length() > 500)
                throw new IllegalArgumentException("Observação do recurso deve ter até %d caracteres.".formatted(500));
        }
    }
}
