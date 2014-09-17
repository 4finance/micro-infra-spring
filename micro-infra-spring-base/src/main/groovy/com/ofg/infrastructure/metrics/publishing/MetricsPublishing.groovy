package com.ofg.infrastructure.metrics.publishing

/**
 * Interface for metrics publishing. 
 * 
 * You can publish metrics to different places including those predfined:
 * <ul>
 *     <li>Console - ConsoleReporter (com.codahale.metrics)</li>
 *     <li>Slf4j - Slf4jReporter (com.codahale.metrics)</li>
 *     <li>Csv - CsvReporter (com.codahale.metrics)</li>
 *     <li>Graphite - GraphiteReporter (com.codahale.metrics.graphite)</li>
 *     <li>Jmx - JmxReporter (com.codahale.metrics)</li>
 * </ul>
 * 
 * as well as your own custom ones.
 */
interface MetricsPublishing {

    /**
     * Starts metrics publishing
     */
    void start()

    /**
     * Stops metrics publishing
     */
    void stop()

}