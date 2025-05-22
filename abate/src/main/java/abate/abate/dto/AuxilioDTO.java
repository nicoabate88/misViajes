
package abate.abate.dto;

import java.util.Objects;

public class AuxilioDTO {
    
    private Integer posicion;
    private Long neumaticoId;
    

    public AuxilioDTO() {
    }

    public AuxilioDTO(Integer posicion, Long neumaticoId) {
        this.posicion = posicion;
        this.neumaticoId = neumaticoId;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public Long getNeumaticoId() {
        return neumaticoId;
    }

    public void setNeumaticoId(Long neumaticoId) {
        this.neumaticoId = neumaticoId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuxilioDTO)) return false;
        AuxilioDTO that = (AuxilioDTO) o;
        return Objects.equals(posicion, that.posicion) &&
               Objects.equals(neumaticoId, that.neumaticoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(posicion, neumaticoId);
    }
    
}
