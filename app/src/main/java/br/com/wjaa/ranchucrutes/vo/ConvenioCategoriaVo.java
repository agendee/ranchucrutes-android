package br.com.wjaa.ranchucrutes.vo;

import br.com.wjaa.ranchucrutes.view.SearchableListModel;

/**
 * Created by wagner on 30/09/15.
 */
public class ConvenioCategoriaVo implements SearchableListModel{

    private Integer id;
    private String nome;
    private Integer idConvenio;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(Integer idConvenio) {
        this.idConvenio = idConvenio;
    }

    @Override
    public String getName() {
        return this.nome;
    }
}
