package br.com.fatecroom.ui.common;

/**
 * Interface genérica para formulários de edição/cadastro.
 * @param <T> tipo de objeto representado pelo formulário.
 */
public interface Form<T> {

    /**
     * Lê os campos do formulário e devolve um objeto do tipo T.
     * Lance IllegalArgumentException se algum campo obrigatório estiver inválido.
     */
    T getData();

    /**
     * Preenche o formulário com os dados do objeto.
     */
    void setData(T data);

    /**
     * Limpa todos os campos do formulário.
     */
    void clear();
}
