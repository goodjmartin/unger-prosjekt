package no.goodtech.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class Main {

	private final int port;
	private final String contextPath;
	private final String workPath;
	private final String[] virtualHosts;

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.start();
	}

	public Main() {
		GenericApplicationContext ctx = new GenericApplicationContext();
		MutablePropertySources sources = ctx.getEnvironment().getPropertySources();
		sources.addFirst(new PropertiesPropertySource("cwdProperties", getPropertiesInCurrentDirectory()));
		sources.addAfter("cwdProperties", new PropertiesPropertySource("classpathProperties", getPropertiesInClassPath()));
		PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(sources);

		//Setter port, contextPath og workpath ut i fra properties fil
		port = Integer.parseInt(propertyResolver.getProperty("jetty.port", "8080"));
		contextPath = propertyResolver.getProperty("jetty.contextPath", "/");
		workPath = propertyResolver.getProperty("jetty.workDir", "workFolder");
		
		String virtualHostProperty = propertyResolver.getProperty("jetty.virtualHosts", "");
		if ("".equals(virtualHostProperty))
			virtualHosts = null;
		else
			virtualHosts = virtualHostProperty.split(",");

		resetTempDirectory();
	}
	
	private Properties getPropertiesInCurrentDirectory() {
		String currentDirectoryPath = getCurrentDirectoryPath();

		String jettyPropertiesFileName = "jetty.properties";
		final String localJettyPropertiesFilePath = currentDirectoryPath + File.separator + jettyPropertiesFileName;
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(localJettyPropertiesFilePath));
			System.out.println("Laster " + localJettyPropertiesFilePath);
		} catch (IOException e) {
			//Trenger vi denne?			System.out.println("Fant ikke " + localJettyPropertiesFilePath);
		}
		return properties;
	}

	private String getCurrentDirectoryPath() {
		String javaVersion = System.getProperty("java.version");
		String currentDirectoryPath;
		String os = OsUtils.getOsName();
		
		if (OsUtils.isWindows()) {
			currentDirectoryPath = getJarFolder();
			System.setProperty("user.dir", getJarFolder());
		} else {
			ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
			currentDirectoryPath = new File(protectionDomain.getCodeSource().getLocation().getPath()).getParent();
		}
		System.out.println(String.format("Java-versjon er %s, OS er %s, current directory er %s ", javaVersion, os, currentDirectoryPath));
		return currentDirectoryPath;
	}

	private Properties getPropertiesInClassPath() {
		Properties properties = new Properties();
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		final Resource resource = resourcePatternResolver.getResource("WEB-INF/classes/jetty.properties");
		if (resource.exists()) {
			try {
				properties.load(resource.getInputStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			System.out.println("Fant jetty.properties i classpath");
		} else {
			//Trenger vi denne? System.out.println("Fant ikke jetty.properties i classpath");
		}
		return properties;
	}
	
	private void start() {
		try {
			StdErrLog stdErrLog = new StdErrLog();
			stdErrLog.setDebugEnabled(false);
			Log.setLog(stdErrLog);

			Server server = new Server(port);
			server.setStopAtShutdown(true);
			server.addLifeCycleListener(createLifeCycleConsoleLogger());

			//Venter 5 sekunder før serveren shuttes ned ved setStopAtShutdown(true)			/*
			/*
			The "grace" period is the time the container will wait for requests currently inside the container to finish processing before shutting down.
            As soon as the shutdown command is given, the container will close the connectors so that they do not accept any more inbound connections.
            This will inform most load balancers that the server is no longer part of the cluster. The contexts are closed so that they do not accept any more requests,
            but the requests currently inside the container will drain out and the Server instance will shutdown after the grace period expires.
            You must also call the setStopAtShutdown(boolean) method with a value of true for the grace period to take effect.
             */
			server.setStopTimeout(5000);

			// Henter war-filen
			ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
			String warFileDirectory = protectionDomain.getCodeSource().getLocation().toExternalForm();
			WebAppContext context = new WebAppContext(warFileDirectory, contextPath);
			File workDir = new File(workPath);
			context.setTempDirectory(workDir);
			context.setServer(server);

			if (virtualHosts != null)
				context.setVirtualHosts(virtualHosts);

			HandlerList handlers = new HandlerList();
			handlers.addHandler(context);
			server.setHandler(handlers);
			server.start();
			server.join();
			
		} catch (Exception e) {
			System.err.println("Exception under oppstart: " + e.getMessage());
		}
	}

	private Listener createLifeCycleConsoleLogger() {
		return new LifeCycle.Listener() {
			@Override
			public void lifeCycleStarting(LifeCycle event) {
				System.out.print("Jetty starting..........");
			}

			@Override
			public void lifeCycleStarted(LifeCycle event) {
				System.out.println("started");
			}

			@Override
			public void lifeCycleFailure(LifeCycle event, Throwable cause) {
				System.err.println("Jetty failure: " + cause);
			}

			@Override
			public void lifeCycleStopping(LifeCycle event) {
				System.out.print("Jetty stopping...........");
			}

			@Override
			public void lifeCycleStopped(LifeCycle event) {
				System.out.println("Jetty stopped.");
			}
		};
	}

	private String getJarFolder() {
		// get name and path
		String name = getClass().getName().replace('.', '/');
		name = getClass().getResource("/" + name + ".class").toString();
		// remove junk
		int indexOfJar = name.indexOf(".jar");
		if (indexOfJar == -1) 
			throw new RuntimeException("Fant ikke current directory, antakelig fordi main-klassen ikke er pakket inn i en jar-fil");
		name = name.substring(0, indexOfJar);
		name = name.substring(name.lastIndexOf(':') - 1, name.lastIndexOf('/') + 1).replace('%', ' ');
		// remove escape characters
		String s = "";
		for (int k = 0; k < name.length(); k++) {
			s += name.charAt(k);
			if (name.charAt(k) == ' ') k += 2;
		}
		// replace '/' with system separator char
		return s.replace('/', File.separatorChar);
	}


	/**
	 * Sletter unna resterende kataloger og filer i valgt workPath
	 *
	 * Ved shutdown på windows maskiner blir ikke  "\webapp\WEB-INF\lib\" under valgt
	 * workPath fjernet, denne metoden sletter resterende filer når Main metoden kjøres
	 *
	 * JVM buggen: http://bugs.sun.com/view_bug.do?bug_id=4950148
	 * Diskusjon av problemet: http://jira.codehaus.org/browse/JETTY-848
	 *
	 *
	 */
	private void resetTempDirectory() {
		File workDir = new File(workPath);
		if (workDir.exists()) {
			System.out.println("Deleting remaining files and folders in: "+workPath);
			deleteDirectory(workDir);
			workDir.delete();
			System.out.println(workPath + " deleted");
		}
	}

	// deleteDirectory is a convenience method to recursively delete a directory
	private static boolean deleteDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return directory.delete();
	}

	public static final class OsUtils {
		private static String OS = null;

		public static String getOsName() {
			if (OS == null)
				OS = System.getProperty("os.name");
			return OS;
		}

		public static boolean isWindows() {
			return getOsName().startsWith("Windows");
		}
	}
}
