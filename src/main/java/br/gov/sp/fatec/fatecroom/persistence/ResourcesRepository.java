package br.gov.sp.fatec.fatecroom.persistence;

import br.gov.sp.fatec.fatecroom.models.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ResourcesRepository {
    public static final String FILE_NAME = "recursos.csv";

    
    
    private static int sequence = 1;

    /**
     * Inserts a new resource into the repository.
     * 
     * @param resourceData A map containing the resource data.
     * @return The inserted resource data with the generated ID.
     * @throws IllegalArgumentException If the resource data is invalid.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Map<String, String> insert(Map<String, String> resourceData) throws IllegalArgumentException, IOException {

        if (resourceData == null) {
            throw new IllegalArgumentException("Dados do recurso não podem ser nulos.");
        } else if (resourceData.containsKey(Resource.ID_FIELD)) {
            throw new IllegalArgumentException("ID do recurso não deve ser fornecido na inserção.");
        }

        Resource.validate(resourceData);

        Map<String, String> resourceDataWithId = new java.util.HashMap<>(resourceData);
        resourceDataWithId.put(Resource.ID_FIELD, String.valueOf(sequence++));
        Repository.insertEnsuringUniqueness(FILE_NAME, resourceDataWithId, resource -> resource.get(Resource.ID_FIELD));

        return resourceDataWithId;
    }

    /**
     * Inserts or updates a resource in the repository.
     * 
     * @param resourceData A map containing the resource data.
     * @return The resource data after insertion or update.
     * @throws IllegalArgumentException If the resource data is invalid.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static Map<String, String> upsert(Map<String, String> resourceData) throws IllegalArgumentException, IOException {
        if (resourceData == null) {
            throw new IllegalArgumentException("Dados do recurso não podem ser nulos.");
        } else if (!resourceData.containsKey(Resource.ID_FIELD)) {
            throw new IllegalArgumentException("ID do recurso é obrigatório para atualização.");
        }

        Resource.validate(resourceData);

        Repository.insertOrUpdate(FILE_NAME, resourceData, resource -> resource.get(Resource.ID_FIELD));

        return resourceData;
    }

    /**
     * Retrieves a range of resources from the repository.
     * 
     * @param skip The number of resources to skip.
     * @param take The number of resources to retrieve.
     * @return A list of resource data maps within the specified range.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public static List<Map<String, String>> getRange(int skip, int take) throws IOException {
        return Repository.getRange(
            FILE_NAME,
            skip,
            take
        );
    }
}
