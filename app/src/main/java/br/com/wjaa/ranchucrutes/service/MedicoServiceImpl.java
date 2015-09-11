package br.com.wjaa.ranchucrutes.service;

import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.form.FindMedicoForm;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.utils.ObjectUtils;
import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaMedicoVo;

/**
 * Created by wagner on 25/07/15.
 */
public class MedicoServiceImpl implements MedicoService{


    @Override
    public ResultadoBuscaMedicoVo find(Integer idEspecialidade, String cep) {
        FindMedicoForm form = new FindMedicoForm();
        form.setCep(cep);
        form.setIdEspecialidade(idEspecialidade);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ResultadoBuscaMedicoVo.class,RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_PROCURAR_MEDICO,json);
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
    public ResultadoBuscaMedicoVo find(Integer idEspecialidade, LocationVo location) {
        FindMedicoForm form = new FindMedicoForm();
        form.setLocation(location);
        form.setIdEspecialidade(idEspecialidade);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ResultadoBuscaMedicoVo.class,RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_PROCURAR_MEDICO,json);
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
