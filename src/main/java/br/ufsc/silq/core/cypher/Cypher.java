package br.ufsc.silq.core.cypher;

public class Cypher {


	/**
	 * Cria uma senha cifrada.
	 * 
	 * @param senhaAberta
	 *            a senha aberta
	 * @return uma senha cifrada a partir da senha aberta
	 */
	public static String createPassword(String senhaAberta) {
		return BCrypt.hashpw(senhaAberta, BCrypt.gensalt());
	}

	/**
	 * Método para verificar se a senha inserida pelo usuário bate com a senha
	 * armazenada no banco de dados.
	 * 
	 * @param senhaCandidata
	 *            a senha aberta inserida pelo usuário.
	 * @param senhaCifrada
	 *            a senha cifrada a ser checada.
	 * @return true se a senha candidata bate com a original, senão false.
	 */
	public static boolean checkPassword(String senhaCandidata, String senhaCifrada) {
		if (senhaCandidata == null) {
			return false;
		}
		if (senhaCifrada == null) {
			return false;
		}
		return BCrypt.checkpw(senhaCandidata, senhaCifrada);
	}

	
}
