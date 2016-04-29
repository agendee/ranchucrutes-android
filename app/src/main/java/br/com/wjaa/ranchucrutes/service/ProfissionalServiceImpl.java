package br.com.wjaa.ranchucrutes.service;

import com.google.inject.Inject;

import java.util.List;

import br.com.wjaa.ranchucrutes.entity.ProfissionalFavoritoEntity;
import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.form.FindClinicaForm;
import br.com.wjaa.ranchucrutes.form.FindProfissionalForm;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.utils.ObjectUtils;
import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaClinicaVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaProfissionalVo;

/**
 * Created by wagner on 25/07/15.
 */
public class ProfissionalServiceImpl implements ProfissionalService {

    @Inject
    private DataService dataService;


    @Override
    public ResultadoBuscaClinicaVo find(Integer idEspecialidade, String cep) {
        FindClinicaForm form = new FindClinicaForm();
        form.setCep(cep);
        form.setIdEspecialidade(idEspecialidade);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ResultadoBuscaClinicaVo.class,RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_PROCURAR_CLINICAS,json);
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
    public ResultadoBuscaClinicaVo find(Integer idEspecialidade, LocationVo location) {
        FindClinicaForm form = new FindClinicaForm();
        form.setLocation(location);
        form.setIdEspecialidade(idEspecialidade);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ResultadoBuscaClinicaVo.class,RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_PROCURAR_CLINICAS,json);
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

    @Override
    public List<ProfissionalFavoritoEntity> listProfissionalFavorito() {
        return dataService.getList(ProfissionalFavoritoEntity.class);
    }

    @Override
    public void addProfissionalFavorito(ProfissionalBasicoVo p) {
        ProfissionalFavoritoEntity pfe = new ProfissionalFavoritoEntity();
        pfe.setIdProfissional(p.getIdProfissao());
        pfe.setIdClinica(p.getIdClinicaAtual().intValue());
        pfe.setNome(p.getNome());
        pfe.setEspec(p.getEspec());
        dataService.insert(pfe);
    }

    @Override
    public boolean isFavorito(Integer idProfissional, Integer idClinica) {
        ProfissionalFavoritoEntity pfe = dataService.findUniqueResult(ProfissionalFavoritoEntity.class,"idProfissional=? and idClinica=?",
                new String[]{idProfissional.toString(),idClinica.toString()});
        return pfe != null;
    }

    @Override
    public ProfissionalFavoritoEntity getProfissionalFavorito(Integer idProfissional, Integer idClinica) {
        return dataService.findUniqueResult(ProfissionalFavoritoEntity.class,"idProfissional=? and idClinica=?",
                new String[]{idProfissional.toString(),idClinica.toString()});

    }
}
