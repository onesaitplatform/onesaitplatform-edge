package com.onesait.edge.engine.modbus.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesait.edge.engine.modbus.conf.GlobalConstants;
import com.onesait.edge.engine.modbus.model.ModbusEnvironment;

public class FileUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	private static final Integer NUMDETAILSRXTXLIBPATH = Integer.valueOf(3);
	private static final String LIBRARYNAME = "libraryName";
	private static final String EXTENSION = "extension";
	private static final String LIBRARYPATH = "libraryPath";

	private FileUtils() {
	}

	public static URL getResource(String file2load) {
		return FileUtils.class.getClassLoader().getResource(file2load);
	}

	public static final ModbusEnvironment getCleanModbusEnvironment(String schemaJsonUrl, @NotNull String process,
			@Min(1) int maxThreadPoolSize) {

		File file = new File(schemaJsonUrl);
		ModbusEnvironment modbusEnvironment = new ModbusEnvironment();

		try {
			file.getParentFile().mkdirs();
			Boolean isCreated = file.createNewFile();
			LOGGER.info("New file Created with result {}", isCreated);
			modbusEnvironment.setProcess(process);
			modbusEnvironment.setCoreThreadPoolSize(maxThreadPoolSize);
			FileUtils.saveModbusEnvironment(modbusEnvironment, file.getAbsolutePath());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return modbusEnvironment;
	}

	/**
	 * Read a modbus environment file
	 * 
	 * @return the content of the file as string
	 * @throws IOException
	 */
	public static final String readModbusEnvironment(String environmentPathFile) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(environmentPathFile));
		return new String(encoded, StandardCharsets.UTF_8);
	}

	/**
	 * Save ModbusEnvironment into disk as a modbus environment json file
	 * 
	 * @param ModbusEnvironment
	 *            object to save as json
	 * @throws IOException
	 */
	public static final synchronized void saveModbusEnvironment(ModbusEnvironment modbusEnvironment,
			String environmentPathFile) throws IOException {

		modbusEnvironment.setUpdate(new Date(System.currentTimeMillis()).toString());

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY).writerWithDefaultPrettyPrinter().writeValue(new File(environmentPathFile), modbusEnvironment);
	}

	public static final Boolean loadRxtxLib() {

		String osname = System.getProperty("os.name");
		
		Boolean existRxtxLib = Boolean.TRUE;
		URL url = null;

		Map<String, String> rxtxPathDetails = getRxtxLibraryPath();
		
		if(!isValidRxtxLibraryPath(rxtxPathDetails)) {
			existRxtxLib = Boolean.FALSE;
		}else if (!osname.toLowerCase().startsWith("linux")) {
			
			LOGGER.info("Rxtx is not preloaded. Trying to load {}", rxtxPathDetails.get(LIBRARYNAME) + rxtxPathDetails.get(EXTENSION));

			url = FileUtils.getResource(rxtxPathDetails.get(LIBRARYPATH) + rxtxPathDetails.get(LIBRARYNAME) + rxtxPathDetails.get(EXTENSION));
			if (url != null) {
				LOGGER.info("URL:{}", url.getPath());

				File fileOut = new File(System.getProperty("java.io.tmpdir") + GlobalConstants.Delimiters.SLASH + rxtxPathDetails.get(LIBRARYNAME) + rxtxPathDetails.get(EXTENSION));
				LOGGER.info("Writing dll to: {}", fileOut.getAbsolutePath());

				try (InputStream in = url.openStream();	OutputStream out = org.apache.commons.io.FileUtils.openOutputStream(fileOut);) {
					IOUtils.copy(in, out);					
					// we need to close before using library
					in.close();
					out.close();

					FileUtils.addLibraryPath(System.getProperty("java.io.tmpdir") + GlobalConstants.Delimiters.SLASH);
					System.loadLibrary(rxtxPathDetails.get(LIBRARYNAME));

				} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					LOGGER.error(e.getMessage());
					existRxtxLib = Boolean.FALSE;
				}
			}
		}

		return existRxtxLib;
	}
	
	private static final Boolean isValidRxtxLibraryPath(Map<String, String> rxtxPathDetails) {
		
		Boolean resultValidation = Boolean.FALSE;
		
		if(rxtxPathDetails != null && rxtxPathDetails.size() == NUMDETAILSRXTXLIBPATH.intValue() 
				&& rxtxPathDetails.get(LIBRARYNAME) != null && rxtxPathDetails.get(EXTENSION) != null 
				&& rxtxPathDetails.get(LIBRARYPATH) != null) {
			resultValidation = Boolean.TRUE;
		}
		
		return resultValidation;
	}
	
	private static final Map<String, String> getRxtxLibraryPath() {
		
		Map<String, String> rxtxPathDetails = new HashMap<>();
		
		String processor = System.getProperty("os.arch");
		String osname = System.getProperty("os.name");
		String library = null;
		String extension = null;

		LOGGER.info("Searching rxtx for:{} - {}", osname, processor);

		StringBuilder libPath = new StringBuilder();

		if (osname.toLowerCase().startsWith("win")) {

			library = "rxtxSerial";
			extension = ".dll";

			if ("amd64".equalsIgnoreCase(processor) || "x86-64".equalsIgnoreCase(processor)) {
				libPath.append("x64_win/");
			} else if ("x86".equalsIgnoreCase(processor)) {
				libPath.append("x86_win/");
			} else {
				LOGGER.warn("Lib rxtxSerial not found for :{} - {}", processor, osname);
			}

		} else if (osname.toLowerCase().startsWith("linux")) {

			library = "librxtxSerial";
			extension = ".so";

			if ("amd64".equalsIgnoreCase(processor) || "x86-64".equalsIgnoreCase(processor)) {
				libPath.append("x64_linux/");
			} else if ("x86".equalsIgnoreCase(processor) || "i386".equalsIgnoreCase(processor)) {
				libPath.append("x86_linux/");
			} else {
				LOGGER.warn("Lib librxtxSerial not found for :{} - {}", processor, osname);
			}

		} else {
			LOGGER.warn("Lib not found for :{} - {}", processor, osname);
		}
		
		rxtxPathDetails.put(LIBRARYNAME, library);
		rxtxPathDetails.put(EXTENSION, extension);
		rxtxPathDetails.put(LIBRARYPATH, libPath.toString());
		
		return rxtxPathDetails;
	}

	@SuppressWarnings("unchecked")
	public static final Boolean isRxtxLoaded(@NotNull String rxtxLibName)
			throws IllegalAccessException, NoSuchFieldException {

		Boolean isLoaded = Boolean.FALSE;
		Field loadedLibraryNames;

		loadedLibraryNames = ClassLoader.class.getDeclaredField("loadedLibraryNames");
		loadedLibraryNames.setAccessible(true);

		// check if the path to add is already present
		for (String path : (Vector<String>) loadedLibraryNames.get(null)) {
			LOGGER.info("Loaded Native Library:{}", path);
			if (rxtxLibName.equals(path)) {
				LOGGER.info("{} loaded!:", path);
				isLoaded = Boolean.TRUE;
				break;
			}
		}

		return isLoaded;
	}

	/**
	 * Adds the specified path to the java library path
	 *
	 * @param pathToAdd
	 *            the path to add
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static void addLibraryPath(String pathToAdd)	throws NoSuchFieldException, IllegalAccessException {
		final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		// get array of paths
		final String[] paths = (String[]) usrPathsField.get(null);

		// check if the path to add is already present
		for (String path : paths) {
			if (path.equals(pathToAdd)) {
				LOGGER.info("Path {} found!", path);
				return;
			}
		}

		// add the new path
		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = pathToAdd;
		LOGGER.info("valor de newPath:{}", pathToAdd);
		usrPathsField.set(null, newPaths);
	}
}
