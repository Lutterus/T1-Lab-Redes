package Server;

public class GameState {
	private int quantidadeDePerguntasDeUmaSessao;
	private int quantidadeDeJogadoresMinimos;
	Users users;

	public GameState(int quantidadeDePerguntasDeUmaSessao, int quantidadeDeJogadoresMinimos, Users users) {
		this.setQuantidadeDeJogadoresMinimos(quantidadeDeJogadoresMinimos);
		this.setQuantidadeDePerguntasDeUmaSessao(quantidadeDePerguntasDeUmaSessao);
		this.users = users;
	}

	public int getQuantidadeDePerguntasDeUmaSessao() {
		return quantidadeDePerguntasDeUmaSessao;
	}

	public void setQuantidadeDePerguntasDeUmaSessao(int quantidadeDePerguntasDeUmaSessao) {
		this.quantidadeDePerguntasDeUmaSessao = quantidadeDePerguntasDeUmaSessao;
	}

	public int getQuantidadeDeJogadoresMinimos() {
		return quantidadeDeJogadoresMinimos;
	}

	public void setQuantidadeDeJogadoresMinimos(int quantidadeDeJogadoresMinimos) {
		this.quantidadeDeJogadoresMinimos = quantidadeDeJogadoresMinimos;
	}

	public boolean canGameStart() {
		// Se possui jogadores suficientes
		if (users.getSize() >= quantidadeDeJogadoresMinimos) {
			// Jogo pode começar
			return true;

		}
		// Jogo nao pode começar
		return false;
	}

	public boolean canGameFinish() {
		return users.haveAllPlayersResponded() == quantidadeDePerguntasDeUmaSessao;
	}
}
