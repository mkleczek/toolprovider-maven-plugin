package org.kleczek.maven.toolprovider.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.spi.ToolProvider;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Goal which executes jar ToolProvider.
 */
@Mojo(name = "jar", defaultPhase = LifecyclePhase.PACKAGE)
public class JarMojo extends AbstractMojo
{

    private static final ToolProvider jarProvider = ToolProvider.findFirst("jar").get();

    @Parameter(name = "args")
    private String[] args;

    @Parameter(name = "argsFile")
    private String argsFile;

    @Parameter(name = "finalName", defaultValue = "${project.build.finalName}")
    private String finalName;

    @Parameter(name = "classifier")
    private String classifier;

    @Parameter(name = "classesDirectory", defaultValue = "${project.build.outputDirectory}")
    private String classesDirectory;

    @Parameter(name = "outputDirectory", defaultValue = "${project.build.directory}")
    private String outputDirectory;

    @Parameter
    private String mainClass;

    @Parameter(readonly = true, defaultValue = "${project}")
    MavenProject project;

    @Component
    MavenProjectHelper projectHelper;

    private boolean hasClassifier()
    {
        return classifier != null && !classifier.trim().isEmpty();
    }

    private String classifierSuffix()
    {
        return hasClassifier() ? "-" + classifier : "";
    }

    private File defaultArchiveFile()
    {
        return new File(new File(outputDirectory), finalName + classifierSuffix() + ".jar");
    }

    private boolean projectHasAlreadySetAnArtifact()
    {
        if (project.getArtifact().getFile() != null)
        {
            return project.getArtifact().getFile().isFile();
        }
        else
        {
            return false;
        }
    }

    String[] buildArgs(File artifactFile)
    {
        final List<String> execArgs = new ArrayList<>(6);

        if (getLog().isDebugEnabled())
        {
            execArgs.add("-v");
        }

        execArgs.add("--create");
        execArgs.add("--file");
        execArgs.add(artifactFile.getAbsolutePath());
        if (mainClass != null && !mainClass.trim().isEmpty())
        {
            execArgs.add("--main-class");
            execArgs.add(mainClass);
        }
        execArgs.add("-C");
        execArgs.add("/");
        execArgs.add(classesDirectory);

        return execArgs.toArray(new String[execArgs.size()]);
    }

    public void execute() throws MojoExecutionException
    {
        String[] execArgs = args;
        final File artifactFile = defaultArchiveFile();
        if (execArgs == null)
        {
            if (argsFile == null)
            {
                execArgs = buildArgs(artifactFile);
            }
            else
            {
                execArgs = new String[] { "@" + argsFile };
            }
        }
        if (getLog().isDebugEnabled())
        {
            getLog().debug("Executing [jar " + Arrays.stream(execArgs).collect(Collectors.joining(" ")) + "]");
        }
        final int result = jarProvider.run(System.out, System.err, execArgs);
        if (result != 0)
        {
            throw new MojoExecutionException("ToolProvider exited with code: " + result);
        }

        if (args == null && argsFile == null)
        {
            if (hasClassifier())
            {
                projectHelper.attachArtifact(project, "jar", classifier, artifactFile);
            }
            else if (projectHasAlreadySetAnArtifact())
            {
                throw new MojoExecutionException(
                        "Artifact has already been set on the project. Set classifier to create an attachment.");
            }
            else
            {
                project.getArtifact().setFile(artifactFile);
            }
        }
    }
}
