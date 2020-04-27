package com.in28minutes.microservices.oauth.security;

import org.aspectj.weaver.patterns.IToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationConfigServer extends AuthorizationServerConfigurerAdapter {

    //Inyectamos los bean del AuthorizationConfigMAnager
    // el authentication Manager ya lleva inyectado el usuario


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // El authenticationManager lo tenemos que registrar en el AuthorizationServer
    //para ello tenemos que sobreescribir 3 métodos del AuthorizationServerConfigureAdapter


    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {


    //Este metodo y el siguiente es para configurar el acceso a los clientes
        //Configuramos los permisos de acceso al
        // POST /oauth/token
        // en principio le damos acceso a todos el mundo
        //cualquiera puede acceder a ese endpoint
        oauthServer.tokenKeyAccess("permitAll")
                // y configramos el chesc que dice que el token se tiene que validat
                //Las credenciales del cleinet se envian en la cebecera
                // nombre app client_id y codigo secreto en Base64 en
                //la cabecera Authorization
        // (El token se envia como Barear)
        .checkTokenAccess("isAuthenticated()");


    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

       // Este metodo es para configurar el acceso a los clientes
        // Recibimos las credenciales del cliente de la aplicacion front (no las del servidor,
       // para obtener las del servidor sólo usamos el username que conectara con Feign para obtener las ceredenciales internas

        clients.inMemory().withClient("frontapp")
        // encriptamos la contraseña
                .secret(passwordEncoder.encode("12345"))

                //Las dos antyeriores son las credenciales del cleinet
                //Lo siguiente es para el server a traves del User con el token

                .scopes("read","write")
                .authorizedGrantTypes("password","refresh_token")//El tipo de autorizacion es con contraseña
        //También pouede ser tipo code, que esa autorizacion temporal que nos genera un token de acceso
        // sin comprobar username

                // y la validez en segundos del token
                .accessTokenValiditySeconds(3600)
                //y del refreh token
            .refreshTokenValiditySeconds(3600);

        //Si tuvieramos otro cliente front
        /*.and()
        .withClient("angularapp")
        .secret(passwordEncoder.encode("7890"))
                .scopes("read","write")
                .authorizedGrantTypes("password","refresh_token")//El tipo de autorizacion es con contraseña
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(3600);*/

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // el endpoint es de tipo POST y siempre es /oauth/token

        //Por el endpoint recibe el username, pass y demás en formato User security
        //creamos el token jwt con el secreto
        // y lo almacenamos - token store


        //Aquí configuramos el AuthorizationMAnager
        //Se encarga de coger los datos del user (en formato autebticate) y meterlos en el token
        // Claims. También se pueden meter datos adicionales como el nombre,apellido, etc. Estos datos adicionales se cononcen como claims
        // En base64

        //en el endpoint registramos el autorizatonManager que recuperamos desde la inyección (ya llava los datos del User)
        endpoints.authenticationManager(authenticationManager)
              //añadimos token store que se encarga de guardar el token, y creamos el método tokenStore
                // publico para @Bean
                .tokenStore(tokenStore())

                //añadimos el metodo de conversion del token (jwt) y cramos el método para hacerlo, que devuelva JWtAccessTokenConverter -no AccessTokenConverter-
        // OJO   que se public -no private - para poder usar el @Bean
        .accessTokenConverter(accessTokenConverter());
        // No retorna nada porque se almacena
    }

    @Bean
    public JwtTokenStore tokenStore() {
        //Devolvemos un objeto JWtTokenStore en el que le pasamos al constructor el jwtAccessTokenConverter
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {

       //Crea el toeken tipo jwt y se le añade la clave secret
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        // Se añade la clave secreta
        jwtAccessTokenConverter.setSigningKey("codigo_secreto");
        return jwtAccessTokenConverter;
    }
}
