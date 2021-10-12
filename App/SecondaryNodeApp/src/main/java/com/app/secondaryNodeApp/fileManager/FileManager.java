
package com.app.secondaryNodeApp.fileManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 * Class for operation with input/output file or folder of received step.
 * @author Filip
 */
public class FileManager {
    
    /** STATIC PROPERY **/
    
    private static final String PARAM_REGEX = "\\$[0-9]+";  // regex for find all $x substrings where x is an integer
    
    /**
     * Type of file system node - file or folder.
     */
    public static enum FsNodeType {
        FILE, FOLDER
    }
    
    /** STATIC METHODS **/
    
    /**
     * Check if input path exists.
     * @param path Input path.
     * @return True if input path exists. False otherwise.
     */
    public static boolean isExistPath(String path) {
        File fsNode = new File(path);
        if (fsNode.exists())
            return true;
        else
            return false;
    }
    
    /**
     * Check if input path is folder.
     * @param path Path to file or folder.
     * @return True if path is folder. False otherwise.
     */
    public static boolean isFolder(String path) {
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory())
            return false;
        else
            return true;
    }    
    
    /**
     * Check if input path is a path to nonempty folder.
     * @param path  Path to folder.
     * @return True if path is nonempty folder. False otherwise.
     */
    public static boolean isNonEmptyFolder(String path) {
        if (!isFolder(path))
            return false;
        File folder = new File(path);
        return (folder.list().length != 0);
    }
    
    /**
     * Check if input path is a path to nonempty file.
     * @param path  Path to file.
     * @return True if path is nonempty file. False otherwise.
     */
    public static boolean isNonEmptyFile(String path) {
        if (isFolder(path))
            return false;
        File file = new File(path);
        return (file.length() != 0);
    }   
    
    /**
     * Get length of file.
     * @param path Path to file.
     * @return Length of file if file exists. Null otherwise.
     */
    public static Long getFsNodeLength(String path) {
        File file = new File(path);
        if (!file.exists())
            return null;
        else if (file.isDirectory())
            return FileUtils.sizeOfDirectory(file);
        else
            return file.length();
    }
    
    /**
     * Method to delete FS node (file or folder (recursively with all content)
     * @param path Path to file or folder to be deleted.
     * @return True if file or folder was deleted. False otherwise.
     */
    public static boolean deleteFsNode(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        else if (file.isDirectory()) {
            for (String name : file.list()) {
                if (!deleteFsNode(file.getPath()+"/"+name))
                    return false;
            }
        }
        return file.delete();
    }
    
    /**
     * Get all files or folders in specified folder.
     * @param path  Path to folder from which the files or folders should be read.
     * @param fileType Type of file system nodes, which should be read.
     * @return List of files or folders which are in input folder if folder exists. Null otherwise.
     */
    public static List<String> getAllFsNodes(String path, FsNodeType fileType) {
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory())
            return null;
        else {
            List<String> fsNodes = new ArrayList<>();
            for (String fsNode : folder.list()) {
                if (Files.isDirectory(Paths.get(path+"/"+fsNode)) && fileType == FsNodeType.FOLDER)
                    fsNodes.add(fsNode);
                if (!Files.isDirectory(Paths.get(path+"/"+fsNode)) && fileType == FsNodeType.FILE)
                    fsNodes.add(fsNode);
            }
            return fsNodes;
        }
    }
    
    /**
     * Get string from Input stream.
     * @param inputStream Stream from which the String should be read.
     * @return String, which is read from input stream.
     * @throws IOException If an I/O error occurs.
     * TODO: trash first html character is append to the end of output. The number of trash characters is equal to the number of lines.
     */
    public static String getStreamContent(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        // StandardCharsets.UTF_8.name() > JDK 7
        return result.toString("UTF-8");
    }
    
    /**
     * Read content of file.
     * @param filename  Name of file.
     * @return String which is represented content of file.
     * @throws FileNotFoundException If file not found.
     * @throws IOException If an I/O error occurs
     */
    public static String getFileContent(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        return getStreamContent(new FileInputStream(file));
    }
    
    /**
     * The method divides the inputFsNodePath (path to input file) into the groups by input regex.Then these groups are applicated to the copy of outputFsNodePath string
       so all $x (x is an integer) in copy of outputFsNodePath string is replaced by corresponding group.
     * @param regex Regex which is applicated to the path to the input and divides its to the groups.
     * @param inputFsNodePath Path to input file.
     * @param outputFsNodePath Path to output file - can be path to output file/folder, output logger etc.
     * @return Copy of outputFsNodePath where all $x strings are replaced by corresponding group, which is extracted from inputFsNodePath by regex.
     */
    public static String getPathToOutputFsNode(final String regex, final String inputFsNodePath, final String outputFsNodePath) {
        String outputPath = String.valueOf(outputFsNodePath);

        // get all $xx substrings from output path
        List<String> allMatches = new ArrayList<>();
        Pattern paramPattern = Pattern.compile(PARAM_REGEX);
        Matcher paramMatcher = paramPattern.matcher(outputFsNodePath);
        while(paramMatcher.find()) 
            allMatches.add(paramMatcher.group());

        // get groups by input regex
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputFsNodePath);
        matcher.find();
           for (String m : allMatches) {
                outputPath = outputPath.replaceAll("\\"+m, matcher.group(Integer.valueOf(m.substring(1))));
            }
        
        
        return outputPath;
    }
}
