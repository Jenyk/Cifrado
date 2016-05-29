package cli;

import basic.EncryptionService;
import control.EncryptedFileStatus;
import control.EncryptionServiceInterface;
import control.RawFile;
import filesystem.FileSystemServiceInterface;
import filesystem.exceptions.FileSystemException;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import filesystem.local.LocalFileStorage;
import filesystem.local.deletion.FileDeleterInterface;
import filesystem.local.deletion.SecureFileDeleter;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import security.AesEncryptionProvider;
import security.EncryptionProviderInterface;
import security.HmacIntegrityProvider;
import security.IntegrityProviderInterface;
import security.exceptions.EncryptionException;
import security.exceptions.IntegrityException;
import utils.FolderAdder;
import utils.FolderAdderInterface;
import java.io.*;
import java.nio.file.Paths;
import java.security.spec.InvalidParameterSpecException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jan on 29.5.16.
 */
public class CLI {

    private final static Logger logger = Logger.getLogger(CLI.class.getName());
    private String[] args = null;
    private Options options = new Options();
    EncryptionServiceInterface encryptionService;
    FolderAdderInterface folderAdder;
    private static final String DATA_PATH = "encfs/data/";
    private static final String INTEGRITY_PATH = "encfs/integrity/";
    private static final String PASSWORD = "secretpassword";
    private static final String ADD = "add";
    private static final String LIST = "list";
    private static final String REMOVE = "remove";
    private static final String MOVE = "move";
    private static final String EXPORT = "export";
    private static final String IMPORT = "import";
    private static final String GET = "get";

    public CLI(String[] args) {
        FileDeleterInterface fileDeleter = new SecureFileDeleter();
        FileSystemServiceInterface dataFileSystemService = new LocalFileStorage(DATA_PATH, fileDeleter);
        FileSystemServiceInterface integrityFileSystemService = new LocalFileStorage(INTEGRITY_PATH, fileDeleter);
        EncryptionProviderInterface encryptionProvider = new AesEncryptionProvider();
        IntegrityProviderInterface integrityProvider = new HmacIntegrityProvider(integrityFileSystemService);
        encryptionService = new EncryptionService(dataFileSystemService, encryptionProvider, integrityProvider);
        folderAdder = new FolderAdder(encryptionService);

        this.args = args;
        options.addOption("h", "help", false, "show help");
        options.addOption("add", false, "Encrypt file or folder. Params: source file or folder, optional target path");
        options.addOption("ls", LIST, false, "List files in a folder. Params: source folder (optional)");
        options.addOption("rm", REMOVE, false, "Remove file or folder. Params: target file or folder");
        options.addOption("mv", MOVE, false, "Move file. Params: source file, target path");
        options.addOption(EXPORT, false, "Export encrypted file for secure transfer. Params: source file, tempPassword");
        options.addOption(IMPORT, false, "Import encrypted file. Params: source file, tempPassword, optional target path");
        options.addOption(GET, false, "Decrypt file. Params: source file");

        options.addOption("src", "source", true, "source file or folder to be encrypted");
        options.addOption("target", true, "target path to the newly encrypted file or folder");
        options.addOption("tempPassword", true, "temporary transfer password");
    }

    public void parse() {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            checkParams(cmd);
            if (cmd.hasOption(ADD)) {
                File source = new File(cmd.getOptionValue("src"));
                String target;
                if (!cmd.hasOption("target")) {
                    target = source.getPath();
                } else {
                    target = cmd.getOptionValue("src");
                }
                if (source.isDirectory()) {
                    try {
                        folderAdder.addFolder(Paths.get(source.getPath()), target, PASSWORD);
                    } catch (IOException e) {
                        logger.severe(e.getMessage());
                    } catch (PathCollisionException e) {
                        logger.severe(e.getMessage());
                    } catch (EncryptionException e) {
                        logger.severe(e.getMessage());
                    } catch (IntegrityException e) {
                        logger.severe(e.getMessage());
                    } catch (FileSystemException e) {
                        logger.severe(e.getMessage());
                    } catch (InvalidPathException e) {
                        logger.severe(e.getMessage());
                    } catch (InvalidParameterSpecException e) {
                        logger.severe(e.getMessage());
                    }
                } else {
                    InputStream data = null;
                    try {
                        data = new FileInputStream(source.getPath());
                    } catch (FileNotFoundException e) {
                        logger.severe(e.getMessage());
                    }
                    RawFile raw = new RawFile(source.getName(), data);
                    try {
                        encryptionService.addFile(raw, target, PASSWORD);
                    } catch (EncryptionException e) {
                        logger.severe(e.getMessage());
                    } catch (FileSystemException e) {
                        logger.severe(e.getMessage());
                    } catch (IntegrityException e) {
                        logger.severe(e.getMessage());
                    } catch (PathCollisionException e) {
                        logger.severe(e.getMessage());
                    } catch (InvalidPathException e) {
                        logger.severe(e.getMessage());
                    } catch (InvalidParameterSpecException e) {
                        logger.severe(e.getMessage());
                    }
                }
            } else if (cmd.hasOption(REMOVE)) {
                String target = cmd.getOptionValue("target");
                if (new File(target).isDirectory()) {
                    try {
                        folderAdder.removeFolder(Paths.get(target));
                    } catch (IOException e) {
                        logger.severe(e.getMessage());
                    } catch (IntegrityException e) {
                        logger.severe(e.getMessage());
                    } catch (FileSystemException e) {
                        logger.severe(e.getMessage());
                    } catch (InvalidPathException e) {
                        logger.severe(e.getMessage());
                    } catch (InvalidParameterSpecException e) {
                        logger.severe(e.getMessage());
                    }
                } else {
                    try {
                        encryptionService.deleteFile(target);
                    } catch (InvalidPathException e) {
                        logger.severe(e.getMessage());
                    } catch (FileSystemException e) {
                        logger.severe(e.getMessage());
                    } catch (IntegrityException e) {
                        logger.severe(e.getMessage());
                    } catch (InvalidParameterSpecException e) {
                        logger.severe(e.getMessage());
                    }
                }
            } else if (cmd.hasOption(MOVE)) {
                String src = cmd.getOptionValue("src");
                String target = cmd.getOptionValue("target");
                try {
                    encryptionService.moveFile(src, target, PASSWORD);
                } catch (EncryptionException e) {
                    logger.severe(e.getMessage());
                } catch (FileSystemException e) {
                    logger.severe(e.getMessage());
                } catch (IntegrityException e) {
                    logger.severe(e.getMessage());
                } catch (PathCollisionException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidPathException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidParameterSpecException e) {
                    logger.severe(e.getMessage());
                }
            } else if (cmd.hasOption(LIST)) {
                checkParams(cmd);
                String targetFolder;
                if (!cmd.hasOption("src")) {
                    targetFolder = "";
                } else {
                    targetFolder = cmd.getOptionValue("src");
                }
                List<EncryptedFileStatus> files;
                try {
                    files = encryptionService.listFiles(targetFolder, PASSWORD);
                    printFileStatusRecursive(files);
                } catch (InvalidPathException e) {
                    logger.severe(e.getMessage());
                } catch (FileSystemException e) {
                    logger.severe(e.getMessage());
                } catch (IntegrityException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidParameterSpecException e) {
                    logger.severe(e.getMessage());
                }
            } else if (cmd.hasOption(EXPORT)) {
                String target = cmd.getOptionValue("src");
                String tempPass = cmd.getOptionValue("tempPassword");
                try {
                    encryptionService.exportEncrypted(target, PASSWORD, tempPass, DATA_PATH);
                } catch (EncryptionException e) {
                    logger.severe(e.getMessage());
                } catch (FileSystemException e) {
                    logger.severe(e.getMessage());
                } catch (IntegrityException e) {
                    logger.severe(e.getMessage());
                } catch (PathCollisionException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidPathException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidParameterSpecException e) {
                    logger.severe(e.getMessage());
                } catch (IOException e) {
                    logger.severe(e.getMessage());
                }
                System.out.println("File was encrypted with temporary transfer password and saved outside of the secure root data directory " + DATA_PATH);
            } else if (cmd.hasOption(IMPORT)) {
                String targetFolder = cmd.getOptionValue("target");
                String source = cmd.getOptionValue("src");
                String tempPass = cmd.getOptionValue("tempPassword");
                try {
                    encryptionService.importEncrypted(source, targetFolder, tempPass, PASSWORD, DATA_PATH);
                } catch (EncryptionException e) {
                    logger.severe(e.getMessage());
                } catch (FileSystemException e) {
                    logger.severe(e.getMessage());
                } catch (IntegrityException e) {
                    logger.severe(e.getMessage());
                } catch (PathCollisionException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidPathException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidParameterSpecException e) {
                    logger.severe(e.getMessage());
                } catch (IOException e) {
                    logger.severe(e.getMessage());
                }
            } else if (cmd.hasOption(GET)) {
                String src = cmd.getOptionValue("src");
                RawFile rawFile;
                try {
                    rawFile = encryptionService.getFile(src, PASSWORD);
                    String[] pathParts = rawFile.getFileName().split("/");
                    String filename = pathParts[pathParts.length - 1];
                    FileUtils.copyInputStreamToFile(rawFile.getData(), new File(filename));
                } catch (EncryptionException e) {
                    logger.severe(e.getMessage());
                } catch (FileSystemException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidPathException e) {
                    logger.severe(e.getMessage());
                } catch (InvalidParameterSpecException e) {
                    logger.severe(e.getMessage());
                } catch (IOException e) {
                    logger.severe(e.getMessage());
                }
            } else if (cmd.hasOption("h")) {
                help();
            }

        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Failed to parse command line properties", e);
            help();
        }
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }

    private void checkParams(CommandLine cmd) {
        if (cmd.hasOption(ADD) || cmd.hasOption(GET)) {
            if (cmd.hasOption("source")) {
                logger.log(Level.INFO, "Using cli arguments " +
                        "-src=" + cmd.getOptionValue("src") + " and " +
                        "-target=" + cmd.getOptionValue("target"));
            } else {
                if (!cmd.hasOption("source")) {
                    logger.log(Level.SEVERE, "Missing argument -src");
                }
                help();
            }
        } else if (cmd.hasOption(REMOVE)) {
            if (cmd.hasOption("target")) {
                logger.log(Level.INFO, "Using cli arguments " +
                        "-target=" + cmd.hasOption("target"));
            } else {
                if (!cmd.hasOption("target")) {
                    logger.log(Level.SEVERE, "Missing argument -target");
                }
                help();
            }
        } else if (cmd.hasOption(MOVE)) {
            if (cmd.hasOption("source") && cmd.hasOption("source")) {
                logger.log(Level.INFO, "Using cli arguments " +
                        "-src=" + cmd.getOptionValue("src") + " and " +
                        "-target=" + cmd.getOptionValue("target"));
            } else {
                if (!cmd.hasOption("source")) {
                    logger.log(Level.SEVERE, "Missing argument -src");
                }
                if (!cmd.hasOption("target")) {
                    logger.log(Level.SEVERE, "Missing argument -target");
                }
                help();
            }
        } else if (cmd.hasOption(EXPORT) || cmd.hasOption(IMPORT)) {
            if (cmd.hasOption("source") && cmd.hasOption("tempPassword")) {
                logger.log(Level.INFO, "Using cli arguments " +
                        "-src=" + cmd.getOptionValue("src") + " and " +
                        "-tempPassword=" + cmd.getOptionValue("tempPassword"));
            } else {
                if (!cmd.hasOption("source")) {
                    logger.log(Level.SEVERE, "Missing argument -src");
                }
                if (!cmd.hasOption("tempPassword")) {
                    logger.log(Level.SEVERE, "Missing argument -tempPassword");
                }
                help();
            }
        }
    }

    private void printFileStatusRecursive(List<EncryptedFileStatus> files) throws InvalidPathException, IntegrityException, InvalidParameterSpecException, FileSystemException {
        printFileStatusRecursive(files, "");
    }

    private void printFileStatusRecursive(List<EncryptedFileStatus> files, String indent) throws InvalidPathException, IntegrityException, InvalidParameterSpecException, FileSystemException {
        for (EncryptedFileStatus file : files) {
            String[] pathParts = file.getPath().split("/");
            String fileName = pathParts[pathParts.length - 1];
            String message = indent + fileName;
            if (file.isFolder()) {
                System.out.println(message);
                printFileStatusRecursive(encryptionService.listFiles(file.getPath(), PASSWORD), indent + "\t");
            } else {
                if (file.isIntegral()) {
                    message += " (integrity OK)";
                } else {
                    message += " (unauthorized change detected!)";
                }
                System.out.println(message);
            }
        }
    }
}
