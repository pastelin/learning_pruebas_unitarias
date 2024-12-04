package com.pastelin.test.springboot.app;

import com.pastelin.test.springboot.app.exceptions.DineroInsuficienteException;
import com.pastelin.test.springboot.app.models.Banco;
import com.pastelin.test.springboot.app.models.Cuenta;
import com.pastelin.test.springboot.app.repositories.BancoRepository;
import com.pastelin.test.springboot.app.repositories.CuentaRepository;
import com.pastelin.test.springboot.app.services.CuentaService;
import com.pastelin.test.springboot.app.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringbootTestApplicationTests {

    @MockitoBean
    CuentaRepository cuentaRepository;

    @MockitoBean
    BancoRepository bancoRepository;

    @Autowired
    CuentaService service;

    @BeforeEach
    void setUp() {
//        cuentaRepository = mock(CuentaRepository.class);
//        bancoRepository = mock(BancoRepository.class);
//        service = new CuentaServiceImpl(cuentaRepository, bancoRepository);

//        Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
//        Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
//        Datos.BANCO.setTotalTransferencias(0);
    }

    @Test
    void contextLoads() {
        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta0001());
        when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta0002());
        when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

        BigDecimal saldoOrigen = service.revisarSaldo(1L);
        BigDecimal saldoDestino = service.revisarSaldo(2L);
        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        service.transferir(1L, 2L, new BigDecimal("100"), 1L);

        saldoOrigen = service.revisarSaldo(1L);
        saldoDestino = service.revisarSaldo(2L);

        assertEquals("900", saldoOrigen.toPlainString());
        assertEquals("2100", saldoDestino.toPlainString());

        int total = service.revisarTotalTransferencias(1L);
        assertEquals(1, total);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(3)).findById(2L);
        verify(cuentaRepository, times(2)).save(any(Cuenta.class));

        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository).save(any(Banco.class));

        verify(cuentaRepository, times(6)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoads2() {
        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta0001());
        when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta0002());
        when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

        BigDecimal saldoOrigen = service.revisarSaldo(1L);
        BigDecimal saldoDestino = service.revisarSaldo(2L);
        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        assertThrows(DineroInsuficienteException.class, () -> {
            service.transferir(1L, 2L, new BigDecimal("1200"), 1L);
        });

        saldoOrigen = service.revisarSaldo(1L);
        saldoDestino = service.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        int total = service.revisarTotalTransferencias(1L);
        assertEquals(0, total);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(2)).findById(2L);
        verify(cuentaRepository, never()).save(any(Cuenta.class));

        verify(bancoRepository, times(1)).findById(1L);
        verify(bancoRepository, never()).save(any(Banco.class));

        verify(cuentaRepository, times(5)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoads3() {
        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta0001());

        Cuenta cuenta1 = service.findById(1L);
        Cuenta cuenta2 = service.findById(1L);

        assertSame(cuenta1, cuenta2);
        assertTrue(cuenta1 == cuenta2);
        assertEquals("Andres", cuenta1.getPersona());
        assertEquals("Andres", cuenta2.getPersona());

        verify(cuentaRepository, times(2)).findById(1L);
    }

    @Test
    void testFindAll() {

        List<Cuenta> datos = Arrays.asList(Datos.crearCuenta0001().orElseThrow(), Datos.crearCuenta0002().orElseThrow());
        when(cuentaRepository.findAll()).thenReturn(datos);

        List<Cuenta> cuentas = service.findAll();

        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
        assertEquals("Andres", cuentas.get(0).getPersona());
        assertEquals("Jhon", cuentas.get(1).getPersona());

        verify(cuentaRepository).findAll();
    }

    @Test
    void testSave() {
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        when(cuentaRepository.save(any())).then(invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        Cuenta cuentaCreada = service.save(cuenta);

        assertEquals("Pepe", cuentaCreada.getPersona());
        assertEquals(3L, cuentaCreada.getId());
        assertEquals("3000", cuentaCreada.getSaldo().toPlainString());

        verify(cuentaRepository).save(any());
    }

}
