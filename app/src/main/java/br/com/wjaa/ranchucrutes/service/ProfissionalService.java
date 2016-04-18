package br.com.wjaa.ranchucrutes.service;

import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaClinicaVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaProfissionalVo;

/**
 * Created by wagner on 25/07/15.
 */
public interface ProfissionalService {

    ResultadoBuscaClinicaVo find(Integer idEspecialidade, String cep);

    ResultadoBuscaClinicaVo find(Integer idEspecialidade, LocationVo location);

    ProfissionalBasicoVo getProfissionalById(Long idProfissional);
}
