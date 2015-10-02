package br.com.wjaa.ranchucrutes.vo;

import br.com.wjaa.ranchucrutes.view.SearchingListModel;

/**
 * Created by wagner on 30/09/15.
 */
public class ConvenioVo implements SearchingListModel {


    private Integer id;
    private String nome;
    private Boolean popular;

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

    public Boolean getPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }


    @Override
    public String toString() {
        return "ConvenioVo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", popular=" + popular +
                '}';
    }

    @Override
    public String getName() {
        return this.nome;
    }
}
