package br.com.agendee.service;

import java.util.List;

import br.com.agendee.entity.ProfissionalFavoritoEntity;
import br.com.agendee.exception.ProfissionalServiceException;
import br.com.agendee.vo.LocationVo;
import br.com.agendee.vo.ProfissionalBasicoVo;
import br.com.agendee.vo.ResultadoBuscaClinicaVo;

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
