package updater;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;


/**
 * @author Andrey Semenyuk on 2017
 */
public class Main {

    // Временная папка с новой версией
    private final String root = "update/";
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String args[]) {
        EventQueue.invokeLater(Main::new);
    }

    private Main() {
        try {
            initLogger();
            logger.info("Updater started. Start copy files");
            copyFiles(new File(root), new File("").getAbsolutePath());

            cleanup();
            launch();
        } catch (Exception | AssertionError ex) {
            logger.log(Level.SEVERE, "Failed to update: ", ex);
        }
    }

    private void initLogger() throws IOException {

        File logDir = new File("logs");

        if (!logDir.exists()) logDir.mkdir();

        FileHandler logFile = new FileHandler("logs/updater.log", true);
        logFile.setFormatter(new Formatter() {
            public String format(LogRecord record) {
                return String.format("%s [%s] %s.%s  - %s\r\n",
                        SimpleDateFormat.getInstance().format(new Date()),
                        record.getLevel(),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        record.getMessage());
            }
        });

        logger.addHandler(logFile);
    }

    private void launch() {

        logger.info("Launch updated application");

        String[] run = {"java", "-jar", "tickets.jar"};

        try {
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to launch updated application: ", ex);
        }

        logger.info("Updated close");

        System.exit(0);
    }

    private void cleanup() {

        logger.info("Cleanup temporary files");

        remove(new File(root));
        new File(root).delete();
    }

    private void remove(File f) {

        File[] files = f.listFiles();

        assert files != null;

        for (File ff : files) {
            if (ff.isDirectory()) {
                remove(ff);
                ff.delete();
            } else {
                ff.delete();
            }
        }
    }

    private void copyFiles(File f, String dir) throws IOException {

        File[] files = f.listFiles();

        if (files == null) throw new AssertionError();

        for (File ff : files) {
            if (ff.isDirectory()) {
                new File(dir + "/" + ff.getName()).mkdir();
                copyFiles(ff, dir + "/" + ff.getName());
            } else {
                copy(ff.getAbsolutePath(), dir + "/" + ff.getName());
            }

        }
    }

    private void copy(String srFile, String dtFile) throws IOException {

        File f1 = new File(srFile);
        File f2 = new File(dtFile);

        InputStream in = new FileInputStream(f1);
        OutputStream out = new FileOutputStream(f2);

        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }
}
