/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNode.fileManager;

import com.app.secondaryNodeApp.fileManager.FileManager;
import static com.app.secondaryNode.TestSettings.*;
import com.app.secondaryNodeApp.fileManager.FileManager.FsNodeType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author filip
 */
public class FileManagerTest {

    public FileManagerTest() {

    }

    /**
     * Test of isExistPath method, of class FileManager.
     */
    @Test
    public void testIsExistPath() {
        assertEquals(true, FileManager.isExistPath(PATH_TO_TEST_FOLDER));
        assertEquals(false, FileManager.isExistPath(PATH_TO_TEST_FOLDER + NONEXIST_FOLDER_NAME));
        assertEquals(false, FileManager.isExistPath(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME));
        assertEquals(false, FileManager.isExistPath(""));
        assertThrows(NullPointerException.class, () -> {
            FileManager.isExistPath(null);
        });
    }

    /**
     * Test of isFolder method.
     */
    @Test
    public void testIsFolder() {
        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + EMPTY_FOLDER_NAME));
        assertEquals(true, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME));

        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + EMPTY_FILE_NAME));
        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + NONEMPTY_FILE_NAME));

        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + NONEXIST_FOLDER_NAME));
        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME));

        assertEquals(false, FileManager.isNonEmptyFolder(""));
        assertThrows(NullPointerException.class, () -> {
            FileManager.isNonEmptyFolder(null);
        });
    }

    /**
     * Test of isNonEmptyFolder method, of class FileManager.
     */
    @Test
    public void testIsNonEmptyFolder() {
        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + EMPTY_FOLDER_NAME));
        assertEquals(true, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME));

        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + EMPTY_FILE_NAME));
        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + NONEMPTY_FILE_NAME));

        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + NONEXIST_FOLDER_NAME));
        assertEquals(false, FileManager.isNonEmptyFolder(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME));

        assertEquals(false, FileManager.isNonEmptyFolder(""));
        assertThrows(NullPointerException.class, () -> {
            FileManager.isNonEmptyFolder(null);
        });
    }

    /**
     * Test of isNonEmptyFile method, of class FileManager.
     */
    @Test
    public void testIsNonEmptyFile() {
        assertEquals(false, FileManager.isNonEmptyFile(PATH_TO_TEST_FOLDER + EMPTY_FILE_NAME));
        assertEquals(true, FileManager.isNonEmptyFile(PATH_TO_TEST_FOLDER + NONEMPTY_FILE_NAME));

        assertEquals(false, FileManager.isNonEmptyFile(PATH_TO_TEST_FOLDER + EMPTY_FOLDER_NAME));
        assertEquals(false, FileManager.isNonEmptyFile(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME));

        assertEquals(false, FileManager.isNonEmptyFile(PATH_TO_TEST_FOLDER + NONEXIST_FOLDER_NAME));
        assertEquals(false, FileManager.isNonEmptyFile(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME));

        assertEquals(false, FileManager.isNonEmptyFile(""));
        assertThrows(NullPointerException.class, () -> {
            FileManager.isNonEmptyFile(null);
        });
    }

    /**
     * Test of getFileLength method, of class FileManager.
     */
    @Test
    public void testGetFileLength() {
        assertEquals(new Long(0), FileManager.getFsNodeLength(PATH_TO_TEST_FOLDER + EMPTY_FILE_NAME));
        assertEquals(new Long(17), FileManager.getFsNodeLength(PATH_TO_TEST_FOLDER + NONEMPTY_FILE_NAME));

        assertEquals(new Long(0), FileManager.getFsNodeLength(PATH_TO_TEST_FOLDER + EMPTY_FOLDER_NAME));
        assertEquals(new Long(50), FileManager.getFsNodeLength(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME));

        assertEquals(null, FileManager.getFsNodeLength(PATH_TO_TEST_FOLDER + NONEXIST_FOLDER_NAME));
        assertEquals(null, FileManager.getFsNodeLength(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME));

        assertEquals(false, FileManager.isNonEmptyFile(""));
        assertThrows(NullPointerException.class, () -> {
            FileManager.getFsNodeLength(null);
        });
    }

    /**
     * Test of deleteFsNode method, of class FileManager.
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testDeleteFsNode() throws IOException, InterruptedException {
        String testFile = PATH_TO_TEST_FOLDER + TMP_FILE_NAME;
        String testFolder = PATH_TO_TEST_FOLDER + TMP_FOLDER_NAME;

        File file = new File(testFile);
        if (!file.createNewFile()) {
            fail("Test file couldn't be created.");
        }
        assertEquals(true, FileManager.deleteFsNode(testFile));

        File folder = new File(testFolder);
        if (!folder.mkdir()) {
            fail("Test folder couldn't be created.");
        }
        assertEquals(true, FileManager.deleteFsNode(testFolder));

        if (!folder.mkdir()) {
            fail("Test folder couldn't be created.");
        }
        
        
        for (int i = 0; i < 10; i++) {
            File nestedFolder = new File(testFolder + "/" + i);
            if (!nestedFolder.mkdir()) {
                fail("Test folder couldn't be created.");
            }
            for (int j = 0; j < 10; j++) {
                File nestedFile = new File(nestedFolder.getAbsolutePath()+"/"+j+".txt");
                if (!nestedFile.createNewFile()) {
                    fail("Test file couldn't be created.");
                }
            }
        }
        assertEquals(true, FileManager.deleteFsNode(testFolder));
        
        // If the method is called repeatly, the true is still returned
        assertEquals(true, FileManager.deleteFsNode(testFolder));
    }

    /**
     * Test of getAllFsNodes method, of class FileManager.
     */
    @Test
    public void testGetAllFsNodes() {
        assertEquals(null, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + EMPTY_FILE_NAME, FsNodeType.FILE));
        assertEquals(null, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + EMPTY_FILE_NAME, FsNodeType.FOLDER));

        assertEquals(null, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + NONEMPTY_FILE_NAME, FsNodeType.FILE));
        assertEquals(null, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + NONEMPTY_FILE_NAME, FsNodeType.FOLDER));

        assertEquals(new ArrayList<String>(), FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + EMPTY_FOLDER_NAME, FsNodeType.FILE));
        assertEquals(new ArrayList<String>(), FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + EMPTY_FOLDER_NAME, FsNodeType.FOLDER));

        List<String> result1 = new ArrayList<String>();
        result1.add(NESTED_FILE_NAME_1);
        result1.add(NESTED_FILE_NAME_2);

        List<String> result2 = new ArrayList<String>();
        result2.add(NESTED_FOLDER_NAME_1);
        result2.add(NESTED_FOLDER_NAME_2);

        assertEquals(result1, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME, FsNodeType.FILE));
        assertEquals(result2, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME, FsNodeType.FOLDER));

        assertEquals(null, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + NONEXIST_FOLDER_NAME, FsNodeType.FILE));
        assertEquals(null, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + NONEXIST_FOLDER_NAME, FsNodeType.FOLDER));

        assertEquals(null, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME, FsNodeType.FILE));
        assertEquals(null, FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME, FsNodeType.FOLDER));

        assertEquals(false, FileManager.isNonEmptyFile(""));
        
        assertThrows(NullPointerException.class, () -> {
            FileManager.getAllFsNodes(null, FsNodeType.FILE);
        });
    }

    /**
     * Test of getStreamContent method, of class FileManager.
     */
    @Test
    public void testGetStreamContent() throws Exception {

        final String trashString1 = "Hello world";
        final String trashString2 = "Hello world\nHello world";
        final String trashString3 = "Hello world\nHello world\n";

        InputStream is = new ByteArrayInputStream("".getBytes(Charset.forName("UTF-8")));
        assertEquals("", FileManager.getStreamContent(is));

        InputStream is1 = new ByteArrayInputStream(trashString1.getBytes(Charset.forName("UTF-8")));
        assertEquals(trashString1, FileManager.getStreamContent(is1));

        InputStream is2 = new ByteArrayInputStream(trashString2.getBytes(Charset.forName("UTF-8")));
        assertEquals(trashString2, FileManager.getStreamContent(is2));

        InputStream is3 = new ByteArrayInputStream(trashString3.getBytes(Charset.forName("UTF-8")));
        assertEquals(trashString3, FileManager.getStreamContent(is3));

        assertThrows(NullPointerException.class, () -> {
            FileManager.getStreamContent(null);
        });
    }

    /**
     * Test of getFileContent method, of class FileManager.
     */
    @Test
    public void testGetFileContent() throws Exception {

        assertEquals(NESTED_FOLDER_2_CONTENT, FileManager.getFileContent(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME + "/" + NESTED_FILE_NAME_2));

        assertEquals("", FileManager.getFileContent(PATH_TO_TEST_FOLDER + EMPTY_FILE_NAME));
        assertEquals(NESTED_FOLDER_1_CONTENT, FileManager.getFileContent(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME + "/" + NESTED_FILE_NAME_1));

        assertThrows(FileNotFoundException.class, () -> { FileManager.getFileContent(PATH_TO_TEST_FOLDER + EMPTY_FOLDER_NAME); });
        assertThrows(FileNotFoundException.class, () -> { FileManager.getFileContent(PATH_TO_TEST_FOLDER + NONEMPTY_FOLDER_NAME); });

        assertThrows(FileNotFoundException.class, () -> { FileManager.getFileContent(PATH_TO_TEST_FOLDER + NONEXIST_FOLDER_NAME); });
        assertThrows(FileNotFoundException.class, () -> { FileManager.getFileContent(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME); });

        assertEquals(false, FileManager.isNonEmptyFile(""));
        assertThrows(NullPointerException.class, () -> {
            FileManager.getFileContent(null);
        });
    }

    /**
     * Test of getPathToOutputFsNode method, of class FileManager.
     */
    @Test
    public void testGetPathToOutputFsNode() {
        assertEquals("mnt/out/filip/file_1.out", FileManager.getPathToOutputFsNode("(.*)\\/(.*)(\\.txt)", "mnt/in/filip/file_1.txt", "mnt/out/filip/$2.out"));
        assertEquals("mnt/out/filip/.out", FileManager.getPathToOutputFsNode("(.*)\\/(.*)(\\.txt)", "mnt/in/filip/file_1.txt", "mnt/out/filip/.out"));
        assertEquals("", FileManager.getPathToOutputFsNode("(.*)\\/(.*)(\\.txt)", "mnt/in/filip/file_1.txt", ""));

        assertThrows(PatternSyntaxException.class, () -> {
            FileManager.getPathToOutputFsNode("(.*)\\/.*)(\\.txt)", "mnt/in/filip/file_1.txt", "");
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            FileManager.getPathToOutputFsNode("(.*)\\/(.*)(\\.txt)", "mnt/in/filip/file_1.txt", "mnt/out/filip/$5.out");
        });
    }
}
