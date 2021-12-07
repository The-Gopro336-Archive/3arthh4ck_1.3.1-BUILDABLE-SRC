package org.spongepowered.asm.mixin.transformer.ext.extensions;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;
import org.spongepowered.asm.util.Constants;
import org.spongepowered.asm.util.PrettyPrinter;
import org.spongepowered.asm.util.SignaturePrinter;

public class ExtensionCheckInterfaces
implements IExtension {
    private static final String AUDIT_DIR = "audit";
    private static final String IMPL_REPORT_FILENAME = "mixin_implementation_report";
    private static final String IMPL_REPORT_CSV_FILENAME = "mixin_implementation_report.csv";
    private static final String IMPL_REPORT_TXT_FILENAME = "mixin_implementation_report.txt";
    private static final Logger logger = LogManager.getLogger("mixin");
    private final File csv;
    private final File report;
    private final Multimap<ClassInfo, ClassInfo.Method> interfaceMethods = HashMultimap.create();
    private boolean strict;

    public ExtensionCheckInterfaces() {
        File debugOutputFolder = new File(Constants.DEBUG_OUTPUT_DIR, AUDIT_DIR);
        debugOutputFolder.mkdirs();
        this.csv = new File(debugOutputFolder, IMPL_REPORT_CSV_FILENAME);
        this.report = new File(debugOutputFolder, IMPL_REPORT_TXT_FILENAME);
        try {
            Files.write("Class,Method,Signature,Interface\n", this.csv, Charsets.ISO_8859_1);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Files.write("Mixin Implementation Report generated on " + dateTime + "\n", this.report, Charsets.ISO_8859_1);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public boolean checkActive(MixinEnvironment environment) {
        this.strict = environment.getOption(MixinEnvironment.Option.CHECK_IMPLEMENTS_STRICT);
        return environment.getOption(MixinEnvironment.Option.CHECK_IMPLEMENTS);
    }

    @Override
    public void preApply(ITargetClassContext context) {
        ClassInfo targetClassInfo = context.getClassInfo();
        for (ClassInfo.Method m : targetClassInfo.getInterfaceMethods(false)) {
            this.interfaceMethods.put(targetClassInfo, m);
        }
    }

    @Override
    public void postApply(ITargetClassContext context) {
        ClassInfo targetClassInfo = context.getClassInfo();
        if (targetClassInfo.isAbstract() && !this.strict) {
            logger.info("{} is skipping abstract target {}", new Object[]{this.getClass().getSimpleName(), context});
            return;
        }
        String className = targetClassInfo.getName().replace('/', '.');
        int missingMethodCount = 0;
        PrettyPrinter printer = new PrettyPrinter();
        printer.add("Class: %s", className).hr();
        printer.add("%-32s %-47s  %s", "Return Type", "Missing Method", "From Interface").hr();
        Set<ClassInfo.Method> interfaceMethods = targetClassInfo.getInterfaceMethods(true);
        HashSet<ClassInfo.Method> implementedMethods = new HashSet<ClassInfo.Method>(targetClassInfo.getSuperClass().getInterfaceMethods(true));
        implementedMethods.addAll(this.interfaceMethods.removeAll(targetClassInfo));
        for (ClassInfo.Method method : interfaceMethods) {
            ClassInfo.Method found = targetClassInfo.findMethodInHierarchy(method.getName(), method.getDesc(), ClassInfo.SearchType.ALL_CLASSES, ClassInfo.Traversal.ALL);
            if (found != null && !found.isAbstract() || implementedMethods.contains(method)) continue;
            if (missingMethodCount > 0) {
                printer.add();
            }
            SignaturePrinter signaturePrinter = new SignaturePrinter(method.getName(), method.getDesc()).setModifiers("");
            String iface = method.getOwner().getName().replace('/', '.');
            ++missingMethodCount;
            printer.add("%-32s%s", signaturePrinter.getReturnType(), signaturePrinter);
            printer.add("%-80s  %s", "", iface);
            this.appendToCSVReport(className, method, iface);
        }
        if (missingMethodCount > 0) {
            printer.hr().add("%82s%s: %d", "", "Total unimplemented", missingMethodCount);
            printer.print(System.err);
            this.appendToTextReport(printer);
        }
    }

    @Override
    public void export(MixinEnvironment env, String name, boolean force, byte[] bytes) {
    }

    private void appendToCSVReport(String className, ClassInfo.Method method, String iface) {
        try {
            Files.append(String.format("%s,%s,%s,%s\n", className, method.getName(), method.getDesc(), iface), this.csv, Charsets.ISO_8859_1);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void appendToTextReport(PrettyPrinter printer) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(this.report, true);
            PrintStream stream = new PrintStream(fos);
            stream.print("\n");
            printer.print(stream);
        }
        catch (Exception exception) {
            IOUtils.closeQuietly(fos);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(fos);
            throw throwable;
        }
        IOUtils.closeQuietly(fos);
    }
}
