package abate.abate.controladores;

import abate.abate.entidades.Cheque.EstadoCheque;
import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.CuentaBancaria.EstadoCuentaBancaria;
import abate.abate.entidades.MovimientoValor;
import abate.abate.entidades.MovimientoValor.TipoMovimientoValor;
import abate.abate.entidades.TitularCheque;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CuentaBancariaServicio;
import abate.abate.servicios.MovimientoValorServicio;
import abate.abate.servicios.TitularChequeServicio;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cuentaBancaria")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class CuentaBancariaControlador {

    @Autowired
    private CuentaBancariaServicio cuentaBancariaServicio;
    @Autowired
    private MovimientoValorServicio movimientoValorServicio;
    @Autowired
    private TitularChequeServicio titularServicio;

    @GetMapping("/listar")
    public String cuentaBancaria(@RequestParam(required = false, defaultValue = "HABILITADA") String estado,
            @RequestParam(required = false, defaultValue = "banco") String orden, @RequestParam(required = false, defaultValue = "false") Boolean registrado,
            @RequestParam(required = false, defaultValue = "false") Boolean actualizado, ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        Long idOrg = usuario.getIdOrg();

        ArrayList<CuentaBancaria> lista = new ArrayList();

        if (estado.equalsIgnoreCase("TODAS")) {

            lista = cuentaBancariaServicio.buscarCuentasBancarias(idOrg, orden);

        } else {

            lista = cuentaBancariaServicio.buscarCuentasBancariasEstado(idOrg, orden, estado);

        }

        BigDecimal totalLimiteOperativo = BigDecimal.ZERO;

        BigDecimal totalSaldoDisponible = BigDecimal.ZERO;

        for (CuentaBancaria cuenta : lista) {

            if (cuenta.getLimiteOperativo() != null) {

                totalLimiteOperativo = totalLimiteOperativo.add(cuenta.getLimiteOperativo());

            }

            if (cuenta.getSaldoDisponible() != null) {

                totalSaldoDisponible = totalSaldoDisponible.add(cuenta.getSaldoDisponible());

            }

        }

        modelo.addAttribute("lista", lista);

        modelo.addAttribute("orden", orden);

        modelo.addAttribute("estado", estado);

        modelo.addAttribute("totalLimiteOperativo", totalLimiteOperativo);

        modelo.addAttribute("totalSaldoDisponible", totalSaldoDisponible);

        if (registrado == true) {

            modelo.put("exito", "Cuenta bancaria registrada correctamente.");

        }

        if (actualizado == true) {

            modelo.put("exito", "Cuenta bancaria actualizada correctamente.");

        }

        return "cuentaBancaria_listar.html";

    }

    @GetMapping("/registrar-cuenta-bancaria")
    public String registrarCuentaBancaria(ModelMap modelo) {

        modelo.addAttribute("estados", EstadoCuentaBancaria.values());

        return "cuentaBancaria_registrar.html";

    }

    @PostMapping("/guardar-cuenta-bancaria")
    public String guardarCuentaBancaria(@RequestParam String banco, @RequestParam String numeroCuenta, @RequestParam BigDecimal saldo,
            @RequestParam EstadoCuentaBancaria estado, HttpSession session, ModelMap modelo) {

        try {

            if (saldo == null || saldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("El límite operativo no puede ser menor a cero.");
            }

            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

            Long idOrg = usuario.getIdOrg();

            cuentaBancariaServicio.validarDatos(idOrg, banco, numeroCuenta);

            CuentaBancaria cuenta = new CuentaBancaria();

            cuenta.setIdOrg(idOrg);

            cuenta.setBanco(banco.toUpperCase());

            cuenta.setNumeroCuenta(numeroCuenta.toUpperCase());

            cuenta.setLimiteOperativo(saldo);

            cuenta.setSaldoDisponible(saldo);

            cuenta.setEstado(estado);

            cuentaBancariaServicio.registrarCuentaBancaria(cuenta);

            Boolean registrado = true;

            return "redirect:/cuentaBancaria/listar?&registrado=" + registrado;

        } catch (Exception e) {

            modelo.addAttribute("estados", EstadoCuentaBancaria.values());
            modelo.put("error", e.getMessage());

            return "cuentaBancaria_registrar.html";

        }

    }

    @GetMapping("/modificar-cuenta-bancaria/{id}")
    public String modificarCuentaBancaria(@PathVariable Long id, ModelMap modelo) throws Exception {

        CuentaBancaria respuesta = cuentaBancariaServicio.buscarPorId(id);

        if (respuesta != null) {

            modelo.addAttribute("cuenta", respuesta);
            modelo.addAttribute("estados", EstadoCuentaBancaria.values());

            return "cuentaBancaria_modificar.html";

        }

        return "redirect:/cuentaBancaria/listar";

    }

    @PostMapping("/actualizar-cuenta-bancaria")
    public String actualizarCuentaBancaria(@RequestParam Long id, @RequestParam String banco, @RequestParam String numeroCuenta,
            @RequestParam BigDecimal limiteOperativo, @RequestParam EstadoCuentaBancaria estado, ModelMap modelo) throws Exception {

        try {

            cuentaBancariaServicio.modificarCuentaBancaria(id, banco, numeroCuenta, limiteOperativo, estado);

            Boolean actualizado = true;

            return "redirect:/cuentaBancaria/listar?&actualizado=" + actualizado;

        } catch (Exception e) {

            modelo.addAttribute("cuenta", cuentaBancariaServicio.buscarPorId(id));
            modelo.addAttribute("estados", EstadoCuentaBancaria.values());
            modelo.put("error", e.getMessage());

            return "cuentaBancaria_modificar.html";
        }
    }

    @GetMapping("/movimiento/{id}")
    public String movimientosCuenta(@PathVariable Long id, ModelMap modelo) throws Exception {

        CuentaBancaria cuenta = cuentaBancariaServicio.buscarPorId(id);

        if (cuenta == null) {
            throw new Exception("Cuenta bancaria no encontrada.");
        }

        ArrayList<MovimientoValor> movimientos = movimientoValorServicio.buscarMovimientosCuenta(cuenta);

        BigDecimal totalPendienteLiberar = cuenta.getLimiteOperativo().subtract(cuenta.getSaldoDisponible());

        modelo.addAttribute("cuenta", cuenta);
        modelo.addAttribute("movimientos", movimientos);
        modelo.addAttribute("totalPendiente", totalPendienteLiberar);
        modelo.addAttribute("titulares", titularServicio.buscarTitularesHabilitados(cuenta.getIdOrg()));

        return "cuentaBancaria_movimiento.html";

    }
    
    @GetMapping("/movimiento/titular")
    public String movimientosCuentaPorTitular(@RequestParam Long idCuenta,
        @RequestParam Long idTitular, ModelMap modelo) throws Exception {

    CuentaBancaria cuenta = cuentaBancariaServicio.buscarPorId(idCuenta);

    if (cuenta == null) {
        throw new Exception("Cuenta bancaria no encontrada.");
    }

    TitularCheque titular = titularServicio.buscarPorId(idTitular);

    if (titular == null) {
        throw new Exception("Titular no encontrado.");
    }

    ArrayList<MovimientoValor> movimientos = movimientoValorServicio.buscarVentasPendientesPorCuentaYTitular(cuenta, titular);

   BigDecimal totalTitularPendiente = BigDecimal.ZERO;

   for (MovimientoValor m : movimientos) {
    totalTitularPendiente = totalTitularPendiente.add(m.getImporte());
   }

    modelo.addAttribute("movimientos", movimientos);
    modelo.addAttribute("titular", titular);
    modelo.addAttribute("cuenta", cuenta);
    modelo.addAttribute("totalPendiente", totalTitularPendiente);

    return "fragmentos/cuentaBancaria_chequesVendidos :: resultados";
}

}
