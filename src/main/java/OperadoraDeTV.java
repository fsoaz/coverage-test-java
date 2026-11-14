import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess", "ClassEscapesDefinedScope"})
public class OperadoraDeTV {
	public static void main(String[] args) {
		// Ponto de entrada para iniciar a aplicação pela IDE.
	}

	private static final double MAXIMO_DESCONTO_PERMITIDO_POR_ASSINANTE = 35.0;
	private static final int ALGUM_TEMPO_DE_ESPERA = 1000;

	enum PlanoDeCanais {
		PLANO_CARO,
		PLANO_SUPER_CARO,
		PLANO_HIPER_CARO,
		NOVO_PLANO_AINDA_SEM_PRECO
	}

	enum TipoDesconto {
		DESCONTINHO,
		DESCONTAO,
		NOVO_TIPO_DE_DESCONTO
	}

	private final HashMap<String, Pessoa> assinantes = new HashMap<>();

	public double consultaPrecoPadrao(PlanoDeCanais plano) {
		if(PlanoDeCanais.PLANO_CARO.equals(plano)) {
			return 120.0;
		} else if(PlanoDeCanais.PLANO_SUPER_CARO.equals(plano)) {
			return 220.0;
		} else if(PlanoDeCanais.PLANO_HIPER_CARO.equals(plano)) {
			return 320.0;
		} else {
			throw new IllegalArgumentException("Plano não encontrado");
		}
	}

	public void criarContrato(String nome, String cpf, Date nascimento, boolean isMasculino, PlanoDeCanais planoEscolhido) {
		assinantes.put(cpf, new Pessoa(nome, cpf, nascimento, isMasculino, planoEscolhido));
	}

	public void concederDesconto(String cpf, TipoDesconto tipoDesconto) {
		final Pessoa assinante = getAssinante(cpf);
		double totalDeDescontosDoAssinante = getTotalDeDescontosDoAssinante(assinante);
		if(totalDeDescontosDoAssinante>MAXIMO_DESCONTO_PERMITIDO_POR_ASSINANTE) {
			throw new IllegalArgumentException("Desconto excede máximo desconto permitido por assinante");
		}
		if(TipoDesconto.DESCONTINHO.equals(tipoDesconto)) {
			assinante.descontos.add(3.0);
		} else if(TipoDesconto.DESCONTAO.equals(tipoDesconto)) {
			assinante.descontos.add(30.0);
		} else {
			throw new IllegalArgumentException("Tipo de desconto inválido!");
		}
	}

	public double mensalidade(String cpf) {
		Pessoa assinante = getAssinante(cpf);
		double totalDesconto = getTotalDeDescontosDoAssinante(assinante);
		double mensalidadePadrao = consultaPrecoPadrao(assinante.plano);
		return mensalidadePadrao - (mensalidadePadrao * totalDesconto / 100);
	}

	public void cancelarAssinatura(String cpf) throws InterruptedException {
		Thread.sleep(ALGUM_TEMPO_DE_ESPERA);
		if(chanceAleatoriaDe50PorcentoAconteceu()) {
			Pessoa assinante = getAssinante(cpf);
			System.err.println(assinante.nome + " sua assinatura foi cancelada com sucesso!");
			assinantes.remove(cpf);
		} else {
			Thread.sleep(ALGUM_TEMPO_DE_ESPERA);
			throw new RuntimeException("Só mais um momento senhor(a) ...");
		}
	}

	public Pessoa getAssinante(String cpf) {
		final Pessoa assinante = assinantes.get(cpf);
		if(assinante == null) {
			throw new IllegalArgumentException("Assinante de cpf: " + cpf + " não encontrado.");	
		}
		return assinante;
	}

	private double getTotalDeDescontosDoAssinante(Pessoa assinante) {
		double totalDesconto = 0;
		for(Double desconto : assinante.descontos) {
			totalDesconto += desconto;
		}
		return totalDesconto;
	}

	private boolean chanceAleatoriaDe50PorcentoAconteceu() {
		Random random = new Random();
		random.nextInt(2);
		return random.nextInt(2) == 1;
	}

	static class Pessoa {
		String nome;
		String cpf;
		Date nascimento;
		boolean isMasculino;
		PlanoDeCanais plano;
		List<Double> descontos = new ArrayList<>();

		Pessoa(String nome, String cpf, Date nascimento, boolean isMasculino, PlanoDeCanais plano) {
			if(nome == null || nome.trim().isEmpty()) {
				throw new IllegalArgumentException("Nome inválido");
			}
			if(cpf == null || cpf.trim().isEmpty()) {
				throw new IllegalArgumentException("CPF inválido");
			}
			this.nome = nome;
			this.cpf = cpf;
			this.nascimento = nascimento;
			this.isMasculino = isMasculino;
			this.plano = plano;
		}
	}

}
