package xyz.gofannon.warpatcher.patcher;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;

public class Patcher {
    private final File warInputFile;

    public Patcher(File warInputFile) {
        this.warInputFile = warInputFile;
    }

    public void patchTo(File warOutputFile) throws IOException {
        System.out.println("Patching " + warOutputFile.getAbsolutePath());
        try (
                OutputStream outputStream = new FileOutputStream(warOutputFile);
                JarInputStream warIn = new JarInputStream(new FileInputStream(warInputFile));
                JarOutputStream warOut = new JarOutputStream(outputStream, warIn.getManifest())
        ) {

            JarEntry inEntry;
            while ((inEntry = warIn.getNextJarEntry()) != null) {
                if (inEntry.isDirectory()) {
                    JarEntry outEntry = new JarEntry(inEntry);
                    warOut.putNextEntry(outEntry);
                    warOut.closeEntry();
                } else if (isSayHelloJar(inEntry)) {
                    System.out.println("SayHelloJar detected");
                    byte[] patchedContent = patchSayHelloJar(warIn);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    IOUtils.copy(warIn, baos);
//                    byte[] patchedContent = baos.toByteArray();

                    JarEntry outEntry = createPatchedJarEntry(inEntry, patchedContent);
//                    JarEntry outEntry = new JarEntry(inEntry.getName());
//                    outEntry.setSize(patchedContent.length);
//                    outEntry.setTime(inEntry.getTime());
//
//                    CRC32 crc32 = new CRC32();
//                    crc32.update(patchedContent);
//                    outEntry.setCrc(crc32.getValue());
//                    outEntry.setMethod(inEntry.getMethod());


                    warOut.putNextEntry(outEntry);
                    warOut.write(patchedContent);
                    warOut.closeEntry();

                } else {
                    JarEntry outEntry = new JarEntry(inEntry);
                    warOut.putNextEntry(outEntry);
                    IOUtils.copy(warIn, warOut);
                    warOut.closeEntry();
                }
            }
        }
    }

    private JarEntry createPatchedJarEntry(JarEntry inEntry, byte[] patchedContent) {
        JarEntry outEntry = new JarEntry(inEntry.getName());
        outEntry.setSize(patchedContent.length);
        outEntry.setTime(inEntry.getTime());
        outEntry.setMethod(inEntry.getMethod());

        CRC32 crc32 = new CRC32();
        crc32.update(patchedContent);
        outEntry.setCrc(crc32.getValue());

        return outEntry;
    }

    private boolean isSayHelloJar(JarEntry entry) {
        return entry.getName().endsWith(".jar") && entry.getName().contains("sayhello-");
    }

    private byte[] patchSayHelloJar(InputStream entryContentOut) throws IOException {
        System.out.println("Patching SayHelloJar...");

        ByteArrayOutputStream outJarContent = new ByteArrayOutputStream();
//        IOUtils.copy(entryContentOut, outJarContent);

        JarInputStream inJar = new JarInputStream(entryContentOut);
        try (
                JarOutputStream outJar = new JarOutputStream(outJarContent, inJar.getManifest());
        ) {
            JarEntry inEntry;
            while ((inEntry = inJar.getNextJarEntry()) != null) {
                if (inEntry.isDirectory()) {
                    JarEntry outEntry = new JarEntry(inEntry);
                    outJar.putNextEntry(outEntry);
                } else if (isHelloProperties(inEntry)) {
                    System.out.println("Hello Properties detected...");
                    ByteArrayOutputStream entryContent = new ByteArrayOutputStream();
                    byte[] patchedContent = patchHelloProperties(entryContent);
                    JarEntry outEntry = createPatchedJarEntry(inEntry, patchedContent);
                    outJar.putNextEntry(outEntry);
                    IOUtils.copy(new ByteArrayInputStream(patchedContent), outJar);
                } else {
                    JarEntry outEntry = new JarEntry(inEntry);
                    outJar.putNextEntry(outEntry);
                    IOUtils.copy(inJar, outJar);
                }
            }
            outJar.closeEntry();
        }

        return outJarContent.toByteArray();
    }

    private boolean isHelloProperties(JarEntry inEntry) {
        return inEntry.getName().equals("hello.properties");
    }


    private byte[] patchHelloProperties(ByteArrayOutputStream entryContentOut) throws IOException {
        Properties props = new Properties();
        props.load(new StringReader(entryContentOut.toString()));
        props.setProperty("helloMessage", "Hello John !");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        props.store(out, "");

        System.out.println("Hello Properties patched: ");
        String content = out.toString(StandardCharsets.ISO_8859_1);
        System.out.println(content);
        System.out.println("------------------------");

        return out.toByteArray();
    }
}
