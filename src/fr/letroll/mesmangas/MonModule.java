package fr.letroll.mesmangas;

import com.google.inject.AbstractModule;

public class MonModule extends AbstractModule {

    protected void configure() {
        /*
         * This tells Guice that whenever it sees a dependency on a
         * TransactionLog, it should satisfy the dependency using a
         * DatabaseTransactionLog.
         */
//	    bind(Ajout1.class).to(Ajout1.class);
    }
}
