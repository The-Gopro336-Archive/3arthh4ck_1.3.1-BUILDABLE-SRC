/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.main;

import java.util.Scanner;
import me.earth.earthhack.impl.modules.client.server.api.ICloseable;
import me.earth.earthhack.impl.modules.client.server.main.command.CommandException;
import me.earth.earthhack.impl.modules.client.server.main.command.CommandLineManager;
import me.earth.earthhack.impl.modules.client.server.main.command.handlers.ExitCommand;

public class BaseCommandLineHandler
extends CommandLineManager {
    private final ICloseable closeable;

    public BaseCommandLineHandler(ICloseable closeable) {
        this.closeable = closeable;
        this.add("exit", new ExitCommand(closeable));
        this.add("stop", new ExitCommand(closeable));
        this.add("bye", new ExitCommand(closeable));
    }

    public void startListening() {
        block16: {
            Thread thread = Thread.currentThread();
            Scanner scanner = new Scanner(System.in);
            Throwable throwable = null;
            block11: while (true) {
                try {
                    while (!thread.isInterrupted() && this.closeable.isOpen()) {
                        String input = scanner.nextLine();
                        try {
                            this.handle(input);
                            continue block11;
                        }
                        catch (CommandException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break block16;
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
            }
            finally {
                if (scanner != null) {
                    if (throwable != null) {
                        try {
                            scanner.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                    } else {
                        scanner.close();
                    }
                }
            }
        }
    }
}

