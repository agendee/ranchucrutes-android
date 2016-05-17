package br.com.wjaa.ranchucrutes.service;

import java.util.List;

import br.com.wjaa.ranchucrutes.entity.ProfissionalFavoritoEntity;
import br.com.wjaa.ranchucrutes.exception.ProfissionalServiceException;
import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaClinicaVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaProfissionalVo;

/**
 * Created by wagner on 25/07/15.
 */
public interface ProfissionalService {

    ResultadoBuscaClinicaVo find(Integer idEspecialidade, String cep) throws ProfissionalServiceException;

    ResultadoBuscaClinicaVo find(Integer idEspecialidade, LocationVo location) throws ProfissionalServiceException;

    ProfissionalBasicoVo getProfissionalById(Long idProfissional);

    List<ProfissionalFavoritoEntity> listProfissionalFavorito();

    void addProfissionalFavorito(ProfissionalBasicoVo p);

    boolean isFavorito(Integer idProfissional, Integer idClinica);

    ProfissionalFavoritoEntity getProfissionalFavorito(Integer idProfissional, Integer idClinica);

    void removeProfissionalFavorito(ProfissionalBasicoVo profissional);
}
