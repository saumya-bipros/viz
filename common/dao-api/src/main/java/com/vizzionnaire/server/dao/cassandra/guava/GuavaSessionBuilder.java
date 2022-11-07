package com.vizzionnaire.server.dao.cassandra.guava;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.session.ProgrammaticArguments;
import com.datastax.oss.driver.api.core.session.SessionBuilder;
import edu.umd.cs.findbugs.annotations.NonNull;

public class GuavaSessionBuilder extends SessionBuilder<GuavaSessionBuilder, GuavaSession> {

    @Override
    protected DriverContext buildContext(
            DriverConfigLoader configLoader,
            ProgrammaticArguments programmaticArguments) {
        return new GuavaDriverContext(
                configLoader,
                programmaticArguments.getTypeCodecs(),
                programmaticArguments.getNodeStateListener(),
                programmaticArguments.getSchemaChangeListener(),
                programmaticArguments.getRequestTracker(),
                programmaticArguments.getLocalDatacenters(),
                programmaticArguments.getNodeFilters(),
                programmaticArguments.getClassLoader());
    }

    @Override
    protected GuavaSession wrap(@NonNull CqlSession defaultSession) {
        return new DefaultGuavaSession(defaultSession);
    }
}
