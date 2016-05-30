package br.ufsc.silq.test;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import br.ufsc.silq.Application;
import br.ufsc.silq.core.forms.usuario.RegisterForm;
import br.ufsc.silq.core.persistence.entities.Usuario;
import br.ufsc.silq.core.service.AuthService;
import br.ufsc.silq.core.service.UsuarioService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles(resolver = TestProfilesResolver.class)
@Transactional
@EnableCaching
public abstract class WebContextTest {

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private AuthService authService;

	/**
	 * Cria um novo usuário na base de dados e o loga no sistema, para testes.
	 *
	 * @return A entidade {@link Usuario} criada.
	 */
	protected Usuario loginUser() {
		return this.loginUser(new RegisterForm("Bruce Wayne", "j0k3r", "batman@batman.com"));
	}

	/**
	 * Cria um novo usuário na base de dados e o loga no sistema, para testes.
	 *
	 * @param registerForm Formulário de registro utilizado para criar o usuário que será logado.
	 * @return A entidade {@link Usuario} criada.
	 */
	protected Usuario loginUser(RegisterForm registerForm) {
		Usuario user = this.usuarioService.register(registerForm);
		return this.doLogin(user, registerForm.getSenha());
	}

	/**
	 * Efetua o login da entidade {@link Usuario}.
	 *
	 * @param user Usuário efetuando o login.
	 * @param senha Senha descodificada do usuário.
	 * @return
	 */
	protected Usuario doLogin(Usuario user, String senha) {
		this.authService.authenticateAndLogin(user.getEmail(), senha);
		return user;
	}
}