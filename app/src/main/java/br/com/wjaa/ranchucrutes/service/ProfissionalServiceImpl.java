package br.com.wjaa.ranchucrutes.service;

import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.form.FindProfissionalForm;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.utils.ObjectUtils;
import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaProfissionalVo;

/**
 * Created by wagner on 25/07/15.
 */
public class ProfissionalServiceImpl implements ProfissionalService {


    @Override
    public ResultadoBuscaProfissionalVo find(Integer idEspecialidade, String cep) {
        FindProfissionalForm form = new FindProfissionalForm();
        form.setCep(cep);
        form.setIdEspecialidade(idEspecialidade);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ResultadoBuscaProfissionalVo.class,RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_PROCURAR_PROFISSIONAL,json);
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
    public ResultadoBuscaProfissionalVo find(Integer idEspecialidade, LocationVo location) {
        FindProfissionalForm form = new FindProfissionalForm();
        form.setLocation(location);
        form.setIdEspecialidade(idEspecialidade);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ResultadoBuscaProfissionalVo.class,RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_PROCURAR_PROFISSIONAL,json);
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
    public ProfissionalBasicoVo getProfissionalById(Long idProfissional) {
        try {
            return RestUtils.getJsonWithParamPath(ProfissionalBasicoVo.class,
                    RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_GET_PROFISSIONAL, idProfissional.toString());
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
