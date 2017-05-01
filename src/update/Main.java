package update;

import java.awt.*;
import java.io.*;


/**
 * @author Andrey Semenyuk on 2017
 */
public class Main {

    private final String root = "update/";

    public static void main(String args[]) {
        EventQueue.invokeLater(Main::new);
    }

    private Main() {
        try {
            copyFiles(new File(root), new File("").getAbsolutePath());
            cleanup();
            launch();
        } catch (Exception ex) {
            System.out.println("error");
        }
    }

    private void launch() {

        String[] run = {"java", "-jar", "tickets.jar"};

        try {
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

    private void cleanup() {

        remove(new File(root));
        new File(root).delete();
    }

    private void remove(File f) {

        File[] files = f.listFiles();

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
