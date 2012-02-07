package fr.letroll.mesmangas;

import roboguice.config.AbstractAndroidModule;

public class MonModule extends AbstractAndroidModule {

	@Override
	protected void configure() {
		 /*
	      * This tells Guice that whenever it sees a dependency on a TransactionLog,
	      * it should satisfy the dependency using a DatabaseTransactionLog.
	      */
	    bind(TransactionLog.class).to(DatabaseTransactionLog.class);
	}

}
