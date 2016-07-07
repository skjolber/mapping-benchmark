package org.simpleflatmapper;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.AbstractRowProcessor;
import com.univocity.parsers.csv.CsvParserSettings;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.utils.ParallelReader;
import org.simpleflatmapper.param.Csv;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@State(Scope.Benchmark)
public class UnivocityParallelCsvParserBenchmark {


    @Param(value = {"32"})
    public int parallelBufferSize;

    private ExecutorService executorService;

    @Setup
    public void setUp() {
        executorService = Executors.newFixedThreadPool(1);
    }

    @TearDown
    public void tearDown() {
        executorService.shutdown();
    }
    @Benchmark
    public void parseCsv(Blackhole blackhole) throws IOException {
        CsvParserSettings settings = new CsvParserSettings();

        //turning off features enabled by default
        settings.setIgnoreLeadingWhitespaces(false);
        settings.setIgnoreTrailingWhitespaces(false);
        settings.setSkipEmptyLines(false);
        settings.setColumnReorderingEnabled(false);
        settings.setReadInputOnSeparateThread(false);

        settings.setRowProcessor(new AbstractRowProcessor() {
            @Override
            public void rowProcessed(String[] row, ParsingContext context) {
                blackhole.consume(row);
            }
        });

        com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(settings);
        try(Reader reader = Csv.getParallelReader(executorService, parallelBufferSize)) {
            parser.parse(reader);
        }
    }

    @Benchmark
    public void parseCsvQuotes(Blackhole blackhole) throws IOException {
        CsvParserSettings settings = new CsvParserSettings();

        //turning off features enabled by default
        settings.setIgnoreLeadingWhitespaces(false);
        settings.setIgnoreTrailingWhitespaces(false);
        settings.setSkipEmptyLines(false);
        settings.setColumnReorderingEnabled(false);
        settings.setReadInputOnSeparateThread(false);

        settings.setRowProcessor(new AbstractRowProcessor() {
            @Override
            public void rowProcessed(String[] row, ParsingContext context) {
                blackhole.consume(row);
            }
        });

        com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(settings);
        try(Reader reader = Csv.getParallelReaderQuotes(executorService, parallelBufferSize)) {
            parser.parse(reader);
        }
    }
}