package com.pastelin.test.springboot.app.repositories;

import com.pastelin.test.springboot.app.models.Cuenta;

import java.util.List;

public interface CuentaRepository {

    List<Cuenta> findAll();

    Cuenta findById(Long id);

    void update(Cuenta cuenta);
}
