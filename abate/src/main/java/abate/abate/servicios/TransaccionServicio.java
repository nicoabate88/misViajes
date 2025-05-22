package abate.abate.servicios;

import abate.abate.entidades.Caja;
import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Entrega;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Ingreso;
import abate.abate.entidades.Recibo;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.ReciboRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.repositorios.IngresoRepositorio;
import abate.abate.util.TransaccionComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@Service
public class TransaccionServicio {

    @Autowired
    private ReciboRepositorio reciboRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private IngresoRepositorio ingresoRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private CajaServicio cajaServicio;
    @Autowired
    private GastoRepositorio gastoRepositorio;
    @Autowired
    private ChoferServicio choferServicio;

    @Transactional
    public void crearTransaccionRecibo(Long idRecibo) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setCliente(recibo.getCliente());
        transaccion.setFecha(recibo.getFecha());
        transaccion.setConcepto("RECIBO");
        transaccion.setObservacion("RECIBO ID" + recibo.getIdRecibo());
        transaccion.setImporte(recibo.getImporte() * -1);
        transaccion.setRecibo(recibo);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaCliente(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionRecibo(Long idRecibo) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdRecibo(idRecibo);

        if (!recibo.getCliente().getNombre().equalsIgnoreCase(transaccion.getCliente().getNombre())) {    //si lo que se modifico en la transacion es cliente, entra en este if

            cuentaServicio.eliminarTransaccionCuentaCliente(transaccion); //elimina transaccion en cuenta cliente modificado

            crearTransaccionRecibo(idRecibo);   //agrega transaccion en cuenta cliente modificado

        } else {

            transaccion.setFecha(recibo.getFecha());
            transaccion.setImporte(recibo.getImporte() * -1);

            transaccionRepositorio.save(transaccion);

            cuentaServicio.modificarTransaccionCuentaCliente(transaccion);

        }
    }

    @Transactional
    public void eliminarTransaccionRecibo(Long idRecibo) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdRecibo(idRecibo);

        cuentaServicio.eliminarTransaccionCuentaCliente(transaccion);

    }

    @Transactional
    public void crearTransaccionEntrega(Entrega entrega) {

        Transaccion transaccion = new Transaccion();

        transaccion.setChofer(entrega.getChofer());
        transaccion.setFecha(entrega.getFecha());
        transaccion.setConcepto("ENTREGA");
        transaccion.setObservacion("ENTREGA ID" + entrega.getIdEntrega());
        transaccion.setImporte(entrega.getImporte() * -1);
        transaccion.setEntrega(entrega);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaChofer(transaccion);

    }

    @Transactional
    public void modificarTransaccionEntrega(Entrega entrega) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdEntrega(entrega.getId());

        if (entrega.getChofer().getId() != transaccion.getChofer().getId()) {    //si lo que se modifico en la transacion es chofer, entra en este if

            cuentaServicio.eliminarTransaccionCuentaChofer(transaccion); //elimina transaccion en cuenta cliente modificado

            crearTransaccionEntrega(entrega);   //agrega transaccion en cuenta cliente modificado

        } else {

            transaccion.setFecha(entrega.getFecha());
            transaccion.setImporte(entrega.getImporte() * -1);

            transaccionRepositorio.save(transaccion);

            cuentaServicio.modificarTransaccionCuentaChofer(transaccion);

        }
    }

    @Transactional
    public void eliminarTransaccionEntrega(Long idEntrega) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdEntrega(idEntrega);

        cuentaServicio.eliminarTransaccionCuentaChofer(transaccion);

    }

    @Transactional
    public void crearTransaccionIngreso(Long idIngreso) {

        Ingreso ingreso = new Ingreso();
        Optional<Ingreso> ing = ingresoRepositorio.findById(idIngreso);
        if (ing.isPresent()) {
            ingreso = ing.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setChofer(ingreso.getChofer());
        transaccion.setFecha(ingreso.getFecha());
        transaccion.setConcepto("INGRESO");
        transaccion.setObservacion("INGRESO ID" + ingreso.getIdIngreso());
        transaccion.setImporte(ingreso.getImporte());
        transaccion.setIngreso(ingreso);

        transaccionRepositorio.save(transaccion);

        cajaServicio.agregarTransaccionCajaChofer(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionIngreso(Long idIngreso) {

        Ingreso ingreso = ingresoRepositorio.getById(idIngreso);
        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdIngreso(idIngreso);

        boolean flag = false;
        if (ingreso.getImporte() != transaccion.getImporte()) {
            flag = true;
        }

        transaccion.setFecha(ingreso.getFecha());
        transaccion.setImporte(ingreso.getImporte());

        transaccionRepositorio.save(transaccion);

        if (flag == true) {
            cajaServicio.modificarTransaccionCajaChofer(transaccion);
        }
    }

    @Transactional
    public void eliminarTransaccionIngreso(Long idIngreso) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdIngreso(idIngreso);

        cajaServicio.eliminarTransaccionCajaChofer(transaccion);

    }

    @Transactional
    public void crearTransaccionFleteChofer(Long idFlete) {

        Flete flete = new Flete();
        Optional<Flete> fle = fleteRepositorio.findById(idFlete);
        if (fle.isPresent()) {
            flete = fle.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setChofer(flete.getChofer());
        transaccion.setFecha(flete.getFechaFlete());
        transaccion.setConcepto("FLETE");
        transaccion.setObservacion("VIAJE ID" + flete.getIdFlete());
        transaccion.setImporte(flete.getPorcentajeChofer());
        transaccion.setFlete(flete);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaChofer(transaccion);

    }

    @Transactional
    public void crearTransaccionFleteCliente(Long idFlete) {

        Flete flete = new Flete();
        Optional<Flete> fle = fleteRepositorio.findById(idFlete);
        if (fle.isPresent()) {
            flete = fle.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setCliente(flete.getCliente());
        transaccion.setFecha(flete.getFechaFlete());
        transaccion.setConcepto("FLETE");
        transaccion.setObservacion("VIAJE ID" + flete.getIdFlete());
        transaccion.setImporte(flete.getTotal());
        transaccion.setFlete(flete);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaCliente(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionFlete(Flete flete) {


        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionesIdFlete(flete.getId());

        Transaccion tChofer = new Transaccion();
        Transaccion tCliente = new Transaccion();

        for (Transaccion t : lista) {
            if (t.getChofer() != null) {
                tChofer = t;                  //se obtiene la transaccin de Chofer
            }
            if (t.getCliente() != null) {
                tCliente = t;                //se obtiene la transaccion de Cliente
            }
        }

        if (flete.getChofer() != tChofer.getChofer()) {   //si lo que se modifico en la transacion es chofer, entra en este if

            cuentaServicio.eliminarTransaccionCuentaChofer(tChofer); //elimina transaccion en cuenta chofer modificado

            crearTransaccionFleteChofer(flete.getId());   //agrega transaccion en cuenta Chofer modificado

            Transaccion tGasto = new Transaccion();

            if (flete.getGasto() != null) {

                Long idGasto = flete.getGasto().getId();
                tGasto = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);

            }

            if (tGasto.getGasto() != null && tGasto.getChofer().getCaja().equalsIgnoreCase("SI")) {

                crearTransaccionGasto(tGasto.getGasto().getId());
                cajaServicio.eliminarTransaccionCajaChofer(tGasto);
            }

            if (tGasto.getGasto() != null && tGasto.getChofer().getCaja().equalsIgnoreCase("NO")) {

                crearTransaccionGasto(tGasto.getGasto().getId());
                cuentaServicio.eliminarTransaccionCuentaChofer(tGasto);
            }
            
        } else if (flete.getCliente().getId() != tCliente.getCliente().getId()) {    //si lo que se modifico en la transacion es cliente, entra en este if
            
            cuentaServicio.eliminarTransaccionCuentaCliente(tCliente); //elimina transaccion en cuenta cliente modificado

            crearTransaccionFleteCliente(flete.getId());   //agrega transaccion en cuenta Chofer modificado

        } else {

            tChofer.setFecha(flete.getFechaFlete());
            tChofer.setImporte(flete.getPorcentajeChofer());

            transaccionRepositorio.save(tChofer);    //se modifica la transaccion de Chofer
            cuentaServicio.modificarTransaccionCuentaChofer(tChofer);

            tCliente.setFecha(flete.getFechaFlete());
            tCliente.setImporte(flete.getTotal());

            transaccionRepositorio.save(tCliente);   //se modifica la transaccion de Cliente
            cuentaServicio.modificarTransaccionCuentaCliente(tCliente);

        }

    }

    @Transactional
    public void eliminarTransaccionFlete(Long idFlete) {

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionesIdFlete(idFlete);
        Transaccion tChofer = new Transaccion();
        Transaccion tCliente = new Transaccion();
        for (Transaccion t : lista) {
            if (t.getChofer() != null) {
                tChofer = t;                  //se obtiene la transaccin de Chofer
            }
            if (t.getCliente() != null) {
                tCliente = t;                //se obtiene la transaccion de Cliente
            }
        }

        cuentaServicio.eliminarTransaccionCuentaChofer(tChofer);
        cuentaServicio.eliminarTransaccionCuentaCliente(tCliente);
    }

    @Transactional
    public void crearTransaccionGasto(Long idGasto) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        Usuario chofer = choferServicio.buscarChofer(gasto.getChofer().getId());

        Transaccion transaccion = new Transaccion();
        transaccion.setChofer(gasto.getChofer());
        transaccion.setFecha(gasto.getFecha());
        transaccion.setConcepto("GASTO");
        transaccion.setObservacion(gasto.getNombre());

        if (chofer.getCaja().equalsIgnoreCase("NO")) {
            transaccion.setImporte(gasto.getImporte());
            transaccion.setGasto(gasto);

            transaccionRepositorio.save(transaccion);

            cuentaServicio.agregarTransaccionCuentaChofer(transaccion);

        } else {
            transaccion.setImporte(gasto.getImporte() * -1);
            transaccion.setGasto(gasto);

            transaccionRepositorio.save(transaccion);

            cajaServicio.agregarTransaccionCajaChofer(buscarUltimo());
        }
    }

    @Transactional
    public void modificarTransaccionGasto(Long idGasto) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        Usuario chofer = choferServicio.buscarChofer(gasto.getChofer().getId());

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);

        if (chofer.getCaja().equalsIgnoreCase("NO")) {

            transaccion.setImporte(gasto.getImporte());
            transaccion.setFecha(gasto.getFecha());

            transaccionRepositorio.save(transaccion);

            cuentaServicio.modificarTransaccionCuentaChofer(transaccion);

        } else {

            transaccion.setImporte(gasto.getImporte() * -1);
            transaccion.setFecha(gasto.getFecha());

            transaccionRepositorio.save(transaccion);

            cajaServicio.modificarTransaccionCajaChofer(transaccion);

        }
    }

    @Transactional
    public void eliminarTransaccionGasto(Long idGasto) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        Usuario chofer = choferServicio.buscarChofer(gasto.getChofer().getId());

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);

        if (chofer.getCaja().equalsIgnoreCase("NO")) {

            cuentaServicio.eliminarTransaccionCuentaChofer(transaccion);

        } else {

            cajaServicio.eliminarTransaccionCajaChofer(transaccion);
        }

    }

    public Long buscarUltimo() {

        return transaccionRepositorio.ultimoTransaccion();
    }

    public Transaccion buscarTransaccion(Long id) {

        return transaccionRepositorio.getById(id);
    }

    public ArrayList<Transaccion> buscarTransaccionIdCuenta(Long idCuenta) {

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionCuenta(idCuenta);

        Collections.sort(lista, TransaccionComparador.ordenarFechaAcs);

        Double saldoAcumulado = 0.0;

        for (Transaccion t : lista) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado + t.getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            t.setSaldoAcumulado(saldoAcumulado);
        }

        Collections.reverse(lista);

        return lista;
    }

    public ArrayList<Transaccion> buscarTransaccionIdCuentaFecha(Long idCuenta, String desde, String hasta) throws ParseException {

        java.sql.Date fd = (java.sql.Date) convertirStringASqlDate(desde);
        java.sql.Date fh = (java.sql.Date) convertirStringASqlDate(hasta);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        Double saldoAcumulado = cuenta.getSaldo();

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionCuentaPorRangoFechas(idCuenta, fd, fh);

        if (!lista.isEmpty()) {
            Collections.sort(lista, TransaccionComparador.ordenarFechaDesc);
            lista.get(0).setSaldoAcumulado(saldoAcumulado);
        }

        for (int i = 1; i < lista.size(); i++) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado - lista.get(i - 1).getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            lista.get(i).setSaldoAcumulado(saldoAcumulado);
        }

        return lista;
    }

    public ArrayList<Transaccion> buscarTransaccionIdCuentaFechaFiltro(Long idCuenta, String desde, String hasta) throws ParseException {

        java.sql.Date fd = (java.sql.Date) convertirStringASqlDate(desde);
        java.sql.Date fh = (java.sql.Date) convertirStringASqlDate(hasta);

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionCuentaPorRangoFechas(idCuenta, fd, fh);

        Collections.sort(lista, TransaccionComparador.ordenarFechaAcs);

        Double saldoAcumulado = 0.0;

        for (Transaccion t : lista) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado + t.getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            t.setSaldoAcumulado(saldoAcumulado);
        }

        Collections.reverse(lista);

        return lista;
    }

    public ArrayList<Transaccion> buscarTransaccionIdCajaFecha(Long idCaja, String desde, String hasta) throws ParseException {

        java.sql.Date fd = (java.sql.Date) convertirStringASqlDate(desde);
        java.sql.Date fh = (java.sql.Date) convertirStringASqlDate(hasta);

        Caja caja = cajaServicio.buscarCaja(idCaja);
        Double saldoAcumulado = caja.getSaldo();

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionCajaPorRangoFechas(idCaja, fd, fh);

        if (!lista.isEmpty()) {
            Collections.sort(lista, TransaccionComparador.ordenarFechaDesc);
            lista.get(0).setSaldoAcumulado(saldoAcumulado);
        }

        for (int i = 1; i < lista.size(); i++) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado - lista.get(i - 1).getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            lista.get(i).setSaldoAcumulado(saldoAcumulado);
        }

        return lista;
    }

    public ArrayList<Transaccion> buscarTransaccionIdCajaFechaFiltro(Long idCaja, String desde, String hasta) throws ParseException {

        java.sql.Date fd = (java.sql.Date) convertirStringASqlDate(desde);
        java.sql.Date fh = (java.sql.Date) convertirStringASqlDate(hasta);

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionCajaPorRangoFechas(idCaja, fd, fh);

        Collections.sort(lista, TransaccionComparador.ordenarFechaAcs);

        Double saldoAcumulado = 0.0;

        for (Transaccion t : lista) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado + t.getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            t.setSaldoAcumulado(saldoAcumulado);
        }

        Collections.reverse(lista);

        return lista;
    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

    public Date convertirStringASqlDate(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = formato.parse(fecha);
        return new java.sql.Date(utilDate.getTime()); // Conversión a java.sql.Date
    }

    public ArrayList<Transaccion> buscarTransaccionIdCajaFecha(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
