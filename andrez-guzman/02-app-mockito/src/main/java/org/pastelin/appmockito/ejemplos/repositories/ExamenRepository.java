package org.pastelin.appmockito.ejemplos.repositories;

import org.pastelin.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();

    Examen guardar(Examen examen);
}
