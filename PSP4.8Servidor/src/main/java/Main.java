    import org.apache.ftpserver.FtpServer;
    import org.apache.ftpserver.FtpServerFactory;
    import org.apache.ftpserver.listener.ListenerFactory;
    import org.apache.ftpserver.usermanager.*;
    import org.apache.ftpserver.usermanager.impl.BaseUser;
    import org.apache.ftpserver.usermanager.impl.WritePermission;
    import org.apache.ftpserver.ftplet.*;

    import java.io.File;
    import java.util.Collections;

    public class Main {
        public static void main(String[] args) {
            try {

                FtpServerFactory serverFactory = new FtpServerFactory();


                ListenerFactory factory = new ListenerFactory();
                factory.setPort(21); // Puerto FTP predeterminado
                serverFactory.addListener("default", factory.createListener());


                BaseUser user = new BaseUser();
                user.setName("ftpuser"); // Nombre de usuario
                user.setPassword("pass123"); // Contraseña
                user.setHomeDirectory("ftp-root"); // Directorio raíz del usuario


                user.setAuthorities(Collections.singletonList(new WritePermission()));


                File homeDir = new File("ftp-root");
                if (!homeDir.exists()) {
                    homeDir.mkdirs();
                }

                // Agregar usuario al servidor
                serverFactory.getUserManager().save(user);

                //  Iniciar el servidor FTP
                FtpServer server = serverFactory.createServer();
                server.start();
                System.out.println(" Servidor FTP iniciado en el puerto 21...");
            } catch (Exception e) {
                System.err.println("Error iniciando el servidor FTP: " + e.getMessage());
            }
        }
    }
