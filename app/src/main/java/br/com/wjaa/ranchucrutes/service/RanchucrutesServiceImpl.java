package br.com.wjaa.ranchucrutes.service;

import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.vo.ConvenioCategoriaVo;
import br.com.wjaa.ranchucrutes.vo.ConvenioVo;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;

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
}
