package org.kleczek.maven.toolprovider.plugin;

import java.util.spi.ToolProvider;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which executes a ToolProovider.
 */
@Mojo(name = "exec")
public class ExecMojo extends AbstractMojo
{

    @Parameter(required = true)
    private String name;

    @Parameter
    private String[] args;

    @Parameter
    private boolean ignoreToolError = false;

    public void execute() throws MojoExecutionException
    {
        ToolProvider toolProvider = ToolProvider.findFirst(name)
                .orElseThrow(() -> new MojoExecutionException("ToolProvider " + name + " not found."));
        final int result = toolProvider.run(System.out, System.err, args);
        if (result != 0 && !ignoreToolError)
        {
            throw new MojoExecutionException("ToolProvider exited with code: " + result);
        }
    }
}
