package br.com.wjaa.ranchucrutes.vo;

import br.com.wjaa.ranchucrutes.view.SearchingListModel;

/**
 * Created by wagner on 30/09/15.
 */
public class ConvenioCategoriaVo implements SearchingListModel {

    private Integer id;
    private String nome;
    private Integer idConvenio;
    private ConvenioVo convenioVo;


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

    public ConvenioVo getConvenioVo() {
        return convenioVo;
    }

    public void setConvenioVo(ConvenioVo convenioVo) {
        this.convenioVo = convenioVo;
    }

}
