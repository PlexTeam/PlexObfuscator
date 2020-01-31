package info.plexteam.obfuscator;

import info.plexteam.obfuscator.config.Config;
import info.plexteam.obfuscator.gui.GUI;
import info.plexteam.obfuscator.transformer.Transformer;
import info.plexteam.obfuscator.transformer.Transformers;
import info.plexteam.obfuscator.tree.Hierarchy;

import javax.swing.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.*;
import java.util.regex.Pattern;

/**
 * @author jakuubkoo
 * @author cookiedragon234
 * @since 01/01/2020
 */
public class PObf {

    public static final Logger logger;

    private static PObf instance = new PObf();

    public static PObf getInstance(){
        return instance;
    }

    static {
        logger = Logger.getLogger("PObf");

        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {

            private final DateFormat df = new SimpleDateFormat("hh:mm:ss.SSS");

            @Override
            public String format(LogRecord record) {
                StringBuilder builder = new StringBuilder();
                builder.append('[');
                builder.append(df.format(new Date(record.getMillis())));
                builder.append(']').append(' ');
                builder.append('[');
                builder.append(record.getLevel().getLocalizedName());
                builder.append(']').append(' ');
                builder.append(formatMessage(record));
                builder.append("\n");
                return builder.toString();
            }
        });

        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public Config config;
    public ClassPath classPath;
    public Hierarchy hierarchy;

    private Random random;
    static List<String> MANIFEST;

    public void run(Config config)
    {
        this.config = config;
        random = new Random();
        classPath = new ClassPath(this, random);
        hierarchy = new Hierarchy(this);

        //Error if obfuscate file is empty
        if (config.inputFile.isEmpty()) {
            JOptionPane.showMessageDialog(new GUI(),
                    "I think you should select some file to obfuscate..",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Error if destination to save obfuscated file is empty
        if (config.outputFile.isEmpty()) {
            JOptionPane.showMessageDialog(new GUI(),
                    "I think you should select some directory to save obfuscated jar..",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        File inputFile = new File(config.inputFile);
        File outputFile = new File(config.outputFile);

        try {
            Instant loadStart = Instant.now();
            classPath.loadJar(inputFile);
            PObf.logger.info("Loaded Jar " + inputFile.getAbsolutePath() + " in " + Duration.between(loadStart, Instant.now()).toMillis() + " ms");

            Instant classPathStart = Instant.now();

            // Load all user defined dependencies (Currently disabled)
//            for(JsonElement dependency : config.getJsonObject().get("dependencies").getAsJsonArray()) {
//                classPath.loadJar(new File(dependency.getAsString()), true);
//            }

            // Load rt.jar
            try {
                String[] files = System.getProperty("sun.boot.class.path").split(Pattern.quote(";"));
                for(String fileName : files)
                {
                    File file = new File(fileName);
                    if(!file.isDirectory() && (file.getName().endsWith("rt.jar") || file.getName().endsWith("classes.jar")))
                        classPath.loadJar(file, true);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }


            PObf.logger.info("Loaded dependencies in " + Duration.between(classPathStart, Instant.now()).toMillis() + " ms");

            Instant hierarchyStart = Instant.now();
            classPath.classes.values().forEach(classNode -> hierarchy.buildHierarchy(classNode, null));
            PObf.logger.info("Generated hierarchy in " + Duration.between(hierarchyStart, Instant.now()).toMillis() + " ms");

            // Now we have all the classes in the jar

            List<Transformer> transformers = Transformers.getTransformers(this, random);

            Instant transformStart = Instant.now();
            // Transform all the classes
            {
                // Start with highest priority
                transformers
                        .stream()
                        .filter(transformer -> transformer.getPriority() == Transformer.Priority.HIGHEST)
                        .forEach(transformer -> transformer.transform(classPath.classes));

                transformers
                        .stream()
                        .filter(transformer -> transformer.getPriority() == Transformer.Priority.HIGH)
                        .forEach(transformer -> transformer.transform(classPath.classes));

                transformers
                        .stream()
                        .filter(transformer -> transformer.getPriority() == Transformer.Priority.NORMAL)
                        .forEach(transformer -> transformer.transform(classPath.classes));

                transformers
                        .stream()
                        .filter(transformer -> transformer.getPriority() == Transformer.Priority.LOW)
                        .forEach(transformer -> transformer.transform(classPath.classes));

                transformers
                        .stream()
                        .filter(transformer -> transformer.getPriority() == Transformer.Priority.LOWEST)
                        .forEach(transformer -> transformer.transform(classPath.classes));
            }
            PObf.logger.info("Transformed " + classPath.classes.size() + " classes in " + Duration.between(transformStart, Instant.now()).toMillis() + " ms");

            // And now write to new output file
            Instant saveStart = Instant.now();
            classPath.saveJar(outputFile);
            PObf.logger.info("Finished writing to " + outputFile.getAbsolutePath() + " in " + Duration.between(saveStart, Instant.now()).toMillis() + " ms");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Nullable
     * @return The main class of the currently loaded jar file
     */
    public String getMainClass() {
        if(MANIFEST != null) {
            for(String s : MANIFEST) {
                if(s.startsWith("Main-Class: ")) {
                    return s.substring("Main-Class: ".length());
                }
            }
        }
        return null;
    }

}
