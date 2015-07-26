package br.com.wjaa.ranchucrutes.service;

import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.vo.EspecialidadeVo;

/**
 * Created by wagner on 26/07/15.
 */
public class RanchucrutesServiceImpl implements RanchucrutesService{
    private static final String URL = "rest.marcmed.com.br";

    @Override
    public EspecialidadeVo[] getEspecialidades() {
        try {
            return RestUtils.getJsonWithParamPath(EspecialidadeVo[].class,URL,"especialidade","all");
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
