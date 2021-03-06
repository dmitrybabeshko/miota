package org.fox;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

import org.fox.domain.GraphBuilder;
import org.fox.domain.Properties;
import org.fox.domain.SearchInfoHolder;
import org.fox.presentation.ConsoleUI;
import org.fox.presentation.ResultFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Miota {

    public static void main(String[] args) throws IOException {

        ApplicationContext context = null;

        try {
            context = new AnnotationConfigApplicationContext(SpringConfig.class);

            ConsoleUI consoleUI = context.getBean(ConsoleUI.class);
            GraphBuilder graphBuilder = context.getBean(GraphBuilder.class);
            ResultFileWriter resultFileWriter = context.getBean(ResultFileWriter.class);
            Properties properties = context.getBean(Properties.class);
            SearchInfoHolder searchInfoHolder = new SearchInfoHolder(properties);

            long startTime = System.currentTimeMillis();
            consoleUI.startUI(searchInfoHolder, startTime);
            graphBuilder.buildTree(searchInfoHolder);

            consoleUI.stopUI(searchInfoHolder, startTime);
            resultFileWriter.writeResult(searchInfoHolder, startTime);

        } catch (Throwable e) {
            if (context != null) {
                ScheduledExecutorService uiExecutor = context.getBean(ScheduledExecutorService.class);
                uiExecutor.shutdown();
            }

            System.out.println(e);
            System.out.println(Arrays.toString(e.getStackTrace()));
        } finally {
            if (context != null) {
                ForkJoinPool forkJoinPool = context.getBean(ForkJoinPool.class);
                forkJoinPool.shutdown();
            }
        }

        System.in.read();
    }
}
