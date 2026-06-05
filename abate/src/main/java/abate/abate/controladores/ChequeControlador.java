package abate.abate.controladores;

import abate.abate.entidades.Banco;
import abate.abate.entidades.Cheque;
import abate.abate.entidades.Cheque.EstadoCheque;
import abate.abate.entidades.CuentaBancaria;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.BancoServicio;
import abate.abate.servicios.ChequeServicio;
import abate.abate.servicios.CuentaBancariaServicio;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cheque")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class ChequeControlador {

    @Autowired
    private ChequeServicio chequeServicio;

    @Autowired
    private BancoServicio bancoServicio;

    @Autowired
    private CuentaBancariaServicio cuentaBancariaServicio;

    @GetMapping("/listar")
    public String cheque(@RequestParam(required = false) EstadoCheque estado, @RequestParam(required = false, defaultValue = "false") Boolean registrado,
            @RequestParam(required = false, defaultValue = "false") Boolean actualizado,
            ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        Long idOrg = usuario.getIdOrg();

        // ESTADO DEFAULT
        if (estado == null) {
            estado = EstadoCheque.EN_CARTERA;
        }

        ArrayList<Cheque> lista = chequeServicio.buscarChequesPorEstado(idOrg, estado);

        BigDecimal totalImporte = BigDecimal.ZERO;

        for (Cheque cheque : lista) {

            if (cheque.getImporte() != null) {

                totalImporte = totalImporte.add(cheque.getImporte());

            }

        }

        modelo.addAttribute("estadoSeleccionado", estado);

        modelo.addAttribute("estados", EstadoCheque.values());

        modelo.addAttribute("totalImporte", totalImporte);

        modelo.addAttribute("cantidadCheques", lista.size());

        modelo.addAttribute("lista", lista);
        
        if (registrado == true){
            
        modelo.put("exito", "Cheque registrado correctamente.");
            
        }
        
        if (actualizado == true){
            
        modelo.put("exito", "Cheque actualizado correctamente.");
           
        }

        return "cheque_listar.html";

    }

    /*
        REGISTRAR
     */
    @GetMapping("/registrar-cheque")
    public String registrarCheque(ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        Long idOrg = usuario.getIdOrg();

        ArrayList<Banco> bancos = bancoServicio.buscarBancosHabilitados(idOrg);

        modelo.addAttribute("bancos", bancos);

        return "cheque_registrar.html";

    }

    /*
        GUARDAR
     */
    @PostMapping("/guardar-cheque")
    public String guardarCheque(@RequestParam String numeroCheque, @RequestParam Long idBanco, @RequestParam String titular,
            @RequestParam BigDecimal importe, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaEmision,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaVencimiento,
            @RequestParam(required = false) String observacion, HttpSession session, ModelMap modelo) {

        try {

            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

            Long idOrg = usuario.getIdOrg();

            chequeServicio.registrarCheque(idOrg, numeroCheque, idBanco, titular, importe, fechaEmision, fechaVencimiento, observacion);
            
            Boolean registrado = true;

            return "redirect:/cheque/listar?&registrado=" + registrado;

        } catch (Exception e) {

            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

            Long idOrg = usuario.getIdOrg();

            ArrayList<Banco> bancos = bancoServicio.buscarBancosHabilitados(idOrg);

            modelo.addAttribute("bancos", bancos);

            modelo.put("error", e.getMessage());

            return "cheque_registrar.html";

        }

    }

    /*
        MODIFICAR
     */
    @GetMapping("/modificar-cheque/{id}")
    public String modificarCheque(@PathVariable Long id, ModelMap modelo, HttpSession session) throws MiException {

        Cheque cheque = chequeServicio.buscarPorId(id);

        if (cheque != null) {

            if (!cheque.getEstado().equals(EstadoCheque.EN_CARTERA)) {
                throw new MiException("Solo se pueden modificar cheques en cartera");
            }

            Usuario usuario = (Usuario) session.getAttribute("usuariosession");

            Long idOrg = usuario.getIdOrg();

            ArrayList<Banco> bancos = bancoServicio.buscarBancosHabilitados(idOrg);

            ArrayList<EstadoCheque> estados = new ArrayList<>();

            estados.add(EstadoCheque.EN_CARTERA);
            estados.add(EstadoCheque.ANULADO);

            modelo.addAttribute("estados", estados);

            modelo.addAttribute("bancos", bancos);

            modelo.addAttribute("cheque", cheque);

            return "cheque_modificar.html";

        }

        return "redirect:/cheque/listar";

    }

    @PostMapping("/actualizar-cheque")
    public String actualizarCheque(@RequestParam Long idCheque, @RequestParam String numeroCheque,
            @RequestParam Long idBanco, @RequestParam String titular, @RequestParam BigDecimal importe,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaEmision,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaVencimiento,
            @RequestParam String observacion, @RequestParam EstadoCheque estado, RedirectAttributes redirectAttributes) {

        try {

            chequeServicio.actualizarCheque(idCheque, numeroCheque, idBanco, titular,
                    importe, fechaEmision, fechaVencimiento, observacion, estado);
            
            Boolean actualizado = true;

            return "redirect:/cheque/listar?&actualizado=" + actualizado;

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/cheque/modificar-cheque/" + idCheque;
        }

    }

    /*
    SELECCIONAR CUENTA PARA VENTA
     */
    @GetMapping("/venta")
    public String ventaCheque(ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        Long idOrg = usuario.getIdOrg();

        ArrayList<CuentaBancaria> cuentas = cuentaBancariaServicio.buscarCuentasBancariasHabilitadas(idOrg);

        modelo.addAttribute("cuentas", cuentas);

        return "cheque_venta.html";

    }

    /*
    MOSTRAR CHEQUES EN CARTERA
     */
    @PostMapping("/venta")
    public String ventaChequeOperacion(@RequestParam Long idCuentaBancaria, ModelMap modelo, HttpSession session) throws Exception {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        Long idOrg = usuario.getIdOrg();

        CuentaBancaria cuenta = cuentaBancariaServicio.buscarPorId(idCuentaBancaria);

        if (cuenta == null) {
            throw new Exception("Cuenta bancaria no encontrada.");
        }

        ArrayList<Cheque> lista = chequeServicio.buscarChequesPorEstado(idOrg, EstadoCheque.EN_CARTERA);

        modelo.addAttribute("cuenta", cuenta);

        modelo.addAttribute("cheques", lista);

        return "cheque_ventaOperacion.html";

    }

    /*
    CONFIRMAR VENTA
     */
    @PostMapping("/confirmar-venta")
    public String confirmarVenta(@RequestParam Long idCuentaBancaria, @RequestParam(name = "idsCheques") List<Long> idsCheques,
            RedirectAttributes redirectAttributes) {

        try {

            chequeServicio.venderCheques(idsCheques, idCuentaBancaria);

            redirectAttributes.addFlashAttribute("exito", "Venta realizada correctamente.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

        }

        return "redirect:/cuentaBancaria/listar";

    }

    @GetMapping("/buscar-numero")
    public String buscarNumeroCheque(@RequestParam String numeroCheque, ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        Long idOrg = usuario.getIdOrg();

        ArrayList<Cheque> lista = chequeServicio.buscarChequesPorNumero(idOrg, numeroCheque);

        BigDecimal totalImporte = BigDecimal.ZERO;

        for (Cheque cheque : lista) {

            if (cheque.getImporte() != null) {

                totalImporte = totalImporte.add(cheque.getImporte());

            }

        }

        modelo.addAttribute("estadoSeleccionado", EstadoCheque.EN_CARTERA);

        modelo.addAttribute("estados", EstadoCheque.values());

        modelo.addAttribute("totalImporte", totalImporte);

        modelo.addAttribute("cantidadCheques", lista.size());
        
        modelo.addAttribute("numeroChequeBusqueda", numeroCheque);

        modelo.addAttribute("lista", lista);

        return "cheque_listar.html";
        

    }

}
