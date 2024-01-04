/**
 * 
 */
package com.oradnata.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.oradnata.util.PublicUtility;

/**
 * 
 */
public class LocalJNDITest {

	private static String dbURL = "jdbc:oracle:thin:@fiscstmp_low";
	private static String userName = "FIUSER";
	private static String dbCred = "tnr27Bb5tUVIw2IoFVpVDQ==";
	private static String jndiName = "jdbc/FISCSTMP_DS";
	private static PublicUtility utility = new PublicUtility();

	public static void main(String[] args) {
		System.out.println("Doing a lookup of;" + jndiName);
		weblogic.jndi.Environment environment = new weblogic.jndi.Environment();
		environment.setInitialContextFactory(weblogic.jndi.Environment.DEFAULT_INITIAL_CONTEXT_FACTORY);
		//environment.setProviderURL("t3://localhost:7001/console");
		//environment.setSecurityPrincipal("weblogic");
		//environment.setSecurityCredentials(utility.DecryptText(dbCred));
		//environment.setSecurityCredentials("Welcome@123");
		try {
			InitialContext ctx = (InitialContext) environment.getInitialContext();
			ctx.lookup(jndiName);
			System.out.println("Passed.");
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
