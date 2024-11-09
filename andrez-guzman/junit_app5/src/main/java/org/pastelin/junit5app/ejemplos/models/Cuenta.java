package org.pastelin.junit5app.ejemplos.models;

import org.pastelin.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

public class Cuenta {

    private String persona;

    private BigDecimal saldo;

    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.saldo = saldo;
        this.persona = persona;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void debito(BigDecimal monto) {
        BigDecimal nuevoSaldo = saldo.subtract(monto);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero Insuficiente");
        }

        saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto) {
        saldo = saldo.add(monto);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cuenta) {
            Cuenta c = (Cuenta) obj;
            return persona != null && saldo != null &&
                    persona.equals(c.getPersona()) && saldo.equals(c.getSaldo());
        }
        return false;
    }
}
