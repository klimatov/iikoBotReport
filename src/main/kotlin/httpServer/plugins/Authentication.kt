package httpServer.plugins

import SecurityData.WEB_LOGIN
import SecurityData.WEB_PASSWORD
import io.ktor.server.auth.*
import io.ktor.server.application.*

fun Application.configureAuth() {
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

/*    routing {
        authenticate("auth-basic") {
            get("/auth") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
    }*/
}
