package webServer

import SecurityData.WEB_LOGIN
import SecurityData.WEB_PASSWORD
import io.ktor.server.auth.*
import io.ktor.server.application.*

fun Application.configureAuth() {
	val tag = "configureAuth"
    authentication {
    		basic(name = "auth-basic") {
    			realm = "Access to the '/' path"
    			validate { credentials ->
    				if (credentials.name == WEB_LOGIN && credentials.password == WEB_PASSWORD) {
    					UserIdPrincipal(credentials.name)
    				} else {
    					null
    				}
    			}
    		}
    	}
}
