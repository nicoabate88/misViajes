
package abate.abate.dto;

public class MantenimientoDTO {
    
    private Long id;
    private Long tipoMantenimiento; 
    private String aplicaA;
    private Integer km;
    private Integer kmVigencia;
    private Integer kmProximo;
    private Integer kmAlarma;
    private String estado;

    public MantenimientoDTO() {
    }

    public MantenimientoDTO(Long id, Long tipoMantenimiento, String aplicaA, Integer km, Integer kmVigencia, Integer kmProximo, Integer kmAlarma, String estado) {
        this.id = id;
        this.tipoMantenimiento = tipoMantenimiento;
        this.aplicaA = aplicaA;
        this.km = km;
        this.kmVigencia = kmVigencia;
        this.kmProximo = kmProximo;
        this.kmAlarma = kmAlarma;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(Long tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public String getAplicaA() {
        return aplicaA;
    }

    public void setAplicaA(String aplicaA) {
        this.aplicaA = aplicaA;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public Integer getKmVigencia() {
        return kmVigencia;
    }

    public void setKmVigencia(Integer kmVigencia) {
        this.kmVigencia = kmVigencia;
    }

    public Integer getKmProximo() {
        return kmProximo;
    }

    public void setKmProximo(Integer kmProximo) {
        this.kmProximo = kmProximo;
    }

    public Integer getKmAlarma() {
        return kmAlarma;
    }

    public void setKmAlarma(Integer kmAlarma) {
        this.kmAlarma = kmAlarma;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    
}
