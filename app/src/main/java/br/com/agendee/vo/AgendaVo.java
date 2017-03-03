package br.com.agendee.vo;

import java.util.Date;
import java.util.List;

/**
 * Created by wagner on 20/10/15.
 */
public class AgendaVo {
    private ProfissionalBasicoVo profissional;
    private List<Date> horariosDisponiveis;

    public ProfissionalBasicoVo getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalBasicoVo profissional) {
        this.profissional = profissional;
    }

    public List<Date> getHorariosDisponiveis() {
        return horariosDisponiveis;
    }

    public void setHorariosDisponiveis(List<Date> horariosDisponiveis) {
        this.horariosDisponiveis = horariosDisponiveis;
    }

}
