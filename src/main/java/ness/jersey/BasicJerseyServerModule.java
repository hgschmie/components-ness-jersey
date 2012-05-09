package ness.jersey;

import ness.jersey.exceptions.NessJerseyExceptionMapperModule;
import ness.jersey.filter.BodySizeLimitResourceFilterFactory;
import ness.jersey.json.NessJacksonJsonProvider;

import com.google.inject.AbstractModule;
import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.selftest.SelftestModule;
import com.nesscomputing.jdbi.argument.ArgumentFactoryModule;
import com.nesscomputing.jdbi.metrics.DatabaseMetricsModule;
import com.nesscomputing.jmx.starter.guice.JmxStarterModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.yammer.metrics.guice.InstrumentationModule;

public class BasicJerseyServerModule extends AbstractModule {
    private final Config config;
    private final String[] paths;

    public BasicJerseyServerModule(Config config) {
        this(config, "/*");
    }

    public BasicJerseyServerModule(Config config, String... paths) {
        this.config = config;
        this.paths = paths;
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        binder().disableCircularProxies();

        install(new JerseyServletModule());
        install(new NessJerseyServletModule(config, paths));
        install(new JmxStarterModule(config));
        install(new InstrumentationModule());
        install(new DatabaseMetricsModule());
        install(new ArgumentFactoryModule());
        install (new NessJerseyExceptionMapperModule());

        install(new SelftestModule());

        NessJerseyServletModule.addResourceFilterFactoryBinding(binder()).to(BodySizeLimitResourceFilterFactory.class);

        bind (NessJacksonJsonProvider.class);
    }
}
