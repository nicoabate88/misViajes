
package abate.abate.dto;

import java.util.List;

public class AuxilioForm {
    
    private List<AuxilioDTO> auxilios;

    public AuxilioForm() {}

    public AuxilioForm(List<AuxilioDTO> auxilios) {
        this.auxilios = auxilios;
    }

    public List<AuxilioDTO> getAuxilios() {
        return auxilios;
    }

    public void setAuxilios(List<AuxilioDTO> auxilios) {
        this.auxilios = auxilios;
    }
    
}
