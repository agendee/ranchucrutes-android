package br.com.agendee.service;

import br.com.agendee.rest.RestUtils;
import br.com.agendee.vo.ConvenioVo;
import br.com.agendee.exception.RestException;
import br.com.agendee.exception.RestRequestUnstable;
import br.com.agendee.exception.RestResponseUnsatisfiedException;
import br.com.agendee.vo.ConvenioCategoriaVo;
import br.com.agendee.vo.EspecialidadeVo;
import br.com.agendee.vo.ProfissaoVo;

/**
 * Created by wagner on 26/07/15.
 */
public class RanchucrutesServiceImpl implements RanchucrutesService{

    @Override
    public EspecialidadeVo[] getEspecialidades() {
        try {
            return RestUtils.getJsonWithParamPath(EspecialidadeVo[].class,RanchucrutesConstants.WS_HOST,
                    "especialidade","all");
        } catch (RestResponseUnsatisfiedException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } catch (RestRequestUnstable restRequestUnstable) {
            restRequestUnstable.printStackTrace();
        }

        return null;
    }

    @Override
    public ConvenioVo[] getConvenios() {
        try {
            return RestUtils.getJsonWithParamPath(ConvenioVo[].class,RanchucrutesConstants.WS_HOST,
                    "convenio","all");
        } catch (RestResponseUnsatisfiedException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } catch (RestRequestUnstable restRequestUnstable) {
            restRequestUnstable.printStackTrace();
        }

        return null;
    }

    @Override
    public ConvenioCategoriaVo[] getConvenioCategoriasByIdConvenio(Integer idConvenio) {
        try {
            return RestUtils.getJsonWithParamPath(ConvenioCategoriaVo[].class,RanchucrutesConstants.WS_HOST,
                    "convenio","categoria",idConvenio.toString());
        } catch (RestResponseUnsatisfiedException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } catch (RestRequestUnstable restRequestUnstable) {
            restRequestUnstable.printStackTrace();
        }

        return null;
    }

    @Override
    public ConvenioCategoriaVo getConvenioCategoriasById(Integer idCategoria) {
        try {
            return RestUtils.getJsonWithParamPath(ConvenioCategoriaVo.class,RanchucrutesConstants.WS_HOST,
                    "categoria",idCategoria.toString());
        } catch (RestResponseUnsatisfiedException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } catch (RestRequestUnstable restRequestUnstable) {
            restRequestUnstable.printStackTrace();
        }

        return null;
    }

    @Override
    public ProfissaoVo[] getProfissoes() {
        try {
            return RestUtils.getJsonWithParamPath(ProfissaoVo[].class,RanchucrutesConstants.WS_HOST,
                    "profissao","all");
        } catch (RestResponseUnsatisfiedException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } catch (RestRequestUnstable restRequestUnstable) {
            restRequestUnstable.printStackTrace();
        }

        return null;
    }

    @Override
    public EspecialidadeVo[] getEspecialidadesByProfissao(Integer idProfissao) {
        try {
            return RestUtils.getJsonWithParamPath(EspecialidadeVo[].class,RanchucrutesConstants.WS_HOST,
                    "especialidade",idProfissao.toString());
        } catch (RestResponseUnsatisfiedException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } catch (RestRequestUnstable restRequestUnstable) {
            restRequestUnstable.printStackTrace();
        }

        return null;
    }
}
