import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static FTPClient ftpClient = new FTPClient();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java Main <servidor> <usuario> <contraseña>");
            return;
        }

        String server = args[0];
        String user = args[1];
        String pass = args[2];

        try {
            ftpClient.connect(server);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            System.out.println(" Conectado a " + server);
            interactiveShell();

            ftpClient.logout();
            ftpClient.disconnect();
            System.out.println("🔌 Desconectado del servidor FTP.");
        } catch (IOException ex) {
            System.out.println(" Error: " + ex.getMessage());
        }
    }

    private static void interactiveShell() {
        String command;
        System.out.println(" Modo interactivo FTP iniciado. Usa 'help' para ver comandos.");
        while (true) {
            System.out.print("ftp> ");
            command = scanner.nextLine().trim();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println(" Cerrando sesión...");
                break;
            } else if (command.equalsIgnoreCase("ls")) {
                listFiles();
            } else if (command.startsWith("cd ")) {
                changeDirectory(command.substring(3).trim());
            } else if (command.startsWith("get ")) {
                downloadFile(command.substring(4).trim());
            } else if (command.startsWith("put ")) {
                uploadFile(command.substring(4).trim());
            } else if (command.startsWith("del ")) {
                deleteFile(command.substring(4).trim());
            } else if (command.equalsIgnoreCase("help")) {
                printHelp();
            } else {
                System.out.println(" Comando desconocido. Usa 'help' para ver la lista de comandos.");
            }
        }
    }

    private static void listFiles() {
        try {
            String[] files = ftpClient.listNames();
            if (files != null) {
                for (String file : files) {
                    System.out.println(file);
                }
            } else {
                System.out.println(" No se encontraron archivos.");
            }
        } catch (IOException ex) {
            System.out.println(" Error al listar archivos: " + ex.getMessage());
        }
    }

    private static void changeDirectory(String path) {
        try {
            if (ftpClient.changeWorkingDirectory(path)) {
                System.out.println(" Directorio cambiado a: " + path);
            } else {
                System.out.println("No se pudo cambiar al directorio: " + path);
            }
        } catch (IOException ex) {
            System.out.println(" Error al cambiar de directorio: " + ex.getMessage());
        }
    }

    private static void downloadFile(String fileName) {
        try (OutputStream outputStream = new FileOutputStream(fileName)) {
            if (ftpClient.retrieveFile(fileName, outputStream)) {
                System.out.println(" Archivo descargado: " + fileName);
            } else {
                System.out.println(" No se pudo descargar el archivo.");
            }
        } catch (IOException ex) {
            System.out.println(" Error al descargar archivo: " + ex.getMessage());
        }
    }

    private static void uploadFile(String fileName) {
        try (InputStream inputStream = new FileInputStream(fileName)) {
            if (ftpClient.storeFile(fileName, inputStream)) {
                System.out.println(" Archivo subido: " + fileName);
            } else {
                System.out.println(" No se pudo subir el archivo.");
            }
        } catch (IOException ex) {
            System.out.println(" Error al subir archivo: " + ex.getMessage());
        }
    }

    private static void deleteFile(String fileName) {
        try {
            if (ftpClient.deleteFile(fileName)) {
                System.out.println("🗑 Archivo eliminado: " + fileName);
            } else {
                System.out.println(" No se pudo eliminar el archivo.");
            }
        } catch (IOException ex) {
            System.out.println(" Error al eliminar archivo: " + ex.getMessage());
        }
    }

    private static void printHelp() {
        System.out.println(" Comandos disponibles:");
        System.out.println("  ls      - Lista archivos en el directorio actual");
        System.out.println("  cd DIR  - Cambia de directorio en el servidor FTP");
        System.out.println("  get FILE - Descarga un archivo del servidor");
        System.out.println("  put FILE - Sube un archivo al servidor");
        System.out.println("  del FILE - Elimina un archivo del servidor");
        System.out.println("  exit    - Cierra la conexión");
    }
}
