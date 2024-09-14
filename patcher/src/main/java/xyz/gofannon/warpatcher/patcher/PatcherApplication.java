package xyz.gofannon.warpatcher.patcher;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PatcherApplication {

    public static void main(String[] args) throws IOException {
        File inputFile = new File("C:\\Users\\gwena\\test\\lib\\helloweb-0.0.1-SNAPSHOT.war.org");
        File targetFile = new File("C:\\Users\\gwena\\test\\lib\\helloweb-0.0.1-SNAPSHOT.war");

        FileUtils.deleteQuietly(targetFile);

        new Patcher(inputFile).patchTo(targetFile);
    }
}
