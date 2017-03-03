package br.com.agendee.service;

import br.com.agendee.vo.ConvenioVo;
import br.com.agendee.vo.ConvenioCategoriaVo;
import br.com.agendee.vo.EspecialidadeVo;
import br.com.agendee.vo.ProfissaoVo;

/**
 * Created by wagner on 26/07/15.
 */
public interface RanchucrutesService {

    EspecialidadeVo[] getEspecialidades();
    ConvenioVo[] getConvenios();
    ConvenioCategoriaVo[] getConvenioCategoriasByIdConvenio(Integer idConvenio);
    ConvenioCategoriaVo getConvenioCategoriasById(Integer idCategoria);
    ProfissaoVo[] getProfissoes();
    EspecialidadeVo[] getEspecialidadesByProfissao(Integer id);
}
