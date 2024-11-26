package org.pastelin.appmockito.ejemplos.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.pastelin.appmockito.ejemplos.Datos;
import org.pastelin.appmockito.ejemplos.models.Examen;
import org.pastelin.appmockito.ejemplos.repositories.ExamenRepository;
import org.pastelin.appmockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.pastelin.appmockito.ejemplos.repositories.PreguntaRepository;
import org.pastelin.appmockito.ejemplos.repositories.PreguntaRepositoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {

    @Spy
    ExamenRepositoryImpl repository;

    @Spy
    PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    ExamenServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void testSpy() {
        List<String> preguntas = List.of("aritmética");

        // Esto ocasiona una llamada real en la parte del when
        // when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examene = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examene.getId());
        assertEquals("Matemáticas", examene.getNombre());
        assertEquals(1, examene.getPreguntas().size());
        assertTrue(examene.getPreguntas().contains("aritmética"));

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

}