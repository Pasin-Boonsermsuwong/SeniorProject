package org.slf4j;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;

public class Main {

    static LoggerContext lc = new LoggerContext();
    static public void main(String[] args) throws Exception {
        
        long start = System.nanoTime();
        Logger logger = withSLF4J();
        //Logger logger  = withoutSLF4J();
        logger.info("hey"); //, new IllegalStateException("as"));
        
        //loop();
        long end = System.nanoTime();
        long diff = (end-start)/1000/1000;
        System.out.println("elapsed time in milli-seconds "+diff);
    }

    private static void loop() {
        for(int i = 0; i < 10000; i++) {
            PatternLayout pl = new PatternLayout();
            pl.setContext(lc);
            pl.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
            pl.start();
        }
    }

    private static Logger withSLF4J() {
        System.out.println("with SLF4J");
        Logger logger = LoggerFactory.getLogger("a");
        return logger;
    }

    private static Logger withoutSLF4J() throws JoranException {
        System.out.println("without SLF4J");
        LoggerContext lc = new LoggerContext();
        new ContextInitializer(lc).autoConfig();
        Logger logger = new LoggerContext().getLogger("a");
        return logger;
    }
}
