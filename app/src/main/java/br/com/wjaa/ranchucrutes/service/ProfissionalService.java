package br.com.wjaa.ranchucrutes.service;

import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaProfissionalVo;

/**
 * Created by wagner on 25/07/15.
 */
public interface ProfissionalService {

    ResultadoBuscaProfissionalVo find(Integer idEspecialidade, String cep);

    ResultadoBuscaProfissionalVo find(Integer idEspecialidade, LocationVo location);

}
