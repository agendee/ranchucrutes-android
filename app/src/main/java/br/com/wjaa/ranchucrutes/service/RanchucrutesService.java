package br.com.wjaa.ranchucrutes.service;

import br.com.wjaa.ranchucrutes.vo.ConvenioCategoriaVo;
import br.com.wjaa.ranchucrutes.vo.ConvenioVo;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;

/**
 * Created by wagner on 26/07/15.
 */
public interface RanchucrutesService {

    EspecialidadeVo[] getEspecialidades();
    ConvenioVo[] getConvenios();
    ConvenioCategoriaVo[] getConvenioCategoriasByIdConvenio(Integer idConvenio);
    ConvenioCategoriaVo getConvenioCategoriasById(Integer idCategoria);
}
