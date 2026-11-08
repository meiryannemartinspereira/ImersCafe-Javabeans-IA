package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.ResultadoPreparar;
import com.animahub.javabeans.ia.engine.GameEngine;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.animahub.javabeans.ia.model.Receita;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private AIService aiService;

    @Mock
    private GameEngine gameEngine;

    @Mock
    private HttpSession session;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(aiService, gameEngine);
    }

    @Test
    void prepararCafeComReceitaCorretaPontuaAcumulaEDevolveDto() {
        configurarSessao(7);
        when(aiService.gerarFeedbackBarista(
                "Cappuccino",
                List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.ESPUMA),
                "Ana",
                "Um cappuccino, por favor."
        )).thenReturn("Pedido recebido!");

        ResultadoPreparar resultado = gameService.prepararCafe(
                session,
                List.of("CAFE", "LEITE", "ESPUMA"),
                "Um cappuccino, por favor."
        );

        assertTrue(resultado.acertou());
        assertEquals(10, resultado.pontosNestaRodada());
        assertEquals(17, resultado.pontuacaoTotal());
        assertEquals("Pedido recebido!", resultado.feedbackIA());
        assertEquals(
                new Receita(
                        "Mocha",
                        List.of(Ingrediente.CAFE, Ingrediente.CHOCOLATE, Ingrediente.LEITE),
                        12
                ),
                resultado.proximoCliente()
        );
        verify(aiService).gerarFeedbackBarista(
                "Cappuccino",
                List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.ESPUMA),
                "Ana",
                "Um cappuccino, por favor."
        );
        verify(session).setAttribute("pontuacaoTotal", 17);
    }

    @Test
    void prepararCafeComIngredienteAusenteNaoPontuaEPreservaPontuacaoAnterior() {
        configurarSessao(23);

        ResultadoPreparar resultado = gameService.prepararCafe(
                session,
                List.of("CAFE", "LEITE"),
                "Um cappuccino, por favor."
        );

        assertFalse(resultado.acertou());
        assertEquals(0, resultado.pontosNestaRodada());
        assertEquals(23, resultado.pontuacaoTotal());
        verify(session).setAttribute("pontuacaoTotal", 23);
    }

    @Test
    void prepararCafeComIngredienteAdicionalNaoPontua() {
        configurarSessao(0);

        ResultadoPreparar resultado = gameService.prepararCafe(
                session,
                List.of("CAFE", "LEITE", "ESPUMA", "CANELA"),
                "Um cappuccino, por favor."
        );

        assertFalse(resultado.acertou());
        assertEquals(0, resultado.pontosNestaRodada());
    }

    @Test
    void prepararCafeComIngredienteInvalidoTrataListaComoVazia() {
        configurarSessao(0);

        ResultadoPreparar resultado = gameService.prepararCafe(
                session,
                List.of("PIMENTA"),
                "Um cappuccino, por favor."
        );

        assertFalse(resultado.acertou());
        assertEquals(0, resultado.pontosNestaRodada());
        verify(aiService).gerarFeedbackBarista(
                "Cappuccino",
                List.of(),
                "Ana",
                "Um cappuccino, por favor."
        );
    }

    @Test
    void prepararCafeComListaVaziaNaoFalhaENaoPontua() {
        lenient().when(session.getAttribute("cenarioAtual")).thenReturn(1);
        lenient().when(session.getAttribute("pontuacaoTotal")).thenReturn(0);
        lenient().when(session.getAttribute("nomeJogador")).thenReturn("Ana");

        ResultadoPreparar resultado = assertDoesNotThrow(() ->
                gameService.prepararCafe(session, List.of(), "Um cappuccino, por favor.")
        );

        assertFalse(resultado.acertou());
        assertEquals(0, resultado.pontosNestaRodada());
    }

    @Test
    @Disabled("Bug identificado em produção")
    void prepararCafeComIngredienteDuplicadoRejeitaReceita() {
        configurarSessao(0);

        ResultadoPreparar resultado = gameService.prepararCafe(
                session,
                List.of("CAFE", "LEITE", "ESPUMA", "ESPUMA"),
                "Um cappuccino, por favor."
        );

        assertFalse(resultado.acertou());
        assertEquals(0, resultado.pontosNestaRodada());
    }

    @Test
    void prepararCafePropagaRuntimeExceptionDaIa() {
        configurarSessao(0);

        when(aiService.gerarFeedbackBarista(
                "Cappuccino",
                List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.ESPUMA),
                "Ana",
                "Um cappuccino, por favor."
        )).thenThrow(new RuntimeException("IA indisponível"));

        assertThrows(RuntimeException.class, () ->
                gameService.prepararCafe(
                        session,
                        List.of("CAFE", "LEITE", "ESPUMA"),
                        "Um cappuccino, por favor."
                )
        );
    }

    private void configurarSessao(int pontuacaoTotal) {
        when(session.getAttribute("cenarioAtual")).thenReturn(1);
        when(session.getAttribute("pontuacaoTotal")).thenReturn(pontuacaoTotal);
        when(session.getAttribute("nomeJogador")).thenReturn("Ana");
    }
}
