import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OperadoraDeTVTest {

    private static final String CPF = "12345678900";
    private OperadoraDeTV operadora;

    @BeforeEach
    void setUp() {
        operadora = new OperadoraDeTV();
        criarContrato("João", CPF, OperadoraDeTV.PlanoDeCanais.PLANO_CARO);
    }

    @Test
    void deveIniciarPeloMetodoMain() {
        assertDoesNotThrow(() -> OperadoraDeTV.main(new String[0]));
    }

    @Test
    void deveRetornarPrecoDeTodosOsPlanos() {
        assertEquals(120.0, operadora.consultaPrecoPadrao(OperadoraDeTV.PlanoDeCanais.PLANO_CARO));
        assertEquals(220.0, operadora.consultaPrecoPadrao(OperadoraDeTV.PlanoDeCanais.PLANO_SUPER_CARO));
        assertEquals(320.0, operadora.consultaPrecoPadrao(OperadoraDeTV.PlanoDeCanais.PLANO_HIPER_CARO));
    }

    @Test
    void deveLancarExcecaoParaPlanoSemPreco() {
        assertThrows(IllegalArgumentException.class,
            () -> operadora.consultaPrecoPadrao(OperadoraDeTV.PlanoDeCanais.NOVO_PLANO_AINDA_SEM_PRECO));
    }

    @Test
    void deveCriarContratoEConsultarAssinante() {
        OperadoraDeTV.Pessoa assinante = operadora.getAssinante(CPF);

        assertEquals("João", assinante.nome);
        assertEquals(CPF, assinante.cpf);
        assertEquals(OperadoraDeTV.PlanoDeCanais.PLANO_CARO, assinante.plano);
    }

    @Test
    void deveRejeitarNomeNuloOuEmBranco() {
        assertThrows(IllegalArgumentException.class,
            () -> criarContrato(null, "111", OperadoraDeTV.PlanoDeCanais.PLANO_CARO));
        assertThrows(IllegalArgumentException.class,
            () -> criarContrato("   ", "111", OperadoraDeTV.PlanoDeCanais.PLANO_CARO));
    }

    @Test
    void deveRejeitarCpfNuloOuEmBranco() {
        assertThrows(IllegalArgumentException.class,
            () -> criarContrato("João", null, OperadoraDeTV.PlanoDeCanais.PLANO_CARO));
        assertThrows(IllegalArgumentException.class,
            () -> criarContrato("João", "   ", OperadoraDeTV.PlanoDeCanais.PLANO_CARO));
    }

    @Test
    void deveCalcularMensalidadeComESemDesconto() {
        assertEquals(120.0, operadora.mensalidade(CPF), 0.001);

        operadora.concederDesconto(CPF, OperadoraDeTV.TipoDesconto.DESCONTINHO);
        assertEquals(116.4, operadora.mensalidade(CPF), 0.001);
    }

    @Test
    void deveCalcularMensalidadeComDescontoMaior() {
        operadora.concederDesconto(CPF, OperadoraDeTV.TipoDesconto.DESCONTAO);

        assertEquals(84.0, operadora.mensalidade(CPF), 0.001);
    }

    @Test
    void deveRejeitarTipoDeDescontoInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> operadora.concederDesconto(CPF, OperadoraDeTV.TipoDesconto.NOVO_TIPO_DE_DESCONTO));
    }

    @Test
    void deveRejeitarDescontoQuandoTotalJaExcedeLimite() {
        operadora.concederDesconto(CPF, OperadoraDeTV.TipoDesconto.DESCONTAO);
        operadora.concederDesconto(CPF, OperadoraDeTV.TipoDesconto.DESCONTINHO);
        operadora.concederDesconto(CPF, OperadoraDeTV.TipoDesconto.DESCONTINHO);

        // O código atual permite chegar a 36%; a concessão seguinte percorre o ramo de limite excedido.
        assertEquals(76.8, operadora.mensalidade(CPF), 0.001);
        assertThrows(IllegalArgumentException.class,
            () -> operadora.concederDesconto(CPF, OperadoraDeTV.TipoDesconto.DESCONTINHO));
    }

    @Test
    void deveLancarExcecaoParaAssinanteInexistente() {
        assertThrows(IllegalArgumentException.class, () -> operadora.getAssinante("99999999999"));
        assertThrows(IllegalArgumentException.class,
            () -> operadora.concederDesconto("99999999999", OperadoraDeTV.TipoDesconto.DESCONTINHO));
        assertThrows(IllegalArgumentException.class, () -> operadora.mensalidade("99999999999"));
    }

    @Test
    void devePercorrerOsDoisResultadosAleatoriosDoCancelamento() throws InterruptedException {
        boolean cancelamentoConcluido = false;
        boolean cancelamentoAdiado = false;

        // Cada tentativa usa um contrato novo porque um cancelamento concluído remove o assinante.
        for (int tentativa = 0; tentativa < 20 && !(cancelamentoConcluido && cancelamentoAdiado); tentativa++) {
            String cpfTentativa = "cpf-cancelamento-" + tentativa;
            criarContrato("Assinante", cpfTentativa, OperadoraDeTV.PlanoDeCanais.PLANO_CARO);

            try {
                operadora.cancelarAssinatura(cpfTentativa);
                cancelamentoConcluido = true;
                assertThrows(IllegalArgumentException.class, () -> operadora.getAssinante(cpfTentativa));
            } catch (RuntimeException adiamento) {
                assertEquals("Só mais um momento senhor(a) ...", adiamento.getMessage());
                cancelamentoAdiado = true;
                assertDoesNotThrow(() -> operadora.getAssinante(cpfTentativa));
            }
        }

        assertTrue(cancelamentoConcluido, "O teste não observou um cancelamento concluído");
        assertTrue(cancelamentoAdiado, "O teste não observou um cancelamento adiado");
    }

    private void criarContrato(String nome, String cpf, OperadoraDeTV.PlanoDeCanais plano) {
        operadora.criarContrato(nome, cpf, new Date(), true, plano);
    }
}
