package br.com.wjaa.ranchucrutes.service;

import br.com.wjaa.ranchucrutes.vo.LocationVo;
import br.com.wjaa.ranchucrutes.vo.ResultadoBuscaMedicoVo;

/**
 * Created by wagner on 25/07/15.
 */
public interface MedicoService {

    ResultadoBuscaMedicoVo find(Integer idEspecialidade, String cep);

    ResultadoBuscaMedicoVo find(Integer idEspecialidade, LocationVo location);

}
