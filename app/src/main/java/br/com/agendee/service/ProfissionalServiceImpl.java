package br.com.agendee.service;

import com.google.inject.Inject;

import java.util.List;

import br.com.agendee.entity.ProfissionalFavoritoEntity;
import br.com.agendee.exception.RestResponseUnsatisfiedException;
import br.com.agendee.rest.RestUtils;
import br.com.agendee.utils.ObjectUtils;
import br.com.agendee.exception.ProfissionalServiceException;
import br.com.agendee.exception.RestException;
import br.com.agendee.exception.RestRequestUnstable;
import br.com.agendee.form.FindClinicaForm;
import br.com.agendee.vo.LocationVo;
import br.com.agendee.vo.ProfissionalBasicoVo;
import br.com.agendee.vo.ResultadoBuscaClinicaVo;

/**
 * Created by wagner on 25/07/15.
 */
public class ProfissionalServiceImpl implements ProfissionalService {

    @Inject
    private DataService dataService;


    @Override
    public ResultadoBuscaClinicaVo find(Integer idEspecialidade, String cep) throws ProfissionalServiceException {
        FindClinicaForm form = new FindClinicaForm();
        form.setCep(cep);
        form.setIdEspecialidade(idEspecialidade);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ResultadoBuscaClinicaVo.class,RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_PROCURAR_CLINICAS,json);
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            throw new ProfissionalServiceException(e.getMessage());
        } catch (RestException e){
            throw new ProfissionalServiceException(e.getErrorMessage().getErrorMessage());
        }
    }

    @Override
    public ResultadoBuscaClinicaVo find(Integer idEspecialidade, LocationVo location) throws ProfissionalServiceException {
        FindClinicaForm form = new FindClinicaForm();
        form.setLocation(location);
        form.setIdEspecialidade(idEspecialidade);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ResultadoBuscaClinicaVo.class,RanchucrutesConstants.WS_HOST,RanchucrutesConstants.END_POINT_PROCURAR_CLINICAS,json);
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            throw new ProfissionalServiceException(e.getMessage());
        } catch (RestException e){
            throw new ProfissionalServiceException(e.getErrorMessage().getErrorMessage());
        }
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

        ProfissionalFavoritoEntity pfe = getProfissionalFavorito(p.getId().intValue(),p.getIdClinicaAtual().intValue());

        if (pfe == null){
            pfe = new ProfissionalFavoritoEntity();
        }
        pfe.setIdProfissional(p.getId().intValue());
        pfe.setIdClinica(p.getIdClinicaAtual().intValue());
        pfe.setNome(p.getNome());
        pfe.setEspec(p.getEspec());
        dataService.insertOrUpdate(pfe);
    }

    @Override
    public boolean isFavorito(Integer idProfissional, Integer idClinica) {
        ProfissionalFavoritoEntity pfe = getProfissionalFavorito(idProfissional,idClinica);
        return pfe != null;
    }

    @Override
    public ProfissionalFavoritoEntity getProfissionalFavorito(Integer idProfissional, Integer idClinica) {
        return dataService.findUniqueResult(ProfissionalFavoritoEntity.class,"id_profissional=? and id_clinica=?",
                new String[]{idProfissional.toString(),idClinica.toString()});

    }

    @Override
    public void removeProfissionalFavorito(ProfissionalBasicoVo profissional) {
        ProfissionalFavoritoEntity pfe = getProfissionalFavorito(profissional.getId().intValue(),
                profissional.getIdClinicaAtual().intValue());
        dataService.deleteById(pfe);
    }
}
