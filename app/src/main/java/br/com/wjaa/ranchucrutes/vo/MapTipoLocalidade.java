package br.com.wjaa.ranchucrutes.vo;

/**
 * Created by wagner on 4/13/16.
 * Tipo de localidade serve para identificar onde está o profissional.
 * Identificamos ele em um Edificio quando exister mais de um profissional no mesmo endereco (localidade) que não seja da mesma clinica.
 * Identificamos ele em uma clinica quando existir mais de um profissional na mesma clinica.
 * Identificamos ele como particular, quando ele estiver sozinho no endereco.
 *
 * #Importante: Esse tipo de localidade só é importante para ser usado no Mapa, porque não necessariamente um médico pode estar
 * num tipo particular, as vezes ele caiu no particular porque no filtro só foi encontrado ele naquela localidade.
 *
 */
public enum MapTipoLocalidade {

    EDIFICIO,
    CLINICA,
    PARTICULAR

}
