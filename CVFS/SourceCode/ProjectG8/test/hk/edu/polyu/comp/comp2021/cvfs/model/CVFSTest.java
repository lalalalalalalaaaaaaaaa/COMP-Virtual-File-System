/*Please note that some errors may occur in testing in different devices is due to
the difference of newline character (println) defined by the different operating systems
(MacOS: \r\n ; Windows: \n)!!!*/

package hk.edu.polyu.comp.comp2021.cvfs.model;

import hk.edu.polyu.comp.comp2021.cvfs.model.control.*;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.*;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.File;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.io.*;

import static org.junit.Assert.*;

public class CVFSTest implements Serializable {
    CVFS cvfs;
    private ByteArrayOutputStream outputStreamCaptor= new ByteArrayOutputStream();;

    @Before
    public void setUp() {
        cvfs = new CVFS(10000);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void testCVFSConstructor() {
        assertNotNull("CVFS should be created and not null",cvfs);
    }

    @Test
    public void testVirtualDiskCreation() {
        cvfs.newVD(8000);
        assertEquals(8000, cvfs.getVirtualDisk().getMaxSize());
        assertEquals(0, cvfs.getVirtualDisk().getUsedSize());

        cvfs.newVD(20000);
        assertEquals(20000, cvfs.getVirtualDisk().getMaxSize());
        assertEquals(0, cvfs.getVirtualDisk().getUsedSize());

        cvfs.newVD(5000);
        assertEquals(5000, cvfs.getVirtualDisk().getMaxSize());
        assertEquals(0, cvfs.getVirtualDisk().getUsedSize());
    }

    @Test
    public void testDocumentCreation() {
        // Test creating four valid files of four valid types respectively
        cvfs.newDoc("file1", "txt", "content1");
        List<File> files = cvfs.getCurrentDir().getFiles();
        assertEquals(1, files.size());
        assertTrue(files.get(0) instanceof Document);
        Document doc = (Document) files.get(0);
        assertEquals("file1", doc.getName());
        assertEquals("txt", doc.getType());
        assertEquals("content1", doc.getContent());
        assertEquals(56, doc.getSize());

        cvfs.newDoc("file2", "java", "public class Test {}");
        files = cvfs.getCurrentDir().getFiles();
        assertEquals(2, files.size());
        assertTrue(files.get(1) instanceof Document);
        doc = (Document) files.get(1);
        assertEquals("file2", doc.getName());
        assertEquals("java", doc.getType());
        assertEquals("public class Test {}", doc.getContent());
        assertEquals(80, doc.getSize());

        cvfs.newDoc("file3", "html", "<html>");
        files = cvfs.getCurrentDir().getFiles();
        assertEquals(3, files.size());
        assertTrue(files.get(2) instanceof Document);
        doc = (Document) files.get(2);
        assertEquals("file3", doc.getName());
        assertEquals("html", doc.getType());
        assertEquals("<html>", doc.getContent());
        assertEquals(52, doc.getSize());

        cvfs.newDoc("file4", "css", "body{}");
        files = cvfs.getCurrentDir().getFiles();
        assertEquals(4, files.size());
        assertTrue(files.get(3) instanceof Document);
        doc = (Document) files.get(3);
        assertEquals("file4", doc.getName());
        assertEquals("css", doc.getType());
        assertEquals("body{}", doc.getContent());
        assertEquals(52, doc.getSize());

        // name too long
        try {
            cvfs.newDoc("veryLongFileName", "txt", "Content of file");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid file name", e.getMessage());
        }

        // invalid type
        try {
            cvfs.newDoc("file5", "invalid", "Content of file5");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid document type. Allowed types are: txt,java,html,css", e.getMessage());
        }

        //invalid name
        try {
            cvfs.newDoc("*****", "invalid", "Content of file6");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid file name", e.getMessage());
        }

        // Test creating a document when disk is full
        cvfs.newVD(10);
        try {
            cvfs.newDoc("file7", "txt", "This is a long content that will exceed the disk size");
            fail("Expected IllegalStateException was not thrown");
        } catch (IllegalStateException e) {
            assertEquals("Disk is full", e.getMessage());
        }

        // Test creating a document when disk is full
        cvfs.newVD(10);
        try {
            cvfs.newDoc("file7", "txt", "This is a long content that will exceed the disk size");
            fail("Expected IllegalStateException was not thrown");
        } catch (IllegalStateException e) {
            assertEquals("Disk is full", e.getMessage());
        }

    }

    @Test
    public void testDirectoryCreation() {
        // Test creating a valid directory
        cvfs.newDir("dir1");
        List<File> files = cvfs.getCurrentDir().getFiles();
        assertEquals(1, files.size());
        Directory dir=cvfs.getCurrentDir();
        assertEquals(80, dir.getSize());  //rootDir也要算
        try {
            cvfs.newDir("dir1");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("A file or directory with the same name already exists", e.getMessage());
        }

        try {
            cvfs.newDir("thisisaverylongdirectoryname");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid file name", e.getMessage());
        }

        // Test creating a directory when disk is full
        cvfs.newVD(10);
        try {
            cvfs.newDir("dir3");
            fail("Expected IllegalStateException was not thrown");
        } catch (IllegalStateException e) {
            assertEquals("Disk is full", e.getMessage());
        }
    }

    @Test
    public void testDelete() {

        cvfs.newDoc("file1", "txt", "Content1");
        cvfs.newDoc("file2", "txt", "Content2");
        List<File> files = cvfs.getCurrentDir().getFiles();
        assertEquals(2, files.size());

        // Delete the document
        cvfs.delete("file1");
        files = cvfs.getCurrentDir().getFiles();
        assertEquals(1, files.size());

        // Check whether file is deleted and space is freed
        VirtualDisk disk = cvfs.getVirtualDisk();
        assertEquals(56, disk.getUsedSize());

        // Try to delete a non-existent file
        try {
            cvfs.delete("file1");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("File does not exist", e.getMessage());
        }
    }

    @Test
    public void testRename() {
        cvfs.newDoc("file", "txt", "Content of file");
        List<File> files = cvfs.getCurrentDir().getFiles();
        assertEquals(1, files.size());
        assertTrue(files.get(0) instanceof Document);
        Document doc = (Document) files.get(0);
        assertEquals("file", doc.getName());

        // Rename the document
        cvfs.rename("file", "newFile");
        files = cvfs.getCurrentDir().getFiles();
        assertEquals(1, files.size());
        doc = (Document) files.get(0);
        assertEquals("newFile", doc.getName());

        // Try to rename a non-existent file
        try {
            cvfs.rename("file1", "newFile2");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("File does not exist", e.getMessage());
        }

        // Try to rename with an invalid new file name
        try {
            cvfs.rename("newFile", "**************");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid new file name", e.getMessage());
        }
        try {
            cvfs.rename("newFile", "newFile");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("A file or directory with the same name already exists", e.getMessage());
        }
    }


    @Test
    public void testChangeDir() {
        // Create directories
        cvfs.newDir("dir1");
        cvfs.newDir("dir2");

        // Change to dir1
        cvfs.changeDir("dir1");
        assertEquals("dir1", cvfs.getCurrentDir().getName());

        // Change to root directory again
        cvfs.changeDir("..");
        assertEquals("/", cvfs.getCurrentDir().getName());

        // Try to change to a non-existent directory
        try {
            cvfs.changeDir("nonexistent");
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Directory not found", e.getMessage());
        }

        // Already in root directory
        cvfs.changeDir("..");
        assertEquals("/", cvfs.getCurrentDir().getName());
    }

    @Test
    public void testNestedDirectories() {
        // Create nested directories
        cvfs.newDir("dir1");
        cvfs.changeDir("dir1");
        cvfs.newDir("dir2");
        cvfs.changeDir("dir2");

        cvfs.changeDir("..");
        assertEquals("dir1", cvfs.getCurrentDir().getName());
        cvfs.changeDir("..");
        assertEquals("/", cvfs.getCurrentDir().getName());

    }

    @Test
    public void testListEmpty() {
        cvfs.list();
        String expectedOutput = "Files in directory /:\r\n" +
                "Total files in the working directory: 0\r\n" +
                "Total size of the working directory: 0 bytes";

        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }
    @Test
    public void testList() {
        // Create some documents and directories
        cvfs.newDir("dir1");
        cvfs.newDoc("file1", "txt", "Content of file1");
        cvfs.newDoc("file2", "java", "public class Test {}");
        cvfs.newDoc("file3", "html", "<html>");
        cvfs.list();
        String expectedOutput = "Files in directory /:\r\n" +
                "dir1 40 bytes\r\n" +
                "file1 txt 72 bytes\r\n" +
                "file2 java 80 bytes\r\n" +
                "file3 html 52 bytes\r\n" +
                "Total files in the working directory: 4\r\n" +
                "Total size of the working directory: 244 bytes";

        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    public void testListWithNestedDirectories() {
        // Create some documents and directories
        cvfs.newDoc("file1", "txt", "Content of file1");
        cvfs.newDir("dir1");
        cvfs.changeDir("dir1");
        cvfs.newDoc("file2", "java", "public class Test {}");
        cvfs.changeDir("..");

        cvfs.list();

        String expectedOutput = "Changed directory to dir1\r\n" +
                "Changed directory to the parent directory.\r\n" +
                "Files in directory /:\r\n" +
                "file1 txt 72 bytes\r\n" +
                "dir1 120 bytes\r\n" +
                "Total files in the working directory: 2\r\n"+
                "Total size of the working directory: 192 bytes";

        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    public void testRListEmpty() {
        cvfs.rList();

        String expectedOutput = "Total files: 0\r\n" +
                "Total size: 0 bytes";

        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    public void testRList() {
        cvfs.newDoc("file1", "txt", "Content of file1");
        cvfs.newDoc("file2", "java", "public class Test {}");
        cvfs.newDir("dir1");

        cvfs.changeDir("dir1");
        cvfs.newDoc("file3", "html", "<html>");
        cvfs.changeDir("..");


        cvfs.rList();
        // Expected output with proper indentation
        String expectedOutput = "Changed directory to dir1\r\n" +
                "Changed directory to the parent directory.\r\n" +
                "file1 txt 72 bytes\r\n" +
                "file2 java 80 bytes\r\n" +
                "dir1 92 bytes\r\n" +
                "  file3 html 52 bytes\r\n" +
                "Total files: 3\r\n" +
                "Total size: 244 bytes";

        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    /*****************************************************************/

    @Test
    public void testNewCriterionValid(){

        cvfs.printAllCriteria();
        assertEquals("No criterion accessible.",  outputStreamCaptor.toString().trim());
        outputStreamCaptor.reset();

        cvfs.newSimpleCri("ab", "name", "contains", "\"file1\"");
        assertNotNull(cvfs.getCriterionHashMap().get("ab"));

        cvfs.newSimpleCri("cd", "size", ">", "10");

        cvfs.isDocument();
        assertNotNull(cvfs.getCriterionHashMap().get("IsDocument"));

        cvfs.newNegation("ef", "ab");
        assertNotNull(cvfs.getCriterionHashMap().get("ef"));

        cvfs.newBinaryCri("gh","ab", "&&", "cd");
        assertNotNull(cvfs.getCriterionHashMap().get("gh"));

        cvfs.printAllCriteria();
        assertEquals(
                "ab: name contains \"file1\"\r\n" +
                        "cd: size > 10\r\n" +
                        "ef: NOT name contains \"file1\"\r\n" +
                        "gh: name contains \"file1\" && size > 10\r\n" +
                        "IsDocument: NOT type equals \"directory\"" ,
                outputStreamCaptor.toString().trim());
        outputStreamCaptor.reset();


    }

        @Test
        public void testSimpleCriterionInvalid() {
            try {
                cvfs.newSimpleCri("abcd", "name", "contains", "\"file1\"");
                fail("Criterion name must contains exactly two English letters.");
            } catch (Exception e) {
            }

            try {
                cvfs.newSimpleCri("ab", "invalid", "contains", "\"file1\"");
                fail("Attribute name must be either name, type or size.");
            } catch (Exception e) {
            }

            try {
                cvfs.newSimpleCri("ab", "name", "invalid", "\"file1\"");
                fail("Attribute is name, Op must be contains.");
            } catch (Exception e) {
            }

            try {
                cvfs.newSimpleCri("ab", "type", "invalid", "\"file1\"");
                fail("Attribute is type, Op must be equals.");
            } catch (Exception e) {
            }

            try {
                cvfs.newSimpleCri("ab", "size", "invalid", "\"file1\"");
                fail("Attribute is size, Op must be >, <, >=, <=, ==, or !=.");
            } catch (Exception e) {
            }

            try {
                cvfs.newSimpleCri("ab", "name", "contains", "123");
                fail("Attribute is name, val must be a string in the double quote.");
            } catch (Exception e) {
            }

            try {
                cvfs.newSimpleCri("ab", "type", "equals", "123");
                fail("Attribute is type, val must be a string in the double quote.");
            } catch (Exception e) {
            }

            try {
                cvfs.newSimpleCri("ab", "size", ">", "invalid");
                fail("Attribute is size, val must be an integer.");
            } catch (Exception e) {
            }

            try {
                cvfs.newSimpleCri("ab", "name", "contains", "\"file1\"");
                cvfs.newSimpleCri("ab", "size", ">", "10");
                fail("Criterion ab already exists.");
            } catch (Exception e) {
            }
        }

        @Test
        public void testNegationCriterionInvalid() {
            try {
                cvfs.newNegation("cd", "ab");
                fail("Criterion ab doesn't exist.");
            } catch (IllegalArgumentException e) {
            }

            try {
                cvfs.newSimpleCri("ab", "size", ">", "10");
                cvfs.newNegation("cde", "ab");
                fail("Criterion name must contains exactly two English letters.");
            } catch (IllegalArgumentException e) {
            }

            try {
                cvfs.newNegation("ab", "ab");
                fail("Criterion ab already exists.");
            } catch (IllegalArgumentException e) {
            }
        }

        @Test
        public void testBinaryCriterionInvalid() {
            cvfs.newSimpleCri("ab", "name", "contains", "\"file1\"");
            cvfs.newSimpleCri("cd", "size", ">", "10");

            try {
                cvfs.newBinaryCri("ef", "ab", "&|", "cd");
                fail("LogicOp is either && or ||");
            } catch (IllegalArgumentException e) {
            }

            try {
                cvfs.newBinaryCri("efg", "ab", "&&", "cd");
                fail("Criterion name must contains exactly two English letters.");
            } catch (IllegalArgumentException e) {
            }

            cvfs.getCriterionHashMap().remove("ab");

            try {
                cvfs.newBinaryCri("ef", "ab", "&&", "cd");
                fail("Criterion ab doesn't exist.");
            } catch (IllegalArgumentException e) {
            }

            cvfs.newSimpleCri("ab", "name", "contains", "\"file1\"");

            try {
                cvfs.newBinaryCri("ab", "ab", "||", "cd");
                fail("Criterion ab already exists.");
            } catch (IllegalArgumentException e) {
            }

            cvfs.getCriterionHashMap().remove("cd");

            try {
                cvfs.newBinaryCri("gh", "ab", "&&", "cd");
                fail("Criterion cd doesn't exist.");
            } catch (IllegalArgumentException e) {
            }
        }


    @Test
    public void testBothSearchMethod(){

        cvfs.newSimpleCri("ab", "name", "contains", "\"document\"");
        cvfs.newSimpleCri("cd", "type", "equals", "\"txt\"");
        cvfs.newSimpleCri("ef", "size", ">=", "72");
        cvfs.isDocument();
        cvfs.newSimpleCri("gh", "size", ">", "72");
        cvfs.newSimpleCri("ij", "size", "<", "72");
        cvfs.newSimpleCri("kl", "size", "<=", "72");
        cvfs.newSimpleCri("mn", "size", "==", "72");
        cvfs.newSimpleCri("op", "size", "!=", "72");

        cvfs.newNegation("qr", "mn");
        cvfs.newBinaryCri("st","ab","&&","gh");
        cvfs.newBinaryCri("uv","cd","||","mn");
        //........

        cvfs.newVD(400);

        cvfs.newDoc("document", "txt", "content is here");
        cvfs.newDoc("document1", "java", "content1 is here");
        cvfs.newDoc("document2", "html", "content2 is here");

        cvfs.newDir("directory1");
        cvfs.changeDir("directory1");
        cvfs.newDoc("document3", "txt", "content3 is here");
        cvfs.changeDir("..");

        outputStreamCaptor.reset();


        List<File> result = cvfs.search("ab");
        assertEquals("Total number of files: 3; Total size of files: 214.", outputStreamCaptor.toString().trim());
        //actual size:40+15*2 + 40+16*2 + 40+16*2= 214
        outputStreamCaptor.reset();

        List<File> result2 = cvfs.search("cd");
        assertEquals("Total number of files: 1; Total size of files: 70.", outputStreamCaptor.toString().trim());
        //actual size:40+15*2 = 70
        outputStreamCaptor.reset();

        List<File> result3 = cvfs.search("ef");
        assertEquals("Total number of files: 3; Total size of files: 256.", outputStreamCaptor.toString().trim());
        //actual size:40+16*2 + 40+16*2 + 40+40+16*2 = 256
        outputStreamCaptor.reset();

        List<File> result4 = cvfs.search("IsDocument");
        assertEquals("Total number of files: 3; Total size of files: 214.", outputStreamCaptor.toString().trim());
        //actual size:40+15*2 + 40+16*2 + 40+16*2= 214
        outputStreamCaptor.reset();

        List<File> result5 = cvfs.search("gh");
        assertEquals("Total number of files: 1; Total size of files: 112.", outputStreamCaptor.toString().trim());
        //actual size:40+40+16*2=112
        outputStreamCaptor.reset();

        List<File> result6 = cvfs.search("ij");
        assertEquals("Total number of files: 1; Total size of files: 70.", outputStreamCaptor.toString().trim());
        //actual size:40+15*2=70
        outputStreamCaptor.reset();

        List<File> result7 = cvfs.search("kl");
        assertEquals("Total number of files: 3; Total size of files: 214.", outputStreamCaptor.toString().trim());
        //actual size:40+15*2 + 40+16*2 + 40+16*2 = 214
        outputStreamCaptor.reset();

        List<File> result8 = cvfs.search("mn");
        assertEquals("Total number of files: 2; Total size of files: 144.", outputStreamCaptor.toString().trim());
        //actual size:40+16*2 + 40+16*2 = 144
        outputStreamCaptor.reset();

        List<File> result9 = cvfs.search("op");
        assertEquals("Total number of files: 2; Total size of files: 182.", outputStreamCaptor.toString().trim());
        //actual size:40+15*2 + 40+40+16*2 = 182
        outputStreamCaptor.reset();

        List<File> result10 = cvfs.search("qr");
        assertEquals("Total number of files: 2; Total size of files: 182.", outputStreamCaptor.toString().trim());
        //actual size:40+15*2 + 40+40+16*2 = 182
        outputStreamCaptor.reset();

        List<File> result11= cvfs.search("st");
        assertEquals("Total number of files: 0; Total size of files: 0.", outputStreamCaptor.toString().trim());
        //actual size:0
        outputStreamCaptor.reset();

        List<File> result13 = cvfs.search("uv");

        assertEquals("Total number of files: 3; Total size of files: 214.", outputStreamCaptor.toString().trim());
        //actual size:40+15*2 + 40+16*2 + 40+16*2 = 214
        outputStreamCaptor.reset();

        cvfs.printSatifiedFile(result13);
        assertEquals("document txt 70 bytes\r\n" +
                        "document1 java 72 bytes\r\n" +
                        "document2 html 72 bytes", outputStreamCaptor.toString().trim());
        outputStreamCaptor.reset();


        List<File> result14 = cvfs.rSearch("mn");
        assertEquals("Total number of files: 3; Total size of files: 216.", outputStreamCaptor.toString().trim());
        //actual size:40+16*2 + 40+16*2 + 40+16*2 = 216
        outputStreamCaptor.reset();


        //......
    }

@Test
    public void testSaveLoad(){
        cvfs.newDir("dir1");
        cvfs.newDoc("file1", "txt", "content1");
        cvfs.newSimpleCri("ab","size", ">", "10");
        //cvfs.newNegation("cd", "ab");
        cvfs.list();
        cvfs.printAllCriteria();

        Controller controller=new Controller(cvfs);
        controller.save("testpath");
        CVFS cvfs2 = Controller.load("testpath");


        cvfs2.list();
        cvfs2.printAllCriteria();
        String expectedOutput = "Files in directory /:\r\n" +
                "dir1 40 bytes\r\n" +
                "file1 txt 56 bytes\r\n" +
                "Total files in the working directory: 2\r\n" +
                "Total size of the working directory: 96 bytes\r\n" +
                "ab: size > 10\r\n" +
                "CVFS saved successfully.\r\n" +
                "CVFS loaded successfully.\r\n" +
                "Files in directory /:\r\n" +
                "dir1 40 bytes\r\n" +
                "file1 txt 56 bytes\r\n" +
                "Total files in the working directory: 2\r\n" +
                "Total size of the working directory: 96 bytes\r\n" +
                "ab: size > 10";
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
        outputStreamCaptor.reset();

         controller.save("?");
         assertEquals("Error saving virtual disk.", outputStreamCaptor.toString().trim());
         outputStreamCaptor.reset();
         cvfs2=Controller.load("*");
         assertEquals("Error loading virtual disk: File not found.", outputStreamCaptor.toString().trim());

    }

    @Test
    public void testNewDirUndoRedoMultipleTimes() {
        cvfs.newDir("dir1");
        cvfs.newDir("dir2");
        cvfs.newDir("dir3");

        assertTrue(cvfs.getCurrentDir().getFile("dir1") instanceof Directory);
        assertTrue(cvfs.getCurrentDir().getFile("dir2") instanceof Directory);
        assertTrue(cvfs.getCurrentDir().getFile("dir3") instanceof Directory);

        cvfs.undo();
        assertNull(cvfs.getCurrentDir().getFile("dir3"));

        cvfs.undo();
        assertNull(cvfs.getCurrentDir().getFile("dir2"));

        cvfs.undo();
       assertNull(cvfs.getCurrentDir().getFile("dir1"));

        cvfs.redo();
        assertTrue(cvfs.getCurrentDir().getFile("dir1") instanceof Directory);

        cvfs.redo();
        assertTrue(cvfs.getCurrentDir().getFile("dir2") instanceof Directory);

        cvfs.redo();
        assertTrue(cvfs.getCurrentDir().getFile("dir3") instanceof Directory);

        cvfs.newDoc("doc1", "txt", "content1");
        cvfs.undo();
        assertNull(cvfs.getCurrentDir().getFile("doc1"));
        cvfs.redo();
        assertNotNull(cvfs.getCurrentDir().getFile("doc1"));

        cvfs.newDir("dir4");
        cvfs.delete("dir4");
        assertNull(cvfs.getCurrentDir().getFile("dir4"));
        cvfs.undo();
        assertNotNull(cvfs.getCurrentDir().getFile("dir4"));
        cvfs.redo();
        assertNull(cvfs.getCurrentDir().getFile("dir4"));

        cvfs.rename("doc1", "doc4");
        cvfs.undo();
        assertNull(cvfs.getCurrentDir().getFile("doc4"));
        cvfs.redo();
        assertNotNull(cvfs.getCurrentDir().getFile("doc4"));

        outputStreamCaptor.reset();
        cvfs.changeDir("dir1");
        cvfs.list();
        assertEquals("Changed directory to dir1\r\n" +
                "Files in directory dir1:\r\n" +
                "Total files in the working directory: 0\r\n" +
                "Total size of the working directory: 0 bytes", outputStreamCaptor.toString().trim());
        outputStreamCaptor.reset();
        //cvfs.changeDir("..");
        cvfs.undo();
        cvfs.list(); //now curWorkDIr is parentDir
        assertEquals("Files in directory /:\r\n" +
                "dir1 40 bytes\r\n" +
                "dir2 40 bytes\r\n" +
                "dir3 40 bytes\r\n" +
                "doc4 txt 56 bytes\r\n" +
                "Total files in the working directory: 4\r\n" +
                "Total size of the working directory: 176 bytes", outputStreamCaptor.toString().trim());
        outputStreamCaptor.reset();
        cvfs.redo();
        cvfs.list(); //now curWorkDir is dir1 again
        assertEquals(
                "Files in directory dir1:\r\n" +
                "Total files in the working directory: 0\r\n" +
                "Total size of the working directory: 0 bytes", outputStreamCaptor.toString().trim());
        outputStreamCaptor.reset();

        cvfs.newSimpleCri("ab", "size", ">", "100");
        cvfs.undo();
        assertNull(cvfs.getCriterionHashMap().get("ab"));
        cvfs.redo();
        assertNotNull(cvfs.getCriterionHashMap().get("ab"));

        cvfs.newNegation("cd", "ab");
        cvfs.undo();
        assertNull(cvfs.getCriterionHashMap().get("cd"));
        cvfs.redo();
        assertNotNull(cvfs.getCriterionHashMap().get("cd"));
    }


}